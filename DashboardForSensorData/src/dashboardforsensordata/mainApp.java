package dashboardforsensordata;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.String.valueOf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * DataToTable          : CAUTION! WE WANT THAT TO RUN 1 TIME! We 'll what we can do for >1 times, IF we have extra options for the user..
 * FillTemp             : Fills the array for temperature. 
 * FillHum              : Fills the array for humidity.
 * ReadData             : Will read the data from the file, calls the functions above.
 * ListData             : Fills the an ArrayList with StructuredData objects. ArrayLists can do the job for this amount of data.
 * ListData(int k)      : Will run only for the linegraphs. k is the value of the respective slider.
 * SelectionFromLeftNav : Efficient as it is, not as an event handler with an interface.
 * showHumPieChart      : Will display (,20] , (20,45] , (45,65] , (65,90] , (90,) as Very Low, Low, OK, High, Very High respectively.
 * showTemPieChart      : Will display (,4] , (4,8] , (8,12] , (12,16] , (16,) as Very Low, Low, OK, High, Very High respectively.
 * showBarChart         : Will display day average for Temperature&Humidity sample values.
 * showLineTempChart    : Will display a linechart for the temperature values, also it has slider.
 * showLineHumChart     : Will display a linechart for the humidity values, also it has slider.
 * 
 * @author kgl
 */

