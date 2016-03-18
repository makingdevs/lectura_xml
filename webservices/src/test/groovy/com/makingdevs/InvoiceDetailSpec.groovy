package com.makingdevs

import spock.lang.Specification
import spock.lang.Shared
import java.lang.Void as Should
import wslite.rest.*
import groovy.json.JsonSlurper
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.*
import groovy.servlet.*

import static org.eclipse.jetty.servlet.ServletContextHandler.*

class InvoiceDetailSpec extends Specification{

  @Shared server = new Server(1234)

  def setup() {
    def context = new ServletContextHandler(server, "/", SESSIONS)
    context.resourceBase = "${new File(".").canonicalPath}/src/main/webapp/WEB-INF/groovy"
    context.setContextPath("/")
    context.addServlet(GroovyServlet, "*.groovy")
    server.start()
    println "*"*80
    println server.dump()
    println "*"*80
  }

  def cleanup(){
    server.stop()
  }

  Should "Verify what response to be POST with Wslite retrieve JSON"(){
    given:
      File invoice = new File(this.class.classLoader.getResource("factura.xml").getFile())
      def client = new RESTClient("http://localhost:1234/InvoiceDetail.groovy")
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
      def client = new RESTClient("http://localhost:1234/InvoiceDetail.groovy")
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
