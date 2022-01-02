package dashboardforsensordata;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
 * showHumPieChart      : Will display (,20] , (20,45] , (45,65] , (65,90] , (90,) as Very High, High, OK, Low, Very Low respectively.
 * showTemPieChart      : Will display (,4] , (4,8] , (8,12] , (12,16] , (16,) asVery High, High, OK, Low, Very Low respectively.
 * showBarChart         : Will display day average for Temperature&Humidity sample values.
 * showLineTempChart    : Will display a linechart for the temperature values, also it has slider.
 * showLineHumChart     : Will display a linechart for the humidity values, also it has slider.
 * showStats()          : Will do the calculations for the Statistics table.
 * @author kgl
 */

public class mainApp extends javax.swing.JFrame {
 
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    Color Default = new Color(23, 23, 69);          // Commonly used.
    CardLayout card;                                // Card Layout.
    ArrayList<StructuredData> list;                 // DD for Table.
    File file;                                      // Data file.
    int ChoiceTracer = -1;                          // Side Nav user selection Trace.
    int TemIndex = 0;                               // Has to be here to allow FillTemp, I don't want to keep track of more stuff to the functions.
    int HumIndex = 0;                               // Has to be here to allow FillHum, I don't want to keep track of more stuff to the functions.
    double Humidity[][] = new double[120][10];      // 120 Slots aka days, for 10 values.
    double Temperature[][] = new double[120][10];   // 120 Slots aka days, for 10 values.
    int SampleTime = -1;                            // 10 values consist 1 sample. 1 Day = 2 samples.
    boolean guard = false;                          // DataToTable won't run twice.
    boolean done = false;                           // CalcStats won't run twice.
    int TempCounter[] = {0, 0, 0, 0, 0};            // Counts Very High, High, OK, Low, Very Low values respectively, for Temperature.
    int HumiCounter[] = {0, 0, 0, 0, 0};            // Counts Very High, High, OK, Low, Very Low values respectively, for Humidity.
    double prevailTemp, prevailHumi;                // Prevailing
    int maxTemp, maxHumi;
    int sumTemp, sumHumi;
    String[] Days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    String[] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    int[] MonthEndsIn = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    int TempVeryHigh = 16, TempHigh = 12, TempLow = 8, TempVeryLow = 4;
    int HumiVeryHigh = 90, HumiHigh = 65, HumiLow = 45, HumiVeryLow = 20;
    User user;
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
    
    public void ReadData() throws FileNotFoundException, IOException {
        String stLine;

        try {
            file = new File("data.txt");

            BufferedReader Br = new BufferedReader(new FileReader(file));

            while ((stLine = Br.readLine()) != null) {

                if (stLine.matches("../..")) {

                    SampleTime++;   // Index for the sample.
                    TemIndex = 0;
                    HumIndex = 0;

                } else {

                    if (stLine.startsWith("Te")) {

                        FillTemp(stLine);
                        TemIndex++;

                    } else if (stLine.startsWith("Hu")) {

                        FillHum(stLine);
                        HumIndex++;
                    }
                }

            }

        } catch (FileNotFoundException e) {
            System.out.println("Wrong Path!");

        }
    } 
    
    public void FillTemp(String stLine){
        
        Temperature[SampleTime][TemIndex] = Double.parseDouble(stLine.substring(17));
    }
    
    public void FillHum(String stLine){
        
        Humidity[SampleTime][HumIndex] = Double.parseDouble(stLine.substring(14));
    }
     
    public ArrayList ListData() {

        list = new ArrayList<>();
        int date = 29;
        int monthcounter = 10;
        int Samples = SampleTime;

        for (int i = 0; i <= Samples; i++) {

            if (date % MonthEndsIn[monthcounter % 12] == 0 && list.size() % 20 == 0) {
                monthcounter++;
                date = 1;
            } else if (list.size() % 20 == 0) {
                date++;
            }

            for (int j = 0; j < 10; j++) {

                list.add(new StructuredData(valueOf(date % (MonthEndsIn[monthcounter % 12] + 1)) + " " + Months[monthcounter % 12], Humidity[i][j], Temperature[i][j]));
            }
            // 31 December... Should be generalized, this is not a formal solution.
            // Works for years 2021-2022-2023. Απόρροια αυτής της υλοποίησης είναι ο τρόπος ανάκτησης των δεδομένων και οι κακές πρακτικές στην αποθήκευσή τους.

        }

        return list;
    }
    
    public void DataToTable() throws IOException {
        int DaysCounter = 0; // 29/12 was Monday

        DefaultTableModel model = (DefaultTableModel) DataTable.getModel();
        list = ListData();

        Object rowData[] = new Object[3];

        for (int i = 0; i < list.size(); i++) {
            if (i % 20 == 0) {
                DaysCounter++;
            }
            rowData[0] = Days[DaysCounter % 7] + " " + list.get(i).Date;
            rowData[1] = list.get(i).Humidity;
            rowData[2] = list.get(i).Temperature;
            model.addRow(rowData);
        }
    }
    
