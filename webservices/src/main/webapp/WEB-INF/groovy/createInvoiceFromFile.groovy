import com.makingdevs.accounting.impl.AccountManagerImpl
import com.makingdevs.accounting.AccountManager
import com.makingdevs.Comprobante
import com.makingdevs.InvoiceParser

def method = request.method
if (method.toLowerCase()=="post"){
  InvoiceParser invoiceParser =new InvoiceParser()
  File file=invoiceParser.getFileFromInputStream(request.inputStream)
  AccountManager invoiceService = new AccountManagerImpl()
  Comprobante comprobante=invoiceService.obtainVoucherFromInvoice(file)
  println "Valores:. "+comprobante.total
  response.contentType='application/json'
  json{
    comprobante
  }
}
