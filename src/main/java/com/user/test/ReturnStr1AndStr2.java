package com.user.test;

public class ReturnStr1AndStr2 {

    public static void main(String[] args) {
        twoStringAsOutput("BC","BANGALORE");
    }

    public static void twoStringAsOutput(String str1, String str2){
        String op1="";
        String op2="";
        for(int i=0;i<str1.length();i++){
            if(!charSearch(str2, str1.charAt(i))){
                op1+=str1.charAt(i);
            }
        }
        for(int i=0;i<str2.length();i++){
            if(!charSearch(str1, str2.charAt(i))){
                op2+=str2.charAt(i);
            }
        }
        System.out.println("op1 ="+ op1);
        System.out.println("op2 ="+ op2);
    }
    public static boolean charSearch(String allStr, char c){
        if(allStr.contains(String.valueOf(c))){
            return true;
        }
        return false;
    }
}
