/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rad;

import clasemaestradeappsinstanciadas.ClaseMaestraDeAppsInstanciadas;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;

/**
 *
 * @author Pc
 */
public class Scrape {
    private Functions functions = new Functions();
    
    private int contador  = 0;
    
    private String url         = ""; // url
    private String date_format = ""; // formato_fecha
    private String prev_days   = ""; // dias_anteriores
    private int config_id      = 0; // id_configuracion
    private String config_name = ""; // nombre_configuracion
    private ArrayList<Object> parseAnimalitos;
    
    private String id_categoria = "";
    private String desc_cat = "";
    private Document contenidoWeb = null;
    public Scrape(String rutaConf){
        
        try {
            DB.DB(rutaConf);
            String rutaPhantomJS = DB.obtenerParametros(rutaConf).getProperty("ruta_phantomjs");
            int offSeason = functions.getMaxSeason();

            ArrayList<Object> parse = new ArrayList();

            ArrayList<Object> parseTeamsAndScores = new ArrayList();
            ArrayList<String> teams = new ArrayList();
            ArrayList<String> scores = new ArrayList();
            boolean season = false;

            System.out.println("\n/***** OBTENER TODAS LAS CATEGOR�AS EXISTENTES *****/");

            String queryCats = "SELECT DISTINCT(ca.id_categoria), descripcion FROM " + DB.getDB_NAME() + ".categorias ca INNER JOIN insignia_deportivo.configuracion co ON ca.id_categoria = co.id_categoria WHERE co.activa = 1";

            ResultSet cats = DB.runQuery(queryCats);

            System.out.println("---------------------------------------------------------");
            while (cats.next())
            {
                id_categoria = cats.getString("id_categoria");
                desc_cat = cats.getString("descripcion");
                
                System.out.println("\n/***** OBTENER CAMPOS DE CONFIGURACI�N PARA LA CATEGOR�A " + desc_cat + " *****/");
                System.out.println("----------------------------------------------------------");

                String queryConfig = "SELECT url, formato_fecha, dias_anteriores, id_configuracion, nombre_configuracion FROM " + DB.getDB_NAME() + ".configuracion co WHERE co.id_categoria = "+ id_categoria +" AND co.activa = 1 ";

                ResultSet urlQuery = DB.runQuery(queryConfig);


                if (!urlQuery.next())
                {
                    System.out.println("\n/***** NO EXISTEN CONFIGURACIONES PARA LA CATEGOR�A " + desc_cat + " CON ID " + id_categoria + " ****\n");
                    System.out.println("---------------------------------------------------------");
                }
                else
                {
                    urlQuery.beforeFirst();
                   
                    
                    contador = 0;
                    
                    
                    System.out.println("\n/*****CATEGOR�A " + desc_cat + " CON ID " + id_categoria + " | OBTENER CADA CONFIGURACI�N *****/\n");

                    while (urlQuery.next()) {
                        url = urlQuery.getString(1);
                        date_format = urlQuery.getString(2);
                        prev_days = urlQuery.getString(3);
                        config_id = urlQuery.getInt(4);
                        config_name = urlQuery.getString(5);

                        String df = functions.getDate(date_format, Integer.parseInt(prev_days));
                        String today = functions.getDate("dd-MM-yyyy", Integer.parseInt(prev_days));

                        teams.clear();
                        scores.clear();
                    
                        System.out.println("- CONFIGURACI�N => " + config_name + " ");
                        System.out.println("- URL => " + url);
                        System.out.println();

                        String queryContent = "SELECT tag, content_attr, content_value FROM html h INNER JOIN parsing p ON h.id_html = p.content_tag WHERE p.id_configuracion = " + config_id;
                        String queryTeam = "SELECT tag, team_attr, team_value FROM html h INNER JOIN parsing p ON h.id_html = p.team_tag WHERE p.id_configuracion = " + config_id;
                        String queryScore = "SELECT tag, score_attr, score_value FROM html h INNER JOIN parsing p ON h.id_html = p.score_tag WHERE p.id_configuracion = " + config_id;

                        ResultSet parsing_content = DB.runQuery(queryContent);
                        ResultSet parsing_team = DB.runQuery(queryTeam);
                        ResultSet parsing_score = DB.runQuery(queryScore);

                        String content_value;
                        String content_tag;
                        String content_attr;


                        if (!parsing_content.first())
                        {
                            content_tag = "";
                            content_attr = "";
                            content_value = "";
                        }
                        else
                        {
                            content_tag = parsing_content.getString("tag");
                            content_attr = parsing_content.getString("content_attr");
                            content_value = parsing_content.getString("content_value");
                        }

                        //while ((parsing_team.next()) && (parsing_score.next()))
                        if((parsing_team.next()) && (parsing_score.next()))
                        {
                            //parsing_team.previous();
                            //parsing_score.previous();

                            String team_tag = parsing_team.getString("tag");
                            String team_attr = parsing_team.getString("team_attr");
                            String team_value = parsing_team.getString("team_value");

                            String score_tag = parsing_score.getString("tag");
                            String score_attr = parsing_score.getString("score_attr");
                            String score_value = parsing_score.getString("score_value");

                            teams.clear();
                            scores.clear();

                            //String url2 = url + df;

                            if (url.contains("mismarcadores"))
                            {
                                //System.out.println(url);
                                //System.out.println("FECHA ACTUAL: " + today);
                                contenidoWeb = functions.parseUrl(url, rutaPhantomJS);
                               // System.out.println(contenidoWeb);
                                //parse = (ArrayList)functions.parseUrlMisMarcadores((Document)parseGoogle, content_tag, content_attr, content_value, team_tag, team_attr, team_value, score_tag, score_attr, score_value, today, rutaPhantomJS);
                                if(contenidoWeb == null){
                                    contenidoWeb = functions.parseUrl(url, rutaPhantomJS);
                                }
                                
                                if(contenidoWeb != null){
                                    do
                                    {
                                        contador++;
                                        today = functions.getDate("dd-MM-yyyy", contador);
                                        String fecha = functions.fechaMisMarcadores(today, "dd.MM.");
                                        System.out.println("\nFECHA NUEVA: " + fecha);

                                        parse = (ArrayList)functions.parseUrlMisMarcadores((Document)contenidoWeb, content_tag, content_attr, content_value, team_tag, team_attr, team_value, score_tag, score_attr, score_value, fecha, rutaPhantomJS);
                                        teams = (ArrayList)parse.get(0);
                                        scores = (ArrayList)parse.get(1);

                                        if ((!teams.isEmpty()) && (!scores.isEmpty())) {
                                            System.out.println("E: " + teams);
                                            System.out.println("R: " + scores);
                                            break;
                                        }
                                        else{
                                           System.out.println("NO HAY RESULTADOS PARA ESTA DISCIPLINA EN LA FECHA ESPECIFICADA\n"); 
                                        }
                                        if (contador == offSeason)
                                        {
                                            System.out.println("NO HAY TEMPORADA EN CURSO PARA ESTA DISCIPLINA");
                                            season = true;
                                        }
                                    } while (contador < offSeason);
                                }
                                else{
                                   System.out.println("NO HAY RESULTADOS PARA ESTA DISCIPLINA EN LA FECHA ESPECIFICADA\n");        
                                }
                                
                            }
                            else if (url.contains("google"))
                            {
                                System.out.println("URL DE GOOGLE");

                                String tag = team_tag = score_tag;
                                String attr = team_attr = score_attr;

                                String querySemanal = "SELECT semanal, formato_fecha FROM " + DB.getDB_NAME() + ".configuracion WHERE id_configuracion = " + config_id;

                                ResultSet semanal = DB.runQuery(querySemanal);
                                semanal.first();
                                boolean isSemanal = semanal.getInt("semanal") == 1;
                                if (isSemanal)
                                {
                                    System.out.println("JUEGOS POR SEMANA");

                                    String formatoFecha = semanal.getString("formato_fecha");

                                    String fecha = functions.fechaGoogleSemanal(today, formatoFecha);

                                    Object parseGoogle = functions.getUrlGoogleSemanal(url, rutaPhantomJS);
                                    parse = (ArrayList)functions.parseUrlGoogleSemanal((Document)parseGoogle, tag, attr, team_value, score_value, fecha, rutaPhantomJS);
                                    teams = (ArrayList)parse.get(0);
                                    scores = (ArrayList)parse.get(1);

                                    System.out.println("E: " + teams);
                                    System.out.println("R: " + scores);
                                    if ((teams.isEmpty()) || (scores.isEmpty())) {
                                        do
                                        {
                                            contador++;

                                            today = functions.getDate("dd-MM-yyyy", contador);
                                            fecha = functions.fechaGoogleSemanal(today, formatoFecha);
                                            System.out.println("FECHA NUEVA : " + fecha);

                                            parse = (ArrayList)functions.parseUrlGoogleSemanal((Document)parseGoogle, tag, attr, team_value, score_value, fecha, rutaPhantomJS);
                                            teams = (ArrayList)parse.get(0);
                                            scores = (ArrayList)parse.get(1);

                                            System.out.println("E: " + teams);
                                            System.out.println("R: " + scores);
                                            if ((!teams.isEmpty()) && (!scores.isEmpty())) {
                                                break;
                                            }
                                            if (contador == offSeason)
                                            {
                                                System.out.println("NO HAY TEMPORADA EN CURSO PARA ESTA DISCIPLINA");
                                                season = true;
                                            }
                                        } while (contador < offSeason);
                                    }
                                }
                                else
                                {
                                    System.out.println("JUEGOS POR D�A");

                                    parse = (ArrayList)functions.parseUrlGoogle(url, tag, team_attr, score_attr, team_value, score_value, rutaPhantomJS);
                                    teams = (ArrayList)parse.get(0);
                                    scores = (ArrayList)parse.get(1);

                                    System.out.println("E: " + teams);
                                    System.out.println("R: " + scores);
                                }
                            }
                            else if (url.contains("animalitos"))
                            {
                                System.out.println("Animalitos");
                                //ArrayList<Object> parseAnimalitos = (ArrayList)functions.parseUrlAnimalitos("https://www.tuazar.com/loteria/animalitos/resultados/", "h2" , "class" , "lotResTit lottoactivo" , "img" , "class", "img-responsive", "div", "class", "horario" ,"C:/Users/Pc/Documents/NetBeansProjects/rad/phantomjs.exe");
                                parseAnimalitos = (ArrayList)functions.parseUrlAnimalitos(url, content_tag, content_attr, content_value, team_tag, team_attr, team_value, score_tag, score_attr, score_value ,rutaPhantomJS);

                                if(!parseAnimalitos.isEmpty()){
                                    teams = (ArrayList)parseAnimalitos.get(0);
                                    scores = (ArrayList)parseAnimalitos.get(1);/**/
                                }
                                else{
                                    System.out.println("Sin Resultaddo de Animalitos");
                                }

                            }
                            else
                            {
                                //System.out.println("1." + url + df);

                                Object parseGoogle = functions.parseUrl(url + df, rutaPhantomJS);
                                parseTeamsAndScores = (ArrayList)functions.getTeamsAndScores((Document)parseGoogle, content_tag, content_attr, content_value, team_tag, team_attr, team_value, score_tag, score_attr, score_value);

                                if ((teams.isEmpty()) && (scores.isEmpty())) {
                                    do
                                    {
                                        contador++;

                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(date_format);
                                        LocalDate newDate = LocalDate.parse(df, formatter).minusDays(contador);

                                        String fecha = newDate.format(formatter);

                                        System.out.println("VALOR CONTADOR : " + contador);
                                        System.out.println("FECHA NUEVA : " + fecha);

                                        today = functions.getDate("dd-MM-yyyy", contador);

                                        parseGoogle = functions.parseUrl(url + fecha, rutaPhantomJS);
                                        parseTeamsAndScores = (ArrayList)functions.getTeamsAndScores((Document)parseGoogle, content_tag, content_attr, content_value, team_tag, team_attr, team_value, score_tag, score_attr, score_value);

                                        teams = (ArrayList)parseTeamsAndScores.get(0);
                                        scores = (ArrayList)parseTeamsAndScores.get(1);

                                        System.out.println("E: " + teams);
                                        System.out.println("R: " + scores);
                                        if ((!teams.isEmpty()) && (!scores.isEmpty())) {
                                            break;
                                        }
                                        if (contador == offSeason)
                                        {
                                            System.out.println("NO HAY TEMPORADA EN CURSO PARA ESTA DISCIPLINA");
                                            season = true;
                                        }
                                    } while (contador < offSeason);
                                }
                                System.out.println(teams);
                                System.out.println(scores);
                            }
                            System.out.println("TODAY : " + today);


                            functions.getResults(teams, scores, today, config_id, true, rutaConf, season);

                        }
                    }
                    System.out.println("----------------------------------------------------------");
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RAD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(RAD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
