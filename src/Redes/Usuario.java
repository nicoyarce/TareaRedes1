package Redes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class Usuario {
    private String nombre;
    private String pass;
    private Calendar ultimaConsulta;
    private ArrayList <Mensaje> mensajes;
    
    public Usuario(){
        nombre="";
        pass="";
    }
    
    public Usuario(String n, String p){
        nombre=n;
        pass=p;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public ArrayList<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(ArrayList<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }
    
    public boolean existeUsuario(String nombre) {
        return this.nombre.equals(nombre);
    }
    
    public boolean claveEsCorrecta(String clave){
        return this.pass.equals(clave);
    }
    public static void main(String[] args) {
        Usuario u = new Usuario();
        System.out.println(LocalDateTime.now());
        
    }
}
