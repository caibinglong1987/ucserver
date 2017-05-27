package com.roamtech.uc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	  public static void main(String[] args)
	  {
	    new ClassPathXmlApplicationContext("spring-config.xml");
	  }
}
