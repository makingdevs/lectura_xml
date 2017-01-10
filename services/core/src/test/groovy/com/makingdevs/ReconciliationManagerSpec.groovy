package com.makingdevs

import com.makingdevs.accounting.ReconciliationManager
import com.makingdevs.accounting.impl.ReconciliationManagerImpl
import spock.lang.Specification
import java.lang.Void as Should

class ReconciliationManagerSpec extends Specification {

  ReconciliationManager manager

  def setup(){
    manager = ReconciliationManagerImpl.instance
  }

  Should "reads an invoice file"(){
    given: "a file"
    String path = "./src/test/resources/facturas.xlsx"
    when: "read the file"
    def invoices = manager.readInvoicesFromAFile(path)
    then: "should get some invoices"
    invoices
  }

  Should "reads a payments file"(){
    given: "a file"
    String path = "./src/test/resources/pagos.xlsx"
    when: "read the file"
    def payments = manager.readPaymentsFromAFile(path)
    then: "should get some invoices"
    payments
  }

}
