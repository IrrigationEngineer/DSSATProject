/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dssat;


import com.opencsv.CSVReader;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;





/**
 *
 * @author rkmalik
 */
public class DSSATMain extends javax.swing.JFrame {

    static WeatherFileSystem wthfile;
    static CultivaFileSystem cultivafile;
    static IrrigationFertilizer irrigationframe;    
    static int weatherstationid;
    
    //static DBConnect global_db = null;    
    //static DBConnect soil_db = null;    
    //static DBConnect weather_historic_daily = null;
    static HashMap <String, String> cropname_culfilename = null;
    
    CalendarProgram calender = null;

    
    /**
     * Creates new form DSSATMain
     */
    public DSSATMain() {
        initComponents();
        
        initMyComponents();
        
        initTextFields ();
        //initPlantingDate ();
        
        initGlobalDBInfo ();       
        
        initSoilInfo (); 
        
        initWeatherHistoric ();
        
        initCropInfo ();
        
        initFrameSize ();
        
    }
    
    private void initFrameSize ()
    {
       // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setBounds(0,0,screenSize.width, screenSize.height);
        
       this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        //this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
       this.pack();
        this.setVisible(true);

        //pack();
    }
    
    
    private void initMyComponents(){
        jMenuBar1 = new CommonMenuBar();
        setJMenuBar(jMenuBar1);
        jMenuBar1.setVisible(true);
        
    }
    
    private void initTextFields () {
        jTextFieldOrgName.setDocument(new LimitedPlainDocument(2));        
        jTextFieldSiteIndex.setDocument(new LimitedPlainDocument(2));        
        Date date = new Date ();
        jDateChooser1.setDate(date);        
    }
    
    private void initCropInfo () {
        
        
        //soil_db = new DBConnect (ServerDetails.SERVER_NUM_RW, ServerDetails.soil_dbname);        
        //StringBuilder searchSpec = new StringBuilder ("");
        //searchSpec.append("SELECT Name, CultivarFile FROM dssat_crop_lookup ORDER BY Name ASC");        
        //System.out.println (searchSpec);
        //ResultSet result = soil_db.Execute (ServerDetails.soil_dbname, searchSpec.toString());

        CSVReader reader;
        String [] nextLine;
        ArrayList <String> cropName = new ArrayList <String> ();
        cropname_culfilename = new HashMap <String, String> () ;
        try {
            reader = new CSVReader(new FileReader(".\\data\\dssat_crop_lookup.csv"));
            while ((nextLine = reader.readNext()) != null) {
                cropName.add(nextLine[1]);
                cropname_culfilename.put(nextLine[1], nextLine[3]);
            }            
        } catch (IOException e) {}
        
        // Sort the arrayList and Initialize the comboboxes.
        Collections.sort(cropName);
        for (int i = 0; i <cropName.size(); i++)
        {
            jComboBoxPrevCropList.addItem(cropName.get(i));
            jComboBoxCropList.addItem(cropName.get(i)); 
        }
   
        
        /*try {
                while (result.next()) {      
                    cropName = result.getString("Name");
                    crpFileName = result.getString("CultivarFile");
                    cropname_culfilename.put(cropName, crpFileName);
                    jComboBoxPrevCropList.addItem(cropName);
                    jComboBoxCropList.addItem(cropName);                    
                    
                }                
                 
             } catch (SQLException e) {                 
                 e.printStackTrace();
             }*/
        
        initCultivar ();
    }
    
    private void initCultivar ()
    {
         // TODO add your handling code here:
        // TODO add your handling code here:

        
        String cropName = (String)jComboBoxCropList.getSelectedItem();
        HashMap <String, String> cultivarHashMap = null; 
        
        StringBuilder cultivadata = new StringBuilder ();
        
        cultivadata.append ("Crop Name,");        
        cultivadata.append(cropName);
        
        
        cultivadata.append (",CultivarFile,");  
        String culfileName = cropname_culfilename.get(cropName);        
        cultivadata.append(culfileName); 
        
        System.out.println ("Crop Name" + cropName);
        
        if (cultivafile == null)
            cultivafile = CultivaFileSystem.getInstance();
         
        cultivafile.UpdateCache(cultivadata.toString());         
        cultivarHashMap = cultivafile.ReadFromFile("VAR-NAME");
        
        ArrayList<String> cultivalist = new ArrayList<String> ();
        Set keys = cultivarHashMap.keySet();
        Iterator itr = keys.iterator();
 
        String key;
        String value;
        while(itr.hasNext())
        {
            key = (String)itr.next();
            value = cultivarHashMap.get(key);
            cultivalist.add(value);
        }
        Collections.sort(cultivalist);        
        int itemCount = jComboBoxCultivar.getItemCount();
        
        for(int i=0;i<itemCount;i++){
            jComboBoxCultivar.removeItemAt(0);
        }
        
        for (int i = 0; i < cultivalist.size(); i++) {
            key = cultivalist.get(i);
            String cultivarname = key.trim();
            System.out.printf (cultivarname + "->");
            if (cultivarname.length()>0)
                jComboBoxCultivar.addItem(cultivarname);
        }
        
    }
    
