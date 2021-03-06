/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dssat;

import static dssat.DSSATMain.curdirpath;
import static dssat.DSSATMain.datadir;
import static dssat.DSSATMain.dirseprator;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author rkmalik
 */
public class WeatherFileSystem extends BaseFileSystem{
    
    private static WeatherFileSystem instance = null;
    
    private WeatherFileSystem () {        
        fileName = null; 
        fileLocation = null;
        fileType = null;
        incache = new HashMap <String, String>();
    }
    
    // This mehto// Update table will update the data for the required keys 
    // UpdateHashTable ()
    public static WeatherFileSystem getInstance ()
    {
        if (instance == null) {
            
            instance = new WeatherFileSystem();
        }
        return ((WeatherFileSystem)instance); 
    }
    
    // This mehtod take a comma seperated key value pair, parse those keyvalue pairs and then updaate the incache
    public void UpdateCache (String keyvalue) {  
        String [] tokens  = keyvalue.split(",");        
        for (int i = 0; i < tokens.length; i++)
        {
            String key = tokens[i];
            i++;
            String value = tokens[i];
            
            
            System.out.printf(key + " " + value + " ---> ");
            incache.put(key, value);           
        }
               
    }
    
    public HashMap <String, String> ReadFromFile (String attributName) {
        
        return null;
    }
    
    // This method is called on the Finish Button to create the weather file. 
    public void WriteToFile () {
        
        // prepare the file name from the organization name and the site index number and then write to that file
        
        String orgName= incache.get("Site");
        String siteIndex =  incache.get("SiteIndex");
        String year = incache.get("PlantingYear");
        
        Integer plantingyear = Integer.parseInt(year);
        Integer twodigyr = plantingyear % 100;
        String wthFileName = orgName  + twodigyr + "01" +  ".WTH";
        System.out.println(wthFileName);       
        
        wthFileName = datadir + dirseprator + wthFileName;
        File file = new File (wthFileName);
        String writeBuffer = null;
        
        PrintWriter pr = null;
        try {
             pr = new PrintWriter(file);   
             writeBuffer = new String ("*WEATHER : \n");
             pr.println(writeBuffer);
             writeBuffer = new String ("@ INSI      LAT     LONG  ELEV   TAV   AMP REFHT WNDHT");
             pr.println(writeBuffer);  
             
             double latitude = -34.23; double longitude = -34.23;
             double elevation = -99; double tavg = -99; double tamp = -99; 
             double tmht = -99; double wmht = -99;
             
             writeBuffer =  new String ();
                                        //Empty 2 Char, Org 2 Char, Site 2Char, Space 1 Char, latitude 8Chars with 3 dec
             writeBuffer = String.format("%.2s%.2s%.2s%.1s%8.3f%.1s%8.3f%.1s%5.0f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f", "  ", orgName, siteIndex, " ", latitude, " ", longitude, " ", elevation, " ", tavg, " ", tamp, " ", tmht, " ", wmht);
             pr.println(writeBuffer);
             
             
             int julianday = 56001; double solarrad = -99;  
             double tmax = -99; double tmin = -99; double rain = -99; 
             double dewp = -99; double wind = -99; double par = -99;
             
             
             writeBuffer = new String ("@DATE  SRAD  TMAX  TMIN  RAIN  DEWP  WIND  PAR");
             pr.println(writeBuffer);
             writeBuffer = new String ();
             
             URL url = null;
             InputStream is = null;
             
             /*Bring the Weather details for last 10 Years and write the details to the WTH File. */
               try {

                    String query = new String(
                                "SELECT%20*%20FROM%20FAWN_historic_daily_20140212%20"+
                                "WHERE%20(%20yyyy%20BETWEEN%20"+(plantingyear-10)+"%20AND%20"+plantingyear.toString()+")" +
                                "%20AND%20(%20LocId%20=%20"+incache.get("StationLocationId")+")"+
                                "%20ORDER%20BY%20yyyy%20DESC%20");

                    String urlStr="http://abe.ufl.edu/bmpmodel/xmlread/rohit.php?sql="+query;

                    //System.out.println(urlStr);
                    URL uflconnection = new URL(urlStr);
                    HttpURLConnection huc = (HttpURLConnection) uflconnection.openConnection();
                    HttpURLConnection.setFollowRedirects(false);
                    huc.setConnectTimeout(15 * 1000);
                    huc.setRequestMethod("GET");
                    huc.connect();
                    InputStream input = huc.getInputStream();                    
                    BufferedReader in = new BufferedReader(new InputStreamReader(input));                    
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                           pr.println(inputLine);
                    }
                    in.close();
                    //System.out.println("Created teh connection successfully");

               } catch (MalformedURLException me) {
			me.printStackTrace();
			//return 21.4;
		}catch (Exception e){
                   e.printStackTrace();
               }          
             /*DBConnect weather_historic_daily = new DBConnect (ServerDetails.SERVER_NUM_RONLY, ServerDetails.weather_historic_daily_dbname);
             

            System.out.println (query);
             ResultSet result = weather_historic_daily.Execute (ServerDetails.weather_historic_daily_dbname, query.toString());
             
             try {
                 while (result.next()) {      
                    String julian = result.getString("yyyy");
                    julian = julian.substring(2);                   

                    int yr = Integer.parseInt(julian);
                    int day = Integer.parseInt(result.getString("doy"));
                    julian = String.format("%02d%03d", yr, day);
                    

                    if (result.getString("Tmin") != null)
                        tmin = Double.parseDouble(result.getString("Tmin"));
                    else 
                        tmin = -99.0;
                    
                    if (result.getString("Tmax") != null)
                        tmax = Double.parseDouble(result.getString("Tmax"));
                    else 
                        tmax = -99.0;
                    
                    if (result.getString("Tavg") != null)
                        tavg = Double.parseDouble(result.getString("Tavg"));
                    else 
                        tavg = -99.0;

                    
                    //writeBuffer = String.format("%5d%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f",  julianday, " ", solarrad, " ", tmax, " ", tmin, " ", rain, " ", dewp, " ", wind, " ", par);
                    writeBuffer = String.format("%.5s%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f",  julian, " ", solarrad, " ", tmax, " ", tmin, " ", rain, " ", dewp, " ", wind, " ", par);
                    pr.println(writeBuffer);
                }                
                 
             } catch (SQLException e) {                 
                 e.printStackTrace();
             }*/
  
        } catch (Exception e) {        
            e.printStackTrace();
        } finally {
            
            if (pr!= null)
                pr.close ();
        }
       
        
        // Here I need to read all the data from the controls and write the data to the files. 
        for (int i = 0; i < incache.size(); i++) {
            
            
            
        }       
    }
    
}
