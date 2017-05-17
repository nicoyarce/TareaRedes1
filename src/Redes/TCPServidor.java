package Redes;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

class TCPServidor {

    static ArrayList<Usuario> usuarios = new ArrayList<>();
    static Calendar fecha = Calendar.getInstance();

    public static void main(String argv[]) throws Exception {
        int hashUsuario;
        String clientSentence;

        ServerSocket welcomeSocket = new ServerSocket(9875);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            //para recibir datos desde el cliente
            BufferedReader recibeDelCliente = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //para enviar datos al cliente
            PrintWriter enviaAlCliente = new PrintWriter(connectionSocket.getOutputStream(), true);

            //recibe opcion de ingreso
            clientSentence = recibeDelCliente.readLine();
            int opcion = Integer.parseInt(clientSentence);

            //recibe usuario y clave
            String nombre = recibeDelCliente.readLine();
            String clave = recibeDelCliente.readLine();

            switch (opcion) {
                case 1:
                    //usuarios nuevos 
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarios.get(i).existeUsuario(nombre)) {
                            do {
                                enviaAlCliente.println("Usuario ya existente");
                            } while (usuarios.get(i).existeUsuario(nombre));
                            enviaAlCliente.println("Nombre disponible");
                            break;
                        } else {
                            enviaAlCliente.println("Nombre disponible");
                        }
                    }
                    usuarios.add(new Usuario(nombre, clave));
                    enviaAlCliente.println("Registrado correctamente");
                    enviaAlCliente.println("OK");
                    break;
                case 2:
                    //usuarios existentes
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarios.get(i).existeUsuario(nombre)) {
                            if (usuarios.get(i).claveEsCorrecta(clave)) {
                                hashUsuario = usuarios.get(i).hashCode();
                                enviaAlCliente.println("Bienvenido " + nombre);
                                enviaAlCliente.println("OK");
                                break;
                            } else {
                                enviaAlCliente.println("Error en clave");
                            }
                        } else {
                            enviaAlCliente.println("Usuario no encontrado");
                        }
                    }
                    break;
            }
            
            enviaAlCliente.println("Ingrese su opcion");
            enviaAlCliente.println("1. Consultar la hora y dia actual");
            enviaAlCliente.println("2. Consultar la hora y dia de la ultima consulta");
            enviaAlCliente.println("3. Listar los seudonimos de los usuarios registrados");
            enviaAlCliente.println("4. Guardar un mensaje para un usuario seleccionado");
            enviaAlCliente.println("5. Consultar si hay algun mensaje a su nombre");
            enviaAlCliente.println("6. Borrar registro (implica borrar seudonimo y mensajes");
            enviaAlCliente.println("7. Finalizar sesion de consulta");
            
            //recibe opcion de servicio
            clientSentence = recibeDelCliente.readLine();
            switch (Integer.parseInt(clientSentence)) {
                case 1:
                    enviaAlCliente.println("La fecha es: " + LocalDateTime.now());
                    enviaAlCliente.println("OK");
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
            enviaAlCliente.println(clientSentence);
        }
        // si se usa una condicion para quebrar el ciclo while, se deben cerrar los sockets!
        // connectionSocket.close(); 
        // welcomeSocket.close(); 
    }

}
