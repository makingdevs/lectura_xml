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
    CellStyle cellStyle = workbook.createCellStyle()
    
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
        c.setCellValue("Serie")
        c = r.createCell(cellnum++)
        c.setCellValue("Fecha")
        c = r.createCell(cellnum++)
        c.setCellValue("Subtotal")
        c = r.createCell(cellnum++)
        c.setCellValue("Descuento")
        c = r.createCell(cellnum++)
        c.setCellValue("Impuesto")
        c = r.createCell(cellnum++)
        c.setCellValue("Total")
        c = r.createCell(cellnum++)
        c.setCellValue("Addenda periodo")
        c = r.createCell(cellnum++)
        c.setCellValue("Sucursal")
        c = r.createCell(cellnum++)
        c.setCellValue("NumeroCuenta")
        c = r.createCell(cellnum++)
        c.setCellValue("NombreCliente")
        c = r.createCell(cellnum++)
        c.setCellValue("Version")
        c = r.createCell(cellnum++)
        c.setCellValue("Movimientos")
        
        cellnum=0
      }
      row=1
    }

    comprobantes.each{factura->

      Row r = sheet.createRow(row++)
      Cell c = r.createCell(cellnum++)
        c.setCellValue(factura.serie)
        c = r.createCell(cellnum++)
        
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm"))
        c.setCellValue(factura.fecha)
        c.setCellStyle(cellStyle)
        
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
                r = sheet.createRow(row++)
                c = r.createCell(cellnum)
                movimientosECB.getProperties().each{movimiento->
                  if(row==3){
                    movimiento.each{descripcion->
                      if(!descripcion.getKey().equalsIgnoreCase("class")){
                        c = r.createCell(cellnum++)
                        c.setCellValue(descripcion.getKey().capitalize())
                      }
                    }
                  }
                }
              }
              row=3
              cellnum=11
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
                          cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm"))
                          c.setCellValue(descripcion.getValue())
                          c.setCellStyle(cellStyle)
                          
                        }
                        else{
                          c.setCellValue(descripcion.getValue().toString())  
                        }
                        
                      }
                    }
                  
                  
                }
                cellnum=11
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