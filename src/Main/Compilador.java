/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import AnalizadorLexico.AnalizadorLexico;
import AnalizadorLexico.CodigoFuente;
import AnalizadorLexico.LineaContenido;
import AnalizadorLexico.MiError;
import AnalizadorLexico.Token;
import AnalizadorSintactico.AnalizadorSintactico;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

/**
 *
 * @author PC
 */
public class Compilador {

    private static AnalizadorLexico lexico;
    private static AnalizadorSintactico sintactico;
    private static CodigoFuente codigoFuente;
    private static List<String> contenidoArchivoFuente;
    private static List<Token> listaTokens;
    private static List<LineaContenido> contenido;
    private static List<LineaContenido> contenidoFinal;
    private static List<MiError> listaErrores;
    private static String fileName;

    public static void main(String[] args) throws IOException {

        //Falta mostrar error sin args[0] no existe es decir si el no se incluye el nombre del archivo origina;
        try {
            fileName = args[0];
        } catch (Exception e) {
            //Muestra un joptionpane dialog using showMessageDialog
            JOptionPane.showMessageDialog(null, "Debe suministrar un nombre de archivo con el formato: nombreArchivo.HUGO", "Falta archivo", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
        //El archivoFuente contiene la localizacion del programa escrito en .HUGO
        String archivoFuente = "C:\\Program Files (x86)\\MSWLogo\\" + fileName;
        if (!archivoFuente.isEmpty()) {

            //Utilizamos el objeto "process" de la clase ProcessBuilder para ejecutar
            //la lista de comandos contenido en "command" de esta forma podemos ejecutar
            //comandos en el el "cmd"
            List<String> command = new ArrayList<>();
            command.add("cmd.exe");
            command.add("/c");
            command.add("cd \"C:\\Program Files (x86)\\MSWLogo\" && logo32.exe -l cuadrado.lgo");
            ProcessBuilder process;
            process = new ProcessBuilder(command);
            process.redirectErrorStream(true);
            process.start();

            System.out.println("main+main+main+main+FINAL");
            contenidoFinal = new ArrayList<>();
            contenidoFinal = compilarArchivoFuente(archivoFuente, fileName);
            //crearArchivoSalida(contenidoFinal);

            System.out.println("main+main+main+main+INICIO");
            contenidoFinal.forEach(item -> System.out.println(item)); //imprimiendo como objetos
            contenidoFinal.forEach((item) -> {
                System.out.println(item.getLinea() + " " + item.getInstruccion());
            });
            System.out.println("main+main+main+main+FINAL");

        } else {
            System.out.println("El archivo fuente no contiene informacion");
        }
    }

    /*
    /Esta funcion es la que se encarga de llamar a las diferentes partes del compilador
    Primero, crea un objeto archivo que es el encargado de abrir el archivo desde la localizacion
    recibida desde el main.
    Luego, creamos un objeto "lexico" con el cual realizamos linea por linea> este objeto, se encarga de 
    la eliminacion de la los espacios y la creacion de la lista de tokens o tabla de simbolos, es decir, la 
    separacion en tokens segun sus tipos.
    Seguidamente, creamos un objeto "sintactico" de la clase AnalisisSintactico el cual recibe
    el objeto lexico anterior. El sintactico se encarga de realizar en analisis sintactico y semantico
    del compilador.
    El producto del analisis sintactico depende de si se encontraron o no errores. En el primer caso,
    produce un archivo denominado "nombreArchivoOriginal-Hugo-Errores.txt". En el segundo caso, es decir, 
    no encuentra errores se produce el nombreArchivo.
     */
    static List<LineaContenido> compilarArchivoFuente(String archivoFuente, String fileName) throws IOException {
        System.out.println("crearArchivoSinErrores-EL NOMBRE DEL ARCHIVO ORIGINAL SIN LA EXTENSION ES-> " + fileName);
        //Creamos un objeto "archivo de la clase CodigoFuente pasandole como paramentro el archivo original a compilar
        CodigoFuente archivo = new CodigoFuente(archivoFuente);

        //El objeto archivo usa el metodo abrirArchivo para convertir las lineas del archivo en una lista de strings
        //la cual va quedar almacenada en el atributo "contenido" del objeto archivo
        archivo.abrirArchivo();

        // Instanciamos un objeto "lexico" de la clase AnalizadorLexico con la lista de strings 
        //almacenada en el atributo "contenido" del objeto archivo, a traves del metodo "getContenidoArchivo"
        //de la clase CodigoFuente
        lexico = new AnalizadorLexico(archivo.getContenidoArchivo());

        //Mediante el objeto "lexico" accedemos al metodo analisisLexico de la clase AnalizadorLexico
        //para realizar el analisis lexico (creacion de tokens) el cual produce una tabla de simbolos de nombre
        //"listaTokens"
        contenido = lexico.analisisLexico();

        //Creamos un objeto de nombre "sintactico" de la clase AnalizadorSintactico, para la cual le pasamos 
        //el objeto "lexico" creado anteriormeente
        sintactico = new AnalizadorSintactico(lexico, fileName);

        //El objeto "sintactico" llama al metodo "sintactico()" de la clase AnalizadorSintactico para realizar el
        //analisis sintanctico y semantico de la tabla de simbolos, en este caso la "listaTokens"
        contenidoFinal = sintactico.sintactico();
        return contenidoFinal;
    }

}
