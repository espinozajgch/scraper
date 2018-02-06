
package rad;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Leandro Mayor
 */
public class Functions 
{
    public ArrayList<Object> resultado = new ArrayList();
    public int contador = 0;
    private String USER_AGENT = "";
    private int numero = 0;
    private int timeout = 10000;
    private String team ="";
    private String score ="";
    
    private String[] user_agent = {
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/601.2.7 (KHTML, like Gecko) Version/9.0.1 Safari/601.2.7",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11) AppleWebKit/601.1.56 (KHTML, like Gecko) Version/9.0 Safari/601.1.56",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36",
			//"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
			//"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.13) Gecko/20080311 Firefox/2.0.0.13",
			"Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko",
			"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)"
			};
    
    public Document parseUrl(String url, String rutaPhantomJS)
    {
        numero = (int) (Math.random() * user_agent.length);
        //System.out.println("USER AGENT: "+ user_agent[numero]);
        //USER_AGENT = user_agent[numero];
        USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
        //String USER_AGENT1 = "Mozilla/5.0 (Linux; U; Android 0.5; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3";
        /* CAPABILITIES */
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        caps.setCapability("phantomjs.page.settings.userAgent", USER_AGENT);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, rutaPhantomJS);
        /* END CAPABILITIES */
        /* LOGGER */
        ArrayList<String> cliArgsCap = new ArrayList<String>();
        cliArgsCap.add("--webdriver-loglevel=NONE");
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
        Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
        /* END LOGGER */
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", USER_AGENT);
        // PhantomJSDriver driver = new PhantomJSDriver(caps);
        PhantomJSDriver driver = new PhantomJSDriver(caps);
        
        //System.out.println("" + url);
        System.out.println("INICIANDO EXTRACCION DE CONTENIDO");
        driver.get(url);
        System.out.println("EXTRACCION DE CONTENIDO FINALIZADA");
        Document doc = Jsoup.parse(driver.getPageSource());
        //System.out.println("" + doc.html());
        
