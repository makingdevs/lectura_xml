package com.makingdevs
class Comprobante{
	
	String serie
	String folio
	Date fecha
	String formaDePago
	BigDecimal subTotal
	BigDecimal descuento
	Integer tipoCambio
	String lugarExpedicion
	String numCtaPago
	String noCertificado
	String certificado
	String sello
	Emisor emisor
	Receptor receptor 
	List<Concepto> conceptos=[] 
	Impuesto impuesto
	TimbreFiscalDigital timbreFiscalDigital
	//Complemento complemento
}