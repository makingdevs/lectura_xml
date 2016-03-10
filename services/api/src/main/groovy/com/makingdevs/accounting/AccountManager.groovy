package com.makingdevs.accounting
import com.makingdevs.Comprobante
import com.makingdevs.Emisor
import com.makingdevs.Receptor
import com.makingdevs.Concepto
import com.makingdevs.Impuesto
import com.makingdevs.TimbreFiscalDigital
import com.makingdevs.Addenda

interface AccountManager{

  Comprobante obtainVoucherFromInvoice(File invoice)

  Emisor obtainTransmitterFromInvoice(File invoice)

  Receptor obtainReceiverFromInvoice(File invoice)

  List<Concepto> obtainConceptsFromInvoice(File invoice)

  Impuesto obtainTaxesFromInvoice(File invoice)

  TimbreFiscalDigital obtainDigitalTaxStampFromInvoice(File invoice)

  Addenda obtainAddendaFromInvoice(File invoice)
}
