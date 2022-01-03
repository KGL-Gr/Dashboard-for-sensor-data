/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dashboardforsensordata;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author panos
 */
public class User implements Serializable {
    public String name;
    String password;
    public String id;
    ArrayList<Character> key;
    private static String displayName;
    private static String displayID;
    private static boolean EnteredMain = false;

    public static boolean isEnteredMain() {
        return EnteredMain;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public void setdisplayID(String displayID) {
        this.displayID = displayID;
    }

    public static String getdisplayID() {
        return displayID;
    }
    public static void setEnteredMain(boolean EnteredMain) {
        User.EnteredMain = EnteredMain;
    }

    


            
}
