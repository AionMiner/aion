package org.aion.zero.impl.core;

import java.math.BigInteger;

import org.aion.zero.impl.types.BlockHeader;

/**
 * Interface for retrieving difficulty calculations for a particular chain configuration, note that
 * depending on where the corresponding class is generated, it will utilized different algorithms.
 * However the common interface of the current and parent blockHeader will remain.
 *
 * @author yao
 */
@FunctionalInterface
public interface IDifficultyCalculator {
    BigInteger calculateDifficulty(BlockHeader current, BlockHeader dependency);
}