public class mainApp extends javax.swing.JFrame {
    
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    Color Default = new Color(23, 23, 69);          // Commonly used.
    CardLayout card;                                // Card Layout.
    ArrayList<StructuredData> list;                 // DD for Table.
    File file;                                      // Data file.
    int ChoiceTracer=-1;                            // Side Nav user selection Trace.
    int TemIndex = 0;                               // Has to be here to allow FillTemp, I don't want to keep track of more stuff to the functions.
    int HumIndex = 0;                               // Has to be here to allow FillHum, I don't want to keep track of more stuff to the functions.
    double Humidity [][] = new double[60][10];      // 60 Slots aka days, for 10 values.
    double Temperature [][] = new double[60][10];   // 60 Slots aka days, for 10 values.
    int SampleTime=-1;                              // 10 values consist 1 sample. 1 Day = 2 samples.
    boolean guard = false;                          // DataToTable won't run twice.
    boolean done = false;                           // CalcStats won't run twice.
    String [] Days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    String [] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    int [] MonthEndsIn = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};    
    
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
    
    public void ReadData() throws FileNotFoundException,IOException{
        String stLine;
        
        try{
            file = new File("data.txt");
            
            BufferedReader Br = new BufferedReader( new FileReader(file));
            
            while(( stLine = Br.readLine()) != null){
                
                if(stLine.matches("../..")){
                    
                    SampleTime++;   // Index for the sample.
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
            System.out.println("Wrong Path!");
            
        }
    }  
    
    public void FillTemp(String stLine){
        
        Temperature[SampleTime][TemIndex] = Double.parseDouble(stLine.substring(17));
    }
    
    public void FillHum(String stLine){
        
        Humidity[SampleTime][HumIndex] = Double.parseDouble(stLine.substring(14));
    }
     
    public ArrayList ListData(){
        
        list = new ArrayList<StructuredData>();
        int date = 29; 
        int monthcounter = 10;
        int Samples = SampleTime;       
        
        for(int i=0; i<=Samples;i++){
            
            if(date%MonthEndsIn[monthcounter%12]==0 && list.size()%20==0){
                monthcounter++;
                date=1;
            }
            else if(list.size()%20==0){
                date++;
            }
            
            for(int j=0; j<10;j++){
                
                list.add( new StructuredData(valueOf(date%(MonthEndsIn[monthcounter%12]+1)) +" "+ Months[monthcounter%12],Humidity[i][j],Temperature[i][j]));       
            }
            // 31 December... Should be generalized, this is not a formal solution.
            // Works for years 2021-2022-2023. Απόρροια αυτής της υλοποίησης είναι ο τρόπος ανάκτησης των δεδομένων και οι κακές πρακτικές στην αποθήκευσή τους.
            // 
            
        }
        
        return list;
    }

    public ArrayList ListData(int Selection){
        
        list = new ArrayList<StructuredData>();
        int date = 29; 
        int monthcounter = 10;
        int Samples = Selection;       
        
        for(int i=0; i<=Samples;i++){
            
            if(date%MonthEndsIn[monthcounter%12]==0 && list.size()%20==0){
                monthcounter++;
                date=1;
            }
            else if(list.size()%20==0){
                date++;
            }
            
            for(int j=0; j<10;j++){
                
                list.add( new StructuredData(valueOf(date%(MonthEndsIn[monthcounter%12]+1)) +" "+ Months[monthcounter%12],Humidity[i][j],Temperature[i][j]));       
            }
            // 31 December... Should be generalized, this is not a formal solution.
            // Works for years 2021-2022-2023. Απόρροια αυτής της υλοποίησης είναι ο τρόπος ανάκτησης των δεδομένων και οι κακές πρακτικές στην αποθήκευσή τους.
            // 
            
        }
        
        return list;
    }
    
    public void DataToTable() throws IOException{
        int DaysCounter=0; // 29/12 was Monday
        
        DefaultTableModel model = (DefaultTableModel)DataTable.getModel();   
        list = ListData();

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
    public void CalcStats(){
        // Mean, Median, Prevailing, min and max values.
        double [] TempValues = new double[list.size()];
        double [] HumiValues = new double[list.size()];
        // average values
        int sumTemp = 0;
        int sumHumi = 0;
        
        for(int i=0; i<list.size();i++){
            sumTemp += list.get(i).Temperature;
            sumHumi += list.get(i).Humidity;
            TempValues[i] = list.get(i).Temperature;
            HumiValues[i] = list.get(i).Humidity;
        }
        Arrays.sort(TempValues);
        Arrays.sort(HumiValues);
        
        // prevailing value
        double Temptrace = TempValues[0];
        double prevailTemp = TempValues[0];
        int counterTemp = 0;
        int maxTemp = 0;
        
        double Humitrace = HumiValues[0];
        double prevailHumi = HumiValues[0];
        int counterHumi = 0;
        int maxHumi = 0;
        
        for(int i=1; i<list.size();i++){
            
            if(TempValues[i]==Temptrace){
                counterTemp++;
            }else{
                
                if(counterTemp>maxTemp){
                    prevailTemp = TempValues[i-1];
                    maxTemp = counterTemp;
                }
                Temptrace = TempValues[i];
                counterTemp = 1;
            }
            
            if(HumiValues[i]==Humitrace){
                counterHumi++;
            }else{
                
                if(counterHumi>maxHumi){
                    prevailHumi = HumiValues[i-1];
                    maxHumi = counterHumi;
                }
                Humitrace = HumiValues[i];
                counterHumi = 1;
            }
            
        }
        System.out.println(prevailTemp +" "+ maxTemp);
        System.out.println(prevailHumi +" "+ maxHumi);
        sumTemp /= list.size();
        sumHumi /= list.size();
        System.out.println(sumTemp+" "+sumHumi);
        System.out.println(TempValues[list.size()/2]+" "+HumiValues[list.size()/2]+" "+TempValues[0]+" "+TempValues[TempValues.length-1]+" "+HumiValues[0]+ " " + HumiValues[HumiValues.length-1]);
    }
    
    public mainApp() throws IOException {
        
        ReadData();
        ListData();
        System.out.println(list.toString());
        initComponents();
        //Some work for the DataForm. It should be removed. Also, scroll and border need to be better..
        
        DataTable.setBackground(new Color(0,0,0,0));
        jScrollPane1.setBackground(new Color(0,0,0,0));
        DataTable.setOpaque(false);
        jScrollPane1.setOpaque(false);
        DataTable.setGridColor(Color.BLUE);
        DataTable.setForeground(Color.BLACK);
        jScrollPane1.getViewport().setOpaque(false);
        
        TemSlider.setMaximum(SampleTime);
        HumSlider.setMaximum(SampleTime);
        
        card = (CardLayout)(cardLayouts.getLayout());
        
    }
    
    public void showTemPieChart(){
        
       DefaultPieDataset barDataset = new DefaultPieDataset( );
       int Counter[] = {0,0,0,0,0};
       
       for(int i=0; i<list.size();i++){
           if(list.get(i).Temperature>16){
               Counter[0]++;  
           }else if(list.get(i).Temperature>12){
               Counter[1]++;
           }else if(list.get(i).Temperature>8){
               Counter[2]++;
           }else if(list.get(i).Temperature>4){
               Counter[3]++;
           }else{
               Counter[4]++;
           }
       }
       barDataset.setValue("Very High", Counter[0]);
       barDataset.setValue("High", Counter[1]);
       barDataset.setValue("OK", Counter[2]);
       barDataset.setValue("Low", Counter[3]);
       barDataset.setValue("Very Low", Counter[4]);
       JFreeChart piechart = ChartFactory.createPieChart("Temp Overview", barDataset, false, true, false);

       piechart.setBackgroundPaint(new Color(0, 0, 0, 0));

       PiePlot piePlot =(PiePlot) piechart.getPlot();

       piePlot.setSectionPaint("Very High", new Color(255,0,0));
       piePlot.setSectionPaint("High", new Color(255,153,51));
       piePlot.setSectionPaint("OK", new Color(0,204,0));
       piePlot.setSectionPaint("Low", new Color(0,204,204));
       piePlot.setSectionPaint("Very Low", new Color(0,0,204));

       piePlot.setBackgroundPaint(new Color(0,0,80,10));
       piePlot.setOutlinePaint(new Color(20,20,20,20));

       ChartPanel pieChartPanel = new ChartPanel(piechart);
       pieChartPanel.setOpaque(false);
       pieChartPanel.setBackground(new Color(0,0,80,20));
       TemPieChart1.removeAll();
       TemPieChart1.add(pieChartPanel, BorderLayout.CENTER);
       TemPieChart1.validate();
       
    }
    
    public void showHumPieChart(){
       DefaultPieDataset barDataset = new DefaultPieDataset( );
       int Counter[] = {0,0,0,0,0};
       
       for(int i=0; i<list.size();i++){
           if(list.get(i).Humidity>80){
               Counter[0]++;  
           }else if(list.get(i).Humidity>65){
               Counter[1]++;
           }else if(list.get(i).Humidity>45){
               Counter[2]++;
           }else if(list.get(i).Humidity>20){
               Counter[3]++;
           }else{
               Counter[4]++;
           }
       }
       barDataset.setValue("Very High", Counter[0]);
       barDataset.setValue("High", Counter[1]);
       barDataset.setValue("OK", Counter[2]);
       barDataset.setValue("Low", Counter[3]);
       barDataset.setValue("Very Low", Counter[4]);
       JFreeChart piechart = ChartFactory.createPieChart("Hum Overview", barDataset, false, true, false);

       piechart.setBackgroundPaint(new Color(0, 0, 0, 0));

       PiePlot piePlot =(PiePlot) piechart.getPlot();

       piePlot.setSectionPaint("Very High", new Color(255,0,0));
       piePlot.setSectionPaint("High", new Color(255,153,51));
       piePlot.setSectionPaint("OK", new Color(0,204,0));
       piePlot.setSectionPaint("Low", new Color(0,204,204));
       piePlot.setSectionPaint("Very Low", new Color(0,0,204));

       piePlot.setBackgroundPaint(new Color(0,0,80,20));
       piePlot.setOutlinePaint(new Color(20,20,20,20));

       ChartPanel pieChartPanel = new ChartPanel(piechart);
       pieChartPanel.setOpaque(false);
       pieChartPanel.setBackground(new Color(0,0,80,20));
       HumPieChart.removeAll();
       HumPieChart.add(pieChartPanel, BorderLayout.CENTER);
       HumPieChart.validate();
       
    }
    // PROBLIMATA, MALLON ESWTERIKA!!! Einai SIGOURA ESWTERIKA
    public void showBarChart(){
        DefaultCategoryDataset UniData = new DefaultCategoryDataset();
        int SumTemp=0,SumHumi=0;
        
        for(int i=0; i<list.size(); i++){
            SumTemp+=list.get(i).Temperature;
            SumHumi+=list.get(i).Humidity;
            if(i%20==0){
                UniData.setValue(SumTemp/20, "Temperature", i+"");
                UniData.setValue(SumHumi/20, "Humidity", i+"");
                SumTemp=0;
                SumHumi=0;
            }
        }
        
        JFreeChart chart = ChartFactory.createBarChart("Bar Chart","Day","Value", 
                                                        UniData,
                                                        PlotOrientation.VERTICAL, false,true,false);
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);
        
        
        categoryPlot.setBackgroundPaint(new Color(0,0,0,0));
        
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        Color clr1 = new Color(51, 0, 255);
        Color clr2 = new Color(255, 0, 51);
        renderer.setSeriesPaint(0, clr2);
        renderer.setSeriesPaint(1, clr1);

        chart.setBackgroundPaint(null);
        ChartPanel barChartPanel = new ChartPanel(chart);
        barChartPanel.setBackground(new Color(0,0,0,0));
        TemHumBarChart.removeAll();
        TemHumBarChart.add(barChartPanel, BorderLayout.CENTER);
        TemHumBarChart.validate();
        
        
    }
    
    public void showLineTempChart(){
        DefaultCategoryDataset Tempdataset = new DefaultCategoryDataset();
        
        
        for(int i=0; i<list.size(); i++){
            Tempdataset.setValue(list.get(i).Temperature, "Temperature", i+"");
        }
        JFreeChart linechart = ChartFactory.createLineChart("Temperature","SampleTimings","Readings", Tempdataset, PlotOrientation.VERTICAL, false,true,false);
        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();
        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255,255,0);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        ChartPanel lineChartPanel = new ChartPanel(linechart);
        
        lineChartPanel.setOpaque(false);
        lineCategoryPlot.setBackgroundPaint(new Color(0,0,0,0));
        linechart.setBackgroundPaint(new Color(30,50,88,50));
        lineChartPanel.setBackground(new Color(0,0,0,0));
        
        TempLineChart.removeAll();
        TempLineChart.add(lineChartPanel, BorderLayout.CENTER);
        TempLineChart.validate();

    }
    
    public void showLineTempChart(int k){
        DefaultCategoryDataset Tempdataset = new DefaultCategoryDataset();
        System.out.println(list.size());

        TempLineChart.removeAll();
        for(int i=0; i<list.size(); i+=k){
            Tempdataset.setValue(list.get(i).Temperature, "Temperature", i+"");
        }
        JFreeChart linechart = ChartFactory.createLineChart("Temperature","SampleTimings","Readings", Tempdataset, PlotOrientation.VERTICAL, false,true,false);
        
        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();
        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255,255,0);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        ChartPanel lineChartPanel = new ChartPanel(linechart);
        
        lineChartPanel.setOpaque(false);
        lineCategoryPlot.setBackgroundPaint(new Color(0,0,0,0));
        linechart.setBackgroundPaint(new Color(0,0,80,25));
        lineChartPanel.setBackground(new Color(0,0,0,0));
        
        TempLineChart.removeAll();
        TempLineChart.add(lineChartPanel, BorderLayout.CENTER);
        TempLineChart.validate();

    }

    public void showLineHumChart(){
        DefaultCategoryDataset Humdataset = new DefaultCategoryDataset();
        
        for(int i=0; i<list.size(); i++){
            Humdataset.setValue(list.get(i).Humidity, "Humidity", i+"");
        }
        JFreeChart linechart = ChartFactory.createLineChart("Humidity","SampleTimings","Readings", Humdataset, PlotOrientation.VERTICAL, false,true,false);
        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();
        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255,255,0);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        ChartPanel lineChartPanel = new ChartPanel(linechart);
        
        lineChartPanel.setOpaque(false);
        lineCategoryPlot.setBackgroundPaint(new Color(0,0,0,0));
        linechart.setBackgroundPaint(new Color(0,0,80,25));
        lineChartPanel.setBackground(new Color(0,0,0,0));
        
        HumLineChart.removeAll();
        HumLineChart.add(lineChartPanel, BorderLayout.CENTER);
        HumLineChart.validate();
    }
    
    public void showLineHumChart(int k){
        DefaultCategoryDataset Humdataset = new DefaultCategoryDataset();
        System.out.println(list.size());
        
        HumLineChart.removeAll();
        for(int i=0; i<list.size(); i+=k){
            Humdataset.setValue(list.get(i).Humidity, "Humidity", i+"");
        }
        JFreeChart linechart = ChartFactory.createLineChart("Humidity","SampleTimings","Readings", Humdataset, PlotOrientation.VERTICAL, false,true,false);
        
        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();
        
        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255,255,0);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        ChartPanel lineChartPanel = new ChartPanel(linechart);
        
        lineChartPanel.setOpaque(false);
        lineCategoryPlot.setBackgroundPaint(new Color(0,0,0,0));
        
        linechart.setBackgroundPaint(new Color(0,0,80,25));
        lineChartPanel.setBackground(new Color(0,0,0,0));
        
        HumLineChart.removeAll();
        HumLineChart.add(lineChartPanel, BorderLayout.CENTER);
        HumLineChart.validate();

    }
     
    public void SelectionFromLeftNav(int key) throws IOException{
        if(key!=ChoiceTracer){
            switch(key){
                case 0 -> {
                    
                    DashboardButton.setColorNormal(new Color(11,11,33));
                    DataButton.setColorNormal(Default);
                    StatisticsButton.setColorNormal(Default);
                    SettingsButton.setColorNormal(Default);
                    
                    DashboardLabel.setForeground(Color.YELLOW);
                    DataLabel.setForeground(Color.WHITE);
                    StatisticsLabel.setForeground(Color.WHITE);
                    SettingsLabel.setForeground(Color.WHITE);
                    ChoiceTracer=key;
                    
                    showLineTempChart();
                    showLineHumChart();
                    showBarChart();
                    showTemPieChart();
                    showHumPieChart();
                }
                case 1 -> {
                    
                    DashboardButton.setColorNormal(Default);
                    DataButton.setColorNormal(new Color(11,11,33));
                    StatisticsButton.setColorNormal(Default);
                    SettingsButton.setColorNormal(Default);
                    
                    DataLabel.setForeground(Color.YELLOW);
                    DashboardLabel.setForeground(Color.WHITE);
                    StatisticsLabel.setForeground(Color.WHITE);
                    SettingsLabel.setForeground(Color.WHITE);
                    ChoiceTracer=key;
                    if(!guard) DataToTable();
                    guard=true;
                    
                }
                case 2 -> {      
                    DashboardButton.setColorNormal(Default);
                    DataButton.setColorNormal(Default);
                    StatisticsButton.setColorNormal(new Color(11,11,33));
                    SettingsButton.setColorNormal(Default);
                    
                    DashboardLabel.setForeground(Color.WHITE);
                    DataLabel.setForeground(Color.WHITE);
                    StatisticsLabel.setForeground(Color.YELLOW);
                    SettingsLabel.setForeground(Color.WHITE);
                    ChoiceTracer=key;
                    if(!done) CalcStats();
                    done = true;
                    
                }
                case 3 ->{
                    DashboardButton.setColorNormal(Default);
                    DataButton.setColorNormal(Default);
                    StatisticsButton.setColorNormal(Default);
                    SettingsButton.setColorNormal(new Color(11,11,33));
                    
                    DashboardLabel.setForeground(Color.WHITE);
                    DataLabel.setForeground(Color.WHITE);
                    StatisticsLabel.setForeground(Color.WHITE);
                    SettingsLabel.setForeground(Color.YELLOW);
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
        cardLayouts = new javax.swing.JPanel();
        GradientMAIN = new keeptoo.KGradientPanel();
        DashboardCard = new keeptoo.KGradientPanel();
        HumSlider = new javax.swing.JSlider();
        TemSlider = new javax.swing.JSlider();
        TempLineChart = new javax.swing.JPanel();
        HumLineChart = new javax.swing.JPanel();
        TemHumBarChart = new javax.swing.JPanel();
        TemPieChart1 = new javax.swing.JPanel();
        HumPieChart = new javax.swing.JPanel();
        DataCard = new keeptoo.KGradientPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        DataTable = new javax.swing.JTable();
        StatisticCard = new keeptoo.KGradientPanel();
        SettingsCard = new keeptoo.KGradientPanel();

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

        DashboardButton.setForeground(new java.awt.Color(254, 254, 254));
        DashboardButton.setColorHover(new java.awt.Color(48, 48, 145));
        DashboardButton.setColorNormal(new java.awt.Color(23, 23, 69));
        DashboardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                DashboardButtonMousePressed(evt);
            }
        });

        DashboardLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        DashboardLabel.setForeground(new java.awt.Color(255, 255, 255));
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
        PresentPanel.setOpaque(false);
        PresentPanel.setLayout(new java.awt.BorderLayout());

        cardLayouts.setLayout(new java.awt.CardLayout());

        GradientMAIN.setkEndColor(new java.awt.Color(69, 69, 209));
        GradientMAIN.setkGradientFocus(2000);
        GradientMAIN.setkStartColor(new java.awt.Color(23, 23, 69));
        GradientMAIN.setkTransparentControls(false);
        GradientMAIN.setMinimumSize(new java.awt.Dimension(1600, 900));
        GradientMAIN.setPreferredSize(new java.awt.Dimension(1600, 900));
        GradientMAIN.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        cardLayouts.add(GradientMAIN, "card6");

        DashboardCard.setkEndColor(new java.awt.Color(17, 0, 173));
        DashboardCard.setkStartColor(new java.awt.Color(27, 27, 75));

        HumSlider.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        HumSlider.setForeground(new java.awt.Color(255, 255, 0));
        HumSlider.setMaximum(60);
        HumSlider.setMinimum(1);
        HumSlider.setValue(1);
        HumSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        HumSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                HumSliderStateChanged(evt);
            }
        });
        HumSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                HumSliderMouseReleased(evt);
            }
        });

        TemSlider.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        TemSlider.setForeground(new java.awt.Color(255, 255, 0));
        TemSlider.setMaximum(60);
        TemSlider.setMinimum(1);
        TemSlider.setValue(1);
        TemSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        TemSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TemSliderStateChanged(evt);
            }
        });
        TemSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TemSliderMouseReleased(evt);
            }
        });
        TemSlider.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TemSliderKeyReleased(evt);
            }
        });

        TempLineChart.setOpaque(false);
        TempLineChart.setPreferredSize(new java.awt.Dimension(600, 300));
        TempLineChart.setLayout(new java.awt.BorderLayout());

        HumLineChart.setOpaque(false);
        HumLineChart.setPreferredSize(new java.awt.Dimension(600, 300));
        HumLineChart.setLayout(new java.awt.BorderLayout());

        TemHumBarChart.setOpaque(false);
        TemHumBarChart.setPreferredSize(new java.awt.Dimension(600, 300));
        TemHumBarChart.setLayout(new java.awt.BorderLayout());

        TemPieChart1.setOpaque(false);
        TemPieChart1.setPreferredSize(new java.awt.Dimension(600, 300));
        TemPieChart1.setLayout(new java.awt.BorderLayout());

        HumPieChart.setOpaque(false);
        HumPieChart.setPreferredSize(new java.awt.Dimension(600, 300));
        HumPieChart.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout DashboardCardLayout = new javax.swing.GroupLayout(DashboardCard);
        DashboardCard.setLayout(DashboardCardLayout);
        DashboardCardLayout.setHorizontalGroup(
            DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DashboardCardLayout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(TemSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(HumSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102))
            .addGroup(DashboardCardLayout.createSequentialGroup()
                .addGroup(DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DashboardCardLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(TempLineChart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DashboardCardLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(TemHumBarChart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DashboardCardLayout.createSequentialGroup()
                        .addComponent(HumLineChart, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                        .addGap(55, 55, 55))
                    .addGroup(DashboardCardLayout.createSequentialGroup()
                        .addComponent(TemPieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(HumPieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        DashboardCardLayout.setVerticalGroup(
            DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DashboardCardLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TempLineChart, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                    .addComponent(HumLineChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TemSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HumSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(HumPieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TemPieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TemHumBarChart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(638, Short.MAX_VALUE))
        );

        cardLayouts.add(DashboardCard, "DashboardCard");

        DataCard.setkEndColor(new java.awt.Color(69, 69, 209));
        DataCard.setkStartColor(new java.awt.Color(23, 23, 69));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setOpaque(false);

        DataTable.setBackground(javax.swing.UIManager.getDefaults().getColor("window"));
        DataTable.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        DataTable.setModel(new javax.swing.table.DefaultTableModel(
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
        DataTable.setRowHeight(20);
        DataTable.setShowGrid(false);
        jScrollPane1.setViewportView(DataTable);

        javax.swing.GroupLayout DataCardLayout = new javax.swing.GroupLayout(DataCard);
        DataCard.setLayout(DataCardLayout);
        DataCardLayout.setHorizontalGroup(
            DataCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1370, Short.MAX_VALUE)
        );
        DataCardLayout.setVerticalGroup(
            DataCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DataCardLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 899, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 480, Short.MAX_VALUE))
        );

        cardLayouts.add(DataCard, "DataCard");

        StatisticCard.setkEndColor(new java.awt.Color(69, 69, 209));
        StatisticCard.setkStartColor(new java.awt.Color(23, 23, 69));

        javax.swing.GroupLayout StatisticCardLayout = new javax.swing.GroupLayout(StatisticCard);
        StatisticCard.setLayout(StatisticCardLayout);
        StatisticCardLayout.setHorizontalGroup(
            StatisticCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1370, Short.MAX_VALUE)
        );
        StatisticCardLayout.setVerticalGroup(
            StatisticCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1379, Short.MAX_VALUE)
        );

        cardLayouts.add(StatisticCard, "StatisticCard");

        SettingsCard.setkEndColor(new java.awt.Color(69, 69, 209));
        SettingsCard.setkStartColor(new java.awt.Color(23, 23, 69));

        javax.swing.GroupLayout SettingsCardLayout = new javax.swing.GroupLayout(SettingsCard);
        SettingsCard.setLayout(SettingsCardLayout);
        SettingsCardLayout.setHorizontalGroup(
            SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1370, Short.MAX_VALUE)
        );
        SettingsCardLayout.setVerticalGroup(
            SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1379, Short.MAX_VALUE)
        );

        cardLayouts.add(SettingsCard, "SettingsCard");

        PresentPanel.add(cardLayouts, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(PresentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 0, 1370, 900));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ExitButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ExitButtonMouseReleased
       
    }//GEN-LAST:event_ExitButtonMouseReleased

    private void ExitButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ExitButtonMousePressed
        // TODO add your handling code here:
        ExitButton.setBackground(new Color(11, 11, 33));
        System.exit(0);
    }//GEN-LAST:event_ExitButtonMousePressed

    private void SettingsButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SettingsButtonMousePressed
        // TODO add your handling code here:
        card.show(cardLayouts, "SettingsCard");
        try {
            SelectionFromLeftNav(3);
        } catch (IOException ex) {
            Logger.getLogger(mainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_SettingsButtonMousePressed

    private void StatisticsButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StatisticsButtonMousePressed
        // TODO add your handling code here:
        card.show(cardLayouts, "StatisticCard");
        try {
            SelectionFromLeftNav(2);
        } catch (IOException ex) {
            Logger.getLogger(mainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_StatisticsButtonMousePressed

    private void DataButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DataButtonMousePressed
        // TODO add your handling code here:
        //-------------------------------------------------------------------------------------------------------
        card.show(cardLayouts, "DataCard");      
        try {
            SelectionFromLeftNav(1);
        } catch (IOException ex) {
            Logger.getLogger(mainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_DataButtonMousePressed

    private void DashboardButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DashboardButtonMousePressed
        try {
            // TODO add your handling code here:
            SelectionFromLeftNav(0);
        } catch (IOException ex) {
            Logger.getLogger(mainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        card.show(cardLayouts , "DashboardCard");
    }//GEN-LAST:event_DashboardButtonMousePressed

    private void HumSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_HumSliderStateChanged
        // TODO add your handling code here:
        showLineHumChart(HumSlider.getValue());
    }//GEN-LAST:event_HumSliderStateChanged

    private void HumSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HumSliderMouseReleased
        // TODO add your handling code here:
        showLineHumChart(HumSlider.getValue());
    }//GEN-LAST:event_HumSliderMouseReleased

    private void TemSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TemSliderStateChanged
        // TODO add your handling code here:
        showLineTempChart(TemSlider.getValue());
    }//GEN-LAST:event_TemSliderStateChanged

    private void TemSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TemSliderMouseReleased
        // TODO add your handling code here:
        showLineTempChart(TemSlider.getValue());
    }//GEN-LAST:event_TemSliderMouseReleased

    private void TemSliderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TemSliderKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_TemSliderKeyReleased
    
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
    private keeptoo.KGradientPanel DashboardCard;
    private javax.swing.JLabel DashboardLabel;
    private rojerusan.RSButtonPane DataButton;
    private keeptoo.KGradientPanel DataCard;
    private javax.swing.JLabel DataLabel;
    private javax.swing.JTable DataTable;
    private rojerusan.RSButtonPane ExitButton;
    private javax.swing.JLabel ExitLabel;
    private keeptoo.KGradientPanel GradientMAIN;
    private javax.swing.JPanel HumLineChart;
    private javax.swing.JPanel HumPieChart;
    private javax.swing.JSlider HumSlider;
    private javax.swing.JPanel LeftNav;
    private javax.swing.JPanel PresentPanel;
    private rojerusan.RSButtonPane SettingsButton;
    private keeptoo.KGradientPanel SettingsCard;
    private javax.swing.JLabel SettingsLabel;
    private keeptoo.KGradientPanel StatisticCard;
    private rojerusan.RSButtonPane StatisticsButton;
    private javax.swing.JLabel StatisticsLabel;
    private javax.swing.JPanel TemHumBarChart;
    private javax.swing.JPanel TemPieChart1;
    private javax.swing.JSlider TemSlider;
    private javax.swing.JPanel TempLineChart;
    private javax.swing.JLabel Welcome_userLabe;
    private javax.swing.JPanel cardLayouts;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
