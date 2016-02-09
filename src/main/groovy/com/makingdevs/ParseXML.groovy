package com.makingdevs

import groovy.xml.XmlUtil
import static java.util.Calendar.*

class ParseXML{

  List<String> getFilesXML(String path){
    List<String> listFiles=[]
    new File(path).eachFile{file -> 
      if(file.name.endsWith(".xml")){
        listFiles.add(file.toString())
      }
    }
    return listFiles
  }


  Comprobante readFile(String path){
    def timbreFiscalDigital=new TimbreFiscalDigital()
    def traslado=new Traslado()
    def impuesto=new Impuesto()
    def concepto=new Concepto()
    def receptor=new Receptor()
    def emisor=new Emisor()
    def comprobante=new Comprobante()
    def regimenFiscal=new RegimenFiscal()
    def lugarExpedicion=new Direccion()
    def domicilioFiscal=new Direccion()
    def direccionReceptor=new Direccion()

    //def timbreFiscalDigital=new TimbreFiscalDigital()
    def file= new File(path).getText() 
    def xml = new XmlSlurper().parseText(file).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    
    xml.attributes().each{atributoRaiz->
      String atributo
      comprobante.getProperties().each{propiedadRaiz->
        if(atributoRaiz.getKey()==propiedadRaiz.getKey()){
          atributo=propiedadRaiz.getKey()
          if(atributo.equalsIgnoreCase("fecha")){
            comprobante[atributo]=Date.parse( "yyyy-MM-dd", atributoRaiz.getValue())
          }
          else if(atributo.equalsIgnoreCase("subTotal")){
            comprobante[atributo]=new BigDecimal(atributoRaiz.getValue())
          }
          else if(atributo.equalsIgnoreCase("descuento")){
            comprobante[atributo]=new BigDecimal(atributoRaiz.getValue())
          }
          else{
            comprobante[atributo]=atributoRaiz.getValue()  
          }
          
        }
      }

    }

    xml.children().each{nodo->
      println ("Informacion: "+nodo.name()+nodo.children()*.name()+"\n")
      
      if (nodo!=null) {

          nodo.attributes().each{atributosNodo->

            if(atributosNodo.getKey().equalsIgnoreCase("rfc") && nodo.name().equalsIgnoreCase("Emisor")){
              emisor.rfc=atributosNodo.getValue()
              comprobante.emisor=emisor
            }
            else if(atributosNodo.getKey().equalsIgnoreCase("nombre") && nodo.name().equalsIgnoreCase("Emisor")){
              emisor.nombre=atributosNodo.getValue()
              comprobante.emisor=emisor
            }
            else if(atributosNodo.getKey().equalsIgnoreCase("rfc") && nodo.name().equalsIgnoreCase("Receptor")){
              receptor.rfc=atributosNodo.getValue()
              comprobante.receptor=receptor
            } 
            else if(atributosNodo.getKey().equalsIgnoreCase("nombre") && nodo.name().equalsIgnoreCase("Receptor")){
              receptor.nombre=atributosNodo.getValue()
              comprobante.receptor=receptor
            } 
            else if(atributosNodo.getKey().equalsIgnoreCase("totalImpuestosTrasladados") && nodo.name().equalsIgnoreCase("Impuestos")){
              impuesto.totalImpuestosTrasladado=new BigDecimal(atributosNodo.getValue())
              comprobante.impuesto=impuesto
            } 

          }
          nodo.children().each{detalle->
          //println detalle.name()
          //println  detalle*.attributes()+"--------------"
          if (detalle*.name().join("")=="RegimenFiscal"){
            String atributo
            //println "RegimenFiscal---"
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                regimenFiscal.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    regimenFiscal[atributo]=elemento.getValue()
                    emisor.regimen=regimenFiscal
                    comprobante.emisor=emisor
                  }

                }
                
              }
            } 
            
          }

          if (detalle*.name().join("")=="ExpedidoEn"){
            println "ExpedidoEn---"
            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                lugarExpedicion.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  //println atributo+"   "+elemento.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    //println "\n\t valores: "+propiedad.getKey()
                    lugarExpedicion[atributo]=elemento.getValue()
                    emisor.lugarExpedicion=lugarExpedicion
                    comprobante.emisor=emisor
                  }
                }
                
              }
            }   
            print comprobante.emisor.lugarExpedicion.municipio     
          }

          if (detalle*.name().join("")=="DomicilioFiscal"){
            println "Domicilio fiscal---"
            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                domicilioFiscal.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  //println atributo+"   "+elemento.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    //println "\n\t valores: "+propiedad.getKey()
                    domicilioFiscal[atributo]=elemento.getValue()
                    emisor.domicilioFiscal=domicilioFiscal
                    comprobante.emisor=emisor
                  }
                }
                
              }
            } 
          }

          if (detalle*.name().join("")=="Domicilio"){
            println "Domicilio---"
            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                direccionReceptor.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  //println atributo+"   "+elemento.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    //println "\n\t valores: "+propiedad.getKey()
                    direccionReceptor[atributo]=elemento.getValue()
                    receptor.direccionReceptor=direccionReceptor
                    comprobante.receptor=receptor
                  }
                }
                
              }
            }
          }

          if (detalle*.name().join("")=="Concepto"){
            println "Concepto---"
            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                concepto.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  //println atributo+"   "+elemento.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    //println "\n\t valores: "+propiedad.getKey()
                    if(elemento.getKey()=="importe"){
                      concepto[atributo]=new BigDecimal(elemento.getValue())
                    }
                    else if(elemento.getKey()=="valorUnitario"){
                      concepto[atributo]=new BigDecimal(elemento.getValue())
                    }
                    else if(elemento.getKey()=="cantidad"){
                      concepto[atributo]=Float.parseFloat(elemento.getValue())
                    }
                    else{
                      concepto[atributo]=elemento.getValue()
                    }
                    comprobante.conceptos.add(concepto)
                    
                  }
                }
                
              }
            }
          }

          if (detalle*.name().join("")=="Traslados"){
            println "Traslados "+detalle*.children()
            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                traslado.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  //println atributo+"   "+elemento.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    //println "\n\t valores: "+propiedad.getKey()
                    traslado[atributo]=elemento.getValue()
                  }
                }
                
              }
            }

          }

          if (detalle*.name().join("")=="TimbreFiscalDigital"){
            println "TimbreFiscalDigital"

            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                timbreFiscalDigital.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  //println atributo+"   "+elemento.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    //println "\n\t valores: "+propiedad.getKey()
                    if(elemento.getKey()=="FechaTimbrado"){
                      timbreFiscalDigital[atributo]=Date.parse( "yyyy-MM-dd", elemento.getValue())
                    }
                    else{
                      timbreFiscalDigital[atributo]=elemento.getValue()
                    }
                    comprobante.timbreFiscalDigital=timbreFiscalDigital
                  }
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