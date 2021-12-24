package dashboardforsensordata;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.String.valueOf;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;

/**
 * DataToTable : CAUTION! WE WANT THAT TO RUN 1 TIME! We 'll what we can do for >1 times, IF we have extra options for the user..
 * FillTemp : Fills the array for temperature. 
 * FillHum  : Fills the array for humidity.
 * ReadData : Will read the data from the file, calls the functions above.
 * ListData : Fills the an ArrayList with StructuredData objects. ArrayLists can do the job for this amount of data.
 * setMAIN  : Clears and Shows the next Form, MUST be called from an event listener! HAS ISSUES.
 * SelectionFromLeftNav: Efficient as it is, not as an event handler with an interface.
 * 
 * @author kgl
 */
public class mainApp extends javax.swing.JFrame {
    File file;
    int ChoiceTracer=0;                             // Side Nav user selection Trace.
    int TemIndex = 0;                               // Has to be here to allow FillTemp, I don't want to keep track of more stuff to the functions.
    int HumIndex = 0;                               // Has to be here to allow FillHum, I don't want to keep track of more stuff to the functions.
    double Humidity [][] = new double[60][10];      //60 Slots aka days, for 10 values.
    double Temperature [][] = new double[60][10];   //60 Slots aka days, for 10 values.
    int SampleTime=-1;                              //10 values consist 1 sample. 1 Day = 2 samples.
    String [] Days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    String [] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public class StructuredData{
        String Date;
        double Humidity;
        double Temperature;

        public StructuredData(String Date, double Humidity, double Temperature) {
            this.Date = Date;
            this.Humidity = Humidity;
            this.Temperature = Temperature;
        }
        
    }
    
    public void DataToTable() throws IOException{
        int DaysCounter=0; //29/12 was Monday
        ReadData();
        
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();   
        ArrayList<StructuredData> list = ListData();

        Object rowData [] = new Object[3];
        
        for(int i=0; i<list.size();i++){
            if(i%20==0){
                DaysCounter++;
            }
            rowData[0] = Days[DaysCounter%7] +" "+ list.get(i).Date;
            rowData[1] = list.get(i).Humidity;
            rowData[2] = list.get(i).Temperature;
            model.addRow(rowData);
        }
    }
    
    public ArrayList ListData(){
        
        ArrayList<StructuredData> list = new ArrayList<StructuredData>();
        int date = 29; 
        int monthcounter = 10;
        int Samples = SampleTime;       
        
        for(int i=0; i<=Samples;i++){
            
            if(date%30==0 && list.size()%20==0){     // 2nd loop
                monthcounter++;
                date=1;
            }
            else if(list.size()%20==0){    // 1st loop
                date++;
            }
            
            for(int j=0; j<10;j++){
                
                list.add( new StructuredData(valueOf(date%31) +" "+ Months[monthcounter%12],Humidity[i][j],Temperature[i][j]));       
            }
            //31 December... Should be generalized, this is not a formal solution.
            
            
        }
        return list;
    }
    
    public void FillTemp(String stLine){
        
        Temperature[SampleTime][TemIndex] = Double.parseDouble(stLine.substring(17));
    }
    
    public void FillHum(String stLine){
        
        Humidity[SampleTime][HumIndex] = Double.parseDouble(stLine.substring(14));
    }
    
    public void ReadData() throws FileNotFoundException,IOException{
        String stLine;
        try{
            file = new File("data.txt");
            
            BufferedReader Br = new BufferedReader( new FileReader(file));
            
            while(( stLine = Br.readLine()) != null){
                
                if(stLine.matches("../..")){
                    
                    SampleTime++;   // Index για το δείγμα.
                    TemIndex = 0;
                    HumIndex = 0;
                    
                }else{
                    
                    if(stLine.startsWith("Te")){
                        
                        FillTemp(stLine);
                        TemIndex++;
                        
                    }else if(stLine.startsWith("Hu")){
                        
                        FillHum(stLine);
                        HumIndex++;
                    }
                }
                
            }
            
        }
        catch(FileNotFoundException e){
            System.out.println("ΛΑΘΟΣ PATH!");
            
        }
    }
    
    
    public mainApp() throws IOException {
        
        initComponents();
        
        //Some work for the DataForm. It should be removed. Also, scroll and border need to be better..
        DataToTable();
        
        
        jTable1.setBackground(new Color(0,0,0,0));
        jScrollPane1.setBackground(new Color(0,0,0,0));
        jTable1.setOpaque(false);
        jScrollPane1.setOpaque(false);
        jTable1.setGridColor(Color.BLUE);
        jTable1.setForeground(Color.BLACK);
        
        jScrollPane1.getViewport().setOpaque(false);
        
    }
    
    
    public void setMAIN(JComponent comp){
        System.out.println("HI");
        GradientMAIN.removeAll(); 
        GradientMAIN.add(comp);
        System.out.println("HI");
        
    }
    
