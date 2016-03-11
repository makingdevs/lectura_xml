package com.makingdevs

import spock.lang.Specification
import java.lang.Void as Should
import com.makingdevs.Comprobante
import com.makingdevs.Emisor
import com.makingdevs.Receptor
import com.makingdevs.Direccion
import com.makingdevs.RegimenFiscal
import com.makingdevs.EstadoDeCuentaBancario
import com.makingdevs.MovimientoECB
import com.makingdevs.accounting.impl.InvoiceFileOperationImpl
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class InvoiceFileOperationSpec extends Specification{

  InvoiceFileOperationImpl invoiceFileOperationImpl

  def setup(){
    invoiceFileOperationImpl = new InvoiceFileOperationImpl()
  }

  Should "create an excel workbook with the invoice info"(){
    given:"the workbook"
      def workbook = invoiceFileOperationImpl.generateExcelWorkbook()
    and:
      def invoice = createInvoice()
    when:
      invoiceFileOperationImpl.addInvoiceDetailToWorkbook(invoice,workbook)
    then:
      workbook.getSheetAt(0).getRow(0).getCell(1).numericCellValue == 10
  }

  Should "create an excel workbook with invoices info"(){
    given:"the invoices"
      def invoices = [createInvoice(),createInvoice()]
    when:
      def invoicesWorkbook = invoiceFileOperationImpl.generateWorkbookWithAllInvoices(invoices)
    then:
      invoicesWorkbook.getSheetAt(0).getPhysicalNumberOfRows()== 3
  }

  Should "create an excel workbook with addenda info"(){
    given:"the invoice"
      def invoice = createInvoice()
    and:"the account bank state"
      def ecbMovements = [new MovimientoECB(fecha:new Date(),
                                            referencia:"ABCD",
                                            descripcion:"ABCD",
                                            importe:400,
                                            moneda:"Una moneda",
                                            saldoInicial:400,
                                            saldoAlCorte:400)]

      def accountBankState = new EstadoDeCuentaBancario(version:0,
                                                        numeroCuenta:"0123456789",
                                                        nombreCliente:"Gamaliel Jiménez",
                                                        periodo:"2016",
                                                        sucursal:"Sucursal 1",
                                                        movimientoECB:ecbMovements)

      def addenda = new Addenda(estadoDeCuentaBancario:accountBankState)
      invoice.addenda = addenda
    when:
      def invoiceWorkbook = invoiceFileOperationImpl.generateWorkbookWithAddendaInvoice(invoice)
    then:
      invoiceWorkbook
  }

  Should "create an excel file with the invoices info"(){
    given:"the files path"
      String path = "${System.getProperty('user.home')}/Workspace/facturas/12_Diciembre"
    when:
      def invoicesFile = invoiceFileOperationImpl.createInvoicesFile(path)
    then:
      invoicesFile.length()
    cleanup:
      invoicesFile.delete()
  }

  Should "create an excel file with the invoice complete detail"(){
    given:
      String filePath = "${System.getProperty('user.home')}/Workspace/facturas/12_Diciembre/151006.AERL.0000014.MAMR750818DF3.MDE130712JA6.xml"
    when:
      def invoiceFile = invoiceFileOperationImpl.createInvoiceCompleteDetailFile(filePath)
    then:
      invoiceFile.length()
    cleanup:
      invoiceFile.delete()
  }

  private Comprobante createInvoice(){
    def emisor = new Emisor(rfc:"JIGE930831RZ1",
                            nombre:"Gamaliel Jiménez",
                            domicilioFiscal:new Direccion(calle:"Chihuahua",
                            municipio:"Cuauhtémoc",
                            estado:"Distrito Federal",
                            pais:"México",
                            codigoPostal:"06700",
                            noExterior:"230",
                            noInterior:"S/N"),
                            lugarExpedicion:new Direccion(calle:"Chihuahua",
                            municipio:"Cuauhtémoc",
                            estado:"Distrito Federal",
                            pais:"México",
                            codigoPostal:"06700",
                            noExterior:"230",
                            noInterior:"S/N"),
                            regimen:new RegimenFiscal(regimen:"Régimen de Incorporación Fiscal"))

    def receptor = new Receptor(rfc:"JIGE930831RZ1",
                                nombre:"Gamaliel Jiménez",
                                direccionReceptor:new Direccion(calle:"Chihuahua",
                                                                municipio:"Cuauhtémoc",
                                                                estado:"Distrito Federal",
                                                                pais:"México",
                                                                codigoPostal:"06700",
                                                                noExterior:"230",
                                                                noInterior:"S/N"))
    def tax = new Impuesto(totalImpuestosTrasladado:5.5)

    def fiscalStamp = new TimbreFiscalDigital(fechaTimbrado:new Date(),
                                              uuid:"BD6B-BD6B-BD6B-BD6B")
    def invoice = new Comprobante(serie:"TUH",
                                    folio:"1234567",
                                    fecha:new Date(),
                                    formaDePago:"En una sola exhibición",
                                    subTotal:10,
                                    total:11.6,
                                    metodoDePago:"Tarjeta de Crédito",
                                    tipoDeComprobante:"Ingreso",
                                    lugarExpedicion:"Lugar 1",
                                    numCtaPago:1234,
                                    noCertificado:"00000000000000000000",
                                    certificado:"ANBgkqhkiG9w0BAQEFAAOnV0YX",
                                    sello:"5yBSuJJ4kye4",
                                    emisor:emisor,
                                    receptor:receptor,
                                    impuesto:tax,
                                    timbreFiscalDigital:fiscalStamp)
  }

}
