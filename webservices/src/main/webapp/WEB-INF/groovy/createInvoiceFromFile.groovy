import com.makingdevs.accounting.impl.AccountManagerImpl
import com.makingdevs.accounting.AccountManager
import com.makingdevs.Comprobante

def method = request.method
File file= new File("invoice.xml")
InputStream inputStream=request.inputStream
OutputStream outputStream = new FileOutputStream(file)
while ((byteInput = inputStream.read()) != -1){
  outputStream.write(byteInput)
}

AccountManager invoiceService = new AccountManagerImpl()
Comprobante comprobante=invoiceService.obtainVoucherFromInvoice(file)

println "Valores:. "+comprobante.total