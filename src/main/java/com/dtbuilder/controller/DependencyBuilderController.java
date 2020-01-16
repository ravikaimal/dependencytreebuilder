package com.dtbuilder.controller;

import com.dtbuilder.artifacts.Artifact;
import com.dtbuilder.config.Constants;
import com.dtbuilder.processor.DependencyFileProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Controller class.
 */
@RestController
public class DependencyBuilderController {

    private static Log logger = LogFactory.getLog(DependencyBuilderController.class);

    /*
        Inject instances of processor classes to process the incoming POM, Gradle or Ivy file.
     */
    @Autowired
    private Map<String, DependencyFileProcessor> processors;
    /*
        This class reads the remote POM file corresponding to the dependency.
     */
    //@Autowired
    //private DependencyProcessor processor;

    private Artifact parent;

    /**
     * This method is responsible for processing the POM/Gradle/Ivy file sent by the client.
     */
    @PostMapping(path = "/upload")
    public void handleInputFile(@RequestParam("type") String type, @RequestParam("file")MultipartFile file){
        logger.info("Entry: handleInputFile");
        if(file == null || file.isEmpty()){
            return;
        }

        if(StringUtils.isEmpty(type)){
            return;
        }

        try {
            byte[] fileBytes = file.getBytes();
            String fileContent = new String(fileBytes);

            if(!processors.containsKey(type))
                return;
            parent = new Artifact();

            processors.get(Constants.POM).processDepedencyData(parent,fileContent);


        } catch (IOException e) {
            logger.error("Error",e);
        }

    }

    /**
     * A quick status check to determine if the file processing is completed.
     * @return boolean
     */
    @GetMapping("/hasFinished")
   // @RequestMapping(value = { "/hasFinished" }, method = { RequestMethod.GET })
    public boolean isProcessingComplete(){
        return false;
        //return processor.getActiveCount() == 0;
    }

    /**
     * If the file processing is completed, return the dependency tree to the client.
     * @return
     */
    @GetMapping("/getDependencyData")
    public String getDependencyData(){
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(parent);
        } catch (JsonProcessingException e) {
            logger.error(e);
        }
        return json;
    }
}
