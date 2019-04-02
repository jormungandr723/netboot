package com.dcis.netboot.until;

public class Trs {
    public static void main(String[] arg){
        String uri="/api/app/getid";
        String[] s=uri.split("/");
        System.out.println(s);
    }
}
