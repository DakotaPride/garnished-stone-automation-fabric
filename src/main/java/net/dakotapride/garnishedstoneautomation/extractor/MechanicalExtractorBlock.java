package net.dakotapride.garnishedstoneautomation.extractor;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Iterate;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.dakotapride.garnishedstoneautomation.ModBlockEntityTypes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

public class MechanicalExtractorBlock extends KineticBlock implements IBE<MechanicalExtractorBlockEntity>, ICogWheel, IWrenchable {

    public MechanicalExtractorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        if (!player.getItemInHand(handIn)
                .isEmpty())
            return InteractionResult.PASS;
        if (worldIn.isClientSide)
            return InteractionResult.SUCCESS;

        withBlockEntityDo(worldIn, pos, extractor -> {
            boolean emptyOutput = true;
            ItemStackHandler inv = extractor.outputInv;
            for (int slot = 0; slot < inv.getSlotCount(); slot++) {
                ItemStack stackInSlot = inv.getStackInSlot(slot);
                if (!stackInSlot.isEmpty())
                    emptyOutput = false;
                player.getInventory()
                        .placeItemBackInInventory(stackInSlot);
                inv.setStackInSlot(slot, ItemStack.EMPTY);
            }

            if (emptyOutput) {
                inv = extractor.inputInv;
                for (int slot = 0; slot < inv.getSlotCount(); slot++) {
                    player.getInventory()
                            .placeItemBackInInventory(inv.getStackInSlot(slot));
                    inv.setStackInSlot(slot, ItemStack.EMPTY);
                }
            }

            extractor.setChanged();
            extractor.sendData();
        });

        return InteractionResult.SUCCESS;
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);

        if (entityIn.level().isClientSide)
            return;
        if (!(entityIn instanceof ItemEntity))
            return;
        if (!entityIn.isAlive())
            return;

        MechanicalExtractorBlockEntity extractor = null;
        for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition()))
            if (extractor == null)
                extractor = getBlockEntity(worldIn, pos);

        if (extractor == null)
            return;

        ItemEntity itemEntity = (ItemEntity) entityIn;
        Storage<ItemVariant> handler = extractor.getItemStorage(null);
        if (handler == null)
            return;

        try (Transaction t = TransferUtil.getTransaction()) {
            ItemStack inEntity = itemEntity.getItem();
            long inserted = handler.insert(ItemVariant.of(inEntity), inEntity.getCount(), t);
            if (inserted == inEntity.getCount())
                itemEntity.discard();
            else itemEntity.setItem(ItemHandlerHelper.copyStackWithSize(inEntity, (int) (inEntity.getCount() - inserted)));
            t.commit();
        }
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<MechanicalExtractorBlockEntity> getBlockEntityClass() {
        return MechanicalExtractorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalExtractorBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.EXTRACTOR.get();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

}

