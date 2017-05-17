package Redes;

import java.io.*;
import java.net.*;

class TCPCliente {

    public static void main(String argv[]) throws Exception {
        String sentence;
        String echoSentence;

        //fijar entrada por teclado
        BufferedReader entradaDelUsuario = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("127.0.0.1", 9875);
        //enviar datos al servidor
        PrintWriter alServidor = new PrintWriter(clientSocket.getOutputStream(), true);
        //recibir datos desde el servidor
        BufferedReader entradaDelServidor = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Ingrese 1 si es usuario nuevo");
        System.out.println("Ingrese 2 si es usuario existente");
        //oracion escrita por teclado es asignada a un string        
        sentence = entradaDelUsuario.readLine();
        //se envia la oracion al servidor
        alServidor.println(sentence);

        System.out.println("Ingrese su nombre de usuario");
        sentence = entradaDelUsuario.readLine();
        alServidor.println(sentence);

        System.out.println("Ingrese su contraseña");
        sentence = entradaDelUsuario.readLine();
        alServidor.println(sentence);

        echoSentence = entradaDelServidor.readLine();
        while (!echoSentence.equals("OK")) {
            //recibe oracion desde el servidor
            echoSentence = entradaDelServidor.readLine();
            System.out.println("El servidor dice: " + echoSentence);
        }

        sentence = entradaDelUsuario.readLine();
        alServidor.println(sentence);

        if (sentence.equals("7")) {
            clientSocket.close();
            System.exit(0);
        }

        echoSentence = entradaDelServidor.readLine();
        while (!echoSentence.equals("OK")) {
            //recibe oracion desde el servidor
            echoSentence = entradaDelServidor.readLine();
            System.out.println("El servidor dice: " + echoSentence);
        }

        clientSocket.close();
        System.exit(0);
    }
}
