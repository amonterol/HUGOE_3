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
public class Identificador {

    public Identificador() {

    }

    public  boolean verificarInicioIdentificador(String primerCaracter) {

        return primerCaracter.matches("^[a-zA-Z].*");
    }

    public  boolean verificarSecuenciaPalabra_Palabra(String cadena) {
        int i = 0;
        int j = cadena.length();
        for (int x = 0; x < cadena.length(); x++) {
            //System.out.println("Caracter " + x + ": " + cadena.charAt(x));
            if(cadena.charAt(x) == '_'){
                ++i;
            } 
            
        }
        return ((cadena.charAt(cadena.length() - 1 ) != '_') && i == 1);
     
    }

    public  boolean verificarSecuenciaLetraDigitos(String cadena) {

        return cadena.matches("[A-Za-z0-9]*");
    }

    public  boolean esIdentificador(String str) {

        boolean resultado;
        if (!verificarInicioIdentificador(str)) {
            //System.out.println("No inicia con letra");
            resultado = false;
        } else if (verificarSecuenciaPalabra_Palabra(str)) {
            //System.out.println("palabra_palabra");
            resultado = true;
        } else if (!verificarSecuenciaLetraDigitos(str)) {
            //System.out.println("No es una secuencia de letras y digitos" + str);
            resultado = false;
        } else {
            resultado = true;
        }
        return resultado;
    }

   

}