        driver.quit();
        //Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(timeout).get();
        //System.out.println("" + doc.html());
        return doc;
        
    }
    
    /* GENERAR PARSING DE LA URL SI ES DE GOOGLE */
    public Object parseUrlGoogle(String url, String tag, String team_attr, String score_attr, String valTeam , String valScore, String rutaPhantomJS)
    {
        numero = (int) (Math.random() * user_agent.length);
        System.out.println("USER AGENT: "+ user_agent[numero]);
        USER_AGENT = user_agent[numero];
        System.out.println(url);
        
        ArrayList<Object> teams = new ArrayList();
        ArrayList<Object> scores = new ArrayList();
        ArrayList<Object> teamsAndScores = new ArrayList();
        //String concatAttr = attr.equals("class") ? "." : "#";
        //String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
        
        /* CAPABILITIES */
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        caps.setCapability("phantomjs.page.settings.userAgent", USER_AGENT);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, rutaPhantomJS);
        /* END CAPABILITIES */

        /* LOGGER */
        ArrayList<String> cliArgsCap = new ArrayList<String>();
        cliArgsCap.add("--webdriver-loglevel=NONE");
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
        Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
        /* END LOGGER */
                
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", USER_AGENT);

        PhantomJSDriver driver = new PhantomJSDriver(caps);

        driver.get(url);
        
        Document doc = Jsoup.parse(driver.getPageSource());

            Elements itemTeam = doc.select(tag + "["+ team_attr +"="+ valTeam + "]");
            Elements item = doc.select(tag + "["+score_attr +"="+ valScore + "]");
            
            for (Element _tag : itemTeam) 
            {               
                teams.add(_tag.text());               
            }
            
            for (Element _tag : item) 
            {               
                scores.add(_tag.text());
            }

        
        teamsAndScores.add(teams);
        teamsAndScores.add(scores);
        
        driver.quit();
        
        return teamsAndScores;
    }
    
    public Object getUrlGoogleSemanal(String url, String rutaPhantomJS) 
    {
        
            //String concatAttr = attr.equals("class") ? "." : "#";
            //System.out.println("DIMENSIONES: "+ user_agent.length);
            numero = (int) (Math.random() * user_agent.length);
            System.out.println("USER AGENT: "+ user_agent[numero]);
            USER_AGENT = user_agent[numero];
            
            int i = 0;
            ArrayList<Object> teams = new ArrayList();
            ArrayList<Object> scores = new ArrayList();
            ArrayList<Object> teamsAndScores = new ArrayList();
            
            
            /*WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.setCssErrorHandler(new com.gargoylesoftware.htmlunit.SilentCssErrorHandler());
            webClient.waitForBackgroundJavaScript(10000);
            HtmlPage page;
            page = webClient.getPage(url);
            
            Document doc = Jsoup.parse(page.asXml());*/
            
            //"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
            //System.out.println("- " + USER_AGENT);
            
            /* CAPABILITIES */
             DesiredCapabilities caps = new DesiredCapabilities();
            caps.setJavascriptEnabled(true);
            caps.setCapability("phantomjs.page.settings.userAgent", USER_AGENT);
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, rutaPhantomJS);
            /* END CAPABILITIES */
            
            /* LOGGER */
            ArrayList<String> cliArgsCap = new ArrayList<String>();
            cliArgsCap.add("--webdriver-loglevel=NONE");
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
            Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
            /* END LOGGER */
            
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", USER_AGENT);
            
            PhantomJSDriver driver = new PhantomJSDriver(caps);
            
            driver.get(url);
            
            Document doc = Jsoup.parse(driver.getPageSource());
            
            Elements cargarMas = doc.select("div._LJ");
            
            if(!cargarMas.isEmpty()) 
            {
                WebDriverWait wait = new WebDriverWait(driver, 10);               
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ellip._Axg.exp-txt-c.xcas")));
                boolean status = element.isDisplayed();
                
                if (status)
                {
                    driver.findElement(By.cssSelector("div._LJ._qxg.xpdarr._WGh.vk_arc")).click();
                }
            }
            driver.quit();
            return doc;
       
    }    
    
    /* GENERAR PARSING DE LA URL SI SON RESULTADOS SEMANALES (GOOGLE) */
    public Object parseUrlGoogleSemanal(Document doc, String tag, String attr, String valTeam , String valScore, String fecha, String rutaPhantomJS) 
    {
        //String concatAttr = attr.equals("class") ? "." : "#";       
        int i = 0;
        ArrayList<Object> teams = new ArrayList();
        ArrayList<Object> scores = new ArrayList();
        ArrayList<Object> teamsAndScores = new ArrayList();

        Elements resultados = doc.select("tr[data-kno-alog-id*=\"" + fecha + "\"]");

        for (Element items : resultados) 
        {
            Elements itemTeam = items.select(tag + "["+ attr +"=" + valTeam + "]");
            Elements item = items.select(tag + "["+ attr +"=" + valScore + "]");

            for (Element _tag : itemTeam) 
            {               
                teams.add(_tag.text()); 
               // System.out.println("E: " + _tag.text());
            }
            
            for (Element _tag : item) 
            {               
                //teams.add(_tag.text());
                scores.add(_tag.text());
               // System.out.println("R: " + _tag.text());
            }
        }
 
        teamsAndScores.add(teams);
        teamsAndScores.add(scores);

        return teamsAndScores;
    }

    public Object parseUrlMisMarcadores(Document doc, String tagContent, String attrContent, String valContent , String tagTeam, String attrTeam, String valTeam , String tagScore, String attrScore, String valScore, String fecha, String rutaPhantomJS) 
    {
        //String concatAttr = attr.equals("class") ? "." : "#";
         
        //int i = 0;
        String fechaGame= "";
        String fechas[];
        
        String resul = "";
        String res[];
        
        String equipo1;
        String equipo2;
        String eq[];
        
        String eq1 = null, eq2 = null;
        String val1 = null, val2 = null;
        
        ArrayList<Object> teams = new ArrayList();
        ArrayList<Object> scores = new ArrayList();
        ArrayList<Object> teamsAndScores = new ArrayList();

        Elements resultados = doc.select(tagContent + "["+ attrContent +"*=\""+ valContent +"\"]");

        for (Element items : resultados) 
        {            
            fechaGame = items.select("td[class=cell_ad time]").text();
            fechas = fechaGame.split(" ");
             
            if(fechas.length > 0)
                if(fecha.equals(fechas[0])){
               
                //equipo = items.select(tagContent + "["+ attrContent +"=\""+ valTeam +"\"]").text();
                //equipo = items.select("span[class=padl]").text();
                eq1 = (String) items.select("td[class*=\"team-home\"]").text();
                eq2 = (String) items.select("td[class*=\"team-away\"]").text();
                
                
                //System.out.println("Equipo 1: " + items.select("td[class*=\"team-home\"]").text());
                //System.out.println("Equipo 2: " + items.select("td[class*=\"team-away\"]").text());
                
                if(!cadenaVacia(eq1) && !cadenaVacia(eq2)){
                    //System.out.println(limpiar(eq1));
                    //System.out.println(eq2);

                    resul = items.select("td[class*=\"score\"]").text();

                    if(resul.length() >= 4){
                        //System.out.println(resul);
                        res = resul.split(":");
                        //System.out.println(res.length);
                        if(res.length >= 2){
                            val1 = res[0];
                            val2 = res[1]; 
                        }
                        //val1 = resul.substring(0, 1);
                        //val2 = resul.substring(4); 
                    }
                    
                    //System.out.println(val1);
                    //System.out.println(val2);
                    
                   // if(!cadenaVacia(eq1) && !cadenaVacia(eq2) && !cadenaVacia(val1) && !cadenaVacia(val2)){
                        eq1 = getTeamABBR(limpiar(eq1));
                        eq2 = getTeamABBR(limpiar(eq2));
                        
                        teams.add(eq1); 
                        scores.add(eliminarCaracteres(val1));
                        teams.add(eq2); 
                        scores.add(eliminarCaracteres(val2));
                   // }
                } 
            }   
        }
        
        teamsAndScores.add(teams);
        teamsAndScores.add(scores);
        //System.out.println("Equipo: " + teams);
        //System.out.println("Resultados: " + scores);
        return teamsAndScores;
    }
    
    private String limpiar(String texto){
        
        String arreglo[] = texto.split(" ");
        
        if(arreglo.length > 1){
            texto = "";
            for(int i = 0; i < arreglo.length; i++){
                if(texto.length() > 0)
                    texto += " " + eliminarCaracteres2(arreglo[i]);
                else
                    texto += eliminarCaracteres2(arreglo[i]);
            }
        }
        //System.out.println("Equipo: "+texto);
        return texto;
    }
    
    public Object parseUrlAnimalitos(String url, String tagContent, String attrContent, String valContent,  String tagTeam, String attrTeam, String valTeam , String tagScore, String attrScore, String valScore, String rutaPhantomJS)
    {
        numero = (int) (Math.random() * user_agent.length);
        //System.out.println("USER AGENT: "+ user_agent[numero]);
        //USER_AGENT = user_agent[numero];
        //int i = 0;
        
        ArrayList<Object> animales      = new ArrayList();
        ArrayList<Object> horas         = new ArrayList();
        ArrayList<Object> animalesHoras = new ArrayList();
        String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
        
        /* CAPABILITIES */
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        caps.setCapability("phantomjs.page.settings.userAgent", USER_AGENT);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, rutaPhantomJS);
        /* END CAPABILITIES */
        
        /* LOGGER */
        ArrayList<String> cliArgsCap = new ArrayList<String>();
        cliArgsCap.add("--webdriver-loglevel=NONE");
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
        Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
        /* END LOGGER */
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", USER_AGENT);
        PhantomJSDriver driver = new PhantomJSDriver(caps);
        driver.get(url);
        
        Document doc = Jsoup.parse(driver.getPageSource());
        Element resul = null;
       // System.out.println(tagContent + "[" + attrContent + "=" + valContent + "]");
        resul = doc.select(tagContent + "[" + attrContent + "=" + valContent + "]").first();
        //Element granjita =  doc.getElementsByClass("lotResTit lagranjita").first().parent();
        if(resul != null){
            Element granjita = resul.parent();
            
            String animals = "";
            String hour = "";
            Elements resultados = granjita.select("div.row.resultado");

            for (Element res : resultados)
            {
                for (Element div : res.select("div.col-xs-6"))
                {
                    animals = div.select(tagTeam + "[" + attrTeam + "="+ valTeam + "]").attr("alt");
                   // System.out.println(tagTeam + "[" + attrTeam + "="+ valTeam + "]");
                    animals = eliminarBlancos(animals);
                    if(!animals.isEmpty() && animals.length() < 15 )
                    {
                        animales.add("("+ animals + ")");
                        //System.out.println(animals);
                        hour = (div.select(tagScore + "[" + attrScore + "=" + valScore + "]").text()).substring(0, 2);
                        //System.out.println(tagScore + "[" + attrScore + "=" + valScore + "]");
                        horas.add(hour);
                        //System.out.println("hora: " + hour);
                    }
                }
            }
            animalesHoras.add(horas);
            animalesHoras.add(animales);
        }
       
        
        //driver.quit();
        
        return animalesHoras;
    }
    
    private String eliminarBlancos(String sTexto){
        String sCadenaSinBlancos = "";
        
        for (int x=0; x < sTexto.length(); x++) {
            if (sTexto.charAt(x) != ' ')
              //System.out.println(sTexto.charAt(x) + " = " + sTexto.codePointAt(x));
              sCadenaSinBlancos += sTexto.charAt(x);
          }
        return sCadenaSinBlancos;
    }
    
    public String getDate(String formato, int d) 
    {
        LocalDate now = LocalDate.now().minusDays(d);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(formato);
        String fecha = now.format(formatters);

        return fecha;
    }
    
    public Object getTeamsAndScores(
            Document parse,
            String content_tag,
            String content_attr,
            String content_val,
            String team_tag, 
            String team_attr, 
            String team_val, 
            String score_tag, 
            String score_attr, 
            String score_val)
    {
        String contentConcatAttr = content_attr.equals("class") ? "." : "#";
        String teamConcatAttr    = team_attr.   equals("class") ? "." : "#";
        String scoreConcatAttr   = score_attr.  equals("class") ? "." : "#";
        String equipo = "";
        String resul = "";
        // System.out.println(content_tag + contentConcatAttr + content_val);

        //int i = 0;
        ArrayList<Object> teams          = new ArrayList();
        ArrayList<Object> scores         = new ArrayList();
        ArrayList<Object> teamsAndScores = new ArrayList();
        
        Elements resultados = parse.select(content_tag + contentConcatAttr + content_val);
        
        // System.out.println(resultados);
        
        for (Element resultado : resultados) 
        {
            Elements team  = resultado.select(team_tag  + teamConcatAttr  + team_val);
            Elements score = resultado.select(score_tag + scoreConcatAttr + score_val);

            
            for (Element t : team)
            {
                if(!score.text().equals(""))
                {
                    equipo = eliminarCaracteres(t.text());
                    //equipo = getTeamABBR(equipo.trim());
                    teams.add(equipo);
                }
            }
            
            for (Element s : score) 
            {
                if (!score.text().equals("")) 
                {
                    resul = eliminarCaracteres(s.text());
                    scores.add(resul);
                }
            }
            
            //i ++ ;
        }
        
        teamsAndScores.add(teams);
        teamsAndScores.add(scores);

        return teamsAndScores;
    }
    
    private String eliminarCaracteres(String cadena){
        String xCadena = cadena;
        
        cadena = xCadena.replaceAll("[^\\dA-Za-z]", "");
        
        return cadena;
    }
    
    private String eliminarCaracteres2(String cadena){
        String xCadena = "";
        
        //cadena = xCadena.replaceAll("[^\\dA-Za-z]", "");
        //cadena = "";
        cadena = Normalizer.normalize(cadena, Normalizer.Form.NFD);
        cadena = cadena.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        
        for (int x=0;x< cadena.length();x++){
            
            if( (cadena.codePointAt(x) > 64 && cadena.codePointAt(x) < 91) ||
                (cadena.codePointAt(x) > 47 && cadena.codePointAt(x) < 56) ||
                (cadena.codePointAt(x) > 96 && cadena.codePointAt(x) < 123)) {
                 xCadena += cadena.charAt(x);
                //System.out.println(cadena.charAt(x) + " = " + cadena.codePointAt(x)); 
            }
            
        }
        System.out.println("Equipo: " + xCadena);
        return xCadena;
    }
    
    private boolean cadenaVacia(String cadena){
        if (cadena == null){
           return true;
        }
        else
            if(cadena.isEmpty()){
                return true;
            }
        return false;
    }
    
    /* FUNCIÓN PARA OBTENER CONTENIDO ESPECÍFICO */
    public ArrayList<Object> getContent(Document parse, String tag, String attr, String val)
    {
        ArrayList<Object> content = new ArrayList();

        Elements teamScore = parse.select(tag + "[" + attr + " = " + val + "]");

        int i=1;
        
        for (Element e : teamScore) 
        {
            System.out.println("Nro " + i);
            System.out.println(e.text());
            content.add(e.text()); 
            System.out.println("");
            
            i+=1;
        }

        return content;

    }
    
    /*public int results()
    {
        return 0;
    }*/
    
    private String toUpperCases(String texto){    
        return texto.toUpperCase();
    }
    
    
    /* FUNCIÓN PARA OBTENER RESULTADOS Y FORMAR SMS */
    public void getResults(ArrayList<String> teams, ArrayList<String> scores, String date, int idConf, boolean update, String rutaConfigDB, boolean isSeason) 
    {
        //DB database = new DB(rutaConfigDB);
        String contFinal = "";
        
        /* LONGITUD DE EQUIPOS */
        int t_lth = teams.size();
         System.out.println("LONGITUD DE EQUIPOS: " + t_lth);
         
        /* LONGITUD DE PUNTAJES */
        int s_lth = scores.size();
         System.out.println("LONGITUD DE PUNTAJES: " + s_lth);
         
        /* SI EL PARÁMETRO UPDATE = TRUE */ 
        if (update) 
        {
            try {
                System.out.println("\n- NRO EQUIPOS : " + t_lth + " | NRO PUNTAJES: " + s_lth);
                
                /* OBTENER CONFIGURACION ACTUAL PARA SMS Y GUARDAR LOS CAMPOS */
                ResultSet configSms = DB.runQuery("SELECT inicio_sms, sin_resultados FROM "+ DB.getDB_NAME() +".configuracion_sms");
                
                
                configSms.first();
               
                String inicioSMS = configSms.getString("inicio_sms");
                String sinRes    = configSms.getString("sin_resultados");
                
                // System.out.println(inicioSMS);
                // System.out.println(sinRes);
                // System.out.println(idConf);
                
                ResultSet catIdQuery = DB.runQuery("SELECT id_categoria FROM "+ DB.getDB_NAME() +".configuracion WHERE id_configuracion = " + idConf);
                catIdQuery.first();
                
                String catId = catIdQuery.getString("id_categoria");
                // System.out.println(catId);
                
                String inicio = inicioSMS.replace("#FECHA#", (date + " "));
                
                //System.out.println("AQUIIIIIIIIIIIIIII"+ inicio);
                
                //if(!isSeason){
                contFinal = inicio;
                // }
                
                 System.out.println(contFinal);
                
                int _teams_scores = t_lth = s_lth;
                
                if((t_lth > 0 && s_lth > 0)) //
                {
                    for (int i = 0; i < t_lth; i++) 
                    {
                        team  = teams.get(i).toUpperCase();
                        score = scores.get(i).toUpperCase();

                        System.out.println("TEAM  : " + team);
                        System.out.println("SCORE : " + score);
                        
                        if (!scores.get(i).toString().isEmpty())
                        {
                            contFinal += team + "-" + score + " ";
                        }
                    }
                }
                else
                {
                    if(isSeason)
                    {
                        contFinal += getSeasonMsg();
                        // _final += "SEASON MESSAGE";
                    }
                    else { contFinal += sinRes;}
                }
                
                String query = "SELECT contenido_completo FROM "+ DB.getDB_NAME() +".configuracion WHERE id_configuracion = " + idConf;
                // System.out.println(querySemanal);
                ResultSet rs = DB.runQuery(query);
                
                if(rs.next()){
                    rs.first();
                    
                    String contenidoCompleto = rs.getString("contenido_completo");
                    
                    System.out.println("Actual: " + contenidoCompleto);
                    System.out.println("Nuevo:  " +contFinal);
                    
                    if(contenidoCompleto == null){
                        String updateContenido = "UPDATE "+ DB.getDB_NAME() +".configuracion SET contenido_completo = '" + contFinal + "', actualizar = 1 WHERE id_configuracion = " + idConf;
                        DB.runQuery(updateContenido);
                        System.out.println("\n/***** CONTENIDO ACTUALIZADO *****/ \n");
                    }
                    else{
                        if(!contenidoCompleto.equalsIgnoreCase(contFinal)){
                            /* ACTUALIZAR CONTENIDO DE LA CONFIGURACION */
                            String updateContenido = "UPDATE "+ DB.getDB_NAME() +".configuracion SET contenido_completo = '" + contFinal + "', actualizar = 1 WHERE id_configuracion = " + idConf;
                            DB.runQuery(updateContenido);
                            System.out.println("\n/***** CONTENIDO ACTUALIZADO *****/ \n");
                        }
                        else{
                            System.out.println("\n/***** EL CONTENIDO NO SERA ACTUALIZADO *****/ \n");
                           
                        }
                    }
                    
                }
                else{
                     System.out.println("\n/***** EL CONTENIDO NO SERA ACTUALIZADO ERROR EM LA CONEXION A LA BD *****/ \n");
                }
            }
            /* SINO, SÓLO MOSTRAR LA SALIDA */ 
            catch (ClassNotFoundException ex) {
                Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SQLException ex) {
                    Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            }
       
        }

        else { System.out.println(contFinal); }
        
        // database.disconnect();
    }
    
    private String getTeamABBR(String team)
    {
        //System.out.println("QUERY: " + team);
        //String seasonMsg = "";
        try 
        {
            String queryMsg = ""
                    + "SELECT abrev "
                    + "FROM "+ DB.getDB_NAME() +".equipos "
                    + "WHERE descripcion LIKE '%" + team.trim() + "%'";
            //System.out.println("QUERY: " + queryMsg);
            ResultSet msg = DB.runQuery(queryMsg);
            
            if(msg.next()){
               team = msg.getString("abrev");
            }
            //System.out.println("ABBR: " + team);
            return team;
        } 
        
        catch (Exception e) 
        {
            return "";
        }
    }
    
    public String getSeasonMsg()
    {
        try 
        {
            String queryMsg = ""
                    + "SELECT temporada "
                    + "FROM "+ DB.getDB_NAME() +".configuracion_sms ";
            ResultSet msg = DB.runQuery(queryMsg);
            msg.first();

            // System.out.println(queryScId);

            String seasonMsg = msg.getString("temporada");

            return seasonMsg;
        } 
        
        catch (Exception e) 
        {
            return "";
        }
    }
    
    public int getMaxSeason()
    {
        //System.out.println(db.getDB_NAME());
        
        try {
            String queryMsg = ""
                    + "SELECT max_season "
                    + "FROM "+ DB.getDB_NAME() +".configuracion_sms ";
            ResultSet msg = DB.runQuery(queryMsg);
            msg.first();

            // System.out.println(queryScId);
            int seasonMsg = msg.getInt("max_season");

            return seasonMsg;
        } catch (Exception e) {
            return 7;
        }
    }
    
    /* FUNCIÓN PARA OBTENER LA FECHA EN EL FORMATO DE GOOGLE (CUANDO SON RESULTADOS SEMANALES) */
    public String fechaGoogleSemanal(String fecha, String formato) 
    {
        Date date;
        String fechaFinal = "";
            
        try {                      
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");            
            SimpleDateFormat targetFormat = new SimpleDateFormat(formato);
            
            date = originalFormat.parse(fecha);
           
            fechaFinal = targetFormat.format(date);
           
        } catch (ParseException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fechaFinal;
    }
    
    /* FUNCIÓN PARA OBTENER LA FECHA EN EL FORMATO DE GOOGLE (CUANDO SON RESULTADOS SEMANALES) */
    public String fechaMisMarcadores(String fecha, String formato) 
    {
        Date date;
        String fechaFinal = "";
            
        try {                      
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");            
            SimpleDateFormat targetFormat = new SimpleDateFormat(formato);
            
            date = originalFormat.parse(fecha);
           
            fechaFinal = targetFormat.format(date);
           
        } catch (ParseException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fechaFinal;
    }
        
    public String milisecondsConvert(long miliSeconds) 
    {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }
}
