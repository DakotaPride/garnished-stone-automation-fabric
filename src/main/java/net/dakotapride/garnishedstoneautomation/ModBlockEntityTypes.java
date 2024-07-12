package net.dakotapride.garnishedstoneautomation;

import static net.dakotapride.garnishedstoneautomation.GarnishedStoneAutomation.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import net.dakotapride.garnishedstoneautomation.extractor.MechanicalExtractorBlockEntity;
import net.dakotapride.garnishedstoneautomation.extractor.MechanicalExtractorCogInstance;
import net.dakotapride.garnishedstoneautomation.extractor.MechanicalExtractorRenderer;

public class ModBlockEntityTypes {

    public static final BlockEntityEntry<MechanicalExtractorBlockEntity> EXTRACTOR = REGISTRATE.get()
            .blockEntity("mechanical_extractor", MechanicalExtractorBlockEntity::new)
            .instance(() -> MechanicalExtractorCogInstance::new, false)
            .validBlocks(ModBlocks.MECHANICAL_EXTRACTOR)
            .renderer(() -> MechanicalExtractorRenderer::new)
            .register();

    public static void init() {}
}
