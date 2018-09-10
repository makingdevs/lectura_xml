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
    xml.attributes().each { attributeXML ->
      voucher.getProperties().each { attributeVoucher ->
        if(attributeXML.key.equalsIgnoreCase(attributeVoucher.key)){
          if(attributeVoucher.key.equalsIgnoreCase("fecha")){
            voucher[attributeVoucher.key] = Date.parse("yyyy-MM-dd'T'HH:mm:ss", attributeXML.value)
          }
          else if(["total","subTotal","descuento","tipoCambio"]*.toLowerCase().contains(attributeVoucher.key.toLowerCase())){
            voucher[attributeVoucher.key] = new BigDecimal(attributeXML.value.trim())
          }
          else{
            voucher[attributeVoucher.key] = attributeXML.value
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
    voucher.formaDePago = xml.@formaDePago.text() ?: xml.@FormaPago.text() ?: "Sin datos"
    voucher.metodoDePago = xml.@metodoDePago.text() ?: xml.@MetodoPago.text() ?: "Sin datos"
    voucher.numCtaPago = xml.@numCtaPago.text() ?: xml.@NumCtaPago.text() ?: "Sin datos"
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
    def emisorNode = xml.'cfdi:Emisor'
    emisor.rfc = emisorNode.@rfc.text() ?: emisorNode.@Rfc.text() ?: "Sin datos"
    emisor.nombre = emisorNode.@nombre.text() ?: emisorNode.@Nombre.text() ?: "Sin datos"
    regimenFiscal.regimen = emisorNode.RegimenFiscal.@regimen.text() ?: emisorNode.RegimenFiscal.@Regimen.text() ?: "Sin datos"
    emisor.regimen = regimenFiscal
    xml.'cfdi:Emisor'.'cfdi:ExpedidoEn'.each { atributo->
      lugarExpedicion.calle = atributo.@calle.text() ?: atributo.@Calle.text() ?: "Sin datos"
      lugarExpedicion.noExterior = atributo.@noExterior.text() ?: atributo.@NoExterior.text() ?: "Sin datos"
      lugarExpedicion.colonia = atributo.@colonia.text() ?: atributo.@Colonia.text() ?: "Sin datos"
      lugarExpedicion.municipio = atributo.@municipio.text() ?: atributo.@Municipio.text() ?: "Sin datos"
      lugarExpedicion.estado = atributo.@estado.text() ?: atributo.@Estado.text() ?: "Sin datos"
      lugarExpedicion.pais = atributo.@pais.text() ?: atributo.@Pais.text() ?: "Sin datos"
      lugarExpedicion.codigoPostal = atributo.@codigoPostal.text() ?: atributo.@CodigoPostal.text() ?: "Sin datos"
      emisor.lugarExpedicion = lugarExpedicion
    }
    xml.'cfdi:Emisor'.'cfdi:DomicilioFiscal'.each { atributo->
      domicilioFiscal.calle = atributo.@calle.text() ?: atributo.@Calle.text() ?: "Sin datos"
      domicilioFiscal.noExterior = atributo.@noExterior.text() ?: atributo.@NoExterior.text() ?: "Sin datos"
      domicilioFiscal.noInterior = atributo.@noInterior.text() ?: atributo.@NoInterior.text() ?: "Sin datos"
      domicilioFiscal.colonia = atributo.@colonia.text() ?: atributo.@Colonia.text() ?: "Sin datos"
      domicilioFiscal.municipio = atributo.@municipio.text() ?: atributo.@Municipio.text() ?: "Sin datos"
      domicilioFiscal.estado = atributo.@estado.text() ?: atributo.@Estado.text() ?: "Sin datos"
      domicilioFiscal.pais = atributo.@pais.text() ?: atributo.@Pais.text() ?: "Sin datos"
      domicilioFiscal.codigoPostal = atributo.@codigoPostal.text() ?: atributo.@CodigoPostal.text() ?: "Sin datos"
      emisor.domicilioFiscal = domicilioFiscal
    }
    emisor
  }

  Receptor obtainReceiverFromInvoice(File invoice){
    def receptor = new Receptor()
    def direccionReceptor = new Direccion()

    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    receptor.rfc = xml.'cfdi:Receptor'.@rfc.text() ?: xml.'cfdi:Receptor'.@Rfc.text() ?: "Sin datos"
    receptor.nombre = xml.'cfdi:Receptor'.@nombre.text() ?: xml.'cfdi:Receptor'.@Nombre.text() ?: "Sin datos"
    xml.'cfdi:Receptor'.'cfdi:Domicilio'.each { atributo->
      direccionReceptor.calle = atributo.@calle.text() ?: atributo.@Calle.text() ?: "Sin datos"
      direccionReceptor.noExterior = atributo.@noExterior.text() ?: atributo.@NoExterior.text() ?: "Sin datos"
      direccionReceptor.noInterior = atributo.@noInterior.text() ?: atributo.@NoInterior.text() ?: "Sin datos"
      direccionReceptor.colonia = atributo.@colonia.text() ?: atributo.@Colonia.text() ?: "Sin datos"
      direccionReceptor.municipio = atributo.@municipio.text() ?: atributo.@Municipio.text() ?: "Sin datos"
      direccionReceptor.estado = atributo.@estado.text() ?: atributo.@Estado.text() ?: "Sin datos"
      direccionReceptor.pais = atributo.@pais.text() ?: atributo.@Pais.text() ?: "Sin datos"
      direccionReceptor.codigoPostal = atributo.@codigoPostal.text() ?: atributo.@CodigoPostal.text() ?: "Sin datos"
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
    xml.'cfdi:Conceptos'.'cfdi:Concepto'.each { atributo->
      concepto = new Concepto()
      concepto.cantidad = Float.parseFloat(atributo.@cantidad.text() ?: atributo.@Cantidad.text())
      concepto.unidad = atributo.@unidad.text() ?: atributo.@Unidad.text()
      concepto.noIdentificacion = atributo.@noIdentificacion.text() ?: atributo.@NoIdentificacion.text()
      concepto.descripcion = atributo.@descripcion.text() ?: atributo.@Descripcion.text()
      concepto.valorUnitario = new BigDecimal(atributo.@valorUnitario.text() ?: atributo.@ValorUnitario.text())
      concepto.importe = new BigDecimal(atributo.@importe.text() ?: atributo.@Importe.text())
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
    xml.'cfdi:Impuestos'.each { atributo->
      impuesto.totalImpuestosTrasladado = new BigDecimal((atributo.@totalImpuestosTrasladados.text() ?: atributo.@TotalImpuestosTrasladados.text()) ?: 0)
    }
    xml.'cfdi:Impuestos'.'cfdi:Traslados'.'cfdi:Traslado'.each { atributo->
      traslado = new Traslado()
      traslado.impuesto = atributo.@impuesto.text() ?: atributo.@Impuesto.text()
      traslado.tasa = Float.parseFloat((atributo.@tasa.text() ?: atributo.@TasaOCuota.text()))
      traslado.importe = new BigDecimal((atributo.@importe.text() ?: atributo.@Importe.text()))
      impuesto.traslado.add(traslado)
    }
    impuesto
  }

  TimbreFiscalDigital obtainDigitalTaxStampFromInvoice(File invoice){
    def timbreFiscalDigital = new TimbreFiscalDigital()
    def xml = new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance", tfd:"http://www.sat.gob.mx/TimbreFiscalDigital")
    xml.'cfdi:Complemento'.TimbreFiscalDigital.each { atributo->
      timbreFiscalDigital.fechaTimbrado = Date.parse("yyyy-MM-dd'T'HH:mm:ss", (atributo.@fechaTimbrado.text() ?: atributo.@FechaTimbrado.text()))
      timbreFiscalDigital.uuid = atributo.@UUID.text()
      timbreFiscalDigital.noCertificadoSAT = atributo.@noCertificadoSAT.text() ?: atributo.@NoCertificadoSAT.text()
      timbreFiscalDigital.selloCFD = atributo.@selloCFD.text() ?: atributo.@SelloCFD.text()
      timbreFiscalDigital.selloSAT = atributo.@selloSAT.text() ?: atributo.@SelloSAT.text()
      timbreFiscalDigital.version = atributo.@version.text() ?: atributo.@Version.text()
    }
    timbreFiscalDigital
  }

  Addenda obtainAddendaFromInvoice(File invoice){
    def addenda = new Addenda()
    def xml  =  new XmlSlurper().parseText(invoice.getText()).declareNamespace(
      cfdi:"http://www.sat.gob.mx/cfd/3",
      xsi:"http://www.w3.org/2001/XMLSchema-instance")
    xml.'cfdi:Addenda'.EstadoDeCuentaBancario.each { atributo->
      def estadoDeCuentaBancario = new EstadoDeCuentaBancario()
      estadoDeCuentaBancario.version = atributo.@version.text() ?: atributo.@Version.text()
      estadoDeCuentaBancario.numeroCuenta = atributo.@numeroCuenta.text() ?: atributo.@NumeroCuenta.text()
      estadoDeCuentaBancario.nombreCliente = atributo.@nombreCliente.text() ?: atributo.@NombreCliente.text()
      estadoDeCuentaBancario.periodo = atributo.@periodo.text() ?: atributo.@Periodo.text()
      estadoDeCuentaBancario.sucursal = atributo.@sucursal.text() ?: atributo.@Sucursal.text()
      addenda.estadoDeCuentaBancario = estadoDeCuentaBancario
    }
    xml.'cfdi:Addenda'.'cfdi:EstadoDeCuentaBancario'.'cfdi:Movimientos'.'cfdi:MovimientoECBFiscal'.each { atributo->
      def movimientoECB = new MovimientoECB()
      movimientoECB.fecha = Date.parse("yyyy-MM-dd'T'HH:mm:ss", (atributo.@fecha.text() ?: atributo.@Fecha.text()))
      movimientoECB.referencia = atributo.@referencia.text() ?: atributo.@Referencia.text()
      movimientoECB.descripcion = atributo.@descripcion.text() ?: atributo.@Descripcion.text()
      movimientoECB.importe = new BigDecimal(atributo.@importe.text() ?: atributo.@Importe.text())
      movimientoECB.moneda = atributo.@moneda.text() ?: atributo.@Moneda.text()
      movimientoECB.saldoInicial = new BigDecimal((atributo.@saldoInicial.text() ?: atributo.@SaldoInicial.text()))
      movimientoECB.saldoAlCorte = new BigDecimal((atributo.@saldoAlCorte.text() ?: atributo.@SaldoAlCorte.text()))
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
