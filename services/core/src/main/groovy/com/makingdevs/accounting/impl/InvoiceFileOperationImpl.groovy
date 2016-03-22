package com.makingdevs.accounting.impl

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFCell
import com.makingdevs.Comprobante
import com.makingdevs.accounting.InvoiceFileOperation
import com.makingdevs.accounting.AccountManager

class InvoiceFileOperationImpl implements InvoiceFileOperation{

  File createInvoicesFile(String path){
    def fileNames = new FileNameByRegexFinder().getFileNames(path, /.*\.xml/)
    def invoices = []
    AccountManager accountManager = new AccountManagerImpl()

    fileNames.each{ fileName ->
      invoices << accountManager.obtainVoucherFromInvoice(new File(fileName))
    }

    XSSFWorkbook workbook = generateWorkbookWithAllInvoices(invoices)
    def invoicesFile = new File("Invoices.xlsx")
    FileOutputStream out = new FileOutputStream(invoicesFile)
    workbook.write(out)
    out.close()

    invoicesFile
  }

  File createInvoiceCompleteDetailFile(String filePath){
    AccountManager accountManager = new AccountManagerImpl()
    Comprobante invoice = accountManager.obtainVoucherFromInvoice(new File(filePath))

    XSSFWorkbook workbook = generateWorkbookWithInvoiceCompleteDetail(invoice)
    def invoiceFile = new File("Invoices.xlsx")
    FileOutputStream out = new FileOutputStream(invoiceFile)
    workbook.write(out)
    out.close()
    invoiceFile
  }

  File createInvoiceWithAddendaFile(String path){
    AccountManager accountManager = new AccountManagerImpl()
    Comprobante invoice = accountManager.obtainAddenda(new File(filePath))

    XSSFWorkbook workbook = generateWorkbookWithAddenda(invoice)
    def invoiceFile = new File("InvoiceAddenda.xlsx")
    FileOutputStream out = new FileOutputStream(invoiceFile)
    workbook.write(out)
    out.close()
    invoiceFile
  }

  XSSFWorkbook generateExcelWorkbook(){
    XSSFWorkbook workbook = new XSSFWorkbook()
    XSSFSheet sheet=workbook.createSheet("Página_1")
    workbook
  }

  XSSFWorkbook generateWorkbookWithAllInvoices(List<Comprobante> invoices){
    XSSFWorkbook workbook = generateExcelWorkbook()
    addHeadersToWorkbook(workbook,getHeadersForDetailReport())

    invoices.each{ invoice ->
      addInvoiceDetailToWorkbook(invoice,workbook)
    }

    workbook
  }

  XSSFWorkbook generateWorkbookWithInvoiceCompleteDetail(Comprobante invoice){
    XSSFWorkbook workbook = generateExcelWorkbook()
    addHeadersToWorkbook(workbook,getHeadersForCompleteDetailReport())
    addCompleteInvoiceDetailToWorkbook(invoice,workbook)
    workbook
  }

  XSSFWorkbook generateWorkbookWithAddenda(Comprobante invoice){
    XSSFWorkbook workbook = generateExcelWorkbook()
    addRecordToWorkbook(workbook.getHeadersForAddenda(invoice))
    XSSFSheet sheet = workbook.getSheetAt(0)
    addCompleteInvoiceDetailToWorkbook(invoice,workbook)
    workbook
  }

  XSSFWorkbook generateWorkbookWithAddendaInvoice(Comprobante invoice){
    XSSFWorkbook workbook = generateExcelWorkbook()
    addHeadersToWorkbook(workbook,getHeadersForAddendaReport())
    XSSFSheet sheet = workbook.getSheetAt(0)

    def fields = [invoice.serie,invoice.fecha,invoice.subTotal,
                  invoice.descuento,invoice.impuesto.totalImpuestosTrasladado,
                  invoice.total,invoice.addenda.estadoDeCuentaBancario.periodo,
                  invoice.addenda.estadoDeCuentaBancario.sucursal,
                  invoice.addenda.estadoDeCuentaBancario.numeroCuenta,
                  invoice.addenda.estadoDeCuentaBancario.nombreCliente,
                  invoice.addenda.estadoDeCuentaBancario.version]


    addRecordToWorkbook(workbook,fields)

    def headers = invoice.addenda.estadoDeCuentaBancario.movimientoECB.first().class.declaredFields.findAll{ !it.synthetic }*.name.collect{ it.capitalize() }
    addHeadersToWorkbook(workbook,headers)

    invoice.addenda.estadoDeCuentaBancario.movimientoECB.each{ movement ->
      addRecordToWorkbook(workbook,[movement.fecha,movement.referencia,
                                    movement.descripcion,movement.importe,
                                    movement.moneda,movement.saldoInicial,
                                    movement.saldoAlCorte])
    }

    workbook
  }

