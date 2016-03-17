package com.makingdevs

class InvoiceParser{
  File getFileFromInputStream(InputStream inputStream){
    File file= new File("invoice.xml")
    OutputStream outputStream = new FileOutputStream(file)
    Integer byteInput
    
    while ((byteInput = inputStream.read()) != -1){
      outputStream.write(byteInput)
    }
    file
  }
}