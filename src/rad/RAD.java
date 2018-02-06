package rad;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.jsoup.nodes.Document;
import clasemaestradeappsinstanciadas.ClaseMaestraDeAppsInstanciadas;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro Mayor
 */
public class RAD {
  
    public static void main(String[] args)
    {   
        Functions functions = new Functions();
        Scrape scraper;
        ClaseMaestraDeAppsInstanciadas app_instanciada = new ClaseMaestraDeAppsInstanciadas();
        /* INICIO */
        long time_start, time_end;
        time_start = System.currentTimeMillis();
        
        if (args.length > 1) 
        {
            boolean verificar_instancia = app_instanciada.buscarAppInstanciada(args[0]);
            if (args[0].contains(".jar")) 
            { 
                if(verificar_instancia)
                {
                System.out.println("\nLA APP :: " + args[0] + " :: SE ESTÁ EJECUTANDO POR LO TANTO NO SE EJECUTARÁ EL PROCESO\n");
                System.exit(0);
                }
/**/
                else
                {/**/
                    System.out.println("\nLA APP :: " + args[0] + " :: NO ESTÁ EN EJECUCIÓN POR LO TANTO INICIA LA EJECUCIÓN DE LA MISMA");
                    scraper = new Scrape(args[1]);

                }/**comentar si quitamos la clase maestra/**/
            }
        }    

        /* FIN */
        time_end = System.currentTimeMillis();
        String totalTime = functions.milisecondsConvert(time_end - time_start);
        
        System.out.println("\nTIEMPO TOTAL DE EJECUCIÓN: " + totalTime + "\n");
        System.out.println("----------------------------------------------------------");
    }   
}
