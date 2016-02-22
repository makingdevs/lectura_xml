package com.makingdevs.accounting

class AccountManager {
  List<File> searchInvoicesInLocation(String location){
    List<File> invoices = []
    new File(location).eachFile{ file ->
      if(file.name.endsWith(".xml")) invoices.add(file)
    }
    invoices
  }
}
