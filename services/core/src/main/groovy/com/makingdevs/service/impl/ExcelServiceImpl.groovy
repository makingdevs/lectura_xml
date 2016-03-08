package com.makingdevs.service.impl

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
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

    def headers = ["Fecha","Subtotal","Descuento","Impuesto",
                   "Total","Emisor","Receptor","NoCertificado","Sello","Folio",
                   "FormaDePago","Addenda","LugarExpedicion","TimbreFiscalDigital",
                   "TipoDeComprobante","TipoDeCambio","Serie","Moneda","NumCtaPago",
                   "Conceptos","Certificado","MetodoDePago"]

    Row headerRow = sheet.createRow(0)

    headers.eachWithIndex{ header, index ->
      Cell cell = headerRow.createCell(index)
      cell.setCellStyle(headerStyle)
      cell.setCellValue(header)
    }

    workbook
  }

  XSSFWorkbook generateFileExcelWithAllInvoices(List<Comprobante> invoices){

  }

  XSSFWorkbook generateFileExcelWithDetailInvoice(Comprobante invoice){

  }

}