    private void initSoilInfo () {
        
        //soil_db = new DBConnect (ServerDetails.SERVER_NUM_RW, ServerDetails.soil_dbname);
        
        CSVFileHandler csvfile = new CSVFileHandler ();
        
        // From the global db initialize the county comboBox
        ArrayList <String> soilList = null;
        ArrayList <String> countyList = null;
        countyList = csvfile.getCountyList_SoilDB();
        Collections.sort(countyList);
        
        for (int i = 0; i < countyList.size(); i++) {
            
            String countyName = countyList.get(i);
            countyName = countyName.substring(0, 1).toUpperCase() + countyName.substring(1).toLowerCase(); 
          soilInfoCountyCombobox.addItem(countyName);     
        }
        //String countyName = (String) soilInfoCountyCombobox.getSelectedItem();
        soilList = csvfile.getCountyBasedSoilList_SoilDB(countyList.get(0));
        

        for (int i = 0; i < soilList.size(); i++) {
          soilSeriesCombobox.addItem(soilList.get(i));            
        }
              
    }
    
    
    private void initWeatherHistoric  ()
    {
        //weather_historic_daily = new DBConnect (ServerDetails.SERVER_NUM_RONLY, ServerDetails.weather_historic_daily_dbname);
    }
    
    private void initGlobalDBInfo  ()
    {
        

        CSVFileHandler csvfile = new CSVFileHandler ();
        // From the global db initialize the county comboBox
        ArrayList <String> countyList = null;
        countyList = csvfile.getCountyList_GlobalDB();
        Collections.sort(countyList);
        for (int i = 0; i < countyList.size(); i++) {
            String countyName = countyList.get(i);
            countyName = countyName.substring(0, 1).toUpperCase() + countyName.substring(1).toLowerCase(); 
            countyNameGlobal.addItem(countyName);            
        }
        
        // By default get the first item in the list and get the Co-Ordinates of the at item and populate the 
        // weather stations based on the first item in the combobox 

        Location countypos = new Location ();
        csvfile.getCountyLocation_GlobalDB (countyList.get(0), countypos); 
                 
        ArrayList<String> weatherstations = null;
        weatherstations = csvfile.getWeatherStations_GlobalDB(countypos);
        Collections.sort(weatherstations);
        //weatherStComboBox.
        for (int i = 0; i < weatherstations.size(); i++) {
            
            String wtstationname = weatherstations.get(i);
            wtstationname = wtstationname.substring(0, 1).toUpperCase() + wtstationname.substring(1).toLowerCase(); 
            
            System.out.println (wtstationname);
            weatherStComboBox.addItem(wtstationname);            
        }
 
    }
    
    
    /*private void initGlobalDBInfo  ()
    {
        
        //System.out.println(fawn_lookup_dbname);
        global_db = new DBConnect (ServerDetails.SERVER_NUM_RONLY, ServerDetails.global_dbname);
        
        // From the global db initialize the county comboBox
        ArrayList <String> countyList = null;
        countyList = global_db.getCountyList_GlobalDB();
        Collections.sort(countyList);
        for (int i = 0; i < countyList.size(); i++) {
            String countyName = countyList.get(i);
            countyName = countyName.substring(0, 1).toUpperCase() + countyName.substring(1).toLowerCase(); 
            countyName1.addItem(countyName);            
        }
        
        // By default get the first item in the list and get the Co-Ordinates of the at item and populate the 
        // weather stations based on the first item in the combobox 

        Location countypos = new Location ();
        global_db.getCountyLocation_GlobalDB (countyList.get(0), countypos); 
                 
        ArrayList<String> weatherstations = null;
        weatherstations = global_db.getWeatherStations_GlobalDB(countypos);
        Collections.sort(weatherstations);
        //weatherStComboBox.
        for (int i = 0; i < weatherstations.size(); i++) {
            
            String wtstationname = weatherstations.get(i);
            wtstationname = wtstationname.substring(0, 1).toUpperCase() + wtstationname.substring(1).toLowerCase(); 
            
            System.out.println (wtstationname);
            weatherStComboBox.addItem(wtstationname);            
        }
 
    }*/
    
