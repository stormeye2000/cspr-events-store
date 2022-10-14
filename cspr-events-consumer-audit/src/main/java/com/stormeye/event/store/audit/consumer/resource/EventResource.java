package com.stormeye.event.store.audit.consumer.resource;

import com.stormeye.event.store.audit.consumer.execption.AuditConsumerException;
import com.stormeye.event.store.audit.consumer.execption.MaxEventsException;
import com.stormeye.event.store.audit.consumer.service.EventAuditService;
import com.stormeye.event.store.audit.consumer.service.EventReplayService;
import com.casper.sdk.model.event.EventType;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;

/**
 * The events REST APIs that match those of a cspr node event APIs. These APIs also allow for filtering of the streams
 * via the use or a query parameter eg: <a href="http://localhost:8080/events/main?query=">...</a>"data.DeployProcessed.deploy_hash:2189c51773cf25d566c855ddd165418dace85bc61c40cff6270716c675787084,dataType:BlockAdded"
 *
 * @author ian@meywood.com
 */
@RestController
@RequestMapping("/events")
@OpenAPIDefinition(
        info = @Info(
                title = "Casper Event Audit REST API",
                description = "The events REST APIs that match those of a cspr node event APIs. These APIs also allow for filtering of the streams\n" +
                        " * via the use or a query parameter eg: http://localhost:8080/events/main?query=\"data.DeployProcessed.deploy_hash:2189c51773cf25d566c855ddd165418dace85bc61c40cff6270716c675787084,dataType:BlockAdded\"" +
                        "For more information see  <a href='https://docs.casperlabs.io/dapp-dev-guide/building-dapps/monitoring-events/'><i>Monitoring and Consuming Events<i/></a>",
                contact = @Contact(
                        name = "Stormeye2000",
                        url = "https://github.com/stormeye2000/cspr-producer-audit"
                )
        )
)
public class EventResource {

    private final Logger logger = LoggerFactory.getLogger(EventResource.class);
    private final EventAuditService eventAuditService;
    private final EventReplayService eventReplayService;

    public EventResource(final EventAuditService eventAuditService, final EventReplayService eventReplayService) {
        this.eventAuditService = eventAuditService;
        this.eventReplayService = eventReplayService;
    }

