def method = request.method

if(method.toLowerCase() == 'post'){
  
}
else if(method.toLowerCase() == 'get'){
  
}

html.html{
  head {
    title 'WebService'
  }  
  body {
    h3   'Bienvenido'
    def fecha = new Date()
    p fecha.format("dd MMMM yyyy HH:mm:ss")
    String msn="Hola!"
    p msn
    p method
  }
}

