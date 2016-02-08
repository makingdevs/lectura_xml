package com.makingdevs

class Main{
    static void main(def args){
      /*
      def concepto=new Concepto(valorUnitario:123)

      def traslado=new Traslado(impuesto:"IVA")
      def impuesto=new Impuesto(totalImpuestosTrasladado:123456)
      impuesto.traslado=traslado

      def timbreFiscalDigital=new TimbreFiscalDigital(selloSAT:"Sello SAT-XXX")
      def complemento = new Complemento()
      complemento.timbreFiscalDigital=timbreFiscalDigital

      def comprobante=new Comprobante(folio:"2016/02/04:10:00")
      def timbreFiscalDigital=new TimbreFiscalDigital(noCertificadoSAT:"1")
      timbreFiscalDigital.uuid="wrqwerewqt"
      comprobante.timbreFiscalDigital=timbreFiscalDigital
      println (comprobante.timbreFiscalDigital.uuid)
      comprobante.conceptos.add(concepto)
      
      println "Informacion de clases"
      println (traslado.impuesto)
      println (impuesto.totalImpuestosTrasladado)
      println (impuesto.traslado?.impuesto)
      println (concepto.valorUnitario)
      println (complemento.timbreFiscalDigital?.selloSAT)
      println (comprobante.folio)

      comprobante.conceptos.each{
        println(it.valorUnitario)
      }
      */
      
      def lecturaXML=new ParseXML()
      def timbreFiscalDigital=new TimbreFiscalDigital()
      def comprobante=new Comprobante()
      comprobante=lecturaXML.readFile("/Users/makingdevs/workspace/lectura_xml/1ORO9612152X9-FAOROABA000000005041.xml")
      print comprobante.timbreFiscalDigital.uuid
      //LecturaXML.getFilesXML("/Users/makingdevs/workspace/lectura_xml/")
    }
}
