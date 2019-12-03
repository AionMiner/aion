package org.aion.zero.impl.types;

import com.google.common.annotations.VisibleForTesting;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.aion.base.AionTransaction;
import org.aion.mcf.blockchain.BlockHeader;
import org.aion.rlp.RLP;
import org.aion.types.AionAddress;
import org.aion.util.bytes.ByteUtil;
import org.aion.util.conversions.Hex;

/** */
public class AionBlock extends AbstractBlock {

    /* Private */
    private A0BlockHeader header;

    /* Constructors */
    private AionBlock() {}

    // copy constructor
    public AionBlock(AionBlock block) {
        if (block == null) {
            throw  new NullPointerException("Block is null");
        }
        this.header = A0BlockHeader.Builder.newInstance().withHeader(block.getHeader()).build();
        this.transactionsList.addAll(block.getTransactionsList());
    }

    /**
     * Returns a new block that contains zero transactions and whose block header is the empty
     * header that is equal to the header generated by {@link A0BlockHeader.Builder#build()} .
     *
     * @return A new empty block.
     */
    public static AionBlock newEmptyBlock() {
        return new AionBlock(
                A0BlockHeader.Builder.newInstance()
                        .withDefaultParentHash()
                        .withDefaultCoinbase()
                        .withDefaultStateRoot()
                        .withDefaultTxTrieRoot()
                        .withDefaultReceiptTrieRoot()
                        .withDefaultLogsBloom()
                        .withDefaultDifficulty()
                        .withDefaultExtraData()
                        .withDefaultNonce()
                        .withDefaultSolution()
                        .build(),
                Collections.emptyList());
    }

    /**
     * All construction using this codepath leads from DB queries or creation of new blocks
     *
     * @implNote do not use this construction path for unsafe sources
     */
    public AionBlock(A0BlockHeader header, List<AionTransaction> transactionsList) {
        if (header == null) {
            throw new NullPointerException("header is null");
        }

        if (transactionsList == null) {
            throw new NullPointerException("transaction list is null");
        }

        this.header = header;
        this.transactionsList.clear();
        this.transactionsList.addAll(transactionsList);
    }

