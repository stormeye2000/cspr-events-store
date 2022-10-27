package com.stormeye.producer.json;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.jupiter.api.Test;
import com.casper.sdk.model.event.Event;
import com.casper.sdk.model.event.EventTarget;
import com.casper.sdk.model.event.EventType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @author ian@meywood.com
 */
class CsprEventSerializerTest {

    private static final String API_TXT = "data:{\"ApiVersion\":\"1.0.0\"}";

    private static final String DATA_TXT = "data:{\"BlockAdded\":{\"block_hash\":" +
            "\"bb878bcf8827649f070c487800a95c35be3eb2e83b5447921675040cea38af1c\",\"block\":{\"hash\":" +
            "\"bb878bcf8827649f070c487800a95c35be3eb2e83b5447921675040cea38af1c\",\"header\":{\"parent_hash\":" +
            "\"0000000000000000000000000000000000000000000000000000000000000000\",\"state_root_hash\":" +
            "\"fbd89036ca934a53b14ebc99abcf64351008ac073848c0b384771036121a25cc\",\"body_hash\":" +
            "\"5187b7a8021bf4f2c004ea3a54cfece1754f11c7624d2363c7f4cf4fddd1441e\",\"random_bit\":false," +
            "\"accumulated_seed\":\"d8908c165dee785924e7421a0fd0418a19d5daeec395fd505a92a0fd3117e428\",\"era_end\"" +
            ":{\"era_report\":{\"equivocators\":[],\"rewards\":[],\"inactive_validators\":[]}," +
            "\"next_era_validator_weights\":[{\"validator\":" +
            "\"010d23984fefcce099679a24496f1d3072a540b95d321f8ba951df0cfe2c0691e5\",\"weight\":\"2000000000000004\"}" +
            ",{\"validator\":\"011213e00a3bd748278b38a00a4787a7143f28a9d564126566716a53daa9499852\",\"weight\":" +
            "\"2000000000000006\"},{\"validator\":\"0180b99ded1d271c61a26d1b18c289ab33fc64355fa90cda4ae18f91786aa6ba4b\"," +
            "\"weight\":\"2000000000000010\"},{\"validator\":\"01959d01aa68197e8cb91aa06bcc920f8d4a245dff60ea726bb89255349107a565\"," +
            "\"weight\":\"2000000000000002\"},{\"validator\":\"01fcf1392c59c7d89190bfcd1b00902cc0801700eab98034aa4e56816d338f6c25\"," +
            "\"weight\":\"2000000000000008\"}]},\"timestamp\":\"2022-07-22T16:56:37.891Z\",\"era_id\":0,\"height\":0," +
            "\"protocol_version\":\"1.0.0\"},\"body\":{\"proposer\":\"00\",\"deploy_hashes\":[],\"transfer_hashes\":[]},\"proofs\":[]}}}";
    private static final String ID_TXT = "id:2";

    @Test
    void buildKafkaEvent() throws Exception {

        final URI source = new URI("http://localhost:9999");

        final Event<String> event = buildEvent(source);

        //noinspection resource
        final String kafkaEvent = new String(new CsprEventSerializer().serialize("main", event), StandardCharsets.UTF_8);
        assertThat(kafkaEvent, is(notNullValue()));

        assertThat(kafkaEvent, hasJsonPath("$.id", is(2)));
        assertThat(kafkaEvent, hasJsonPath("$.source", is(source.toString())));
        assertThat(kafkaEvent, hasJsonPath("$.type", is("main")));
        assertThat(kafkaEvent, hasJsonPath("$.dataType", is("BlockAdded")));
        assertThat(kafkaEvent, hasJsonPath("$.version", is("1.0.0")));
        assertThat(kafkaEvent, hasJsonPath("$.data"));
        assertThat(kafkaEvent, hasJsonPath("$.data.BlockAdded"));
        assertThat(kafkaEvent, hasJsonPath("$.data.BlockAdded.block_hash", is("bb878bcf8827649f070c487800a95c35be3eb2e83b5447921675040cea38af1c")));
    }

    /**
     * The SDK event builder is package private so use reflection to create and invoke it
     *
     * @param source the source URI of a casper node event stream
     * @return a test event
     */
    private static Event<String> buildEvent(final URI source) throws Exception {

        Class<?> eventBuildClass = Class.forName("com.casper.sdk.service.impl.event.EventBuilder");
        Constructor<?> ctor = eventBuildClass.getDeclaredConstructor(EventType.class, EventTarget.class, String.class);
        ctor.setAccessible(true);
        Object eventBuilder = ctor.newInstance(EventType.MAIN, EventTarget.RAW, source.toString());

        Method processLine = eventBuildClass.getDeclaredMethod("processLine", String.class);
        processLine.setAccessible(true);

        processLine.invoke(eventBuilder, API_TXT);
        processLine.invoke(eventBuilder, DATA_TXT);
        processLine.invoke(eventBuilder, ID_TXT);

        Method buildEvent = eventBuildClass.getDeclaredMethod("buildEvent");
        buildEvent.setAccessible(true);

        //noinspection unchecked
        return (Event<String>) buildEvent.invoke(eventBuilder);
    }
}