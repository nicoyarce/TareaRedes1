package Redes;

public class Usuario {
    private String nombre;
    private String pass;
    
    public Usuario(){
        nombre="";
        pass="";
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
    
    public boolean verificarExistencia(String nombre) {
        return this.nombre.equals(nombre);
    }
    
    public boolean verificarClave(String clave){
        return this.pass.equals(clave);
    }
}
