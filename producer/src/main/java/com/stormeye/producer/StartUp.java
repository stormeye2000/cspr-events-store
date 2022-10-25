package com.stormeye.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.stormeye.producer.config.BrokerState;
import com.stormeye.producer.service.producer.ProducerService;

/**
 * Starts the application via the ProducerService one all beans have been configured
 */
@Component
class StartUp implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(StartUp.class.getName());

    private final ProducerService service;
    private final BrokerState brokerState;

    private StartUp(final ProducerService service, final BrokerState brokerState) {
        this.service = service;
        this.brokerState = brokerState;
    }

    @Override
    public void run(final ApplicationArguments args) {
        if (brokerState.isAvailable()){
            service.startEventConsumers();
        } else {
            logger.error("No kafka broker available.");
        }
    }

}
