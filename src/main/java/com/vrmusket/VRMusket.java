package com.vrmusket;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.fabric.GTCEuFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VRMusket implements ModInitializer {
	public static final String MOD_ID = "vrmusket";

	public static final boolean isDebugEnabled = Boolean.getBoolean("mymod.debug");

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Item WOODEN_STOCK = new Item(new Item.Properties());
	public static final Item BLUNDERBUSS_BARREL = new Item(new Item.Properties());
	public static final Item MUSKET_BARREL = new Item(new Item.Properties());

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "wooden_stock"), WOODEN_STOCK);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "blunderbuss_barrel"), BLUNDERBUSS_BARREL);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "musket_barrel"), MUSKET_BARREL);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> {
			content.accept(WOODEN_STOCK);
			content.accept(BLUNDERBUSS_BARREL);
			content.accept(MUSKET_BARREL);
		});

	}
}