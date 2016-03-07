package com.makingdevs.service

import org.apache.poi.xssf.usermodel.*
import com.makingdevs.Comprobante

interface ExcelService{

  XSSFWorkbook generateFileExcel()

  XSSFWorkbook generateFileExcelWithAllInvoices(List<Comprobante> invoices)

  XSSFWorkbook generateFileExcelWithAddendaInvoice(Comprobante invoice)

  XSSFWorkbook generateFileExcelWithDetailInvoice(Comprobante invoice)

}
