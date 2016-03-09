package com.makingdevs.service.impl

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
import com.makingdevs.service.ExcelService

class ExcelServiceImpl implements ExcelService{

  XSSFWorkbook generateExcelWorkbook(){
    XSSFWorkbook workbook = new XSSFWorkbook()
    XSSFSheet sheet=workbook.createSheet("Página_1")
    workbook
  }

  void addInvoiceDetailToWorkbook(Comprobante invoice,XSSFWorkbook workbook){
    CreationHelper createHelper = workbook.getCreationHelper()
    XSSFSheet sheet = workbook.getSheetAt(0)
    XSSFCellStyle dateStyle = workbook.createCellStyle()
    dateStyle.dataFormat = createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm")

    def fields = [invoice.fecha,
                  invoice.subTotal,
                  invoice.descuento,
                  invoice.impuesto.totalImpuestosTrasladado,
                  invoice.total,
                  invoice.emisor.nombre,
                  invoice.receptor.nombre,
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
                  invoice.conceptos.toString(),
                  invoice.certificado,
                  invoice.metodoDePago]

    Row invoiceRow = sheet.createRow(sheet.getPhysicalNumberOfRows())
    Cell invoiceCell = invoiceRow.createCell(invoiceRow.lastCellNum+1)

    fields.each{ field ->
      if(field?.class?.simpleName == BigDecimal.class.simpleName)
        invoiceCell.cellType = XSSFCell.CELL_TYPE_NUMERIC
      else if(field?.class?.simpleName == Date.class.simpleName)
        invoiceCell.cellStyle = dateStyle

      invoiceCell.cellValue = field
      invoiceCell = invoiceRow.createCell(invoiceRow.lastCellNum)
    }
  }

  XSSFWorkbook generateWorkbookWithAllInvoices(List<Comprobante> invoices){
    XSSFWorkbook workbook = generateExcelWorkbook()
    addHeadersToWorkbook(workbook)

    invoices.each{ invoice ->
      addInvoiceDetailToWorkbook(invoice,workbook)
    }

    FileOutputStream out = new FileOutputStream(new File("Test_Excel.xlsx"))
    workbook.write(out)
    out.close()
    workbook
  }

  XSSFWorkbook generateFileExcelWithAddendaInvoice(Comprobante invoice){

  }

  private void addHeadersToWorkbook(XSSFWorkbook workbook){
    XSSFSheet sheet = workbook.getSheetAt(0)

    def headers = ["Fecha","Subtotal","Descuento","Impuesto","Total",
                   "Emisor","Receptor","No.Certificado","Sello",
                   "Folio","FormaDePago","Addenda","LugarExpedicion",
                   "TimbreFiscalDigital","TipoDeComprobante","TipoDeCambio",
                   "Serie","Moneda","NumCtaPago","Conceptos",
                   "Certificado","MetodoDePago"]

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

}
