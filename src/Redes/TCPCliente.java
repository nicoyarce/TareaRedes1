package Redes;

import java.io.*;
import java.net.*;

class TCPCliente {

    public static void main(String argv[]) throws Exception {
        String oracion;
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
        oracion = entradaDelUsuario.readLine();
        alServidor.println(oracion);

        System.out.println("Ingrese su nombre de usuario");
        oracion = entradaDelUsuario.readLine();
        alServidor.println(oracion);

        System.out.println("Ingrese su contraseña");
        oracion = entradaDelUsuario.readLine();
        alServidor.println(oracion);

        //recibe mensajes del servidor
        echoSentence = entradaDelServidor.readLine();
        while (echoSentence.endsWith(".")) {
            echoSentence = entradaDelServidor.readLine();
            if (!echoSentence.equals("")) {
                System.out.println("servidor> " + echoSentence);
            }
            if (echoSentence.equals("Error en clave.") || echoSentence.equals("Usuario no encontrado.")) {
                System.exit(0);
            }
        }

        //lee tipo de consulta
        System.out.println("Ingrese su opcion.");
        oracion = entradaDelUsuario.readLine();
        alServidor.println(oracion);

        //recibe informacion de la consulta
        echoSentence = entradaDelServidor.readLine();
        while (!echoSentence.equals("OK")) {
            System.out.println("servidor> " + echoSentence);
            echoSentence = entradaDelServidor.readLine();
        }

        clientSocket.close();
        System.exit(0);
    }
}
