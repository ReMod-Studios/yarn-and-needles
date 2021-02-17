package com.remodstudios.yaneedles.datagen.generators.block;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.IdUtils;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class FenceGateBlockGenerator extends AbstractParentedBlockGenerator {

    private static final Object2IntMap<Direction> DIR2DEG = new Object2IntLinkedOpenHashMap<>();
    private static final String[] VARIANT_SUFFIXES = { "", "_open", "_wall", "_wall_open" };

    static {
        DIR2DEG.put(Direction.SOUTH, 0);
        DIR2DEG.put(Direction.WEST, 1);
        DIR2DEG.put(Direction.NORTH, 2);
        DIR2DEG.put(Direction.EAST, 3);
    }

    public FenceGateBlockGenerator(Map<String, String> arguments) {
        super(arguments);
    }
    public FenceGateBlockGenerator(Identifier baseBlockId) {
        super(baseBlockId);
    }

    @Override
    protected void generateBlockStates(ArtificeResourcePack.ClientResourcePackBuilder rrp, Identifier id) {
        Identifier blockPath = getBlockSubPath(id);

        rrp.addBlockState(id, state -> {
            for (Direction facing : FenceGateBlock.FACING.getValues())
            for (boolean in_wall : FenceGateBlock.IN_WALL.getValues())
            for (boolean open: FenceGateBlock.OPEN.getValues()) {
                String varStr = String.format(
                        "facing=%s,in_wall=%s,open=%s",
                        facing, in_wall, open);

                state.variant(varStr, var -> {
                    StringBuilder modelStr = new StringBuilder(blockPath.toString());
                    if (in_wall) modelStr.append("_wall");
                    if (open) modelStr.append("_open");
                    var .model(new Identifier(modelStr.toString()))
                        .uvlock(true)
                        .rotationY(DIR2DEG.getInt(facing) * 90);
                });
            }
        });
    }

    @Override
    protected void generateModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        for (String suffix : VARIANT_SUFFIXES) {
            pack.addBlockModel(IdUtils.wrapPath(id, suffix), model -> model
                .parent(new Identifier("block/template_fence_gate" + suffix))
                .texture("texture", baseBlockId)
            );
        }
    }
}
