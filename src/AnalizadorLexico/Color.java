/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorLexico;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author PC
 */
public final class Color {

    
    
    private String nombre;
    private int numero;

    public Color(String nombre, int numero) {
        this.nombre = nombre;
        this.numero = numero;
       
    }

    public Color() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
    
      
    
    
    /* 
    String[] listaColores = {"NEGRO",
        "AZULFUERTE",
        "VERDE",
        "AZULCLARO",
        "ROJO",
        "ROSA",
        "AMARILLO",
        "BLANCO",
        "CAFE",
        "CAFECLARO",
        "VERDEMEDIO",
        "VERDEAZUL",
        "SALMON",
        "LILA",
        "NARANJA",
        "GRIS"};

    public boolean esColorPermitido(String str) {
        //System.out.println(str);
        System.out.println("Estamos en esColorPermitido " + str);
        boolean consulta = false;
        for (String color : listaColores) {
            if (color.equalsIgnoreCase(str)) {
                consulta = true;
            }
        }
        System.out.println("El valor de la consulta es " + consulta);
        return consulta;

    }
*/
}
