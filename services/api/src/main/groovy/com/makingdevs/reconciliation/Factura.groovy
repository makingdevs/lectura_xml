package com.makingdevs.reconciliation

import groovy.transform.ToString

@ToString(includeNames=true, includeFields=true, excludes='concepto,metodoDePago,cuentaPago')
class Factura {
  Date fecha
  String folio
  Float monto
  String emisor
  String rfc
  String cuentaPago
  String concepto
  String metodoDePago
  Boolean payed = false
  List<Pago> payments = []
}
