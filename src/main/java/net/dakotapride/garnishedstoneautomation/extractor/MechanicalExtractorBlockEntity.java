package net.dakotapride.garnishedstoneautomation.extractor;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.sound.SoundScapes;
import com.simibubi.create.foundation.utility.VecHelper;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.ViewOnlyWrappedStorageView;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerSlot;
import net.dakotapride.garnishedstoneautomation.GarnishedStoneAutomation;
import net.dakotapride.garnishedstoneautomation.ModBlocks;
import net.dakotapride.garnishedstoneautomation.ModRecipeTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MechanicalExtractorBlockEntity extends KineticBlockEntity implements SidedStorageBlockEntity, IHaveGoggleInformation {

    public ItemStackHandlerContainer inputInv;
    public ItemStackHandler outputInv;
    public ExtractorInventoryHandler capability;
    public int timer;
    private ExtractingRecipe lastRecipe;

    public MechanicalExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInv = new ItemStackHandlerContainer(1);
        outputInv = new ItemStackHandler(9);
        capability = new ExtractorInventoryHandler();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        tooltip.add(Component.literal(""));

        if (!(level.getBlockState(this.getBlockPos().below()).is(ModBlocks.HEAT_SOURCES_C))) {

            GarnishedStoneAutomation.LANG.translate("text.requires_heat").color(0xBFB6AE).space().forGoggles(tooltip, 1);
            // tooltip.add(Component.translatable("text.garnishedstoneautomation.requires_heat").setStyle(Style.EMPTY.withColor(0xBFB6AE)));

            if (isPlayerSneaking) {
                GarnishedStoneAutomation.LANG.translate("text.heat_source_list.1").color(0xBFB6AE).space().forGoggles(tooltip, 1);
                GarnishedStoneAutomation.LANG.translate("text.heat_source_list.2").color(0xBFB6AE).space().forGoggles(tooltip, 1);
                GarnishedStoneAutomation.LANG.translate("text.heat_source_list.3").color(0xBFB6AE).space().forGoggles(tooltip, 1);
                // tooltip.add(Component.translatable("text.garnishedstoneautomation.heat_source_list"));
            }
        } else {
            GarnishedStoneAutomation.LANG.translate("text.heat_source_found").color(0xE88300).space().forGoggles(tooltip, 1);
            // tooltip.add(Component.translatable("text.garnishedstoneautomation.heat_source_found").setStyle(Style.EMPTY.withColor(0xE88300)));
        }

        return true;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        super.addBehaviours(behaviours);
        // registerAwardables(behaviours, AllAdvancements.MILLSTONE);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        if (getSpeed() == 0)
            return;
        if (inputInv.getStackInSlot(0)
                .isEmpty())
            return;

        float pitch = Mth.clamp((Math.abs(getSpeed()) / 256f) + .45f, .85f, 1f);
        SoundScapes.play(SoundScapes.AmbienceGroup.MILLING, worldPosition, pitch);
    }

    @Override
    public void tick() {
        super.tick();

        if (getSpeed() == 0)
            return;
        for (int i = 0; i < outputInv.getSlotCount(); i++)
            if (outputInv.getStackInSlot(i)
                    .getCount() == outputInv.getSlotLimit(i))
                return;

        if (timer > 0) {
            timer -= getProcessingSpeed();

            if (level.isClientSide) {
                spawnParticles();
                return;
            }
            if (timer <= 0)
                process();
            return;
        }

        if (inputInv.getStackInSlot(0)
                .isEmpty())
            return;

        if (lastRecipe == null || !lastRecipe.matches(inputInv, level) ||  !(level.getBlockState(this.getBlockPos().below()).is(ModBlocks.HEAT_SOURCES_C))) {
            Optional<ExtractingRecipe> recipe = ModRecipeTypes.EXTRACTING.find(inputInv, level);
            if (!recipe.isPresent()) {
                timer = 100;
                sendData();
            } else {
                lastRecipe = recipe.get();
                timer = lastRecipe.getProcessingDuration();
                sendData();
            }
            return;
        }

        timer = lastRecipe.getProcessingDuration();
        sendData();
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inputInv);
        ItemHelper.dropContents(level, worldPosition, outputInv);
    }

    private void process() {
        if (lastRecipe == null || !lastRecipe.matches(inputInv, level) || !(level.getBlockState(this.getBlockPos().below()).is(ModBlocks.HEAT_SOURCES_C))) {
            Optional<ExtractingRecipe> recipe = ModRecipeTypes.EXTRACTING.find(inputInv, level);
            if (!recipe.isPresent())
                return;
            lastRecipe = recipe.get();
        }

        try (Transaction t = TransferUtil.getTransaction()) {
            ItemStackHandlerSlot slot = inputInv.getSlot(0);
            slot.extract(slot.getResource(), 1, t);
            lastRecipe.rollResults().forEach(stack -> outputInv.insert(ItemVariant.of(stack), stack.getCount(), t));
            t.commit();
        }
        // award(AllAdvancements.MILLSTONE);

        sendData();
        setChanged();
    }

    public void spawnParticles() {
        ItemStack stackInSlot = inputInv.getStackInSlot(0);
        if (stackInSlot.isEmpty())
            return;

        ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
        float angle = level.random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, 0, 0.5f);
        offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y);

        Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
        level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("Timer", timer);
        compound.put("InputInventory", inputInv.serializeNBT());
        compound.put("OutputInventory", outputInv.serializeNBT());
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        timer = compound.getInt("Timer");
        inputInv.deserializeNBT(compound.getCompound("InputInventory"));
        outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
        super.read(compound, clientPacket);
    }

    public int getProcessingSpeed() {
        return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
    }

    @Nullable
    @Override
    public Storage<ItemVariant> getItemStorage(@Nullable Direction direction) {
        return capability;
    }

    private boolean canProcess(ItemStack stack) {
        ItemStackHandlerContainer tester = new ItemStackHandlerContainer(1);
        tester.setStackInSlot(0, stack);

        if (lastRecipe != null && lastRecipe.matches(tester, level) && (level.getBlockState(this.getBlockPos().below()).is(ModBlocks.HEAT_SOURCES_C)))
            return true;
        return ModRecipeTypes.EXTRACTING.find(tester, level)
                .isPresent() && level.getBlockState(this.getBlockPos().below()).is(ModBlocks.HEAT_SOURCES_C);
    }

    private class ExtractorInventoryHandler extends CombinedStorage<ItemVariant, ItemStackHandler> {

        public ExtractorInventoryHandler() {
            super(List.of(inputInv, outputInv));
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (canProcess(resource.toStack()))
                return inputInv.insert(resource, maxAmount, transaction);
            return 0;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return outputInv.extract(resource, maxAmount, transaction);
        }

        @Override
        public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
            return new ExtracterInventoryHandlerIterator();
        }

        private class ExtracterInventoryHandlerIterator implements Iterator<StorageView<ItemVariant>> {
            private boolean output = true;
            private Iterator<StorageView<ItemVariant>> wrapped;

            public ExtracterInventoryHandlerIterator() {
                wrapped = outputInv.iterator();
            }

            @Override
            public boolean hasNext() {
                return wrapped.hasNext();
            }

            @Override
            public StorageView<ItemVariant> next() {
                StorageView<ItemVariant> view = wrapped.next();
                if (!output) view = new ViewOnlyWrappedStorageView<>(view);
                if (output && !hasNext()) {
                    wrapped = inputInv.iterator();
                    output = false;
                }
                return view;
            }
        }
    }

}
