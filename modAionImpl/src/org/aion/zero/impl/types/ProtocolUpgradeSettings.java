package org.aion.zero.impl.types;


import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import org.aion.util.bytes.ByteUtil;

/**
 * The protocol upgrade configure class, including the protocol upgrade block number and the rollback
 * transaction hashes (we fallback the transaction at the 1.6 upgrade).
 *
 * @author Jay Tseng
 */
public final class ProtocolUpgradeSettings {

    public final Properties upgrade;
    public final List<byte[]> rollbackTransactionHash;

    /**
     * The constructor method constructed by the loader class
     * @param upgradeMap the protocol upgrade settings for the kernel version
     * @param rollback the rollback transaction setup (for 1.6 upgrade)
     */
    public ProtocolUpgradeSettings(Properties upgradeMap, List<byte[]> rollback) {
        upgrade = upgradeMap;
        rollbackTransactionHash = rollback;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProtocolUpgradeSettings [\n");

        builder.append("  protocol upgrade block number:\n");
        for (Entry<Object, Object> e : upgrade.entrySet()) {
            builder.append("    ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }

        builder.append("  fallback transaction hash:\n");
        for (byte[] hash : rollbackTransactionHash) {
            builder.append("    ").append(ByteUtil.toHexString(hash)).append("\n");
        }

        // footer
        builder.append("]");
        return builder.toString();
    }
}
