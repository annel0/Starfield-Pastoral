package com.stardew.craft.integration.jade;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.utility.AbstractTwoBlockUtilityBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(StardewCraft.MODID)
public class GenericUtilityMachineJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(GenericUtilityMachineJadeProvider.INSTANCE, AbstractTwoBlockUtilityBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(GenericUtilityMachineJadeProvider.INSTANCE, AbstractTwoBlockUtilityBlock.class);
    }
}