    public void CalcStats() {
        
        // Mean, Median, Prevailing, min and max values.
        double[] TempValues = new double[list.size()];
        double[] HumiValues = new double[list.size()];
        // average values
        sumTemp = 0;
        sumHumi = 0;
        // They need to be recalculated because, it is possible for the user to
        // read wrong info if he performs a sequence of actions (0,1,3,2).
        for (int i = 0; i < list.size(); i++) {
            sumTemp += list.get(i).Temperature;
            sumHumi += list.get(i).Humidity;
            TempValues[i] = list.get(i).Temperature;
            HumiValues[i] = list.get(i).Humidity;
            
            if (list.get(i).Temperature > TempVeryHigh) {
                TempCounter[0]++;
            } else if (list.get(i).Temperature > TempHigh) {
                TempCounter[1]++;
            } else if (list.get(i).Temperature > TempLow) {
                TempCounter[2]++;
            } else if (list.get(i).Temperature > TempVeryLow) {
                TempCounter[3]++;
            } else {
                TempCounter[4]++;
            }
            
            if (list.get(i).Humidity > HumiVeryHigh) {
                HumiCounter[0]++;
            } else if (list.get(i).Humidity > HumiHigh) {
                HumiCounter[1]++;
            } else if (list.get(i).Humidity > HumiLow) {
                HumiCounter[2]++;
            } else if (list.get(i).Humidity > HumiVeryLow) {
                HumiCounter[3]++;
            } else {
                HumiCounter[4]++;
            }
        }
        Arrays.sort(TempValues);
        Arrays.sort(HumiValues);

        // prevailing value
        double Temptrace = TempValues[0];
        prevailTemp = TempValues[0];
        int counterTemp = 0;
        maxTemp = 0;

        double Humitrace = HumiValues[0];
        prevailHumi = HumiValues[0];
        int counterHumi = 0;
        maxHumi = 0;
        // nlogn the best implementation I could think of.
        for (int i = 1; i < list.size(); i++) {

            if (TempValues[i] == Temptrace) {
                counterTemp++;
            } else {

                if (counterTemp > maxTemp) {
                    prevailTemp = TempValues[i - 1];
                    maxTemp = counterTemp;
                }
                Temptrace = TempValues[i];
                counterTemp = 1;
            }

            if (HumiValues[i] == Humitrace) {
                counterHumi++;
            } else {

                if (counterHumi > maxHumi) {
                    prevailHumi = HumiValues[i - 1];
                    maxHumi = counterHumi;
                }
                Humitrace = HumiValues[i];
                counterHumi = 1;
            }

        }
        sumTemp /= list.size();
        sumHumi /= list.size();

        DefaultTableModel Secmodel = (DefaultTableModel) jTable1.getModel();
        Object rowData[] = new Object[3];

        rowData[0] = "Mean";
        rowData[1] = sumTemp;
        rowData[2] = sumHumi;
        Secmodel.addRow(rowData);
        rowData[0] = "Median";
        rowData[1] = TempValues[list.size() / 2];
        rowData[2] = HumiValues[list.size() / 2];
        Secmodel.addRow(rowData);
        rowData[0] = "Prevailing Value";
        rowData[1] = prevailTemp;
        rowData[2] = prevailHumi;
        Secmodel.addRow(rowData);
        rowData[0] = "Times Appeared";
        rowData[1] = maxTemp;
        rowData[2] = maxHumi;
        Secmodel.addRow(rowData);
        rowData[0] = "Minimum";
        rowData[1] = TempValues[0];
        rowData[2] = HumiValues[0];
        Secmodel.addRow(rowData);
        rowData[0] = "Maximum";
        rowData[1] = TempValues[TempValues.length - 1];
        rowData[2] = HumiValues[HumiValues.length - 1];
        Secmodel.addRow(rowData);
        rowData[0] = "Very High";
        rowData[1] = TempCounter[0];
        rowData[2] = HumiCounter[0];
        Secmodel.addRow(rowData);
        rowData[0] = "High";
        rowData[1] = TempCounter[1];
        rowData[2] = HumiCounter[1];
        Secmodel.addRow(rowData);
        rowData[0] = "OK";
        rowData[1] = TempCounter[2];
        rowData[2] = HumiCounter[2];
        Secmodel.addRow(rowData);
        rowData[0] = "Low";
        rowData[1] = TempCounter[3];
        rowData[2] = HumiCounter[3];
        Secmodel.addRow(rowData);
        rowData[0] = "Very Low";
        rowData[1] = TempCounter[4];
        rowData[2] = HumiCounter[4];
        Secmodel.addRow(rowData);
    }
    
    public void giveTempAdvice(){        
        if(((TempCounter[0]+TempCounter[1])/list.size())>(TempCounter[2]/list.size())){
            RecommendationText.setText("Take meassures to cool it down.");
            
            if(maxTemp < sumTemp/list.size()){  // Prevailing value freq vs mean
                RecommendationText.setText("You should definitely take meassures to cool it down!");
                
            }
            return;
        }
        
        if(((TempCounter[4]+TempCounter[3])/list.size())>(TempCounter[2]/list.size())){
            RecommendationText.setText("Take meassures to heat it up.");
            
            if(maxTemp > sumTemp/list.size()){  // Prevailing value freq vs mean
                RecommendationText.setText("You should definitely take meassures to heat it up!");
            }
            return;
        }
        
        
        RecommendationText.setText("Temperature is OK!");
    }
    
    public void giveHumiAdvice(){
        if(((HumiCounter[0]+HumiCounter[1])/list.size())>HumiCounter[2]/list.size()){
            RecommendationText.setText("Take meassures to lower the humidity.");
            
            if(maxTemp < sumTemp/list.size()){  // Prevailing value freq vs mean
                RecommendationText.setText("You should definitely take meassures to lower the humidity!");
                
            }
            return;
        }
        
        if(((HumiCounter[4]+HumiCounter[3])/list.size())>HumiCounter[2]/list.size()){
            RecommendationText.setText("Take meassures to raise the humidity.");
            
            if(maxHumi < sumHumi/list.size()){  // Prevailing value freq vs mean
                RecommendationText.setText("You should definitely take meassures to raise the humidity!");
                
            }
            return;
        }
        RecommendationText.setText("Humidity is OK!");
    }
    
