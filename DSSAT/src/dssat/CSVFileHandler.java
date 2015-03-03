/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dssat;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author rkmalik
 */
public class CSVFileHandler {
    
    // Implemented
    public ArrayList getCountyList_SoilDB () {
        Set <String> countySet = new  HashSet<String> ();
        ArrayList <String> countyList = new ArrayList<String>();
        CSVReader reader;
        String [] nextLine; 
        
        try {
            reader = new CSVReader(new FileReader(".\\data\\dssat_countywise_list_of_soils.csv"));
            while ((nextLine = reader.readNext()) != null) {                 
                
                String county = nextLine[1];                               
                if (county.isEmpty() == false) { 
                    countySet.add(county);
                }
            }            
        } catch (IOException e) {} 
        
        
        for (String countyName : countySet) {
            countyList.add(countyName);            
        }        
        Collections.sort(countyList);
        return countyList;
    }
      
    // Implemented
    public ArrayList getCountyBasedSoilList_SoilDB(String countyName) {
        
        CSVReader reader;
        String [] nextLine;
        ArrayList<String> soilList = new  ArrayList<String> ();
        
        try {
            reader = new CSVReader(new FileReader(".\\data\\dssat_countywise_list_of_soils.csv"));
            while ((nextLine = reader.readNext()) != null) {           
            
                String county = nextLine[1];                               
                if (county.isEmpty() == false && county.equals(countyName)) { 
                   soilList.add(nextLine[4]);  
                }
            }            
        } catch (IOException e) {} 

        return soilList;        
    }
    
    // Implemented
    public ArrayList getCountyList_GlobalDB ()
    {
        
        CSVReader reader;
        String [] nextLine;    
        ArrayList<String> countyList = new  ArrayList<String> ();
        //String query = "Select * from counties_centroid where state = 'FL'";    
        
        try {
            reader = new CSVReader(new FileReader(".\\data\\counties_centroid.csv"));
            while ((nextLine = reader.readNext()) != null) {                
                String state = nextLine[2];                               
                if (state.equals("FL")) { 
                    countyList.add(nextLine[1]);
                }
            }            
        } catch (IOException e) {}       

        return countyList;
    }
    
    // Implemented
    public void getCountyLocation_GlobalDB (String countyName, Location pos)  {
         
        CSVReader reader;
        String [] nextLine;        
        
        try {
            reader = new CSVReader(new FileReader(".\\data\\counties_centroid.csv"));
            while ((nextLine = reader.readNext()) != null) {
                
                String state = nextLine[2];
                String county = nextLine[1];
               
                if (state.equals("FL") && county.equalsIgnoreCase(countyName)) { 
                    String longitude = nextLine[11];
                    
                    String latitude = nextLine[12];
                    
                    pos.latitude = Double.parseDouble(latitude);
                    pos.longitude = Double.parseDouble(longitude);
                    
                    break;                    
                }
            }            
        } catch (IOException e) {}
    }
    
    // Implemented
    public String getWeatherStationId_GlobalDB (String weatherstationname)
   {        

        CSVReader reader;
        String [] nextLine;
        String location;

        try {
            reader = new CSVReader(new FileReader(".\\data\\fawn_lookup.csv"));
            while ((nextLine = reader.readNext()) != null) {
                location = nextLine[2];
                location = location.toLowerCase();
                weatherstationname = weatherstationname.toLowerCase();
                if (location.equals(weatherstationname))
                    return nextLine[0];
            }            
        } catch (IOException e) {}
                
        return null;
    }
    
    // Implemented
    public ArrayList getWeatherStations_GlobalDB(Location loc) {        

        double longright = loc.longitude + 11.17;
        double longleft = loc.longitude - 11.17;
        double latupper = loc.latitude + 7.15;
        double latlower = loc.latitude - 7.15;
        
        CSVReader reader;
        String [] nextLine;
        String location;
        ArrayList<String> weatherstinrange = new  ArrayList<String> ();
        
        Double latitude;
        Double longitude;        
        
        try {
            reader = new CSVReader(new FileReader(".\\data\\fawn_lookup.csv"));
            while ((nextLine = reader.readNext()) != null) {
                
                latitude = Double.parseDouble(nextLine[5]);
                longitude = Double.parseDouble(nextLine[6]);
                
                if ((longitude >= longleft) && (longitude <= longright) && (latitude >= latlower) && (latitude <= latupper))
                {
                    String name = nextLine [2];
                    weatherstinrange.add(name);
                }        
            }            
        } catch (IOException e) {}       

        return weatherstinrange;
    }   
}
