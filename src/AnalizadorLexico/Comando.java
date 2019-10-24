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
public final class Comando {

    private List<String> listaComandos;

    public Comando() {
        listaComandos = new ArrayList();
        agregarComandosEnLista();
    }

        
    public void agregarComandosEnLista() {
        
        //Comandos Tarea #2
        listaComandos.add("PARA");
        listaComandos.add("FIN");
        
        listaComandos.add("AVANZA");
        listaComandos.add("AV");
        listaComandos.add("RETROCEDE");
        listaComandos.add("RE");
        
        listaComandos.add("GIRAIZQUIERDA");
        listaComandos.add("GI");
        listaComandos.add("GIRADERECHA");
        listaComandos.add("GD");
        listaComandos.add("ADIOS");
        listaComandos.add("HAZ");//DECLARACION DE VARIABLE
        
        
        //Comandos Tarea #3
        listaComandos.add("BORRAPANTALLA");
        listaComandos.add("BP");
        listaComandos.add("SUBELAPIZ");
        listaComandos.add("SL");
        listaComandos.add("BAJALAPIZ");
        listaComandos.add("BL");
        listaComandos.add("GOMA");
        listaComandos.add("CENTRO");
        listaComandos.add("LAPIZNORMAL");
        listaComandos.add("PONLAPIZ");
        listaComandos.add("OCULTATORTUGA");
        listaComandos.add("OT");
        listaComandos.add("MUESTRATORTUGA");
        listaComandos.add("MT");
        listaComandos.add("GOMA");
        listaComandos.add("PONCOLORLAPIZ");
        listaComandos.add("PONCL");
        listaComandos.add("PONCOLORRELLENO");
        listaComandos.add("RELLENA");
        listaComandos.add("REPITE");
        
        /*
        listaComandos.add("PONGROSOR");
        listaComandos.add("ROTULA");
        listaComandos.add("RO");
        listaComandos.add("TONO");
        listaComandos.add("PONGROSOR");
        listaComandos.add("PONG");
        listaComandos.add("RELLENA");
        listaComandos.add("PONPOS");
        listaComandos.add("ESCRIBE");
        listaComandos.add("ES");
        listaComandos.add("BORRATEXTO");
        listaComandos.add("BT");
       */
        
       
    }
     
    /*
    public void agregarComandosEnLista() {

        listaComandos.add("TO");
        listaComandos.add("END");
        
        listaComandos.add("FORWARD");
        listaComandos.add("FD");
        listaComandos.add("BACK");
        

        listaComandos.add("RIGHT");
        listaComandos.add("RT");
        listaComandos.add("LEFT");
        listaComandos.add("LT");
        
        listaComandos.add("ST");
        listaComandos.add("HT");
        
        listaComandos.add("MAKE"); //DECLARACION DE VARIABLE
    }
*/
    public String getComando(int i) {
        return listaComandos.get(i);
    }


    public boolean esComando(String str) {
        //System.out.println(str);

        return listaComandos.contains(str);
    }

    public String comienzaConComando(String str) {
        String cmd = "";

        Iterator<String> iter;
        iter = listaComandos.iterator();
        while (iter.hasNext()) {
            cmd = iter.next();

            if (str.startsWith(cmd)) {
                break;
            } else {
                cmd = "";
            }
        }
        return cmd;
    }

    public String terminaConComando(String str) {

        String cmd = "";
        Iterator<String> iter;
        iter = listaComandos.iterator();
        while (iter.hasNext()) {
            cmd = iter.next();
            if (str.endsWith(cmd)) {
                break;
            } else {
                cmd = "";
            }
        }
        return cmd;
    }

    public void mostrarListaComandos() {
        System.out.println("Esta es la lista de Comandos");
        Iterator<String> iter;
        iter = listaComandos.iterator();
        while (iter.hasNext()) {
            String cmd = iter.next();
            System.out.println(cmd);
        }
    }

}
