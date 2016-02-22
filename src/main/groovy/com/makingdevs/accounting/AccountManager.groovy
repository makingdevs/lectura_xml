package com.makingdevs.accounting

import com.makingdevs.*

class AccountManager {
  List<File> searchInvoicesInLocation(String location){
    List<File> invoices = []
    new File(location).eachFile{ file ->
      if(file.name.endsWith(".xml")) invoices.add(file)
    }
    invoices
  }

  Comprobante obtainVoucherFromInvoice(File invoice){
    new Comprobante()
  }
}
