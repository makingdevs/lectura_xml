import com.makingdevs.accounting.impl.AccountManagerImpl
import com.makingdevs.accounting.AccountManager
import com.makingdevs.Comprobante
import com.makingdevs.InvoiceParser
import javax.servlet.http.HttpServletResponse

import org.slf4j.Logger
import org.slf4j.LoggerFactory


Logger log = LoggerFactory.getLogger(getClass())

log.debug "*"*80
log.debug request.properties.toString()
log.error "Errorz"
log.warn "Warning"

def method = request.method

try{

  def contentType = headers.find{ k,v -> k.toLowerCase() == 'content-type' }?.value

  if(contentType != "application/octet-stream")
    throw new RuntimeException("Please use 'application/octet-stream' header")

  if(method.toLowerCase()=="post"){
    InvoiceParser invoiceParser =new InvoiceParser()
    File file=invoiceParser.getFileFromInputStream(request.inputStream)
    AccountManager invoiceService = new AccountManagerImpl()
    Comprobante invoice=invoiceService.obtainVoucherFromInvoice(file)
    response.contentType = 'application/json'
    json(invoice)
  }
  if(method.toLowerCase()=="get"){
    json([status:"OK"])
  }

}
catch(RuntimeException e){
  response.contentType='application/json'
  response.setStatus(HttpServletResponse.SC_BAD_REQUEST,e.message)
}
catch(Exception e){
  response.contentType='application/json'
  response.setStatus(HttpServletResponse.SC_NOT_FOUND,e.message)
}
