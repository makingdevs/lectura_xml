package com.makingdevs

import com.makingdevs.accounting.AccountManager

class ReadInvoiceFromXmlTest extends GroovyTestCase {

  void testWhenAccountManagerIsSearchingInvoices(){
    AccountManager manager = new AccountManager()
    String location = "${System.getProperty('user.home')}/Documents/invoices"
    def files = manager.searchInvoicesInLocation(location)
    assert files
    assert files.size() == 59
    assert files.every { f -> f.absolutePath.split('\\.').last() == 'xml' }
  }

  void testWhenReadInvoiceAndObtainVoucher(){
    AccountManager manager = new AccountManager()
    File invoice = new File('.')
    Comprobante comprobante = manager.obtainVoucherFromInvoice(invoice)
    assert comprobante
    assert comprobante.serie
    assert comprobante.folio
    assert comprobante.fecha
    assert comprobante.formaDePago
    assert comprobante.subTotal
    assert comprobante.descuento
    assert comprobante.tipoCambio
    assert comprobante.moneda
    assert comprobante.total
    assert comprobante.metodoDePago
    assert comprobante.tipoDeComprobante
    assert comprobante.lugarExpedicion
    assert comprobante.numCtaPago
    assert comprobante.noCertificado
    assert comprobante.certificado
    assert comprobante.sello
    assert comprobante.timbreFiscalDigital
  }

  void testWhenVoucherHasTransmitterAndReceiver(){
    AccountManager manager = new AccountManager()
    File invoice = new File('.')
    Comprobante comprobante = manager.obtainVoucherFromInvoice(invoice)
    assert comprobante.emisor
    assert comprobante.receptor
  }

  void testWhenVoucherHasConceptsAndTaxes(){
    AccountManager manager = new AccountManager()
    File invoice = new File('.')
    Comprobante comprobante = manager.obtainVoucherFromInvoice(invoice)
    assert comprobante.conceptos
    assert comprobante.impuesto
  }
}
