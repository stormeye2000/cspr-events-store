package com.stormeye.event.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Deploy;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DeployRepositoryTest {

    @Autowired
    private DeployRepository deployRepository;

    @BeforeEach
    void setUp() {
        deployRepository.deleteAll();
    }

    @Test
    void saveSuccess() {

        var timestamp = new Date();

        final Deploy deploy = Deploy.builder()
                .account(new Digest("0185244fdb3dffe94cc7ca0af1f6fa12e2d8b99ff749cef1bc5bb8e917dc3dfa88"))
                .blockHash(new Digest("a44dcb1f939e235270b1eea98186672dae9782d575d38589e8ce32fd9c75b807"))
                .cost(new BigInteger("100000000"))
                .deployHash(new Digest("c62363d239e1523ec35609da6ba00db00558331bb18b9e4d595b81ea59379432"))
                .timestamp(timestamp)
                .errorMessage(null)
                .eventId(65028921L)
                .build();

        final Deploy saved = deployRepository.save(deploy);
        assertThat(saved.getId(), is(greaterThan(0L)));

        final Optional<Deploy> byId = deployRepository.findById(Objects.requireNonNull(saved.getId()));
        assertThat(byId.isPresent(), is(true));

        final Deploy found = byId.get();

        assertThat(found.getId(), is(saved.getId()));
        assertThat(found.getBlockHash(), is(new Digest("a44dcb1f939e235270b1eea98186672dae9782d575d38589e8ce32fd9c75b807")));
        assertThat(found.getAccount(), is(new Digest("0185244fdb3dffe94cc7ca0af1f6fa12e2d8b99ff749cef1bc5bb8e917dc3dfa88")));
        assertThat(found.getCost(), is(new BigInteger("100000000")));
        assertThat(found.getDeployHash(), is(new Digest("c62363d239e1523ec35609da6ba00db00558331bb18b9e4d595b81ea59379432")));
        assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));
        assertThat(found.getErrorMessage(), is(Matchers.nullValue()));
        assertThat(found.getEventId(), is(65028921L));
    }

    @Test
    void saveFailure() {

        var timestamp = new Date();

        final Deploy deploy = Deploy.builder()
                .account(new Digest("015c3b6de747e61d6cf9e2ea6593f68f34947277219c3cb5813e79634d300c0a4f"))
                .blockHash(new Digest("497405e6d478a1c1778b1d2cd1547437d41d5b36607221c3c3be05bcef757752"))
                .deployHash(new Digest("c7d0840f2275a18efcd716f425c06691f2ca1a0e6d7d7ecff49cab06c2428ee8"))
                .timestamp(timestamp)
                .errorMessage("ApiError::InvalidArgument [3]")
                .eventId(65028921)
                .build();

        final Deploy saved = deployRepository.save(deploy);
        assertThat(saved.getId(), is(greaterThan(0L)));

        final Optional<Deploy> byId = deployRepository.findById(Objects.requireNonNull(saved.getId()));
        assertThat(byId.isPresent(), is(true));

        final Deploy found = byId.get();

        assertThat(found.getId(), is(saved.getId()));
        assertThat(found.getBlockHash(), is(new Digest("497405e6d478a1c1778b1d2cd1547437d41d5b36607221c3c3be05bcef757752")));
        assertThat(found.getAccount(), is(new Digest("015c3b6de747e61d6cf9e2ea6593f68f34947277219c3cb5813e79634d300c0a4f")));
        assertThat(found.getDeployHash(), is(new Digest("c7d0840f2275a18efcd716f425c06691f2ca1a0e6d7d7ecff49cab06c2428ee8")));
        assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));
        assertThat(found.getEventId(), is(65028921L));
        assertThat(found.getErrorMessage(), is("ApiError::InvalidArgument [3]"));
    }

    @Test
    void findByDeployHashAndEventId(){

        var timestamp = new Date();

        final Deploy deploy = Deploy.builder()
                .account(new Digest("0185244fdb3dffe94cc7ca0af1f6fa12e2d8b99ff749cef1bc5bb8e917dc3dfa88"))
                .blockHash(new Digest("a44dcb1f939e235270b1eea98186672dae9782d575d38589e8ce32fd9c75b807"))
                .cost(new BigInteger("100000000"))
                .deployHash(new Digest("c62363d239e1523ec35609da6ba00db00558331bb18b9e4d595b81ea59379432"))
                .timestamp(timestamp)
                .eventId(65028921L)
                .errorMessage(null)
                .build();

        final Deploy saved = deployRepository.save(deploy);
        assertThat(saved.getId(), is(greaterThan(0L)));

        final Optional<Deploy> byId = deployRepository.findById(Objects.requireNonNull(saved.getId()));
        assertThat(byId.isPresent(), is(true));

        final Deploy found = deployRepository.findByDeployHashAndEventId(
                new Digest("c62363d239e1523ec35609da6ba00db00558331bb18b9e4d595b81ea59379432"),
                65028921L
        );
        assertThat(found, is(Matchers.notNullValue()));

        assertThat(found.getId(), is(saved.getId()));
        assertThat(found.getBlockHash(), is(new Digest("a44dcb1f939e235270b1eea98186672dae9782d575d38589e8ce32fd9c75b807")));
        assertThat(found.getAccount(), is(new Digest("0185244fdb3dffe94cc7ca0af1f6fa12e2d8b99ff749cef1bc5bb8e917dc3dfa88")));
        assertThat(found.getCost(), is(new BigInteger("100000000")));
        assertThat(found.getDeployHash(), is(new Digest("c62363d239e1523ec35609da6ba00db00558331bb18b9e4d595b81ea59379432")));
        assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));
        assertThat(found.getErrorMessage(), is(Matchers.nullValue()));
        assertThat(found.getEventId(), is(65028921L));
    }

    @Test
    void findByDeployHash(){

        var timestamp = new Date();

        final Deploy deploy = Deploy.builder()
                .account(new Digest("0185244fdb3dffe94cc7ca0af1f6fa12e2d8b99ff749cef1bc5bb8e917dc3dfa88"))
                .blockHash(new Digest("a44dcb1f939e235270b1eea98186672dae9782d575d38589e8ce32fd9c75b807"))
                .cost(new BigInteger("100000000"))
                .deployHash(new Digest("c62363d239e1523ec35609da6ba00db00558331bb18b9e4d595b81ea59379432"))
                .timestamp(timestamp)
                .eventId(65028921L)
                .errorMessage(null)
                .build();

        final Deploy saved = deployRepository.save(deploy);
        assertThat(saved.getId(), is(greaterThan(0L)));

        final Optional<Deploy> byId = deployRepository.findById(Objects.requireNonNull(saved.getId()));
        assertThat(byId.isPresent(), is(true));

        final Optional<Deploy> byDeployHash = deployRepository.findByDeployHash(
                new Digest("c62363d239e1523ec35609da6ba00db00558331bb18b9e4d595b81ea59379432")
        );
        assertThat(byDeployHash.isPresent(), is(true));

        final Deploy found = byDeployHash.get();

        assertThat(found.getId(), is(saved.getId()));
        assertThat(found.getBlockHash(), is(new Digest("a44dcb1f939e235270b1eea98186672dae9782d575d38589e8ce32fd9c75b807")));
        assertThat(found.getAccount(), is(new Digest("0185244fdb3dffe94cc7ca0af1f6fa12e2d8b99ff749cef1bc5bb8e917dc3dfa88")));
        assertThat(found.getCost(), is(new BigInteger("100000000")));
        assertThat(found.getDeployHash(), is(new Digest("c62363d239e1523ec35609da6ba00db00558331bb18b9e4d595b81ea59379432")));
        assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));
        assertThat(found.getErrorMessage(), is(Matchers.nullValue()));
        assertThat(found.getEventId(), is(65028921L));
    }
}
