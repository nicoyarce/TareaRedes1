
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
    Calendar fecha = Calendar.getInstance();

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
        boolean nuevaConsulta = false;
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
                            enviaAlCliente.println("Ese nombre ya existe. Ingrese otro nombre de usuario.");
                            enviaAlCliente.println("?");
                            nombre = recibeDelCliente.readLine();
                            enviaAlCliente.println("Ingrese clave.");
                            enviaAlCliente.println("?");
                            clave = recibeDelCliente.readLine();
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
                        return;
                    }
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarios.get(i).existeUsuario(nombre)) {
                            if (usuarios.get(i).claveEsCorrecta(clave)) {
                                indiceUsuario = i;
                                enviaAlCliente.println("Bienvenido " + nombre + ".");
                                break;
                            } else {
                                enviaAlCliente.println("Error en clave.");
                                return;
                            }
                        } else if (i == usuarios.size()) {
                            enviaAlCliente.println("Usuario no encontrado.");
                            return;
                        }
                    }
                    break;
            }

            do {
                enviaAlCliente.println("1. Consultar la hora y dia actual.");
                enviaAlCliente.println("2. Consultar la hora y dia de la ultima consulta.");
                enviaAlCliente.println("3. Listar los seudonimos de los usuarios registrados.");
                enviaAlCliente.println("4. Enviar un mensaje a un usuario.");
                enviaAlCliente.println("5. Consultar si hay algun mensaje a su nombre.");
                enviaAlCliente.println("6. Borrar registro (implica borrar seudonimo y mensajes).");
                enviaAlCliente.println("7. Finalizar sesion de consulta.");
                enviaAlCliente.println("//Ingrese su opcion//");
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
                        break;
                    case 2:
                        LocalDateTime ultimaConsulta = usuarios.get(indiceUsuario).getUltimaConsulta();
                        formatearFechaConsulta(ultimaConsulta);
                        enviaAlCliente.println(formatearFechaConsulta(ultimaConsulta));
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
                        } else {
                            enviaAlCliente.println("Usuario no encontrado");
                        }
                        break;
                    case 5:
                        int cantidadMens = usuarios.get(indiceUsuario).getMensajes().size();
                        int cantidadMostrada = 0;
                        int opcionMensajes = 0;
                        String opcionEliminar = "n";
                        if (cantidadMens > 0) {
                            enviaAlCliente.println("Tiene " + cantidadMens + " mensaje(s).");
                            //consulta por la cantidad de mensajes que se mostraran
                            do {
                                enviaAlCliente.println("Ingrese cuantos mensajes desea mostrar.");
                                enviaAlCliente.println("?");
                                try {
                                    cantidadMostrada = Integer.parseInt(recibeDelCliente.readLine());
                                    if (cantidadMostrada <= 0 || cantidadMostrada > cantidadMens) {
                                        enviaAlCliente.println("Ingrese opcion valida.");
                                    }
                                } catch (IOException | NumberFormatException e) {
                                    enviaAlCliente.println("Ingrese opcion valida.");
                                }

                            } while (cantidadMostrada <= 0 || cantidadMostrada > cantidadMens);
                            //consulta por cuales mensajes mostrara
                            do {
                                if (cantidadMens == cantidadMostrada) {
                                    opcionMensajes = 3;
                                    break;
                                }
                                enviaAlCliente.println("Ingrese opcion de cuales mensajes desea mostrar.");
                                enviaAlCliente.println("1. El/Los " + cantidadMostrada + " primero(s) (mas antiguo(s)).");
                                enviaAlCliente.println("2. El/Los " + cantidadMostrada + " ultimo(s) (mas nuevo(s)).");
                                enviaAlCliente.println("?");
                                try {
                                    opcionMensajes = Integer.parseInt(recibeDelCliente.readLine());
                                    if (opcionMensajes != 1 && opcionMensajes != 2) {
                                        enviaAlCliente.println("Ingrese opcion valida.");
                                    }
                                } catch (IOException | NumberFormatException e) {
                                    enviaAlCliente.println("Ingrese opcion valida");
                                }

                            } while (opcionMensajes != 1 && opcionMensajes != 2);
                            listarMensajes(cantidadMostrada, opcionMensajes, enviaAlCliente, indiceUsuario, cantidadMens);
                        } else {
                            enviaAlCliente.println("No tiene mensajes");
                            break;
                        }
                        do {
                            enviaAlCliente.println("Desea eliminar los mensajes que leyo (s/n)?");
                            enviaAlCliente.println("?");
                            opcionEliminar = recibeDelCliente.readLine();
                        } while (!opcionEliminar.equalsIgnoreCase("s")
                                && !opcionEliminar.equalsIgnoreCase("n"));
                        if (opcionEliminar.equalsIgnoreCase("s")) {
                            eliminarMensajes(cantidadMostrada, opcionMensajes, indiceUsuario, cantidadMens);
                        }
                        break;
                    case 6:
                        usuarios.remove(indiceUsuario);
                        enviaAlCliente.println("Ha sido eliminado correctamente.");
                        break;
                    case 7:
                        enviaAlCliente.println("Sesion finalizada");
                        break;
                    default:
                        enviaAlCliente.println("Ingrese una opcion valida.");
                        break;
                }
                //linea por consola indicando quien envio que mensaje
                System.out.println(connectionSocket.getInetAddress() + " - " + nombre + " solicito una consulta tipo " + opcionConsulta);

                //captura momento de consulta            
                /*verifica si el usuario elimino su cuenta
                para no guardar la fecha y hora de 
                asi como para preguntar si desea otra consulta*/
                if (opcionConsulta != 6 && opcionConsulta != 7) {
                    capturarConsulta(indiceUsuario);
                    do {
                        enviaAlCliente.println("Desea realizar otra consulta? (s/n)");
                        enviaAlCliente.println("?");
                        oracionCliente = recibeDelCliente.readLine();
                    } while (!oracionCliente.equalsIgnoreCase("s")
                            && !oracionCliente.equalsIgnoreCase("n"));

                    if (oracionCliente.equalsIgnoreCase("s")) {
                        nuevaConsulta = true;
                    } else if (oracionCliente.equalsIgnoreCase("n")) {
                        nuevaConsulta = false;
                        enviaAlCliente.println("BYE");
                        return;
                    }
                } else {
                    nuevaConsulta = false;
                    enviaAlCliente.println("BYE");
                    return;
                }
            } while (nuevaConsulta == true);
        } catch (IOException ex) {
            System.out.println("Problema lectura/escritura en socket");
        }
    }

    public static void main(String argv[]) throws Exception {
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

    private String obtenerHora() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String horaFormateada = LocalDateTime.now().format(formato);
        return horaFormateada;
    }

    private String obtenerFecha() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateada = LocalDateTime.now().format(formato);
        return fechaFormateada;
    }

    private void capturarConsulta(int indice) {
        if (indice != -1) {
            usuarios.get(indice).setUltimaConsulta(LocalDateTime.now());
        }
    }

    private String formatearFechaConsulta(LocalDateTime fechaConsulta) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fecha = fechaConsulta.format(formato);
        formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String hora = fechaConsulta.format(formato);
        String fechaLista = String.join(" ", "La ultima consulta fue el", fecha, "a las", hora);
        return fechaLista;
    }

    private boolean enviarMensaje(String rem, String dest, String mensaje) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).existeUsuario(dest)) {
                usuarios.get(i).getMensajes().add(new Mensaje(rem, dest, mensaje));
                return true;
            }
        }
        return false;
    }

    private void listarUsuarios(PrintWriter enviaAlCliente) {
        enviaAlCliente.println("Los usuarios registrados son:");
        for (int i = 0; i < usuarios.size(); i++) {
            String temp = usuarios.get(i).getNombre();
            enviaAlCliente.println(temp);
        }
    }

    private void listarMensajes(int cantidadAImprimir, int opcionMensajes,
            PrintWriter enviaAlCliente, int indiceUsuario, int cantidadMens) {

        switch (opcionMensajes) {
            case 1:
                enviaAlCliente.println("El/Los " + cantidadAImprimir + " primer(os) mensaje(s)(mas antiguo(s)):");
                if (cantidadMens < cantidadAImprimir) {
                    for (int i = 0; i < cantidadMens; i++) {
                        String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                        enviaAlCliente.println(mens);
                    }
                } else {
                    for (int i = 0; i < cantidadAImprimir; i++) {
                        String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                        enviaAlCliente.println(mens);
                    }
                }
                break;
            case 2:
                enviaAlCliente.println("El/Los" + cantidadAImprimir + " ultimo(s) mensaje(s)(mas nuevo(s)):");
                if (cantidadMens < cantidadAImprimir) {
                    for (int i = 0; i < cantidadMens; i++) {
                        String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                        enviaAlCliente.println(mens);
                    }
                } else {
                    for (int i = cantidadMens - cantidadAImprimir; i < cantidadMens; i++) {
                        String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                        enviaAlCliente.println(mens);
                    }
                }
                break;
            case 3:
                enviaAlCliente.println("Tiene todos estos mensajes a su nombre:");
                for (int i = 0; i < cantidadMens; i++) {
                    String mens = usuarios.get(indiceUsuario).getMensajes().get(i).toString();
                    enviaAlCliente.println(mens);
                }
                break;
            default:
                enviaAlCliente.println("Debe ingresar una opcion valida");
                break;
        }
    }

    private void eliminarMensajes(int cantidadAImprimir, int opcionMensajes,
            int indiceUsuario, int cantidadMens) {
        switch (opcionMensajes) {
            case 1:
                if (cantidadMens < cantidadAImprimir) {
                    for (int i = 0; i < cantidadMens; i++) {
                        usuarios.get(indiceUsuario).getMensajes().remove(i);
                    }
                } else {
                    for (int i = 0; i < cantidadAImprimir; i++) {
                        usuarios.get(indiceUsuario).getMensajes().remove(i);
                    }
                }
                break;
            case 2:
                if (cantidadMens < cantidadAImprimir) {
                    for (int i = 0; i < cantidadMens; i++) {
                        usuarios.get(indiceUsuario).getMensajes().remove(i);
                    }
                } else {
                    for (int i = cantidadMens - cantidadAImprimir; i < cantidadMens; i++) {
                        usuarios.get(indiceUsuario).getMensajes().remove(i);
                    }
                }
                break;
            case 3:
                for (int i = 0; i < cantidadMens; i++) {
                    usuarios.get(indiceUsuario).getMensajes().remove(i);
                }
                break;
        }
    }
}
