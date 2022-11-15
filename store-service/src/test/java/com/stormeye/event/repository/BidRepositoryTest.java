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
import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.Bid;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class BidRepositoryTest {

    @Autowired
    private BidRepository bidRepository;

    @BeforeEach
    void setUp() {
        bidRepository.deleteAll();
    }

    @Test
    void save() throws NoSuchAlgorithmException {

        var timestamp = new Date();

        final Bid bid = Bid.builder()
                .bidKey("bid-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63")
                .bondingPurse("uref-ec2c8b244abb16efd48fcb25740863bfd305ed4b8be1ee9466fff998b93e3a9c-007")
                .delegationRate(5)
                .delegators("json string")
                .validatorPublicKey(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45"))
                .deployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"))
                .stakedAmount(new BigInteger("12005548678314"))
                .inactive(false)
                .timestamp(timestamp)
                .vestingSchedule("json string")
                .build();

        final Bid saved = bidRepository.save(bid);
        assertThat(saved.getId(), is(greaterThan(0L)));

        final Optional<Bid> byId = bidRepository.findById(Objects.requireNonNull(saved.getId()));
        assertThat(byId.isPresent(), is(true));

        final Bid found = byId.get();

        assertThat(found.getId(), is(saved.getId()));
        assertThat(found.getBidKey(), is("bid-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63"));
        assertThat(found.getBondingPurse(), is("uref-ec2c8b244abb16efd48fcb25740863bfd305ed4b8be1ee9466fff998b93e3a9c-007"));
        assertThat(found.getStakedAmount(), is(new BigInteger("12005548678314")));
        assertThat(found.getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
        assertThat(found.getDelegators(), is("json string"));
        assertThat(found.getVestingSchedule(), is("json string"));
        assertThat(found.getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45")));
        assertThat(found.isInactive(), is(false));
        assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));

    }

    @Test
    void findByDeployHash() throws NoSuchAlgorithmException {

        var timestamp = new Date();

        final Bid bid = Bid.builder()
                .bidKey("bid-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63")
                .bondingPurse("uref-ec2c8b244abb16efd48fcb25740863bfd305ed4b8be1ee9466fff998b93e3a9c-007")
                .delegationRate(5)
                .delegators("json string")
                .validatorPublicKey(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45"))
                .deployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"))
                .stakedAmount(new BigInteger("12005548678314"))
                .inactive(false)
                .timestamp(timestamp)
                .vestingSchedule("json string")
                .build();

        final Bid saved = bidRepository.save(bid);
        assertThat(saved.getId(), is(greaterThan(0L)));

        final Optional<Bid> byId = bidRepository.findById(Objects.requireNonNull(saved.getId()));
        assertThat(byId.isPresent(), is(true));

        final List<Bid> byFindByDeployHash = bidRepository.findByDeployHash(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087"));
        assertThat(byFindByDeployHash.isEmpty(), is(false));

        final List<Bid> found = byFindByDeployHash;

        assertThat(found.size(), is(1));

        assertThat(found.get(0).getId(), is(saved.getId()));
        assertThat(found.get(0).getBidKey(), is("bid-080ef8dd1d2479776d9058cd08d5df91e37980b89124b4878ff79bb0f0c32e63"));
        assertThat(found.get(0).getBondingPurse(), is("uref-ec2c8b244abb16efd48fcb25740863bfd305ed4b8be1ee9466fff998b93e3a9c-007"));
        assertThat(found.get(0).getStakedAmount(), is(new BigInteger("12005548678314")));
        assertThat(found.get(0).getDeployHash(), is(new Digest("fb81219f33aa58a2c2f50f7eea20c3065963f61bc3c74810729f10dc21981087")));
        assertThat(found.get(0).getDelegators(), is("json string"));
        assertThat(found.get(0).getVestingSchedule(), is("json string"));
        assertThat(found.get(0).getValidatorPublicKey(), is(PublicKey.fromTaggedHexString("0138e64f04c03346e94471e340ca7b94ba3581e5697f4d1e59f5a31c0da720de45")));
        assertThat(found.get(0).isInactive(), is(false));
        assertThat(found.get(0).getTimestamp().getTime(), is(timestamp.getTime()));

    }


}
