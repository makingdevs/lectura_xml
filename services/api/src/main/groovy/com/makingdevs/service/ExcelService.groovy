package com.makingdevs.service

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import com.makingdevs.Comprobante

interface ExcelService{

  XSSFWorkbook generateExcelWorkbook()

  void addInvoiceDetailToWorkbook(Comprobante invoice,XSSFWorkbook workbook)

  XSSFWorkbook generateWorkbookWithAllInvoices(List<Comprobante> invoices)

  XSSFWorkbook generateWorkbookWithAddendaInvoice(Comprobante invoice)

}
