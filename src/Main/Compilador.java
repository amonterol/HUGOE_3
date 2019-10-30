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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static void main(String[] args) throws IOException {

        //El archivoFuente contiene la localizacion del programa escrito en .HUGO
        String archivoFuente = "C:\\Program Files (x86)\\MSWLogo\\cuadrado.hugo";
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
            contenidoFinal = compilarArchivoFuente(archivoFuente);
            //crearArchivoSalida(contenidoFinal);

            System.out.println("main+main+main+main+INICIO");
            contenidoFinal.forEach(item -> System.out.println(item)); //imprimiendo como objetos
            contenidoFinal.forEach((item) -> {
                System.out.println(item.getLinea() + " " + item.getInstruccion());
            });
            System.out.println("main+main+main+main+FINAL");

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
    static List<LineaContenido> compilarArchivoFuente(String archivoFuente) throws IOException {

        CodigoFuente archivo = new CodigoFuente(archivoFuente);
        List<String> contenido1 = new ArrayList<>();
        /*
        (1)
        archivo.abrirArchivo();
        //contenido1 = archivo.getContenidoArchivo();
        //hasta aqui todo bien me devuelve el archivo convertido en una List<String>, es decir una instruccion por linea  

        lexico = new AnalizadorLexico(archivo.getContenidoArchivo());
        // contenido1 = lexico.eliminarCaracteresRedundates( archivo.getContenidoArchivo() );
        //hasta aqui va bien me devuelve el List<String> con archivos sin caracteres redundantes
        contenido = lexico.analisisLexico();
        //hasta aqui va bien me devuelve una List<LineaContenido> con cada linea de contenido del archivo final y una List<Tokens> con cada tokens 
         */

 /*(2)*/
        archivo.abrirArchivo();
        lexico = new AnalizadorLexico(archivo.getContenidoArchivo());
        contenido = lexico.analisisLexico();

        sintactico = new AnalizadorSintactico(lexico);
        contenidoFinal = sintactico.sintactico();
        return contenidoFinal;
    }

    

}
