/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorLexico;

/**
 *
 * @author Administrator
 */
public class NombreProcedimiento {

    public NombreProcedimiento() {
    }
     public boolean verificarInicioConSoloLetras(String primerCaracter) {

        return primerCaracter.matches("^[a-zA-Z].*");
    }

    public boolean verificarSecuenciaLetraDigitos(String cadena) {

        return cadena.matches("[A-Za-z0-9]*");
    }

    public boolean esIdentificador(String str) {

        boolean resultado = false;
        if (verificarInicioConSoloLetras(str)) {
            System.out.println("Inicia con letra");
            if (verificarSecuenciaLetraDigitos(str)) {
                System.out.println("Es secuencia de letras y numeros");
                resultado = true;
            } else {
                System.out.println("No es secuencia de letras y numeros ni una palabra_palabra");
                resultado = false;
            }
        } else {
            System.out.println("No inicia con letra");
            resultado = false;
        }
        System.out.println("Estamos en Nombre Procedmiento,  el valor de la consulta es  " + resultado);
        return resultado;
    }
}
