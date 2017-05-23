package Redes;

import java.io.*;
import java.net.*;

class Cliente {

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

        //validacion por tipo de usuario    
        do {
            System.out.println("Ingrese 1 si es usuario nuevo.");
            System.out.println("Ingrese 2 si es usuario existente.");
            oracion = entradaDelUsuario.readLine();
            if (!oracion.equals("1") && !oracion.equals("2")) {
                System.out.println("Ingrese opcion valida.");
            }
        } while (!oracion.equals("1") && !oracion.equals("2"));
        alServidor.println(oracion);

        System.out.println("Ingrese su nombre de usuario.");
        oracion = entradaDelUsuario.readLine();
        alServidor.println(oracion);

        System.out.println("Ingrese su contraseña.");
        oracion = entradaDelUsuario.readLine();
        alServidor.println(oracion);

        //recibe mensajes del servidor respecto al login       
        echoSentence = entradaDelServidor.readLine();
        while (!echoSentence.equals("OK")) {
            if (echoSentence.equals("Error en clave.")
                    || echoSentence.equals("Usuario no encontrado.")
                    || echoSentence.equals("No hay usuarios registrados.")) {
                System.out.println(echoSentence);
                clientSocket.close();
                System.exit(0);
            }
            if (echoSentence.equals("?")) {
                oracion = entradaDelUsuario.readLine();
                alServidor.println(oracion);
            }
            if (!echoSentence.equals("?")) {
                System.out.println("servidor> " + echoSentence);
            }
            echoSentence = entradaDelServidor.readLine();
        }

        //lee tipo de consulta
        oracion = entradaDelUsuario.readLine();
        alServidor.println(oracion);

        //recibe mensajes respecto consultas
        echoSentence = entradaDelServidor.readLine();
        while (!echoSentence.equals("OK")) {
            if (echoSentence.equals("?")) {
                oracion = entradaDelUsuario.readLine();
                alServidor.println(oracion);
            }
            if (!echoSentence.equals("?")) {
                System.out.println("servidor> " + echoSentence);
            }
            echoSentence = entradaDelServidor.readLine();
        }

        clientSocket.close();
        System.exit(0);
    }
}