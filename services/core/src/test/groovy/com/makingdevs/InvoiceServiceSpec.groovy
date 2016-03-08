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
}