  void addCompleteInvoiceDetailToWorkbook(Comprobante invoice,XSSFWorkbook workbook){
    def fields = [invoice.fecha,invoice.serie,invoice.folio,
                  invoice.formaDePago,invoice.subTotal,invoice.descuento,
                  invoice.total,invoice.tipoCambio,invoice.moneda,
                  invoice.metodoDePago,invoice.tipoDeComprobante,invoice.lugarExpedicion,
                  invoice.numCtaPago,invoice.noCertificado,invoice.certificado,
                  invoice.sello,invoice.total]

    addRecordToWorkbook(workbook,fields)
  }

  void addInvoiceDetailToWorkbook(Comprobante invoice,XSSFWorkbook workbook){
    def fields = [invoice.fecha,
                  invoice.subTotal,
                  invoice.descuento,
                  invoice.impuesto.totalImpuestosTrasladado,
                  invoice.total,
                  invoice.emisor.nombre,
                  invoice.emisor.rfc,
                  invoice.receptor.nombre,
                  invoice.receptor.rfc,
                  invoice.noCertificado,
                  invoice.sello,
                  invoice.folio,
                  invoice.formaDePago,
                  invoice.addenda.toString(),
                  invoice.lugarExpedicion,
                  invoice.timbreFiscalDigital.uuid,
                  invoice.tipoDeComprobante,
                  invoice.tipoCambio,
                  invoice.serie,
                  invoice.moneda,
                  invoice.numCtaPago,
                  invoice.conceptos*.descripcion.join(","),
                  invoice.certificado,
                  invoice.metodoDePago]

    addRecordToWorkbook(workbook,fields)
  }

  private void addHeadersToWorkbook(XSSFWorkbook workbook,headers){
    XSSFSheet sheet = workbook.getSheetAt(0)

    Row headerRow = sheet.createRow(sheet.getPhysicalNumberOfRows())
    XSSFCellStyle headerStyle = workbook.createCellStyle()
    headerStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
    headerStyle.fillPattern = CellStyle.SOLID_FOREGROUND

    headers.eachWithIndex{ header,index ->
      Cell headerCell = headerRow.createCell(index)
      headerCell.cellStyle = headerStyle
      headerCell.cellValue = header
    }
  }

  private def addRecordToWorkbook(workbook,fields){
    XSSFSheet sheet = workbook.getSheetAt(0)
    Row row = sheet.createRow(sheet.getPhysicalNumberOfRows())
    Cell cell = row.createCell(row.lastCellNum+1)
    CreationHelper createHelper = workbook.getCreationHelper()
    XSSFCellStyle dateStyle = workbook.createCellStyle()
    dateStyle.dataFormat = createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm")

    fields.each{ field ->
      if(field?.class?.simpleName == BigDecimal.class.simpleName)
        cell.cellType = XSSFCell.CELL_TYPE_NUMERIC
      else if(field?.class?.simpleName == Date.class.simpleName)
        cell.cellStyle = dateStyle

      cell.cellValue = field
      cell = row.createCell(row.lastCellNum)
    }
  }

  private def getHeadersForCompleteDetailReport(){
    ["Fecha","Serie","Folio","Forma de Pago","SubTotal","Descuento",
     "Total","Tipo de Cambio","Moneda","Método de Pago","Tipo De Comprobante",
     "Lugar de Expedición","Num.Cta Pago","No.Certificado","Certificado",
     "Sello","Total"]
  }

  private def getHeadersForDetailReport(){
    ["Fecha","Subtotal","Descuento","Impuesto","Total",
     "Emisor","Emisor RFC","Receptor","Receptor RFC","No.Certificado","Sello",
     "Folio","Forma de Pago","Addenda","LugarExpedicion",
     "TimbreFiscalDigital","TipoDeComprobante","TipoDeCambio",
     "Serie","Moneda","NumCtaPago","Conceptos",
     "Certificado","MetodoDePago"]
  }

  private def getHeadersForAddendaReport(){
    ["Serie","Fecha","Subtotal","Descuento","Impuesto",
     "Total","Addenda periodo","Sucursal","NumeroCuenta",
     "NombreCliente","Version"]
  }

  private def getHeadersForAddenda(Comprobante invoice){
    AccountManager accountManager = new AccountManagerImpl()
    def fields=[]
    def listOfHeaders=accountManager.obtainAddenda(invoice)
    listOfHeaders.each{nodo->
      nodo.collect{header->
        fields<< header.key
      }
    }
    fields
  }

}
