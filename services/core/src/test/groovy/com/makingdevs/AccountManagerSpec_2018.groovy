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
      voucher.emisor.rfc=="PMU940317114"
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
      impuesto.totalImpuestosTrasladado==303.31
      impuesto.traslado.size>0
  }

  Should "Get timbre fiscal digital from voucher" (){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura-2018.xml").getFile())
    when:
      TimbreFiscalDigital timbreFiscalDigital=accountManagerImpl.obtainDigitalTaxStampFromInvoice(invoice)
    then:
      timbreFiscalDigital
      timbreFiscalDigital.uuid=="00651edb-ed72-46ad-8e57-2385205eb4ff"
  }

}
