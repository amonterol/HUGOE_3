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
        
        String archivoFuente = "C:\\Program Files (x86)\\MSWLogo\\hexagono9.txt";

        /*
        (1)
        contenido = new ArrayList<>();
        contenido = compilarArchivoFuente(archivoFuente);
        System.out.println("main+main+main+main+INICIO");
        contenido.forEach(item -> System.out.println(item)); //imprimiendo como objetos
        contenido.forEach( (item) -> {
            System.out.println( item.getLinea() + " " + item.getInstruccion() );
        });
        System.out.println("main+main+main+main+FINAL");
         */
        contenidoFinal = new ArrayList<>();
        contenidoFinal = compilarArchivoFuente(archivoFuente);
        System.out.println("main+main+main+main+INICIO");
        contenidoFinal.forEach(item -> System.out.println(item)); //imprimiendo como objetos
        contenidoFinal.forEach((item) -> {
            System.out.println(item.getLinea() + " " + item.getInstruccion());
        });
        System.out.println("main+main+main+main+FINAL");
    }

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

        sintactico = new AnalizadorSintactico( lexico );
        contenidoFinal = sintactico.sintactico();
        return contenidoFinal;
    }

}
