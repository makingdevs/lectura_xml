package com.makingdevs
import spock.lang.Specification
import java.lang.Void as Should
import wslite.rest.*
import com.makingdevs.*
import groovy.json.JsonSlurper
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.*
import groovy.servlet.*

//import static org.eclipse.jetty.servlet.ServletContextHandler.*

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
      def server = new Server(1234)
      def context = new ServletContextHandler(server, "/", SESSIONS)
      context.resourceBase = "."
      context.addServlet(GroovyServlet, "*.groovy")
      server.start()
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