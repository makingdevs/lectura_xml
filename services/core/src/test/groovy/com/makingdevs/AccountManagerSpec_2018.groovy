package com.makingdevs
import spock.lang.Specification
import java.lang.Void as Should
import com.makingdevs.accounting.impl.AccountManagerImpl
import com.makingdevs.Comprobante

class AccountManagerSpec_2018 extends Specification{

  AccountManagerImpl accountManagerImpl

  def setup (){
    accountManagerImpl = new AccountManagerImpl()
  }

  Should "Get the voucher information from file"(){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura-2018.xml").getFile())
    when:
      Comprobante voucher=accountManagerImpl.obtainVoucherFromInvoice(invoice)
    then:
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
      voucher.emisor.rfc=="SOM101125UEA"
      voucher.receptor.rfc=="MDE130712JA6"
      voucher.impuesto
      voucher.conceptos
      voucher.timbreFiscalDigital
      voucher.addenda
  }

  Should "Get emisor from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura-2018.xml").getFile())
    when:
      Emisor emisor=accountManagerImpl.obtainTransmitterFromInvoice(invoice)
    then:
      emisor
  }

  Should "Get receptor from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura-2018.xml").getFile())
    when:
      Receptor receptor=accountManagerImpl.obtainReceiverFromInvoice(invoice)
    then:
      receptor
      receptor.direccionReceptor.calle=="CALZADA ERMITA IZTAPALAPA"
  }

  Should "Get conceptos from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura-2018.xml").getFile())
    when:
      List<Concepto> conceptos=accountManagerImpl.obtainConceptsFromInvoice(invoice)
    then:
      conceptos.size > 0
  }

  Should "Get impuesto from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura-2018.xml").getFile())
    when:
      Impuesto impuesto=accountManagerImpl.obtainTaxesFromInvoice(invoice)
    then:
      impuesto
      impuesto.totalImpuestosTrasladado==197.420000
      impuesto.traslado.size>0
  }

  Should "Get timbre fiscal digital from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura-2018.xml").getFile())
    when:
      TimbreFiscalDigital timbreFiscalDigital=accountManagerImpl.obtainDigitalTaxStampFromInvoice(invoice)
    then:
      timbreFiscalDigital
      timbreFiscalDigital.uuid=="4004340f-e1ea-4df6-9c97-30d701b01b47"
  }

  Should "Get addenda from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura-addenda.xml").getFile())
    when:
      Addenda addenda=accountManagerImpl.obtainAddendaFromInvoice(invoice)
    then:
      addenda
      addenda.estadoDeCuentaBancario.periodo =="DEL 2015/11/13 AL 2015/12/13"
  }
}
