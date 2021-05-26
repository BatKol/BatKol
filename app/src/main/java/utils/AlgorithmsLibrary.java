package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class AlgorithmsLibrary
{
    public static String millisecondsToString(int time) {
        String elapsedTime = "";
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        elapsedTime = minutes+":";
        if(seconds < 10) {
            elapsedTime += "0";
        }
        elapsedTime += seconds;

        return  elapsedTime;
    }
    public static String hashMD5(String passwordToHash){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    public static boolean stringInArray(ArrayList<String> ls,String s){
        s = s.replaceAll(" ","");
        for (int i = 0; i < ls.size();i++) {
            String word = ls.get(i);
            System.out.println( s+" "+word + " "+ s.equals(word));
            if (s.equals(word))
                return true;
        }
        return false;
    }
    public static String parsStringafter(ArrayList<String> ls,String s){
        boolean flag = true;
        StringBuilder ans = new StringBuilder();
        s = s.replaceAll(" ","");
        for (int i = 0; i < ls.size();i++) {
            String word = ls.get(i);
            if (flag) {
                System.out.println(s + " " + word + " " + s.equals(word));
                if (s.equals(word))
                    flag = false;
            }
            else if (i<ls.size()-1)
                ans.append(word).append(" ");
            else
                ans.append(word);


        }
        return ans.toString();

    }
}
