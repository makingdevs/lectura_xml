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
  }

}