package com.nis;


import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring.xml")*/
public class ConfagentMain {
	private static Logger logger = LoggerFactory.getLogger(ConfagentMain.class);
	
	public static void main(String[] args) {
		 ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
	     System.out.println("initContext successfully");
	     logger.info("initContext successfully");
	}
}