    /**
     * Obtains deploy events if no events are currently in the database will provide lines of ':' every 10 seconds
     * until data is available.
     *
     * @param startFrom the optional start from value that is the ID of the 1st event to obtain
     * @param maxEvents the optional max events to limit reading to the max will stop streaming once met
     * @param query     the optional comma separated query parameters from the request that match against mongo keys and values using 'and' eg:
     *                  "data.DeployProcessed.deploy_hash:2189c51773cf25d566c855ddd165418dace85bc61c40cff6270716c675787084,dataType:BlockAdded"
     * @param response  the http response to write to
     */
    @GetMapping(value = "/deploys", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(tags = "deploys", summary = "Obtains the deploy events",
            description = "If no events are currently in the database will provide lines of ':' " +
                    "every 10 seconds until data is available."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "correctly serving the 'delpoys' SSE stream",
                    content = {@Content(mediaType = "text/event-stream")}),
            @ApiResponse(responseCode = "404", description = "event stream not found ",
                    content = @Content)}
    )
    public void getDeployEvents(
            @Parameter(description = "the optional start from value that is the ID of the 1st event to obtain")
            @RequestParam(value = "start_from", required = false) final Long startFrom,

            @Parameter(description = "the optional max events to limit reading to the max will stop streaming once met")
            @RequestParam(value = "max_events", required = false) final Long maxEvents,

            @Parameter(description = "the optional comma separated query parameters from the request that match against mongo keys and values using 'and' eg:\n" +
                    "\"data.DeployProcessed.deploy_hash:2189c51773cf25d566c855ddd165418dace85bc61c40cff6270716c675787084,dataType:BlockAdded\"")
            @RequestParam(value = "query", required = false) final String query,
            final HttpServletResponse response) {

        logger.info("getDeployEvents start_from={}, max_events {}", startFrom, maxEvents);
        streamEvents(EventType.DEPLOYS, startFrom, maxEvents, query, response);
    }


    /**
     * Obtains main events if no events are currently in the database will provide lines of ':' every 10 seconds
     * until data is available.
     *
     * @param startFrom the optional start from value that is the ID of the 1st event to obtain
     * @param maxEvents the optional max events to limit reading to the max will stop streaming once met
     * @param query     the comma separated query parameters from the request that match against mongo keys and values using 'and' eg:
     *                  "data.DeployProcessed.deploy_hash:2189c51773cf25d566c855ddd165418dace85bc61c40cff6270716c675787084,dataType:BlockAdded"
     * @param response  the http response to write to
     */
    @GetMapping(value = "/main", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(tags = "main", summary = "Obtains the main events",
            description = "If no events are currently in the database will provide lines of ':' " +
                    "every 10 seconds until data is available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "correctly serving the 'events' SSE stream",
                    content = {@Content(mediaType = "text/event-stream")}),
            @ApiResponse(responseCode = "404", description = "event stream not found ",
                    content = @Content)}
    )
    public void getMainEvents(@Parameter(description = "the optional start from value that is the ID of the 1st event to obtain")
                              @RequestParam(value = "start_from", required = false) final Long startFrom,

                              @Parameter(description = "the optional max events to limit reading to the max will stop streaming once met")
                              @RequestParam(value = "max_events", required = false) final Long maxEvents,

                              @Parameter(description = "the optional comma separated query parameters from the request that match against mongo keys and values using 'and' eg:\n" +
                                      "\"data.DeployProcessed.deploy_hash:2189c51773cf25d566c855ddd165418dace85bc61c40cff6270716c675787084,dataType:BlockAdded\"")
                              @RequestParam(value = "query", required = false) final String query,
                              final HttpServletResponse response) {

        logger.info("getMainEvents start_from={}, max_events {}", startFrom, maxEvents);

        streamEvents(EventType.MAIN, startFrom, maxEvents, query, response);
    }

    /**
     * Obtains signature events if no events are currently in the database will provide lines of ':' every 10 seconds
     * until data is available.
     *
     * @param startFrom the optional start from value that is the ID of the 1st event to obtain
     * @param maxEvents the optional max events to limit reading to the max will stop streaming once met
     * @param query     the comma separated query parameters from the request that match against mongo keys and values using 'and' eg:
     *                  "data.DeployProcessed.deploy_hash:2189c51773cf25d566c855ddd165418dace85bc61c40cff6270716c675787084,dataType:BlockAdded"
     * @param response  the http response to write to
     */
    @GetMapping(value = "/sigs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(tags = "sigs", summary = "Obtains the signature events",
            description = "if no events are currently in the database will provide lines of ':' " +
                    "every 10 seconds until data is available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "correctly serving the 'sigs' SSE stream",
                    content = {@Content(mediaType = "text/event-stream")}),
            @ApiResponse(responseCode = "404", description = "event stream not found ",
                    content = @Content)}
    )
    public void getSigEvents(@Parameter(description = "the optional start from value that is the ID of the 1st event to obtain")
                             @RequestParam(value = "start_from", required = false) final Long startFrom,

                             @Parameter(description = "the optional max events to limit reading to the max will stop streaming once met")
                             @RequestParam(value = "max_events", required = false) final Long maxEvents,

                             @Parameter(description = "the optional comma separated query parameters from the request that match against mongo keys and values using 'and' eg:\n" +
                                     "\"data.DeployProcessed.deploy_hash:2189c51773cf25d566c855ddd165418dace85bc61c40cff6270716c675787084,dataType:BlockAdded\"")
                             @RequestParam(value = "query", required = false) final String query,
                             final HttpServletResponse response) {

        logger.info("getSigEvents start_from={}, max_events {}", startFrom, maxEvents);

        streamEvents(EventType.SIGS, startFrom, maxEvents, query, response);
    }

    private Map<String, String> buildQueryMap(String query) {

        final Map<String, String> queryMap = new HashMap<>();


        if (query != null) {
            Arrays.stream(query.split(",")).forEach(criteria -> {
                var keyValue = criteria.split(":");
                if (keyValue.length == 2) {
                    queryMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            });
        }

        return queryMap;
    }


    private void streamEvents(final EventType eventType,
                              final Long startFrom,
                              final Long maxEvents,
                              final String query,
                              final HttpServletResponse response) {

        final Map<String, String> queryMap = buildQueryMap(query);

        // if no start_from is provided stream from the last event
        final long currentEventId = Objects.requireNonNullElseGet(startFrom, () -> eventAuditService.getLastEventId(EventType.MAIN).orElse(0L));
        // If max_events is not specified default to -1 (unlimited)
        final long max = Objects.requireNonNullElse(maxEvents, -1L);

        response.setStatus(HttpStatus.OK.value());
        response.setHeader(HttpHeaders.TRANSFER_ENCODING, "chunked");
        response.setHeader(CACHE_CONTROL, CacheControl.noCache().getHeaderValue());
        response.setContentType(MediaType.TEXT_EVENT_STREAM.toString());

        final ServletOutputStream outputStream;
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            throw new AuditConsumerException(e);
        }

        try (final Stream<String> stream = eventReplayService.replayAsStream(eventType, currentEventId, max, queryMap)) {
            stream.forEach(line -> writeLine(outputStream, line));
        } catch (MaxEventsException e) {
            logger.debug("Max events of {} reached", max);
        }
    }

    private void writeLine(final OutputStream outputStream, final String line) {
        try {
            IOUtils.write(line, outputStream, StandardCharsets.UTF_8);
            // Ensure each line is flushed to it appears immediately on the requesting clients
            outputStream.flush();
        } catch (IOException e) {
            throw new AuditConsumerException(e);
        }
    }
}
