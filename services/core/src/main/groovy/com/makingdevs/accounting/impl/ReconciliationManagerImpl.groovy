package com.makingdevs.accounting.impl

import com.makingdevs.accounting.ReconciliationManager
import com.makingdevs.reconciliation.Factura
import com.makingdevs.reconciliation.Pago
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

@Singleton
class ReconciliationManagerImpl implements ReconciliationManager {

  List<Pago> readPaymentsFromAFile(String filePath) {
    Workbook wb = WorkbookFactory.create(new File(filePath))
    Sheet sheet = wb.getSheetAt(0)
    def rawPayments = readTheContentFromASheet(sheet)
    rawPayments.removeAt(0)

    rawPayments.collect { operationDate, operationName, amount ->
      new Pago(
          fecha : new Date().parse("dd/MM/yyyy",operationDate),
          concepto : operationName,
          cantidad : amount ?: 0
      )
    }
  }

  List<Factura> readInvoicesFromAFile(String filePath) {
    Workbook wb2 = WorkbookFactory.create(new File(filePath))
    Sheet sheet2 = wb2.getSheetAt(0)
    def rawInvoices = readTheContentFromASheet(sheet2)
    rawInvoices.removeAt(0)

    rawInvoices.collect { fecha, folio, total, emisor, rfc, ctaPago, concepto, metodoDePago ->
      new Factura(
          fecha : new Date().parse("dd-MM-yyyy hh:mm",fecha),
          folio: folio,
          monto : total,
          emisor : emisor,
          rfc : rfc,
          cuentaPago : ctaPago,
          concepto : concepto,
          metodoDePago : metodoDePago
      )
    }
  }

  private def readTheContentFromASheet(Sheet sheet){
    sheet.collect { row ->
      row.collect { cell ->
        switch(cell.cellTypeEnum) {
          case CellType.NUMERIC:
            cell.numericCellValue
            break
          case CellType.STRING:
            cell.stringCellValue
            break
          default:
            null
        }
      }
    }
  }
}
