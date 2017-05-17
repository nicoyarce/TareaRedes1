package Redes;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

class TCPServidor {

    static ArrayList<Usuario> usuarios = new ArrayList<>();
    static Calendar fecha = Calendar.getInstance();

    public static void main(String argv[]) throws Exception {
        int indiceUsuario = 0;
        String oracionCliente;

        ServerSocket welcomeSocket = new ServerSocket(9875);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            //para recibir datos desde el cliente
            BufferedReader recibeDelCliente = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //para enviar datos al cliente
            PrintWriter enviaAlCliente = new PrintWriter(connectionSocket.getOutputStream(), true);

            //recibe opcion de ingreso
            oracionCliente = recibeDelCliente.readLine();
            int opcion = Integer.parseInt(oracionCliente);

            //recibe usuario y clave
            String nombre = recibeDelCliente.readLine();
            String clave = recibeDelCliente.readLine();

            switch (opcion) {
                case 1:
                    //usuarios nuevos 
                    for (int i = 0; i < usuarios.size(); i++) {
                        while(usuarios.get(i).existeUsuario(nombre)){
                            enviaAlCliente.println("Ingrese otro nombre de usuario.");
                            nombre = recibeDelCliente.readLine();
                            if(!usuarios.get(i).existeUsuario(nombre))
                                break;
                        }
                        enviaAlCliente.println("Nombre disponible.");
                    }
                    usuarios.add(new Usuario(nombre, clave, LocalDateTime.now()));
                    indiceUsuario = usuarios.size() - 1;
                    enviaAlCliente.println("Bienvenido " + nombre + ".");
                    break;
                case 2:
                    //usuarios existentes
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarios.get(i).existeUsuario(nombre)) {
                            if (usuarios.get(i).claveEsCorrecta(clave)) {
                                indiceUsuario = i;
                                enviaAlCliente.println("Bienvenido " + nombre + ".");
                                break;
                            } else {
                                enviaAlCliente.println("Error en clave.");
                            }
                        } else {
                            enviaAlCliente.println("Usuario no encontrado.");
                        }
                    }
                    break;
                default:
                    enviaAlCliente.println("Opcion invalida.");
                    break;                    
            }

            enviaAlCliente.println("Ingrese su opcion.");
            enviaAlCliente.println("1. Consultar la hora y dia actual.");
            enviaAlCliente.println("2. Consultar la hora y dia de la ultima consulta.");
            enviaAlCliente.println("3. Listar los seudonimos de los usuarios registrados.");
            enviaAlCliente.println("4. Guardar un mensaje para un usuario seleccionado.");
            enviaAlCliente.println("5. Consultar si hay algun mensaje a su nombre.");
            enviaAlCliente.println("6. Borrar registro (implica borrar seudonimo y mensajes.");
            enviaAlCliente.println("7. Finalizar sesion de consulta.");
            enviaAlCliente.println("");

            //recibe opcion de servicio
            oracionCliente = recibeDelCliente.readLine();
            opcion = Integer.parseInt(oracionCliente);
            switch (opcion) {
                case 1:
                    enviaAlCliente.println("La fecha es: " + obtenerFecha() + " y son las " + obtenerHora());
                    enviaAlCliente.println("OK");
                    break;
                case 2:
                    LocalDateTime ultimaConsulta = usuarios.get(indiceUsuario).getUltimaConsulta();
                    formatearFechaConsulta(ultimaConsulta);
                    enviaAlCliente.println(formatearFechaConsulta(ultimaConsulta));
                    enviaAlCliente.println("OK");
                    break;
                case 3:
                    enviaAlCliente.println("Los usuarios registrados son:");
                    for (int i = 0; i < usuarios.size(); i++) {
                        String temp = usuarios.get(i).getNombre();
                        enviaAlCliente.println(temp);
                    }
                    enviaAlCliente.println("OK");
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    usuarios.remove(indiceUsuario);
                    enviaAlCliente.println("Ha sido eliminado correctamente.");
                    enviaAlCliente.println("OK");
                    break;
                case 7:
                    enviaAlCliente.println("Adios " + usuarios.get(indiceUsuario).getNombre() + ".");
                    enviaAlCliente.println("OK");
                    break;
                default:
                    enviaAlCliente.println("Ingrese una opcion valida.");
                    enviaAlCliente.println("OK");
                    break;
            }
            //captura momento de consulta
            if (opcion != 6) {
                capturarConsulta(indiceUsuario);
            }
            //linea por consola indicando quien envio que mensaje
            System.out.println(connectionSocket.getInetAddress() + " solicito una consulta tipo " + oracionCliente);
        }
        // si se usa una condicion para quebrar el ciclo while, se deben cerrar los sockets!
        // connectionSocket.close(); 
        // welcomeSocket.close(); 
    }

    private static String obtenerHora() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String horaFormateada = LocalDateTime.now().format(formato);
        return horaFormateada;
    }

    private static String obtenerFecha() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateada = LocalDateTime.now().format(formato);
        return fechaFormateada;
    }

    private static void capturarConsulta(int indice) {
        usuarios.get(indice).setUltimaConsulta(LocalDateTime.now());
    }

    private static String formatearFechaConsulta(LocalDateTime fechaConsulta) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fecha = fechaConsulta.format(formato);
        formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String hora = fechaConsulta.format(formato);
        String fechaLista = String.join(" ", "La ultima consulta fue el", fecha, "a las", hora);
        return fechaLista;
    }

}
