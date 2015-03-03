/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dssat;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author rkmalik
 */

    
public class DBConnect {
    
    final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  	
//    private Connection connection = null;
//    private Statement statement = null;
    private ResultSet result = null; 
    

    private static Connection connection_global_db = null;
    private static Statement statement_global_db = null;
    //private ResultSet result_global_db = null;
    
    private static Connection connection_soil_db = null;
    private static Statement statement_soil_db = null;
    
    
    private static Connection connection_weather_historic_daily_db = null;
    private static Statement statement_weather_historic_daily_db = null;

    
    DBConnect (int serverNum, String dbname) {
        
        try {
            Class.forName(JDBC_DRIVER);           
            String newurl = null;
            String userName = null;
            String password = null;
            
            
            switch (serverNum) {
                
                case 1: 
                    newurl = ServerDetails.DB_URL_SERVER1+dbname;
                    userName = ServerDetails.USER_SERVER1;
                    password =  ServerDetails.PASS_SERVER1;
                    break;
                    
                case 2: 
                    newurl = ServerDetails.DB_URL_SERVER2+dbname;
                    userName = ServerDetails.USER_SERVER2;
                    password =  ServerDetails.PASS_SERVER2;
                    break;
                
                
            }
            
            if (dbname.equals(ServerDetails.global_dbname) && connection_global_db == null) {        
        
                try {
                connection_global_db = DriverManager.getConnection(newurl, userName, password);
                statement_global_db = connection_global_db.createStatement();
                } catch (SQLException e){
                    e.printStackTrace();                    
                }      
                    
            }
            if (dbname.equals(ServerDetails.soil_dbname) && connection_soil_db == null) {        
        
                try {
                connection_soil_db = DriverManager.getConnection(newurl, userName, password);
                statement_soil_db = connection_soil_db.createStatement();
                } catch (SQLException e){
                    e.printStackTrace();                    
                }      
                    
            }
            if (dbname.equals(ServerDetails.weather_historic_daily_dbname) && connection_weather_historic_daily_db == null) {        
        
                try {
                connection_weather_historic_daily_db = DriverManager.getConnection(newurl, userName, password);
                statement_weather_historic_daily_db = connection_weather_historic_daily_db.createStatement();
                } catch (SQLException e){
                    e.printStackTrace();                    
                }      
                    
            }
            
        } catch (Exception e){ 
            e.printStackTrace(); 
        } 

    } 
    

    public Connection getConnection (String dbName) {
        
        if (dbName.equals(ServerDetails.global_dbname))
            return connection_global_db;
        if (dbName.equals(ServerDetails.weather_historic_daily_dbname))
            return connection_global_db;
        if (dbName.equals(ServerDetails.soil_dbname))
            return connection_global_db;
        
        return null;
   }
    
    public Statement getStatement (String dbName) {
        
        if (dbName.equals(ServerDetails.global_dbname))
            return statement_global_db;
        if (dbName.equals(ServerDetails.weather_historic_daily_dbname))
            return statement_weather_historic_daily_db;
        if (dbName.equals(ServerDetails.soil_dbname))
            return statement_soil_db;
        
        return null;
   }
    
    public ArrayList getCountyList_SoilDB () {
       ArrayList<String> countyList = new  ArrayList<String> ();
        String query = "Select distinct County from dssat_countywise_list_of_soils where County IS NOT NULL";    
        
        try {
            result = statement_soil_db.executeQuery(query);
            while (result.next()) {                
                String name = result.getString("County");
                countyList.add(name);
            } 
            
        } catch (SQLException e) {            
            e.printStackTrace();            
        }
        
        return countyList;
    }
    
    public ArrayList getCountyBasedSoilList_SoilDB(String countyName) {
    
        //System.out.println(countyName);
        String query = "Select Soil_Name from dssat_countywise_list_of_soils where County = '" + countyName +"'"; 
        System.out.println(query);
        ArrayList<String> soilList = new  ArrayList<String> ();
        
        try {
            result = statement_soil_db.executeQuery(query);
            //while (result.next()) {
               result.next ();
            while (result.next()) {                
                String name = result.getString("Soil_Name");
                soilList.add(name);
                //System.out.print (name + " ");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soilList;        
    }
   
   public void getCountyLocation_GlobalDB (String countyName, Location pos)  {
         String query = "Select latitude, longitude from counties_centroid where state = 'FL' AND county = '" + countyName +"'";  
         //System.out.println(query);
         
         
        try {
            result = statement_global_db.executeQuery(query);
            //while (result.next()) {
               result.next ();
               String latitude = result.getString("latitude");
               String longitude = result.getString ("longitude");
  
               pos.latitude = Double.parseDouble(latitude);
               pos.longitude = Double.parseDouble(longitude);
  
        } catch (SQLException e) {            
            e.printStackTrace();            
        }
        
    }   
    
    public ArrayList getCountyList_GlobalDB ()
    {
        ArrayList<String> countyList = new  ArrayList<String> ();
        String query = "Select * from counties_centroid where state = 'FL'";    
        
        try {
            result = statement_global_db.executeQuery(query);
            while (result.next()) {                
                String name = result.getString("County");
                countyList.add(name);
            }
            
            
        } catch (SQLException e) {            
            e.printStackTrace();            
        }
        return countyList;
    }
    
   public String getWeatherStationId_GlobalDB (String weatherstationname)
   {
        
        String query = null;
        String locid = null;
       
        
        try {           

            query = "Select distinct LocId from fawn_lookup where Location = '" + weatherstationname + "'";
            System.out.println(query);
            result = statement_global_db.executeQuery(query);    
           
            while (result.next()) {                
                locid = result.getString("LocId");      
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locid;
    }
    public ArrayList getWeatherStations_GlobalDB(Location loc) {
        
        String query = null;
        double longright = loc.longitude + 11.17;
        double longleft = loc.longitude - 11.17;
        double latupper = loc.latitude + 7.15;
        double latlower = loc.latitude - 7.15;
        
        
        ArrayList<String> weatherstinrange = new  ArrayList<String> ();

        
        try {           

            query =  "select location, longitude, latitude from fawn_lookup where (longitude between " + longleft + " and " + longright + " ) and ( latitude between " + latlower + " and " + latupper + ")" ;
            result = statement_global_db.executeQuery(query);    
           
            while (result.next()) {                
                String name = result.getString("Location");

                weatherstinrange.add(name);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherstinrange;
    }
    
    public ResultSet Execute (String dbName, String query) {
        
        Statement stmt = getStatement(dbName);
        ResultSet result = null;
        
        try {
            result = stmt.executeQuery(query);
        }  catch (SQLException e) {
            e.printStackTrace();
        }
        return result;        
    }
    
    public void Close ()
    {
        if (result != null) {
            try {
                result.close();
            } catch (SQLException e) { 
                e.printStackTrace();
            }
        }
        if (statement_global_db != null) {
            try {
                statement_global_db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection_global_db != null) {
            try {
                connection_global_db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        
        if (statement_soil_db != null) {
            try {
                statement_soil_db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection_soil_db != null) {
            try {
                connection_soil_db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } 
    }
}
		
		

    
