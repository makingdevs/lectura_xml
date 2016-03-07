package com.makingdevs

import org.apache.poi.ss.usermodel.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.xssf.usermodel.*
import org.apache.poi.ss.util.*
import org.apache.poi.ss.usermodel.*
import com.makingdevs.accounting.*

class FileOperation{
  XSSFWorkbook generateFileExcel(String location){
    XSSFWorkbook workbook = new XSSFWorkbook()
    XSSFSheet sheet=workbook.createSheet("Pagina_1")
    CreationHelper createHelper = workbook.getCreationHelper()
    XSSFCellStyle dateStyle = workbook.createCellStyle()
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm"))
    XSSFCellStyle headStyle = workbook.createCellStyle()
    headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex())
    headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
    
    FileOutputStream out = new FileOutputStream(new File("Test_Excel.xlsx"))
    workbook.write(out)
    out.close()

    workbook
  }
  
  List<String> getInvoiceTitles(){
    def invoiceTitles=["fecha","subtotal","descuento","impuesto","total","emisor",
                  "receptor","noCertificado","sello","folio","formaDePago",
                  "addenda","lugarExpedicion","timbreFiscalDigital","tipoDeComprobante",
                  "tipoDeCambio","serie","moneda","numCtaPago","conceptos","certificado",
                  "metodoDePago"]
    invoiceTitles
  }

  def getElementOfInvoice(File factura){
    AccountManager manager = new AccountManager()
    def elementosFactura=["emisor":manager.obtainTransmitterFromInvoice(factura),
                      "receptor":manager.obtainReceiverFromInvoice(factura),
                      "conceptos":manager.obtainConceptsFromInvoice(factura),
                      "impuesto":manager.obtainTaxesFromInvoice(factura),
                      "timbreFiscalDigital":manager.obtainDigitalTaxStampFromInvoice(factura),
                      "addenda":manager.obtainAddendaFromInvoice(factura)]
    elementosFactura
  }

  def getItemsMainInvoice(){
    def elementosPrincipales=["fecha","subTotal","descuento",
                                "total","noCertificado","sello",
                                "folio","formaDePago","addenda",
                                "lugarExpedicion","tipoDeComprobante",
                                "tipoCambio","serie","moneda","numCtaPago",
                                "certificado","metodoDePago"]
    elementosPrincipales
  }

  def getItemsComplementaryInvoice(){
    def elementosComplementarios=["impuesto":"totalImpuestosTrasladado",
                                  "emisor":"nombre","receptor":"nombre",
                                  "timbreFiscalDigital":"uuid"
                                  ]
    elementosComplementarios
  }

  XSSFWorkbook generateFileExcelWithAllInvoices(String location){
    XSSFWorkbook workbook = new XSSFWorkbook()
    XSSFSheet sheet=workbook.createSheet("Pagina_1")
    CreationHelper createHelper = workbook.getCreationHelper()
    XSSFCellStyle dateStyle = workbook.createCellStyle()
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm"))
    XSSFCellStyle headStyle = workbook.createCellStyle()
    headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex())
    headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
    Row rowSheet 
    Cell cellSheet
    Integer row=0,cellnum=0
    
    AccountManager manager = new AccountManager()
    def files = manager.searchInvoicesInLocation(location)
    Comprobante voucher 
    def encabezados=getInvoiceTitles()
    
    files.each{factura->
      voucher = manager.obtainVoucherFromInvoice(factura)
      def elementosPrincipalesFactura=getElementOfInvoice(factura)
      elementosPrincipalesFactura.each{atributo,metodo->
        voucher[atributo]=metodo
      }
      if(row==0){
        rowSheet=sheet.createRow(row++)
        encabezados.each{titulo->
          cellSheet = rowSheet.createCell(cellnum++)
          cellSheet.setCellStyle(headStyle)
          cellSheet.setCellValue(titulo)
        }
        cellnum=0
      } 
      if(row>=1){
        def atributosEspecificos=getItemsMainInvoice()
        def atributosComplementarios=getItemsComplementaryInvoice()
        rowSheet=sheet.createRow(row++)
        atributosEspecificos.each{atributoFactura->
          cellSheet = rowSheet.createCell(cellnum++)
          if(atributoFactura.equalsIgnoreCase("fecha")){
            cellSheet.setCellStyle(dateStyle)
            cellSheet.setCellValue(voucher[atributoFactura])
          }
          else if(atributoFactura.equalsIgnoreCase("subTotal")
            ||atributoFactura.equalsIgnoreCase("descuento")
            ||atributoFactura.equalsIgnoreCase("total")){
            cellSheet.setCellType(XSSFCell.CELL_TYPE_NUMERIC)
            cellSheet.setCellValue(voucher[atributoFactura])
          }
          else{
            cellSheet.setCellValue(voucher[atributoFactura].toString())
          }
          
        }

        atributosComplementarios.each{atributoFactura,valorFactura->
          if(valorFactura.equalsIgnoreCase("totalImpuestosTrasladado")){
            cellSheet.setCellType(XSSFCell.CELL_TYPE_NUMERIC)
            cellSheet.setCellValue(voucher[atributoFactura][valorFactura])
          }
          else{
            cellSheet.setCellValue(voucher[atributoFactura][valorFactura])
          }
          
        }
                
        cellnum=0
      }
    }
    FileOutputStream out = new FileOutputStream(new File("Libro_Facturas.xlsx"))
    workbook.write(out)
    out.close()
    workbook
  }

  XSSFWorkbook generateFileExcelWithAddendaInvoice(String file){
    XSSFWorkbook workbook = new XSSFWorkbook()
    XSSFSheet sheet=workbook.createSheet("Pagina_1")
    CreationHelper createHelper = workbook.getCreationHelper()
    XSSFCellStyle dateStyle = workbook.createCellStyle()
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm"))
    XSSFCellStyle headStyle = workbook.createCellStyle()
    headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex())
    headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
    Row rowSheet 
    Cell cellSheet
    Integer row=0,cellnum=0

    AccountManager manager = new AccountManager()
    File invoice = new File(file)
    Comprobante voucher = manager.obtainVoucherFromInvoice(invoice)
    voucher.impuesto = manager.obtainTaxesFromInvoice(invoice)
    voucher.addenda = manager.obtainAddendaFromInvoice(invoice)
    def files=[]
    files<<voucher
    def encabezados=["Serie","Fecha","Subtotal","Descuento","Impuesto",
                      "Total","Addenda periodo","Sucursal","NumeroCuenta",
                      "NombreCliente","Version"]
    
    files.each{factura->
      if(row==0){
        rowSheet=sheet.createRow(row++)
        encabezados.each{titulo->
          cellSheet = rowSheet.createCell(cellnum++)
          cellSheet.setCellStyle(headStyle)
          cellSheet.setCellValue(titulo)
        }
        cellnum=0
      }
      if(row>=1){
        rowSheet=sheet.createRow(row++)
        cellSheet = rowSheet.createCell(cellnum++)
        cellSheet.setCellValue(voucher.serie)
        cellSheet = rowSheet.createCell(cellnum++)
        
        cellSheet.setCellValue(voucher.fecha)
        cellSheet.setCellStyle(dateStyle)
        
        cellSheet = rowSheet.createCell(cellnum++)
        cellSheet.setCellValue(voucher.subTotal)
        cellSheet = rowSheet.createCell(cellnum++)
        cellSheet.setCellValue(voucher.descuento)
        cellSheet = rowSheet.createCell(cellnum++)
        cellSheet.setCellValue(voucher.impuesto.totalImpuestosTrasladado)
        cellSheet = rowSheet.createCell(cellnum++)
        cellSheet.setCellValue(voucher.total)
        
        voucher.addenda.estadoDeCuentaBancario.getProperties().each{detalle->
          if(!detalle.getKey().equalsIgnoreCase("class")){
            if(detalle.getKey().equalsIgnoreCase("movimientoECB")){
              voucher.addenda.estadoDeCuentaBancario.movimientoECB.each{movimientosECB->
                cellnum=0
                rowSheet = sheet.createRow(row++)
                cellSheet = rowSheet.createCell(cellnum)
                movimientosECB.getProperties().each{movimiento->
                  if(row==3){
                    movimiento.each{descripcion->
                      if(!descripcion.getKey().equalsIgnoreCase("class")){
                        cellSheet = rowSheet.createCell(cellnum++)
                        cellSheet.setCellStyle(headStyle)
                        cellSheet.setCellValue(descripcion.getKey().capitalize())
                      }
                    }
                  }
                }
              }
              row=3
              cellnum=0
              voucher.addenda.estadoDeCuentaBancario.movimientoECB.each{movimientosECB->
                rowSheet = sheet.createRow(row++)
                cellSheet = rowSheet.createCell(cellnum)
                movimientosECB.getProperties().each{movimiento->
                  
                  movimiento.each{descripcion->
                    if(!descripcion.getKey().equalsIgnoreCase("class")){
                      cellSheet = rowSheet.createCell(cellnum++)
                      if(descripcion.getKey().equalsIgnoreCase("importe") || 
                        descripcion.getKey().equalsIgnoreCase("saldoInicial") ||
                        descripcion.getKey().equalsIgnoreCase("saldoAlCorte")){
                        cellSheet.setCellType(XSSFCell.CELL_TYPE_NUMERIC)
                        cellSheet.setCellValue(descripcion.getValue())
                      }
                      else if(descripcion.getKey().equalsIgnoreCase("fecha")){
                        cellSheet.setCellValue(descripcion.getValue())
                        cellSheet.setCellStyle(dateStyle)
                        
                      }
                      else{
                        cellSheet.setCellValue(descripcion.getValue().toString())  
                      }
                      
                    }
                  }
                }
                cellnum=0
              }
            }
            else{
              cellSheet = rowSheet.createCell(cellnum++)
              cellSheet.setCellValue(detalle.getValue().toString())
            }
          }
        }
      }
    }

    FileOutputStream out = new FileOutputStream(new File("Libro_Factura_Addenda.xlsx"))
    workbook.write(out)
    out.close()
    workbook

  }

  XSSFWorkbook generateFileExcelWithDetailInvoice(String file){
    XSSFWorkbook workbook = new XSSFWorkbook()
    XSSFSheet sheet=workbook.createSheet("Pagina_1")
    CreationHelper createHelper = workbook.getCreationHelper()
    XSSFCellStyle dateStyle = workbook.createCellStyle()
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm"))
    XSSFCellStyle headStyle = workbook.createCellStyle()
    headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex())
    headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
    def encabezados = ["Fecha","Subtotal","Descuento","Impuesto",
    "Total","Emisor","Receptor","NoCertificado","Sello","Folio",
    "FormaDePago","Addenda","LugarExpedicion","TimbreFiscalDigital",
    "TipoDeComprobante","TipoDeCambio","Serie","Moneda","NumCtaPago",
    "Conceptos","Certificado","MetodoDePago"]

    AccountManager manager = new AccountManager()
    File invoice = new File(file)
    Comprobante voucher = manager.obtainVoucherFromInvoice(invoice)
    def atributos=["emisor":manager.obtainTransmitterFromInvoice(invoice),
                      "receptor":manager.obtainReceiverFromInvoice(invoice),
                      "conceptos":manager.obtainConceptsFromInvoice(invoice),
                      "impuesto":manager.obtainTaxesFromInvoice(invoice),
                      "timbreFiscalDigital":manager.obtainDigitalTaxStampFromInvoice(invoice),
                      "addenda":manager.obtainAddendaFromInvoice(invoice)]
    atributos.each{atributo,metodo->
      voucher[atributo]=metodo
    }
      
    def files=[]
    files<<voucher
    
    int row=0,cellnum=0
    files.each{factura->
      Row r = sheet.createRow(row++)
      if(row==1){
        encabezados.each{titulo->
          Cell c = r.createCell(cellnum++)
          c.setCellStyle(headStyle)
          c.setCellValue(titulo)
        }
        cellnum=0
      }
    }
    row=1
    files.each{factura->
      
      Row r = sheet.createRow(row++)
      Cell c = r.createCell(cellnum++)
      c.setCellStyle(dateStyle)
      c.setCellValue(voucher.fecha)
      c = r.createCell(cellnum++)
      c.setCellType(XSSFCell.CELL_TYPE_NUMERIC)
      c.setCellValue(voucher.subTotal)
      c = r.createCell(cellnum++)
      c.setCellType(XSSFCell.CELL_TYPE_NUMERIC)
      c.setCellValue(voucher.descuento)
      c = r.createCell(cellnum++)
      c.setCellType(XSSFCell.CELL_TYPE_NUMERIC)
      c.setCellValue(voucher.impuesto.totalImpuestosTrasladado)
      c = r.createCell(cellnum++)
      c.setCellType(XSSFCell.CELL_TYPE_NUMERIC)
      c.setCellValue(voucher.total)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.emisor.nombre)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.receptor.nombre)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.noCertificado)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.sello)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.folio)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.formaDePago)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.addenda.toString())
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.lugarExpedicion)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.timbreFiscalDigital.uuid)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.tipoDeComprobante)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.tipoCambio)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.serie)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.moneda)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.numCtaPago)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.conceptos.toString())
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.certificado)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.metodoDePago)
      cellnum=0

      cellnum=0
      r = sheet.createRow(row++)
      def encabecadoEmisor=["Emisor RFC","Nombre","Pais","Municipio",
                              "Estado","Codigo Postal","Calle","Regimen"]
      encabecadoEmisor.each{titulo->
          c = r.createCell(cellnum++)
          c.setCellStyle(headStyle)
          c.setCellValue(titulo)
      }
      
      cellnum=0
      r = sheet.createRow(row++)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.emisor.rfc)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.emisor.nombre)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.emisor.domicilioFiscal.pais)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.emisor.domicilioFiscal.municipio)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.emisor.domicilioFiscal.estado)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.emisor.domicilioFiscal.codigoPostal)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.emisor.domicilioFiscal.calle)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.emisor.regimen.regimen)

      cellnum=0
      r = sheet.createRow(row++)
      def encabecadoReceptor=["Receptor RFC","Nombre","Pais","Municipio",
                              "Estado","Codigo Postal","Calle"]
      encabecadoReceptor.each{titulo->
          c = r.createCell(cellnum++)
          c.setCellStyle(headStyle)
          c.setCellValue(titulo)
      }
      cellnum=0
      r = sheet.createRow(row++)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.receptor.rfc)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.receptor.nombre)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.receptor.direccionReceptor.pais)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.receptor.direccionReceptor.municipio)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.receptor.direccionReceptor.estado)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.receptor.direccionReceptor.codigoPostal)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.receptor.direccionReceptor.calle)
      
      cellnum=0
      r = sheet.createRow(row++)
      def detalleAtributos=["cantidad","unidad","noIdentificacion",
                            "descripcion","valorUnitario","importe"]
      detalleAtributos.each{titulo->
          c = r.createCell(cellnum++)
          c.setCellStyle(headStyle)
          c.setCellValue(titulo.capitalize())
      }
      
      factura.conceptos.each{detalle->
        cellnum=0
        r = sheet.createRow(row++)
        detalleAtributos.each{atributo->
          c = r.createCell(cellnum++)
          c.setCellValue(detalle[atributo])
        }
      }

      cellnum=0
      r = sheet.createRow(row++)
      def encabecadoTraslado=["Total Impuestos Trasladados","Traslado importe",
                            "Traslado Tasa","Traslado Impuesto"]
      encabecadoTraslado.each{titulo->
          c = r.createCell(cellnum++)
          c.setCellStyle(headStyle)
          c.setCellValue(titulo)
      }
      
      cellnum=0
      r = sheet.createRow(row++)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.impuesto.totalImpuestosTrasladado)
      voucher.impuesto.traslado.each{detalle->
        cellnum=1
        r = sheet.createRow(row++)
        c = r.createCell(cellnum++)
        c.setCellValue(detalle.importe)
        c = r.createCell(cellnum++)
        c.setCellValue(detalle.tasa)
        c = r.createCell(cellnum++)
        c.setCellValue(detalle.impuesto)
      }

      cellnum=0
      r = sheet.createRow(row++)
      def encabecadoTimbreFiscal=["Fecha timbrado","UUID","No Certificado SAT",
                              "Sello CFD","Sello SAT","Version"]
      encabecadoTimbreFiscal.each{titulo->
          c = r.createCell(cellnum++)
          c.setCellStyle(headStyle)
          c.setCellValue(titulo)
      }
      
      cellnum=0
      r = sheet.createRow(row++)
      c = r.createCell(cellnum++)
      c.setCellStyle(dateStyle)
      c.setCellValue(voucher.timbreFiscalDigital.fechaTimbrado)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.timbreFiscalDigital.uuid)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.timbreFiscalDigital.noCertificadoSAT)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.timbreFiscalDigital.selloCFD)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.timbreFiscalDigital.selloSAT)
      c = r.createCell(cellnum++)
      c.setCellValue(voucher.timbreFiscalDigital.version)
      
    }

    FileOutputStream out = new FileOutputStream(new File("Libro_Factura_Detalle.xlsx"))
    workbook.write(out)
    out.close()
    workbook

  }
  

}