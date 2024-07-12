package net.dakotapride.garnishedstoneautomation;

import static net.dakotapride.garnishedstoneautomation.GarnishedStoneAutomation.REGISTRATE;

import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.Item;

public class ModItems {
	// Incomplete Processing Items
	public static final ItemEntry<Item> INCOMPLETE_CRIMSITE = REGISTRATE.get().item("incomplete_crimsite", Item::new).register();
	public static final ItemEntry<Item> INCOMPLETE_VERIDIUM = REGISTRATE.get().item("incomplete_veridium", Item::new).register();
	public static final ItemEntry<Item> INCOMPLETE_ASURINE = REGISTRATE.get().item("incomplete_asurine", Item::new).register();
	public static final ItemEntry<Item> INCOMPLETE_OCHRUM = REGISTRATE.get().item("incomplete_ochrum", Item::new).register();

	// Vehement Clusters
	public static final ItemEntry<Item> CRIMSITE_CLUSTER = REGISTRATE.get().item("crimsite_cluster", Item::new).register();
	public static final ItemEntry<Item> VERIDIUM_CLUSTER = REGISTRATE.get().item("veridium_cluster", Item::new).register();
	public static final ItemEntry<Item> ASURINE_CLUSTER = REGISTRATE.get().item("asurine_cluster", Item::new).register();
	public static final ItemEntry<Item> OCHRUM_CLUSTER = REGISTRATE.get().item("ochrum_cluster", Item::new).register();

	public static void init() {
		// load the class and register everything
		GarnishedStoneAutomation.LOGGER.info("Registering items for " + GarnishedStoneAutomation.NAME);
	}
}
