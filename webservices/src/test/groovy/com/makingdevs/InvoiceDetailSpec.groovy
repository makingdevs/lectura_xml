package com.makingdevs
import spock.lang.Specification
import java.lang.Void as Should
import wslite.rest.*
import com.makingdevs.*
import groovy.json.JsonSlurper

class InvoiceDetailSpec extends Specification{
  
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
  }

  Should "Verify what response to be GET with Wslite retrieve STATUS=OK"(){
    given:
      def slurperJson = new JsonSlurper()
      def client = new RESTClient("http://localhost:1234/webservices/invoiceDetail.groovy")
    when:
      def responseGET = client.get(headers:[
                                            "Content-Type":"application/octet-stream",
                                            "Accept":"application/json"
                                            ]) {}
      def statusWebService = slurperJson.parseText(responseGET.contentAsString)
    then:
      statusWebService.status=="OK"
  }

}