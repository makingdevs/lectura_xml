import groovy.xml.XmlUtil
def readFile(String path){
  def file= new File(path).getText() 
  def xml = new XmlSlurper().parseText(file).declareNamespace(
    cfdi:"http://www.sat.gob.mx/cfd/3",
    xsi:"http://www.w3.org/2001/XMLSchema-instance")
    
  xml.children().each{
    println ("Informacion: "+it.name()+it.children()*.name()+"\n")
    if (it!=null) {
      it.children().each{detalle->
          println  detalle*.attributes()
      }   
    }
  }
}
