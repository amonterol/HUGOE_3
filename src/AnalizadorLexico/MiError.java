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
public class MiError {

    int linea;
    String error;

    public MiError(int linea, String error) {
        this.linea = linea;
        this.error = error;
    }

    public MiError() {
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    private String[] errores = {
        " Advertencia: instrucción  xxxx no es soportada por esta versión",
        " ERROR 100: no hay informacion que mostrar.",
        " ERROR 101: el numero de líneas del programa excede la cantidad máxima permitida",
        " ERROR 102: falta corchete izquierdo",
        " ERROR 103: falta corchete derecho",
        " ERROR 110: se require una variable o identificador valido",
        " Error 111: este comando no admite argumentos",
        " Error 112: se require un argumento entero o una variable declarada previamente para este comando",
        " ERROR 114: un numero entero solo puede ser usado como argumento de una función",
        " ERROR 115: falta el comando PARA",
        " ERROR 116: falta el comando FIN",
        " ERROR 119: falta el operador de declaracion de variables ( \" )",
        " ERROR 120: falta la variable a declarar",
        " ERROR 122: la variable fue definida previamente",
        " ERROR 123: la variable no ha sido declarada previamente",
        " ERROR 125: toda instruccion de REPITE debe ser comando valido",
        " ERROR 126: un entero no puede ser utilizado como variable",
        " ERROR 127: un numero real no puede ser utilizado como variable",
        " ERROR 128: se esperaba un identificador valido ",
        " ERROR 129: No es identificador valido ",
        " ERROR 130: falta el valor para asignar a la variable declarada",
        " ERROR 131: la lista de comandos a repetir debe comenzar con un comando valido",
        " ERROR 132: se require un argumento entero",
        " ERROR 133: no se indica el numero de veces que se debe repetir las instrucciones",
        " ERROR 134: falta el operador de (:)de poder utilizar una variable",
        " ERROR 135: la instruccion debe comenzar con un comando valido",
        " ERROR 136: el nombre de variable no es valido",
        " ERROR 137: la funcion requiere como argumento un color valido",
        " ERROR 138: el color proporcionado no es un color valido",
        " ERROR 139: los valores numéricos debe ser de tipo entero",
        " ERROR 140: el programa debe iniciar con el comando PARA",
        " ERROR 141: el nombre del programa debe ser un identificador valido",
        " ERROR 142: el programa debe finalizar con el comando FIN",
        " ERROR 143: no se permiten mas comandos luego del comando FIN",
        " ERROR 144: falta el entero que indica en numero de repiticiones del comando",
        " ERROR 145: los comandos solo estan permitidos dentro de los corchetes",
        " ERROR 146: se requiere una lista de comandos validos",
        " ERROR 147: esta version solo acepta corchetes en el comando REPITE",
        " ERROR 148: se require una lista de argumentos para este comando",
        " ERROR 149: la lista de argumentos esta incompleta",
        " ERROR 150: la lista de comandos de REPITE no debe contener el comando HAZ",
        " ERROR 151: toda identificador o variable debe ser el argumento de un comando valido",
        " ERROR 152: se require un entero o una variable declarada para completar la declaracion de la variable",
        " ERROR 153: la lista de argumentos esta incompleta, se require HAZ \"Nombre de la variable :Valor de la variable",
        " ERROR 154: el valor a asignar a la variable debe ser un entero o una variable declarada previamente",
        " ERROR 155: se requiere establecer previamente el color para el relleno",
        " ERROR 156: el comando debe estar al inicio de la linea",
        " ERROR 157: un color valido solo pueden utilizarse como argumentos de PONCOLORELLENO o PONCOLORLAPIZ",
        " ERROR 158: un color valido no puede ser utilizado como nombre de variable a declarar",
        " ERROR 159: un comando de hugo  no puede ser como nombre de variable a declarar",
        " ERROR 160: la lista de comandos a repetir debe estar entre un corchete izquierdo y uno derecho",
        " ERROR 161: un comando de logo no puede ser usado como nombre de variable a declarar",
       " ERROR 162: un comando de hugo no puede ser usado como valor",
       " ERROR 163: un comando de logo no puede ser usado como valor",
       " ERROR 164: la instruccion es invalida"

    };

    @Override
    public String toString() {
        return "Error: " + this.linea + " " + this.error;
    }

}
