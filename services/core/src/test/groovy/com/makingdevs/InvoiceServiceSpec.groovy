package com.makingdevs
import spock.lang.Specification
import java.lang.Void as Should
import com.makingdevs.service.impl.InvoiceServiceImpl
import com.makingdevs.Comprobante

class InvoiceServiceSpec extends Specification{
  InvoiceServiceImpl invoiceServiceImpl
  def setup (){
    invoiceServiceImpl=new InvoiceServiceImpl()
  }
  Should "Get the voucher information from file"(){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura.xml").getFile())
    when:
      Comprobante voucher=invoiceServiceImpl.obtainVoucherFromInvoice(invoice)
    then:
      voucher
      voucher
      voucher.serie
      voucher.folio
      voucher.fecha
      voucher.formaDePago
      voucher.subTotal
      voucher.descuento >= 0
      voucher.moneda
      voucher.total
      voucher.metodoDePago
      voucher.tipoDeComprobante
      voucher.lugarExpedicion
      voucher.numCtaPago
      voucher.noCertificado
      voucher.certificado
      voucher.sello
      
  }

  Should "Get emisor from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura.xml").getFile())
    when:
      Emisor emisor=invoiceServiceImpl.obtainTransmitterFromInvoice(invoice)
    then:
      emisor
  }

  Should "Get receptor from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura.xml").getFile())
    when:
      Receptor receptor=invoiceServiceImpl.obtainReceiverFromInvoice(invoice)
    then:
      receptor
      receptor.direccionReceptor.calle=="CALZADA ERMITA IZTAPALAPA"
  }

  Should "Get conceptos from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura.xml").getFile())
    when:
      List<Concepto> conceptos=invoiceServiceImpl.obtainConceptsFromInvoice(invoice)
    then:
      conceptos.size > 0
  }

  Should "Get impuesto from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura.xml").getFile())
    when:
      Impuesto impuesto=invoiceServiceImpl.obtainTaxesFromInvoice(invoice)
    then:
      impuesto
      impuesto.totalImpuestosTrasladado==197.420000
      impuesto.traslado.size>0
  }

  Should "Get timbre fiscal digital from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura.xml").getFile())
    when:
      TimbreFiscalDigital timbreFiscalDigital=invoiceServiceImpl.obtainDigitalTaxStampFromInvoice(invoice)
    then:
      timbreFiscalDigital
      timbreFiscalDigital.uuid=="4004340f-e1ea-4df6-9c97-30d701b01b47"
  }

  Should "Get addenda from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura-addenda.xml").getFile())
    when:
      Addenda addenda=invoiceServiceImpl.obtainAddendaFromInvoice(invoice)
    then:
      addenda
      addenda.estadoDeCuentaBancario.periodo =="DEL 2015/11/13 AL 2015/12/13"
  }
}