    /*private void initGlobalDBInfo  ()
    {
        
        //System.out.println(fawn_lookup_dbname);
        global_db = new DBConnect (ServerDetails.SERVER_NUM_RONLY, ServerDetails.global_dbname);
        
        // From the global db initialize the county comboBox
        ArrayList <String> countyList = null;
        countyList = global_db.getCountyList_GlobalDB();
        Collections.sort(countyList);
        for (int i = 0; i < countyList.size(); i++) {
            String countyName = countyList.get(i);
            countyName = countyName.substring(0, 1).toUpperCase() + countyName.substring(1).toLowerCase(); 
            countyName1.addItem(countyName);            
        }
        
        // By default get the first item in the list and get the Co-Ordinates of the at item and populate the 
        // weather stations based on the first item in the combobox 

        Location countypos = new Location ();
        global_db.getCountyLocation_GlobalDB (countyList.get(0), countypos); 
                 
        ArrayList<String> weatherstations = null;
        weatherstations = global_db.getWeatherStations_GlobalDB(countypos);
        Collections.sort(weatherstations);
        //weatherStComboBox.
        for (int i = 0; i < weatherstations.size(); i++) {
            
            String wtstationname = weatherstations.get(i);
            wtstationname = wtstationname.substring(0, 1).toUpperCase() + wtstationname.substring(1).toLowerCase(); 
            
            System.out.println (wtstationname);
            weatherStComboBox.addItem(wtstationname);            
        }
 
    }*/

    //********Class to limit the number of characters a user can enter into a field.*********
    public class LimitedPlainDocument extends javax.swing.text.PlainDocument {

         private int maxLen = -1;

         /** Creates a new instance of LimitedPlainDocument */
         public LimitedPlainDocument() {
         }

         public LimitedPlainDocument(int maxLen) {
              this.maxLen = maxLen;
         }

         public void insertString(int param, String str, javax.swing.text.AttributeSet attributeSet) throws javax.swing.text.BadLocationException {
              if (str != null && maxLen > 0 && this.getLength() + str.length() > maxLen) {
                   java.awt.Toolkit.getDefaultToolkit().beep();
                   return;
              }

              super.insertString(param, str, attributeSet);
         }

    }
    public String getOrgName () {
        return jTextFieldOrgName.getText();
    }
    public String getSiteIndex () {
        return jTextFieldSiteIndex.getText();
        
    }
    