    public void SelectionFromLeftNav(int key){
        
        if(key!=ChoiceTracer){
            switch(key){
                case 0 -> {
                    setMAIN(new DashboardForm());
                    DashboardButton.setBackground(new java.awt.Color(11, 11, 33));
                    DashboardLabel.setForeground(new java.awt.Color(255, 255, 0));
                    DataLabel.setForeground(new java.awt.Color(255, 255, 255));
                    StatisticsLabel.setForeground(new java.awt.Color(255, 255, 255));
                    SettingsLabel.setForeground(new java.awt.Color(255, 255, 255));
                    ChoiceTracer=key;
                }
                case 1 -> {
                    setMAIN(new DataForm());
                    DataButton.setBackground(new java.awt.Color(11, 11, 33));
                    DataLabel.setForeground(new java.awt.Color(255, 255, 0));
                    DashboardLabel.setForeground(new java.awt.Color(255, 255, 255));
                    StatisticsLabel.setForeground(new java.awt.Color(255, 255, 255));
                    SettingsLabel.setForeground(new java.awt.Color(255, 255, 255));
                    ChoiceTracer=key;
                }
                case 2 -> {
                    setMAIN(new StatisticsForm());
                    StatisticsButton.setBackground(new java.awt.Color(11, 11, 33));
                    StatisticsLabel.setForeground(new java.awt.Color(255, 255, 0));
                    DashboardLabel.setForeground(new java.awt.Color(255, 255, 255));
                    DataLabel.setForeground(new java.awt.Color(255, 255, 255));
                    SettingsLabel.setForeground(new java.awt.Color(255, 255, 255));
                    ChoiceTracer=key;
                }
                case 3 ->{
                    setMAIN(new SettingsForm());
                    SettingsButton.setBackground(new java.awt.Color(11, 11, 33));
                    SettingsLabel.setForeground(new java.awt.Color(255, 255, 0));
                    DashboardLabel.setForeground(new java.awt.Color(255, 255, 255));
                    DataLabel.setForeground(new java.awt.Color(255, 255, 255));
                    StatisticsLabel.setForeground(new java.awt.Color(255, 255, 255));
                    ChoiceTracer=key;
                }
                default -> System.out.println("Problima");   
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LeftNav = new javax.swing.JPanel();
        AppName = new javax.swing.JLabel();
        Welcome_userLabe = new javax.swing.JLabel();
        DashboardButton = new rojerusan.RSButtonPane();
        DashboardLabel = new javax.swing.JLabel();
        DataButton = new rojerusan.RSButtonPane();
        DataLabel = new javax.swing.JLabel();
        StatisticsButton = new rojerusan.RSButtonPane();
        StatisticsLabel = new javax.swing.JLabel();
        SettingsButton = new rojerusan.RSButtonPane();
        SettingsLabel = new javax.swing.JLabel();
        ExitButton = new rojerusan.RSButtonPane();
        ExitLabel = new javax.swing.JLabel();
        PresentPanel = new javax.swing.JPanel();
        GradientMAIN = new keeptoo.KGradientPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        LeftNav.setBackground(new java.awt.Color(23, 23, 69));
        LeftNav.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        AppName.setFont(new java.awt.Font("Arial", 2, 16)); // NOI18N
        AppName.setForeground(new java.awt.Color(254, 254, 254));
        AppName.setText("NodeMCU-DHT11 Dashboard");
        LeftNav.add(AppName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 50));

        Welcome_userLabe.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        Welcome_userLabe.setForeground(new java.awt.Color(254, 254, 254));
        Welcome_userLabe.setText("Welcome \"Username\"");
        LeftNav.add(Welcome_userLabe, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 230, 30));

        DashboardButton.setBackground(new java.awt.Color(23, 23, 69));
        DashboardButton.setForeground(new java.awt.Color(254, 254, 254));
        DashboardButton.setColorHover(new java.awt.Color(48, 48, 145));
        DashboardButton.setColorNormal(new java.awt.Color(23, 23, 69));
        DashboardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                DashboardButtonMousePressed(evt);
            }
        });

        DashboardLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        DashboardLabel.setForeground(new java.awt.Color(255, 255, 0));
        DashboardLabel.setText("Dashboard");

        javax.swing.GroupLayout DashboardButtonLayout = new javax.swing.GroupLayout(DashboardButton);
        DashboardButton.setLayout(DashboardButtonLayout);
        DashboardButtonLayout.setHorizontalGroup(
            DashboardButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DashboardButtonLayout.createSequentialGroup()
                .addContainerGap(53, Short.MAX_VALUE)
                .addComponent(DashboardLabel)
                .addGap(52, 52, 52))
        );
        DashboardButtonLayout.setVerticalGroup(
            DashboardButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DashboardButtonLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(DashboardLabel)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        LeftNav.add(DashboardButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 230, 80));

        DataButton.setBackground(new java.awt.Color(23, 23, 69));
        DataButton.setColorHover(new java.awt.Color(48, 48, 145));
        DataButton.setColorNormal(new java.awt.Color(23, 23, 69));
        DataButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                DataButtonMousePressed(evt);
            }
        });

        DataLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        DataLabel.setForeground(new java.awt.Color(254, 254, 254));
        DataLabel.setText("Data");

        javax.swing.GroupLayout DataButtonLayout = new javax.swing.GroupLayout(DataButton);
        DataButton.setLayout(DataButtonLayout);
        DataButtonLayout.setHorizontalGroup(
            DataButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DataButtonLayout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(DataLabel)
                .addContainerGap(125, Short.MAX_VALUE))
        );
        DataButtonLayout.setVerticalGroup(
            DataButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DataButtonLayout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(DataLabel)
                .addGap(22, 22, 22))
        );

        LeftNav.add(DataButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 230, 80));

        StatisticsButton.setBackground(new java.awt.Color(23, 23, 69));
        StatisticsButton.setColorHover(new java.awt.Color(48, 48, 145));
        StatisticsButton.setColorNormal(new java.awt.Color(23, 23, 69));
        StatisticsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                StatisticsButtonMousePressed(evt);
            }
        });

        StatisticsLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        StatisticsLabel.setForeground(new java.awt.Color(254, 254, 254));
        StatisticsLabel.setText("Statistics");

        javax.swing.GroupLayout StatisticsButtonLayout = new javax.swing.GroupLayout(StatisticsButton);
        StatisticsButton.setLayout(StatisticsButtonLayout);
        StatisticsButtonLayout.setHorizontalGroup(
            StatisticsButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, StatisticsButtonLayout.createSequentialGroup()
                .addContainerGap(52, Short.MAX_VALUE)
                .addComponent(StatisticsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
        );
        StatisticsButtonLayout.setVerticalGroup(
            StatisticsButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatisticsButtonLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(StatisticsLabel)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        LeftNav.add(StatisticsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 230, 80));

        SettingsButton.setBackground(new java.awt.Color(23, 23, 69));
        SettingsButton.setColorHover(new java.awt.Color(48, 48, 145));
        SettingsButton.setColorNormal(new java.awt.Color(23, 23, 69));
        SettingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                SettingsButtonMousePressed(evt);
            }
        });

        SettingsLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        SettingsLabel.setForeground(new java.awt.Color(254, 254, 254));
        SettingsLabel.setText("Settings");

        javax.swing.GroupLayout SettingsButtonLayout = new javax.swing.GroupLayout(SettingsButton);
        SettingsButton.setLayout(SettingsButtonLayout);
        SettingsButtonLayout.setHorizontalGroup(
            SettingsButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SettingsButtonLayout.createSequentialGroup()
                .addContainerGap(52, Short.MAX_VALUE)
                .addComponent(SettingsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
        );
        SettingsButtonLayout.setVerticalGroup(
            SettingsButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsButtonLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(SettingsLabel)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        LeftNav.add(SettingsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 230, 80));

        ExitButton.setBackground(new java.awt.Color(23, 23, 69));
        ExitButton.setColorHover(new java.awt.Color(48, 48, 145));
        ExitButton.setColorNormal(new java.awt.Color(23, 23, 69));
        ExitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ExitButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ExitButtonMouseReleased(evt);
            }
        });

        ExitLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        ExitLabel.setForeground(new java.awt.Color(254, 254, 254));
        ExitLabel.setText("Exit");

        javax.swing.GroupLayout ExitButtonLayout = new javax.swing.GroupLayout(ExitButton);
        ExitButton.setLayout(ExitButtonLayout);
        ExitButtonLayout.setHorizontalGroup(
            ExitButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ExitButtonLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ExitLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
        );
        ExitButtonLayout.setVerticalGroup(
            ExitButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ExitButtonLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(ExitLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        LeftNav.add(ExitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 230, 80));

        getContentPane().add(LeftNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 900));

        PresentPanel.setBackground(new java.awt.Color(254, 254, 254));
        PresentPanel.setLayout(new java.awt.BorderLayout());

        GradientMAIN.setkEndColor(new java.awt.Color(69, 69, 209));
        GradientMAIN.setkGradientFocus(2000);
        GradientMAIN.setkStartColor(new java.awt.Color(23, 23, 69));
        GradientMAIN.setkTransparentControls(false);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setForeground(new Color(0,0,0,255));
        System.out.println(jScrollPane1.isOpaque());

        jTable1.setBackground(javax.swing.UIManager.getDefaults().getColor("window"));
        jTable1.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Humidity", "Temperature"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setForeground(new Color(0,0,0,255));
        jTable1.setRowHeight(20);
        jTable1.setShowGrid(false);
        System.out.println("jTable"+jTable1.isOpaque());
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout GradientMAINLayout = new javax.swing.GroupLayout(GradientMAIN);
        GradientMAIN.setLayout(GradientMAINLayout);
        GradientMAINLayout.setHorizontalGroup(
            GradientMAINLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GradientMAINLayout.createSequentialGroup()
                .addGap(144, 144, 144)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 889, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(337, Short.MAX_VALUE))
        );
        GradientMAINLayout.setVerticalGroup(
            GradientMAINLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GradientMAINLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(247, Short.MAX_VALUE))
        );

        PresentPanel.add(GradientMAIN, java.awt.BorderLayout.CENTER);

        getContentPane().add(PresentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 0, 1370, 900));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ExitButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ExitButtonMouseReleased
        // TODO add your handling code here:
        ExitButton.setBackground(new java.awt.Color(23, 23, 69));
        System.exit(0);
    }//GEN-LAST:event_ExitButtonMouseReleased

    private void ExitButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ExitButtonMousePressed
        // TODO add your handling code here:
        ExitButton.setBackground(new java.awt.Color(11, 11, 33));
    }//GEN-LAST:event_ExitButtonMousePressed

    private void SettingsButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SettingsButtonMousePressed
        // TODO add your handling code here:
        SelectionFromLeftNav(3);
    }//GEN-LAST:event_SettingsButtonMousePressed

    private void StatisticsButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StatisticsButtonMousePressed
        // TODO add your handling code here:
        SelectionFromLeftNav(2);
    }//GEN-LAST:event_StatisticsButtonMousePressed

    private void DataButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DataButtonMousePressed
        // TODO add your handling code here:
        SelectionFromLeftNav(1);
    }//GEN-LAST:event_DataButtonMousePressed

    private void DashboardButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DashboardButtonMousePressed
        // TODO add your handling code here:
        SelectionFromLeftNav(0);
    }//GEN-LAST:event_DashboardButtonMousePressed
    
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
            java.util.logging.Logger.getLogger(mainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new mainApp().setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(mainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AppName;
    private rojerusan.RSButtonPane DashboardButton;
    private javax.swing.JLabel DashboardLabel;
    private rojerusan.RSButtonPane DataButton;
    private javax.swing.JLabel DataLabel;
    private rojerusan.RSButtonPane ExitButton;
    private javax.swing.JLabel ExitLabel;
    private keeptoo.KGradientPanel GradientMAIN;
    private javax.swing.JPanel LeftNav;
    private javax.swing.JPanel PresentPanel;
    private rojerusan.RSButtonPane SettingsButton;
    private javax.swing.JLabel SettingsLabel;
    private rojerusan.RSButtonPane StatisticsButton;
    private javax.swing.JLabel StatisticsLabel;
    private javax.swing.JLabel Welcome_userLabe;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
