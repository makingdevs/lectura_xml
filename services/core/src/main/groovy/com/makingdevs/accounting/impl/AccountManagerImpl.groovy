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
    def emisor = new Emisor()
    def domicilioFiscal = new Direccion()
    def lugarExpedicion = new Direccion()
    def regimenFiscal = new RegimenFiscal()

    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.Emisor.each { atributo->
      emisor.rfc = atributo.@rfc ?: atributo.@Rfc
      emisor.nombre = atributo.@nombre ?: atributo.@Nombre
      regimenFiscal.regimen = atributo.RegimenFiscal.@regimen ?: atributo.RegimenFiscal.@Regimen
      emisor.regimen = regimenFiscal
    }
    xml.Emisor.ExpedidoEn.each { atributo->
      lugarExpedicion.calle = atributo.@calle ?: atributo.@Calle
      lugarExpedicion.noExterior = atributo.@noExterior ?: atributo.@NoExterior
      lugarExpedicion.colonia = atributo.@colonia ?: atributo.@Colonia
      lugarExpedicion.municipio = atributo.@municipio ?: atributo.@Municipio
      lugarExpedicion.estado = atributo.@estado ?: atributo.@Estado
      lugarExpedicion.pais = atributo.@pais ?: atributo.@Pais
      lugarExpedicion.codigoPostal = atributo.@codigoPostal ?: atributo.@CodigoPostal
      emisor.lugarExpedicion = lugarExpedicion
    }
    xml.Emisor.DomicilioFiscal.each{atributo->
      domicilioFiscal.calle = atributo.@calle ?: atributo.@Calle
      domicilioFiscal.noExterior = atributo.@noExterior ?: atributo.@NoExterior
      domicilioFiscal.noInterior = atributo.@noInterior ?: atributo.@NoInterior
      domicilioFiscal.colonia = atributo.@colonia ?: atributo.@Colonia
      domicilioFiscal.municipio = atributo.@municipio ?: atributo.@Municipio
      domicilioFiscal.estado = atributo.@estado ?: atributo.@Estado
      domicilioFiscal.pais = atributo.@pais ?: atributo.@Pais
      domicilioFiscal.codigoPostal = atributo.@codigoPostal ?: atributo.@CodigoPostal
      emisor.domicilioFiscal = domicilioFiscal
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
      direccionReceptor.calle = atributo.@calle ?: atributo.@Calle
      direccionReceptor.noExterior = atributo.@noExterior ?: atributo.@NoExterior
      direccionReceptor.noInterior = atributo.@noInterior ?: atributo.@NoInterior
      direccionReceptor.colonia = atributo.@colonia ?: atributo.@Colonia
      direccionReceptor.municipio = atributo.@municipio ?: atributo.@Municipio
      direccionReceptor.estado = atributo.@estado ?: atributo.@Estado
      direccionReceptor.pais = atributo.@pais ?: atributo.@Pais
      direccionReceptor.codigoPostal = atributo.@codigoPostal ?: atributo.@CodigoPostal
      receptor.direccionReceptor = direccionReceptor
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
      concepto.cantidad = Float.parseFloat((atributo.@cantidad ?: atributo.@Cantidad).toString())
      concepto.unidad = atributo.@unidad ?: atributo.@Unidad
      concepto.noIdentificacion = atributo.@noIdentificacion ?: atributo.@NoIdentificacion
      concepto.descripcion = atributo.@descripcion ?: atributo.@Descripcion
      concepto.valorUnitario = new BigDecimal((atributo.@valorUnitario ?: atributo.@ValorUnitario).toString())
      concepto.importe = new BigDecimal((atributo.@importe ?: atributo.@Importe).toString())
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
      impuesto.totalImpuestosTrasladado=new BigDecimal((atributo.@totalImpuestosTrasladados ?: atributo.@TotalImpuestosTrasladados).toString() ?: 0)
    }
    xml.Impuestos.Traslados.Traslado.each{atributo->
      traslado = new Traslado()
      traslado.impuesto = atributo.@impuesto ?: atributo.@Impuesto
      traslado.tasa = Float.parseFloat((atributo.@tasa ?: atributo.@Tasa).toString())
      traslado.importe = new BigDecimal((atributo.@importe ?: atributo.@Importe).toString())
      impuesto.traslado.add(traslado)
    }
    impuesto
  }

  TimbreFiscalDigital obtainDigitalTaxStampFromInvoice(File invoice){
    def timbreFiscalDigital = new TimbreFiscalDigital()
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.Complemento.TimbreFiscalDigital.each{atributo->
      timbreFiscalDigital.fechaTimbrado = Date.parse("yyyy-MM-dd'T'HH:mm:ss", (atributo.@fechaTimbrado ?: atributo.@FechaTimbrado).toString())
      timbreFiscalDigital.uuid = atributo.@UUID
      timbreFiscalDigital.noCertificadoSAT = atributo.@noCertificadoSAT ?: atributo.@NoCertificadoSAT
      timbreFiscalDigital.selloCFD = atributo.@selloCFD ?: atributo.@SelloCFD
      timbreFiscalDigital.selloSAT = atributo.@selloSAT ?: atributo.@SelloSAT
      timbreFiscalDigital.version = atributo.@version ?: atributo.@Version
    }
    timbreFiscalDigital
  }

  Addenda obtainAddendaFromInvoice(File invoice){
    def addenda = new Addenda()
    def xml  =  new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.Addenda.EstadoDeCuentaBancario.each{atributo->
      def estadoDeCuentaBancario = new EstadoDeCuentaBancario()
      estadoDeCuentaBancario.version = atributo.@version ?: atributo.@Version
      estadoDeCuentaBancario.numeroCuenta = atributo.@numeroCuenta ?: atributo.@NumeroCuenta
      estadoDeCuentaBancario.nombreCliente = atributo.@nombreCliente ?: atributo.@NombreCliente
      estadoDeCuentaBancario.periodo = atributo.@periodo ?: atributo.@Periodo
      estadoDeCuentaBancario.sucursal = atributo.@sucursal ?: atributo.@Sucursal
      addenda.estadoDeCuentaBancario = estadoDeCuentaBancario
    }
    xml.Addenda.EstadoDeCuentaBancario.Movimientos.MovimientoECBFiscal.each{atributo->
      def movimientoECB = new MovimientoECB()
      movimientoECB.fecha = Date.parse("yyyy-MM-dd'T'HH:mm:ss", (atributo.@fecha ?: atributo.@Fecha).toString())
      movimientoECB.referencia = atributo.@referencia ?: atributo.@Referencia
      movimientoECB.descripcion = atributo.@descripcion ?: atributo.@Descripcion
      movimientoECB.importe = new BigDecimal(atributo.@importe.toString())
      movimientoECB.moneda = atributo.@moneda ?: atributo.@Moneda
      movimientoECB.saldoInicial = new BigDecimal((atributo.@saldoInicial ?: atributo.@SaldoInicial).toString())
      movimientoECB.saldoAlCorte = new BigDecimal((atributo.@saldoAlCorte ?: atributo.@SaldoAlCorte).toString())
      addenda.estadoDeCuentaBancario.movimientoECB.add(movimientoECB)
    }
    addenda
  }

  def obtainAddenda(File invoice){
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance",
      bfa3:"http://www.buzonfiscal.com/ns/addenda/bf/3")
    def listWithAddenda = []
    getAttributesFromXML(xml.Addenda,listWithAddenda)
  }

  def getAttributesFromXML(def parentNode,def listAddenda){
    def childrens = parentNode.children()
    if(childrens.size()){
      childrens.each{ children ->
        getAttributesFromXML(children,listAddenda)
      }
    }
    else{
      listAddenda << [(parentNode.name()):parentNode.attributes()]
    }
    listAddenda
  }

}
