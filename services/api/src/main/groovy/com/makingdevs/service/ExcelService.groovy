package com.makingdevs.service

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import com.makingdevs.Comprobante

interface ExcelService{

  XSSFWorkbook generateExcelWorkbookWithInvoiceDetail(Comprobante invoice)

  XSSFWorkbook generateFileExcelWithAllInvoices(List<Comprobante> invoices)

  XSSFWorkbook generateFileExcelWithDetailInvoice(Comprobante invoice)

}
