package net.dakotapride.garnishedstoneautomation.compat.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.dakotapride.garnishedstoneautomation.GarnishedStoneAutomation;
import net.dakotapride.garnishedstoneautomation.ModBlocks;
import net.dakotapride.garnishedstoneautomation.ModItems;
import net.dakotapride.garnishedstoneautomation.ModRecipeTypes;
import net.dakotapride.garnishedstoneautomation.extractor.ExtractingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class GarnishedStoneAutomationJEI implements IModPlugin {
    private static final ResourceLocation ID = GarnishedStoneAutomation.asResource("jei_plugin");

    protected final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
    protected IIngredientManager ingredientManager;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();
        allCategories.forEach(c -> c.registerRecipes(registration));

        registration.addIngredientInfo(new ItemStack(ModItems.INCOMPLETE_ASURINE.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.garnishedstoneautomation.asurine.information"));
        registration.addIngredientInfo(new ItemStack(ModItems.INCOMPLETE_CRIMSITE.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.garnishedstoneautomation.crimsite.information"));
        registration.addIngredientInfo(new ItemStack(ModItems.INCOMPLETE_OCHRUM.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.garnishedstoneautomation.ochrum.information"));
        registration.addIngredientInfo(new ItemStack(ModItems.INCOMPLETE_VERIDIUM.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.garnishedstoneautomation.veridium.information"));

        registration.addIngredientInfo(new ItemStack(ModItems.ASURINE_CLUSTER.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.garnishedstoneautomation.asurine_cluster.information"));
        registration.addIngredientInfo(new ItemStack(ModItems.CRIMSITE_CLUSTER.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.garnishedstoneautomation.crimsite_cluster.information"));
        registration.addIngredientInfo(new ItemStack(ModItems.OCHRUM_CLUSTER.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.garnishedstoneautomation.ochrum_cluster.information"));
        registration.addIngredientInfo(new ItemStack(ModItems.VERIDIUM_CLUSTER.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.garnishedstoneautomation.veridium_cluster.information"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }

    private static <T extends Recipe<?>> RecipeCategoryBuilder<T> builder(Class<T> cls) {
        return new RecipeCategoryBuilder<>(GarnishedStoneAutomation.MOD_ID, cls);
    }

    private void loadCategories() {
        allCategories.clear();
        allCategories.add(
                builder(ExtractingRecipe.class)
                        .addTypedRecipes(ModRecipeTypes.EXTRACTING::getType)
                        .catalyst(ModBlocks.MECHANICAL_EXTRACTOR::get)
                        .doubleItemIcon(ModBlocks.MECHANICAL_EXTRACTOR.get(), AllPaletteStoneTypes.ASURINE.getBaseBlock().get())
                        .emptyBackground(177, 76)
                        .build("extracting", ExtractingCategory::new));
    }
}
