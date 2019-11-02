/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorLexico;

import AnalizadorSintactico.AnalizadorSintactico;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author PC
 */
public class AnalizadorLexico {

    private List<String> archivo;
    private List<String> nuevoArchivo;
    private List<Token> auxTokens;
    private List<Token> listaTokens;
    private List<Color> listaColores;
    private List<ComandoLogo> listaComandosLogo;
    private List<LineaContenido> listaContenidoFinal;
    private List<MiError> listaErrores;
    private List<String> archivoFinal;

    public AnalizadorLexico(List<String> archivo) {
        this.archivo = archivo;
    }

    public List<LineaContenido> analisisLexico() {

        this.nuevoArchivo = eliminarCaracteresRedundates(archivo);

        System.out.println("222222-INICIA NUEVA LISA DESPUES DE ELIMINAR CR");
        this.nuevoArchivo.forEach(item -> System.out.println(item));
        System.out.println("22222-FINALIZA  NUEVA LISA DESPUES DE ELIMINAR CR" + "\n");

        archivoFinal = new ArrayList<>();

        auxTokens = new ArrayList<>();
        listaErrores = new ArrayList<>();
        listaTokens = new ArrayList<>();

        listaContenidoFinal = new ArrayList<>();

        //AnalizadorSintactico parser = new AnalizadorSintactico( auxTokens, listaErrores, listaTokens);
        //Color color = new Color();
        Colores colores = new Colores();
        ComandosLogo comandos = new ComandosLogo();
        Identificador id = new Identificador();// = new Identificador();
        NombreProcedimiento nombreProcedimiento = new NombreProcedimiento();
        Token nuevoToken;
        LineaContenido nuevoContenido;

        listaTokens.clear();
        listaContenidoFinal.clear();
        String nuevaLinea;
        int linea;
        linea = 0;

        Iterator<String> iter;
        iter = nuevoArchivo.iterator();
        while (iter.hasNext()) {
            // System.out.println("SEGUIMOS EN EL WHILE");
            nuevaLinea = iter.next();
            ++linea;
            System.out.println("\n" + "222222-ESTA ES LA NUEVA EXPRESION DEL  ANALISIS LEXICO-> " + nuevaLinea + " EN LA POSICION " + linea + "\n");

            if (nuevaLinea.startsWith(";")) {
                //System.out.println("Linea de comentario " + nuevaLinea);
                nuevoToken = new Token(nuevaLinea, Token.Tipos.COMENTARIO, linea, 0);
                listaTokens.add(nuevoToken); //REVISAR SI EN LOS TOKENS ESTAN LOS COMENTARIOS?
                nuevoContenido = new LineaContenido(linea, nuevaLinea); //Guardamos  el comentario de linea en archivo del contenido final
                listaContenidoFinal.add(nuevoContenido);
                //contenido.add(0, "Contenido del archivo salida");
                //archivoFinal.add(nuevaLinea);
            } else {

                nuevoContenido = new LineaContenido(linea, nuevaLinea); //Creamos un objeto LineaContenido con cada linea y
                listaContenidoFinal.add(nuevoContenido); //Cada linea pasada por el analisis lexico es agregada al conenido final
                //archivoFinal.add(nuevaLinea); //Como un string

                //Ajustamos corchetes del comando Repetir para evitar situaciones como "4[AV"  o "id]" o "entero]"
                if (nuevaLinea.startsWith("REPITE")) {
                    System.out.println("#####33333-LINEA QUE INICIA CON REPITE ->" + nuevaLinea);
                    nuevaLinea = ajustesLineaRepite(nuevaLinea);
                    System.out.println("#####33333-LINEA QUE INICIA CON REPITE DESPUES DE AJUSES ->" + nuevaLinea);
                }
                //Cada linea del archivo fuente es separada en lexemas para clasificar cada uno en tokens
                String[] lexemas;
                lexemas = nuevaLinea.split(" ");

                System.out.println("33333-INICIA CONTENIDO DE ARRAY LEXEMAS DESPUES DE SEPARAR NUEVALISTAR");
                for (String item : lexemas) {
                    System.out.println(item);
                }
                System.out.println("33333-FINALIZA CONTENIDO DE ARRAY LEXEMAS DESPUES DE SEPARAR NUEVALISTAR" + "\n");

                String nuevoComentario = " ";
                boolean comentarioEnLinea = false;
                //boolean existeLineaIniciaSinComando = false;

                //String noComando = "";
                for (int i = 0; i < lexemas.length; ++i) {
                    System.out.println("66666-ESTE ES EL LEXEMA # " + i + " " + lexemas[i] + " LINEA " + linea);

                    if (comentarioEnLinea) {
                        nuevoComentario = nuevoComentario.concat(" ").concat(lexemas[i]);
                    } else if (lexemas[i].charAt(0) == ';') {
                        comentarioEnLinea = true;
                        nuevoComentario += lexemas[i];
                        //System.out.println("Encontramos un comentario dentro de una linea de comando " + lexemas[i]);
                        //RECORDAR NO TRATAR COMENTARIOS COMO TOKENS
                    } else if (Character.isDigit(lexemas[i].charAt(0))) {
                        //System.out.println("Es un numero entero");
                        nuevoToken = esNumero(lexemas[i], linea, i);
                        listaTokens.add(nuevoToken);
                        auxTokens.add(nuevoToken);
                        //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());
                    } else if (comandos.esComando(lexemas[i])) {
                        System.out.println("Es una palabra reservada " + lexemas[i]);
                        nuevoToken = comandos.esComandoDeHugo(lexemas[i], linea, i);
                        listaTokens.add(nuevoToken);
                        auxTokens.add(nuevoToken);
                        //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());

                    } else if (colores.esColorPermitido(lexemas[i])) {
                        System.out.println("Es un color permitido " + lexemas[i]);
                        nuevoToken = new Token(lexemas[i], Token.Tipos.COLOR, linea, i);
                        listaTokens.add(nuevoToken);
                        auxTokens.add(nuevoToken);
                        //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());
                    } else if ( lexemas[i-1].equals("PARA") && nombreProcedimiento.esIdentificador(lexemas[i])) { 
                        System.out.println("Es un nombre de procedimiento " + lexemas[i]);
                        nuevoToken = new Token(lexemas[i], Token.Tipos.NOMBREPROCEDIMIENTO, linea, i);
                        listaTokens.add(nuevoToken);
                        auxTokens.add(nuevoToken);
                        //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());
                        //linea = ++linea;
                    } else if (id.esIdentificador(lexemas[i])) { // && !existeLineaIniciaSinComando) {
                        System.out.println("Es un identificador " + lexemas[i]);
                        nuevoToken = new Token(lexemas[i], Token.Tipos.IDENTIFICADOR, linea, i);
                        listaTokens.add(nuevoToken);
                        auxTokens.add(nuevoToken);
                        //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());
                        //linea = ++linea;
                    } else if (lexemas[i].charAt(0) == '[') {
                        nuevoToken = new Token(lexemas[i], Token.Tipos.CORIZQ, linea, i);
                        listaTokens.add(nuevoToken);
                        auxTokens.add(nuevoToken);
                    } else if (lexemas[i].charAt(0) == ']') {
                        nuevoToken = new Token(lexemas[i], Token.Tipos.CORDER, linea, i);
                        listaTokens.add(nuevoToken);
                        auxTokens.add(nuevoToken);
                    } else if (lexemas[i].charAt(0) == '"') {
                        //Contemplamos la posibilidad de se presente la forma ("id) o ("entero) 
                        System.out.println("Es comillas de declaracion " + lexemas[i]);
                        if (lexemas[i].length() > 1) {
                            if (i == 1) {

                                String primerCaracter = lexemas[i].substring(0, 1);
                                System.out.println("Es comillas de declaracion " + primerCaracter);
                                nuevoToken = new Token(primerCaracter, Token.Tipos.DECLARACION, linea, 1);
                                listaTokens.add(nuevoToken);
                                auxTokens.add(nuevoToken);
                                //El resto seria el resto de lexemas[i] menos el primer caracter o sea un entero o un posible identificador 
                                String resto = lexemas[i].substring(1);
                                System.out.println("Al quitar comillas queda " + resto );
                                if (Character.isDigit(resto.charAt(0))) {
                                    nuevoToken = esNumero(lexemas[i], linea, 2);
                                    listaTokens.add(nuevoToken);
                                    auxTokens.add(nuevoToken);
                                } else if (comandos.esComando(resto)) {
                                    nuevoToken = comandos.esComandoDeHugo(resto, linea, 2);
                                    System.out.println("Es una palabra reservada " + resto);
                                    //nuevoToken = new Token(resto, Token.Tipos.COMANDOHUGO, linea, 2);
                                    listaTokens.add(nuevoToken);
                                    auxTokens.add(nuevoToken);
                                    //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());
                                } else if (colores.esColorPermitido(resto)) {
                                    System.out.println("Estamos en esColorePermitido " + resto);
                                    nuevoToken = new Token(resto, Token.Tipos.COLOR, linea, 2);
                                    listaTokens.add(nuevoToken);
                                    auxTokens.add(nuevoToken);
                                } else if (id.esIdentificador(resto)) {
                                    nuevoToken = new Token(resto, Token.Tipos.IDENTIFICADOR, linea, 2);
                                    listaTokens.add(nuevoToken);
                                    auxTokens.add(nuevoToken);
                                } else {
                                    nuevoToken = new Token(resto, Token.Tipos.DESCONOCIDO, linea, 2);
                                    listaTokens.add(nuevoToken);
                                    auxTokens.add(nuevoToken);
                                }
                            } else {
                                nuevoToken = new Token(lexemas[i], Token.Tipos.DESCONOCIDO, linea, 2);
                                listaTokens.add(nuevoToken);
                                auxTokens.add(nuevoToken);
                            }
                        } else {
                            //Contemplamos la posibilidad de se presente la forma (" id) o (" entero) 
                            nuevoToken = new Token(lexemas[i], Token.Tipos.DECLARACION, linea, i);
                            listaTokens.add(nuevoToken);
                            auxTokens.add(nuevoToken);
                        }
                        //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());
                    } else if (lexemas[i].charAt(0) == ':') {
                        //Contemplamos la posibilidad de se presente la forma (:id) o (:entero) 
                        if (lexemas[i].length() > 1) {
                            //El primer caracter seria :
                            String primerCaracter = lexemas[i].substring(0, 1);
                            nuevoToken = new Token(primerCaracter, Token.Tipos.ASIGNACION, linea, 3);
                            listaTokens.add(nuevoToken);
                            auxTokens.add(nuevoToken);
                            //El resto seria el resto de lexemas[i] menos el primer caracter o sea un entero o un posible identificador 
                            String resto = lexemas[i].substring(1);
                            if (Character.isDigit(resto.charAt(0))) {
                                nuevoToken = esNumero(resto, linea, 4);
                                listaTokens.add(nuevoToken);
                                auxTokens.add(nuevoToken);
                            } else if (comandos.esComando(resto)) {
                                nuevoToken = comandos.esComandoDeHugo(resto, linea, 4);
                                System.out.println("Es una palabra reservada " + resto);
                                //nuevoToken = new Token(resto, Token.Tipos.COMANDOHUGO, linea, 4);
                                listaTokens.add(nuevoToken);
                                auxTokens.add(nuevoToken);
                                //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());

                            } else if (colores.esColorPermitido(resto)) {
                                nuevoToken = new Token(resto, Token.Tipos.COLOR, linea, 4);
                                listaTokens.add(nuevoToken);
                                auxTokens.add(nuevoToken);
                            } else if (id.esIdentificador(resto)) {
                                nuevoToken = new Token(resto, Token.Tipos.IDENTIFICADOR, linea, 4);
                                listaTokens.add(nuevoToken);
                                auxTokens.add(nuevoToken);
                            } else {
                                nuevoToken = new Token(resto, Token.Tipos.DESCONOCIDO, linea, 4);
                                listaTokens.add(nuevoToken);
                                auxTokens.add(nuevoToken);
                            }

                        } else if (colores.esColorPermitido(lexemas[i])) {
                            System.out.println("Es un color permitido " + lexemas[i]);
                            nuevoToken = new Token(lexemas[i], Token.Tipos.COLOR, linea, i);
                            listaTokens.add(nuevoToken);
                            auxTokens.add(nuevoToken);
                            //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());
                        } else {
                            //Contemplamos la posibilidad de se presente la forma (: id) o (: entero) 
                            nuevoToken = new Token(lexemas[i], Token.Tipos.ASIGNACION, linea, i);
                            listaTokens.add(nuevoToken);
                            auxTokens.add(nuevoToken);
                        }
                        //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());
                    } else {
                        System.out.println("Se encontro una expresion  no valida." + linea);
                        nuevoToken = new Token(lexemas[i], Token.Tipos.DESCONOCIDO, linea, i);
                        listaTokens.add(nuevoToken);
                        auxTokens.add(nuevoToken);
                        /*
                        nuevoError = new MiError(
                                linea,
                                " Error 129: XXXXXXXXXXXXXXXXNo es identificador valido "
                        );
                         */

                    }
                    //System.out.println("SEGUIMOS EN EL FOR");
                }//fin for

                //System.out.println("SALIMOS DEL FOR");
                System.out.println("44444-INICIA  LISTA DE TOKENS****");
                listaTokens.forEach(item -> System.out.println(item.getNombre() + " <> " + item.getTipo() + " <> " + item.getLinea() + "<>" + item.getPosicion()));
                System.out.println("44444-FINALIZA LISTA DE TOKENS" + "\n");

                /*YA NO VAMOS AL PARSER SINO QUE EL PARSER HACE EL TRABAJO FINAL              
              
                //VAMOS AL ANALISIS SINTACTICO ENVIANDO LINEA DE LEXEMAS
                System.out.println("55555-INICIA LISTA DE AUXTOKENS");
                auxTokens.forEach(item -> System.out.println(item.getNombre() + " <> " + item.getTipo() + " <> " + item.getTipo() + "<>" + item.getPosicion()));
                System.out.println("55555-FINALIZA  LISTA AUXTOKENS" + "\n");

                
                
                System.out.println("\n" + "\n" + "\n" + "77777-VAMOS PARA AL PARSER " + "\n");
                List<MiError> listaErr = parser.sintactico(auxTokens, listaErrores, listaTokens);
                System.out.println("88888 - INICIA LISTERRS");
                System.out.println(listaErr.size());
                if (listaErr != null) {
                    listaErr.forEach(item -> System.out.println(item));
                    System.out.println("88888 - FINALIZA LISTERR" + "\n");
                } else {
                    System.out.println("LA LISTA DE ERROES REGRESO VACIA");
                }
                System.out.println("66666-FUIMOS AL PARSER " + "\n" + "\n" + "\n");

                if (listaErr.size() > 0) {
                    listaErr.forEach(
                            (item) -> {
                                listaErrores.add(item);
                            });

                } else {
                    System.out.println("NO SE ENCONTRO NINGUN EROR AL VOLVER DEL PARSER" + "\n");
                }

                auxTokens.clear();
                 */
                if (comentarioEnLinea) {
                    nuevoToken = new Token();
                    nuevoToken.setNombre(nuevoComentario.trim());
                    nuevoToken.setPosicion(linea);
                    nuevoToken.setTipo(Token.Tipos.COMENTARIO);
                    //listaTokens.add(nuevoToken);

                    nuevoContenido = new LineaContenido();
                    nuevoContenido.setLinea(linea);
                    nuevoContenido.setInstruccion(nuevaLinea);
                    //contenido.add(nuevoContenido);
                    //archivoFinal.add(nuevaLinea);
                }

            } // fin if starWith(;)
            //System.out.println("SEGUIMOS EN EL WHILE.");
        } //fin del while

        return listaContenidoFinal;
    } //fin AnalizadorLexico

