import com.makingdevs.service.impl.InvoiceServiceImpl
import com.makingdevs.Comprobante

def method = request.method
File file= new File("invoice.xml")
InputStream inputStream=request.inputStream
OutputStream outputStream = new FileOutputStream(file)
while ((byteInput = inputStream.read()) != -1){
  outputStream.write(byteInput)
}

InvoiceServiceImpl invoiceService = new InvoiceServiceImpl()
Comprobante comprobante=invoiceService.obtainVoucherFromInvoice(file)

println "Valores:. "+comprobante.total