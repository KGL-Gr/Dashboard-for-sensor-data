/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project.login;

import java.io.*;

/**
 *
 * @author panos
 */
public class Save {
    Save(User user, int yes, String search, String password, String wrongname) throws IOException, ClassNotFoundException{
        if(yes == 1)
        {
            saveUser(user, yes, search, password, wrongname);
        }
        else if(yes == 0)
        {
            loadUser(user, yes, search, password, wrongname);
        }
    }
    //apothikeuei ta stoixia tou user se enan ser file(prepei na alaksoume topo8esia)
    public void saveUser(User user, int yes, String search, String password, String wrongname) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        String file = user.name;//to file exei to onoma tou user
        String tempSearch = file;
        file = file + ".ser";
        //---------------------------------------------
        //username
        //search = search + ".ser";//filename
        try{
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            user = (User)in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Username already exist");
            //System.out.println("tempsearch : " + tempSearch + " password : " +password);
            //Encryption test = new Encryption(user, yes, tempSearch, password);
            //String found = "Found";
        }
        catch(IOException ex)//ama den uparxei user 
        {
            System.out.println("IOException is caught");
            System.out.println("No user found registration OK");
            //====================================================
            try{
                FileOutputStream fileOut = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(user);
                out.close();
                fileOut.close();
                System.out.println("saved! " +user.name +" " + user.password);
            }
            catch(IOException ep)
            {
                System.out.println("IOException is caught");
            }
            
        }
        System.out.println(user.name+ " : onoma user " + tempSearch + " :temp");
        /*if(search.equals(user.name))
        {
            System.out.println("Already exist");
        }
        else{
            //========================

        }*/
    }
    public void loadUser(User user, int yes, String search, String password, String wrongname) throws ClassNotFoundException
    {
        String tempSearch = search;//username
        search = search + ".ser";//filename
        try{
            FileInputStream fileIn = new FileInputStream(search);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            user = (User)in.readObject();
            in.close();
            fileIn.close();
            System.out.println("onoma : " + user.name + " kodikos : "+ user.password + " key : "+ user.key );
            System.out.println("tempsearch : " + tempSearch + " password : " +password);
            Encryption test = new Encryption(user, yes, tempSearch, password);
            //String found = "Found";
        }
        catch(IOException ex)//ama den uparxei user 
        {
            System.out.println("IOException is caught");
            System.out.println("No user found :(");
            //String notFound = "User not found";
        }
    }
}

