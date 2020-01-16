package com.dtbuilder;

import com.dtbuilder.processor.DependencyProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DependencyBuilderApplication {
    public static void main(String[] args){
        ApplicationContext context = SpringApplication.run(DependencyBuilderApplication.class,args);
        DependencyProcessor dependencyProcessor = (DependencyProcessor)context.getBean("dependencyProcessor");
        dependencyProcessor.start();
    }
}
