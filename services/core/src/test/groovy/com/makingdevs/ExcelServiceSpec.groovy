package com.makingdevs

import spock.lang.Specification
import java.lang.Void as Should
import com.makingdevs.Comprobante
import com.makingdevs.Emisor
import com.makingdevs.Receptor
import com.makingdevs.Direccion
import com.makingdevs.RegimenFiscal
import com.makingdevs.service.impl.ExcelServiceImpl
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelServiceSpec extends Specification{

  ExcelServiceImpl excelServiceImpl

  def setup(){
    excelServiceImpl = new ExcelServiceImpl()
  }

  Should "create an excel workbook with the invoice info"(){
    given:"the workbook"
      def workbook = excelServiceImpl.generateExcelWorkbook()
    and:
      def invoice = createInvoice()
    when:
      excelServiceImpl.addInvoiceDetailToWorkbook(invoice,workbook)
    then:
      workbook.getSheetAt(0).getRow(0).getCell(1).numericCellValue == 10
  }

  Should "create an excel workbook with invoices info"(){
    given:"the invoices"
      def invoices = [createInvoice(),createInvoice()]
    when:
      def invoicesWorkbook = excelServiceImpl.generateWorkbookWithAllInvoices(invoices)
    then:
      invoicesWorkbook.getSheetAt(0).getPhysicalNumberOfRows()== 3
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
