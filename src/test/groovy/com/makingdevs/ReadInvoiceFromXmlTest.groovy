package com.makingdevs

import com.makingdevs.accounting.AccountManager
import com.makingdevs.warehouse.*
import org.apache.poi.xssf.usermodel.*

class ReadInvoiceFromXmlTest extends GroovyTestCase {

  void testWhenAccountManagerIsSearchingInvoices(){
    AccountManager manager = new AccountManager()
    String location = "${System.getProperty('user.home')}/workspace/facturas/12_Diciembre"
    
    def files = manager.searchInvoicesInLocation(location)
    assert files
    assert files.size() == 58
    assert files.every { f -> f.absolutePath.split('\\.').last() == 'xml' }
  }

  void testWhenReadInvoiceAndObtainVoucher(){
    AccountManager manager = new AccountManager()
    File invoice = new File("${System.getProperty('user.home')}/workspace/prosesu/src/test/resources/factura.xml")
    

    Comprobante voucher = manager.obtainVoucherFromInvoice(invoice)
    voucher.emisor = manager.obtainTransmitterFromInvoice(invoice)
    voucher.receptor = manager.obtainReceiverFromInvoice(invoice)
    voucher.conceptos = manager.obtainConceptsFromInvoice(invoice)
    voucher.impuesto = manager.obtainTaxesFromInvoice(invoice)
    voucher.timbreFiscalDigital = manager.obtainDigitalTaxStampFromInvoice(invoice)
    voucher.addenda = manager.obtainAddendaFromInvoice(invoice)
    assert voucher
    assert voucher.serie
    assert voucher.folio
    assert voucher.fecha
    assert voucher.formaDePago
    assert voucher.subTotal
    assert voucher.descuento >= 0
    assert voucher.moneda
    assert voucher.total
    assert voucher.metodoDePago
    assert voucher.tipoDeComprobante
    assert voucher.lugarExpedicion
    assert voucher.numCtaPago
    assert voucher.noCertificado
    assert voucher.certificado
    assert voucher.sello
    assert voucher.emisor
    assert voucher.receptor
    assert voucher.conceptos
    assert voucher.impuesto
    assert voucher.timbreFiscalDigital
    assert voucher.addenda

  }

  void testWhenVoucherHasTransmitter(){
    AccountManager manager = new AccountManager()
    File invoice = new File("${System.getProperty('user.home')}/workspace/prosesu/src/test/resources/factura.xml")
    Emisor emisor = manager.obtainTransmitterFromInvoice(invoice)
    assert emisor
  }

  void testWhenVoucherHasReceiver(){
    AccountManager manager = new AccountManager()
    File invoice = new File("${System.getProperty('user.home')}/workspace/prosesu/src/test/resources/factura.xml")
    Receptor receptor = manager.obtainReceiverFromInvoice(invoice)
    assert receptor
  }
 

  void testWhenVoucherHasConcepts(){
    AccountManager manager = new AccountManager()
    File invoice = new File("${System.getProperty('user.home')}/workspace/prosesu/src/test/resources/factura.xml")
    List<Concepto> conceptos = manager.obtainConceptsFromInvoice(invoice)
    assert conceptos.size > 0
  }

  void testWhenVoucherHasTaxes(){
    AccountManager manager = new AccountManager()
    File invoice = new File("${System.getProperty('user.home')}/workspace/prosesu/src/test/resources/factura.xml")
    Impuesto impuesto = manager.obtainTaxesFromInvoice(invoice)
    assert impuesto
  }

  void testWhenVoucherHasDigitalTaxStamp(){
    AccountManager manager = new AccountManager()
    File invoice = new File("${System.getProperty('user.home')}/workspace/prosesu/src/test/resources/factura.xml")
    TimbreFiscalDigital timbreFiscalDigital = manager.obtainDigitalTaxStampFromInvoice(invoice)
    assert timbreFiscalDigital
  }

  void testWhenVoucherHasAddenda(){
    AccountManager manager = new AccountManager()
    File invoice = new File("${System.getProperty('user.home')}/workspace/facturas/11-12-2015_4931730006062697.xml")
    Addenda addenda = manager.obtainAddendaFromInvoice(invoice)
    assert addenda
  }
  
  void testWhenVoucherHasNotExchangeRate(){
    AccountManager manager = new AccountManager()
    File invoice = new File("${System.getProperty('user.home')}/workspace/prosesu/src/test/resources/factura.xml")
    Comprobante voucher = manager.obtainVoucherFromInvoice(invoice)
    assert voucher.tipoCambio==null
  }

  void testWhenVoucherHasExchangeRate(){
    AccountManager manager = new AccountManager()
    File invoice = new File("${System.getProperty('user.home')}/workspace/prosesu/src/test/resources/factura.xml")
    Comprobante voucher = manager.obtainVoucherFromInvoice(invoice)
    //assert voucher.tipoCambio
  }

  void testWhenGenerateFileExcel(){
    FileOperation fileOperation =new FileOperation()
    XSSFWorkbook fileExcel=fileOperation.generateFileExcel()
    assert fileExcel
    assert fileExcel.getNumberOfSheets() > 0
  }

  void testWhenGenerateFileExcelWithAllInvoices (){
    FileOperation fileOperation =new FileOperation()
    String location = "${System.getProperty('user.home')}/workspace/facturas/12_Diciembre"
    
    XSSFWorkbook fileExcel=fileOperation.generateFileExcelWithAllInvoices(location)
    assert fileExcel
  }

  void testWhenGenerateFileExcelWithAddendaInvoice (){
    FileOperation fileOperation =new FileOperation()
    
    String file = "${System.getProperty('user.home')}/workspace/facturas/11-12-2015_4931730006062697.xml"
    XSSFWorkbook fileExcel=fileOperation.generateFileExcelWithAddendaInvoice(file)
    assert fileExcel
  }

  void testWhenGenerateFileExcelWithDetailInvoice (){
    FileOperation fileOperation =new FileOperation()
    
    String file = "${System.getProperty('user.home')}/workspace/facturas/11-12-2015_4931730006062697.xml"
    XSSFWorkbook fileExcel=fileOperation.generateFileExcelWithDetailInvoice(file)
    assert fileExcel
  }

  void testGetInvoiceTitles(){
    FileOperation fileOperation =new FileOperation()
    def invoiceTitles=fileOperation.getInvoiceTitles()
    assert invoiceTitles
  }
}
