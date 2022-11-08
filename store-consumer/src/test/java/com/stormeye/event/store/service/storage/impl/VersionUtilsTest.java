package com.stormeye.event.store.service.storage.impl;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author ian@meywood.com
 */
class VersionUtilsTest {

    @Test
    void isVersionGreaterOrEqual() {

        assertThat(VersionUtils.isVersionGreaterOrEqual("1.0.0", "1.0.0"), is(true));
        assertThat(VersionUtils.isVersionGreaterOrEqual("1.0.1", "1.0.0"), is(true));
        assertThat(VersionUtils.isVersionGreaterOrEqual("1.1.0", "1.0.1"), is(true));
        assertThat(VersionUtils.isVersionGreaterOrEqual("1.1.1", "1.1.1"), is(true));
        assertThat(VersionUtils.isVersionGreaterOrEqual("1.1.0", "1.1.1"), is(false));
        assertThat(VersionUtils.isVersionGreaterOrEqual("1.0.0", "1.0.1"), is(false));
        assertThat(VersionUtils.isVersionGreaterOrEqual("2.0.0", "1.1.1"), is(true));
        assertThat(VersionUtils.isVersionGreaterOrEqual("0.0.0", "0.2.0"), is(false));
        assertThat(VersionUtils.isVersionGreaterOrEqual("0.0.0", "0.0.1"), is(false));
    }
}