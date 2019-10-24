/*
Los “parsers” toman cada token, encuentran información sintáctica, 
y construyen un objeto llamado “Árbol de Sintaxis Abstracta”. Imagina que un ASA
es como un mapa para nuestro código 
— una forma de entender cómo es la estructura de cada pedazo de código.
 */
package AnalizadorSintactico;

import AnalizadorLexico.*;
import AnalizadorLexico.Comando;
import AnalizadorLexico.Token.Tipos;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static java.util.Objects.isNull;

/**
 *
 * @author PC
 */
public class AnalizadorSintactico {

    private List<Token> listaTokens;
    private List<MiError> listaErrores;
    private List<LineaContenido> listaContenidoFinal;

    public AnalizadorSintactico(AnalizadorLexico lexico) {
        this.listaTokens = lexico.getAuxTokens();
        this.listaErrores = lexico.getListaErrores();
        this.listaContenidoFinal = lexico.getListaContenidoFinal();
    }

    public List<LineaContenido> sintactico() {
        System.out.println("ENTRAMOS AL SINTACTICO" + "\n" + "\n");

        List<Token> auxTokens = new ArrayList<>();
        System.out.println("111111-AS- INICIA  LISTA DE TOKENS****");
        listaTokens.forEach(item -> System.out.println(item.getNombre() + " <> " + item.getTipo() + " <> " + item.getLinea() + "<>" + item.getPosicion()));
        System.out.println("111111-AS- FINALIZA LISTA DE TOKENS" + "\n");

        System.out.println("22222-AS- INICIA  LISTA CONTENIDO FINAL****");
        listaContenidoFinal.forEach(item -> System.out.println(item.getLinea() + " <> " + item.getInstruccion() + " <> " + item.getErroresEncontrados()));
        System.out.println("22222-AS- FINALIZA LISTA DE CONTENIDO FINAL" + "\n");
        //List<Token> listaTokens = listaTok;
        Comando cmd = new Comando();
        List<Token> nuevaListaTokens = new ArrayList<>();
        nuevaListaTokens = listaTokens;
        ArrayList<String> variablesDeclaradas = new ArrayList<>();

        if (!listaTokens.isEmpty()) {
            System.out.println("33333-AS-Entramos al if");
            //Revisamos si el tamano del archivo supera al maximo permitido
            if (listaTokens.size() > 999) {
                System.out.println(" ERROR 101: el numero de líneas del programa excede la cantidad máxima permitida");
            }
            //INICIA TODO NUEVO
            //Recorrer listaTokens buscando PARA sino aparace -> error
            //Recorrer listaTokens buscando FIN sino aparece -> error
            Iterator iter = nuevaListaTokens.iterator();
            int m = 0;
            boolean existeFin = false;
            boolean existePara = false;
            boolean existeVariableDeclarada = false;
            int posicionPara = 0;
            int posicionFin = 0;
            while (!nuevaListaTokens.isEmpty()) {
                System.out.println("*****EL TAMANIO DEL LA LISTA DE NUEVALISTATOKENSS ES " + nuevaListaTokens.size());
                System.out.println("*****EL TAMANIO DEL LA LISTA DE LISTATOKENS ES " + listaTokens.size());
                Token tknActual = nuevaListaTokens.remove(0);
                System.out.println("wwwwwwwww-AS-EL VALOR TOKENACTUAL ES ES> " + tknActual.getNombre());
                System.out.println("wwwwwwwww-AS-EL TIPO DEL TOKENACTUAL ES ES> " + tknActual.getTipo());
                Token tknSigte = new Token();
                MiError e;
                List<MiError> erroresEncontrados = new ArrayList<>();
                LineaContenido nuevoContenido = new LineaContenido();
                int linea;
                //DEBO VERIFICAR LA EXISTENCIA DE AMBAS PALABRAS Y EN SUS POSICIONES CORRECTAS

                switch (tknActual.getTipo().toString().trim()) {
                    case "COMANDOHUGO":
                        System.out.println("vvvvvvvvvv-AS-ENTRAMOS A CASE OF COMANDO> " + tknActual.getNombre());
                        switch (tknActual.getNombre()) {
                            case "PARA":
                                System.out.println("xxxxxxxx-AS-ESTAMOS EN PARA");
                                //El primer comando debe ser PARA 

                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());

                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

                                if (m != 0) {
                                    e = new MiError(linea, " ERROR 140: el programa debe iniciar con el comando PARA");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                } else {
                                    //Token siguiente esperado debe ser tipo IDENTIFICADOR 
                                    if (!nuevaListaTokens.isEmpty()) {
                                        tknSigte = nuevaListaTokens.get(0);
                                        if (tknSigte.getLinea() == tknActual.getLinea()) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            if (!tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                e = new MiError(linea, " ERROR 141: el nombre del programa debe ser un identificador valido");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            }
                                        }
                                    }

                                }
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                break;
                            case "AVANZA":
                            case "AV":
                                System.out.println("xxxxxxxx-AS-ESTAMOS EN AVANZA");
                                //Token siguiente esperado debe ser tipo IDENTIFICADOR 
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                if (existeFin) {
                                    e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);

                                } else {
                                    if (!nuevaListaTokens.isEmpty()) {
                                        tknSigte = nuevaListaTokens.get(0);
                                        if (tknSigte.getLinea() == tknActual.getLinea()) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            if (!tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                if (!tknActual.getTipo().equals(Tipos.ENTERO)) {
                                                    e = new MiError(linea, "Error 111: Se espera un numero entero");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                }
                                            }
                                        } else {
                                            e = new MiError(linea, " Error 112: Se necesita un argumento entero para esta funcion");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        }
                                    }
                                }
                                break;
                            case "GIRADERECHA":
                            case "GD":
                                System.out.println("xxxxxxxx-AS-ESTAMOS EN GIRADERECHA");
                                //Token siguiente esperado debe ser tipo IDENTIFICADOR 
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                if (existeFin) {
                                    e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);

                                } else {
                                    if (!nuevaListaTokens.isEmpty()) {
                                        tknSigte = nuevaListaTokens.get(0);
                                        System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKESIQUIENTE ES> " + tknSigte.getLinea());
                                        if (tknSigte.getLinea() == tknActual.getLinea()) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            if (!tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                if (!tknActual.getTipo().equals(Tipos.ENTERO)) {
                                                    System.out.println("zzzzzzzzzzzzz-AS-ENCONTRAMOS ERROR> " + tknActual.getNombre() + tknActual.getLinea());
                                                    e = new MiError(linea, "Error 111: Se espera un numero entero");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                }
                                            }
                                        } else {
                                            e = new MiError(linea, " Error 112: Se necesita un argumento entero para esta funcion");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        }
                                    }
                                }
                                break;
                            case "GIRAIZQUIERDA":
                            case "GI":
                                //Token siguiente esperado debe ser tipo IDENTIFICADOR 
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                if (existeFin) {
                                    e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);

                                } else {
                                    if (!nuevaListaTokens.isEmpty()) {
                                        tknSigte = nuevaListaTokens.get(0);
                                        if (tknSigte.getLinea() == tknActual.getLinea()) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            if (!tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                if (!tknActual.getTipo().equals(Tipos.ENTERO)) {
                                                    e = new MiError(linea, "Error 111: Se espera un numero entero");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                }
                                            }
                                        } else {
                                            e = new MiError(linea, " Error 112: Se necesita un argumento entero para esta funcion");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        }
                                    }
                                }
                                break;

                            case "FIN":
                                //El ultimo comando debe ser FIN
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                existeFin = true;
                                //Revisamos si comando FIN tiene algun argumento o sino es el ultimo comando 
                                if (!nuevaListaTokens.isEmpty()) {
                                    tknActual = nuevaListaTokens.remove(0);
                                    System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                    //Verificamos si hay un token en la misma linea de FIN
                                    if (tknSigte.getLinea() == tknActual.getLinea()) {
                                        e = new MiError(linea, " ERROR 111: la funcion no admite argumentos");
                                        erroresEncontrados.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        System.out.println("zzzzzzzzzzzzz-AS-SE ENCONTRO UN ERROR> " + e.getError());
                                    } else {
                                        //Si el token esta en otra linea es porque hay una instruccion adicional y el 
                                        //programa no esta terminando con FIN
                                        e = new MiError(linea, " ERROR 142: el programa debe finalizar con el comando FIN");
                                        erroresEncontrados.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + e.getError());
                                    }

                                }

                                break;

                            case "HAZ":
                                //Token siguiente esperado debe ser tipo IDENTIFICADOR 
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                if (existeFin) {
                                    System.out.println("yyyyyyyyyyyyy-AS-EL VALOR de EXISTE FIN> " + existeFin);
                                    e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                } else {
                                    if (!nuevaListaTokens.isEmpty()) {
                                        tknSigte = nuevaListaTokens.get(0);
                                        if (tknSigte.getLinea() == tknActual.getLinea()) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("yyyyyyyyyyyyy-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (!tknActual.getTipo().equals(Tipos.DECLARACION)) {
                                                e = new MiError(linea, " ERROR 119: falta el operador de declaracion de variables");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            }
                                        }
                                        tknSigte = nuevaListaTokens.get(0);

                                        if (tknSigte.getLinea() == tknActual.getLinea()) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("yyyyyyyyyyyyy-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (!tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                e = new MiError(linea, " ERROR 110: falta el nombre de un identificador valido");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                System.out.println("yyyyyyyyyyyyy-AS-HAYAMO UN ERROR1> ");
                                            } else {
                                                existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                if (existeVariableDeclarada) {
                                                    e = new MiError(linea, " ERROR 122: la variable ya fue declarada con anterioridad");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("yyyyyyyyyyyyy-AS-HAYAMO UN ERROR2> ");
                                                } else {
                                                    variablesDeclaradas.add(tknActual.getNombre());
                                                }
                                            }
                                        }
                                        tknSigte = nuevaListaTokens.get(0);
                                        if (tknSigte.getLinea() == tknActual.getLinea()) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("yyyyyyyyyyyyy-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (!tknActual.getTipo().equals(Tipos.ASIGNACION)) {
                                                e = new MiError(linea, " ERROR 134: falta el operador de asignacion");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                System.out.println("yyyyyyyyyyyyy-AS-HAYAMO UN ERROR3> ");
                                            }
                                        }
                                        tknSigte = nuevaListaTokens.get(0);
                                        if (tknSigte.getLinea() == tknActual.getLinea()) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("yyyyyyyyyyyyy-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (!tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                if (!tknActual.getTipo().equals(Tipos.ENTERO)) {
                                                    e = new MiError(linea, " ERROR 130: falta el valor para asignar a la variable declarada");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("yyyyyyyyyyyyy-AS-HAYAMO UN ERROR4> ");
                                                }
                                            } else {
                                                existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                if (!existeVariableDeclarada) {
                                                    e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("yyyyyyyyyyyyy-AS-HAYAMO UN ERROR5> ");
                                                }
                                            }

                                        } else {
                                            e = new MiError(linea, " ERROR 130: falta el valor para asignar a la variable declarada");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            System.out.println("yyyyyyyyyyyyy-AS-HAYAMO UN ERROR7> ");
                                        }
                                    }
                                }

                                break;
                            case "IDENTIFICADOR":
                              
                                break;
                            case "ENTERO":
                             
                                break;
                            case "REAL":
                              
                                break;
                            case "DESCONOCIDO":
                             
                                break;
                            default:
                                break;
                        }
                    case "IDENTIFICADOR":
                        break;
                    default:
                        break;
                }

            }//fin del while

            //FINAL TODO NUEVO 
        }
        System.out.println("22222-AS- INICIA  LISTA CONTENIDO FINAL****");
        listaContenidoFinal.forEach(item -> System.out.println(item.getLinea() + " <> " + item.getInstruccion() + " <> " + item.getErroresEncontrados()));
        System.out.println("22222-AS- FINALIZA LISTA DE CONTENIDO FINAL" + "\n");
        return listaContenidoFinal;
    } //FIN DEL NUEVO SINTACTICO

    public LineaContenido buscarInstruccion(Token tknActual) {

        int linea = tknActual.getLinea();
        System.out.println("zzzzzzzzzzzzz-BUSCARINSTRUCCIONES-EL VALOR DE LINEA ES> " + linea);
        LineaContenido nuevoContenido = new LineaContenido();
        List<LineaContenido> contenidoFinal = this.getListaContenidoFinal();
        for (int i = 0; i < contenidoFinal.size(); ++i) {
            if (contenidoFinal.get(i).getLinea() == linea) {
                nuevoContenido = contenidoFinal.get(i);
                System.out.println("yyyyyyyyyyyyy-BUSCARINSTRUCIONES-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                break;
            }
        }
        return nuevoContenido;
    }

    //Funcion para verificar si una variable ya fue declarada previamente
    //Devuelve true si encuentra que la cantidad de tokens con el nombre de la 
    //variable en mayor que 1
    public boolean consultaVariablesDeclaradas(String variable, int linea, ArrayList<String> variablesDeclaradas) {
        boolean existe = false;
        int cantidad = 0;
        for (String var : variablesDeclaradas) {
            if (var.equals(variable)) {
                ++cantidad;
            }
        }
        System.out.println("bbbbbbb55555-AS CANTIDAD ES IGUAL A " + cantidad + " para la variable " + variable);
        return existe = cantidad > 0; //Encontro una variable declarada previamente
    }

    public List<Token> getListaTokens() {
        return listaTokens;
    }

    public void setListaTokens(List<Token> listaTokens) {
        this.listaTokens = listaTokens;
    }

    public List<MiError> getListaErrores() {
        return listaErrores;
    }

    public void setListaErrores(List<MiError> listaErrores) {
        this.listaErrores = listaErrores;
    }

    public List<LineaContenido> getListaContenidoFinal() {
        return listaContenidoFinal;
    }

    public void setListaContenidoFinal(List<LineaContenido> listaContenidoFinal) {
        this.listaContenidoFinal = listaContenidoFinal;
    }

} //FIN DE Analizador Sintactico

