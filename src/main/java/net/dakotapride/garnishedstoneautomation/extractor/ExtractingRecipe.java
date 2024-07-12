package net.dakotapride.garnishedstoneautomation.extractor;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;

import net.dakotapride.garnishedstoneautomation.ModRecipeTypes;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
public class ExtractingRecipe extends AbstractCrushingRecipe {

    public ExtractingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(ModRecipeTypes.EXTRACTING, params);
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0)
                .test(inv.getItem(0));
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }
}

