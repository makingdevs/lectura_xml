package com.makingdevs.accounting.impl

import com.makingdevs.accounting.ReconciliationManager
import com.makingdevs.reconciliation.Factura
import com.makingdevs.reconciliation.Pago
import groovy.transform.TypeChecked
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

@Singleton
class ReconciliationManagerImpl implements ReconciliationManager {

  List<Pago> readPaymentsFromAFile(String filePath) {
    Workbook wb = WorkbookFactory.create(new File(filePath))
    Sheet sheet = wb.getSheetAt(0)
    Collection rawPayments = readTheContentFromASheet(sheet)
    rawPayments.removeAt(0)

    rawPayments.collect { operationDate, operationName, amount ->
      new Pago(
          fecha : new Date().parse("dd/MM/yyyy",operationDate),
          concepto : operationName,
          cantidad : amount ?: 0
      )
    }
  }

  @Override
  List<Factura> searchForPayedInvoices(List<Factura> facturas, List<Pago> pagos) {
    facturas.each { invoice ->
      def payment = pagos.findAll { p ->
        p.cantidad
      }.sort { a,b ->
        a.fecha <=> b.fecha
      }.find { p ->
        (invoice.monto == p.cantidad) && !p.billed
      }
      if(payment){
        invoice.payments << payment
        payment.invoices << invoice
        invoice.payed = true
        payment.billed = true
      }
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
    sheet.collect { Row row ->
      row.collect { Cell cell ->
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
