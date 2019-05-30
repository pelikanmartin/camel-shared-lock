package eu.mpelikan.camel.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Main {
    public static void main(String[] args) throws Exception {

        new ClassPathXmlApplicationContext("classpath:META-INF/spring/camel-context.xml");
        Thread.sleep(100000L);
    }
}