    public List<String> eliminarCaracteresRedundates(List<String> lista) {
        //System.out.println("Entramos a elimninarCaracteres>> ");
        //Primero eliminamos los caracteres en blanco que este al inicio o al
        //final de la lex recibida
        List<String> nuevaLista = new ArrayList<>();
        String lexema = "";
        String comentario = "";
        String str;
        try {
            if (!lista.isEmpty()) {
                //System.out.println("LISTA DE ELEMENTOS DE LA LISTA DEL eliminarCaracteresRedudantes: ");
                Iterator<String> iter;
                iter = lista.iterator();

                char caracterActual;
                char caracterAnterior;
                while (iter.hasNext()) {

                    str = iter.next();
                    //System.out.println("Este es el tamanaio de la entrada " + str + " = " + str.length() + "\n");

                    if (!lexema.isEmpty()) {
                        nuevaLista.add(lexema.trim());
                        lexema = "";
                    }
                    //Primero eliminamos los caracteres en blanco que este al inicio o al
                    //final de la cadena recibida
                    str = str.trim().replace('\t', ' ');
                    //Ahora eliminamos los caracteres redundantes de cada string

                    for (int x = 0; x < str.length(); x++) {
                        caracterActual = str.charAt(x);
                        switch (caracterActual) {
                            case ' ':
                                if (x != 0) {
                                    caracterAnterior = str.charAt(x - 1);
                                    if (caracterAnterior == ' ' || caracterAnterior == '\t') {
                                        break;
                                    } else {
                                        lexema += caracterActual;
                                    }
                                } else {
                                    break;
                                }
                            case '\t':
                                break;
                            case '\n':
                                break;
                            case ';':
                                lexema += " ;";
                                break;
                            default:
                                lexema += caracterActual;
                                break;
                        }
                    } //fin del for

                }//fin del while
                nuevaLista.add(lexema);
            } // fin del if 
        } catch (NullPointerException e) {
            System.out.println("NullPointerException Caught en recorrerArchivo linea 102");
        }

        return nuevaLista;

    }

