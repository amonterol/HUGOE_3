/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorSintactico;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 *
 * @author PC
 */
public class ArchivoSalida {

    //FileWriter archivo = null;
    String ruta = "C:\\Users\\pc\\Desktop\\hexagono3-Hugo-Errrores.txt";

    //Creamos el archivo de salida con los errores y el programa en HUGO
    public void crearArchivoSalida(List<String> s) {
       Path path = Paths.get(ruta);
      try (BufferedWriter br = Files.newBufferedWriter(path,
            Charset.defaultCharset(), StandardOpenOption.CREATE)) {
         for (String line : s) {
            br.write(line);
            br.newLine();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
}



}
