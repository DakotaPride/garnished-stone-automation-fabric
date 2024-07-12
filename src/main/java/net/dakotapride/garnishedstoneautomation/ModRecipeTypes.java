package net.dakotapride.garnishedstoneautomation;

import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import io.github.fabricators_of_create.porting_lib.util.ShapedRecipeUtil;
import net.dakotapride.garnishedstoneautomation.extractor.ExtractingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public enum ModRecipeTypes implements IRecipeTypeInfo {
    EXTRACTING(ExtractingRecipe::new);

    private final ResourceLocation id;
    private final RecipeSerializer<?> serializerObject;
    @Nullable
    private final RecipeType<?> typeObject;
    private final Supplier<RecipeType<?>> type;

    ModRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
        String name = GarnishedStoneAutomation.LANG.asId(name());
        id = GarnishedStoneAutomation.asResource(name);
        serializerObject = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializerSupplier.get());
        if (registerType) {
            typeObject = typeSupplier.get();
            Registry.register(BuiltInRegistries.RECIPE_TYPE, id, typeObject);
            type = typeSupplier;
        } else {
            typeObject = null;
            type = typeSupplier;
        }
    }

    ModRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = GarnishedStoneAutomation.LANG.asId(name());
        id = GarnishedStoneAutomation.asResource(name);
        serializerObject = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializerSupplier.get());
        typeObject = simpleType(id);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, id, typeObject);
        type = () -> typeObject;
    }

    ModRecipeTypes(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory) {
        this(() -> new ProcessingRecipeSerializer<>(processingFactory));
    }

    public static <T extends Recipe<?>> RecipeType<T> simpleType(ResourceLocation id) {
        String stringId = id.toString();
        return new RecipeType<T>() {
            @Override
            public String toString() {
                return stringId;
            }
        };
    }

    public static void init() {
        ShapedRecipeUtil.setCraftingSize(9, 9);
        // fabric: just load the class
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) type.get();
    }

    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
        return world.getRecipeManager()
                .getRecipeFor(getType(), inv, world);
    }

    public static boolean shouldIgnoreInAutomation(Recipe<?> recipe) {
        RecipeSerializer<?> serializer = recipe.getSerializer();
        if (serializer != null && AllTags.AllRecipeSerializerTags.AUTOMATION_IGNORE.matches(serializer))
            return true;
        return recipe.getId()
                .getPath()
                .endsWith("_manual_only");
    }
}
