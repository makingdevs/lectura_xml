package com.makingdevs

import javax.swing.*;
import javax.swing.filechooser.*
import groovy.swing.SwingBuilder
import com.makingdevs.accounting.impl.*
import com.makingdevs.accounting.*

class App {

  static InvoiceFileOperation invoiceFileOperation = new InvoiceFileOperationImpl()

  static void main(String[] args){
    def initialPath = System.getProperty("user.dir");
    JFileChooser fc = new JFileChooser(initialPath);
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int result = fc.showOpenDialog( null );
    switch ( result )
    {
      case JFileChooser.APPROVE_OPTION:
      File file = fc.getSelectedFile();
      def path =  fc.getCurrentDirectory().getAbsolutePath();
      println "-"*100
      println "$path"
      println "$file.properties"
      println "-"*100
      File excelFile = invoiceFileOperation.createInvoicesFile(file.canonicalPath)
      excelFile.renameTo(new File(dir, excelFile.getName()))
      println excelFile.properties
      break;
      case JFileChooser.CANCEL_OPTION:
      case JFileChooser.ERROR_OPTION:
      break;
    }
  }
}
