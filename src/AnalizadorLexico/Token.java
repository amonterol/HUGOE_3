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
public class Token {
    
    public enum Tipos {
        ENTERO, REAL, COMANDOHUGO,COMANDOLOGO, IDENTIFICADOR, NOMBREPROCEDIMIENTO,COMENTARIO, OPERADOR, DECLARACION, ASIGNACION, CORIZQ, CORDER, COLOR, DESCONOCIDO
    }
    
    private String nombre;
    private Tipos tipo;
    private int linea;
    private int posicion;

    public Token(String nombre, Tipos tipo, int linea, int posicion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.linea = linea;
        this.posicion = posicion;
    }

    public Token() {
        
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Tipos getTipo() {
        return tipo;
    }

    public void setTipo(Tipos tipo) {
        this.tipo = tipo;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
    @Override
    public String toString() {
        return "Token:" + this.nombre + " " + this.tipo + " " +
                + this.linea + " " + this.posicion;
    }
   
}
