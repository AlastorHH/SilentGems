package net.silentchaos512.gems.lib.module;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.lib.Foods;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.PlayerHelper;

import java.util.Calendar;

public class ModuleHolidayCheer {

  // Candy configs
  public static final int CANDY_TRY_DELAY = 10 * 60 * 20;
  public static final float CANDY_RATE = 0.125f;
  public static final int CANDY_MAX_QUANTITY = 3;

  // Date range
  public static final int MONTH = Calendar.DECEMBER;
  public static final int DAY_MIN = 20;
  public static final int DAY_MAX = 27;

  public static ModuleHolidayCheer instance = new ModuleHolidayCheer();

  public Calendar today;
  private boolean rightDay = checkDateAgain();
  boolean moduleEnabled = true;
  boolean forcedOn = false;

  @SubscribeEvent
  public void onPlayerJoin(PlayerLoggedInEvent event) {

    if (isEnabled())
      greetPlayer(event.player);
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {

    if (isEnabled() && event.player.world.getTotalWorldTime() % CANDY_TRY_DELAY == 0) {
      checkDateAgain();
      if (isRightDay() && !event.player.world.isRemote)
        tryGiveCandy(event.player);
    }
  }

  public boolean isEnabled() {

    return moduleEnabled || forcedOn;
  }

  public boolean isRightDay() {

    return rightDay || forcedOn;
  }

  public boolean checkDateAgain() {

    today = Calendar.getInstance();
    int month = today.get(Calendar.MONTH);
    int day = today.get(Calendar.DATE);
    rightDay = month == MONTH && day >= DAY_MIN && day <= DAY_MAX;
    return rightDay;
  }

  public void greetPlayer(EntityPlayer player) {

    checkDateAgain();
    if (isRightDay() && !player.world.isRemote) {
      String str = "[%s] Happy Holidays! Have some candy.";
      str = String.format(str, SilentGems.MOD_NAME/* , day */);

      ChatHelper.sendMessage(player, TextFormatting.GREEN + str);
      PlayerHelper.giveItem(player, Foods.CANDY_CANE.getStack());
    }
  }

  public void tryGiveCandy(EntityPlayer player) {

    if (SilentGems.random.nextFloat() <= CANDY_RATE) {
      int count = SilentGems.random.nextInt(CANDY_MAX_QUANTITY);
      PlayerHelper.giveItem(player, Foods.CANDY_CANE.getStack(count));
    }
  }

  public void loadConfig(Configuration c) {

    String cat = "misc.holiday_cheer";
    c.setCategoryComment(cat, "Winter holiday event options.");

    moduleEnabled = c.getBoolean("Enabled", cat, true,
        "Players will occasionally receive candy during part of December");
    forcedOn = c.getBoolean("Forced On", cat, false, "'Tis the season, every day all day!");
  }
}
