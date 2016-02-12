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

    List<String> filesXML=parseXML.getFilesXML("/Users/makingdevs/Downloads/12_Diciembre")
    List<Comprobante> comprobantes=[]
    filesXML.each{f->
      //comprobantes.add(parseXML.readFile(file))
      parseXML.readFile(f)
      println f
    }
    
    int row=0,cellnum=0

    comprobantes.each{factura->
      Row r = sheet.createRow(row++)
      if(row==1){
        factura.getProperties().each{atributo->
          if(!atributo.getKey().equalsIgnoreCase("class")){
            Cell c = r.createCell(cellnum++)
            c.setCellValue(atributo.getKey().toString().capitalize())
          }
        }
        cellnum=0
      }
      row=1
    }

    comprobantes.each{factura->
      Row r = sheet.createRow(row++)
      factura.getProperties().each{atributo->
        if(!atributo.getKey().equalsIgnoreCase("class")){
          Cell c = r.createCell(cellnum++)
          if(atributo.getValue()!=null){
            if(atributo.getKey().equalsIgnoreCase("receptor")){
              c.setCellValue(atributo.getValue().nombre.toString()) 
            }
            else if(atributo.getKey().equalsIgnoreCase("emisor")){
              c.setCellValue(atributo.getValue().nombre.toString()) 
            }
            else if(atributo.getKey().equalsIgnoreCase("timbreFiscalDigital")){
              c.setCellValue(atributo.getValue().uuid.toString()) 
            }
            else if(atributo.getKey().equalsIgnoreCase("impuesto")){
              c.setCellValue(atributo.getValue().totalImpuestosTrasladado.toString()) 
            }
            else{
              c.setCellValue(atributo.getValue().toString())  
            } 
          }
          else{
            c.setCellValue("")
          }
        }
      }
      cellnum=0
    }
    
    FileOutputStream out = new FileOutputStream(new File("libro_factura.xlsx"))
    workbook.write(out)
    out.close()
    println("Libro Factura")
    
  }
}