package Redes;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

class TCPServidor {
    ArrayList<Usuario> usuarios = new ArrayList<>();
    
    public static void main(String argv[]) throws Exception {
        String usuario, clientSentence;
        
        ServerSocket welcomeSocket = new ServerSocket(9875);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            //para recibir datos desde el cliente
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //para enviar datos al cliente
            PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
            
            //recibe opcion de ingreso
            clientSentence = inFromClient.readLine();
            int opcion = Integer.parseInt(clientSentence);
            
            //recibe usuario
            clientSentence = inFromClient.readLine();
            
            switch (opcion) {
                case 1:
                    
                    break;
                case 2:
                    
                    break;
            }
            
            //recibe opcion de servicio
            clientSentence = inFromClient.readLine();
            switch (Integer.parseInt(clientSentence)) {
                case 1:
                    
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                default:
                    break;
            }
            
            //linea por consola indicando quien envio que mensaje
            System.out.println(connectionSocket.getInetAddress() + " message:" + clientSentence);
            //envia una respuesta al cliente
            outToClient.println(clientSentence);
        }
        // si se usa una condicion para quebrar el ciclo while, se deben cerrar los sockets!
        // connectionSocket.close(); 
        // welcomeSocket.close(); 
    }

    

}
