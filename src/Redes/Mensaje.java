/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Redes;

/**
 *
 * @author Nicoyarce
 */
public class Mensaje {    
    private String remitente;
    private String destinatario;
    private String mensaje;
    private boolean leido;
    
    public Mensaje(String r, String d, String m){
        this.remitente=r;
        this.destinatario=d;
        this.mensaje=m;
        this.leido=false;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    @Override
    public String toString() {
        return "Mensaje> " + "Remitente:" + remitente +" Contenido:" + mensaje;
    }
    
    
}
