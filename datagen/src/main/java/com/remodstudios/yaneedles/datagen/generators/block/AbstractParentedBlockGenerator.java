package com.remodstudios.yaneedles.datagen.generators.block;

import com.swordglowsblue.artifice.api.util.IdUtils;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Generator that contains a reference to a base block.
 * Used for slabs, stairs, fences, walls, ...
 */
public abstract class AbstractParentedBlockGenerator extends SimpleBlockGenerator {

    @NotNull protected final Identifier baseBlockId;

    public AbstractParentedBlockGenerator(Identifier baseBlockId) {
        this.baseBlockId = IdUtils.wrapPath("block/", baseBlockId);
    }
}
