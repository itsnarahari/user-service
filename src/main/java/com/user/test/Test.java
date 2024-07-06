package com.user.test;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        String str = "i am narahari my name is narahari i am from warangal ,";
        Stream<String> stream= Stream.of(str.split(" "));
        Map<String, Long> result =stream.collect(Collectors.groupingBy(o -> o,Collectors.counting()));
        Set set = result.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toSet());
        System.out.println(set);

        int arr[]={52,89,45,78,35};

    }
}
