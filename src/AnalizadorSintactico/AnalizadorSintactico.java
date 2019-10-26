/*
Los “parsers” toman cada token, encuentran información sintáctica, 
y construyen un objeto llamado “Árbol de Sintaxis Abstracta”. Imagina que un ASA
es como un mapa para nuestro código 
— una forma de entender cómo es la estructura de cada pedazo de código.
 */
package AnalizadorSintactico;

import AnalizadorLexico.*;
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

            int m = 0;
            int lineaTknRepite = -1;
            boolean existeFin = false;
            boolean posicionFin = posicionComandoFin();

            boolean existePara = false;
            boolean existeVariableDeclarada = false;
            int posicionPara = 0;

            boolean estamosEnRepite = false;
            boolean existeCorIzqEnRepite = false;
            boolean existeCorDerEnRepite = false;
            boolean existeListaComandosEnRepite = false;
            boolean existePonColorRelleno = false;
            //Verificamos que el ultimo comando del programa se FIN
            posicionFin = posicionComandoFin();
            List<MiError> erroresEncontrados = new ArrayList<>();
            List<MiError> erroresEncontradosEnRepite = new ArrayList<>();
            int linea = 0;
            MiError e;
            LineaContenido nuevoContenido = null;
            while (!nuevaListaTokens.isEmpty()) {
                System.out.println("\n" + "\n" + "\n" + "******EL TAMANIO DEL LA LISTA DE NUEVALISTATOKENSS ES " + nuevaListaTokens.size());
                System.out.println("******EL TAMANIO DEL LA LISTA DE LISTATOKENS ES " + listaTokens.size() + "\n");
                //Removemos el primer token de la lista para aplicar tecnica FIFO -> ¿sera mejor usar una cola?

                Token tknActual = nuevaListaTokens.remove(0);

                System.out.println("******-AS-TENEMOS UN NUEVO TOKEN ACTUAL SU NOMBR ES -> " + tknActual.getNombre());
                System.out.println("******-AS-EL TIPO DEL TOKENACTUAL ES ES> " + tknActual.getTipo() + "\n");

                Token tknSigte = new Token();

                System.out.println("*******-AS- EL VALOR DE estamosEnRepite es->S> " + estamosEnRepite);

                if (tknActual.getNombre().equals("REPITE")) {
                    lineaTknRepite = tknActual.getLinea();
                }

                System.out.println("*******-AS- EL VALOR DE estamosEnRepite es->S> " + estamosEnRepite + "\n");

                estamosEnRepite = tknActual.getLinea() == lineaTknRepite;

                if (estamosEnRepite) {
                    erroresEncontrados = erroresEncontradosEnRepite;

                    System.out.println("*******-AS- ERRORES ENCONTRADOS EN IF estamosEnRepite es->S> " + erroresEncontrados + "\n");

                } else {
                    erroresEncontrados = new ArrayList<>();

                    System.out.println("*******-AS- ERRORES ENCONTRADOS EN ELSE estamosEnRepite es->S> " + erroresEncontrados + "\n");
                }

                //DEBO VERIFICAR LA EXISTENCIA DE AMBAS PALABRAS Y EN SUS POSICIONES CORRECTAS
                switch (tknActual.getTipo().toString().trim()) {

                    case "COMANDOHUGO":
                        System.out.println("\n" + "\n" + "ssssss-AS-ENTRAMOS A CASE OF COMANDO> " + tknActual.getNombre());
                        OUTER:
                        switch (tknActual.getNombre()) {
                            case "PARA":
                                System.out.println("xxxxxxxx-AS-ESTAMOS EN PARA");
                                //El primer comando debe ser PARA 
                                erroresEncontrados = new ArrayList<MiError>();
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea() + "\n");

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

                            case "FIN":
                                //El ultimo comando debe ser FIN
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                existeFin = true;
                                erroresEncontrados = new ArrayList<MiError>();
                                if (!posicionFin) {
                                    //Si FIN esta en otra linea que no se la ultima programa no esta terminando con FIN
                                    e = new MiError(linea, " ERROR 142: el programa debe finalizar con el comando FIN");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + e.getError());
                                }

                                //Revisamos si comando FIN tiene algun argumento 
                                if (!nuevaListaTokens.isEmpty()) {
                                    tknActual = nuevaListaTokens.remove(0);
                                    System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                    //Verificamos si hay un token en la misma linea de FIN
                                    if (tknSigte.getLinea() == tknActual.getLinea()) {
                                        e = new MiError(linea, " ERROR 111: la funcion no admite argumentos");
                                        erroresEncontrados.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        System.out.println("zzzzzzzzzzzzz-AS-SE ENCONTRO UN ERROR> " + e.getError());
                                    }
                                }
                                break;
                            case "BORRAPANTALLA":
                            case "BP":
                            case "SUBELAPIZ":
                            case "SL":
                            case "BAJALAPIZ":
                            case "BL":
                            case "GOMA":
                            case "CENTRO":
                            case "OCULTATORTUGA":
                            case "OT":
                            case "MUESTRATORTUGA":
                            case "MT":
                            case "PONLAPIZ":
                            case "LAPIZNORMAL":

                                System.out.println("gigigi-AS-ESTAMOS EN GIRADERECHA ANTES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO");
                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO ANTES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO ->> " + nuevoContenido.getInstruccion());
                                nuevoContenido = casoComandoSinArgumento(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin);
                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO DESPUES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO ->> " + nuevoContenido);
                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO DESPUES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO ->> " + nuevoContenido.getErroresEncontrados());
                                break;

                            case "AVANZA":
                            case "AV":
                            case "GIRADERECHA":
                            case "GD":
                            case "GIRAIZQUIERDA":
                            case "GI":
                            case "RETROCEDE":
                            case "RE":
                                System.out.println("gigigi-AS-ESTAMOS EN GIRADERECHA ANTES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO");
                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO ANTES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO ->> " + nuevoContenido.getInstruccion());
                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO CONTIENE LOS SIGUIENTE ERRORES->> " + nuevoContenido.getErroresEncontrados());

                                nuevoContenido = casoComandoConArgumentoEntero(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin, variablesDeclaradas);

                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO DESPUES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO ->> " + nuevoContenido);
                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO DESPUES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO ->> " + nuevoContenido.getErroresEncontrados());
                                break;
                            case "PONCOLORLAPIZ":
                            case "PONCL":
                                casoPonColorLapiz(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin);
                                break;
                            case "PONCOLORRELLENO":
                                existePonColorRelleno = true;
                                nuevoContenido = casoPonColorRelleno(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin);
                                break;
                            case "RELLENA":
                                nuevoContenido = casoComandoRellena(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin, existePonColorRelleno);
                                break;
                            case "HAZ":
                                System.out.println("hhhhhh-AS-ESTAMOS EN HAZ 1> ");
                                //Token esperado debe ser tipo OPERADOR DE DECLARACION DE VARIABLE (")
                                System.out.println("hhhhhh-AS-EL TOKEN ACTUAL ES 2-> " + tknActual.toString());
                                linea = tknActual.getLinea();
                                System.out.println("hhhhhh-AS-EL VALOR DE LINEA ACTUAL ES 3-> " + linea);
                                System.out.println("hhhhhh-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES 4 ->  " + tknActual.getLinea());
                                System.out.println("hhhhhh-AS-LOS ERRORES ANTES DE UBICAR EL CONTENIDO SON -> " + nuevoContenido.getErroresEncontrados());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("hhhhhh-AS-EL VALOR DE NUEVOCONTENIDO ES -> " + nuevoContenido.getInstruccion());
                                System.out.println("hhhhhh-AS-LOS ERRORES ENCONTRADOS LUEGO DE IR A LA FUNCION BUSCARiNSTRUCCION SON -> " + nuevoContenido.getErroresEncontrados());
                                String nuevaVariable = "";

                                if (estamosEnRepite) {
                                    e = new MiError(linea, " ERROR 150: la lista de comandos de REPITE no debe contener el comando HAZ");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                }

                                if (!posicionFin) {
                                    e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);

                                } else {
                                    if (!nuevaListaTokens.isEmpty()) {
                                        tknSigte = nuevaListaTokens.get(0);
                                        if (tknSigte.getLinea() == linea) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("hhhhhh-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (tknActual.getTipo().equals(Tipos.DECLARACION)) {
                                                //Encontramos el token esperado, por lo tanto solo lo aceptamos y seguimos adelante
                                            } else {
                                                //Como el token no coincide con el esperado entonces existe un error
                                                e = new MiError(linea, " ERROR 119: falta el operador de declaracion de variables ( \" )");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                System.out.println("hhhhhh-AS-ENCONTRAMOS UN ERRROR -> " + e.toString());
                                                System.out.println("hhhhhh-AS-LOS ERRORES ENCONTRADOS LUEGO DEL NUEVO ERROR -> " + nuevoContenido.getErroresEncontrados());
                                            }
                                        } else {
                                            e = new MiError(linea, " ERROR 153: la lista de argumentos esta incompleta, se require HAZ \"Nombre de la variable :Valor de la variable");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR3 -> " + e.toString());
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                            break;

                                        }

                                        //Token esperado debe ser tipo VARIABLE NO DECLARADA 
                                        tknSigte = nuevaListaTokens.get(0);
                                        System.out.println("hhhhhh-AS-EL VALOR tknSgte> " + tknSigte.getNombre());
                                        //Revisamos que sigamos en la misma linea
                                        if (tknSigte.getLinea() == linea) {
                                            // Como sigue siendo un argumento de HAZ lo removemos de la lista de tokens para analizarlo
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("hhhhhh-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                //Encontramos el token esperado, al ser un identificador, verificamos que no haya sido declarado antes
                                                existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                if (existeVariableDeclarada) {
                                                    //El identificador existe por lo tanto no puede ser usado nuevante, lanzamos un error
                                                    e = new MiError(linea, " ERROR 122: la variable fue definida previamente");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR2 -> " + e.toString());
                                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                } else {
                                                    //El identificador no existe en las variablesDeclaradas => es nuevaVariable
                                                    nuevaVariable = tknActual.getNombre();
                                                }
                                            } else {
                                                e = new MiError(linea, " ERROR 130: falta el valor para asignar a la variable declarada");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                System.out.println("hhhhhh-AS-HAYAMO UN ERROR1> ");
                                            }
                                        } else {
                                            e = new MiError(linea, " ERROR 153: la lista de argumentos esta incompleta, se require HAZ \"Nombre de la variable :Valor de la variable");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR3 -> " + e.toString());
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                            break;

                                        }

                                        //Token esperado debe ser tipo OPERADOR DE ASIGNACION (:)
                                        tknSigte = nuevaListaTokens.get(0);
                                        System.out.println("hhhhhh-AS-EL VALOR tknSgte> " + tknSigte.getNombre());
                                        //Revisamos que sigamos en la misma linea
                                        if (tknSigte.getLinea() == linea) {
                                            // Como sigue siendo un argumento de HAZ lo removemos de la lista de tokens para analizarlo
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("yyyyyyyyyyyyy-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (tknActual.getTipo().equals(Tipos.ASIGNACION)) {
                                                //Encontramos el token esperado, por lo tanto solo lo aceptamos y seguimos adelante
                                            } else {
                                                //Como el token no coincide con el esperado entonces existe un error
                                                e = new MiError(linea, " ERROR 134: falta el operador de asignacion de valor a variables (:) para completar la declaracion");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                System.out.println("hhhhhh-AS-ENCONTRAMOS UN ERRROR -> " + e.toString());
                                                System.out.println("hhhhhh-AS-LOS ERRORES ENCONTRADOS LUEGO DEL NUEVO ERROR -> " + nuevoContenido.getErroresEncontrados());
                                            }
                                        } else {
                                            e = new MiError(linea, " ERROR 153: la lista de argumentos esta incompleta, se require HAZ \"Nombre de la variable :Valor de la variable");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR3 -> " + e.toString());
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                            break;

                                        }

                                        //Token esperado debe ser tipo IDENTIFICADOR O ENTERO
                                        tknSigte = nuevaListaTokens.get(0);
                                        System.out.println("hhhhhh-AS-EL VALOR tknSgte> " + tknSigte.getNombre());
                                        //Revisamos que sigamos en la misma linea
                                        if (tknSigte.getLinea() == tknActual.getLinea()) {
                                            // Como sigue siendo un argumento de HAZ lo removemos de la lista de tokens para analizarlo
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("hhhhhh-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                //Encontramos el token esperado, al ser un identificador, verificamos que haya sido declarado antes
                                                existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                if (!existeVariableDeclarada) {
                                                    //El identificador no existe por lo tanto no puede ser usado => error
                                                    e = new MiError(linea, " ERROR 154: el valor a asignar a la variable debe ser un entero o una variable declarada previamente");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR1 -> " + e.toString());
                                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                } else {
                                                    //El identificador fue declarado previamente por lo tanto puede ser utilizado como valor de variable => declaramos la nuevaVariable
                                                    if (nuevaVariable.length() > 0) {
                                                        variablesDeclaradas.add(nuevaVariable);
                                                        System.out.println("hhhhhh-SE DECLARO UNA NUEVA VARIABLE -> " + variablesDeclaradas);
                                                    }
                                                }
                                            } else if (tknActual.getTipo().equals(Tipos.ENTERO)) {
                                                //Encontramos el token esperado, en este caso un entero, por lo tanto podemos declarar la nuevaVariable 
                                                if (nuevaVariable.length() > 0) {
                                                    variablesDeclaradas.add(nuevaVariable);
                                                }
                                                System.out.println("hhhhhh-SE DECLARO UNA NUEVA VARIABLE -> " + variablesDeclaradas);
                                            } else {
                                                e = new MiError(linea, " ERROR 130: falta el valor para asignar a la variable declarada");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR2 -> " + e.toString());
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                break;

                                            }
                                        }
                                    }
                                }
                                break;

                            case "REPITE":
                                linea = tknActual.getLinea();
                                System.out.println("rrrrrrr-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("rrrrrrr-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                erroresEncontradosEnRepite = new ArrayList<MiError>();
                                System.out.println("rrrrrr-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                estamosEnRepite = true;
                                if (!posicionFin) {
                                    e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                                    erroresEncontradosEnRepite.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);

                                } else {
                                    if (!nuevaListaTokens.isEmpty()) {
                                        //Vemos si el token siguiente esperamos un IDENTIFICADOR o ENTERO
                                        tknSigte = nuevaListaTokens.get(0);
                                        System.out.println("rrrrrr-AS-EL VALOR de tknSigte-> " + tknSigte.getNombre());
                                        //Comprobamos que sea un argumento de REPITE esperamos un IDENTIFICADOR o ENTERO
                                        if (tknSigte.getLinea() == linea) {
                                            //Es un argumento de la funcion asi que lo removemos para analizarlo
                                            tknActual = nuevaListaTokens.remove(0);
                                            switch (tknActual.getTipo()) {
                                                case IDENTIFICADOR:
                                                    //lo aceptamos ->  ¿ tknSigte = nuevaListaTokens.get(0); ?
                                                    //y revisamos si la variable fue declarada con anterioridad
                                                    existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                    if (!existeVariableDeclarada) {
                                                        e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                                        erroresEncontradosEnRepite.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR2 > " + e.toString());
                                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                    }
                                                    break;
                                                //lo aceptamos y vemos el siguiente argumento
                                                //¿ tknSigte = nuevaListaTokens.get(0); ?
                                                case ENTERO:
                                                    break;
                                                case REAL:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    System.out.println("rrrrrr-AS-HAYAMO UN ERROR2> ");
                                                    break;
                                                case COMANDOHUGO:
                                                case COMANDOLOGO:
                                                    e = new MiError(linea, " ERROR 145: los comandos solo estan permitidos dentro de los corchetes");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    System.out.println("rrrrrr-AS-HAYAMO UN ERROR3> ");
                                                    break;
                                                case DESCONOCIDO:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    System.out.println("rrrrrr-AS-HAYAMO UN ERROR4> ");
                                                    break;
                                                default:
                                                    e = new MiError(linea, " ERROR 144: falta el entero que indica en numero de repiticiones del comando");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    System.out.println("rrrrrr-AS-HAYAMO UN ERROR5> ");
                                                    break;
                                            }
                                        } else {
                                            e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                            erroresEncontradosEnRepite.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                            System.out.println("rrrrrr-AS-HAYAMO UN ERROR9> ");
                                            break;
                                        }
                                        //Vemos si el token siguiente esperamos un CORIZQ
                                        tknSigte = nuevaListaTokens.get(0);
                                        //Comprobamos que sea un argumento de REPITE esperamos un CORIZQ
                                        System.out.println("rrrrrr-AS-EL VALOR de tknSigte-> " + tknSigte.getNombre());
                                        if (tknSigte.getLinea() == linea) {
                                            //Es un argumento de la funcion asi que lo removemos para analizarlo
                                            //tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("rrrrrr-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (tknSigte.getTipo().equals(Tipos.CORIZQ)) {
                                                //es un CORIZQ -> lo aceptamos ->  ¿ tknSigte = nuevaListaTokens.get(0); ?
                                                //Comprobamos que sea un argumento de REPITE esperamos un CORIZQ
                                                existeCorIzqEnRepite = true;

                                            } else {
                                                existeCorIzqEnRepite = false;
                                                e = new MiError(linea, " ERROR 102: falta corchete izquierdo");
                                                erroresEncontradosEnRepite.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                System.out.println("rrrrrr-AS-HAYAMO UN ERROR7> ");

                                            }

                                        } else {
                                            e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                            erroresEncontradosEnRepite.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                            System.out.println("rrrrrr-AS-HAYAMO UN ERROR8> ");
                                            break;
                                        }

                                        //Vemos si el token siguiente esperamos un COMANDOHUGO
                                        tknSigte = nuevaListaTokens.get(1);
                                        //Comprobamos que sea un argumento de REPITE esperamos un COMANDOHUGO
                                        System.out.println("rrrrrr-AS-EL VALOR de tknSigte-> " + tknSigte.getNombre());
                                        if (tknSigte.getLinea() == linea) {
                                            //Es un argumento de la funcion asi que lo removemos para analizarlo
                                            //tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("rrrrrr-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (tknSigte.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                //es un CORIZQ -> lo aceptamos ->  ¿ tknSigte = nuevaListaTokens.get(0); ?
                                                //Comprobamos que sea un argumento de REPITE esperamos un CORIZQ
                                                System.out.println("rrrrrr-AS-TENEMOS UNA LISTA DE COMANDOS DE REPITE QUE COMIENZA CON COMANDOHUGO-> " + tknSigte.getNombre());
                                                existeListaComandosEnRepite = true;
                                                System.out.println("rrrrrr-AS- INICIA  LISTA DE NUEVALISTATOKENS RESTANTE");
                                                nuevaListaTokens.forEach(item -> System.out.println(item.getNombre() + " <> " + item.getTipo() + " <> " + item.getLinea() + "<>" + item.getPosicion()));
                                                System.out.println("rrrrrr-AS- FINALIZA LISTA DE NUEVALISTATOKENS RESTANTE " + "\n");
                                            } else if (tknSigte.getTipo().equals(Tipos.CORDER)) {
                                                e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                                erroresEncontradosEnRepite.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                System.out.println("rrrrrr-AS-HAYAMO UN ERROR9> ");

                                            } else if (!tknSigte.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                System.out.println("rrrrrr-AS- TENEMOS UNA LISTA DE COMANDOS DE REPITE QUE NO COMIENZA CON COMANDOHUGO-> " + tknSigte.getNombre());
                                                existeCorIzqEnRepite = false;
                                                e = new MiError(linea, " ERROR 131: la lista de comandos a repetir debe comenzar con un comando valido");
                                                erroresEncontradosEnRepite.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                System.out.println("rrrrrr-AS-HAYAMOS UN ERROR2-> " + e.toString());
                                                System.out.println("rrrrrr-AS-LA LISTA DE ERRORES DE ESTA LINEA SON->> " + nuevoContenido.getErroresEncontrados());
                                            }

                                        } else {
                                            e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                            erroresEncontradosEnRepite.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                            System.out.println("rrrrrr-AS-HAYAMO UN ERROR9> ");
                                            break;
                                        }

                                    }

                                }//Fin del else de existeFin
                                break;

                            default:
                                break;
                        }
                        break;

                    case "ENTERO":
                        System.out.println("[eeeeee-AS-ESTAMOS EN ENTERO->> " + tknActual.toString());
                        break;
                    case "REAL":

                        break;
                    case "DESCONOCIDO":
                        break;
                    case "COLOR":
                        //SE QUIRE MANEJAR LOS CASOS EN QUE APARECE UN COLOR VALIDO AL INICIO DE UNA INSTRUCCION
                        System.out.println("iiiiii-AS-ESTAMOS EN CASOCOLOR" + '\n' + tknActual.getNombre());

                        linea = tknActual.getLinea();

                        System.out.println("iiiiii-AS-EL VALOR DE LINEA ES 1 > " + linea);
                        System.out.println("iiiiii-AS-EL VALOR DE LINEA DEL tokenActual ES 2> " + tknActual.getLinea());

                        nuevoContenido = buscarInstruccion(tknActual);

                        System.out.println("iiiiii-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    e = new MiError(linea, " ERROR 156: un color valido solo pueden utilizarse como argumento de PONCOLORELLENO o PONCOLORLAPIZ");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                } else if (tknActual.getPosicion() == 0) {
                                    //Token es un identificador en el comienzo de una nueva linea 
                                    e = new MiError(linea, " ERROR 156: un color valido solo pueden utilizarse como argumento de PONCOLORELLENO o PONCOLORLAPIZ");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 6> " + e.toString());
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 7> " + nuevoContenido.getErroresEncontrados());
                                    //Vemos si el token siguiente NO esperamos ningun otro token
                                    tknSigte = nuevaListaTokens.get(0);
                                    //Comprobamos que este en la misma linea que el identificador encontrado al inicio de una instruccion
                                    if (tknSigte.getLinea() == linea) {
                                        //Problema hay mas tokens en la misma linea => NUEVO ERROR
                                        tknActual = nuevaListaTokens.remove(0);
                                        //Como existe un nuevo token en la misma linea  removemos para analizarlo
                                        if (tknActual.getNombre().equals("PONCOLORRELLENO") || tknActual.getNombre().equals("PONCOLORLPAIZ")) {
                                            e = new MiError(linea, " ERROR 156: el comando debe ser la primera palabra de toda instruccion");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            System.out.println("iiiiii-AS-HAYAMOS UN ERROR 8> " + e.toString());
                                            System.out.println("iiiiii-AS-HAYAMOS UN ERROR 9> " + nuevoContenido.getErroresEncontrados());
                                        }
                                    }

                                }
                            }
                        }
                        break;
                    case "IDENTIFICADOR":
                        //SE QUIERE MANEJAR LOS CASOS EN QUE APARECE UN IDENTIFICADOR VALIDO AL INICIO DE UNA INSTRUCCION 
                        //O UN IDENTIFICADOR SIN ESTAR ASOCIADO A UN COMANDO EN REPITE
                        System.out.println("iiiiii-AS-ESTAMOS EN CASOIDENTIFICADOR" + '\n' + tknActual.getNombre());

                        linea = tknActual.getLinea();

                        System.out.println("iiiiii-AS-EL VALOR DE LINEA ES 1 > " + linea);
                        System.out.println("iiiiii-AS-EL VALOR DE LINEA DEL tokenActual ES 2> " + tknActual.getLinea());

                        nuevoContenido = buscarInstruccion(tknActual);

                        System.out.println("iiiiii-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    e = new MiError(linea, " ERROR 151: toda identificador o variable debe ser el argumento de un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                } else if (tknActual.getPosicion() == 0) {
                                    //Token es un identificador en el comienzo de una nueva linea 
                                    e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 6> " + e.toString());
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 7> " + nuevoContenido.getErroresEncontrados());
                                    //Vemos si el token siguiente NO esperamos ningun otro token
                                    tknSigte = nuevaListaTokens.get(0);
                                    //Comprobamos que este en la misma linea que el identificador encontrado al inicio de una instruccion
                                    if (tknSigte.getLinea() == linea) {
                                        //Problema hay mas tokens en la misma linea => NUEVO ERROR
                                        tknActual = nuevaListaTokens.remove(0);
                                        //Como existe un nuevo token en la misma linea  removemos para analizarlo
                                        if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                            e = new MiError(linea, " ERROR 156: el comando debe estar al inicio de la linea");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            System.out.println("iiiiii-AS-HAYAMOS UN ERROR 8> " + e.toString());
                                            System.out.println("iiiiii-AS-HAYAMOS UN ERROR 9> " + nuevoContenido.getErroresEncontrados());
                                        }
                                    }

                                }
                            }
                        }
                        break;

                    case "CORIZQ":
                        System.out.println("[[[[[[-AS-ESTAMOS EN [->> " + tknActual.toString());
                        linea = tknActual.getLinea();
                        System.out.println("[[[[[[-AS-EL VALOR DE LINEA ES> " + linea);
                        System.out.println("[[[[[[-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());

                        nuevoContenido = buscarInstruccion(tknActual);

                        //System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " +  nuevoContenido.getErroresEncontrados().size();
                        //erroresEncontrados = nuevoContenido.getErroresEncontrados();
                        System.out.println("[[[[[[-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion() + " ubicados en la linea " + linea);
                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);

                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                //Primero verificamos estar dentro del comando REPITE
                                if (estamosEnRepite) {
                                    //Buscamos la existencia del CORDER en la misma linea del CORIZQ
                                    System.out.println("[[[[[[-AS-ESTAMOS EN CORIZQ DENTRO DE  IF ESTAMOSENREPITE> " + estamosEnRepite);
                                    System.out.println("[[[[[[-AS-EL TAMANO RESTANTE DE LA nuevaListaTokens es -> " + nuevaListaTokens.size());
                                    Token tok = new Token();
                                    for (int i = 0; i < nuevaListaTokens.size(); ++i) {
                                        tok = nuevaListaTokens.get(i);
                                        if (tok.getLinea() == linea) {
                                            if (tok.getTipo().equals(Tipos.CORDER)) {
                                                existeCorDerEnRepite = true;
                                                System.out.println("[[[[[-AS-ENCONTRAMOS EL CORDER EN LA MISMA LINEA 0-> " + existeCorDerEnRepite);
                                                break;
                                            } else {
                                                existeCorDerEnRepite = false;
                                            }
                                        } else {
                                            //El CORDER no estaba en la misma linea del CORIZQ
                                            existeCorDerEnRepite = false;
                                            break;
                                        }
                                        System.out.println("[[[[[-AS-NO ENCONTRAMOE EL CORDER 1-> " + existeCorDerEnRepite);
                                        System.out.println("[[[[[-AS-EL VALOR DE EXISTECORDERENREPITE ES 2-> " + tok.toString());
                                    }

                                    System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido 1> " + nuevoContenido.getErroresEncontrados());

                                    //nuevoContenido.getErroresEncontrados().forEach( item ->System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " + item )); 
                                    System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " + nuevoContenido.getInstruccion());

                                    if (!existeCorDerEnRepite) {
                                        existeCorDerEnRepite = false;
                                        e = new MiError(linea, " ERROR 103: falta corchete derecho");
                                        erroresEncontrados.add(e);
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido > " + nuevoContenido.getErroresEncontrados());
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido 3> " + nuevoContenido.toString());

                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 falta corchete derecho> " + e.toString());
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido 4> " + nuevoContenido.getErroresEncontrados());
                                    }

                                } else {
                                    System.out.println("[[[[[[-AS-EL VALOR de EXISTE FIN> " + existeFin);
                                    e = new MiError(linea, " ERROR 147: esta version solo acepta corchetes en el comando REPITE");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    System.out.println("[[[[[[-AS-HAYAMO UN ERROR3> ");
                                }
                            }

                        }//Fin del else de existeFin

                        break;
                    case "CORDER":
                        System.out.println("]]]]]]-AS-ESTAMOS EN ]->> ");
                        linea = tknActual.getLinea();
                        System.out.println("]]]]]]-AS-EL VALOR DE LINEA ES> " + linea);
                        System.out.println("]]]]]]-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                        nuevoContenido = buscarInstruccion(tknActual);
                        //erroresEncontrados = nuevoContenido.getErroresEncontrados();
                        System.out.println("]]]]]]-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                        /*
                        if (!estamosEnRepite) {
                            erroresEncontrados = new ArrayList<MiError>();
                        }
                         */
                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existeCorDerEnRepite = false;
                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    estamosEnRepite = false;
                                } else {
                                    System.out.println("rrrrrr-AS-EL VALOR de EXISTE FIN> " + existeFin);
                                    e = new MiError(linea, " ERROR 147: esta version solo acepta corchetes en el comando REPITE");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2> ");
                                    estamosEnRepite = false;
                                }
                            }

                        }//Fin del else de existeFin

                        break;
                    default:
                        break;
                }

            }//fin del while

            //FINAL TODO NUEVO 
        }
        System.out.println(
                "22222-AS- INICIA  LISTA CONTENIDO FINAL****");

        for (int i = 0;
                i < listaContenidoFinal.size();
                ++i) {

            if (listaContenidoFinal.get(i).getErroresEncontrados() == null) {
                System.out.println(listaContenidoFinal.get(i).getLinea() + " <> " + listaContenidoFinal.get(i).getInstruccion());
            } else {
                System.out.println(listaContenidoFinal.get(i).getLinea() + " <> " + listaContenidoFinal.get(i).getInstruccion());
                for (int k = 0; k < listaContenidoFinal.get(i).getErroresEncontrados().size(); ++k) {
                    System.out.println(
                            "\t" + listaContenidoFinal.get(i).getErroresEncontrados().get(k).getError());
                    //break;
                }
            }
        }

        System.out.println(
                "22222-AS- FINALIZA LISTA DE CONTENIDO FINAL" + "\n");
        return listaContenidoFinal;
    } //FIN DEL NUEVO SINTACTICO

    public LineaContenido casoComandoConArgumentoEntero(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin, ArrayList<String> variablesDeclaradas) {
        System.out.println("cCcAE-AS-ESTAMOS DENRO FUNCION casoComandoConArgumentoEntero con el token->" + tknActual.toString());
        //Token siguiente esperado debe ser tipo IDENTIFICADOR 

        int linea = tknActual.getLinea();
        boolean existeVariableDeclarada = false;

        System.out.println("cCcAE-AS-EL VALOR DE LINEA ES> " + linea);
        System.out.println("cCcAE-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
        //erroresEncontrados = new ArrayList<MiError>();

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        System.out.println("cCcAE-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
        /*
                                if (!estamosEnRepite) {
                                    erroresEncontrados = new ArrayList<MiError>();
                                }
         */
        if (!posicionFin) {
            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
            erroresEncontrados.add(e);
            nuevoContenido.setErroresEncontrados(erroresEncontrados);

        } else {
            if (!nuevaListaTokens.isEmpty()) {
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("cCcAE-AS-EL TOKEN SIGUIENTE ES -> " + tknSigte.toString());
                System.out.println("cCcAE-AS-EL VALOR DE LINEA DEL TOKESIQUIENTE ES> " + tknSigte.getLinea());
                if (tknSigte.getLinea() == tknActual.getLinea()) {
                    tknActual = nuevaListaTokens.remove(0);
                    System.out.println("cCcAE-AS-EL NUEVO TOKEN ACTUAL ES -> " + tknActual.toString());

                    if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                        existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                        if (!existeVariableDeclarada) {
                            e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            System.out.println("cCcAE-AS-HAYAMO UN ERROR1> ");
                            System.out.println("cCcAE-AS-HAYAMOS UN ERROR2 falta corchete derecho> " + e.toString());
                            System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                        }
                    } else if (tknActual.getTipo().equals(Tipos.ENTERO)) {
                        //Solo lo aceptamos y seguimos adelante
                    } else {
                        System.out.println("cCcAE-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                        e = new MiError(linea, " Error 112: se require un argumento entero o una variable declarada previamente para este comando");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        System.out.println("cCcAE-AS-HAYAMOS UN ERROR2 falta de argumento entero> " + e.toString());
                        System.out.println("cCcAE-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());

                    }
                } else {
                    e = new MiError(linea, " Error 112: se require un argumento entero o una variable declarada previamente para este comando");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                }
            }
        }
        return nuevoContenido;

    }

    public LineaContenido casoComandoSinArgumento(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin) {

        System.out.println("ccsa-AS-ESTAMOS DENRO FUNCION casoComandoSinArgumento con el token->" + tknActual.toString());
        //Token siguiente esperado -> NINGUNO 

        int linea = tknActual.getLinea();

        System.out.println("ccsa-AS-EL VALOR DE LINEA ES> " + linea);
        System.out.println("ccsa-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
        //erroresEncontrados = new ArrayList<MiError>();

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        System.out.println("ccsa-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

        if (!posicionFin) {
            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
            erroresEncontrados.add(e);
            nuevoContenido.setErroresEncontrados(erroresEncontrados);

        } else {
            if (!nuevaListaTokens.isEmpty()) {
                //Token siguiente esperado -> NINGUNO 
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("ccsa-AS-EL TOKEN SIGUIENTE ES -> " + tknSigte.toString());
                System.out.println("ccsa-AS-EL VALOR DE LINEA DEL TOKESIQUIENTE ES> " + tknSigte.getLinea());
                //Revisamos si sigamos en la misma linea
                if (tknSigte.getLinea() == linea) {
                    //Como existe un nuevo token en la misma linea del comando lo removemos para analizarlo
                    tknActual = nuevaListaTokens.remove(0);
                    //La funcion no admite argumentos, por lo tanto, sin importar el token que siga => error
                    System.out.println("ccsa-AS-EL NUEVO TOKEN ACTUAL ES -> " + tknActual.toString());
                    e = new MiError(linea, " Error 111: este comando no admite argumentos");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    System.out.println("ccsa-AS-HAYAMOS UN ERROR3 -> " + e.toString());
                    System.out.println("ccsa-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());

                }
            }
        }
        return nuevoContenido;

    }

    public LineaContenido casoComandoRellena(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin, boolean existePonColorRelleno) {

        System.out.println("rellena-AS-ESTAMOS DENTRO FUNCION casoRellena con el token->" + tknActual.toString());
        //Token siguiente esperado -> NINGUNO 

        int linea = tknActual.getLinea();

        System.out.println("rellena-AS-EL VALOR DE LINEA ES> " + linea);
        System.out.println("rellena-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);
        System.out.println("rellena-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

        MiError e = new MiError();
        Token tknSigte = new Token();

        if (!posicionFin) {
            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
            erroresEncontrados.add(e);
            nuevoContenido.setErroresEncontrados(erroresEncontrados);

        } else {
            if (!nuevaListaTokens.isEmpty()) {
                //Token siguiente esperado -> NINGUNO 
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("rellena-AS-EL TOKEN SIGUIENTE ES -> " + tknSigte.toString());
                System.out.println("rellena-AS-EL VALOR DE LINEA DEL TOKESIQUIENTE ES> " + tknSigte.getLinea());
                //Revisamos si sigamos en la misma linea
                if (tknSigte.getLinea() == linea) {
                    //Como existe un nuevo token en la misma linea del comando lo removemos para analizarlo
                    tknActual = nuevaListaTokens.remove(0);
                    //La funcion no admite argumentos, por lo tanto, sin importar el token que siga => error
                    System.out.println("rellena-AS-EL NUEVO TOKEN ACTUAL ES -> " + tknActual.toString());
                    e = new MiError(linea, " Error 111: este comando no admite argumentos");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    System.out.println("rellena-AS-HAYAMOS UN ERROR3 -> " + e.toString());
                    System.out.println("rellena-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());

                }
            }
        }
        //ESTA FUNCION NECESITA QUE ANTES SE HAYA FIJADO UN COLOR PARA EL RELLENO
        //USANDO LA FUNCION PONCOLORRELLENO -> PONCOLORRELLENO color/n/:variable
        System.out.println("rellena-AS-EL VALOR DE existePonColorRelleno -> " + existePonColorRelleno);
        if (!existePonColorRelleno) {
            //Como no se encontro el comando PONCOLORRELLENA dentro de las instrucciones del programa => NUEVO ERROR
            e = new MiError(linea, " ERROR 155: se requiere establecer previamente el color para el relleno");
            erroresEncontrados.add(e);
            nuevoContenido.setErroresEncontrados(erroresEncontrados);
            System.out.println("rellena-AS-HAYAMOS UN ERROR3 -> " + e.toString());
            System.out.println("rellena-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
        }
        return nuevoContenido;
    }

    public LineaContenido casoPonColorRelleno(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin) {

        System.out.println("poncolorrelleno-AS-ESTAMOS DENRO FUNCION casoPonColorRelleno con el token->" + tknActual.toString());
        //Token siguiente esperado debe ser tipo COLOR

        int linea = tknActual.getLinea();
        System.out.println("poncolorrelleno-AS-EL VALOR DE LINEA ES> " + linea);
        System.out.println("poncolorrelleno-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        System.out.println("poncolorrelleno-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

        if (!posicionFin) {
            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
            erroresEncontrados.add(e);
            nuevoContenido.setErroresEncontrados(erroresEncontrados);

        } else {
            if (!nuevaListaTokens.isEmpty()) {
                //Token esperado debe ser tipo COLOR
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("poncolorrelleno-AS-EL VALOR tknSgte> " + tknSigte.getNombre());
                //Revisamos que sigamos en la misma linea
                if (tknSigte.getLinea() == linea) {
                    // Como sigue siendo un argumento de PONCOLORRELLENO lo removemos de la lista de tokens para analizarlo
                    tknActual = nuevaListaTokens.remove(0);
                    System.out.println("poncolorrelleno-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                    if (tknActual.getTipo().equals(Tipos.COLOR)) {
                        //El argumento corresponde a un color valido de HUGO => lo aceptamos
                    } else {
                        e = new MiError(linea, " ERROR 138: se debe proporcionar un color valido");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR2 > " + e.toString());
                        System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());

                    }
                } else {
                    System.out.println("poncolorrelleno-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                    e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR5 o> " + e.toString());
                    System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR6 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                }
            }
        }
        return nuevoContenido;
    }

    public LineaContenido casoPonColorLapiz(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin) {

        System.out.println("poncolorlapiz-AS-ESTAMOS DENRO FUNCION casoPonColorLapiz con el token->" + tknActual.toString());
        //Token siguiente esperado debe ser tipo COLOR

        int linea = tknActual.getLinea();
        System.out.println("poncolorlapiz-AS-EL VALOR DE LINEA ES> " + linea);
        System.out.println("poncolorlapiz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        System.out.println("poncolorlapiz-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

        if (!posicionFin) {
            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
            erroresEncontrados.add(e);
            nuevoContenido.setErroresEncontrados(erroresEncontrados);

        } else {
            if (!nuevaListaTokens.isEmpty()) {
                //Token esperado debe ser tipo COLOR
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("poncolorlapiz-AS-EL VALOR tknSgte> " + tknSigte.getNombre());
                //Revisamos que sigamos en la misma linea
                if (tknSigte.getLinea() == linea) {
                    // Como sigue siendo un argumento de PONCOLORLAPIZ lo removemos de la lista de tokens para analizarlo
                    tknActual = nuevaListaTokens.remove(0);
                    System.out.println("poncolorlapiz-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                    if (tknActual.getTipo().equals(Tipos.COLOR)) {
                        //El argumento corresponde a un color valido de HUGO => lo aceptamos
                    } else {
                        e = new MiError(linea, " ERROR 138: se debe proporcionar un color valido");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR2 > " + e.toString());
                        System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());

                    }
                } else {
                    System.out.println("poncolorlapiz-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                    e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR5 o> " + e.toString());
                    System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR6 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                }
            }
        }
        return nuevoContenido;
    }

    public LineaContenido buscarInstruccion(Token tknActual) {

        int linea = tknActual.getLinea();
        System.out.println("bIbIbI-BUSCARINSTRUCCIONES-EL VALOR DE LINEA ES 1 > " + linea);
        LineaContenido nuevo = new LineaContenido();
        List<LineaContenido> contenidoFinal = this.getListaContenidoFinal();
        for (int i = 0; i < contenidoFinal.size(); ++i) {
            if (contenidoFinal.get(i).getLinea() == linea) {
                nuevo = (LineaContenido) contenidoFinal.get(i);
                System.out.println("bIbIbI-BUSCARINSTRUCIONES-EL VALOR NUEVOCONTENIDO ES 2> " + nuevo.getInstruccion());
                break;
            }
        }
        return nuevo;
    }

    public List<MiError> buscarInstruccion2(Token tknActual) {

        int linea = tknActual.getLinea();
        System.out.println("zzzzzzzzzzzzz-BUSCARINSTRUCCIONES-EL VALOR DE LINEA ES> " + linea);
        LineaContenido nuevo = new LineaContenido();
        List<LineaContenido> contenidoFinal = this.getListaContenidoFinal();

        for (int i = 0; i < contenidoFinal.size(); ++i) {
            if (contenidoFinal.get(i).getLinea() == linea) {
                nuevo = (LineaContenido) contenidoFinal.get(i);
                System.out.println("yyyyyyyyyyyyy-BUSCARINSTRUCIONES-EL VALOR NUEVOCONTENIDO ES> " + nuevo.getInstruccion());
                break;
            }
        }
        return nuevo.getErroresEncontrados();
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

    public boolean existeComandoFin() {
        boolean existeFin = false;
        Token tkn;

        Iterator<Token> iterator = listaTokens.iterator();
        while (iterator.hasNext()) {
            tkn = (Token) iterator.next();
            if (tkn.getNombre().equals("FIN")) {
                existeFin = true;
                break;
            }
        }
        return existeFin;
    }

    public boolean posicionComandoFin() {
        boolean posicionFin = listaTokens.get(listaTokens.size() - 1).getNombre().equals("FIN");
        return posicionFin;
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

