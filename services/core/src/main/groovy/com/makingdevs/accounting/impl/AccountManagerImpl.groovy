package com.makingdevs.accounting.impl
import com.makingdevs.accounting.AccountManager
import com.makingdevs.Comprobante
import com.makingdevs.Emisor
import com.makingdevs.Receptor
import com.makingdevs.Concepto
import com.makingdevs.Impuesto
import com.makingdevs.TimbreFiscalDigital
import com.makingdevs.Addenda
import com.makingdevs.Direccion
import com.makingdevs.RegimenFiscal
import com.makingdevs.Traslado
import com.makingdevs.MovimientoECB
import com.makingdevs.EstadoDeCuentaBancario

class AccountManagerImpl implements AccountManager{

  Comprobante obtainVoucherFromInvoice(File invoice){
    def voucher= new Comprobante()

    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.attributes().each{attributeXML->
      voucher.getProperties().each{attributeVoucher->
        if(attributeXML.key.equalsIgnoreCase(attributeVoucher.key)){
          if(attributeVoucher.key.equalsIgnoreCase("fecha")){
            voucher[attributeVoucher.key]=Date.parse("yyyy-MM-dd'T'HH:mm:ss", attributeXML.value)
          }
          else if(["total","subTotal","descuento","tipoCambio"]*.toLowerCase().contains(attributeVoucher.key.toLowerCase())){
            voucher[attributeVoucher.key]=new BigDecimal(attributeXML.value.trim())
          }
          else{
            voucher[attributeVoucher.key]=attributeXML.value
          }
        }
      }
    }
    voucher.emisor = obtainTransmitterFromInvoice(invoice)
    voucher.receptor = obtainReceiverFromInvoice(invoice)
    voucher.conceptos = obtainConceptsFromInvoice(invoice)
    voucher.impuesto = obtainTaxesFromInvoice(invoice)
    voucher.timbreFiscalDigital = obtainDigitalTaxStampFromInvoice(invoice)
    voucher.addenda = obtainAddendaFromInvoice(invoice)
    voucher
  }

  Emisor obtainTransmitterFromInvoice(File invoice){
    def emisor=new Emisor()
    def domicilioFiscal=new Direccion()
    def lugarExpedicion=new Direccion()
    def regimenFiscal=new RegimenFiscal()

    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.Emisor.each{atributo->
      emisor.rfc=atributo.@rfc
      emisor.nombre=atributo.@nombre
      regimenFiscal.regimen=atributo.RegimenFiscal.@Regimen
      emisor.regimen=regimenFiscal
    }
    xml.Emisor.ExpedidoEn.each{atributo->
      lugarExpedicion.calle=atributo.@calle
      lugarExpedicion.noExterior=atributo.@noExterior
      lugarExpedicion.colonia=atributo.@colonia
      lugarExpedicion.municipio=atributo.@municipio
      lugarExpedicion.estado=atributo.@estado
      lugarExpedicion.pais=atributo.@pais
      lugarExpedicion.codigoPostal=atributo.@codigoPostal
      emisor.lugarExpedicion=lugarExpedicion
    }
    xml.Emisor.DomicilioFiscal.each{atributo->
      domicilioFiscal.calle=atributo.@calle
      domicilioFiscal.noExterior=atributo.@noExterior
      domicilioFiscal.noInterior=atributo.@noInterior
      domicilioFiscal.colonia=atributo.@colonia
      domicilioFiscal.municipio=atributo.@municipio
      domicilioFiscal.estado=atributo.@estado
      domicilioFiscal.pais=atributo.@pais
      domicilioFiscal.codigoPostal=atributo.@codigoPostal
      emisor.domicilioFiscal=domicilioFiscal
    }
    emisor
  }

  Receptor obtainReceiverFromInvoice(File invoice){
    def receptor=new Receptor()
    def direccionReceptor=new Direccion()

    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.Receptor.each{atributo->
      receptor.rfc=atributo.@rfc
      receptor.nombre=atributo.@nombre
    }
    xml.Receptor.Domicilio.each{atributo->
      direccionReceptor.calle=atributo.@calle
      direccionReceptor.noExterior=atributo.@noExterior
      direccionReceptor.noInterior=atributo.@noInterior
      direccionReceptor.colonia=atributo.@colonia
      direccionReceptor.municipio=atributo.@municipio
      direccionReceptor.estado=atributo.@estado
      direccionReceptor.pais=atributo.@pais
      direccionReceptor.codigoPostal=atributo.@codigoPostal
      receptor.direccionReceptor=direccionReceptor
    }
    receptor
  }

