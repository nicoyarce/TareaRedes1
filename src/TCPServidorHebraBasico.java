
import java.io.*;
import java.net.*;

class TCPServidorHebraBasico extends Thread {

    BufferedReader inFromClient;
    PrintWriter outToClient;
    Socket connectionSocket;

    public TCPServidorHebraBasico(Socket s) {
        connectionSocket = s;
        try {
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Problema creacion socket");
        }

    }

    public void run() {
        //while(true){
        try {
            String clientSentence = inFromClient.readLine();
            System.out.println(connectionSocket.getInetAddress() + " message:" + clientSentence);
            outToClient.println(clientSentence);
        } catch (IOException e) {
            System.out.println("Problema lectura/escritura en socket");
        }
        // }
    }

    public static void main(String argv[]) throws Exception {
        String clientSentence;
        ServerSocket welcomeSocket = new ServerSocket(9875);
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            // crear hebra para nuevo cliente
            TCPServidorHebraBasico clienteNuevo
                    = new TCPServidorHebraBasico(connectionSocket);
            clienteNuevo.start();
        }
        // si se usa una condicion para quebrar el ciclo while, se deben cerrar los sockets!
        // connectionSocket.close(); 
        // welcomeSocket.close(); 
    }
}
