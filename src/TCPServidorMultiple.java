//package redes;

import java.io.*; 
import java.net.*; 
import java.util.Vector;

class TCPServidorMultiple extends Thread { 

  public Vector<BufferedReader> readers;
  public Vector<Socket> clientes;
  public Socket connectionSocket;
  public  Vector<PrintWriter> writers;
  private boolean ready;  
  
  public TCPServidorMultiple(){
      ready = false;
      clientes = new Vector<Socket>();
      readers = new Vector<BufferedReader>();
      writers = new Vector<PrintWriter>();
  }
  
  public void add(Socket connectionSocket) throws IOException{
	    ready = false;
	    clientes.add(connectionSocket);
      	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
		readers.add(inFromClient);
		writers.add(outToClient);
		ready = true;
  }
  
  public void run(){
	  
	  String clientSentence; 
	  while(clientes!=null)
	  {
		
		  for(int i =0; i < clientes.size(); ++i)
		  {
			  if(ready) // una variable para controlar concurrencia. Mejor usar locks si multiples hebras usan add(), lo que no es el caso
			  {
			  BufferedReader reader = readers.get(i);
			  PrintWriter writer = writers.get(i);
			  
			try {
				 if(reader.ready()) // revisa si hay datos nuevos en el buffer de lectura del socket i-emo
				 {
				   clientSentence = reader.readLine(); 
				   System.out.println("Datos recibidos:"+clientSentence+" desde "+clientes.get(i).getInetAddress().getHostAddress());
                   		   writer.println(clientSentence); 
                   		   writer.flush();
				 }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			} //if
		  }//for
	  }//while
	  
  }
	
	
  public static void main(String argv[]) throws Exception 
    { 
      
      ServerSocket welcomeSocket = new ServerSocket(9876);
      
      boolean run = true;
      TCPServidorMultiple server = new TCPServidorMultiple();
      server.start(); // hebra que atiende conexiones aceptadas     
      
      while(run) {  // hebra principal escucha intentos de conexion
    	  Socket connectionSocket = welcomeSocket.accept(); 
    	  System.out.println("Agregando nuevo cliente");
    	  server.add(connectionSocket);
	 }
      welcomeSocket.close();
     }
  } 