  List<Concepto> obtainConceptsFromInvoice(File invoice){
    List<Concepto> conceptos=[]
    Concepto concepto
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.Conceptos.Concepto.each{atributo->
      concepto = new Concepto()
      concepto.cantidad=Float.parseFloat(atributo.@cantidad.toString() ?: "0")
      concepto.unidad=atributo.@unidad
      concepto.noIdentificacion=atributo.@noIdentificacion
      concepto.descripcion=atributo.@descripcion
      concepto.valorUnitario=new BigDecimal(atributo.@valorUnitario.toString() ?: "0")
      concepto.importe=new BigDecimal(atributo.@importe.toString() ?: "0")
      conceptos.add(concepto)
    }
    conceptos
  }

  Impuesto obtainTaxesFromInvoice(File invoice){
    def impuesto = new Impuesto()
    Traslado traslado
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.Impuestos.each{atributo->
      impuesto.totalImpuestosTrasladado=new BigDecimal(atributo.@totalImpuestosTrasladados.toString() ?: 0)
    }
    xml.Impuestos.Traslados.Traslado.each{atributo->
      traslado=new Traslado()
      traslado.impuesto=atributo.@impuesto
      traslado.tasa=Float.parseFloat(atributo.@tasa.toString() ?: "0")
      traslado.importe=new BigDecimal(atributo.@importe.toString() ?: "0")
      impuesto.traslado.add(traslado)
    }
    impuesto
  }

  TimbreFiscalDigital obtainDigitalTaxStampFromInvoice(File invoice){
    def timbreFiscalDigital=new TimbreFiscalDigital()
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.Complemento.TimbreFiscalDigital.each{atributo->
      timbreFiscalDigital.fechaTimbrado=Date.parse("yyyy-MM-dd'T'HH:mm:ss",atributo.@FechaTimbrado.toString())
      timbreFiscalDigital.uuid=atributo.@UUID
      timbreFiscalDigital.noCertificadoSAT=atributo.@noCertificadoSAT
      timbreFiscalDigital.selloCFD=atributo.@selloCFD
      timbreFiscalDigital.selloSAT=atributo.@selloSAT
      timbreFiscalDigital.version=atributo.@version
    }
    timbreFiscalDigital
  }

  Addenda obtainAddendaFromInvoice(File invoice){
    def addenda=new Addenda()
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.Addenda.EstadoDeCuentaBancario.each{atributo->
      def estadoDeCuentaBancario=new EstadoDeCuentaBancario()
      estadoDeCuentaBancario.version=atributo.@version
      estadoDeCuentaBancario.numeroCuenta=atributo.@numeroCuenta
      estadoDeCuentaBancario.nombreCliente=atributo.@nombreCliente
      estadoDeCuentaBancario.periodo=atributo.@periodo
      estadoDeCuentaBancario.sucursal=atributo.@sucursal
      addenda.estadoDeCuentaBancario=estadoDeCuentaBancario
    }
    xml.Addenda.EstadoDeCuentaBancario.Movimientos.MovimientoECBFiscal.each{atributo->
      def movimientoECB=new MovimientoECB()
      movimientoECB.fecha=Date.parse("yyyy-MM-dd'T'HH:mm:ss", atributo.@fecha.toString())
      movimientoECB.referencia=atributo.@referencia
      movimientoECB.descripcion=atributo.@descripcion
      movimientoECB.importe=new BigDecimal(atributo.@importe.toString())
      movimientoECB.moneda=atributo.@moneda
      movimientoECB.saldoInicial=new BigDecimal(atributo.@saldoInicial.toString())
      movimientoECB.saldoAlCorte=new BigDecimal(atributo.@saldoAlCorte.toString())
      addenda.estadoDeCuentaBancario.movimientoECB.add(movimientoECB)
    }
    addenda
  }

  def obtainAddenda(File invoice){
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance",
      bfa3:"http://www.buzonfiscal.com/ns/addenda/bf/3")
    def listWithAddenda=[]
    getAttributesFromXML(xml.Addenda,listWithAddenda)
  }

  def getAttributesFromXML(def parentNode,def listAddenda){
    def childrens = parentNode.children()
    if(childrens.size()){
      childrens.each{ children ->
        //println "."*10+children.name()
        getAttributesFromXML(children,listAddenda)
      }
    }
    else{
      //println "*"*10+parentNode.name()+parentNode.attributes()
      listAddenda<<[(parentNode.name()):parentNode.attributes()]
    }
    listAddenda
  }

}
