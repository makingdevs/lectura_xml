package com.makingdevs

import javax.swing.*
import javax.swing.filechooser.*
import groovy.swing.SwingBuilder
import com.makingdevs.accounting.impl.*
import com.makingdevs.accounting.*

class App {

  static InvoiceFileOperation invoiceFileOperation = new InvoiceFileOperationImpl()

  static void main(String[] args){
    def initialPath = System.getProperty("user.dir")
    JFileChooser fc = new JFileChooser(initialPath)
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos XML", "xml")
    fc.setFileFilter(filter)
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY)
    int result = fc.showOpenDialog( null )
    switch ( result ) {
      case JFileChooser.APPROVE_OPTION:
        File file = fc.selectedFile
        def path =  fc.currentDirectory.absolutePath
        File excelFile = invoiceFileOperation.createInvoiceWithAddendaFile(file)
        excelFile.renameTo(new File("${file.canonicalPath}.xlsx"))
      break
      case JFileChooser.CANCEL_OPTION:
      case JFileChooser.ERROR_OPTION:
      break
    }
  }
}
