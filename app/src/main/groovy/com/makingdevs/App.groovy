package com.makingdevs

import org.fusesource.jansi.AnsiConsole
import static org.fusesource.jansi.Ansi.*
import static org.fusesource.jansi.Ansi.Color.*

class App {
  static void main(String[] args){
    AnsiConsole.systemInstall()
    println "${ansi().eraseScreen().render('@|red Hello|@ @|green World|@')}"
    println "What is your name?"
    println "Your name is ${System.in.newReader().readLine()}"
  }
}

