/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dashboardforsensordata;
import java.io.*;

/**
 *
 * @author kgl
 */
public class DashboardForSensorData {

    static String stLine;
    static int TemIndex = 0;
    static int HumIndex = 0;
    static double Humidity [][] = new double[60][10];
    static double Temperature [][] = new double[60][10];
    static int SampleTime=-1;
    
    static void FillTemp(String stLine){
        
        Temperature[SampleTime][TemIndex] = Double.parseDouble(stLine.substring(17));
        
    }
    static void FillHum(String stLine){
        
        Humidity[SampleTime][HumIndex] = Double.parseDouble(stLine.substring(14));
    }
    public static void main(String[] args) throws FileNotFoundException, IOException{
        try{
            File file = new File("data.txt");
            
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
        
        for (int i =0; i<SampleTime;i++){
            for (int j=0; j<10;j++){
                System.out.println("Humidity is: "+Humidity[i][j]);
                System.out.println("Temperature is: "+Temperature[i][j]);
                
            }
        }
        dashboardforsensordata.mainApp.main(args);
    }
    
}
