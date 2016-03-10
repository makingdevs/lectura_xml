def method = request.method
InputStream inputStream=request.inputStream
OutputStream outputStream = new FileOutputStream("woorbook.xml")
while ((byteInput = inputStream.read()) != -1){
  outputStream.write(byteInput)
}