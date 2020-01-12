package com.nis;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.nis.server.SNMPTrapServer;


/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring.xml")*/
public class ConfagentMain {
	private static Logger logger = LoggerFactory.getLogger(ConfagentMain.class);
	public static void main(String[] args) {
	
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
		/*  ApplicationContext ctx = new FileSystemXmlApplicationContext(
					"file:" + System.getProperty("confagent.dir")
							+ File.separator+ "spring.xml");*/
		
	     logger.info("initContext successfully");
	}
}
