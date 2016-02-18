package com.makingdevs

import org.apache.poi.ss.usermodel.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.xssf.usermodel.*
import org.apache.poi.ss.util.*
import org.apache.poi.ss.usermodel.*

class CreateWorkbook{
  static void main(def args){
    
    XSSFWorkbook workbook = new XSSFWorkbook()
    XSSFSheet sheet=workbook.createSheet("Pagina_1")
    CreationHelper createHelper = workbook.getCreationHelper()
    XSSFCellStyle dateStyle = workbook.createCellStyle()
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm"))
    XSSFCellStyle headStyle = workbook.createCellStyle()
    headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex())
    headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)

    def parseXML=new ParseXML()
    def comprobante=new Comprobante()

    List<String> filesXML=parseXML.getFilesXML("/Users/makingdevs/workspace/facturas")
    List<Comprobante> comprobantes=[]
    filesXML.each{f->
      comprobantes.add(parseXML.readFile(f))
      parseXML.readFile(f)
    }
    
    int row=0,cellnum=0

    comprobantes.each{factura->
      Row r = sheet.createRow(row++)
      if(row==1){
        Cell c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("Serie")
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("Fecha")
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("Subtotal")
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("Descuento")
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("Impuesto")
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("Total")
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("Addenda periodo")
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("Sucursal")
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("NumeroCuenta")
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("NombreCliente")
        c.setCellStyle(headStyle)
        c = r.createCell(cellnum++)
        c.setCellStyle(headStyle)
        c.setCellValue("Version")
        
        
        cellnum=0
      }
      row=1
    }

    comprobantes.each{factura->

      Row r = sheet.createRow(row++)
      Cell c = r.createCell(cellnum++)
        c.setCellValue(factura.serie)
        c = r.createCell(cellnum++)
        
        c.setCellValue(factura.fecha)
        c.setCellStyle(dateStyle)
        
        c = r.createCell(cellnum++)
        c.setCellValue(factura.subTotal)
        c = r.createCell(cellnum++)
        c.setCellValue(factura.descuento)
        c = r.createCell(cellnum++)
        c.setCellValue(factura.impuesto.totalImpuestosTrasladado)
        c = r.createCell(cellnum++)
        c.setCellValue(factura.total)
        
        factura.addenda.estadoDeCuentaBancario.getProperties().each{detalle->
          if(!detalle.getKey().equalsIgnoreCase("class")){
            if(detalle.getKey().equalsIgnoreCase("movimientoECB")){
              factura.addenda.estadoDeCuentaBancario.movimientoECB.each{movimientosECB->
                cellnum=0
                r = sheet.createRow(row++)
                c = r.createCell(cellnum)
                movimientosECB.getProperties().each{movimiento->
                  if(row==3){
                    movimiento.each{descripcion->
                      if(!descripcion.getKey().equalsIgnoreCase("class")){
                        c = r.createCell(cellnum++)
                        c.setCellStyle(headStyle)
                        c.setCellValue(descripcion.getKey().capitalize())
                      }
                    }
                  }
                }
              }
              row=3
              cellnum=0
              factura.addenda.estadoDeCuentaBancario.movimientoECB.each{movimientosECB->
                r = sheet.createRow(row++)
                c = r.createCell(cellnum)
                movimientosECB.getProperties().each{movimiento->
                  
                    movimiento.each{descripcion->
                      if(!descripcion.getKey().equalsIgnoreCase("class")){
                        c = r.createCell(cellnum++)
                        if(descripcion.getKey().equalsIgnoreCase("importe") || 
                          descripcion.getKey().equalsIgnoreCase("saldoInicial") ||
                          descripcion.getKey().equalsIgnoreCase("saldoAlCorte")){
                          c.setCellType(XSSFCell.CELL_TYPE_NUMERIC)
                          c.setCellValue(descripcion.getValue())
                        }
                        else if(descripcion.getKey().equalsIgnoreCase("fecha")){
                          c.setCellValue(descripcion.getValue())
                          c.setCellStyle(dateStyle)
                          
                        }
                        else{
                          c.setCellValue(descripcion.getValue().toString())  
                        }
                        
                      }
                    }
                  
                  
                }
                cellnum=0
              }
            }
            else{
              c = r.createCell(cellnum++)
              c.setCellValue(detalle.getValue().toString())
            }
          }
        }
    }
    
    FileOutputStream out = new FileOutputStream(new File("libro_factura_movimientos.xlsx"))
    workbook.write(out)
    out.close()
  }
}