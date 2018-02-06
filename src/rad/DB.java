/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rad;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Leandro Mayor
 */
public class DB
{
    
    private static String DATABASE_DRIVER;
    private static String DATABASE_URL;
    // private static final String PASSWORD = "88bzQztu#";
    // private static final String USERNAME = "rad_imc";
    private static String USERNAME;
    private static String PASSWORD;
    private static String DB_NAME;
    private static DB cnx = new DB();
    private static PreparedStatement statement; 
    // init connection object
    private static Connection connection;
    // init properties object
    private static Properties properties;

    private static String getDATABASE_DRIVER() {
        return DATABASE_DRIVER;
    }

    private static void setDATABASE_DRIVER(String DATABASE_DRIVER) {
        DATABASE_DRIVER = DATABASE_DRIVER;
    }

    private static String getDATABASE_URL() {
        return DATABASE_URL;
    }

    private static void setDATABASE_URL(String DATABASE_URL) {
        DATABASE_URL = DATABASE_URL;
    }

    private static String getUSERNAME() {
        return USERNAME;
    }

    private static void setUSERNAME(String USERNAME) {
        USERNAME = USERNAME;
    }

    private static String getPASSWORD() {
        return PASSWORD;
    }

    private static void setPASSWORD(String PASSWORD) {
        PASSWORD = PASSWORD;
    }
    
    private static void setDB_NAME(String DB_NAME) {
        DB_NAME = DB_NAME;
    }
    
    public static String getDB_NAME() {
        return DB_NAME;
    }

    public static void DB(String rutaArchivo) 
    {
        Properties p = obtenerParametros(rutaArchivo);
        
        DATABASE_DRIVER = "com.mysql.jdbc.Driver";
        
        String insignia_deportivo_host    = p.getProperty("insignia_deportivo_db_host");
        String insignia_deportivo_user    = p.getProperty("insignia_deportivo_db_user");
        String insignia_deportivo_pass    = p.getProperty("insignia_deportivo_db_pass");
        String insignia_deportivo_db_name = p.getProperty("insignia_deportivo_db_name");

        DB_NAME = insignia_deportivo_db_name;
        USERNAME = insignia_deportivo_user;
        PASSWORD = insignia_deportivo_pass;
        DATABASE_URL = "jdbc:mysql://" + insignia_deportivo_host + "/" + insignia_deportivo_db_name;
    }
    
    public static Properties obtenerParametros(String archivo) {
        Properties props = new Properties();

        try {
            FileInputStream file = new FileInputStream(archivo);
            props.load(file);
        } catch (Exception e) {
            System.err.println("ERROR AL OBTENER LA RUTA DEL ARCHIVO " + e.getMessage());
            System.exit(0);
        }

        return props;

    }

    // create properties
    private static Properties getProperties() 
    {
        if (properties == null) 
        {
            properties = new Properties();
            properties.setProperty("user", getUSERNAME());
            properties.setProperty("password", getPASSWORD());
        }
        
        return properties;
    }

    // connect database
    private static Connection connect() throws SQLException, ClassNotFoundException 
    {
        if (connection == null) 
        {
            try 
            {
                Class.forName(getDATABASE_DRIVER());
                connection = DriverManager.getConnection(getDATABASE_URL(), getProperties());
            } 
            
            catch (SQLException e) 
            {
                System.out.println("ERROR EN LA CONEXION CON LA BASE DE DATOS");
                e.printStackTrace();
            }
        }
        
        return connection;
    }

    // disconnect database
   /* private static void disconnect() 
    {
        if (connection != null)
        {
            try 
            {
                connection.close();
                connection = null;
            } 
            
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
        }
    }*/
    
    public static ResultSet runQuery(String query) throws ClassNotFoundException
    {
        ResultSet rs = null;
        
        try 
        {
            statement = connect().prepareStatement(query);
            
            if(query.toUpperCase().startsWith("SELECT")) { rs = statement.executeQuery(); }
            
            else
            {
                statement.executeUpdate(query);
                //disconnect();
            }

        }
        
        catch (SQLException e) { 
            e.printStackTrace(); 
        } 
        
        return rs;
    }
}