    @VisibleForTesting
    public AionBlock(
            byte[] parentHash,
            AionAddress coinbase,
            byte[] logsBloom,
            byte[] difficulty,
            long number,
            long timestamp,
            byte[] extraData,
            byte[] nonce,
            byte[] receiptsRoot,
            byte[] transactionsRoot,
            byte[] stateRoot,
            List<AionTransaction> transactionsList,
            byte[] solutions,
            long energyConsumed,
            long energyLimit) {

        A0BlockHeader.Builder builder = A0BlockHeader.Builder.newInstance();

        try {
            builder.withParentHash(parentHash)
                    .withCoinbase(coinbase)
                    .withLogsBloom(logsBloom)
                    .withDifficulty(difficulty)
                    .withNumber(number)
                    .withTimestamp(timestamp)
                    .withExtraData(extraData)
                    .withNonce(nonce)
                    .withReceiptTrieRoot(receiptsRoot)
                    .withTxTrieRoot(transactionsRoot)
                    .withStateRoot(stateRoot)
                    .withSolution(solutions)
                    .withEnergyConsumed(energyConsumed)
                    .withEnergyLimit(energyLimit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.header = builder.build();
        this.transactionsList.clear();
        this.transactionsList.addAll(transactionsList);
    }

    /**
     * Constructor used in genesis creation, note that although genesis does check whether the
     * fields are correct, we emit a checked exception if in some unforseen circumstances we deem
     * the fields as incorrect
     * @see AionGenesis
     */
    protected AionBlock(
            byte[] parentHash,
            AionAddress coinbase,
            byte[] logsBloom,
            byte[] difficulty,
            long number,
            long timestamp,
            byte[] extraData,
            byte[] nonce,
            long energyLimit) {
        if (parentHash == null) {
            throw new NullPointerException("parentHash is null");
        }

        if (coinbase == null) {

            throw new NullPointerException("coinbase is null");
        }

        if (logsBloom == null) {
            throw new NullPointerException("logBloom is null");
        }

        if (difficulty == null) {
            throw new NullPointerException("difficulty is null");
        }

        if (extraData == null) {
            throw new NullPointerException("extraData is null");
        }

        if (nonce == null) {
            throw new NullPointerException("nonce is null");
        }

        A0BlockHeader.Builder builder = A0BlockHeader.Builder.newInstance();
        builder.withParentHash(parentHash)
                .withCoinbase(coinbase)
                .withLogsBloom(logsBloom)
                .withDifficulty(difficulty)
                .withNumber(number)
                .withTimestamp(timestamp)
                .withExtraData(extraData)
                .withNonce(nonce)
                .withEnergyLimit(energyLimit)
                .withDefaultStateRoot()
                .withDefaultTxTrieRoot()
                .withDefaultReceiptTrieRoot()
                .withDefaultSolution();

        this.header = builder.build();
    }

    @Override
    public int size() {
        return getEncoded().length;
    }

    public A0BlockHeader getHeader() {
        return this.header;
    }

    @Override
    public byte[] getHash() {
        return this.header.getHash();
    }

    @Override
    public byte[] getParentHash() {
        return this.header.getParentHash();
    }

    @Override
    public AionAddress getCoinbase() {
        return this.header.getCoinbase();
    }

    @Override
    public byte[] getStateRoot() {
        return this.header.getStateRoot();
    }

    @Override
    public byte[] getTxTrieRoot() {
        return this.header.getTxTrieRoot();
    }

    @Override
    public byte[] getReceiptsRoot() {
        return this.header.getReceiptsRoot();
    }

    @Override
    public byte[] getLogBloom() {
        return this.header.getLogsBloom();
    }

    @Override
    public byte[] getDifficulty() {
        return this.header.getDifficulty();
    }

    @Override
    public BigInteger getDifficultyBI() {
        return this.header.getDifficultyBI();
    }

    @VisibleForTesting
    @Override
    public void updateHeader(BlockHeader _header) {
        if (_header == null) {
            throw new NullPointerException();
        }

        header = (A0BlockHeader) _header;
    }

    @Override
    public long getTimestamp() {
        return this.header.getTimestamp();
    }

    @Override
    public long getNumber() {
        return this.header.getNumber();
    }

    @Override
    public byte[] getExtraData() {
        return this.header.getExtraData();
    }

    public byte[] getNonce() {
        return this.header.getNonce();
    }

    @Override
    public List<AionTransaction> getTransactionsList() {
        return transactionsList;
    }

    /**
     * Facilitates the "finalization" of the block, after processing the necessary transactions.
     * This will be called during block creation and is considered the last step conducted by the
     * blockchain before handing it off to miner. This step is necessary to add post-execution
     * states:
     *
     * <p>{@link A0BlockHeader#txTrieRoot} {@link A0BlockHeader#receiptTrieRoot} {@link
     * A0BlockHeader#stateRoot} {@link A0BlockHeader#logsBloom} {@link
     * this#transactionsList} {@link A0BlockHeader#energyConsumed}
     *
     * <p>The (as of now) unenforced contract by using this function is that the user should not
     * modify any fields set except for {@link A0BlockHeader#solution} and {@link
     * A0BlockHeader#nonce} after this function is called.
     *
     * @param txs list of transactions input to the block (final)
     * @param txTrieRoot the rootHash of the transaction receipt, should correspond with {@code txs}
     * @param stateRoot the root of the world after transactions are executed
     * @param bloom the concatenated blooms of all logs emitted from transactions
     * @param receiptRoot the rootHash of the receipt trie
     * @param energyUsed the amount of energy consumed in the execution of the block
     */
    @SuppressWarnings("JavadocReference")
    public void updateTransactionAndState(
            List<AionTransaction> txs,
            byte[] txTrieRoot,
            byte[] stateRoot,
            byte[] bloom,
            byte[] receiptRoot,
            long energyUsed) {
        if (txs == null) {
            throw new NullPointerException("transaction list is null");
        }

        if (txTrieRoot == null) {
            throw new NullPointerException("txTrieRoot is null");
        }

        if (stateRoot == null) {
            throw new NullPointerException("stateRoot is null");
        }

        if (bloom == null) {
            throw new NullPointerException("bloom data is null");
        }

        if (receiptRoot == null) {
            throw new NullPointerException("receiptRoot is null");
        }

        header =
            A0BlockHeader.Builder.newInstance()
                        .withHeader(header)
                        .withTxTrieRoot(txTrieRoot)
                        .withStateRoot(stateRoot)
                        .withLogsBloom(bloom)
                        .withReceiptTrieRoot(receiptRoot)
                        .withEnergyConsumed(energyUsed)
                        .build();

        this.transactionsList.clear();
        this.transactionsList.addAll(txs);
    }

    @Override
    public String toString() {
        StringBuilder toStringBuff = new StringBuilder();
        toStringBuff.setLength(0);
        toStringBuff.append("BlockData [ ");
        toStringBuff.append("hash=").append(ByteUtil.toHexString(this.getHash())).append("\n");
        toStringBuff.append(header.toString());

        if (totalDifficulty != null) {
            toStringBuff.append(" total difficulty=").append(totalDifficulty).append("\n");
        }

        if (mainChain != null) {
            toStringBuff.append("  mainChain=").append(mainChain ? "yes" : "no").append("\n");
        }

        if (!getTransactionsList().isEmpty()) {
            toStringBuff
                    .append("  transactions=")
                    .append(getTransactionsList().size())
                    .append("\n\n");
            toStringBuff.append("  Txs [\n");
            int index = 0;
            for (AionTransaction tx : getTransactionsList()) {
                toStringBuff
                        .append("  ")
                        .append("index=")
                        .append(index++)
                        .append("\n")
                        .append(tx)
                        .append("\n");
            }
            toStringBuff.append("  ]\n");
        } else {
            toStringBuff.append("  Txs []\n");
        }
        toStringBuff.append("]\n");
        toStringBuff.append(Hex.toHexString(this.getEncoded())).append("\n");

        return toStringBuff.toString();
    }

    @Override
    public boolean isGenesis() {
        return this.header.isGenesis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AionBlock block = (AionBlock) o;
        return Arrays.equals(getEncoded(), block.getEncoded());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getEncoded());
    }

    @Override
    public byte[] getEncoded() {
        byte[] header = this.header.getEncoded();

        List<byte[]> block = getBodyElements();
        block.add(0, header);
        byte[][] elements = block.toArray(new byte[block.size()][]);

        return RLP.encodeList(elements);
    }

    @Override
    public String getShortHash() {
        return Hex.toHexString(getHash()).substring(0, 6);
    }

    @Override
    public String getShortDescr() {
        return "#"
                + getNumber()
                + " ("
                + Hex.toHexString(getHash()).substring(0, 6)
                + " <~ "
                + Hex.toHexString(getParentHash()).substring(0, 6)
                + ") Txs:"
                + getTransactionsList().size();
    }

    @Override
    public long getNrgConsumed() {
        return this.header.getEnergyConsumed();
    }

    @Override
    public long getNrgLimit() {
        return this.header.getEnergyLimit();
    }

    public void seal(byte[] nonce, byte[] solution) {
        if (nonce == null) {
            throw new NullPointerException("nonce is null");
        }

        if (solution == null) {
            throw new NullPointerException("solution is null");
        }

        header =
                A0BlockHeader.Builder.newInstance()
                        .withHeader(header)
                        .withNonce(nonce)
                        .withSolution(solution)
                        .build();
    }

    @Override
    public void updateHeaderDifficulty(byte[] diff) {
        if (diff == null) {
            throw new NullPointerException("difficulty is null");
        }
        this.header =
            A0BlockHeader.Builder.newInstance()
                .withHeader(getHeader())
                .withDifficulty(diff)
                .build();
    }
}
