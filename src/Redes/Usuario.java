package Redes;

import java.util.ArrayList;

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
    
    public boolean verificarUsuario(ArrayList <Usuario> usuarios, String nombre) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getNombre().equals(nombre)) {
                System.out.println("");
            } else {
                
            }
            
        }
        return false;
    }
}
