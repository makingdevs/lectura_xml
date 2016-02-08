package com.makingdevs
import spock.lang.Specification 
class ParseXMLServiceSpec extends Specification{
	private CalculatorService calculatorService
	def setup(){
		calculatorService=new CalculatorService()
	}
	def "Add two numbers"(){
		given:
			Integer a=2
			Integer b=3
		when:
			def sum=calculatorService.sum(a,b)

		then:
			sum==5
	}

}