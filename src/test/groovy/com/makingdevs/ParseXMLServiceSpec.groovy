package com.makingdevs
import spock.lang.Specification 
class ParseXMLServiceSpec extends Specification{
  private ParseXML parseXML
  def setup(){
    parseXML=new ParseXML()
  }
  def "Read files XML"(){
    given:
      List<String> files=[]
    when:
      files=parseXML.getFilesXML("/Users/makingdevs/workspace/lectura_xml/")
    then:
      files.size>0
  }
  
  @Ignore
  def "Get a object Comprobante"(){
    given:
      Comprobante comprobante=new Comprobante()
    when:
      files=parseXML.getFilesXML("/Users/makingdevs/workspace/lectura_xml/")
      
    then:
      true
  }
}