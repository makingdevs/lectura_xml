package com.makingdevs

class Main{
    static void main(def args){
      def concepto=new Concepto(valorUnitario:123)

      def traslado=new Traslado(impuesto:"IVA")
      def impuesto=new Impuesto(totalImpuestosTrasladado:123456)
      impuesto.traslado=traslado

      def timbreFiscalDigital=new TimbreFiscalDigital(selloSAT:"Sello SAT-XXX")
      def complemento = new Complemento()
      complemento.timbreFiscalDigital=timbreFiscalDigital

      def comprobante=new Comprobante(folio:"2016/02/04:10:00")
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
    }
}
