package com.dtbuilder.config;

import com.dtbuilder.processor.DependencyFileProcessor;
import com.dtbuilder.processor.DependencyProcessor;
import com.dtbuilder.processor.POMFileProcessor;
import com.dtbuilder.repository.RepositoryMaster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class.
 * Contains the beans used by the application
 */
@Configuration
public class DependencyBuilderConfig {

    /**
     *
     * @return Map which maps a processor class to an input file type (POM/Gradle build file/Ivy file)
     */
    @Bean
    public Map getProcessors(){
        Map<String, DependencyFileProcessor> map = new HashMap<String, DependencyFileProcessor>();
        map.put(Constants.POM, getPOMFileProcessor());
        return map;
    }

    /**
     *
     * @return bean representing a POM file processor
     */
    @Bean(name = Constants.POM)
    public DependencyFileProcessor getPOMFileProcessor(){
        DependencyFileProcessor processor = new POMFileProcessor();

        return processor;
    }

    @Bean(name = "processor")
    public DependencyProcessor getProcessor(){
        DependencyProcessor processor = new DependencyProcessor();
        return processor;
    }

    @Bean
    public RepositoryMaster getRepositoryMaster(){
        RepositoryMaster repositoryMaster = new RepositoryMaster();
        return repositoryMaster;
    }

}
