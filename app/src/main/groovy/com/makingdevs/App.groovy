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
      File excelFile = invoiceFileOperation.createInvoicesFile(path)
      println excelFile.properties
      break;
      case JFileChooser.CANCEL_OPTION:
      case JFileChooser.ERROR_OPTION:
      break;
    }
  }
}
