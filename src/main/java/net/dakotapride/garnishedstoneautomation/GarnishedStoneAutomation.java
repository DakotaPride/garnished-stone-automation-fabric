package net.dakotapride.garnishedstoneautomation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

public class GarnishedStoneAutomation implements ModInitializer {

	public static final String MOD_ID = "garnishedstoneautomation";
	public static final String NAME = "Garnished Stone Automation";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	public static final Lang LANG = new Lang(MOD_ID);
	public static final NonNullSupplier<CreateRegistrate> REGISTRATE =
			NonNullSupplier.lazy(() -> CreateRegistrate.create(MOD_ID));

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	@Override
	public void onInitialize() {

		ModBlockEntityTypes.init();
		ModItems.init();
		ModBlocks.init();
		ModItemTabs.init();
		ModRecipeTypes.init();
		REGISTRATE.get().register();

		LOGGER.info("Create addon mod [{}] is loading alongside Create [{}]!", NAME, Create.VERSION);
		LOGGER.info(EnvExecutor.unsafeRunForDist(
				() -> () -> "{} is accessing Porting Lib from the client!",
				() -> () -> "{} is accessing Porting Lib from the server!"
		), NAME);
	}

	static {
		REGISTRATE.get().setTooltipModifierFactory(item -> {
			return new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
					.andThen(TooltipModifier.mapNull(KineticStats.create(item)));
		});
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
