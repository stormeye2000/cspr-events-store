package com.stormeye.event.api.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class ActuatorTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        this.mockMvc.getDispatcherServlet().setThrowExceptionIfNoHandlerFound(true);
    }

    @Test
    public void testEndpoint() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testActuator() {
        final Health health = new Health.Builder(Status.UP).build();
        assertThat(health, is(notNullValue()));
        assertThat(health.getStatus(), is(Status.UP));
    }

}
