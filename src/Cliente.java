
import java.io.*;
import java.net.*;

class Cliente {

    public static void main(String argv[]) throws Exception {
        String oracionTeclado;
        String echoSentence;

        //fijar entrada por teclado
        BufferedReader entradaDelUsuario = new BufferedReader(new InputStreamReader(System.in));
        //enviar datos al servidor
        Socket clientSocket = new Socket("127.0.0.1", 7777);
        //enviar datos al servidor
        PrintWriter alServidor = new PrintWriter(clientSocket.getOutputStream(), true);
        //recibir datos desde el servidor
        BufferedReader entradaDelServidor = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //validacion por tipo de usuario
        do {
            System.out.println("Ingrese 1 si es usuario nuevo.");
            System.out.println("Ingrese 2 si es usuario existente.");
            oracionTeclado = entradaDelUsuario.readLine();
            if (!oracionTeclado.equals("1") && !oracionTeclado.equals("2")) {
                System.out.println("Ingrese opcion valida.");
            }
        } while (!oracionTeclado.equals("1") && !oracionTeclado.equals("2"));
        alServidor.println(oracionTeclado);

        System.out.println("Ingrese su nombre de usuario.");
        oracionTeclado = entradaDelUsuario.readLine();
        alServidor.println(oracionTeclado);

        System.out.println("Ingrese su contraseña.");
        oracionTeclado = entradaDelUsuario.readLine();
        alServidor.println(oracionTeclado);

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
                oracionTeclado = entradaDelUsuario.readLine();
                alServidor.println(oracionTeclado);
            }
            if (!echoSentence.equals("?")) {
                System.out.println("servidor> " + echoSentence);
            }
            echoSentence = entradaDelServidor.readLine();
        }
        /*se utiliza la palabra clave OK para saber cuando el servidor dejara
        de enviar datos o la palabra BYE para revisar si hara mas consultas,
        asi como el signo ? cuando se pide una entrada de datos para interrumpir
        la recepcion de estos mismos*/
        while (!echoSentence.equals("BYE")) {
            //lee tipo de consulta
            int opcion = 0;
            do {
                oracionTeclado = entradaDelUsuario.readLine();
                try {
                    opcion = Integer.parseInt(oracionTeclado);
                    if (opcion <= 0 || opcion >= 8) {
                        System.out.println("Ingrese del 1 al 7");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ingrese del 1 al 7");
                }
            } while (opcion <= 0 || opcion >= 8);

            alServidor.println(oracionTeclado);

            //recibe respuestas respecto consultas
            echoSentence = entradaDelServidor.readLine();
            while (!echoSentence.equals("OK")) {
                if (echoSentence.equals("?")) {
                    do {
                        oracionTeclado = entradaDelUsuario.readLine();
                        if (oracionTeclado.length() > 30) {
                            System.out.println("Ingrese menos de 30 caracteres");
                        }
                    } while (oracionTeclado.length() > 30);
                    alServidor.println(oracionTeclado);
                }
                if (!echoSentence.equals("?") && !echoSentence.equals("BYE")) {
                    System.out.println("servidor> " + echoSentence);
                }
                if (echoSentence.equals("BYE")) {
                    System.out.println("Conexion terminada");
                    clientSocket.close();
                    System.exit(0);
                }
                echoSentence = entradaDelServidor.readLine();
            }
        }
        System.out.println("Conexion terminada");
        clientSocket.close();
        System.exit(0);
    }
}
