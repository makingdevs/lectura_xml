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

  XSSFWorkbook generateExcelWorkbookWithInvoiceDetail(Comprobante invoice){
    XSSFWorkbook workbook = new XSSFWorkbook()
    XSSFSheet sheet=workbook.createSheet("Página_1")
    CreationHelper createHelper = workbook.getCreationHelper()
    XSSFCellStyle dateStyle = workbook.createCellStyle()
    XSSFCellStyle headerStyle = workbook.createCellStyle()

    dateStyle.dataFormat = createHelper.createDataFormat().getFormat("dd-MM-YYYY HH:mm")
    headerStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
    headerStyle.fillPattern = CellStyle.SOLID_FOREGROUND

    def fields = ["Fecha":invoice.fecha,
                  "Subtotal":invoice.subTotal,
                  "Descuento":invoice.descuento,
                  "Impuesto":invoice.impuesto.totalImpuestosTrasladado,
                  "Total":invoice.total,
                  "Emisor":invoice.emisor.nombre,
                  "Receptor":invoice.receptor.nombre,
                  "No.Certificado":invoice.noCertificado,
                  "Sello":invoice.sello,
                  "Folio":invoice.folio,
                  "FormaDePago":invoice.formaDePago,
                  "Addenda":invoice.addenda.toString(),
                  "LugarExpedicion":invoice.lugarExpedicion,
                  "TimbreFiscalDigital":invoice.timbreFiscalDigital.uuid,
                  "TipoDeComprobante":invoice.tipoDeComprobante,
                  "TipoDeCambio":invoice.tipoCambio,
                  "Serie":invoice.serie,
                  "Moneda":invoice.moneda,
                  "NumCtaPago":invoice.numCtaPago,
                  "Conceptos":invoice.conceptos.toString(),
                  "Certificado":invoice.certificado,
                  "MetodoDePago":invoice.metodoDePago]

    Row headerRow = sheet.createRow(sheet.lastRowNum)

    fields.eachWithIndex{ field, index ->
      Cell headerCell = headerRow.createCell(index)
      headerCell.cellStyle = headerStyle
      headerCell.cellValue = field.key
    }

    Row invoiceRow = sheet.createRow(sheet.lastRowNum+1)
    Cell invoiceCell = invoiceRow.createCell(invoiceRow.lastCellNum+1)

    fields.each{ field ->
      if(field.value?.class?.simpleName == BigDecimal.class.simpleName)
        invoiceCell.cellType = XSSFCell.CELL_TYPE_NUMERIC
      else if(field.value?.class?.simpleName == Date.class.simpleName)
        invoiceCell.cellStyle = dateStyle

      invoiceCell.cellValue = field.value
      invoiceCell = invoiceRow.createCell(invoiceRow.lastCellNum)
    }

    workbook
  }

  XSSFWorkbook generateFileExcelWithAllInvoices(List<Comprobante> invoices){

  }

  XSSFWorkbook generateFileExcelWithDetailInvoice(Comprobante invoice){

  }

}
