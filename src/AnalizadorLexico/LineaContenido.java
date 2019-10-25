/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorLexico;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class LineaContenido {

    private int linea;
    private String instruccion;
    private List<MiError> erroresEncontrados;

    public LineaContenido(int linea, String instruccion, List<MiError> erroresEncontrados) {
        this.linea = linea;
        this.instruccion = instruccion;
        this.erroresEncontrados = erroresEncontrados;
    }
    public LineaContenido(int linea, String instruccion) {
        this.linea = linea;
        this.instruccion = instruccion;
    }

    public LineaContenido() {
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruccion) {
        this.instruccion = instruccion;
    }

    public List<MiError> getErroresEncontrados() {
        return erroresEncontrados;
    }

    public void setErroresEncontrados(List<MiError> erroresEncontrados) {
        this.erroresEncontrados = erroresEncontrados;
    }
   
    
 @Override
    public String toString() {
        return "Token:" + this.linea + " " + this.instruccion + " " + this.erroresEncontrados;
                
    }

}
