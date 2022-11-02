package com.stormeye.event.api.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.audit.service.EventAuditService;
import com.stormeye.event.utils.MongoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EventResourceTest {

    private static final String MAIN_EVENTS_JSON = "/kafka-data/kafka-events-main.json";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private EventResource eventResource;
    @Autowired
    private EventAuditService eventAuditService;
    private MockMvc mockMvc;
    @Autowired
    private MongoOperations mongoOperations;
    private JsonNode jsonNode;

    @BeforeEach
    void setUp() {
        MongoUtils.deleteAllDocuments(mongoOperations);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        this.mockMvc.getDispatcherServlet().setThrowExceptionIfNoHandlerFound(true);
    }

    @Test
    void configuration() {
        assertThat(context, is(notNullValue()));
        assertThat(eventResource, is(notNullValue()));
        assertThat(mockMvc, is(notNullValue()));
    }


    @Test
    void testNotFound() throws Exception {

        mockMvc.perform(get("/events/invalid"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("invalid path: expected '/events/main', '/events/deploys' or '/events/sigs'"));
    }

    @Test
    void testGetMainEvents() throws Exception {

        var jsonIn = EventResourceTest.class.getResourceAsStream(MAIN_EVENTS_JSON);
        jsonNode = new ObjectMapper().readTree(jsonIn);
        loadAllData();

        // Obtain the number of
        long count = mongoOperations.count(new Query(), "main");

        // obtain all of the
        var body = mockMvc.perform(get("/events/main")
                        .param("max_events", Long.toString(count))
                        .param("start_from", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(header().string(HttpHeaders.TRANSFER_ENCODING, is("chunked")))
                .andExpect(header().doesNotExist("Keep-Alive"))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, is("no-cache")))
                .andReturn().getResponse().getContentAsString();

        assertThat(body, startsWith("data:{\"ApiVersion\":"));
        assertThat(body, endsWith("id:66908335\n\n"));


        body = mockMvc.perform(get("/events/main").param("max_events", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(header().string(HttpHeaders.TRANSFER_ENCODING, is("chunked")))
                .andExpect(header().doesNotExist("Keep-Alive"))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, is("no-cache")))
                .andReturn().getResponse().getContentAsString();

        // assertThat(body, startsWith("data:\"ApiVersion\":"));

        assertThat(body, endsWith("\n:\n\n:\n"));
    }

    private void loadAllData() {
        jsonNode.forEach(child -> eventAuditService.save(child.toPrettyString()));
    }
}
