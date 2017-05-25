

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Usuario {
    private String nombre;
    private String pass;
    private LocalDateTime ultimaConsulta;
    private ArrayList <Mensaje> mensajes;
    
    public Usuario(){
        nombre="";
        pass="";
    }
    
    public Usuario(String n, String p, LocalDateTime f){
        nombre=n;
        pass=p;
        ultimaConsulta = f;
        mensajes = new ArrayList<Mensaje>();
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

    public LocalDateTime getUltimaConsulta() {
        return ultimaConsulta;
    }

    public void setUltimaConsulta(LocalDateTime ultimaConsulta) {
        this.ultimaConsulta = ultimaConsulta;
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
}
