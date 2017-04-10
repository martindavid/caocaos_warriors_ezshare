package com.ezshare;

public class javaTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s="S";
		String j="";
		
		String x ="a*";
		String g="aaaa\0bbb";
		
		if (s != null && !s.isEmpty()){
			System.out.println(1);
		}
		if (j == null || j.isEmpty()){
			System.out.println(2);
		}
		if(g.contains("\0")){
			System.out.println(3);
		}
		

	}

}
