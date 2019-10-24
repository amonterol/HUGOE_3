/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorLexico;

/**
 *
 * @author PC
 */
public class ComandoLogo {

   private String nombre;
    private int enHugo;

    public ComandoLogo(String nombre, int enHugo) {
        this.nombre = nombre;
        this.enHugo = enHugo;
    }

    public ComandoLogo() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEnHugo() {
        return enHugo;
    }

    public void setEnHugo(int enHugo) {
        this.enHugo = enHugo;
    }

}
