package com.makingdevs

import org.apache.poi.ss.usermodel.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.xssf.usermodel.*
import org.apache.poi.ss.util.*
import org.apache.poi.ss.usermodel.*

import org.apache.poi.*
class CreateWorkbook{
  static void main(def args){
    
    XSSFWorkbook workbook = new XSSFWorkbook()
    XSSFSheet sheet=workbook.createSheet("Pagina_1")

    def parseXML=new ParseXML()
    def comprobante=new Comprobante()

    List<String> filesXML=parseXML.getFilesXML("/Users/makingdevs/workspace/facturas")
    List<Comprobante> comprobantes=[]
    filesXML.each{f->
      comprobantes.add(parseXML.readFile(f))
      parseXML.readFile(f)
      println f
    }
    
    int row=0,cellnum=0

    comprobantes.each{factura->
      Row r = sheet.createRow(row++)
      if(row==1){
        Cell c = r.createCell(cellnum++)
        c.setCellValue("serie")
        c = r.createCell(cellnum++)
        c.setCellValue("fecha")
        c = r.createCell(cellnum++)
        c.setCellValue("subtotal")
        c = r.createCell(cellnum++)
        c.setCellValue("descuento")
        c = r.createCell(cellnum++)
        c.setCellValue("total")
        c = r.createCell(cellnum++)
        c.setCellValue("addenda periodo")
        c = r.createCell(cellnum++)
        c.setCellValue("sucursal")
        c = r.createCell(cellnum++)
        c.setCellValue("numeroCuenta")
        c = r.createCell(cellnum++)
        c.setCellValue("nombreCliente")
        c = r.createCell(cellnum++)
        c.setCellValue("version")
        c = r.createCell(cellnum++)
        c.setCellValue("movimientos")
        
        cellnum=0
      }
      row=1
    }

    comprobantes.each{factura->
      Row r = sheet.createRow(row++)
      Cell c = r.createCell(cellnum++)
        c.setCellValue(factura.serie)
        c = r.createCell(cellnum++)
        c.setCellValue(factura.fecha.toString())
        c = r.createCell(cellnum++)
        c.setCellValue(factura.subTotal)
        c = r.createCell(cellnum++)
        c.setCellValue(factura.descuento)
        c = r.createCell(cellnum++)
        c.setCellValue(factura.total)
        
        factura.addenda.estadoDeCuentaBancario.getProperties().each{detalle->
          if(!detalle.getKey().equalsIgnoreCase("class")){
            if(detalle.getKey().equalsIgnoreCase("movimientoECB")){
              factura.addenda.estadoDeCuentaBancario.movimientoECB.each{movimiento->
                c = r.createCell(cellnum++)
                c.setCellValue(movimiento.getProperties().toString())
              }
            }
            else{
              c = r.createCell(cellnum++)
              c.setCellValue(detalle.getValue().toString())
            }
          }
        }
    }
    
    FileOutputStream out = new FileOutputStream(new File("libro_factura.xlsx"))
    workbook.write(out)
    out.close()
  }
}