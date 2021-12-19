/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project.login;
import java.util.*;
/**
 *
 * @author panos
 */
public class Encryption {
    private Random random;
    private ArrayList<Character> pin;
    private ArrayList<Character> randomPin;
    private char character;
    private String line;
    private char[] pass;
   // User user = new User();
    Encryption(User user, int yes, String search, String password){
        random = new Random();
        pin = new ArrayList();
        randomPin = new ArrayList();
        character = ' ';
        //ama einai 1 kanei to encryptio 0 decryption
        if(yes == 1)
        {
            System.out.println("oxi");
            genkey(user);//create
        }
        else if(yes == 0)
        {
            System.out.println("mesa");
            decrypt(user, search, password);
        }
    }
    private void genkey(User user){ //dhmiourgei to prosopiko kleidi
        character = ' ';
        pin.clear();
        int i = 32;
        
        for(i=32;i<127;i++)
        {
            pin.add(Character.valueOf(character));
            character++;
        }
        randomPin = new ArrayList(pin);
        Collections.shuffle(randomPin);
        user.key = randomPin;
        //password = user.password;
        encrypt(user);
    }
    private void getkey(){// deixnei to key
        System.out.println();
        for(Character x : pin)
        {
            System.out.println(x);
        }
        System.out.println();
        for(Character x : randomPin)
        {
            System.out.println(x);
        }
    }
    private void encrypt(User user){ //kanei to encryption
        int i=0, j=0;
        //String test = user.password;
        //apo string se char
        pass = user.password.toCharArray();
        user.password = " ";
        for(i=0;i<pass.length;i++)
        {
         for(j=0;j<pin.size();j++)
         {
             if(pass[i]==pin.get(j))
             {
                 pass[i]=randomPin.get(j);
                 break;
             }
         }
        }
        //apo char se string
        for(i=0;i<pass.length;i++)
        {
            user.password = user.password + String.valueOf(pass[i]);
            System.out.println(user.password);
        }
        //deixnei to password
        for(Character x : pass)
        {
            System.out.println(x);
        }
    }
    public void decrypt(User user, String search, String password){//decryption
        int i=0, j=0;
        for(i=32;i<127;i++)
        {
            pin.add(Character.valueOf(character));
            character++;
        }
        randomPin = user.key;
        System.out.println("auto : "+ user.password);
        pass = user.password.toCharArray();
        for(i=0;i<pass.length;i++)
        {
         for(j=0;j<randomPin.size();j++)
         {
             if(pass[i]==randomPin.get(j))
             {
                 pass[i]=pin.get(j);
                 break;
             }
         }
        }
        user.password = "";
        System.out.println("adio string : "+ user.password);
        for(i=1;i<pass.length;i++)
        {
            user.password = user.password + String.valueOf(pass[i]);
            //System.out.println("reapet : "+ user.password + " eleos : "+ pass[i]);
        }
        System.out.println(user.name + " " + user.password);
        login load = new login(user, search, password);
        
    }
    
}
