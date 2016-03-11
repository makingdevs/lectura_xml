import com.makingdevs.accounting.impl.AccountManagerImpl
import com.makingdevs.accounting.AccountManager
import com.makingdevs.Comprobante
import com.makingdevs.InvoiceParser

def method = request.method
if (method.toLowerCase()=="post"){
  InvoiceParser invoiceParser =new InvoiceParser()
  File file=invoiceParser.getFileFromInputStream(request.inputStream)
  AccountManager invoiceService = new AccountManagerImpl()
  Comprobante invoice=invoiceService.obtainVoucherFromInvoice(file)

  response.contentType='application/json'
  json(invoice)
}
