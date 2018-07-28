package net.silentchaos512.gems.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.api.Skulls;
import net.silentchaos512.gems.client.gui.GuiHandlerSilentGems;
import net.silentchaos512.gems.compat.BaublesCompat;
import net.silentchaos512.gems.compat.crafttweaker.CTSilentGems;
import net.silentchaos512.gems.event.GemsCommonEvents;
import net.silentchaos512.gems.event.ShieldEventHandler;
import net.silentchaos512.gems.handler.PlayerDataHandler;
import net.silentchaos512.gems.init.ModItems;
import net.silentchaos512.gems.item.quiver.QuiverHelper;
import net.silentchaos512.gems.lib.EnumModParticles;
import net.silentchaos512.gems.lib.module.ModuleHalloweenHijinks;
import net.silentchaos512.gems.lib.module.ModuleHolidayCheer;
import net.silentchaos512.gems.network.NetworkHandler;
import net.silentchaos512.gems.util.SoulManager;
import net.silentchaos512.gems.util.ToolHelper;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.Color;

public class GemsCommonProxy extends net.silentchaos512.lib.proxy.CommonProxy {

  @Override
  public void preInit(SRegistry registry) {

    super.preInit(registry);

    ToolHelper.FAKE_MATERIAL.setRepairItem(ModItems.craftingMaterial.chaosEssenceEnriched);

    ModItems.guideBook.book.preInit();

    NetworkHandler.init();

    NetworkRegistry.INSTANCE.registerGuiHandler(SilentGems.instance, new GuiHandlerSilentGems());

    MinecraftForge.EVENT_BUS.register(new PlayerDataHandler.EventHandler());
    MinecraftForge.EVENT_BUS.register(new GemsCommonEvents());
    MinecraftForge.EVENT_BUS.register(new ShieldEventHandler());
    MinecraftForge.EVENT_BUS.register(QuiverHelper.instance);
    MinecraftForge.EVENT_BUS.register(new SoulManager());
    MinecraftForge.EVENT_BUS.register(ModuleHolidayCheer.instance);
    MinecraftForge.EVENT_BUS.register(ModuleHalloweenHijinks.instance);

    LootTableList.register(new ResourceLocation(SilentGems.MODID, "ender_slime"));
  }

  @Override
  public void init(SRegistry registry) {

    super.init(registry);
  }

  @Override
  public void postInit(SRegistry registry) {

    super.postInit(registry);
    Skulls.init();
    ModItems.enchantmentToken.addModRecipes();
    ModItems.guideBook.book.postInit();

    if (Loader.isModLoaded(BaublesCompat.MOD_ID))
      BaublesCompat.MOD_LOADED = true;

    if (Loader.isModLoaded("crafttweaker"))
      CTSilentGems.postInit();
  }

  public void spawnParticles(EnumModParticles type, Color color, World world, double x, double y,
      double z, double motionX, double motionY, double motionZ) {

  }

  public int getParticleSettings() {

    return 0;
  }

  public EntityPlayer getClientPlayer() {

    return null;
  }

  public boolean isClientPlayerHoldingDebugItem() {

    EntityPlayer player = getClientPlayer();
    if (player == null) {
      return false;
    }

    ItemStack mainhand = player.getHeldItemMainhand();
    ItemStack offhand = player.getHeldItemOffhand();
    return (mainhand != null && mainhand.getItem() == ModItems.debugItem)
        || (offhand != null && offhand.getItem() == ModItems.debugItem);
  }
}
