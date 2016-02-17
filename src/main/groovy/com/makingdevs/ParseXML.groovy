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
    def receptor=new Receptor()
    def emisor=new Emisor()
    def comprobante=new Comprobante()
    def regimenFiscal=new RegimenFiscal()
    def lugarExpedicion=new Direccion()
    def domicilioFiscal=new Direccion()
    def direccionReceptor=new Direccion()
    def estadoDeCuentaBancario=new EstadoDeCuentaBancario()
    def addenda=new Addenda()
    

    def file= new File(path).getText() 
    def xml = new XmlSlurper().parseText(file).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    
    xml.attributes().each{atributoRaiz->
      String atributo
      comprobante.getProperties().each{propiedadRaiz->
        if(atributoRaiz.getKey().equalsIgnoreCase(propiedadRaiz.getKey())){
          atributo=propiedadRaiz.getKey()
          if(atributo.equalsIgnoreCase("fecha")){
            comprobante[atributo]=Date.parse("yyyy-MM-dd'T'HH:mm:ss", atributoRaiz.getValue())
          }
          else if(atributo.equalsIgnoreCase("total")){
            comprobante[atributo]=new BigDecimal(atributoRaiz.getValue())
          }
          else if(atributo.equalsIgnoreCase("subTotal")){
            comprobante[atributo]=new BigDecimal(atributoRaiz.getValue())
          }
          else if(atributo.equalsIgnoreCase("descuento")){
            comprobante[atributo]=new BigDecimal(atributoRaiz.getValue())
          }
          else if(atributo.equalsIgnoreCase("tipoCambio")){
            comprobante[atributo]=Float.parseFloat(atributoRaiz.getValue())
          }
          else{
            comprobante[atributo]=atributoRaiz.getValue()  
          }
          
        }
      }

    }

    xml.children().each{nodo->
      //println ("Informacion: "+nodo.name()+nodo.children()*.name()+nodo.children().children()*.name()+nodo.children().children().children()*.name()+"\n")
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
          if (detalle*.name().join("")=="RegimenFiscal"){
            String atributo
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
            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                lugarExpedicion.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    lugarExpedicion[atributo]=elemento.getValue()
                    emisor.lugarExpedicion=lugarExpedicion
                    comprobante.emisor=emisor
                  }
                }
              }
            }   
          }

          if (detalle*.name().join("")=="DomicilioFiscal"){
            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                domicilioFiscal.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    domicilioFiscal[atributo]=elemento.getValue()
                    emisor.domicilioFiscal=domicilioFiscal
                    comprobante.emisor=emisor
                  }
                }
              }
            } 
          }

          if (detalle*.name().join("")=="Domicilio"){
            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                direccionReceptor.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    direccionReceptor[atributo]=elemento.getValue()
                    receptor.direccionReceptor=direccionReceptor
                    comprobante.receptor=receptor
                  }
                }
              }
            }
          }

          if (detalle*.name().join("")=="Concepto"){
            String atributo
            Concepto concepto = new Concepto()
            detalle*.attributes().each{atributos-> 
              atributos.each{elemento->
                concepto.getProperties().each{propiedad->
                  atributo=elemento.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
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
                    
                  }
                }
              }
              comprobante.conceptos.add(concepto)
            }
          }

          if (detalle*.name().join("")=="Traslados"){
            String atributo
            detalle.children()*.attributes().each{atributos->             
              atributos.each{elemento->
                traslado.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    if(atributo.equalsIgnoreCase("tasa")){
                      traslado[atributo]=new Float(elemento.getValue())
                    }
                    else if(atributo.equalsIgnoreCase("importe")){
                      traslado[atributo]=new BigDecimal(elemento.getValue())
                    }
                    else{
                      traslado[atributo]=elemento.getValue()
                    }
                  }
                }
              }
              comprobante.impuesto.traslado.add(traslado)
            }
          }

          if (detalle*.name().join("")=="TimbreFiscalDigital"){
            String atributo
            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                timbreFiscalDigital.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
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

          if (detalle*.name().join("").equalsIgnoreCase("EstadoDeCuentaBancario")){
            String atributo

            detalle*.attributes().each{atributos->             
              atributos.each{elemento->
                estadoDeCuentaBancario.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    estadoDeCuentaBancario[atributo]=elemento.getValue()
                    addenda.estadoDeCuentaBancario=estadoDeCuentaBancario
                    comprobante.addenda=addenda
                  }
                }
              }
            }
            detalle.children().children()*.attributes().each{atributos->
              def movimientoECB=new MovimientoECB()
              atributos.each{elemento->
                movimientoECB.getProperties().each{propiedad->
                  atributo=propiedad.getKey()
                  if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                    if(atributo.equalsIgnoreCase("importe")){
                      movimientoECB[atributo]=new BigDecimal(elemento.getValue())
                    }
                    else if(atributo.equalsIgnoreCase("saldoInicial")){
                      movimientoECB[atributo]=new BigDecimal(elemento.getValue())
                    }
                    else if(atributo.equalsIgnoreCase("saldoAlCorte")){
                      movimientoECB[atributo]=new BigDecimal(elemento.getValue())
                    }
                    else if(atributo.equalsIgnoreCase("fecha")){
                      movimientoECB[atributo]=Date.parse( "yyyy-MM-dd", elemento.getValue())
                    }
                    else{
                      movimientoECB[atributo]=elemento.getValue()
                    }
                  }
                }
              }
              estadoDeCuentaBancario.movimientoECB.add(movimientoECB)
            }
          }
          else{}
        }   
      }
    }
    return comprobante
  }
}