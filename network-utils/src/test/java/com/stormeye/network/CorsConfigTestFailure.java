package com.stormeye.network;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;

@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.yml"})
public class CorsConfigTestFailure {

    @Autowired
    private WebApplicationContext context;

    public MockMvc mockMvc;


    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        this.mockMvc.getDispatcherServlet().setThrowExceptionIfNoHandlerFound(true);
    }

    @Test
    void testCorsConfig() throws Exception {

        final MvcResult result = this.mockMvc
                .perform(get("/test-cors"))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        assertThat(result.getResponse().getContentAsString(), is("no cors"));

        final Collection<String> headerNames = result.getResponse().getHeaderNames();

        assertThat(headerNames, not(hasItem("Vary")));

    }

    @SpringBootConfiguration
    @Controller
    static class SpringApp {

        public static void main(String[] args) {
            SpringApplication.run(SpringApp.class, args);
        }

        @RequestMapping(value = {"test-cors"},  method = RequestMethod.GET)
        @ResponseStatus(HttpStatus.OK)
        public @ResponseBody String testCors() {
            return "no cors";
        }
    }


}
