package com.makingdevs

import com.makingdevs.accounting.AccountManager

class ReadInvoiceFromXmlTest extends GroovyTestCase {

  void testWhenAccountManagerIsSearchingInvoices(){
    AccountManager manager = new AccountManager()
    String location = "${System.getProperty('user.home')}/Documents/invoices"
    def files = manager.searchInvoicesInLocation(location)
    assert files
    assert files.size() == 59
    assert files.every { f -> f.absolutePath.split('\\.').last() == 'xml' }
  }
}
