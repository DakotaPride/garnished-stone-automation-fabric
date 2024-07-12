package net.dakotapride.garnishedstoneautomation;

import com.simibubi.create.AllCreativeModeTabs;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModItemTabs {
    public static final AllCreativeModeTabs.TabInfo GARNISHED_STONE_AUTOMATION = register("create.garnished.stone_automation", () -> {
        return FabricItemGroup.builder().title(Component.translatable("itemGroup.create.garnished.stone_automation")).icon(() -> {
            return (ModItems.ASURINE_CLUSTER.get()).getDefaultInstance();
        }).displayItems(new GarnishedStoneAutomationDisplayItemsGenerator()).build();
    });

    public static class GarnishedStoneAutomationDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
        public GarnishedStoneAutomationDisplayItemsGenerator() {
        }

        @Override
        public void accept(CreativeModeTab.@NotNull ItemDisplayParameters params, CreativeModeTab.@NotNull Output output) {
            output.accept(ModBlocks.MECHANICAL_EXTRACTOR.asStack());
            output.accept(ModItems.ASURINE_CLUSTER.asStack());
            output.accept(ModItems.CRIMSITE_CLUSTER.asStack());
            output.accept(ModItems.OCHRUM_CLUSTER.asStack());
            output.accept(ModItems.VERIDIUM_CLUSTER.asStack());
        }
    }

    private static AllCreativeModeTabs.TabInfo register(String name, Supplier<CreativeModeTab> supplier) {
        ResourceLocation id = GarnishedStoneAutomation.asResource(name);
        ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id);
        CreativeModeTab tab = supplier.get();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
        return new AllCreativeModeTabs.TabInfo(key, tab);
    }

    public static void init() {}
}
