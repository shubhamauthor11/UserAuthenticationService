package com.shubham.userauthenticationservice;

import org.apache.logging.log4j.util.Chars;

public class Test {

    public static void main(String[] args){

        String str = "?gad?bcc?dg?";
        int k =1;

        int n = str.length();

        char[] chars = str.toCharArray();

        for(int i =0; i<n/2; i++){
            int j = n-i-1;
            if(chars[i] == '?' && chars[j] == '?'){
                chars[i] = chars[j] = 'a';
            }else if(chars[i] == '?'){
                chars[i] = chars[j];
            }else if(chars[j] == '?'){
                chars[j] = chars[i];
            }
            else if(chars[i] != chars[j]){
//                System.out.println("chars[i] : " + chars[i] + " and chars[j]" + chars[j]);
                System.out.println("NO");
            }
        }

        if(n%2 == 1 && chars[n/2] == '?'){
            chars[n/2] = 'a';
        }

        int count = 0;

        for(int i =0; i<n/2; i++){
            int j = n-i-1;
            if(chars[i] != chars[j]){
                count++;
            }
        }

        if(count > k){
            System.out.println("NOt possible");
        }

        System.out.println(new String(chars));
    }
}
