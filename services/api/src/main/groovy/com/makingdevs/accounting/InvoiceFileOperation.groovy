package com.makingdevs.accounting

interface InvoiceFileOperation{

  File createInvoicesFile(String path)
  File createInvoiceCompleteDetailFile(String filePath)

}
