package com.makingdevs.accounting

import com.makingdevs.*
import groovy.xml.XmlUtil

class AccountManager {
  List<File> searchInvoicesInLocation(String location){
    List<File> invoices = []
    new File(location).eachFile{ file ->
      if(file.name.endsWith(".xml")) invoices.add(file)
    }
    invoices
  }

  Comprobante obtainVoucherFromInvoice(File invoice){
    def voucher= new Comprobante()
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.attributes().each{attributeXML->
      String attribute
      voucher.getProperties().each{attributeVoucher->
        if(attributeXML.getKey().equalsIgnoreCase(attributeVoucher.getKey())){
          attribute=attributeVoucher.getKey()
          if(attribute.equalsIgnoreCase("fecha")){
            voucher[attribute]=Date.parse("yyyy-MM-dd'T'HH:mm:ss", attributeXML.getValue())
          }
          else if(attribute.equalsIgnoreCase("total")){
            voucher[attribute]=new BigDecimal(attributeXML.getValue())
          }
          else if(attribute.equalsIgnoreCase("subTotal")){
            voucher[attribute]=new BigDecimal(attributeXML.getValue())
          }
          else if(attribute.equalsIgnoreCase("descuento")){
            voucher[attribute]=new BigDecimal(attributeXML.getValue())
          }
          else if(attribute.equalsIgnoreCase("tipoCambio")){
            voucher[attribute]=Float.parseFloat(attributeXML.getValue())
          }
          else{
            voucher[attribute]=attributeXML.getValue()
          }

        }
      }
    }
    voucher
  }

