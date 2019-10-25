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
            boolean existeFin = false;
            boolean posicionFin = posicionComandoFin();

            boolean existePara = false;
            boolean existeVariableDeclarada = false;
            int posicionPara = 0;

            boolean estamosEnRepite = false;
            boolean existeCorIzqEnRepite = false;
            boolean existeCorDerEnRepite = false;
            boolean existeListaComandosEnRepite = false;
            //Verificamos que el ultimo comando del programa se FIN
            posicionFin = posicionComandoFin();
             
            while (!nuevaListaTokens.isEmpty()) {
                System.out.println("*****EL TAMANIO DEL LA LISTA DE NUEVALISTATOKENSS ES " + nuevaListaTokens.size());
                System.out.println("*****EL TAMANIO DEL LA LISTA DE LISTATOKENS ES " + listaTokens.size() + "\n");
                //Removemos el primer token de la lista para aplicar tecnica FIFO -> ¿sera mejor usar una cola?
                Token tknActual = nuevaListaTokens.remove(0);
                System.out.println("wwwwwwwww-AS-EL VALOR TOKENACTUAL ES ES> " + tknActual.getNombre());
                System.out.println("wwwwwwwww-AS-EL TIPO DEL TOKENACTUAL ES ES> " + tknActual.getTipo() + "\n");
                Token tknSigte = new Token();
                MiError e;
                List<MiError> erroresEncontrados= new ArrayList<MiError>();
                LineaContenido nuevoContenido;
                int linea;
                //DEBO VERIFICAR LA EXISTENCIA DE AMBAS PALABRAS Y EN SUS POSICIONES CORRECTAS

                switch (tknActual.getTipo().toString().trim()) {

                    case "COMANDOHUGO":
                        System.out.println("vvvvvvvvvv-AS-ENTRAMOS A CASE OF COMANDO> " + tknActual.getNombre());
                        OUTER:
                        switch (tknActual.getNombre()) {
                            case "PARA":
                                System.out.println("xxxxxxxx-AS-ESTAMOS EN PARA");
                                //El primer comando debe ser PARA 

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
                            case "AVANZA":
                            case "AV":
                                System.out.println("xxxxxxxx-AS-ESTAMOS EN AVANZA");
                                //Token siguiente esperado debe ser tipo IDENTIFICADOR 
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                if (!posicionFin) {
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
                                if (!posicionFin) {
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
                                                    System.out.println("zzzzzzzzzzzzz-AS-ENCONTRAMOS ERROR1> " + tknActual.getNombre() + " " + tknActual.getLinea());
                                                    e = new MiError(linea, " ERROR 112: se necesita un argumento entero para esta funcion ");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 falta corchete derecho> " + e.toString());
                                                    System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());

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
                                if (!posicionFin) {
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

                            case "HAZ":
                                //Token siguiente esperado debe ser tipo IDENTIFICADOR 
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                if (!posicionFin) {
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

                            case "REPITE":
                                linea = tknActual.getLinea();
                                System.out.println("rrrrrrr-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("rrrrrrr-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("rrrrrr-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                                estamosEnRepite = true;
                                if (!posicionFin) {
                                    e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);

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
                                                        erroresEncontrados.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                        System.out.println("rrrrrr-AS-HAYAMO UN ERROR1> ");
                                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 falta corchete derecho> " + e.toString());
                                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                    }
                                                    break;
                                                //lo aceptamos y vemos el siguiente argumento
                                                //¿ tknSigte = nuevaListaTokens.get(0); ?
                                                case ENTERO:
                                                    break;
                                                case REAL:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("rrrrrr-AS-HAYAMO UN ERROR2> ");
                                                    break;
                                                case COMANDOHUGO:
                                                case COMANDOLOGO:
                                                    e = new MiError(linea, " ERROR 145: los comandos solo estan permitidos dentro de los corchetes");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("rrrrrr-AS-HAYAMO UN ERROR3> ");
                                                    break;
                                                case DESCONOCIDO:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("rrrrrr-AS-HAYAMO UN ERROR4> ");
                                                    break;
                                                default:
                                                    e = new MiError(linea, " ERROR 144: falta el entero que indica en numero de repiticiones del comando");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    System.out.println("rrrrrr-AS-HAYAMO UN ERROR5> ");
                                                    break;
                                            }
                                        } else {
                                            e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
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
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                System.out.println("rrrrrr-AS-HAYAMO UN ERROR7> ");

                                            }

                                        } else {
                                            e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
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
                                                System.out.println("rrrrrr-AS-EL VALOR TENEMOS UNA LISTA DE COMANDOS DE REPITE QUE COMIENZA CON COMANDOHUGO-> " + tknSigte.getNombre());
                                                existeListaComandosEnRepite = true;
                                                System.out.println("rrrrrr-AS- INICIA  LISTA DE NUEVALISTATOKENS RESTANTE");
                                                nuevaListaTokens.forEach(item -> System.out.println(item.getNombre() + " <> " + item.getTipo() + " <> " + item.getLinea() + "<>" + item.getPosicion()));
                                                System.out.println("rrrrrr-AS- FINALIZA LISTA DE NUEVALISTATOKENS RESTANTE " + "\n");
                                            } else if (tknSigte.getTipo().equals(Tipos.CORDER)) {
                                                e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                System.out.println("rrrrrr-AS-HAYAMO UN ERROR9> ");

                                            } else if (!tknSigte.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                System.out.println("rrrrrr-AS-EL VALOR TENEMOS UNA LISTA DE COMANDOS DE REPITE QUE NO COMIENZA CON COMANDOHUGO-> " + tknSigte.getNombre());
                                                existeCorIzqEnRepite = false;
                                                e = new MiError(linea, " ERROR 131: la lista de comandos a repetir debe comenzar con un comando valido");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                System.out.println("rrrrrr-AS-HAYAMO UN ERROR7> ");
                                                System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 falta corchete derecho> " + e.toString());
                                                System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                            }

                                        } else {
                                            e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            System.out.println("rrrrrr-AS-HAYAMO UN ERROR9> ");
                                            break;
                                        }

                                    }

                                }//Fin del else de existeFin
                                break;

                            default:
                                break;
                        }

                    case "IDENTIFICADOR":
                        System.out.println("xxxxxxxx-AS-ESTAMOS EN CASOIDENTIFICADOR");
                        //Token siguiente esperado debe ser tipo IDENTIFICADOR 
                        linea = tknActual.getLinea();
                        System.out.println("iii-AS-EL VALOR DE LINEA ES> " + linea);
                        System.out.println("iii-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                        nuevoContenido = buscarInstruccion(tknActual);
                        System.out.println("iii-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                } else if (tknActual.getPosicion() == 0) {
                                    //Token es un identificador en el comienzo de una nueva linea 
                                    e = new MiError(linea, " Error 125: toda instruccion de REPITE debe ser comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    System.out.println("iii-AS-HAYAMOS UN ERROR2> ");
                                }
                            }
                        }

                        //e = new MiError(linea, " Error 125: la expresion debe comenzar con un comando permitido");
                        break;

                    case "ENTERO":

                        break;
                    case "REAL":

                        break;
                    case "DESCONOCIDO":
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
                                if (estamosEnRepite) {//Vemos si el token siguiente 
                                    //es un CORIZQ -> lo aceptamos ->  ¿ tknSigte = nuevaListaTokens.get(0); ?
                                    System.out.println("[[[[[[-AS-ESTAMOS EN CORIZQ DENTRO DE ESTAMOSENREPITE> " + estamosEnRepite);
                                    Token tok = new Token();
                                    for (int i = 0; i < nuevaListaTokens.size(); ++i) {
                                        tok = nuevaListaTokens.get(i);
                                        if (tok.getTipo().equals(Tipos.CORDER) && (tok.getLinea() == linea)) {
                                            existeCorDerEnRepite = true;
                                        }
                                        System.out.println("[[[[[-AS-EL VALOR DE EXISTECORDERENREPITE ES -> " + existeCorDerEnRepite);
                                    }

                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido 1> " + nuevoContenido.getErroresEncontrados());
                                        
                                        
                                        //nuevoContenido.getErroresEncontrados().forEach( item ->System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " + item )); 
                                       
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " +  nuevoContenido.getInstruccion());
                                        
                                        //for (int i = 0; i < nuevoContenido.getErroresEncontrados().size(); ++i){
                                         //   MiError e1 = nuevoContenido.getErroresEncontrados().get(1);
                                        //    erroresEncontrados.add(e1);
                                        //}
                                 
                                      //List<MiError> myError = new ArrayList<MiError>();
                                      //myError = nuevoContenido.getErroresEncontrados().subList(0, 1);
                                      //System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido> " + myError.get(0));
                                    if (!existeCorDerEnRepite) {
                                        existeCorDerEnRepite = false;
                                        e = new MiError(linea, " ERROR 103: falta corchete derecho");
                                        erroresEncontrados.add(e);
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido > " + nuevoContenido.getErroresEncontrados());
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido 3> " +nuevoContenido.toString());
                               
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 falta corchete derecho> " + e.toString());
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR2 cantidad de errores en linea de contenido 4> " + nuevoContenido.getErroresEncontrados().get(0));
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
                        System.out.println("]]]]]]-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());
                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
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
                        existeCorDerEnRepite = false;
                        break;
                    default:
                        break;
                }

            }//fin del while

            //FINAL TODO NUEVO 
        }
        System.out.println("22222-AS- INICIA  LISTA CONTENIDO FINAL****");

        for (int i = 0; i < listaContenidoFinal.size(); ++i) {

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
        System.out.println("22222-AS- FINALIZA LISTA DE CONTENIDO FINAL" + "\n");
        return listaContenidoFinal;
    } //FIN DEL NUEVO SINTACTICO

    public LineaContenido buscarInstruccion(Token tknActual) {

        int linea = tknActual.getLinea();
        System.out.println("zzzzzzzzzzzzz-BUSCARINSTRUCCIONES-EL VALOR DE LINEA ES> " + linea);
        LineaContenido nuevo = new LineaContenido();
        List<LineaContenido> contenidoFinal = this.getListaContenidoFinal();
        for (int i = 0; i < contenidoFinal.size(); ++i) {
            if (contenidoFinal.get(i).getLinea() == linea) {
                nuevo = (LineaContenido)contenidoFinal.get(i);
                System.out.println("yyyyyyyyyyyyy-BUSCARINSTRUCIONES-EL VALOR NUEVOCONTENIDO ES> " + nuevo.getInstruccion());
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
                nuevo = (LineaContenido)contenidoFinal.get(i);
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

