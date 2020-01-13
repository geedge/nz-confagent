package com.nis;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.nis.server.SNMPTrapServer;


/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring.xml")*/
public class ConfagentMain {
	private static Logger logger = LoggerFactory.getLogger(ConfagentMain.class);
	public static void main(String[] args) {
		
		// 如果放到linux环境 打包下需要注释掉  同时解开下面注释内容 进行打包  
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
		
		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.properties");
		
		// 如果放到linux环境 打包下需要解开注释  
		/*  ApplicationContext ctx = new FileSystemXmlApplicationContext(
					"file:" + System.getProperty("confagent.dir")
							+ File.separator+ "spring.xml");*/
		
	     logger.info("initContext successfully");
	}
}
