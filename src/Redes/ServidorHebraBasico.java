package Redes;

import static Redes.Servidor.usuarios;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

class ServidorHebraBasico extends Thread {

    BufferedReader recibeDelCliente;
    PrintWriter enviaAlCliente;
    Socket connectionSocket;
    static ArrayList<Usuario> usuarios = new ArrayList<>();
    static Calendar fecha = Calendar.getInstance();

    public ServidorHebraBasico(Socket s) {
        connectionSocket = s;
        try {
            recibeDelCliente = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            enviaAlCliente = new PrintWriter(connectionSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Problema creacion socket");
        }
    }

    public void run() {
        int indiceUsuario = 0;
        int opcionLogin, opcionConsulta;
        String oracionCliente;
        while (true) {
            try {
            //recibe opcion de ingreso
            oracionCliente = recibeDelCliente.readLine();
            opcionLogin = Integer.parseInt(oracionCliente);

            //recibe usuario y clave
            String nombre = recibeDelCliente.readLine();
            String clave = recibeDelCliente.readLine();

            switch (opcionLogin) {
                case 1:
                    //usuarios nuevos                     
                    for (int i = 0; i < usuarios.size(); i++) {
                        while (usuarios.get(i).existeUsuario(nombre)) {
                            enviaAlCliente.println("Ingrese otro nombre de usuario.");
                            enviaAlCliente.println("?");
                            nombre = recibeDelCliente.readLine();
                            enviaAlCliente.println("Ingrese clave.");
                            enviaAlCliente.println("?");
                            clave = recibeDelCliente.readLine();
                        }
                        if (i == usuarios.size()) {
                            enviaAlCliente.println("Nombre disponible.");
                        }
                    }
                    usuarios.add(new Usuario(nombre, clave, LocalDateTime.now()));
                    indiceUsuario = usuarios.size() - 1;
                    enviaAlCliente.println("Bienvenido " + nombre + ".");
                    break;
                case 2:
                    //usuarios existentes
                    if (usuarios.isEmpty()) {
                        enviaAlCliente.println("No hay usuarios registrados.");
                        enviaAlCliente.println("OK");
                        break;
                    }
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarios.get(i).existeUsuario(nombre)) {
                            if (usuarios.get(i).claveEsCorrecta(clave)) {
                                indiceUsuario = i;
                                enviaAlCliente.println("Bienvenido " + nombre + ".");
                                break;
                            } else {
                                enviaAlCliente.println("Error en clave.");
                            }
                        } else if (i == usuarios.size()) {
                            enviaAlCliente.println("Usuario no encontrado.");
                        }
                    }
                    break;
            }

            enviaAlCliente.println("///Ingrese su opcion///");
            enviaAlCliente.println("1. Consultar la hora y dia actual.");
            enviaAlCliente.println("2. Consultar la hora y dia de la ultima consulta.");
            enviaAlCliente.println("3. Listar los seudonimos de los usuarios registrados.");
            enviaAlCliente.println("4. Enviar un mensaje a un usuario.");
            enviaAlCliente.println("5. Consultar si hay algun mensaje a su nombre.");
            enviaAlCliente.println("6. Borrar registro (implica borrar seudonimo y mensajes.");
            enviaAlCliente.println("7. Finalizar sesion de consulta.");
            enviaAlCliente.println("OK");

            try {
                //recibe opcion de servicio
                oracionCliente = recibeDelCliente.readLine();
                opcionConsulta = Integer.parseInt(oracionCliente);
            } catch (IOException | NumberFormatException e) {
                //en caso de tener problemas al parsear se fija una opcion 7 para salir
                opcionConsulta = 7;
            }

            switch (opcionConsulta) {
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
                    listarUsuarios(enviaAlCliente);
                    break;
                case 4:
                    String remitente,
                     destinatario,
                     mensaje;
                    remitente = usuarios.get(indiceUsuario).getNombre();
                    enviaAlCliente.println("Escriba destinatario");
                    enviaAlCliente.println("?");
                    destinatario = recibeDelCliente.readLine();
                    enviaAlCliente.println("Escriba mensaje");
                    enviaAlCliente.println("?");
                    mensaje = recibeDelCliente.readLine();
                    if (enviarMensaje(remitente, destinatario, mensaje)) {
                        enviaAlCliente.println("Mensaje enviado correctamente a "
                                + destinatario);
                        enviaAlCliente.println("OK");
                    } else {
                        enviaAlCliente.println("Usuario no encontrado");
                        enviaAlCliente.println("OK");
                    }
                    break;
                case 5:
                    int cantidadMens = usuarios.get(indiceUsuario).getMensajes().size();
                    if (cantidadMens > 0) {
                        enviaAlCliente.println("Tiene "+cantidadMens+" mensaje(s).");
                        enviaAlCliente.println("Ingrese opcion de cuales mensajes desea mostrar.");
                        enviaAlCliente.println("1. Los 5 primeros (mas antiguos).");
                        enviaAlCliente.println("2. Los 5 ultimos (mas nuevos).");
                        enviaAlCliente.println("3. Todos");
                        enviaAlCliente.println("?");
                        int opcionMensajes = Integer.parseInt(recibeDelCliente.readLine());
                        listarMensajes(opcionMensajes, enviaAlCliente, indiceUsuario, cantidadMens);
                    } else {
                        enviaAlCliente.println("No tiene mensajes");
                        enviaAlCliente.println("OK");
                    }
                    break;
                case 6:
                    usuarios.remove(indiceUsuario);
                    enviaAlCliente.println("Ha sido eliminado correctamente.");
                    enviaAlCliente.println("OK");
                    break;
                case 7:
                    enviaAlCliente.println("Sesion finalizada");
                    enviaAlCliente.println("OK");
                    break;
                default:
                    enviaAlCliente.println("Ingrese una opcion valida.");
                    enviaAlCliente.println("OK");
                    break;
            }
            //captura momento de consulta            
            try {
                /*verifica si el usuario elimino su cuenta
                para no guardar la fecha y hora de consulta*/
                if (opcionConsulta != 6) {
                    capturarConsulta(indiceUsuario);
                    indiceUsuario = -1;
                }
            } catch (Exception e) {
                capturarConsulta(0);
            }
            //linea por consola indicando quien envio que mensaje
            System.out.println(connectionSocket.getInetAddress() + " - " + nombre + " solicito una consulta tipo " + opcionConsulta);
            } catch (IOException ex) {
                System.out.println("Problema lectura/escritura en socket");
            }
        }
    }

    public static void main(String argv[]) throws Exception {
        String clientSentence;
        ServerSocket welcomeSocket = new ServerSocket(7777);
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            // crear hebra para nuevo cliente
            ServidorHebraBasico clienteNuevo
                    = new ServidorHebraBasico(connectionSocket);
            clienteNuevo.start();
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
        if (indice != 0) {
            usuarios.get(indice).setUltimaConsulta(LocalDateTime.now());
        }
    }

    private static String formatearFechaConsulta(LocalDateTime fechaConsulta) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fecha = fechaConsulta.format(formato);
        formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String hora = fechaConsulta.format(formato);
        String fechaLista = String.join(" ", "La ultima consulta fue el", fecha, "a las", hora);
        return fechaLista;
    }

    private static boolean enviarMensaje(String rem, String dest, String mensaje) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).existeUsuario(dest)) {
                usuarios.get(i).getMensajes().add(new Mensaje(rem, dest, mensaje));
                return true;
            }
        }
        return false;
    }

    private static void listarUsuarios(PrintWriter enviaAlCliente) {
        enviaAlCliente.println("Los usuarios registrados son:");
        for (int i = 0; i < usuarios.size(); i++) {
            String temp = usuarios.get(i).getNombre();
            enviaAlCliente.println(temp);
        }
        enviaAlCliente.println("OK");
    }

    private static void listarMensajes(int opcionMensajes,
            PrintWriter enviaAlCliente, int indiceUsuario, int cantidadMens) {

        switch (opcionMensajes) {
            case 1:
                enviaAlCliente.println("Los 5 primeros mensajes (mas antiguos):");
                if (cantidadMens < 5) {
                    for (int i = 0; i < cantidadMens; i++) {
                        String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                        enviaAlCliente.println(mens);
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                        enviaAlCliente.println(mens);
                    }
                }
                enviaAlCliente.println("OK");
                break;
            case 2:
                enviaAlCliente.println("Los 5 ultimos mensajes (mas nuevos):");
                if (cantidadMens < 5) {
                    for (int i = 0; i < cantidadMens; i++) {
                        String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                        enviaAlCliente.println(mens);
                    }
                } else {
                    for (int i = cantidadMens - 5; i < cantidadMens; i++) {
                        String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                        enviaAlCliente.println(mens);
                    }
                }
                enviaAlCliente.println("OK");
                break;
            case 3:
                enviaAlCliente.println("Tiene todos estos mensajes a su nombre:");
                for (int i = 0; i < cantidadMens; i++) {
                    String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                    enviaAlCliente.println(mens);
                }
                enviaAlCliente.println("OK");
                break;
            default:
                enviaAlCliente.println("Debe ingresar una opcion valida");
                enviaAlCliente.println("OK");
                break;
        }

    }
}