  Emisor obtainTransmitterFromInvoice(File invoice){
    def voucher= new Comprobante()
    def emisor=new Emisor()
    def domicilioFiscal=new Direccion()
    def lugarExpedicion=new Direccion()
    def regimenFiscal=new RegimenFiscal()
    
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.children().each{child->
      child.attributes().each{atributosNodo->

        if(atributosNodo.getKey().equalsIgnoreCase("rfc") && child.name().equalsIgnoreCase("Emisor")){
          emisor.rfc=atributosNodo.getValue()
          voucher.emisor=emisor
        }
        else if(atributosNodo.getKey().equalsIgnoreCase("nombre") && child.name().equalsIgnoreCase("Emisor")){
          emisor.nombre=atributosNodo.getValue()
          voucher.emisor=emisor
        }
      }
      child.children().each{element->
        if (element*.name().join("")=="RegimenFiscal"){
          String attribute
          element*.attributes().each{atributos->
            atributos.each{elemento->
              regimenFiscal.getProperties().each{propiedad->
                attribute=propiedad.getKey()
                if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                  regimenFiscal[attribute]=elemento.getValue()
                  emisor.regimen=regimenFiscal
                  voucher.emisor=emisor
                }
              }
            }
          }
        }

        if (element*.name().join("")=="ExpedidoEn"){
          String attribute
          element*.attributes().each{atributos->
            atributos.each{elemento->
              lugarExpedicion.getProperties().each{propiedad->
                attribute=propiedad.getKey()
                if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                  lugarExpedicion[attribute]=elemento.getValue()
                  emisor.lugarExpedicion=lugarExpedicion
                  voucher.emisor=emisor
                }
              }
            }
          }
        }

        if (element*.name().join("")=="DomicilioFiscal"){
          String attribute
          element*.attributes().each{atributos->
            atributos.each{elemento->
              domicilioFiscal.getProperties().each{propiedad->
                attribute=propiedad.getKey()
                if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                  domicilioFiscal[attribute]=elemento.getValue()
                  emisor.domicilioFiscal=domicilioFiscal
                  voucher.emisor=emisor
                }
              }
            }
          }
        }
      }
    }
    emisor
  }

  Receptor obtainReceiverFromInvoice(File invoice){
    def voucher= new Comprobante()
    def receptor=new Receptor()
    def direccionReceptor=new Direccion()

    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.children().each{child->
      child.attributes().each{atributosNodo->

        if(atributosNodo.getKey().equalsIgnoreCase("rfc") && child.name().equalsIgnoreCase("Receptor")){
          receptor.rfc=atributosNodo.getValue()
          voucher.receptor=receptor
        }
        else if(atributosNodo.getKey().equalsIgnoreCase("nombre") && child.name().equalsIgnoreCase("Receptor")){
          receptor.nombre=atributosNodo.getValue()
          voucher.receptor=receptor
        }
        
      }
      child.children().each{element->
        if (element*.name().join("")=="Domicilio"){
          String attribute
          element*.attributes().each{atributos->
            atributos.each{elemento->
              direccionReceptor.getProperties().each{propiedad->
                attribute=propiedad.getKey()
                if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                  direccionReceptor[attribute]=elemento.getValue()
                  receptor.direccionReceptor=direccionReceptor
                  voucher.receptor=receptor
                }
              }
            }
          }
        }
      }
    }
    receptor
  }

  List<Concepto> obtainConceptsFromInvoice(File invoice){
    def voucher= new Comprobante()
    List<Concepto> conceptos=[]
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.children().each{child->
      child.children().each{detalle->
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
            conceptos.add(concepto)
          }
        }
      }
    }
    conceptos
  }

  Impuesto obtainTaxesFromInvoice(File invoice){
    def voucher= new Comprobante()
    def impuesto = new Impuesto()
    def traslado=new Traslado()
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.children().each{child->
      child.attributes().each{atributosNodo->
        if(atributosNodo.getKey().equalsIgnoreCase("totalImpuestosTrasladados") && child.name().equalsIgnoreCase("Impuestos")){
          impuesto.totalImpuestosTrasladado=new BigDecimal(atributosNodo.getValue())
          voucher.impuesto=impuesto
        }
      }
      child.children().each{detalle->
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
            voucher.impuesto.traslado.add(traslado)
          }
        }
      }
    }
    impuesto
  }

  TimbreFiscalDigital obtainDigitalTaxStampFromInvoice(File invoice){
    def voucher= new Comprobante()
    def timbreFiscalDigital=new TimbreFiscalDigital()
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.children().each{child->
      child.children().each{detalle->
        if (detalle*.name().join("")=="TimbreFiscalDigital"){
          String atributo
          detalle*.attributes().each{atributos->
            atributos.each{elemento->
              timbreFiscalDigital.getProperties().each{propiedad->
                atributo=propiedad.getKey()
                if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                  if(elemento.getKey()=="FechaTimbrado"){
                    timbreFiscalDigital[atributo]=Date.parse("yyyy-MM-dd'T'HH:mm:ss", elemento.getValue())
                  }
                  else{
                    timbreFiscalDigital[atributo]=elemento.getValue()
                  }
                  voucher.timbreFiscalDigital=timbreFiscalDigital
                }
              }
            }
          }
        }
      }
    }
    timbreFiscalDigital
  }

  Addenda obtainAddendaFromInvoice(File invoice){
    def voucher= new Comprobante()
    def addenda=new Addenda()
    def estadoDeCuentaBancario=new EstadoDeCuentaBancario()
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.children().each{child->
      child.children().each{detalle->
        if (detalle*.name().join("").equalsIgnoreCase("EstadoDeCuentaBancario")){
          String atributo

          detalle*.attributes().each{atributos->
            atributos.each{elemento->
              estadoDeCuentaBancario.getProperties().each{propiedad->
                atributo=propiedad.getKey()
                if(elemento.getKey().equalsIgnoreCase(propiedad.getKey())){
                  estadoDeCuentaBancario[atributo]=elemento.getValue()
                  addenda.estadoDeCuentaBancario=estadoDeCuentaBancario
                  voucher.addenda=addenda
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
                    movimientoECB[atributo]=Date.parse("yyyy-MM-dd'T'HH:mm:ss", elemento.getValue())
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
      }
    }
    addenda
  }
  
}
