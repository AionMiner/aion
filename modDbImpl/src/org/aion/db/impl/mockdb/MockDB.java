package org.aion.db.impl.mockdb;

import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aion.db.impl.AbstractDB;
import org.aion.db.impl.PersistenceMethod;
import org.aion.util.types.ByteArrayWrapper;
import org.slf4j.Logger;

public class MockDB extends AbstractDB {

    protected Map<ByteArrayWrapper, byte[]> kv;
    private Map<ByteArrayWrapper, byte[]> writeBatch = new HashMap<>();

    public MockDB(String name, Logger log) {
        super(name, log);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":<name=" + name + ">";
    }

    @Override
    public boolean open() {
        if (isOpen()) {
            return true;
        }

        LOG.debug("init database {}", this.toString());

        // using a regular map since synchronization is handled through the read-write lock
        kv = new HashMap<>();

        return isOpen();
    }

    @Override
    public void close() {
        // release resources if needed
        if (kv != null) {
            LOG.info("Closing database " + this.toString());

            kv.clear();
        }

        // set map to null
        kv = null;
    }

    @Override
    public boolean isOpen() {
        return kv != null;
    }

    @Override
    public PersistenceMethod getPersistenceMethod() {
        // MockDB doesn't persist anything to disk, so it's type is IN_MEMORY
        return PersistenceMethod.IN_MEMORY;
    }

    @Override
    public boolean isCreatedOnDisk() {
        return false;
    }

    @Override
    public long approximateSize() {
        check();
        return 32L * kv.size() + kv.values().stream().map(k -> (long) k.length).reduce(0L, Long::sum);
    }

    @Override
    public boolean isEmpty() {
        check();
        return kv.isEmpty();
    }

    /**
     * Used for direct updates of the database during unit tests.
     */
    @VisibleForTesting
    public void putAndCommit(byte[] key, byte[] value) {
        kv.put(ByteArrayWrapper.wrap(key), value);
    }

    /**
     * Used for direct updates of the database during unit tests.
     */
    @VisibleForTesting
    public void deleteAndCommit(byte[] key) {
        kv.remove(ByteArrayWrapper.wrap(key));
    }

    @Override
    public Iterator<byte[]> keys() {
        Set<byte[]> set = new HashSet<>();

        check();

        kv.keySet().forEach(key -> set.add(key.toBytes()));

        // empty when retrieval failed
        return set.iterator();
    }

    @Override
    protected byte[] getInternal(byte[] key) {
        return kv.get(ByteArrayWrapper.wrap(key));
    }

    @Override
    public void putToBatchInternal(byte[] key, byte[] value) {
        writeBatch.put(ByteArrayWrapper.wrap(key), value);
    }

    @Override
    public void deleteInBatchInternal(byte[] key) {
        writeBatch.put(ByteArrayWrapper.wrap(key), null);
    }

    @Override
    public void commit() {
        check();
        if (!writeBatch.isEmpty()) {
            // move to permanent storage
            for (Entry<ByteArrayWrapper, byte[]> e : writeBatch.entrySet()) {
                if (e.getValue() == null) {
                    kv.remove(e.getKey());
                } else {
                    kv.put(e.getKey(), e.getValue());
                }
            }
            // clear current batch
            writeBatch.clear();
        }
    }

    @Override
    public void putBatchInternal(Map<byte[], byte[]> input) {
        try {
            // simply do a put, because setting a kv pair to null is same as delete
            input.forEach(
                    (key, value) -> {
                        if (value == null) {
                            kv.remove(ByteArrayWrapper.wrap(key));
                        } else {
                            kv.put(ByteArrayWrapper.wrap(key), value);
                        }
                    });
        } catch (Exception e) {
            LOG.error(
                    "Unable to execute batch put/update operation on " + this.toString() + ".", e);
        }
    }

    @Override
    public void deleteBatchInternal(Collection<byte[]> keys) {
        try {
            keys.forEach((e) -> kv.remove(ByteArrayWrapper.wrap(e)));
        } catch (Exception e) {
            LOG.error("Unable to execute batch delete operation on " + this.toString() + ".", e);
        }
    }

    @Override
    public void drop() {
        kv.clear();
    }
}
