package com.makingdevs
import spock.lang.Specification
import java.lang.Void as Should
import wslite.rest.*
import com.makingdevs.*

class InvoiceDetailSpec extends Specification{
  //InvoiceParser invoiceDetail
  def setup (){
    //invoiceDetail = new InvoiceParser()
  }
  Should "Verify what response to be POST with Wslite retrieve JSON"(){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura.xml").getFile())
      def client = new RESTClient("http://localhost:1234/webservices/invoiceDetail.groovy")
    when:
      def responsePOST = client.post(headers:[
                                              "Content-Type":"application/octet-stream",
                                              "Accept":"application/json"
                                              ]) {
                                                  type ContentType.BINARY  
                                                  bytes invoice.bytes
                                                }
    then:
      responsePOST.contentAsString
      println responsePOST.contentAsString
  }
}