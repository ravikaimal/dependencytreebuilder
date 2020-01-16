package com.dtbuilder.controller;

import com.dtbuilder.DependencyBuilderApplication;
import com.dtbuilder.repository.RepositoryMaster;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,classes = {DependencyBuilderApplication.class})
public class DependencyBuilderControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private RepositoryMaster repositoryMaster;

    @Autowired
    private WebApplicationContext webApplicationContext;
    @org.junit.Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @org.junit.Test
    public void handleInputFile() throws Exception{
        MockMultipartFile file = new MockMultipartFile("file","test-pom.xml","text/plain","test-pom.xml".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload/").file(file).param("type","POM"))
                .andExpect(status().is(200));

    }


    @org.junit.Test
    public void isProcessingComplete() {
        try {
            mockMvc.perform(get("/hasFinished").accept(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @org.junit.Test
    public void getDependencyData() {
    }
}