/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project.login;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author panos
 */
public class User implements Serializable {
    String name;
    String password;
    String id;
    ArrayList<Character> key;
    String displayName;
    
    
    public void test()
    {
        System.out.println(password);
    }
            
}
