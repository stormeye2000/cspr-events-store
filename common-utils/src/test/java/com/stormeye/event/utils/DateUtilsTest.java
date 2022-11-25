package com.stormeye.event.utils;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Unit tests for the DateUtils class
 *
 * @author ian@meywood.com
 */
class DateUtilsTest {

    @SuppressWarnings("deprecation")
    @Test
    void fromIso8601AndToIso8601() {

        // Ensure using UTC timezone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        String isoDate = "2022-11-08T13:43:09.568Z";
        Date date = DateUtils.fromIso8601(isoDate);
        assertThat(date, is(notNullValue()));

        assertThat(date.getYear(), is(2022 - 1900));
        assertThat(date.getMonth(), is(11 - 1));
        assertThat(date.getDate(), is(8));
        assertThat(date.getHours(), is(13));
        assertThat(date.getMinutes(), is(43));
        assertThat(date.getSeconds(), is(9));

        assertThat(DateUtils.toIso8601(date), is(isoDate));
    }
}
