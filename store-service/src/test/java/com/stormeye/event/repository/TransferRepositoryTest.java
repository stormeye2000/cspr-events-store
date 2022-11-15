package com.stormeye.event.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Transfer;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TransferRepositoryTest {
    @Autowired
    private TransferRepository transferRepository;

    @BeforeEach
    void setUp() {
        transferRepository.deleteAll();
    }

    @Test
    void save(){

        var timestamp = new Date();

        final Transfer transfer = Transfer.builder()
                .blockHash(new Digest("5ae463abe56ebd37044600b90236d91fa93e3ff88d47f12a9c616d8b16ae9100"))
                .deployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"))
                .transferId(new BigInteger("1"))
                .transferHash(new Digest("transfer-bae0cec10eb82aa81af18a31393ff5d5023e25ea7ed820e978b8407ccc22160d".substring(9)))
                .amount(new BigInteger("2500000000"))
                .fromAccount(new Digest("account-hash-59cbc880e6d1f7407f18c36393c33d47ae51d5a54258f94a837ff996bf25a34d".substring(13)))
                .toAccount(new Digest("account-hash-a6cdb6f049363f6ab119be0c961c36e4a3c09319589341dd861f405d9836fc67".substring(13)))
                .targetPurse("uref-05f54a84872c75f7f05c8e8aaf9338ec848fa1a5b4f07202e371955c982f7f60-004")
                .sourcePurse("uref-0eeb2bd99ae07173be21a5fc86db7a2ea7fae0abdfb5e81350bf52f22a66ea80-007")
                .timestamp(timestamp)
                .build();

        final Transfer saved = transferRepository.save(transfer);
        assertThat(saved.getId(), is(greaterThan(0L)));

        final Optional<Transfer> byId = transferRepository.findById(Objects.requireNonNull(saved.getId()));
        assertThat(byId.isPresent(), is(true));

        final Transfer found = byId.get();

        assertThat(found.getId(), is(saved.getId()));

        assertThat(found.getDeployHash(),is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
        assertThat(found.getTransferId(), is(new BigInteger("1")));
        assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));
        assertThat(found.getBlockHash(), is(new Digest("5ae463abe56ebd37044600b90236d91fa93e3ff88d47f12a9c616d8b16ae9100")));
        assertThat(found.getAmount(), is(new BigInteger("2500000000")));
        assertThat(found.getSourcePurse(), is("uref-0eeb2bd99ae07173be21a5fc86db7a2ea7fae0abdfb5e81350bf52f22a66ea80-007"));
        assertThat(found.getTargetPurse(), is("uref-05f54a84872c75f7f05c8e8aaf9338ec848fa1a5b4f07202e371955c982f7f60-004"));
        assertThat(found.getFromAccount(), is(new Digest("account-hash-59cbc880e6d1f7407f18c36393c33d47ae51d5a54258f94a837ff996bf25a34d".substring(13))));
        assertThat(found.getToAccount(), is(new Digest("account-hash-a6cdb6f049363f6ab119be0c961c36e4a3c09319589341dd861f405d9836fc67".substring(13))));
        assertThat(found.getTransferHash(), is(new Digest("transfer-bae0cec10eb82aa81af18a31393ff5d5023e25ea7ed820e978b8407ccc22160d".substring(9))));

    }

    @Test
    void findByDeployHash(){

        var timestamp = new Date();

        final Transfer transfer = Transfer.builder()
                .blockHash(new Digest("5ae463abe56ebd37044600b90236d91fa93e3ff88d47f12a9c616d8b16ae9100"))
                .deployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"))
                .transferId(new BigInteger("1"))
                .transferHash(new Digest("transfer-bae0cec10eb82aa81af18a31393ff5d5023e25ea7ed820e978b8407ccc22160d".substring(9)))
                .amount(new BigInteger("2500000000"))
                .fromAccount(new Digest("account-hash-59cbc880e6d1f7407f18c36393c33d47ae51d5a54258f94a837ff996bf25a34d".substring(13)))
                .toAccount(new Digest("account-hash-a6cdb6f049363f6ab119be0c961c36e4a3c09319589341dd861f405d9836fc67".substring(13)))
                .targetPurse("uref-05f54a84872c75f7f05c8e8aaf9338ec848fa1a5b4f07202e371955c982f7f60-004")
                .sourcePurse("uref-0eeb2bd99ae07173be21a5fc86db7a2ea7fae0abdfb5e81350bf52f22a66ea80-007")
                .timestamp(timestamp)
                .build();

        final Transfer saved = transferRepository.save(transfer);
        assertThat(saved.getId(), is(greaterThan(0L)));

        final Optional<Transfer> byId = transferRepository.findById(Objects.requireNonNull(saved.getId()));
        assertThat(byId.isPresent(), is(true));

        final Optional<Transfer> byDeployHashAndBlockHash = transferRepository.findByDeployHash(
                new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")
        );

        assertThat(byDeployHashAndBlockHash.isPresent(), is(true));
        final Transfer found = byDeployHashAndBlockHash.get();

        assertThat(found.getId(), is(saved.getId()));

        assertThat(found.getDeployHash(),is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
        assertThat(found.getTransferId(), is(new BigInteger("1")));
        assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));
        assertThat(found.getBlockHash(), is(new Digest("5ae463abe56ebd37044600b90236d91fa93e3ff88d47f12a9c616d8b16ae9100")));
        assertThat(found.getAmount(), is(new BigInteger("2500000000")));
        assertThat(found.getSourcePurse(), is("uref-0eeb2bd99ae07173be21a5fc86db7a2ea7fae0abdfb5e81350bf52f22a66ea80-007"));
        assertThat(found.getTargetPurse(), is("uref-05f54a84872c75f7f05c8e8aaf9338ec848fa1a5b4f07202e371955c982f7f60-004"));
        assertThat(found.getFromAccount(), is(new Digest("account-hash-59cbc880e6d1f7407f18c36393c33d47ae51d5a54258f94a837ff996bf25a34d".substring(13))));
        assertThat(found.getToAccount(), is(new Digest("account-hash-a6cdb6f049363f6ab119be0c961c36e4a3c09319589341dd861f405d9836fc67".substring(13))));
        assertThat(found.getTransferHash(), is(new Digest("transfer-bae0cec10eb82aa81af18a31393ff5d5023e25ea7ed820e978b8407ccc22160d".substring(9))));

    }



}
