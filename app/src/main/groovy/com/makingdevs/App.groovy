package com.makingdevs

import javax.swing.*
import javax.swing.filechooser.*
import groovy.swing.SwingBuilder
import com.makingdevs.accounting.impl.*
import com.makingdevs.accounting.*
import javax.swing.JOptionPane;

class App {

  static InvoiceFileOperation invoiceFileOperation = new InvoiceFileOperationImpl()

  static void main(String[] args){
    def initialPath = System.getProperty("user.dir")
    JFileChooser fc = new JFileChooser(initialPath)
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
    int result = fc.showOpenDialog( null )
    switch ( result ) {
      case JFileChooser.APPROVE_OPTION:
        File file = fc.selectedFile
        def path =  fc.currentDirectory.absolutePath
        File excelFile = invoiceFileOperation.createInvoicesFile(file.canonicalPath)
        excelFile.renameTo(new File(file.canonicalPath, excelFile.name))
      break
      case JFileChooser.CANCEL_OPTION:
      case JFileChooser.ERROR_OPTION:
      break
    }
    JOptionPane.showMessageDialog(null, "Su factura ha sido procesada", "Facturas", JOptionPane.INFORMATION_MESSAGE);
  }
}
