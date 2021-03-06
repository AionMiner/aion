package org.aion.precompiled.contracts.ATB;

import static com.google.common.truth.Truth.assertThat;

import org.aion.precompiled.ExternalCapabilitiesForTesting;
import org.aion.precompiled.ExternalStateForTests;
import org.aion.precompiled.type.CapabilitiesProvider;
import org.aion.precompiled.type.IExternalStateForPrecompiled;
import org.aion.precompiled.util.AddressUtils;
import org.aion.precompiled.util.ByteUtil;
import org.aion.types.AionAddress;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BridgeStorageConnectorTest {
    private BridgeStorageConnector connector;
    private static final AionAddress contractAddress = AddressUtils.ZERO_ADDRESS;
    private static ExternalCapabilitiesForTesting capabilities;

    @BeforeClass
    public static void setupCapabilities() {
        capabilities = new ExternalCapabilitiesForTesting();
        CapabilitiesProvider.installExternalCapabilities(capabilities);
    }

    @AfterClass
    public static void teardownCapabilities() {
        CapabilitiesProvider.removeExternalCapabilities();
    }

    @Before
    public void beforeEach() {
        IExternalStateForPrecompiled worldState = ExternalStateForTests.usingDefaultRepository();
        this.connector = new BridgeStorageConnector(worldState, contractAddress);
    }

    // should be null
    @Test
    public void testDefaultOwnerAddress() {
        assertThat(this.connector.getOwner()).isNull();
    }

    @Test
    public void testDefaultNewOwnerAddress() {
        assertThat(this.connector.getNewOwner()).isNull();
    }

    @Test
    public void testDefaultMemberCount() {
        assertThat(this.connector.getMemberCount()).isEqualTo(0);
    }

    @Test
    public void testDefaultMinThresh() {
        assertThat(this.connector.getMinThresh()).isEqualTo(0);
    }

    @Test
    public void testDefaultRingLocked() {
        assertThat(this.connector.getRingLocked()).isFalse();
    }

    @Test
    public void testDefaultMemberMap() {
        byte[] memberKey = capabilities.blake2b("member1".getBytes());
        assertThat(this.connector.getActiveMember(memberKey)).isFalse();
    }

    @Test
    public void testDefaultBundleMap() {
        byte[] bundleKey = capabilities.blake2b("active1".getBytes());
        assertThat(this.connector.getBundle(bundleKey)).isEqualTo(ByteUtil.EMPTY_WORD);
    }

    @Test
    public void testDefaultInitialized() {
        assertThat(this.connector.getInitialized()).isFalse();
    }

    @Test
    public void testNoKeyOverlap() {
        byte[] key = capabilities.blake2b("key".getBytes());
        this.connector.setActiveMember(key, true);
        this.connector.setBundle(key, ByteUtil.EMPTY_WORD);

        assertThat(this.connector.getActiveMember(key)).isTrue();
        assertThat(this.connector.getBundle(key)).isEqualTo(ByteUtil.EMPTY_WORD);

        // test that the reverse is true
        byte[] hash = capabilities.blake2b("hash".getBytes());
        this.connector.setActiveMember(key, false);
        this.connector.setBundle(key, hash);

        assertThat(this.connector.getActiveMember(key)).isFalse();
        assertThat(this.connector.getBundle(key)).isEqualTo(hash);
    }

    @Test
    public void testOwnerAddress() {
        byte[] ownerAddress = capabilities.blake2b("ownerAddress".getBytes());
        this.connector.setOwner(ownerAddress);
        byte[] retrieved = this.connector.getOwner();
        assertThat(retrieved).isEqualTo(ownerAddress);
    }

    @Test
    public void testNewOwnerAddress() {
        byte[] newOwnerAddress = capabilities.blake2b("newOwnerAddress".getBytes());
        this.connector.setNewOwner(newOwnerAddress);
        byte[] retrieved = this.connector.getNewOwner();
        assertThat(retrieved).isEqualTo(newOwnerAddress);
    }

    @Test
    public void testInitialized() {
        this.connector.setInitialized(false);
        assertThat(this.connector.getInitialized()).isFalse();

        this.connector.setInitialized(true);
        assertThat(this.connector.getInitialized()).isTrue();
    }
}