    public mainApp() throws IOException {
        
        addWindowListener(new WindowAdapter() {//puts the display name and admin
            @Override
            public void windowOpened(WindowEvent e) {
                String usa = User.getDisplayName(); 
                Welcome_userLabe.setText("Welcome " +usa);
                if(User.getId()!=null)
                {
                    Admin.setVisible(true);
                }
            }
            });
        ReadData();
        ListData();
        System.out.println(list.toString());
        initComponents();
        
        Admin.setVisible(false);
        DataTable.setBackground(new Color(0, 0, 0, 0));
        jScrollPane1.setBackground(new Color(0, 0, 0, 0));
        DataTable.setOpaque(false);
        jScrollPane1.setOpaque(false);
        DataTable.setForeground(Color.BLACK);
        jScrollPane1.getViewport().setOpaque(false);
        
        jTable1.setBackground(new Color(0,0,0,0));
        jTable1.setOpaque(false);
        jTable1.setForeground(Color.BLACK);

        TemSlider.setMaximum(SampleTime);
        HumSlider.setMaximum(SampleTime);

        card = (CardLayout) (cardLayouts.getLayout());

    }
    
    public void showTemPieChart() {

        DefaultPieDataset barDataset = new DefaultPieDataset();
        int Counter[] = {0, 0, 0, 0, 0};

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).Temperature > TempVeryHigh) {
                Counter[0]++;
            } else if (list.get(i).Temperature > TempHigh) {
                Counter[1]++;
            } else if (list.get(i).Temperature > TempLow) {
                Counter[2]++;
            } else if (list.get(i).Temperature > TempVeryLow) {
                Counter[3]++;
            } else {
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

        PiePlot piePlot = (PiePlot) piechart.getPlot();

        piePlot.setSectionPaint("Very High", new Color(227, 41, 41));
        piePlot.setSectionPaint("High", new Color(227, 131, 41));
        piePlot.setSectionPaint("OK", new Color(19, 181, 11));
        piePlot.setSectionPaint("Low", new Color(0, 179, 255));
        piePlot.setSectionPaint("Very Low", new Color(13, 19, 209));

        piePlot.setBackgroundPaint(new Color(0, 0, 80, 20));
        piePlot.setOutlinePaint(new Color(20, 20, 20, 20));

        ChartPanel pieChartPanel = new ChartPanel(piechart);
        pieChartPanel.setOpaque(false);
        pieChartPanel.setBackground(new Color(0, 0, 80, 20));
        TemPieChart.removeAll();
        TemPieChart.add(pieChartPanel, BorderLayout.CENTER);
        TemPieChart.validate();

    }
    
    public void showHumPieChart() {
        DefaultPieDataset barDataset = new DefaultPieDataset();
        int Counter[] = {0, 0, 0, 0, 0};

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).Humidity > HumiVeryHigh) {
                Counter[0]++;
            } else if (list.get(i).Humidity > HumiHigh) {
                Counter[1]++;
            } else if (list.get(i).Humidity > HumiLow) {
                Counter[2]++;
            } else if (list.get(i).Humidity > HumiVeryLow) {
                Counter[3]++;
            } else {
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

        PiePlot piePlot = (PiePlot) piechart.getPlot();

        piePlot.setSectionPaint("Very High", new Color(227, 41, 41));// Nice
        piePlot.setSectionPaint("High", new Color(227, 131, 41));    // Fits ^
        piePlot.setSectionPaint("OK", new Color(19, 181, 11));           // 
        piePlot.setSectionPaint("Low", new Color(0, 179, 255));
        piePlot.setSectionPaint("Very Low", new Color(13, 19, 209));

        piePlot.setBackgroundPaint(new Color(0, 0, 80, 20));
        piePlot.setOutlinePaint(new Color(20, 20, 20, 20));

        ChartPanel pieChartPanel = new ChartPanel(piechart);
        pieChartPanel.setOpaque(false);
        pieChartPanel.setBackground(new Color(0, 0, 80, 20));
        HumPieChart.removeAll();
        HumPieChart.add(pieChartPanel, BorderLayout.CENTER);
        HumPieChart.validate();

    }
    
    // Major problem, completely freaks out when you click it.
    public void showBarChart() {
        DefaultCategoryDataset UniData = new DefaultCategoryDataset();
        int SumTemp = 0, SumHumi = 0;

        for (int i = 0; i < list.size(); i++) {
            SumTemp += list.get(i).Temperature;
            SumHumi += list.get(i).Humidity;
            if (i % 20 == 0) {
                UniData.setValue(SumTemp / 20, "Temperature", i + "");
                UniData.setValue(SumHumi / 20, "Humidity", i + "");
                SumTemp = 0;
                SumHumi = 0;
            }
        }

        JFreeChart chart = ChartFactory.createBarChart("Bar Chart", "Day", "Value",
                UniData,
                PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);

        categoryPlot.setBackgroundPaint(new Color(0, 0, 0, 0));

        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        Color clr1 = new Color(51, 0, 255);
        Color clr2 = new Color(255, 0, 51);
        renderer.setSeriesPaint(0, clr2);
        renderer.setSeriesPaint(1, clr1);

        chart.setBackgroundPaint(null);
        ChartPanel barChartPanel = new ChartPanel(chart);
        barChartPanel.setBackground(new Color(0, 0, 0, 0));
        TemHumBarChart.removeAll();
        TemHumBarChart.add(barChartPanel, BorderLayout.CENTER);
        TemHumBarChart.validate();

    }
    
    public void showLineTempChart() {
        DefaultCategoryDataset Tempdataset = new DefaultCategoryDataset();

        for (int i = 0; i < list.size(); i++) {
            Tempdataset.setValue(list.get(i).Temperature, "Temperature", i + "");
        }
        JFreeChart linechart = ChartFactory.createLineChart("Temperature", "SampleTimings", "Readings", Tempdataset, PlotOrientation.VERTICAL, false, true, false);
        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();
        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255, 255, 0);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        ChartPanel lineChartPanel = new ChartPanel(linechart);

        lineChartPanel.setOpaque(false);
        lineCategoryPlot.setBackgroundPaint(new Color(0, 0, 0, 0));
        linechart.setBackgroundPaint(new Color(0, 0, 80, 25));
        lineChartPanel.setBackground(new Color(0, 0, 0, 0));

        TempLineChart.removeAll();
        TempLineChart.add(lineChartPanel, BorderLayout.CENTER);
        TempLineChart.validate();

    }
    
    public void showLineTempChart(int k) {
        DefaultCategoryDataset Tempdataset = new DefaultCategoryDataset();

        TempLineChart.removeAll();
        for (int i = 0; i < list.size(); i += k) {
            Tempdataset.setValue(list.get(i).Temperature, "Temperature", i + "");
        }
        JFreeChart linechart = ChartFactory.createLineChart("Temperature", "SampleTimings", "Readings", Tempdataset, PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();
        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255, 255, 0);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        ChartPanel lineChartPanel = new ChartPanel(linechart);

        lineChartPanel.setOpaque(false);
        lineCategoryPlot.setBackgroundPaint(new Color(0, 0, 0, 0));
        linechart.setBackgroundPaint(new Color(0, 0, 80, 25));
        lineChartPanel.setBackground(new Color(0, 0, 0, 0));
        
        TempLineChart.removeAll();
        TempLineChart.add(lineChartPanel, BorderLayout.CENTER);
        TempLineChart.validate();

    }

    public void showLineHumChart() {
        DefaultCategoryDataset Humdataset = new DefaultCategoryDataset();

        for (int i = 0; i < list.size(); i++) {
            Humdataset.setValue(list.get(i).Humidity, "Humidity", i + "");
        }
        JFreeChart linechart = ChartFactory.createLineChart("Humidity", "SampleTimings", "Readings", Humdataset, PlotOrientation.VERTICAL, false, true, false);
        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();
        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255, 255, 0);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        ChartPanel lineChartPanel = new ChartPanel(linechart);

        lineChartPanel.setOpaque(false);
        lineCategoryPlot.setBackgroundPaint(new Color(0, 0, 0, 0));
        linechart.setBackgroundPaint(new Color(0, 0, 80, 25));
        lineChartPanel.setBackground(new Color(0, 0, 0, 0));

        HumLineChart.removeAll();
        HumLineChart.add(lineChartPanel, BorderLayout.CENTER);
        HumLineChart.validate();
    }
    
    public void showLineHumChart(int k) {
        DefaultCategoryDataset Humdataset = new DefaultCategoryDataset();

        HumLineChart.removeAll();
        for (int i = 0; i < list.size(); i += k) {
            Humdataset.setValue(list.get(i).Humidity, "Humidity", i + "");
        }
        JFreeChart linechart = ChartFactory.createLineChart("Humidity", "SampleTimings", "Readings", Humdataset, PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();

        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255, 255, 0);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        ChartPanel lineChartPanel = new ChartPanel(linechart);

        lineChartPanel.setOpaque(false);
        lineCategoryPlot.setBackgroundPaint(new Color(0, 0, 0, 0));

        linechart.setBackgroundPaint(new Color(0, 0, 80, 25));
        lineChartPanel.setBackground(new Color(0, 0, 0, 0));

        HumLineChart.removeAll();
        HumLineChart.add(lineChartPanel, BorderLayout.CENTER);
        HumLineChart.validate();

    }
    
    public void SelectionFromLeftNav(int key) throws IOException {
        if (key != ChoiceTracer) {
            switch (key) {
                case 0 -> {

                    DashboardButton.setColorNormal(new Color(11, 11, 33));
                    DataButton.setColorNormal(Default);
                    StatisticsButton.setColorNormal(Default);
                    SettingsButton.setColorNormal(Default);

                    DashboardLabel.setForeground(Color.YELLOW);
                    DataLabel.setForeground(Color.WHITE);
                    StatisticsLabel.setForeground(Color.WHITE);
                    SettingsLabel.setForeground(Color.WHITE);
                    ChoiceTracer = key;

                    showLineTempChart();
                    showLineHumChart();
                    showBarChart();
                    showTemPieChart();
                    showHumPieChart();
                }
                case 1 -> {

                    DashboardButton.setColorNormal(Default);
                    DataButton.setColorNormal(new Color(11, 11, 33));
                    StatisticsButton.setColorNormal(Default);
                    SettingsButton.setColorNormal(Default);

                    DataLabel.setForeground(Color.YELLOW);
                    DashboardLabel.setForeground(Color.WHITE);
                    StatisticsLabel.setForeground(Color.WHITE);
                    SettingsLabel.setForeground(Color.WHITE);
                    ChoiceTracer = key;
                    if (!guard) {
                        DataToTable();
                    }
                    guard = true;

                }
                case 2 -> {
                    DashboardButton.setColorNormal(Default);
                    DataButton.setColorNormal(Default);
                    StatisticsButton.setColorNormal(new Color(11, 11, 33));
                    SettingsButton.setColorNormal(Default);

                    DashboardLabel.setForeground(Color.WHITE);
                    DataLabel.setForeground(Color.WHITE);
                    StatisticsLabel.setForeground(Color.YELLOW);
                    SettingsLabel.setForeground(Color.WHITE);
                    ChoiceTracer = key;
                    if (!done) {
                        CalcStats();
                    }
                    done = true;

                }
                case 3 -> {
                    DashboardButton.setColorNormal(Default);
                    DataButton.setColorNormal(Default);
                    StatisticsButton.setColorNormal(Default);
                    SettingsButton.setColorNormal(new Color(11, 11, 33));

                    DashboardLabel.setForeground(Color.WHITE);
                    DataLabel.setForeground(Color.WHITE);
                    StatisticsLabel.setForeground(Color.WHITE);
                    SettingsLabel.setForeground(Color.YELLOW);
                    ChoiceTracer = key;
                }
                default ->
                    System.out.println("Something went really wrong..");
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
        Admin = new javax.swing.JLabel();
        PresentPanel = new javax.swing.JPanel();
        cardLayouts = new javax.swing.JPanel();
        GradientMAIN = new keeptoo.KGradientPanel();
        DashboardCard = new keeptoo.KGradientPanel();
        HumSlider = new javax.swing.JSlider();
        TemSlider = new javax.swing.JSlider();
        TempLineChart = new javax.swing.JPanel();
        HumLineChart = new javax.swing.JPanel();
        TemHumBarChart = new javax.swing.JPanel();
        TemPieChart = new javax.swing.JPanel();
        HumPieChart = new javax.swing.JPanel();
        DataCard = new keeptoo.KGradientPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        DataTable = new javax.swing.JTable();
        StatisticCard = new keeptoo.KGradientPanel();
        jTable1 = new javax.swing.JTable();
        StatsInfoLabel = new javax.swing.JLabel();
        TempStatsLabel = new javax.swing.JLabel();
        HumiStatsLabel = new javax.swing.JLabel();
        RecommendationTempButton = new rojerusan.RSButtonPane();
        RecommendationLabel = new javax.swing.JLabel();
        RecommendationText = new javax.swing.JLabel();
        RecommendationHumiButton = new rojerusan.RSButtonPane();
        RecommendationLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        SettingsCard = new keeptoo.KGradientPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        VHTemp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        HTemp = new javax.swing.JTextField();
        LTemp = new javax.swing.JTextField();
        VLTemp = new javax.swing.JTextField();
        TempButton = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        VHerror = new javax.swing.JLabel();
        Herror = new javax.swing.JLabel();
        Lerror = new javax.swing.JLabel();
        VLerror = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        VHHum = new javax.swing.JTextField();
        HHum = new javax.swing.JTextField();
        LHum = new javax.swing.JTextField();
        VLHum = new javax.swing.JTextField();
        HumidityBut = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        VHHerror = new javax.swing.JLabel();
        HHerror = new javax.swing.JLabel();
        LHerror = new javax.swing.JLabel();
        VLHerror = new javax.swing.JLabel();

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

        Admin.setBackground(new java.awt.Color(255, 255, 255));
        Admin.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Admin.setForeground(new java.awt.Color(255, 255, 255));
        Admin.setText("Admin");
        LeftNav.add(Admin, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 60, -1));

        getContentPane().add(LeftNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 900));

        PresentPanel.setBackground(new java.awt.Color(254, 254, 254));
        PresentPanel.setOpaque(false);
        PresentPanel.setLayout(new java.awt.BorderLayout());

        cardLayouts.setLayout(new java.awt.CardLayout());

        GradientMAIN.setkEndColor(new java.awt.Color(17, 0, 173));
        GradientMAIN.setkGradientFocus(2000);
        GradientMAIN.setkStartColor(new java.awt.Color(27, 27, 75));
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

        TemPieChart.setOpaque(false);
        TemPieChart.setPreferredSize(new java.awt.Dimension(600, 300));
        TemPieChart.setLayout(new java.awt.BorderLayout());

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
                        .addComponent(TemPieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(HumPieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        DashboardCardLayout.setVerticalGroup(
            DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DashboardCardLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TempLineChart, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                    .addComponent(HumLineChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TemSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HumSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(DashboardCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TemPieChart, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(TemHumBarChart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HumPieChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(663, Short.MAX_VALUE))
        );

        cardLayouts.add(DashboardCard, "DashboardCard");

        DataCard.setkEndColor(new java.awt.Color(17, 0, 173));
        DataCard.setkStartColor(new java.awt.Color(27, 27, 75));

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
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        DataTable.setRowHeight(20);
        DataTable.setShowGrid(false);
        DataTable.getTableHeader().setReorderingAllowed(false);
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
                .addGap(0, 531, Short.MAX_VALUE))
        );

        cardLayouts.add(DataCard, "DataCard");

        StatisticCard.setkEndColor(new java.awt.Color(17, 0, 173));
        StatisticCard.setkStartColor(new java.awt.Color(27, 27, 75));

        jTable1.setFont(new java.awt.Font("SansSerif", 2, 18)); // NOI18N
        jTable1.setForeground(new java.awt.Color(254, 254, 254));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setGridColor(new java.awt.Color(129, 128, 128));
        jTable1.getTableHeader().setReorderingAllowed(false);

        StatsInfoLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        StatsInfoLabel.setForeground(new java.awt.Color(254, 254, 254));
        StatsInfoLabel.setText("Statistics Info");

        TempStatsLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        TempStatsLabel.setForeground(new java.awt.Color(254, 254, 254));
        TempStatsLabel.setText("Temperature stats");

        HumiStatsLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        HumiStatsLabel.setForeground(new java.awt.Color(254, 254, 254));
        HumiStatsLabel.setText("Humidity stats");

        RecommendationTempButton.setBackground(new java.awt.Color(60, 60, 183));
        RecommendationTempButton.setColorHover(new java.awt.Color(23, 23, 69));
        RecommendationTempButton.setColorNormal(new java.awt.Color(60, 60, 183));
        RecommendationTempButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RecommendationTempButtonMouseClicked(evt);
            }
        });

        RecommendationLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        RecommendationLabel.setForeground(new java.awt.Color(254, 254, 254));
        RecommendationLabel.setText("For temperature");

        javax.swing.GroupLayout RecommendationTempButtonLayout = new javax.swing.GroupLayout(RecommendationTempButton);
        RecommendationTempButton.setLayout(RecommendationTempButtonLayout);
        RecommendationTempButtonLayout.setHorizontalGroup(
            RecommendationTempButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RecommendationTempButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RecommendationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addContainerGap())
        );
        RecommendationTempButtonLayout.setVerticalGroup(
            RecommendationTempButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RecommendationTempButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RecommendationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addContainerGap())
        );

        RecommendationText.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        RecommendationText.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        RecommendationHumiButton.setBackground(new java.awt.Color(60, 60, 183));
        RecommendationHumiButton.setColorHover(new java.awt.Color(23, 23, 69));
        RecommendationHumiButton.setColorNormal(new java.awt.Color(60, 60, 183));
        RecommendationHumiButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RecommendationHumiButtonMouseClicked(evt);
            }
        });

        RecommendationLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        RecommendationLabel1.setForeground(new java.awt.Color(254, 254, 254));
        RecommendationLabel1.setText("For humidity");

        javax.swing.GroupLayout RecommendationHumiButtonLayout = new javax.swing.GroupLayout(RecommendationHumiButton);
        RecommendationHumiButton.setLayout(RecommendationHumiButtonLayout);
        RecommendationHumiButtonLayout.setHorizontalGroup(
            RecommendationHumiButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RecommendationHumiButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RecommendationLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addContainerGap())
        );
        RecommendationHumiButtonLayout.setVerticalGroup(
            RecommendationHumiButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RecommendationHumiButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RecommendationLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel2.setText("See recommended actions:");

        javax.swing.GroupLayout StatisticCardLayout = new javax.swing.GroupLayout(StatisticCard);
        StatisticCard.setLayout(StatisticCardLayout);
        StatisticCardLayout.setHorizontalGroup(
            StatisticCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatisticCardLayout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addGroup(StatisticCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(StatisticCardLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RecommendationTempButton, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(RecommendationHumiButton, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
                    .addComponent(jTable1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(StatisticCardLayout.createSequentialGroup()
                        .addComponent(StatsInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TempStatsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HumiStatsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(RecommendationText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(206, Short.MAX_VALUE))
        );
        StatisticCardLayout.setVerticalGroup(
            StatisticCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatisticCardLayout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addGroup(StatisticCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StatsInfoLabel)
                    .addComponent(TempStatsLabel)
                    .addComponent(HumiStatsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(StatisticCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(StatisticCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(RecommendationTempButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(RecommendationHumiButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(RecommendationText, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(444, Short.MAX_VALUE))
        );

        cardLayouts.add(StatisticCard, "StatisticCard");

        SettingsCard.setkEndColor(new java.awt.Color(17, 0, 173));
        SettingsCard.setkStartColor(new java.awt.Color(27, 27, 75));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Set the temperature levels manually");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Very High Temperature");

        VHTemp.setBackground(new java.awt.Color(51, 51, 51));
        VHTemp.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        VHTemp.setForeground(new java.awt.Color(255, 255, 255));
        VHTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VHTempActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("High Temperature");

        jLabel6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Low Temperature");

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Very Low Temperature");

        HTemp.setBackground(new java.awt.Color(51, 51, 51));
        HTemp.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        HTemp.setForeground(new java.awt.Color(255, 255, 255));

        LTemp.setBackground(new java.awt.Color(51, 51, 51));
        LTemp.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        LTemp.setForeground(new java.awt.Color(255, 255, 255));

        VLTemp.setBackground(new java.awt.Color(51, 51, 51));
        VLTemp.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        VLTemp.setForeground(new java.awt.Color(255, 255, 255));

        TempButton.setBackground(new java.awt.Color(0, 0, 0));
        TempButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                TempButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                TempButtonMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TempButtonMousePressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 204, 255));
        jLabel5.setText("Change them");

        javax.swing.GroupLayout TempButtonLayout = new javax.swing.GroupLayout(TempButton);
        TempButton.setLayout(TempButtonLayout);
        TempButtonLayout.setHorizontalGroup(
            TempButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TempButtonLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel5)
                .addContainerGap(38, Short.MAX_VALUE))
        );
        TempButtonLayout.setVerticalGroup(
            TempButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TempButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        VHerror.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        VHerror.setForeground(new java.awt.Color(255, 0, 51));
        VHerror.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        Herror.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        Herror.setForeground(new java.awt.Color(204, 0, 51));
        Herror.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        Lerror.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        Lerror.setForeground(new java.awt.Color(204, 0, 51));
        Lerror.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        VLerror.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        VLerror.setForeground(new java.awt.Color(255, 0, 51));
        VLerror.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel8.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Set the humidity levels manually");

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Very High Humidity");

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("High Humidity");

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Low Humidity");

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Very Low Humidity");

        VHHum.setBackground(new java.awt.Color(51, 51, 51));
        VHHum.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        VHHum.setForeground(new java.awt.Color(255, 255, 255));

        HHum.setBackground(new java.awt.Color(51, 51, 51));
        HHum.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        HHum.setForeground(new java.awt.Color(255, 255, 255));

        LHum.setBackground(new java.awt.Color(51, 51, 51));
        LHum.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        LHum.setForeground(new java.awt.Color(255, 255, 255));

        VLHum.setBackground(new java.awt.Color(51, 51, 51));
        VLHum.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        VLHum.setForeground(new java.awt.Color(255, 255, 255));

        HumidityBut.setBackground(new java.awt.Color(0, 0, 0));
        HumidityBut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                HumidityButMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                HumidityButMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                HumidityButMousePressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(0, 0, 0));
        jLabel13.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Change them");

        javax.swing.GroupLayout HumidityButLayout = new javax.swing.GroupLayout(HumidityBut);
        HumidityBut.setLayout(HumidityButLayout);
        HumidityButLayout.setHorizontalGroup(
            HumidityButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HumidityButLayout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(40, 40, 40))
        );
        HumidityButLayout.setVerticalGroup(
            HumidityButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HumidityButLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addContainerGap())
        );

        VHHerror.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        VHHerror.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        HHerror.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        HHerror.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        LHerror.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        LHerror.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        VLHerror.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        VLHerror.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout SettingsCardLayout = new javax.swing.GroupLayout(SettingsCard);
        SettingsCard.setLayout(SettingsCardLayout);
        SettingsCardLayout.setHorizontalGroup(
            SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsCardLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Lerror, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(Herror, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(VHerror, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(VLerror, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(358, 358, 358)
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(HHerror, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                    .addComponent(VHHerror, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LHerror, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(VLHerror, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(SettingsCardLayout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SettingsCardLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(HTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(SettingsCardLayout.createSequentialGroup()
                                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(SettingsCardLayout.createSequentialGroup()
                                        .addGap(83, 83, 83)
                                        .addComponent(VHTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SettingsCardLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(LTemp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(VLTemp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(TempButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 163, Short.MAX_VALUE)
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(SettingsCardLayout.createSequentialGroup()
                        .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(79, 79, 79)
                        .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(VHHum)
                            .addComponent(HHum)
                            .addComponent(LHum)
                            .addComponent(VLHum)
                            .addComponent(HumidityBut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(212, 212, 212))
        );
        SettingsCardLayout.setVerticalGroup(
            SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsCardLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel8))
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(SettingsCardLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(VHHerror, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(SettingsCardLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(VHerror, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(VHTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(VHHum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Herror, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HHerror, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(HTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(HHum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SettingsCardLayout.createSequentialGroup()
                        .addComponent(Lerror, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(LTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(LHum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(VLerror, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(VLHerror, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(VLTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(VLHum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(SettingsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(HumidityBut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(TempButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(LHerror, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(982, Short.MAX_VALUE))
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

        ExitButton.setBackground(new Color(11, 11, 33));
        System.exit(0);
    }//GEN-LAST:event_ExitButtonMousePressed

    private void SettingsButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SettingsButtonMousePressed

        card.show(cardLayouts, "SettingsCard");
        try {
            SelectionFromLeftNav(3);
        } catch (IOException ex) {
            Logger.getLogger(mainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_SettingsButtonMousePressed

    private void StatisticsButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StatisticsButtonMousePressed

        card.show(cardLayouts, "StatisticCard");
        try {
            SelectionFromLeftNav(2);
        } catch (IOException ex) {
            Logger.getLogger(mainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_StatisticsButtonMousePressed

    private void DataButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DataButtonMousePressed

        card.show(cardLayouts, "DataCard");      
        try {
            SelectionFromLeftNav(1);
        } catch (IOException ex) {
            Logger.getLogger(mainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_DataButtonMousePressed

    private void DashboardButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DashboardButtonMousePressed

        try {
            SelectionFromLeftNav(0);
        } catch (IOException ex) {
            Logger.getLogger(mainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        card.show(cardLayouts , "DashboardCard");
    }//GEN-LAST:event_DashboardButtonMousePressed

    private void HumSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_HumSliderStateChanged
        showLineHumChart(HumSlider.getValue());
    }//GEN-LAST:event_HumSliderStateChanged

    private void HumSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HumSliderMouseReleased
        showLineHumChart(HumSlider.getValue());
    }//GEN-LAST:event_HumSliderMouseReleased

    private void TemSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TemSliderStateChanged
        showLineTempChart(TemSlider.getValue());
    }//GEN-LAST:event_TemSliderStateChanged

    private void TemSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TemSliderMouseReleased
        showLineTempChart(TemSlider.getValue());
    }//GEN-LAST:event_TemSliderMouseReleased

    private void TemSliderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TemSliderKeyReleased
    }//GEN-LAST:event_TemSliderKeyReleased

    private void RecommendationTempButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RecommendationTempButtonMouseClicked
        giveTempAdvice();
        RecommendationLabel.setForeground(Color.YELLOW);
    }//GEN-LAST:event_RecommendationTempButtonMouseClicked

    private void RecommendationHumiButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RecommendationHumiButtonMouseClicked
        giveHumiAdvice();
        RecommendationLabel1.setForeground(Color.YELLOW);
    }//GEN-LAST:event_RecommendationHumiButtonMouseClicked

    private void VHTempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VHTempActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_VHTempActionPerformed

    private void TempButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TempButtonMousePressed
        int VHT=0, HT=0, LT=0, VLT=0; //temperature levels
        try{
            try{
                VHT=Integer.parseInt(VHTemp.getText());
                VHerror.setText("Temp set!");
                VHerror.setForeground(Color.green);
            }
            catch(NumberFormatException ex)
            {
                VHerror.setText("Not a number");
                VHerror.setForeground(Color.red);
            }
            try{
                HT=Integer.parseInt(HTemp.getText());
                Herror.setText("Temp set!");
                Herror.setForeground(Color.green);
            }
            catch(NumberFormatException ex)
            {
                Herror.setText("Not a number");
                Herror.setForeground(Color.red);
            }
            try{
                LT=Integer.parseInt(LTemp.getText());
                Lerror.setText("Temp set!");
                Lerror.setForeground(Color.green);
            }
            catch(NumberFormatException ex)
            {
                Lerror.setText("Not a number");
                Lerror.setForeground(Color.red);
            }
            try{
                VLT=Integer.parseInt(VLTemp.getText());
                VLerror.setText("Temp set!");
                VLerror.setForeground(Color.green);
            }
            catch(NumberFormatException ex)
            {
                VLerror.setText("Not a number");
                VLerror.setForeground(Color.red);
            }
            if(VHT>HT && LT>VLT)
            {
                if(VHT>LT)
                {
                    TempVeryHigh= VHT;
                    TempHigh = HT;
                    TempLow = LT;
                    TempVeryLow = VLT;
                    System.out.println("done "+VHT+" "+HT +" lt "+ LT + " VLT "+VLT);
                }
                else if(LT>VHT)
                {
                    Lerror.setForeground(Color.red);
                    Lerror.setText("Low Temp cant be higher than Very high");
                }
                if(LT>HT)
                {
                    Lerror.setForeground(Color.red);
                    Lerror.setText("Low Temp cant be higher than high");
                }
            }
            else if(HT>VHT)
            {
                Herror.setForeground(Color.red);
                Herror.setText("High temp cant be higher than Very High");
                if(VLT>HT)
                {
                    VLerror.setForeground(Color.red);
                    VLerror.setText("Very Low cant be higher than High");  
                }
                if(VLT>LT)
                {
                    Lerror.setForeground(Color.red);
                    Lerror.setText("Low Temp cant be higher than High");
                }
            }
            if(VLT>LT)
            {
                VLerror.setForeground(Color.red);
                VLerror.setText("Very Low Temp cant be higher than Low");
            }
           
        }catch(NumberFormatException ex)
        {
            System.out.println("Unexpected");
        }   
        
    }//GEN-LAST:event_TempButtonMousePressed

    private void TempButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TempButtonMouseEntered

        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_TempButtonMouseEntered

    private void TempButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TempButtonMouseExited
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_TempButtonMouseExited

    private void HumidityButMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HumidityButMousePressed

        int VHT=0, HT=0, LT=0, VLT=0; //temperature levels
        try{
            try{
                VHT=Integer.parseInt(VHHum.getText());
                VHHerror.setText("Humidity set!");
                VHHerror.setForeground(Color.green);
            }
            catch(NumberFormatException ex)
            {
                VHHerror.setText("Not a number");
                VHHerror.setForeground(Color.red);
            }
            try{
                HT=Integer.parseInt(HHum.getText());
                HHerror.setText("Humidity set!");
                HHerror.setForeground(Color.green);
            }
            catch(NumberFormatException ex)
            {
                HHerror.setText("Not a number");
                HHerror.setForeground(Color.red);
            }
            try{
                LT=Integer.parseInt(LHum.getText());
                LHerror.setText("Humidity set!");
                LHerror.setForeground(Color.green);
            }
            catch(NumberFormatException ex)
            {
                LHerror.setText("Not a number");
                LHerror.setForeground(Color.red);
            }
            try{
                VLT=Integer.parseInt(VLHum.getText());
                VLHerror.setText("Humidity set!");
                VLHerror.setForeground(Color.green);
            }
            catch(NumberFormatException ex)
            {
                VLHerror.setText("Not a number");
                VLHerror.setForeground(Color.red);
            }
            if(VHT>HT && LT>VLT)
            {
                if(VHT>LT)
                {
                    HumiVeryHigh= VHT;
                    HumiHigh = HT;
                    HumiLow = LT;
                    HumiVeryLow = VLT;
                    System.out.println("done "+VHT+" "+HT +" lt "+ LT + " VLT "+VLT);
                }
                else if(LT>VHT)
                {
                    LHerror.setForeground(Color.red);
                    LHerror.setText("Low Hum cant be higher than Very high");
                }
                if(LT>HT)
                {
                    LHerror.setForeground(Color.red);
                    LHerror.setText("Low Hum cant be higher than high");
                }
            }
            else if(HT>VHT)
            {
                HHerror.setForeground(Color.red);
                HHerror.setText("High Hum cant be higher than Very High");
                if(VLT>HT)
                {
                    VLHerror.setForeground(Color.red);
                    VLHerror.setText("Very Low cant be higher than High");  
                }
                if(VLT>LT)
                {
                    LHerror.setForeground(Color.red);
                    LHerror.setText("Low Hum cant be higher than High");
                }
            }
            if(VLT>LT)
            {
                VLHerror.setForeground(Color.red);
                VLHerror.setText("Very Low Hum cant be higher than Low");
            }
           
        }catch(NumberFormatException ex)
        {
            System.out.println("Unexpected");
        }
        
    }//GEN-LAST:event_HumidityButMousePressed

    private void HumidityButMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HumidityButMouseEntered
        setCursor(new Cursor(Cursor.HAND_CURSOR));     
    }//GEN-LAST:event_HumidityButMouseEntered

    private void HumidityButMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HumidityButMouseExited
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_HumidityButMouseExited
    
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
    private javax.swing.JLabel Admin;
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
    private javax.swing.JLabel HHerror;
    private javax.swing.JTextField HHum;
    private javax.swing.JTextField HTemp;
    private javax.swing.JLabel Herror;
    private javax.swing.JPanel HumLineChart;
    private javax.swing.JPanel HumPieChart;
    private javax.swing.JSlider HumSlider;
    private javax.swing.JLabel HumiStatsLabel;
    private javax.swing.JPanel HumidityBut;
    private javax.swing.JLabel LHerror;
    private javax.swing.JTextField LHum;
    private javax.swing.JTextField LTemp;
    private javax.swing.JPanel LeftNav;
    private javax.swing.JLabel Lerror;
    private javax.swing.JPanel PresentPanel;
    private rojerusan.RSButtonPane RecommendationHumiButton;
    private javax.swing.JLabel RecommendationLabel;
    private javax.swing.JLabel RecommendationLabel1;
    private rojerusan.RSButtonPane RecommendationTempButton;
    private javax.swing.JLabel RecommendationText;
    private rojerusan.RSButtonPane SettingsButton;
    private keeptoo.KGradientPanel SettingsCard;
    private javax.swing.JLabel SettingsLabel;
    private keeptoo.KGradientPanel StatisticCard;
    private rojerusan.RSButtonPane StatisticsButton;
    private javax.swing.JLabel StatisticsLabel;
    private javax.swing.JLabel StatsInfoLabel;
    private javax.swing.JPanel TemHumBarChart;
    private javax.swing.JPanel TemPieChart;
    private javax.swing.JSlider TemSlider;
    private javax.swing.JPanel TempButton;
    private javax.swing.JPanel TempLineChart;
    private javax.swing.JLabel TempStatsLabel;
    private javax.swing.JLabel VHHerror;
    private javax.swing.JTextField VHHum;
    private javax.swing.JTextField VHTemp;
    private javax.swing.JLabel VHerror;
    private javax.swing.JLabel VLHerror;
    private javax.swing.JTextField VLHum;
    private javax.swing.JTextField VLTemp;
    private javax.swing.JLabel VLerror;
    private javax.swing.JLabel Welcome_userLabe;
    private javax.swing.JPanel cardLayouts;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
