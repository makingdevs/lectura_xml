package com.makingdevs

class Main{
  static void main(def args){
    def parseXML=new ParseXML()
    def comprobante=new Comprobante()
    
    List<String> filesXML=parseXML.getFilesXML("C:\\Users\\PhenomII\\Documents\\home_work")
    List<Comprobante> comprobantes=[]
      filesXML.each{file->
        comprobantes.add(parseXML.readFile(file))
    }

    comprobantes.each{factura->
      println "Factura informacion: "+factura.serie+" "+factura.subTotal+
        " "+factura.total+" "+factura.conceptos.descripcion+
        factura.receptor.nombre
      factura.addenda.estadoDeCuentaBancario.getProperties().each{valor->
        println valor.getValue()
      }   
    }

  }
}