    public void imprimirArchivoFinal(List<String> lista) {

        lista.forEach((item) -> {
            System.out.println(item);
        });

    }

    public void imprimirArchivoSalida(List<LineaContenido> lista) {

        lista.forEach((item) -> {
            System.out.println(item.getLinea() + " " + item.getInstruccion());
        });

    }

    public void imprimirArchivoListaErrores(List<MiError> lista) {
        Iterator iterator = lista.iterator();

        while (iterator.hasNext()) {
            MiError e = new MiError();
            e = (MiError) iterator.next();
            System.out.println("Error:" + "Linea: " + e.getLinea() + "Texto: " + e.getError());
        }
    }

    public boolean esOperadorAsignacion(String str) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.startsWith("\"");
        }
    }

    public boolean esVariableUtilizada(String str) {
        //System.out.println("entramos aqui con " + str);
        if (str.isEmpty()) {
            return false;
        } else {
            return str.startsWith(":");
        }
    }

    public List<LineaContenido> unirContenidos(List<LineaContenido> contenido, List<MiError> errores) {
        System.out.println("xxxxxxxxx" + errores.size());
        List<LineaContenido> nuevoContenido = contenido;
        LineaContenido n1;
        LineaContenido n2;
        MiError e;
        LineaContenido c;
        if (!errores.isEmpty()) {
            for (int i = 0; i < errores.size(); ++i) {
                e = errores.get(i);
                if (!contenido.isEmpty()) {
                    for (int j = 0; j < contenido.size(); ++j) {
                        c = contenido.get(j);
                        if (c.getLinea() == e.getLinea()) {
                            n1 = new LineaContenido(
                                    c.getLinea(),
                                    e.getError()
                            );
                            nuevoContenido.add(j + 1, n1);
                            break;
                        } else {
                            nuevoContenido.add(c);
                        }

                    }
                }

            }

        }

        return nuevoContenido;
    }
    //Funcion que crea el archivo con el analisis de los errores sintacticos
    // y semanticos

    public void crearArchivoSalida(List<String> s) {

        //Ruta a MSW logo usando version espanol -> MSWLogoEs65b
        // String ruta = "C:\\Program Files (x86)\\MSWLogoEs65b\\cuadrado-Hugo-Errores.txt";
        //Ruta a MSW logo usando version espanol -> MSWLogo
        String ruta = "C:\\Program Files (x86)\\MSWLogo\\hexagono8-Hugo-Errores.txt";

        //Ruta a MSW logo en ingles
        //String ruta = "C:\\Program Files (x86)\\Softronics\\Microsoft Windows Logo\\cuadrado-Hugo-Errores.txt";
        //USAMO RUTA AL ESCRITORIO POR LOS ESCRITURA DEBIDO A PERMISOS DE ADMINISTRADOR
        //String ruta = "C:\\Users\\PC\\Desktop\\cuadrado-Hugo-Errores.txt";
        Path path = Paths.get(ruta);
        try (BufferedWriter br = Files.newBufferedWriter(path,
                Charset.defaultCharset(), StandardOpenOption.CREATE)) {
            for (String line : s) {
                br.write(line);
                br.newLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Token esNumero(String str, int linea, int posicion) {
        Token token = new Token();

        if (str.matches("\\d*")) {
            //System.out.println("Es un numero entero");
            token.setNombre(str);
            token.setTipo(Token.Tipos.ENTERO);
            token.setLinea(linea);
            token.setPosicion(posicion);
            //listaTokens.add(nuevoToken);
            //auxTokens.add(nuevoToken);
            //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());

        } else if (str.matches("\\d*(\\.\\d+)")) {
            //System.out.println("Es un numero real con punto");
            token.setNombre(str);
            token.setTipo(Token.Tipos.REAL);
            token.setLinea(linea);
            token.setPosicion(posicion);
            //listaTokens.add(nuevoToken);
            //auxTokens.add(nuevoToken);
            //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());

        } else if (str.matches("\\d*(\\,\\d+)")) {
            //System.out.println("Es un numero real con coma");
            token.setNombre(str);
            token.setTipo(Token.Tipos.REAL);
            token.setLinea(linea);
            token.setPosicion(posicion);
            //listaTokens.add(nuevoToken);
            //auxTokens.add(nuevoToken);
            //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());

        } else {
            //System.out.println("Es un identificador no valido");
            token.setNombre(str);
            token.setTipo(Token.Tipos.DESCONOCIDO);
            token.setLinea(linea);
            token.setPosicion(posicion);
            //listaTokens.add(nuevoToken);
            //auxTokens.add(nuevoToken);
            //System.out.println("Se incluyo un nuevo token " + nuevoToken.getNombre());

        }
        return token;
    } //Fin funcion esNumero

    public String ajustesLineaRepite(String str) {
        String lexema = " ";

        char caracterAnterior;
        char caracterActual;
        System.out.println("El tamanio del string es ->" + str.length());
        for (int x = 0; x < str.length(); x++) {
            caracterActual = str.charAt(x);
            switch (caracterActual) {

                case '[':
                    if (x == str.length() - 1) {
                        System.out.println("El valor de x es ->" + x);
                        caracterAnterior = str.charAt(x - 1);
                        if (caracterAnterior != ' ') {
                            lexema += " ";
                            lexema += caracterActual;
                        } else {
                            lexema += caracterActual;
                        }
                        break;
                    }
                    if (x != 0) {
                        caracterAnterior = str.charAt(x - 1);
                        char caracterSiguiente = str.charAt(x + 1);

                        if (caracterAnterior == ' ' && caracterSiguiente == ' ') {
                            lexema += caracterActual;
                        } else if (caracterAnterior == ' ' && caracterSiguiente != ' ') {
                            lexema += caracterActual;
                            lexema += " ";
                        } else if (caracterAnterior != ' ' && caracterSiguiente == ' ') {
                            lexema += " ";
                            lexema += caracterActual;
                        } else if (caracterAnterior != ' ' && caracterSiguiente != ' ') {
                            lexema += " ";
                            lexema += caracterActual;
                            lexema += " ";
                        }
                    }
                    break;
                case ']':

                    if (x != 0) {
                        caracterAnterior = str.charAt(x - 1);
                        if (caracterAnterior == ' ') {
                            lexema += caracterActual;
                        } else if (caracterAnterior == '[') {
                            lexema += caracterActual;
                        } else {
                            lexema += " ";
                            lexema += caracterActual;
                        }
                    }
                    break;
                default:
                    lexema += caracterActual;
                    break;
            }
        }

        return lexema.trim();
    }

    public List<String> getArchivo() {
        return archivo;
    }

    public void setArchivo(List<String> archivo) {
        this.archivo = archivo;
    }

    public List<LineaContenido> getListaContenidoFinal() {
        return listaContenidoFinal;
    }

    public void setListaContenidoFinal(List<LineaContenido> listaContenidoFinal) {
        this.listaContenidoFinal = listaContenidoFinal;
    }

    public List<String> getNuevoArchivo() {
        return nuevoArchivo;
    }

    public void setNuevoArchivo(List<String> nuevoArchivo) {
        this.nuevoArchivo = nuevoArchivo;
    }

    public List<Token> getAuxTokens() {
        return auxTokens;
    }

    public void setAuxTokens(List<Token> auxTokens) {
        this.auxTokens = auxTokens;
    }

    public List<Token> getListaTokens() {
        return listaTokens;
    }

    public void setListaTokens(List<Token> listaTokens) {
        this.listaTokens = listaTokens;
    }

    public List<LineaContenido> getlistaContenidoFinal() {
        return listaContenidoFinal;
    }

    public void setContenido(List<LineaContenido> listaContenidoFinal) {
        this.listaContenidoFinal = listaContenidoFinal;
    }

    public List<MiError> getListaErrores() {
        return listaErrores;
    }

    public void setListaErrores(List<MiError> listaErrores) {
        this.listaErrores = listaErrores;
    }

    public List<String> getArchivoFinal() {
        return archivoFinal;
    }

    public void setArchivoFinal(List<String> archivoFinal) {
        this.archivoFinal = archivoFinal;
    }

} //fin clase AnalizadorLexico
