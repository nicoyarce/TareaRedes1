package Redes;

import java.io.*;
import java.net.*;

class TCPCliente {

    public static void main(String argv[]) throws Exception {
        String sentence;
        String echoSentence;
        
        //fijar entrada por teclado
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("127.0.0.1", 9875);        
        //enviar datos al servidor
        PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        //recibir datos desde el servidor
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        System.out.println("Ingrese su nombre de usuario");
        //oracion escrita por teclado es asignada a un string
        sentence = inFromUser.readLine();
        //se envia la oracion al servidor
        outToServer.println(sentence);
        
        System.out.println("Ingrese su contraseña");
        sentence = inFromUser.readLine(); 
        outToServer.println(sentence);
        
        System.out.println("Ingrese su opcion");
        System.out.println("1.Consultar la hora y dia actual");
        System.out.println("2.Consultar la hora y dia de la ultima consulta");
        System.out.println("3.Listar los seudonimos de los usuarios registrados");
        System.out.println("4.Guardar un mensaje para un usuario seleccionado");
        System.out.println("5.Consultar si hay algun mensaje a su nombre");
        System.out.println("6.Borrar registro (implica borrar seudonimo y mensajes");
        System.out.println("7.Finalizar sesion de consulta");
        sentence = inFromUser.readLine();         
        outToServer.println(sentence);
        
        
        //recibe oracion desde el servidor
        echoSentence = inFromServer.readLine();
        System.out.println("DEL SERVIDOR: " + echoSentence);

        clientSocket.close();
    }
    
}
