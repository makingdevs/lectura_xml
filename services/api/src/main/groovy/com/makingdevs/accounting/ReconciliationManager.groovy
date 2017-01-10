package com.makingdevs.accounting

import com.makingdevs.reconciliation.Factura
import com.makingdevs.reconciliation.Pago

/**
 * Created by makingdevs on 1/10/17.
 */
interface ReconciliationManager {

  List<Factura> readInvoicesFromAFile(String filePath)
  List<Pago> readPaymentsFromAFile(String filePath)

}