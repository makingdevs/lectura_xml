package com.makingdevs.reconciliation

class Pago {
  Date fecha
  String concepto
  Float cantidad
  Boolean billed = false
  List<Factura> invoices = []

  String getDescripcion(){
    "${fecha.format('dd/MM/yy')} ${cantidad} ${concepto}"
  }
}
