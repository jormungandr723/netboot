package com.dcis.netboot.until;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Trs {
    public static void main(String[] arg){
        List<String> ss=new ArrayList<>();
        for (int i = 0; i <100 ; i++) {
            ss.add(i+"");
        }
        Iterator<String> it = ss.iterator();
        while (it.hasNext()){
            String s=it.next();
            if(s.equals("5")||s.equals("55")){
                it.remove();
            }
        }
        System.out.println(ss.size());
    }
}
