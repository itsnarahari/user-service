package com.user.test;

import java.util.stream.Stream;

public class EliminateDuplicateUsers {

    public static void main(String[] args) {
        String[] users = {"U1,U2","U3,U4","U1,U5","U2,U1","U3,U4"};
        Stream<String> stringStream = Stream.of(users);
        removeDuplicates(users);
    }
    public static void removeDuplicates(String[] users){

        for (int i = 0; i <users.length; i++) {
            for (int j = i+1; j < users.length; j++) {
                String[] eachIndex = users[i].split(",");
                if(charSearch(users[j],eachIndex[0]) && charSearch(users[j],eachIndex[1])){
//                    System.out.println(users[i]);
                    users[i]=null;
                    break;
                }
            }
        }
        System.out.println("New Array is");
        for (int i = 0; i < users.length; i++) {
            if(users[i]==null){
                continue;
            }
            System.out.println(users[i]);
        }
    }

    public static boolean charSearch(String allStr, String c){
        if(allStr.contains(c)){
            return true;
        }
        return false;
    }
}
