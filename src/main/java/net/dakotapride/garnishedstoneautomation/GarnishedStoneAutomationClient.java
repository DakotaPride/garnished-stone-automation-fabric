package net.dakotapride.garnishedstoneautomation;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class GarnishedStoneAutomationClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModPonderScenes.init();

		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MECHANICAL_EXTRACTOR.get(), RenderType.cutout());
	}
}
