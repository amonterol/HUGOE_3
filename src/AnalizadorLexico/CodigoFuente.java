/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorLexico;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author PC
 */
public class CodigoFuente {

    private   List<String> contenidoArchivo;
    private   String archivoFuente;

    public CodigoFuente(String archivoFuente) {
        this.archivoFuente = archivoFuente;
    }

    public  void abrirArchivo() throws IOException {
        //Funcion que lee el archivo que contiene el programa en formato .HUGO  
        Stream<String> stream = Files.lines( Paths.get( this.getArchivoFuente() ) );
        List<String> contenido = new ArrayList<>();

        contenido = stream
                .filter(line -> !line.isEmpty())
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        setContenidoArchivo(contenido);
    }

    public   List<String> getContenidoArchivo() {
        return contenidoArchivo;
    }

    public   void setContenidoArchivo(List<String> contenidoArchivo) {
        this.contenidoArchivo = contenidoArchivo;
    }

    public   String getArchivoFuente() {
        return archivoFuente;
    }

    public   void setArchivoFuente(String archivoFuente) {
       this.archivoFuente = archivoFuente;
    }

} //fin clase CodigoFuente
