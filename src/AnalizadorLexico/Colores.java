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
public final class Colores {
    private List<Color> listaColores;

    public Colores() {
        this.listaColores = agregarColoresAListaDeColores();
    }

   
    public List<Color> agregarColoresAListaDeColores() {

        List<Color> lista = new ArrayList();
        lista.add(new Color("NEGRO", 0));
        lista.add(new Color("AZULFUERTE", 1));
        lista.add(new Color("VERDE", 2));
        lista.add(new Color("AZULCLARO", 3));
        lista.add(new Color("ROJO", 4));
        lista.add(new Color("ROSA", 5));
        lista.add(new Color("AMARILLO", 6));
        lista.add(new Color("BLANCO", 7));
        lista.add(new Color("CAFE", 8));
        lista.add(new Color("CAFECLARO", 9));
        lista.add(new Color("VERDEMEDIO", 10));
        lista.add(new Color("VERDEAZUL", 11));
        lista.add(new Color("SALMON", 12));
        lista.add(new Color("LILA", 13));
        lista.add(new Color("NARANJA", 14));
        lista.add(new Color("GRIS", 15));

        return lista;

    }
    
    public boolean esColorPermitido(String str) {
        //System.out.println(str);
        System.out.println("Estamos en esColorPermitido " + str);
        boolean consulta = false;
        List<Color> lista;
        lista = getListaColores();
        Iterator<Color> iter;
        iter = lista.iterator();
        while (iter.hasNext()) {
            Color c = (Color) iter.next();
            if (c.getNombre().equalsIgnoreCase(str.trim())) {
                consulta = true;
                break;
            } else {
                consulta = false;
            }
        }
        System.out.println("El valor de la consulta es " + consulta);
        return consulta;

    }
     
    public List<Color> getListaColores() {
        return listaColores;
    }

    public void setListaColores(List<Color> listaColores) {
        this.listaColores = listaColores;
    }
}