    public String getCountyName () {
        return (String)countyNameGlobal.getSelectedItem ();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxPrevCropList = new javax.swing.JComboBox();
        jComboBoxCultivar = new javax.swing.JComboBox();
        jComboBoxCropList = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        countyNameGlobal = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jTextFieldOrgName = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jTextFieldSiteIndex = new javax.swing.JTextField();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jTextField7 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        weatherStComboBox = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        jNextButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox();
        jTextField3 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        soilInfoCountyCombobox = new javax.swing.JComboBox();
        soilSeriesCombobox = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Crop", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 3, 16), new java.awt.Color(0, 51, 204))); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(620, 193));

        jLabel5.setText("Previous Crop From List");

        jLabel6.setText("Crop Name");

        jLabel7.setText("Cultivar");

        jComboBoxCropList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCropListItemStateChanged(evt);
            }
        });
        jComboBoxCropList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCropListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBoxCropList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxPrevCropList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxCultivar, 0, 200, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxPrevCropList, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxCropList, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxCultivar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(73, Short.MAX_VALUE))
        );

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel25.setText("Planting Date :");

        countyNameGlobal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countyNameGlobalActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel23.setText("Your Location :");

        jTextFieldOrgName.setColumns(2);
        jTextFieldOrgName.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 102, 204)));

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel26.setText("Organization Name:");

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel27.setText("Site Name:");

        jTextFieldSiteIndex.setColumns(2);
        jTextFieldSiteIndex.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 102, 204)));

        jDateChooser1.setDateFormatString("MMM, dd, yyyy");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jTextFieldOrgName, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(jTextFieldSiteIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(72, 72, 72)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(countyNameGlobal, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(104, 104, 104))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(countyNameGlobal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextFieldOrgName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldSiteIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Weather Station", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 3, 16), new java.awt.Color(0, 51, 204))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(620, 193));

        jLabel1.setText("Nearest FAWN Weather Station");
        jLabel1.setToolTipText("");

        weatherStComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weatherStComboBoxActionPerformed(evt);
            }
        });

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssat/images/Crop_Height.gif"))); // NOI18N
        jLabel22.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(136, 136, 136)
                        .addComponent(weatherStComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel22))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(weatherStComboBox))
                .addGap(27, 27, 27)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        jNextButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssat/images/Next-icon.png"))); // NOI18N
        jNextButton.setText("Next");
        jNextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jNextButtonMouseClicked(evt);
            }
        });
        jNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNextButtonActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bed System", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 3, 16), new java.awt.Color(0, 51, 204))); // NOI18N
        jPanel4.setPreferredSize(new java.awt.Dimension(620, 193));

        jLabel11.setText("Bed Width");

        jTextField2.setColumns(10);

        jLabel13.setText("Plastic Mulch Color");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Black", "Silver", "White", " " }));

        jLabel14.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Planting");

        jLabel15.setText("Planting Method");

        jLabel16.setText("Planting Spacing in Row");

        jLabel17.setText("Planting Depth");

        jLabel18.setText("Row Spacing");

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Transplant", "Seed", " " }));

        jTextField3.setColumns(5);

        jLabel19.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel19.setText("Feet");

        jLabel20.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel20.setText("Inches");

        jLabel21.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel21.setText("Inches");

        jTextField4.setColumns(5);

        jTextField5.setColumns(5);

        jLabel24.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel24.setText("Inches");

        jLabel29.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel29.setText("Inches");

        jTextField6.setColumns(10);

        jLabel12.setText("Bed Height");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel16)))
                        .addGap(58, 58, 58)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jComboBox8, 0, 79, Short.MAX_VALUE)
                                .addGap(12, 12, 12)))
                        .addGap(56, 56, 56)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(27, 27, 27)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)))
                        .addGap(100, 100, 100))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(236, 236, 236))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jTextField6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel29))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jTextField2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)))
                        .addGap(35, 35, 35)
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox7, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(83, 83, 83))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29))))
                .addGap(24, 24, 24)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel17)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel18)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Soil Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 3, 16), new java.awt.Color(0, 51, 204))); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(620, 193));

        jLabel3.setText("County");

        jLabel4.setText("Soil Series Name");

        soilInfoCountyCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soilInfoCountyComboboxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(soilInfoCountyCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(32, 32, 32)
                        .addComponent(soilSeriesCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(456, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(soilInfoCountyCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(soilSeriesCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTree1.setBackground(new java.awt.Color(240, 240, 240));
        jTree1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Projects");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Project1");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("violet");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("red");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("yellow");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Project2");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Alachua");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Project3");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("hot dogs");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("pizza");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("ravioli");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("bananas");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNextButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNextButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(87, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void countyNameGlobalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countyNameGlobalActionPerformed
        // TODO add your handling code here:

        String countyName = (String) countyNameGlobal.getSelectedItem();

        // By default get the first item in the list and get the Co-Ordinates of the at item and populate the
        // weather stations based on the first item in the combobox

        int itemCount = weatherStComboBox.getItemCount();
        for(int i=0;i<itemCount;i++){
            weatherStComboBox.removeItemAt(0);
        }

        CSVFileHandler csvfilehandler = new CSVFileHandler ();
        Location countypos = new Location ();
        csvfilehandler.getCountyLocation_GlobalDB (countyName, countypos);

        ArrayList<String> weatherstations = null;
        weatherstations = csvfilehandler.getWeatherStations_GlobalDB(countypos);
        Collections.sort(weatherstations);
        //weatherStComboBox.
        for (int i = 0; i < weatherstations.size(); i++) {
            String wtstationname = weatherstations.get(i);
            wtstationname = wtstationname.substring(0, 1).toUpperCase() + wtstationname.substring(1).toLowerCase(); 
            
            System.out.println (wtstationname);
            weatherStComboBox.addItem(wtstationname);
        }
    }//GEN-LAST:event_countyNameGlobalActionPerformed

    private void weatherStComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weatherStComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_weatherStComboBoxActionPerformed

    private void soilInfoCountyComboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soilInfoCountyComboboxActionPerformed
        // TODO add your handling code here:
        ArrayList <String> soilList = null;
        String countyName = (String) soilInfoCountyCombobox.getSelectedItem();
        //soilList = soil_db.getCountyBasedSoilList_SoilDB(countyName);
        CSVFileHandler csvfilehandler = new CSVFileHandler();
        soilList = csvfilehandler.getCountyBasedSoilList_SoilDB(countyName);

        int itemCount = soilSeriesCombobox.getItemCount();
        for(int i=0;i<itemCount;i++){
            soilSeriesCombobox.removeItemAt(0);
        }

        for (int i = 0; i < soilList.size(); i++) {
            //System.out.print(soilList.get(i));
            soilSeriesCombobox.addItem(soilList.get(i));
        }
    }//GEN-LAST:event_soilInfoCountyComboboxActionPerformed

    private void jNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNextButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jNextButtonActionPerformed

    private void jNextButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jNextButtonMouseClicked
        // TODO add your handling code here:
        // TODO add your handling code here:
        // This will update the cache and then it will move to the next frame
        // Calls the singleton object and then update the hash map of the class
        UpdateFile ();
        if (irrigationframe == null){
            irrigationframe = new IrrigationFertilizer (this);
            
        }
        this.setVisible(false);
        jMenuBar1.setVisible(true);
        irrigationframe.setVisible(true);

    }//GEN-LAST:event_jNextButtonMouseClicked

    private void jComboBoxCropListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCropListActionPerformed
        
        
    }//GEN-LAST:event_jComboBoxCropListActionPerformed

    private void jComboBoxCropListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCropListItemStateChanged
        // TODO add your handling code here:
        // TODO add your handling code here:
        
        if (evt.getStateChange()==ItemEvent.SELECTED) {
            initCultivar ();
           /* String cropName = (String)jComboBoxCropList.getSelectedItem();
            HashMap <String, String> cultivarHashMap = null; 

            StringBuilder cultivadata = new StringBuilder ();

            cultivadata.append ("Crop Name,");        
            cultivadata.append(cropName);


            cultivadata.append (",CultivarFile,");  
            String culfileName = cropname_culfilename.get(cropName);        
            cultivadata.append(culfileName); 

            System.out.println ("Crop Name" + cropName);

            if (cultivafile == null)
                cultivafile = CultivaFileSystem.getInstance();

            cultivafile.UpdateCache(cultivadata.toString());         
            cultivarHashMap = cultivafile.ReadFromFile("VAR-NAME");

            ArrayList<String> cultivalist = new ArrayList<String> ();
            Set keys = cultivarHashMap.keySet();
            Iterator itr = keys.iterator();

            String key;
            String value;
            while(itr.hasNext())
            {
                key = (String)itr.next();
                String val = cultivarHashMap.get(key);
                cultivalist.add(val);
            }
            Collections.sort(cultivalist); 

            int itemCount = jComboBoxCultivar.getItemCount();
            for(int i=0;i<itemCount;i++){
                jComboBoxCultivar.removeItemAt(0);
            }

            for (int i = 0; i < cultivalist.size(); i++) {
                key = cultivalist.get(i);
                key = key.trim();
                jComboBoxCultivar.addItem(key);
            } */
            
        }  
    }//GEN-LAST:event_jComboBoxCropListItemStateChanged

    private void UpdateFile () {
        
        StringBuilder   weatherdata = new StringBuilder ("");
        if (wthfile == null)
            wthfile = WeatherFileSystem.getInstance();
        
        weatherdata.append ("Organization,");
        weatherdata.append(jTextFieldOrgName.getText() + ",");
        
        weatherdata.append ("SiteIndex,");
        weatherdata.append (jTextFieldSiteIndex.getText() + ",");
        
        Date date = jDateChooser1.getDate();        
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        
        Integer day =  new Integer(calendar.get(Calendar.DATE));
        String daystr = day.toString();
        
        Integer year = new Integer (calendar.get(Calendar.YEAR));
        String yearstr = year.toString();
        
        Integer month = new Integer (calendar.get(Calendar.MONTH));
        String monthstr = month.toString();
                     
        
        //System.out.println ("Hello This is rohit - " + day + "//" + month + "//"+  year);
        CSVFileHandler filehandler = new CSVFileHandler ();
        weatherdata.append ("PlantingMonth,");
        //weatherdata.append ((String) jPlantingMonthComboBox.getSelectedItem() + ",");
        weatherdata.append (monthstr + ",");
        
        weatherdata.append ("PlantingDay,");
        //weatherdata.append ((String) jPlantingDayComboBox.getSelectedItem() + ",");
        weatherdata.append (daystr + ",");
        
        weatherdata.append ("PlantingYear,");
        //weatherdata.append ((String) jPlantingYearComboBox.getSelectedItem() + ",");
        weatherdata.append (yearstr + ",");
        
        weatherdata.append ("FawnWeatherStation,");
        String watherstation = (String) weatherStComboBox.getSelectedItem();
        weatherdata.append (watherstation + ",");  
        
        weatherdata.append ("StationLocationId,");        
        weatherdata.append(filehandler.getWeatherStationId_GlobalDB (watherstation));
        
        System.out.println("Weather Data -" + weatherdata);
        wthfile.UpdateCache(weatherdata.toString());  
        
        
        
    }
    
    /*private void UpdateFile () {
        
        StringBuilder   weatherdata = new StringBuilder ("");
        if (wthfile == null)
            wthfile = WeatherFileSystem.getInstance();
        
        weatherdata.append ("Organization,");
        weatherdata.append(jTextFieldOrgName.getText() + ",");
        
        weatherdata.append ("SiteIndex,");
        weatherdata.append (jTextFieldSiteIndex.getText() + ",");
        
        Date date = jDateChooser1.getDate();        
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        
        Integer day =  new Integer(calendar.get(Calendar.DATE));
        String daystr = day.toString();
        
        Integer year = new Integer (calendar.get(Calendar.YEAR));
        String yearstr = year.toString();
        
        Integer month = new Integer (calendar.get(Calendar.MONTH));
        String monthstr = month.toString();
                     
        
        //System.out.println ("Hello This is rohit - " + day + "//" + month + "//"+  year);
        
        weatherdata.append ("PlantingMonth,");
        //weatherdata.append ((String) jPlantingMonthComboBox.getSelectedItem() + ",");
        weatherdata.append (monthstr + ",");
        
        weatherdata.append ("PlantingDay,");
        //weatherdata.append ((String) jPlantingDayComboBox.getSelectedItem() + ",");
        weatherdata.append (daystr + ",");
        
        weatherdata.append ("PlantingYear,");
        //weatherdata.append ((String) jPlantingYearComboBox.getSelectedItem() + ",");
        weatherdata.append (yearstr + ",");
        
        weatherdata.append ("FawnWeatherStation,");
        String watherstation = (String) weatherStComboBox.getSelectedItem();
        weatherdata.append (watherstation + ",");  
        
        weatherdata.append ("StationLocationId,");        
        weatherdata.append(global_db.getWeatherStationId_GlobalDB (watherstation));
        
        System.out.println("Weather Data -" + weatherdata);
        wthfile.UpdateCache(weatherdata.toString());  
        
        
        
    }*/
    
    /*static DBConnect getDBConnection (String dbName){
        DBConnect db = null;
        if (dbName.equals("global")) {
            
            if (global_db == null) {
                
                global_db = new DBConnect (ServerDetails.DB_URL_SERVER1, ServerDetails.USER_SERVER1, ServerDetails.PASS_SERVER1, global_dbname);
            }
            
            db = global_db; 
            
        } else if (dbName.equals("weather_historic_daily")){
             if (weather_historic_daily == null) {
                
                weather_historic_daily = new DBConnect (ServerDetails.DB_URL_SERVER1, ServerDetails.USER_SERVER1, ServerDetails.PASS_SERVER1, weather_historic_daily_dbname);
            }
            db = weather_historic_daily;    
        } 
        
       return db; 
    }*/
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DSSATMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DSSATMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DSSATMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DSSATMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DSSATMain().setVisible(true);
            }
        });
    }
    
    private CommonMenuBar jMenuBar1;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox countyNameGlobal;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBoxCropList;
    private javax.swing.JComboBox jComboBoxCultivar;
    private javax.swing.JComboBox jComboBoxPrevCropList;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JButton jNextButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextFieldOrgName;
    private javax.swing.JTextField jTextFieldSiteIndex;
    private javax.swing.JTree jTree1;
    private javax.swing.JComboBox soilInfoCountyCombobox;
    private javax.swing.JComboBox soilSeriesCombobox;
    private javax.swing.JComboBox weatherStComboBox;
    // End of variables declaration//GEN-END:variables
}
