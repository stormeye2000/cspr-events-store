package com.stormeye.mongo;

/**
 * @author ian@meywood.com
 */
class TestMongoConfig extends AbstractMongoConfig {

    public TestMongoConfig(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") String connectionString) {
        super(connectionString);
    }
}
