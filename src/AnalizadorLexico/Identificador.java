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

    public boolean verificarInicioIdentificador(String primerCaracter) {

        return primerCaracter.matches("^[a-zA-Z].*");
    }

    public boolean verificarSecuenciaPalabra_Palabra(String cadena) {
        int i = 0;
        int index = 0;
        boolean resultado = false;

        //Primero verificamos que solo exista un guion bajo y que no este ni 
        //al inicio ni al final del posible identificador
        for (int x = 0; x < cadena.length(); x++) {
            //System.out.println("Caracter " + x + ": " + cadena.charAt(x));
            if (cadena.charAt(x) == '_') {
                ++i;
            }
        }
        boolean existeSoloUnGuionBajo = ((cadena.charAt(cadena.length() - 1) != '_') && (cadena.charAt(0) != '_') && i == 1);
        //Como solo hay un guion bajo, encontramos el indice donde se encuentra y 
        //dividimos el posible identificador en dos cadenas para comprobar que ambas
        //son secuencias de letras y numeros
        if (existeSoloUnGuionBajo) {
            index = cadena.indexOf("_");
            String palabraInicial = cadena.substring(0, index);
            System.out.println("La parte antes del guion bajo es ->" + palabraInicial);
            String palabraFinal = cadena.substring(index + 1);
            System.out.println("La parte despues del guion bajo es ->" + palabraFinal);
            boolean existeSecuenciaLetrasNumerosAntesGuion = verificarSecuenciaLetraDigitos(palabraInicial);
            System.out.println("Antes del guion bajo " + existeSecuenciaLetrasNumerosAntesGuion);
            boolean existeSecuenciaLetrasNumerosDespuesGuion = verificarSecuenciaLetraDigitos(palabraFinal) && !verificarSecuenciaSoloDigitos(palabraFinal);

            System.out.println("Despues del guion bajo " + verificarSecuenciaLetraDigitos(palabraFinal));

            System.out.println("Despues del guion bajo " + verificarSecuenciaSoloDigitos(palabraFinal));
            System.out.println("Despues del guion bajo " + existeSecuenciaLetrasNumerosDespuesGuion);

            if (existeSecuenciaLetrasNumerosAntesGuion && existeSecuenciaLetrasNumerosDespuesGuion) {
                resultado = true;
            } else {
                resultado = false;
            }
        } else {
            resultado = false;
        }
        System.out.println("La consulta de palabra_palabra " + resultado);

        return resultado;

    }

    public boolean verificarSecuenciaLetraDigitos(String cadena) {

        return cadena.matches("[A-Za-z0-9]*");
    }

    public boolean verificarSecuenciaSoloDigitos(String cadena) {
        return cadena.matches("[0-9]*");
    }

    /*
    public boolean esIdentificador(String str) {

        boolean resultado;
        if (!verificarInicioIdentificador(str)) {
            System.out.println("No inicia con letra");
            resultado = false;
        } else if (verificarSecuenciaPalabra_Palabra(str)) {
            System.out.println("palabra_palabra");
            resultado = true;
        } else if (!verificarSecuenciaLetraDigitos(str)) {
            System.out.println("No es una secuencia de letras y digitos" + str);
            resultado = false;
        } else {
            resultado = true;
        }
        System.out.println("Estamos en IDENTIFICADOR el valor de la consulta es  " + resultado);
        return resultado;
    }

     */
    public boolean esIdentificador(String str) {

        boolean resultado = false;
        if (verificarInicioIdentificador(str)) {
            System.out.println("Inicia con letra");
            if (verificarSecuenciaPalabra_Palabra(str)) {
                 System.out.println("Es secuencia de Palabra_Palabra");
                resultado = true;
            } else if (verificarSecuenciaLetraDigitos(str)) {
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
        System.out.println("Estamos en IDENTIFICADOR el valor de la consulta es  " + resultado);
        return resultado;
    }

}
