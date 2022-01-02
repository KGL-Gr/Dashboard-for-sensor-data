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
    public static String id;
    ArrayList<Character> key;
    private static String displayName;

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static void setId(String id) {
        User.id = id;
    }

    public static String getId() {
        return id;
    }


            
}
