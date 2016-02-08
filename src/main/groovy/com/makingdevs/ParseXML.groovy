package com.makingdevs

import groovy.xml.XmlUtil

class ParseXML{

  static def getFilesXML(String path){
    new File(path).eachFile{file -> 
      if(file.name.endsWith(".xml")){
        println file.toString()
      }
    }
  }

  Comprobante readFile(String path){
    def timbreFiscalDigital=new TimbreFiscalDigital()
    def traslado=new Traslado()
    def concepto=new Concepto()
    def receptor=new Receptor()
    def emisor=new Emisor()
    def comprobante=new Comprobante()


    //def timbreFiscalDigital=new TimbreFiscalDigital()
    def file= new File(path).getText() 
    def xml = new XmlSlurper().parseText(file).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
      
    xml.children().each{nodo->
      println ("Informacion: "+nodo.name()+nodo.children()*.name()+"---"+nodo.class+"\n")
      if (nodo!=null) {
          nodo.children().each{detalle->
          //println  detalle*.attributes()
          if (nodo.children()*.name().join("")=="TimbreFiscalDigital"){
            detalle*.attributes().each{atributos->
            //println atributos               
              atributos.each{elemento->
              //println "Elemento: "+elemento.getKey()+elemento.getValue()
                if (elemento.getKey()=="UUID"){
                  println "Elemento: "+elemento.getValue()
                  new TimbreFiscalDigital.uuid=elemento.getValue()
                  comprobante.timbreFiscalDigital=timbreFiscalDigital
                }
              }
            }
          }
          else{}
        }   
      }
    }
    return comprobante
  }
}