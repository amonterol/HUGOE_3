/*
Los “parsers” toman cada token, encuentran información sintáctica, 
y construyen un objeto llamado “Árbol de Sintaxis Abstracta”. Imagina que un ASA
es como un mapa para nuestro código 
— una forma de entender cómo es la estructura de cada pedazo de código.
 */
package AnalizadorSintactico;

import AnalizadorLexico.*;
import AnalizadorLexico.Token.Tipos;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private List<LineaContenido> listaContenidoFinalSinErrores;
    boolean existenErroresEnArchivoOriginal = false;
    int numeroErroresEnArchivoOriginal = 0;
    boolean estamosEnRepite = false;
    String nombreArchivoOriginal;
    ArrayList<String> variablesDeclaradas = new ArrayList<>();

    public AnalizadorSintactico(AnalizadorLexico lexico, String nombreArchivoOriginal) {
        this.listaTokens = lexico.getAuxTokens();
        this.listaErrores = lexico.getListaErrores();
        this.listaContenidoFinal = lexico.getListaContenidoFinal();
        this.listaContenidoFinalSinErrores = new ArrayList<>();
        this.nombreArchivoOriginal = nombreArchivoOriginal;
    }

    public List<LineaContenido> sintactico() throws IOException {
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

        //Revisa  que existen tokens que analizar
        if (!listaTokens.isEmpty()) {
            System.out.println("33333-AS-Entramos al if");
            //Verifica que el tamano del archivo nosupera al maximo permitido
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

            boolean existeCorIzqEnRepite = false;
            boolean existeCorDerEnRepite = false;
            boolean existeListaComandosEnRepite = false;
            boolean existePonColorRelleno = false;
            //Verifica si existen errores en el archivo fuente

            //Verificamos que el ultimo comando del programa se FIN
            posicionFin = posicionComandoFin();
            List<MiError> erroresEncontrados = new ArrayList<>();
            List<MiError> erroresEncontradosEnRepite = new ArrayList<>();
            MiError e;
            //Contiene el numero de la linea de contenido que se esta procesando
            int linea = 0;

            //Accede a la linea de contenido que se esta analizando 
            LineaContenido nuevoContenido = null;

            while (!nuevaListaTokens.isEmpty()) {

                System.out.println("\n" + "\n" + "\n" + "******EL TAMANIO DEL LA LISTA DE NUEVALISTATOKENSS ES " + nuevaListaTokens.size());
                System.out.println("******EL TAMANIO DEL LA LISTA DE LISTATOKENS ES " + listaTokens.size() + "\n");

                //Removemos el primer token de la lista para aplicar tecnica FIFO -> ¿sera mejor usar una cola?
                Token tknActual = nuevaListaTokens.remove(0);

                System.out.println("******-AS-TENEMOS UN NUEVO TOKEN ACTUAL SU NOMBR ES -> " + tknActual.getNombre());
                System.out.println("******-AS-EL TIPO DEL TOKENACTUAL ES ES> " + tknActual.getTipo() + "\n");
                //Observa el token siguiente al actual, en este caso esta en la posicion nuevaListaTokens(0) pues vamos removiendo cada token para el analisis
                Token tknSigte = new Token();

                System.out.println("*******-AS- EL VALOR DE estamosEnRepite antes de verificar si estamos en repite es->S> " + estamosEnRepite);

                //Controla si los tokens analizados son parte de la lista de instrucciones del comando REPITE
                //de esta forma no creamos una nueva lista de errores encontrados, pues no es una linea nueva, sino la misma linea de REPITE
                if (tknActual.getNombre().equals("REPITE")) {
                    lineaTknRepite = tknActual.getLinea();
                }
                estamosEnRepite = tknActual.getLinea() == lineaTknRepite;
                if (estamosEnRepite) {
                    erroresEncontrados = erroresEncontradosEnRepite;
                    System.out.println("*******-AS- ERRORES ENCONTRADOS EN IF estamosEnRepite es->S> " + erroresEncontrados + "\n");
                } else {
                    erroresEncontrados = new ArrayList<>();
                    System.out.println("*******-AS- ERRORES ENCONTRADOS EN ELSE estamosEnRepite es->S> " + erroresEncontrados + "\n");
                }
                System.out.println("*******-AS- EL VALOR DE estamosEnRepite despues de verificar si estamos en repite es->S> " + estamosEnRepite + "\n");

                //DEBO VERIFICAR LA EXISTENCIA DE AMBAS PALABRAS Y EN SUS POSICIONES CORRECTAS
                System.out.println("*******-AS-1 El valor de existenErroresEnArchivoOriginal luego de analizar un token es->S> " + existenErroresEnArchivoOriginal + "\n");
                existenErroresEnArchivoOriginal = false;
                System.out.println("*******-AS-1 El valor de existenErroresEnArchivoOriginal es->S> " + existenErroresEnArchivoOriginal + "\n");
                //El switch toma el tokenActual y verifica el tipo, porque el analisis sintactico y semantico es llevado acabo de acuerdo al tipo de token
                switch (tknActual.getTipo().toString().trim()) {

                    case "COMANDOHUGO":
                        System.out.println("\n" + "\n" + "ssssss-AS-ENTRAMOS A CASE OF COMANDO> " + tknActual.getNombre());
                        OUTER:
                        switch (tknActual.getNombre()) {
                            case "PARA":
                                System.out.println("xxxxxxxx-AS-ESTAMOS EN PARA");
                                //El primer comando debe ser PARA 
                                //erroresEncontrados = new ArrayList<>();
                                linea = tknActual.getLinea();
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA ES> " + linea);
                                System.out.println("zzzzzzzzzzzzz-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea() + "\n");

                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("yyyyyyyyyyyyy-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

                                if (m != 0) {
                                    e = new MiError(linea, " ERROR 140: el programa debe iniciar con el comando PARA");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("pppppp-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("pppppp-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("pppppp-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
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
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;
                                                System.out.println("pppppp-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                System.out.println("pppppp-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("pppppp-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            }
                                        }
                                    }

                                }
                                //nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                if (!existenErroresEnArchivoOriginal) {
                                    listaContenidoFinalSinErrores.add(nuevoContenido);
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
                                //erroresEncontrados = new ArrayList<MiError>();
                                if (!posicionFin) {
                                    //Si FIN esta en otra linea que no se la ultima programa no esta terminando con FIN
                                    e = new MiError(linea, " ERROR 142: el programa debe finalizar con el comando FIN");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("ffffff-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("ffffff-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("ffffff-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
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
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;
                                        System.out.println("ffffff-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                        System.out.println("ffffff-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                        System.out.println("ffffff-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                    }
                                }
                                if (!existenErroresEnArchivoOriginal) {
                                    listaContenidoFinalSinErrores.add(nuevoContenido);
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
                                nuevoContenido = casoPonColorLapiz(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin);
                                break;
                            case "PONCOLORRELLENO":
                                existePonColorRelleno = true;
                                nuevoContenido = casoPonColorRelleno(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin);
                                break;
                            case "RELLENA":
                                nuevoContenido = casoComandoRellena(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin, existePonColorRelleno);
                                break;
                            case "HAZ":
                                //HAZ se utiliza para la declaración de variables, su sintaxis es-> HAZ[espacio]"NOMBREDELAVARIABLE[espacio]VALORDELAVARIABLEe 
                                System.out.println("hhhhhh-AS-ESTAMOS EN HAZ 1> ");
                                //Token esperado debe ser tipo OPERADOR DE DECLARACION DE VARIABLE (")
                                System.out.println("hhhhhh-AS-EL TOKEN ACTUAL ES 2-> " + tknActual.toString());
                                //Recuperamos la linea donde se encuentran el comando HAZ para analizar si contiene o no argumentos
                                linea = tknActual.getLinea();
                                System.out.println("hhhhhh-AS-EL VALOR DE LINEA ACTUAL ES 3-> " + linea);
                                System.out.println("hhhhhh-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES 4 ->  " + tknActual.getLinea());
                                System.out.println("hhhhhh-AS-LOS ERRORES ANTES DE UBICAR EL CONTENIDO SON -> " + nuevoContenido.getErroresEncontrados());
                                //Recuperamos el contenido de archivo del contenido final correspondiente a esta linea de instruccion
                                //para poder incluirle los errores si aparecen
                                nuevoContenido = buscarInstruccion(tknActual);
                                System.out.println("hhhhhh-AS-EL VALOR DE NUEVOCONTENIDO ES -> " + nuevoContenido.getInstruccion());
                                System.out.println("hhhhhh-AS-LOS ERRORES ENCONTRADOS LUEGO DE IR A LA FUNCION BUSCARiNSTRUCCION SON -> " + nuevoContenido.getErroresEncontrados());
                                //Almacena el nombre de la variable que se declara para agregarla a la lista de variablesDeclaradas
                                String nuevaVariable = "";

                                //Verificamos si el comando pertenece o no a una lista de comandos del comando REPITE
                                if (estamosEnRepite) {
                                    e = new MiError(linea, " ERROR 150: la lista de comandos de REPITE no debe contener el comando HAZ");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                }

                                if (!posicionFin) {
                                    e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                                } else {
                                    if (!nuevaListaTokens.isEmpty()) {
                                        //Como estamos dentro del comando HAZ esperamos que el siguiente token sea de tipo OPERADOR DECLARACION (")
                                        tknSigte = nuevaListaTokens.get(0);
                                        //Revisamos que sigamos en la misma linea de HAZ
                                        if (tknSigte.getLinea() == linea) {
                                            // Como sigue siendo un argumento de HAZ lo removemos de la lista de tokens para analizarlo
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("hhhhhh-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (tknActual.getTipo().equals(Tipos.DECLARACION)) {
                                                //El tokenActual es tipo esperado, por lo tanto solo lo aceptamos y seguimos adelante
                                            } else {
                                                //Como el token no coincide con el esperado entonces existe un error
                                                e = new MiError(linea, " ERROR 119: falta el operador de declaracion de variables ( \" )");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            }
                                        } else {
                                            //Como el token siguiente no esta en la misma linea => que el comando HAZ no tiene argumentos => ERROR
                                            e = new MiError(linea, " ERROR 153: la lista de argumentos esta incompleta, se require HAZ \"Nombre de la variable :Valor de la variable");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                            System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            break;

                                        }

                                        //Token esperado debe ser tipo IDENTIFICADOR o nombre de la variable  
                                        tknSigte = nuevaListaTokens.get(0);
                                        System.out.println("hhhhhh-AS-EL VALOR tknSgte> " + tknSigte.getNombre());
                                        //Revisamos que sigamos en la misma linea
                                        if (tknSigte.getLinea() == linea) {
                                            // Como sigue siendo un argumento de HAZ lo removemos de la lista de tokens para analizarlo
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("hhhhhh-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                            if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                //Encontramos el token esperado, al ser IDENTIFICADOR, debemos verificar  no haya sido declarado antes
                                                existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                if (existeVariableDeclarada) {
                                                    //El identificador existe por lo tanto no puede ser usado nuevante, lanzamos un error
                                                    e = new MiError(linea, " ERROR 122: la variable fue definida previamente");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                } else {
                                                    //El identificador no existe en las variablesDeclaradas => es nuevaVariable
                                                    nuevaVariable = tknActual.getNombre();
                                                    //Sin embargo, no podemos meterlo en las variables declaradas hasta ver si le asignaron un valor entero  u otro identificador
                                                    System.out.println("hhhhhh-AS-TENEMOS UNA NUEVA POSIBLE VARIABLE> " + nuevaVariable);
                                                    //variablesDeclaradas.add(nuevaVariable);
                                                }
                                                //Como el token no es el esperado tratamos de idenficar su tipo para dar mas detalle al mensaje de error    
                                            } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                                e = new MiError(linea, " ERROR 158: un color valido no puede ser utilizado como nombre de variable a declarar");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                e = new MiError(linea, " ERROR 159: un comando de hugo no puede ser como nombre de variable");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                                e = new MiError(linea, " ERROR 161: un comando de logo no puede ser usado como nombre de variable");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            } else {
                                                e = new MiError(linea, " ERROR 136: el nombre de variable no es valido");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            }
                                        } else {
                                            //Como el token siguiente no esta en la misma linea del comando HAZ => no se incluyeron argumentos
                                            e = new MiError(linea, " ERROR 153: la lista de argumentos esta incompleta, se require HAZ \"Nombre de la variable :Valor de la variable");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                            System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            break;

                                        }

                                        //Token esperado debe ser  ENTERO o un OPERADOR DE ASIGNACION
                                        tknSigte = nuevaListaTokens.get(0);
                                        System.out.println("cCcAE-AS-EL TOKEN SIGUIENTE ES -> " + tknSigte.toString());
                                        System.out.println("cCcAE-AS-EL VALOR DE LINEA DEL TOKESIQUIENTE ES> " + tknSigte.getLinea());
                                        //Verificamos si el tokenSiguiente esta en la misma linea del comando
                                        if (tknSigte.getLinea() == linea) {
                                            //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                                            tknActual = nuevaListaTokens.remove(0);
                                            System.out.println("cCcAE-AS-EL NUEVO TOKEN ACTUAL ES -> " + tknActual.toString());
                                            if (tknActual.getTipo().equals(Tipos.ENTERO)) {
                                                //Como el token encontrado es tipo ENTERO solo lo aceptamos y declaramos la variable incluyendo en la 
                                                //lista de variablesDeclaradas
                                                variablesDeclaradas.add(nuevaVariable);

                                            } else if (tknActual.getTipo().equals(Tipos.REAL)) {
                                                e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;
                                                System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 5> " + e.toString());
                                                System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 6> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            } else if (tknActual.getTipo().equals(Tipos.ASIGNACION)) {
                                                //Encontramos el token de asignacion => el argumento del comando es una variable declarada
                                                //lo aceptamos y seguimos a revisar el siguiente token
                                                //Token esperado debe ser NOMBRE DE VARIABLE ya declarada
                                                tknSigte = nuevaListaTokens.get(0);
                                                //Verificamos si el tokenSiguiente esta en la misma linea del comando
                                                if (tknSigte.getLinea() == linea) {
                                                    //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                                                    tknActual = nuevaListaTokens.remove(0);
                                                    //Verificamos si el tokenActual es del tipo IDENTIFICADOR o sea un nombre de variable
                                                    if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                        //Como es un nombre de variable debemos verificar que haya sido declarada con anteriormente
                                                        //para ello consultamos el ArrayList de nombre "variablesDeclaradas"
                                                        existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                        if (!existeVariableDeclarada) {
                                                            //La variable no ha sido declarada con anterioridad => no puede usarse => ERROR
                                                            e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                                            erroresEncontrados.add(e);
                                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                            existenErroresEnArchivoOriginal = true;
                                                            ++numeroErroresEnArchivoOriginal;
                                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                            System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                            System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                        } else {
                                                            //Como la variable de asignacion utilizada ya habia sido declrada antes puede utilizarse 
                                                            //en la declaracion de la nuevaVariable, por lo tanto la agregamos a las variablesDeclaradas
                                                            variablesDeclaradas.add(nuevaVariable);
                                                        }
                                                    } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                                        e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                                                        erroresEncontrados.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;
                                                        System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                        System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                        System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                        e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                                                        erroresEncontrados.add(e);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;
                                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                        System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                        System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                        System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                                        e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                                                        erroresEncontrados.add(e);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;
                                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                        System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                        System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                        System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    } else {
                                                        //El token encontrado no es del tipo IDENTIFICADOR => no es un nombre de variable valido
                                                        System.out.println("cCcAE-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                                                        e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                                        erroresEncontrados.add(e);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;
                                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                        System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                        System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                        System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    }
                                                } else {
                                                    //
                                                    e = new MiError(linea, " Error 112: se require un argumento entero o una variable declarada previamente para este comando");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                    System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                }
                                            } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                                e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                this.existenErroresEnArchivoOriginal = true;
                                                ++this.numeroErroresEnArchivoOriginal;
                                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                this.existenErroresEnArchivoOriginal = true;
                                                ++this.numeroErroresEnArchivoOriginal;
                                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                this.existenErroresEnArchivoOriginal = true;
                                                ++this.numeroErroresEnArchivoOriginal;
                                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                this.existenErroresEnArchivoOriginal = true;
                                                ++this.numeroErroresEnArchivoOriginal;
                                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                                e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                this.existenErroresEnArchivoOriginal = true;
                                                ++this.numeroErroresEnArchivoOriginal;
                                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;
                                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                            }
                                            else {
                                                //Como el token no era entero, se espera el uso de una variable declarada, por lo tanto debe estar el operador de asignacion (:)
                                                e = new MiError(linea, " ERROR 134: falta el operador de asignacion (:) para poder utilizar una variable");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;

                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                                System.out.println("hhhhhh-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                                System.out.println("hhhhhh-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                                            }
                                             
                                        }
                                    }
                                }
                                System.out.println("hhhhhh-AS-INICIA LISTA CONTENIDO SIN ERRORES>  " + existenErroresEnArchivoOriginal);
                                if (!existenErroresEnArchivoOriginal) {
                                    listaContenidoFinalSinErrores.add(nuevoContenido);
                                    System.out.println("hhhhhh-AS-INICIA LISTA CONTENIDO SIN ERRORES>  " + existenErroresEnArchivoOriginal);
                                    System.out.println("hhhhhh-AS-INICIA LISTA CONTENIDO SIN ERRORES> ");
                                    listaContenidoFinalSinErrores.forEach((item) -> {
                                        System.out.println(item.getLinea() + " " + item.getInstruccion());
                                    });
                                    System.out.println("hhhhhh-AS-FINALIZA LISTA CONTENIDO SIN ERRORES> ");
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
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 1> " + e.toString());
                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 2> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                                } else {
                                    if (!nuevaListaTokens.isEmpty()) {
                                        // 1- Comprobamos  si el token siguiente es un  IDENTIFICADOR o ENTERO
                                        tknSigte = nuevaListaTokens.get(0);
                                        System.out.println("rrrrrr-AS-EL VALOR de tknSigte 1 -> " + tknSigte.getNombre());
                                        // Verificamos que el token analizado este en la misma linea del comando REPITE
                                        if (tknSigte.getLinea() == linea) {
                                            //Es un argumento de la funcion REPITE asi que lo removemos para analizarlo observando su tipo
                                            tknActual = nuevaListaTokens.remove(0);
                                            switch (tknActual.getTipo()) {
                                                case IDENTIFICADOR:
                                                    System.out.println("rrrrrr-ENTRAMOS AL CASO DE IDENTIFICADOR->> " + tknActual.getNombre());
                                                    //Al ser el token esperado lo aceptamos pero com es un identificador  revisamos si la variable fue declarada con anterioridad
                                                    existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                    if (!existeVariableDeclarada) {
                                                        e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                                        erroresEncontradosEnRepite.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;
                                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 3> " + e.toString());
                                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 4> " + nuevoContenido.getErroresEncontrados());
                                                        System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    }
                                                    break;
                                                case ENTERO:
                                                    //lo aceptamos y vemos el siguiente argumento
                                                    break;
                                                case REAL:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 5> " + e.toString());
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 6> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    break;
                                                case COMANDOHUGO:
                                                case COMANDOLOGO:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    e = new MiError(linea, " ERROR 145: los comandos solo estan permitidos dentro de los corchetes");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 7> " + e.toString());
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 8> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    break;
                                                case CORDER:
                                                    existeCorDerEnRepite = true;
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    e = new MiError(linea, " ERROR 102: falta corchete izquierdo");
                                                    erroresEncontradosEnRepite.add(e);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 9> " + e.toString());
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 10> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    break;
                                                case DESCONOCIDO:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 11> " + e.toString());
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 12> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    break;
                                                default:
                                                    e = new MiError(linea, " ERROR 144: falta el entero que indica el numero de repiticiones del comando");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 13> " + e.toString());
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 14> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    break;
                                            }

                                            // 2 - Comprobamos si el token siguiente es el que esperamos, en este caso, un CORIZQ
                                            tknSigte = nuevaListaTokens.get(0);
                                            //Comprobamos que sea un argumento de REPITE esperamos un CORIZQ, es decir, en la misma linea
                                            System.out.println("rrrrrr-AS-EL VALOR de tknSigte 2 -> " + tknSigte.getNombre());
                                            if (tknSigte.getLinea() == linea) {
                                                System.out.println("rrrrrr-AS-EL VALOR tknActual ES> " + tknSigte.getNombre() + " " + tknSigte.getTipo());
                                                if (tknSigte.getTipo().equals(Tipos.CORIZQ)) {
                                                    //es el token esperado,  un corchete izquierdo por lo tanto, lo aceptamos y establecemos su existencia 
                                                    existeCorIzqEnRepite = true;
                                                    System.out.println("rrrrrr-AS-EXISTE EL CORIZQ EN REPITE 4> " + existeCorIzqEnRepite);
                                                    //Vemos si el token siguiente esperamos un COMANDOHUGO
                                                    
                                                } else {
                                                    //No existe el corchete izquiedo 
                                                    existeCorIzqEnRepite = false;
                                                    e = new MiError(linea, " ERROR 102: falta corchete izquierdo");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 17> " + e.toString());
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 18> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                    System.out.println("rrrrrr-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                                                    //Tratamos de ver que tipo de token es para mejorar la explicacion del error
                                                    if (tknSigte.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                        //es un CORIZQ -> lo aceptamos ->  ¿ tknSigte = nuevaListaTokens.get(0); ?
                                                        //Comprobamos que sea un argumento de REPITE esperamos un CORIZQ
                                                        System.out.println("rrrrrr-AS-TENEMOS UNA LISTA DE COMANDOS DE REPITE QUE COMIENZA CON COMANDOHUGO-> " + tknSigte.getNombre());
                                                        existeListaComandosEnRepite = true;
                                                        System.out.println("rrrrrr-AS- INICIA  LISTA DE NUEVALISTATOKENS RESTANTE");
                                                        nuevaListaTokens.forEach(item -> System.out.println(item.getNombre() + " <> " + item.getTipo() + " <> " + item.getLinea() + "<>" + item.getPosicion()));
                                                        System.out.println("rrrrrr-AS- FINALIZA LISTA DE NUEVALISTATOKENS RESTANTE " + "\n");
                                                        
                                                    } else if (tknSigte.getTipo().equals(Tipos.CORDER)) {
                                                        System.out.println("rrrrrr-AS- TENEMOS UN CORCHETE DERECHO SN LISTA DE COMANDOS-> " + tknSigte.getNombre());
                                                        existeCorIzqEnRepite = false;
                                                        e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                                        erroresEncontradosEnRepite.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;
                                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 19> " + e.toString());
                                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 20> " + nuevoContenido.getErroresEncontrados());
                                                        System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                       
                                                    } else if (!tknSigte.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                        System.out.println("rrrrrr-AS- TENEMOS UNA LISTA DE COMANDOS DE REPITE QUE NO COMIENZA CON COMANDOHUGO-> " + tknSigte.getNombre());
                                                        existeCorIzqEnRepite = false;
                                                        e = new MiError(linea, " ERROR 131: la lista de comandos a repetir debe comenzar con un comando valido");
                                                        erroresEncontradosEnRepite.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;
                                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 19> " + e.toString());
                                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 20> " + nuevoContenido.getErroresEncontrados());
                                                        System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                      
                                                    }
                                                }

                                            } else {
                                                if (!existeCorDerEnRepite) {
                                                    //El token analizado no es un CORCHETE IZQUIERDO,ademas, esta en otra linea no es un argumento de REPITE
                                                    e = new MiError(linea, " ERROR 102: falta corchete izquierdo");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 9> " + e.toString());
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 10> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                                                    e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 9> " + e.toString());
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 10> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                                                    e = new MiError(linea, " ERROR 103: falta corchete derecho");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 9> " + e.toString());
                                                    System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 10> " + nuevoContenido.getErroresEncontrados());
                                                    System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                                   
                                                }
                                            }

                                        } else {
                                            //El token analizado no es un identificador ni un entero ademas, esta en otra linea no es un argumento de REPITE
                                            e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                            erroresEncontradosEnRepite.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;
                                            System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 15> " + e.toString());
                                            System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 16> " + nuevoContenido.getErroresEncontrados());
                                            System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                           
                                        }

                                    } //fin de if de si la lista no esta a
                                    System.out.println("rrrrrr-AS-VEMOS SI EXISTENERRORESENARCHIVOORIGINAL> " + existenErroresEnArchivoOriginal);
                                    if (!existenErroresEnArchivoOriginal) {
                                        listaContenidoFinalSinErrores.add(nuevoContenido);
                                        System.out.println("rrrrrr-AS-INICIA LISTA CONTENIDO SIN ERRORES> ");
                                        listaContenidoFinalSinErrores.forEach((item) -> {
                                            System.out.println(item.getLinea() + " " + item.getInstruccion());
                                        });
                                        System.out.println("rrrrrr-AS-FINALIZA LISTA CONTENIDO SIN ERRORES> ");
                                    }
                                   
                                }//fin else posicion fin
                        } //fin del switch dentro del case COMANDOHUGO
                        break;
                    // fin case  COMANDOHUGO
                    case "COMANDOLOGO":
                        switch (tknActual.getNombre()) {
                            case "ABIERTOS":
                            case "ABRE":
                            case "ABREACTUALIZAR":
                            case "ABREDIALOGO":
                            case "ABREMIDI":
                            case "ABREPUERTO":
                            case "AC":
                            case "ACTIVA":
                            case "ACTIVAVENTANA":
                            case "ACTUALIZABOTON":
                            case "ACTUALIZAESTATICO":
                            case "AJUSTA":
                            case "ALTO":
                            case "ANALIZA":
                            case "ANTERIOR":
                            case "ANTES":
                            case "APLICA":
                            case "ARCCOS":
                            case "ARCODEELIPSE":
                            case "ARCSEN":
                            case "ARCTAN":
                            case "AREAACTIVA":
                            case "ARREGLO":
                            case "ASCII":
                            case "ATRAPA":
                            case "ATRAS":
                            case "AYUDA":
                            case "AYUDADEWINDOWS":
                            case "AZAR":
                            case "AÑADECADENALISTBOX":
                            case "AÑADELINEACOMBOBOX":
                            case "BA":
                            case "BAJAN":
                            case "BAJANARIZ":
                            case "BAL":
                            case "BALANCEA":
                            case "BALANCEAIZQUIERDA":
                            case "BALANCEO":
                            case "BARRERA":
                            case "BITINVERSO":
                            case "BITO":
                            case "BITXOR":
                            case "BITY":
                            case "BO":
                            case "BOARCHIVO":
                            case "BORRA ":
                            case "BORRABARRADESPLAZAMIENTO":
                            case "BORRABOTON":
                            case "BORRABOTONRADIO":
                            case "BORRACADENALISTBOX":
                            case "BORRACHECKBOX":
                            case "BORRACOMBOBOX":
                            case "BORRADIALOGO":
                            case "BORRADIR":
                            case "BORRAESTATICO":
                            case "BORRAGROUPBOX":
                            case "BORRALINEACOMBOBOX":
                            case "BORRALISTBOX":
                            case "BORRAPALETA":
                            case "BORRAPANTALLA":
                            case "BORRAR":
                            case "BORRARARCHIVO":
                            case "BORRATEXTO":
                            case "BORRAVENTANA":
                            case "BOTON":
                            case "BT":
                            case "CABECEA":
                            case "CABECEO":
                            case "CAI":
                            case "CAMBIADIRECTORIO":
                            case "CAMBIASIGNO":
                            case "CAR":
                            case "CARACTER":
                            case "CARGA":
                            case "CARGADIB":
                            case "CARGADIBTAMAÑO":
                            case "CARGADLL":
                            case "CARGAGIF":
                            case "CD":
                            case "CERCA":
                            case "CIERRA":
                            case "CIERRAMIDI":
                            case "CIERRAPUERTO":
                            case "CL":
                            case "CO":
                            case "COGE":
                            case "COLORLAPIZ":
                            case "COLORPAPEL":
                            case "COLORRELLENO":
                            case "COMODEVUELVE":
                            case "CONTADORACERO":
                            case "CONTENIDO":
                            case "CONTINUA":
                            case "COPIAAREA":
                            case "COPIADEF":
                            case "CORTAAREA":
                            case "CREABARRADESPLAZAMIENTO":
                            case "CREABOTON":
                            case "CREABOTONRADIO":
                            case "CREACHECKBOX":
                            case "CREACOMBOBOX":
                            case "CREADIALOGO":
                            case "CREADIR":
                            case "CREADIRECTORIO":
                            case "CREAESTATICO":
                            case "CREAGROUPBOX":
                            case "CREALISTBOX":
                            case "CREAVENTANA":
                            case "CS":
                            case "CUENTA":
                            case "CUENTAREPITE":
                            case "CURSOR":
                            case "DEFINE":
                            case "DEFINEMACRO":
                            case "DEFINIDO":
                            case "DEFINIDOP":
                            case "DESPLAZA":
                            case "DESPLAZAIZQUIERDA":
                            case "DESPLAZAX":
                            case "DESPLAZAY":
                            case "DESTAPA":
                            case "DEV":
                            case "DEVUELVE":
                            case "DIFERENCIA":
                            case "DIRECTORIO":
                            case "DIRECTORIOPADRE":
                            case "DIRECTORIOS":
                            case "DIVISION":
                            case "ED":
                            case "EDITA":
                            case "EDITAFICHERO":
                            case "EJECUTA":
                            case "EJECUTAANALIZA":
                            case "ELEMENTO":
                            case "EMPIEZAPOLIGONO":
                            case "ENCADENA":
                            case "ENTERO":
                            case "ENVIA":
                            case "ENVIAVALORRED":
                            case "ENVOLVER":
                            case "ERROR":
                            case "ESCRIBE":
                            case "ESCRIBEBOTONRADIO":
                            case "ESCRIBECADENAPUERTO":
                            case "ESCRIBECARACTERPUERTO":
                            case "ESCRIBEPUERTO":
                            case "ESCRIBEPUERTO2":
                            case "ESCRIBERED":
                            case "ESCRIBIRARCHIVO":
                            case "ESCRITURA":
                            case "ESPERA":
                            case "ESTADO":
                            case "ESTADOCHECKBOX":
                            case "EXCLUSIVO":
                            case "EXP":
                            case "FINLEC":
                            case "FINRED":
                            case "FORMATONUMERO":
                            case "FR":
                            case "FRASE":
                            case "GOTEAR":
                            case "GROSOR":
                            case "GUARDA":
                            case "GUARDADIALOGO":
                            case "GUARDADIB":
                            case "GUARDAGIF":
                            case "HABILITABOTON":
                            case "HABILITACHECKBOX":
                            case "HABILITACOMBOBOX":
                            case "HACIA":
                            case "HACIAXYZ":
                            case "HORA":
                            case "HORAMILI":
                            case "IG":
                            case "IGUAL":
                            case "IGUALES":
                            case "ILA":
                            case "IM":
                            case "IMPROP":
                            case "IMTS":
                            case "IMTSP":
                            case "INDICEIMAGEN":
                            case "INICIARED":
                            case "INVERSOLAPIZ":
                            case "IZ":
                            case "IZQUIERDA":
                            case "LAPIZ":
                            case "LC":
                            case "LCS":
                            case "LECTURA":
                            case "LEEBARRADESPLAZAMIENTO":
                            case "LEEBOTONRADIO":
                            case "LEECADENAPUERTO":
                            case "LEECAR":
                            case "LEECARACTERPUERTO":
                            case "LEECARC":
                            case "LEECARCS":
                            case "LEEFOCO":
                            case "LEELISTA":
                            case "LEEPALABRA":
                            case "LEEPUERTO":
                            case "LEEPUERTO2":
                            case "LEEPUERTOJUEGOS":
                            case "LEERED":
                            case "LEESELECCIONLISTBOX":
                            case "LEETECLA":
                            case "LEETEXTOCOMBOBOX":
                            case "LEEVALORRED":
                            case "LIMPIA":
                            case "LIMPIAPUERTO":
                            case "LISTA":
                            case "LISTAARCH":
                            case "LL":
                            case "LLAMADLL":
                            case "LN":
                            case "LOCAL":
                            case "LOG":
                            case "LPROP":
                            case "LR":
                            case "LUZ":
                            case "LVARS":
                            case "MACRO":
                            case "MATRIZ":
                            case "MAYOR":
                            case "MAYORQUE":
                            case "MAYUSCULAS":
                            case "MCI":
                            case "MENOR":
                            case "MENORQUE":
                            case "MENOS":
                            case "MENOSPRIMERO":
                            case "MENOSPRIMEROS":
                            case "MENSAJE":
                            case "MENSAJEMIDI":
                            case "MIEMBRO":
                            case "MINUSCULAS":
                            case "MODOBITMAP":
                            case "MODOPUERTO":
                            case "MODOTORTUGA":
                            case "MODOVENTANA":
                            case "MODULO":
                            case "MP":
                            case "MPR":
                            case "MPS":
                            case "MU":
                            case "MUESTRA":
                            case "MUESTRAPOLIGONO":
                            case "MUESTRAT":
                            case "MUESTRATORTUGA":
                            case "NO":
                            case "NODOS":
                            case "NOESTADO":
                            case "NOEXCLUSIVO":
                            case "NOGOTEAR":
                            case "NOMBRE":
                            case "NOMBRES":
                            case "NOPAS":
                            case "NORED":
                            case "NOTRAZA":
                            case "NUMERO":
                            case "O":
                            case "PALABRA":
                            case "PARADA":
                            case "PASO":
                            case "PATRONLAPIZ":
                            case "PAUSA":
                            case "PEGA":
                            case "PEGAENINDICE":
                            case "PERSPECTIVA":
                            case "PFT":
                            case "PINTACOLOR":
                            case "PIXEL":
                            case "PLA":
                            case "POCCR":
                            case "PONAREAACTIVA":
                            case "PONBALANCEO":
                            case "PONBARRADESPLAZAMIENTO":
                            case "PONCABECEO":
                            case "PONCHECKBOX":
                            case "PONCLIP":
                            case "PONCOLORPAPEL":
                            case "PONCONTADOR":
                            case "PONCP":
                            case "PONCURSORESPERA":
                            case "PONCURSORNOESPERA":
                            case "PONELEMENTO":
                            case "PONESCRITURA":
                            case "PONF":
                            case "PONFOCO":
                            case "PONFONDO":
                            case "PONFORMATORTUGA":
                            case "PONG":
                            case "PONGROSOR":
                            case "PONINDICEBIT":
                            case "PONLECTURA":
                            case "PONLUPA":
                            case "PONLUZ":
                            case "PONMARGENES":
                            case "PONMODOBIT":
                            case "PONMODOTORTUGA":
                            case "PONMP":
                            case "PONPATRONLAPIZ":
                            case "PONPIXEL":
                            case "PONPOS":
                            case "PONPOSESCRITURA":
                            case "PONPOSLECTURA":
                            case "PONPRIMERO":
                            case "PONPROP":
                            case "PONR":
                            case "PONRATON":
                            case "PONRED":
                            case "PONRONZAL":
                            case "PONRUMBO":
                            case "PONTAMAÑOTIPO":
                            case "PONTECLADO":
                            case "PONTEXTOCOMBOBOX":
                            case "PONULTIMO":
                            case "PONX":
                            case "PONXY":
                            case "PONXYZ":
                            case "PONY":
                            case "PONZ":
                            case "POS":
                            case "POS3D":
                            case "POSICIONATE":
                            case "POSLECTURA":
                            case "POSRATON":
                            case "POTENCIA":
                            case "PP":
                            case "PPR":
                            case "PREGUNTABOX":
                            case "PRI":
                            case "PRIMERO":
                            case "PRIMEROS":
                            case "PRIMITIVA":
                            case "PRODUCTO":
                            case "PROP":
                            case "PROPIEDAD":
                            case "PRUEBA":
                            case "PTT":
                            case "PUL":
                            case "QUITADIBUJOTORTUGA":
                            case "QUITADLL":
                            case "QUITAESTADO":
                            case "QUITARED":
                            case "QUITARRATON":
                            case "QUITATECLADO":
                            case "RADARCCOS":
                            case "RADARCSEN":
                            case "RADARCTAN":
                            case "RADCOS":
                            case "RADSEN":
                            case "RADTAN":
                            case "RAIZCUADRADA":
                            case "RC":
                            case "REAZAR":
                            case "RECTANGULORRELLENO":
                            case "REDONDEA":
                            case "RESTO":
                            case "RESULTADOEJECUTA":
                            case "RO":
                            case "RONZAL":
                            case "ROTULA":
                            case "RUMBO":
                            case "SELECCIONBOX":
                            case "SEN":
                            case "SHELL":
                            case "SI":
                            case "SIC":
                            case "SICIERTO":
                            case "SIEMPRE":
                            case "SIEVENTO":
                            case "SIF":
                            case "SIFALSO":
                            case "SINOBOX":
                            case "SIRED":
                            case "SISINO":
                            case "SISTEMA":
                            case "SIVERDADERO":
                            case "STANDOUT":
                            case "SUENAWAVE":
                            case "SUMA":
                            case "TAMAÑODECORADO":
                            case "TAMAÑODIBUJO":
                            case "TAMAÑOGIF":
                            case "TAMAÑOTIPO":
                            case "TAN":
                            case "TAPA":
                            case "TAPADO":
                            case "TAPANOMBRE":
                            case "TECLA":
                            case "TERMINAPOLIGONO":
                            case "TEXTO":
                            case "TIENEBARRA":
                            case "TIPO":
                            case "TONO":
                            case "TORTUGA":
                            case "TORTUGAS":
                            case "TRAZA":
                            case "UL":
                            case "ULTIMO":
                            case "UNSTE":
                            case "VACIA":
                            case "VACIO":
                            case "VALOR":
                            case "VAR":
                            case "VENTANADEPURADOR":
                            case "VIRA":
                            case "VISIBLE":
                            case "Y":
                                System.out.println("gigigi-AS-ESTAMOS EN GIRADERECHA ANTES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO");
                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO ANTES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO ->> " + nuevoContenido.getInstruccion());
                                nuevoContenido = casoInstruccionSoloValidaEnLogo(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin, existenErroresEnArchivoOriginal);
                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO DESPUES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO ->> " + nuevoContenido);
                                System.out.println("gigigi-AS-ESTA LINEA DE CONTENIDO DESPUES DE IR A FUNCION CASOCOMANDOCONARGUMENTOENTERO ->> " + nuevoContenido.getErroresEncontrados());
                                break;
                        } //SWITCH COMANDOLOGO
                        break;
                    case "ENTERO":
                        System.out.println("eeeeee-AS-ESTAMOS EN ENTERO->> " + tknActual.toString());
                        break;
                    case "REAL":

                        break;
                    case "DESCONOCIDO":
                        //SE QUIRE MANEJAR LOS CASOS EN QUE APARECE NOMBRES DE VARIABLES QUE NO CUMPLEN LAS CONDICIONES DE IDENTFICADOR VALIDO
                        System.out.println("dddddd-AS-ESTAMOS EN DESCONOCIDO" + '\n' + tknActual.getNombre());
                        System.out.println("dddddd-AS-EL VALOR DE LINEA DEL tokenActual ES 2-> " + tknActual.getLinea());
                        System.out.println("dddddd-AS-LA POSICION DEL tokenActual ES 2-> " + tknActual.getPosicion());
                        System.out.println("dddddd-AS-EL TIPO DEL tokenActual ES 2-> " + tknActual.getTipo());

                        linea = tknActual.getLinea();

                        System.out.println("dddddd-AS-EL VALOR DE LINEA ES 1 > " + linea);
                        System.out.println("dddddd-AS-EL VALOR DE LINEA DEL tokenActual ES 2> " + tknActual.getLinea());

                        nuevoContenido = buscarInstruccion(tknActual);

                        System.out.println("dddddd-AS-EL VALOR NUEVOCONTENIDO ES 3> " + nuevoContenido.getInstruccion());

                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            System.out.println("dddddd-AS-HAYAMOS UN ERROR 4> " + e.toString());
                            System.out.println("dddddd-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                            System.out.println("dddddd-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("dddddd-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("dddddd-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("dddddd-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                } else if (tknActual.getPosicion() == 0) {
                                    //Token es un identificador en el comienzo de una nueva linea 
                                    e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("dddddd-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("dddddd-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("dddddd-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                    //Vemos si el token siguiente NO esperamos ningun otro token
                                    //Comprobamos que este en la misma linea que el identificador erroneo encontrado al inicio de una instruccion
                                    tknSigte = nuevaListaTokens.get(0);
                                    if (tknSigte.getLinea() == linea) {
                                        //Problema hay mas tokens en la misma linea => NUEVO ERROR
                                        //Como la instruccion es invalida removemos el token de la misma linea
                                        //para que no sea analizado por el sintactico
                                        tknActual = nuevaListaTokens.remove(0);
                                    }
                                }
                            }
                        }
                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {
                            System.out.println("dddddd-AS-AGREGAMOS UN NUEVA LINEA DE CONTENIDO AL CONTENIDO FINAL SIN ERRORES-> ");
                            listaContenidoFinalSinErrores.add(nuevoContenido);
                            System.out.println("dddddd-AS-INICIA LISTA CONTENIDO SIN ERRORES> ");
                            listaContenidoFinalSinErrores.forEach((item) -> {
                                System.out.println(item.getLinea() + " " + item.getInstruccion());
                            });
                            System.out.println("dddddd-AS-FINALIZA LISTA CONTENIDO SIN ERRORES> ");
                        }

                        break;
                    case "COLOR":
                        //MANEJA LOS CASOS EN QUE APAREZCAN COLORES EN OTRAS POSICIONES QUE NO SEAN ARGUMENTOS
                        //DE FUNCIONES QUE REQUIEREN COMO PARAMETRO UN COLOR VALIDO
                        //EL CASO DEL BUEN USO DEL COLOR SE MANEJA EN funciones poncolorrelleno y poncolorlapiz
                        System.out.println("cccccc-AS-ESTAMOS EN CASOCOLOR" + '\n' + tknActual.getNombre());
                        System.out.println("cccccc-AS-EL VALOR DE LINEA DEL tokenActual ES 2-> " + tknActual.getLinea());
                        System.out.println("cccccc-AS-LA POSICION DEL tokenActual ES 2-> " + tknActual.getPosicion());
                        System.out.println("cccccc-AS-EL TIPO DEL tokenActual ES 2-> " + tknActual.getTipo());

                        linea = tknActual.getLinea();

                        System.out.println("cccccc-AS-EL VALOR DE LINEA ES 1 > " + linea);

                        nuevoContenido = buscarInstruccion(tknActual);

                        System.out.println("cccccc-AS-EL VALOR NUEVOCONTENIDO ES 3> " + nuevoContenido.getInstruccion());

                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            System.out.println("cccccc-AS-HAYAMOS UN ERROR 4> " + e.toString());
                            System.out.println("cccccc-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                            System.out.println("cccccc-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    e = new MiError(linea, " ERROR 156: un color valido solo pueden utilizarse como argumento de PONCOLORELLENO o PONCOLORLAPIZ");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("cccccc-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("cccccc-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("cccccc-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                } else if (tknActual.getPosicion() == 0) {
                                    //Token es un identificador en el comienzo de una nueva linea 
                                    e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("cccccc-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("cccccc-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("cccccc-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                    //Vemos si el token siguiente NO esperamos ningun otro token
                                    //Comprobamos que este en la misma linea que el identificador erroneo encontrado al inicio de una instruccion
                                    tknSigte = nuevaListaTokens.get(0);
                                    if (tknSigte.getLinea() == linea) {
                                        //Problema hay mas tokens en la misma linea => NUEVO ERROR
                                        //Como la instruccion es invalida removemos el token de la misma linea
                                        //para que no sea analizado por el sintactico
                                        tknActual = nuevaListaTokens.remove(0);
                                    }

                                }
                            }
                        }
                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {
                            System.out.println("cccccc-AS-AGREGAMOS UN NUEVA LINEA DE CONTENIDO AL CONTENIDO FINAL SIN ERRORES-> ");
                            listaContenidoFinalSinErrores.add(nuevoContenido);
                            System.out.println("cccccc-AS-INICIA LISTA CONTENIDO SIN ERRORES> ");
                            listaContenidoFinalSinErrores.forEach((item) -> {
                                System.out.println(item.getLinea() + " " + item.getInstruccion());
                            });
                            System.out.println("cccccc-AS-FINALIZA LISTA CONTENIDO SIN ERRORES> ");

                        }

                        break;
                    case "IDENTIFICADOR":
                        //SE QUIERE MANEJAR LOS CASOS EN QUE APARECE UN IDENTIFICADOR VALIDO AL INICIO DE UNA INSTRUCCION 
                        //O UN IDENTIFICADOR SIN ESTAR ASOCIADO A UN COMANDO EN REPITE
                        System.out.println(
                                "iiiiii-AS-ESTAMOS EN CASOIDENTIFICADOR" + '\n' + tknActual.getNombre());

                        linea = tknActual.getLinea();

                        System.out.println(
                                "iiiiii-AS-EL VALOR DE LINEA ES 1 > " + linea);
                        System.out.println(
                                "iiiiii-AS-EL VALOR DE LINEA DEL tokenActual ES 2-> " + tknActual.getLinea());
                        System.out.println(
                                "iiiiii-AS-LA POSICION DEL tokenActual ES 2-> " + tknActual.getPosicion());
                        System.out.println(
                                "iiiiii-AS-EL TIPO DEL tokenActual ES 2-> " + tknActual.getTipo());

                        nuevoContenido = buscarInstruccion(tknActual);

                        System.out.println(
                                "iiiiii-AS-EL VALOR NUEVOCONTENIDO ES 3> " + nuevoContenido.getInstruccion());

                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            System.out.println("iiiiii-AS-HAYAMOS UN ERROR 4> " + e.toString());
                            System.out.println("iiiiii-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                            System.out.println("iiiiii-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    e = new MiError(linea, " ERROR 151: toda identificador o variable debe ser el argumento de un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("iiiiii-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                } else if (tknActual.getPosicion() == 0) {
                                    //Token es un identificador en el comienzo de una nueva linea 
                                    e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("iiiiii-AS-HAYAMO UN ERROR1> " + e.toString());
                                    System.out.println("iiiiii-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("iiiiii-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                    //Vemos si el token siguiente NO esperamos ningun otro token
                                    //Comprobamos que este en la misma linea que el identificador erroneo encontrado al inicio de una instruccion
                                    tknSigte = nuevaListaTokens.get(0);
                                    if (tknSigte.getLinea() == linea) {
                                        //Problema hay mas tokens en la misma linea => NUEVO ERROR
                                        //Como la instruccion es invalida removemos el token de la misma linea
                                        //para que no sea analizado por el sintactico
                                        tknActual = nuevaListaTokens.remove(0);
                                    }

                                }
                            }
                        }
                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {
                            System.out.println("iiiiii-AS-AGREGAMOS UN NUEVA LINEA DE CONTENIDO AL CONTENIDO FINAL SIN ERRORES-> ");
                            listaContenidoFinalSinErrores.add(nuevoContenido);
                            System.out.println("iiiiii-AS-INICIA LISTA CONTENIDO SIN ERRORES> ");
                            listaContenidoFinalSinErrores.forEach((item) -> {
                                System.out.println(item.getLinea() + " " + item.getInstruccion());
                            });
                            System.out.println("iiiiii-AS-FINALIZA LISTA CONTENIDO SIN ERRORES> ");
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
                        System.out.println(
                                "[[[[[[-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion() + " ubicados en la linea " + linea);
                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            System.out.println("[[[[[[-AS-HAYAMO UN ERROR1> " + e.toString());
                            System.out.println("[[[[[[-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                            System.out.println("[[[[[[-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                //Primero verificamos estar dentro del comando REPITE
                                if (estamosEnRepite) {
                                    // 1 - Comprobamos si el token siguiente es el que esperamos, en este caso, un   COMANDO HUGO
                                    tknSigte = nuevaListaTokens.get(0);
                                    //Comprobamos que sea un argumento de REPITE esperamos un COMANDOHUGO, es decir, en la misma linea
                                    System.out.println("rrrrrr-AS-EL VALOR de tknSigte 2 -> " + tknSigte.getNombre());
                                    if (tknSigte.getLinea() == linea) {

                                        if (tknSigte.getTipo().equals(Tipos.COMANDOHUGO)) {
                                            //Es el token esperaco COMANDOHUGO, lo aceptamos 
                                            System.out.println("rrrrrr-AS-EL VALOR tknActual ES> " + tknSigte.getNombre() + " " + tknSigte.getTipo());

                                        } else if (tknSigte.getTipo().equals(Tipos.CORDER)) {
                                            System.out.println("rrrrrr-AS- TENEMOS UNA LISTA DE COMANDOS DE REPITE QUE NO COMIENZA CON COMANDOHUGO-> " + tknSigte.getNombre());
                                            existeCorIzqEnRepite = false;
                                            e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                            erroresEncontradosEnRepite.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;
                                            System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 19> " + e.toString());
                                            System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 20> " + nuevoContenido.getErroresEncontrados());
                                            System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                                        } else {
                                            System.out.println("rrrrrr-AS- TENEMOS UNA LISTA DE COMANDOS DE REPITE QUE NO COMIENZA CON COMANDOHUGO-> " + tknSigte.getNombre());
                                            existeCorIzqEnRepite = false;
                                            e = new MiError(linea, " ERROR 131: la lista de comandos a repetir debe comenzar con un comando valido");
                                            erroresEncontradosEnRepite.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;
                                            System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 19> " + e.toString());
                                            System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 20> " + nuevoContenido.getErroresEncontrados());
                                            System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                        }
                                    } else {
                                        e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                        erroresEncontradosEnRepite.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;
                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 19> " + e.toString());
                                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 20> " + nuevoContenido.getErroresEncontrados());
                                        System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                                    }

                                    // 2 - Buscamos la existencia del CORDER en la misma linea del CORIZQ
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
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;
                                        System.out.println("[[[[[[-AS-HAYAMO UN ERROR1> " + e.toString());
                                        System.out.println("[[[[[[-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                        System.out.println("[[[[[[-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                    }

                                } else {
                                    System.out.println("[[[[[[-AS-EL VALOR de EXISTE FIN> " + existeFin);
                                    e = new MiError(linea, " ERROR 147: esta version solo acepta corchetes en el comando REPITE");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("[[[[[[-AS-HAYAMO UN ERROR1> " + e.toString());
                                    System.out.println("[[[[[[-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("[[[[[[-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                }
                            }

                        }//Fin del else de existeFin
                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {
                            System.out.println("[[[[[[-AS-AGREGAMOS UN NUEVA LINEA DE CONTENIDO AL CONTENIDO FINAL SIN ERRORES-> ");
                            listaContenidoFinalSinErrores.add(nuevoContenido);
                            System.out.println("[[[[[[-AS-INICIA LISTA CONTENIDO SIN ERRORES> ");
                            listaContenidoFinalSinErrores.forEach((item) -> {
                                System.out.println(item.getLinea() + " " + item.getInstruccion());
                            });
                            System.out.println("[[[[[[-AS-FINALIZA LISTA CONTENIDO SIN ERRORES> ");
                        }

                        break;
                    case "CORDER":
                        System.out.println("]]]]]]-AS-ESTAMOS EN ]->> ");
                        linea = tknActual.getLinea();

                        System.out.println("]]]]]]-AS-EL VALOR DE LINEA ES> " + linea);
                        System.out.println("]]]]]]-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
                        nuevoContenido = buscarInstruccion(tknActual);
                        //existenErroresEnArchivoOriginal = true;
                        //++numeroErroresEnArchivoOriginal;
                        //erroresEncontrados = nuevoContenido.getErroresEncontrados();

                        System.out.println("]]]]]]-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            existeCorDerEnRepite = false;
                            System.out.println("]]]]]]-AS-HAYAMO UN ERROR1> " + e.toString());
                            System.out.println("]]]]]]-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                            System.out.println("]]]]]]-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    //estamosEnRepite = true;
                                    // 1 - Comprobamos si el token siguiente es el que esperamos, en este caso, un   COMANDO HUGO
                                    tknSigte = nuevaListaTokens.get(0);
                                    //Comprobamos que sea un argumento de REPITE esperamos un COMANDOHUGO, es decir, en la misma linea
                                    System.out.println("rrrrrr-AS-EL VALOR de tknSigte 2 -> " + tknSigte.getNombre());
                                    if (tknSigte.getLinea() == linea) {
                                        e = new MiError(linea, " ERROR 160: la lista de comandos a repetir debe estar entre un corchete izquierdo y uno derecho");
                                        erroresEncontrados.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;
                                        System.out.println("]]]]]]-AS-HAYAMO UN ERROR1> " + e.toString());
                                        System.out.println("]]]]]]-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                        System.out.println("]]]]]]-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                    }

                                } else {
                                    System.out.println("rrrrrr-AS-EL VALOR de EXISTE FIN> " + existeFin);
                                    e = new MiError(linea, " ERROR 147: esta version solo acepta corchetes en el comando REPITE");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("]]]]]]-AS-HAYAMO UN ERROR1> " + e.toString());
                                    System.out.println("]]]]]]-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("]]]]]]-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                    //estamosEnRepite = false;
                                }
                            }

                        }//Fin del else de existeFin
                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {
                            System.out.println("]]]]]]-AS-AGREGAMOS UN NUEVA LINEA DE CONTENIDO AL CONTENIDO FINAL SIN ERRORES-> ");
                            listaContenidoFinalSinErrores.add(nuevoContenido);
                            System.out.println("]]]]]]-AS-INICIA LISTA CONTENIDO SIN ERRORES> ");
                            listaContenidoFinalSinErrores.forEach((item) -> {
                                System.out.println(item.getLinea() + " " + item.getInstruccion());
                            });
                            System.out.println("]]]]]]-AS-FINALIZA LISTA CONTENIDO SIN ERRORES> ");
                        }
                        break;
                    default:
                        break;
                }

            }//fin del while

            //FINAL TODO NUEVO 
        } // fin if listaTokens esta vacia?

        System.out.println("22222-AS- SALIENDO DEL SINTACTICOINICIA  LISTA CONTENIDO FINAL****");
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

        System.out.println("22222-AS-SALIENDO DEL SINTACTICO - FINALIZA LISTA DE CONTENIDO FINAL" + "\n");

        System.out.println("22222-AS- SALIENDO DEL SINTACTICO INICIA  LISTA CONTENIDO FINAL SIN ERRORES****");
        for (int i = 0;
                i < listaContenidoFinalSinErrores.size();
                ++i) {

            if (listaContenidoFinalSinErrores.get(i).getErroresEncontrados() == null) {
                System.out.println(listaContenidoFinalSinErrores.get(i).getLinea() + " <> " + listaContenidoFinalSinErrores.get(i).getInstruccion());
            } else {
                System.out.println(listaContenidoFinalSinErrores.get(i).getLinea() + " <> " + listaContenidoFinalSinErrores.get(i).getInstruccion());
                for (int k = 0; k < listaContenidoFinalSinErrores.get(i).getErroresEncontrados().size(); ++k) {
                    System.out.println(
                            "\t" + listaContenidoFinalSinErrores.get(i).getErroresEncontrados().get(k).getError());
                    //break;
                }
            }
        }

        System.out.println("22222-AS-SALIENDO DEL SINTACTICO - FINALIZA LISTA DE CONTENIDO FINAL SIN ERRORES" + "\n");
        System.out.println("22222-AS-ANTES DE SALIR DEL SINTACTICO VERIFICAMOS SI EXISTEN O NO ERRORES" + "\n" + existenErroresEnArchivoOriginal);
        System.out.println("22222-AS-ANTES DE SALIR DEL SINTACTICO VERIFICAMOS SI EXISTEN O NO ERRORES" + "\n" + numeroErroresEnArchivoOriginal);
        //Control si existen o no errores en el archivo fuente
        if (numeroErroresEnArchivoOriginal
                > 0) {
            crearArchivoConErrores(listaContenidoFinal, this.nombreArchivoOriginal);
            return listaContenidoFinal;
        } else {
            crearArchivoSinErrores(listaContenidoFinalSinErrores, this.nombreArchivoOriginal);
            return listaContenidoFinalSinErrores;
        }
    } //FIN DEL NUEVO SINTACTICO

    public LineaContenido casoComandoConArgumentoEntero(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin, ArrayList<String> variablesDeclaradas) {
        // Esta funcion en llamada en el caso de comandos que tiene como argumento un numero entero o una varible previamente declarada
        //La sintaxis de este tipo de comandos es ->NOMBRECOMANDO [espacio] NUMEROENTERO | :NOMBREDELAVARIABLE 

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

        if (!posicionFin) {
            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
            erroresEncontrados.add(e);
            nuevoContenido.setErroresEncontrados(erroresEncontrados);
            this.existenErroresEnArchivoOriginal = true;
            ++this.numeroErroresEnArchivoOriginal;
        } else {
            if (!nuevaListaTokens.isEmpty()) {
                //Token esperado debe ser  ENTERO o un OPERADOR DE ASIGNACION
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("cCcAE-AS-EL TOKEN SIGUIENTE ES -> " + tknSigte.toString());
                System.out.println("cCcAE-AS-EL VALOR DE LINEA DEL TOKESIQUIENTE ES> " + tknSigte.getLinea());
                //Verificamos si el tokenSiguiente esta en la misma linea del comando
                if (tknSigte.getLinea() == linea) {
                    //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                    tknActual = nuevaListaTokens.remove(0);
                    System.out.println("cCcAE-AS-EL NUEVO TOKEN ACTUAL ES -> " + tknActual.toString());
                    if (tknActual.getTipo().equals(Tipos.ENTERO)) {
                        //Como el token encontrado es tipo ENTERO solo lo aceptamos y seguimos adelante con la nueva linea del programa
                    } else if (tknActual.getTipo().equals(Tipos.REAL)) {
                        e = new MiError(linea, " ERROR 132: se require un argumento entero");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        existenErroresEnArchivoOriginal = true;
                        ++numeroErroresEnArchivoOriginal;
                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 5> " + e.toString());
                        System.out.println("rrrrrr-AS-HAYAMOS UN ERROR 6> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("rrrrrr-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                    } else if (tknActual.getTipo().equals(Tipos.ASIGNACION)) {
                        //Encontramos el token de asignacion => el argumento del comando es una variable declarada
                        //lo aceptamos y seguimos a revisar el siguiente token
                        //Token esperado debe ser NOMBRE DE VARIABLE ya declarada
                        tknSigte = nuevaListaTokens.get(0);
                        //Verificamos si el tokenSiguiente esta en la misma linea del comando
                        if (tknSigte.getLinea() == linea) {
                            //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                            tknActual = nuevaListaTokens.remove(0);
                            //Verificamos si el tokenActual es del tipo IDENTIFICADOR o sea un nombre de variable
                            if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                //Como es un nombre de variable debemos verificar que haya sido declarada con anteriormente
                                //para ello consultamos el ArrayList de nombre "variablesDeclaradas"
                                existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                if (!existeVariableDeclarada) {
                                    //La variable no ha sido declarada con anterioridad => no puede usarse => ERROR
                                    e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    this.existenErroresEnArchivoOriginal = true;
                                    ++this.numeroErroresEnArchivoOriginal;
                                    System.out.println("cCcAE-AS-HAYAMOS UN ERROR2 falta corchete derecho> " + e.toString());
                                    System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                                }
                            } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                this.existenErroresEnArchivoOriginal = true;
                                ++this.numeroErroresEnArchivoOriginal;
                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                this.existenErroresEnArchivoOriginal = true;
                                ++this.numeroErroresEnArchivoOriginal;
                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;
                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            } else {
                                //El token encontrado no es del tipo IDENTIFICADOR => no es un nombre de variable valido
                                System.out.println("cCcAE-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                                e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                this.existenErroresEnArchivoOriginal = true;
                                ++this.numeroErroresEnArchivoOriginal;
                                System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                                System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            }
                        } else {
                            //
                            e = new MiError(linea, " Error 112: se require un argumento entero o una variable declarada previamente para este comando");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            this.existenErroresEnArchivoOriginal = true;
                            ++this.numeroErroresEnArchivoOriginal;
                            System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                            System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                            System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                        }
                    } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                        e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                        System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                        e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                        System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                    } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                        e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                        System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                        e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                        System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                    } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                        e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                        System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                        e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        existenErroresEnArchivoOriginal = true;
                        ++numeroErroresEnArchivoOriginal;
                        System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                        System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                    } else {
                        //Como el token no era entero, se espera el uso de una variable declarada, por lo tanto debe estar el operador de asignacion (:)
                        e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        System.out.println("cCcAE-AS-HAYAMO UN ERROR1> " + e.toString());
                        System.out.println("cCcAE-AS-HAYAMOS UN ERROR3 cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("cCcAE-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                    }

                }
            } //fin if isEmpty
        }
        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {
            System.out.println("cCcAE-AGREGAMOS UN NUEVA LINEA A LA LISTA CONTENIDO FINAL SIN ERRORES> ");
            listaContenidoFinalSinErrores.add(nuevoContenido);
        }
        return nuevoContenido;
    }

    public LineaContenido casoComandoSinArgumento(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin) {
        // Esta funcion en llamada en el caso de comandos que no tiene un argumento 
        //La sintaxis de este tipo de comandos es -> NOMBRECOMANDO 
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
            this.existenErroresEnArchivoOriginal = true;
            ++this.numeroErroresEnArchivoOriginal;

        } else {
            if (!nuevaListaTokens.isEmpty()) {
                //Token siguiente esperado -> NINGUNO 
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("ccsa-AS-EL TOKEN SIGUIENTE ES -> " + tknSigte.toString());
                System.out.println("ccsa-AS-EL VALOR DE LINEA DEL TOKESIQUIENTE ES> " + tknSigte.getLinea());
                //Revisamos si sigamos en la misma linea
                if (tknSigte.getLinea() == linea) {
                    while (tknSigte.getLinea() == linea) {
                        tknActual = nuevaListaTokens.remove(0);
                        tknSigte = nuevaListaTokens.get(0);
                    }
                    e = new MiError(linea, " Error 111: este comando no admite argumentos");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    this.existenErroresEnArchivoOriginal = true;
                    ++this.numeroErroresEnArchivoOriginal;
                    System.out.println("ccsa-AS-HAYAMOS UN ERROR3 -> " + e.toString());
                    System.out.println("ccsa-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
                }
                /*
                if (tknSigte.getLinea() == linea) {
                    //Como existe un nuevo token en la misma linea del comando lo removemos para analizarlo
                    tknActual = nuevaListaTokens.remove(0);
                    //La funcion no admite argumentos, por lo tanto, sin importar el token que siga => error
                    System.out.println("ccsa-AS-EL NUEVO TOKEN ACTUAL ES -> " + tknActual.toString());
                    e = new MiError(linea, " Error 111: este comando no admite argumentos");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    this.existenErroresEnArchivoOriginal = true;
                    ++this.numeroErroresEnArchivoOriginal;
                    System.out.println("ccsa-AS-HAYAMOS UN ERROR3 -> " + e.toString());
                    System.out.println("ccsa-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());

                }
                 */
            }
        }
        if (!existenErroresEnArchivoOriginal) {
            System.out.println("ccsa-ENTRAMOS A AGREGAR A LA LISTA SIN ERRORES -> ");
            listaContenidoFinalSinErrores.add(nuevoContenido);
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
            this.existenErroresEnArchivoOriginal = true;
            ++this.numeroErroresEnArchivoOriginal;

        } else {
            if (!nuevaListaTokens.isEmpty()) {
                //Token siguiente esperado -> NINGUNO 
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("rellena-AS-EL TOKEN SIGUIENTE ES -> " + tknSigte.toString());
                System.out.println("rellena-AS-EL VALOR DE LINEA DEL TOKESIQUIENTE ES> " + tknSigte.getLinea());
                //Revisamos si sigamos en la misma linea
                if (tknSigte.getLinea() == linea) {
                    while (tknSigte.getLinea() == linea) {
                        tknActual = nuevaListaTokens.remove(0);
                        tknSigte = nuevaListaTokens.get(0);
                    }
                    e = new MiError(linea, " Error 111: este comando no admite argumentos");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    this.existenErroresEnArchivoOriginal = true;
                    ++this.numeroErroresEnArchivoOriginal;
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
            this.existenErroresEnArchivoOriginal = true;
            ++this.numeroErroresEnArchivoOriginal;
            System.out.println("rellena-AS-HAYAMOS UN ERROR3 -> " + e.toString());
            System.out.println("rellena-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());
        }
        if (!existenErroresEnArchivoOriginal) {
            listaContenidoFinalSinErrores.add(nuevoContenido);
        }
        return nuevoContenido;
    }

    public LineaContenido casoPonColorRelleno(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin) {

        System.out.println("poncolorrelleno-AS-ESTAMOS DENRO FUNCION casoPonColorRelleno con el token->" + tknActual.toString());
        //Token siguiente esperado debe ser tipo COLOR

        int linea = tknActual.getLinea();
        String nombreComando = tknActual.getNombre();
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
            this.existenErroresEnArchivoOriginal = true;
            ++this.numeroErroresEnArchivoOriginal;
            System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 4> " + e.toString());
            System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
            System.out.println("poncolorrelleno-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

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
                    } else if (tknActual.getTipo().equals(Tipos.ASIGNACION)) {
                        //Encontramos el token de asignacion => el argumento del comando es una variable ya declarada
                        //lo aceptamos y seguimos a revisar el siguiente token
                        //Token esperado debe ser NOMBRE DE VARIABLE ya declarada
                        tknSigte = nuevaListaTokens.get(0);
                        //Verificamos si el tokenSiguiente esta en la misma linea del comando
                        if (tknSigte.getLinea() == linea) {
                            //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                            tknActual = nuevaListaTokens.remove(0);
                            //Verificamos si el tokenActual es del tipo IDENTIFICADOR o sea un nombre de variable
                            if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                //Como es un nombre de variable debemos verificar que haya sido declarada con anteriormente
                                //para ello consultamos el ArrayList de nombre "variablesDeclaradas"
                                boolean existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                if (!existeVariableDeclarada) {
                                    //La variable no ha sido declarada con anterioridad => no puede usarse => ERROR
                                    e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("poncolorrelleno-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                                }
                            } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;
                                System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("poncolorrelleno-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                                erroresEncontrados.add(e);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("poncolorrelleno-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                                erroresEncontrados.add(e);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("poncolorrelleno-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            } else {
                                //El token encontrado no es del tipo IDENTIFICADOR => no es un nombre de variable valido
                                System.out.println("cCcAE-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                                e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                erroresEncontrados.add(e);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("poncolorrelleno-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            }
                        } else {
                            e = new MiError(linea, " ERROR 128: se esperaba un identificador valido ");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            this.existenErroresEnArchivoOriginal = true;
                            ++this.numeroErroresEnArchivoOriginal;
                            System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 4> " + e.toString());
                            System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                            System.out.println("poncolorrelleno-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                        }
                    } else {
                        System.out.println("poncolorrelleno-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                        e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 4> " + e.toString());
                        System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("poncolorrelleno-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                    }
                } else {
                    //No hay argumento en la funcion poncolorrelleno 
                    System.out.println("poncolorrelleno-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                    e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    this.existenErroresEnArchivoOriginal = true;
                    ++this.numeroErroresEnArchivoOriginal;
                    System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 4> " + e.toString());
                    System.out.println("poncolorrelleno-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                    System.out.println("poncolorrelleno-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                }

            } // fin de if lista vacia
        } //fin if posicion fin
        if (!existenErroresEnArchivoOriginal) {
            System.out.println("poncolorlapiz-NO HAYAMOS ERRORES EN LA INSTRUCCION> ");
            String nombreColor = tknActual.getNombre();
            Colores colors = new Colores();
            int numeroColor;
            numeroColor = colors.numeroColorEnLogo(nombreColor);
            LineaContenido nuevoContenidoSinErrores = new LineaContenido();

            nuevoContenidoSinErrores.setLinea(tknActual.getLinea());
            nuevoContenidoSinErrores.setInstruccion(nombreComando + " " + String.valueOf(numeroColor));

            listaContenidoFinalSinErrores.add(nuevoContenidoSinErrores);
        }
        return nuevoContenido;
    }

    public LineaContenido casoPonColorLapiz(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin) {

        System.out.println("poncolorlapiz-AS-ESTAMOS DENRO FUNCION casoPonColorLapiz con el token->" + tknActual.toString());
        //Token siguiente esperado debe ser tipo COLOR

        int linea = tknActual.getLinea();
        String nombreComando = tknActual.getNombre();
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
            this.existenErroresEnArchivoOriginal = true;
            ++this.numeroErroresEnArchivoOriginal;
            System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 4> " + e.toString());
            System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
            System.out.println("poncolorlapiz-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

        } else {
            if (!nuevaListaTokens.isEmpty()) {
                //Token esperado debe ser tipo COLOR
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("poncolorrelleno-AS-EL VALOR tknSgte> " + tknSigte.getNombre());
                //Revisamos que sigamos en la misma linea
                if (tknSigte.getLinea() == linea) {
                    // Como sigue siendo un argumento de PONCOLORRELLENO lo removemos de la lista de tokens para analizarlo
                    tknActual = nuevaListaTokens.remove(0);
                    System.out.println("poncolorlapiz-AS-EL VALOR tknActual ES> " + tknActual.getNombre());
                    if (tknActual.getTipo().equals(Tipos.COLOR)) {
                        //El argumento corresponde a un color valido de HUGO => lo aceptamos
                    } else if (tknActual.getTipo().equals(Tipos.ASIGNACION)) {
                        //Encontramos el token de asignacion => el argumento del comando es una variable ya declarada
                        //lo aceptamos y seguimos a revisar el siguiente token
                        //Token esperado debe ser NOMBRE DE VARIABLE ya declarada
                        tknSigte = nuevaListaTokens.get(0);
                        //Verificamos si el tokenSiguiente esta en la misma linea del comando
                        if (tknSigte.getLinea() == linea) {
                            //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                            tknActual = nuevaListaTokens.remove(0);
                            //Verificamos si el tokenActual es del tipo IDENTIFICADOR o sea un nombre de variable
                            if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                //Como es un nombre de variable debemos verificar que haya sido declarada con anteriormente
                                //para ello consultamos el ArrayList de nombre "variablesDeclaradas"
                                boolean existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                if (!existeVariableDeclarada) {
                                    //La variable no ha sido declarada con anterioridad => no puede usarse => ERROR
                                    e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;
                                    System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                    System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                    System.out.println("poncolorlapiz-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                                }
                            } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;
                                System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("poncolorlapiz-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                                erroresEncontrados.add(e);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("poncolorlapiz-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                                erroresEncontrados.add(e);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("poncolorlapiz-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            } else {
                                //El token encontrado no es del tipo IDENTIFICADOR => no es un nombre de variable valido
                                System.out.println("cCcAE-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                                e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                erroresEncontrados.add(e);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 4> " + e.toString());
                                System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                                System.out.println("v-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                            }
                        } else {
                            e = new MiError(linea, " ERROR 128: se esperaba un identificador valido ");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            this.existenErroresEnArchivoOriginal = true;
                            ++this.numeroErroresEnArchivoOriginal;
                            System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 4> " + e.toString());
                            System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                            System.out.println("v-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);

                        }
                    } else {
                        System.out.println("poncolorrelleno-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                        e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 4> " + e.toString());
                        System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                        System.out.println("poncolorlapiz-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                    }
                } else {
                    //No hay argumento en la funcion poncolorrelleno 
                    System.out.println("poncolorlapiz-AS-ENCONTRAMOS ERROR4> " + tknActual.getNombre() + " " + tknActual.getLinea());
                    e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    this.existenErroresEnArchivoOriginal = true;
                    ++this.numeroErroresEnArchivoOriginal;
                    System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 4> " + e.toString());
                    System.out.println("poncolorlapiz-AS-HAYAMOS UN ERROR 5> " + nuevoContenido.getErroresEncontrados());
                    System.out.println("poncolorlapiz-AS-EL VALOR DEL NUMERO DE ERRORES ES-> " + numeroErroresEnArchivoOriginal);
                }

            } // fin de if lista vacia
        } //fin if posicion fin
        if (!existenErroresEnArchivoOriginal) {
            System.out.println("poncolorlapiz-NO HAYAMOS ERRORES EN LA INSTRUCCION> ");
            String nombreColor = tknActual.getNombre();
            Colores colors = new Colores();
            int numeroColor;
            numeroColor = colors.numeroColorEnLogo(nombreColor);
            LineaContenido nuevoContenidoSinErrores = new LineaContenido();

            nuevoContenidoSinErrores.setLinea(tknActual.getLinea());
            nuevoContenidoSinErrores.setInstruccion(nombreComando + " " + String.valueOf(numeroColor));

            listaContenidoFinalSinErrores.add(nuevoContenidoSinErrores);
        }
        return nuevoContenido;
    }

    public LineaContenido casoInstruccionSoloValidaEnLogo(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin, boolean existenErroresEnArchivoOriginal) {

        System.out.println("comandosLogo-AS-ESTAMOS DENTRO FUNCION casoInstruccionSoloValidaEnLogo con el token->" + tknActual.toString());
        //Token siguiente esperado = NINGUNO

        int linea = tknActual.getLinea();

        System.out.println("comandosLogo-AS-EL VALOR DE LINEA ES> " + linea);
        System.out.println("comandosLogo-AS-EL VALOR DE LINEA DEL TOKENACTUAL ES> " + tknActual.getLinea());
        //erroresEncontrados = new ArrayList<MiError>();

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        System.out.println("comandosLogo-AS-EL VALOR NUEVOCONTENIDO ES> " + nuevoContenido.getInstruccion());

        if (!posicionFin) {
            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
            erroresEncontrados.add(e);
            nuevoContenido.setErroresEncontrados(erroresEncontrados);
            this.existenErroresEnArchivoOriginal = true;
            ++this.numeroErroresEnArchivoOriginal;

        } else {
            if (!nuevaListaTokens.isEmpty()) {
                //Token siguiente esperado -> NINGUNO 
                tknSigte = nuevaListaTokens.get(0);
                System.out.println("comandosLogo-AS-EL TOKEN SIGUIENTE ES -> " + tknSigte.toString());
                System.out.println("comandosLogo-AS-EL VALOR DE LINEA DEL TOKESIQUIENTE ES> " + tknSigte.getLinea());
                //Revisamos si sigamos en la misma linea
                if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                    //Encontramos un comando solo valido en logo
                    System.out.println("comandosLogo-AS-EL NUEVO TOKEN ACTUAL ES -> " + tknActual.toString());
                    e = new MiError(linea, " Advertencia: instrucción " + tknActual.getNombre() + " no es soportada por esta versión");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    this.existenErroresEnArchivoOriginal = true;
                    ++this.numeroErroresEnArchivoOriginal;
                    System.out.println("comandosLogo-AS-HAYAMOS UN ERROR3 -> " + e.toString());
                    System.out.println("v-AS-HAYAMOS UN ERROR cantidad de errores en linea de contenido> " + nuevoContenido.getErroresEncontrados());

                }
            }
        }
        if (!existenErroresEnArchivoOriginal) {
            listaContenidoFinalSinErrores.add(nuevoContenido);
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

    public static void crearArchivoConErrores(List<LineaContenido> archivo, String nombreArchivoOriginal) throws IOException {
        //String ruta = "C:\\Users\\pc\\Desktop\\hexagono8-Hugo-Errores.txt";
        int index = nombreArchivoOriginal.indexOf(".");
        String nombreSinExtension = nombreArchivoOriginal.substring(0, index);
        System.out.println("crearArchivoSinErrores-EL NOMBRE DEL ARCHIVO ORIGINAL SIN LA EXTENSION ES-> " + nombreSinExtension);

        //El  archivoErrores contiene la localizacion del resultado del compilador
        String nombreArchivoConErrores = nombreSinExtension + "-Hugo-Errores.txt";
        String rutaArchivoErrores = "C:\\Program Files (x86)\\MSWLogo\\" + nombreArchivoConErrores;
        System.out.println("crearArchivoConErrores-EL NOMBRE DEL ARCHIVO SIN ERRORES ES-> " + nombreArchivoConErrores);
        System.out.println("crearArchivoConErrores-LA RUTA DEL ARCHIVO SIN ERRORES ES-> " + rutaArchivoErrores);

        List<String> texts = new ArrayList<>();

        for (int i = 0; i < archivo.size(); ++i) {
            if (archivo.get(i).getErroresEncontrados() == null) {
                texts.add(archivo.get(i).getInstruccion());
            } else {
                for (int k = 0; k < archivo.get(i).getErroresEncontrados().size(); ++k) {
                    String lineaConErrores = " " + archivo.get(i).getErroresEncontrados().get(k).getError();
                    texts.add(archivo.get(i).getInstruccion());
                    texts.add(lineaConErrores);
                }
            }
        }

        //archivo.forEach((LineaContenido linea) -> texts.add(linea.toStringConErrores()));
        Path destino = Paths.get(rutaArchivoErrores);
        Charset cs = Charset.forName("US-ASCII");
        try {
            Path p;
            p = Files.write(destino, texts,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Archivo con errores fue creado en " + p.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Se produjo un error al crear el archivo de errores " + e);
        }

    }

    public static void crearArchivoSinErrores(List<LineaContenido> archivo, String nombreArchivoOriginal) throws IOException {
        //String ruta = "C:\\Users\\pc\\Desktop\\hexagono8-Hugo-Errores.txt";

        int index = nombreArchivoOriginal.indexOf(".");
        String nombreSinExtension = nombreArchivoOriginal.substring(0, index);
        System.out.println("crearArchivoSinErrores-EL NOMBRE DEL ARCHIVO ORIGINAL SIN LA EXTENSION ES-> " + nombreSinExtension);

        //El  archivoErrores contiene la localizacion del resultado del compilador
        String nombreArchivoSinErrores = nombreSinExtension + ".lgo";
        String rutaArchivoSinErrores = "C:\\Program Files (x86)\\MSWLogo\\" + nombreArchivoSinErrores;
        System.out.println("crearArchivoSinErrores-EL NOMBRE DEL ARCHIVO SIN ERRORES ES-> " + nombreArchivoSinErrores);
        System.out.println("crearArchivoSinErrores-LA RUTA DEL ARCHIVO SIN ERRORES ES-> " + rutaArchivoSinErrores);
        //String ruta = "C:\\Users\\pc\\Desktop\\cuadro-Hugo-Errores.txt";

        List<String> texts = new ArrayList<>();
        archivo.forEach((LineaContenido linea) -> texts.add(linea.toString()));

        Path destino = Paths.get(rutaArchivoSinErrores);
        Charset cs = Charset.forName("US-ASCII");
        try {
            Path p;
            p = Files.write(destino, texts,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Archivo sin errores fue creado en " + p.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Se produjo un error al crear el archivo sin errores " + e);
        }

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

