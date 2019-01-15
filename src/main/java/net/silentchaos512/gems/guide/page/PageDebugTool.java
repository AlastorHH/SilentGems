package net.silentchaos512.gems.guide.page;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.init.ModItems;
import net.silentchaos512.gems.item.CraftingItems;
import net.silentchaos512.gems.lib.Gems;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;
import net.silentchaos512.lib.guidebook.page.GuidePage;

public class PageDebugTool extends GuidePage {

  public static final int WIDTH_ITEMS = 7;

  int index;

  public PageDebugTool(GuideBook book, int key) {

    super(book, key);
  }

  public PageDebugTool(GuideBook book, int key, int priority) {

    super(book, key, priority);
  }

  @Override
  public String getInfoText() {

    return "";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void initGui(GuiGuideBase gui, int startX, int startY) {

    super.initGui(gui, startX, startY);

    index = 0;

    if (this.key == 1) {
      // Bunch of randomized tools!
      for (int i = 0; i < 63; ++i) {
        Item item = ModItems.tools.get(SilentGems.random.nextInt(ModItems.tools.size()));
        ItemStack stack = ToolRandomizer.INSTANCE.randomize(new ItemStack(item));
        stack.getTagCompound().setBoolean(ToolHelper.NBT_EXAMPLE_TOOL, true);
        addItem(gui, startX, startY, stack);
      }
    } else if (this.key == 2) {
      // Error (data-less) tools.
      for (Item item : ModItems.tools) {
        ItemStack tool = new ItemStack(item);
        tool.setTagCompound(new NBTTagCompound());
        tool.getTagCompound().setBoolean(ToolHelper.NBT_EXAMPLE_TOOL, true);
        addItem(gui, startX, startY, tool);
      }
      // Fill in the remainder of the row.
      for (int i = 0; i < 7 - ModItems.tools.size() % 7; ++i) {
        addItem(gui, startX, startY, ItemStack.EMPTY);
      }
      // Broken tools.
      for (Item item : ModItems.tools) {
        ItemStack stack = ToolHelper.constructTool(item, CraftingItems.ORNATE_GOLD_ROD.getStack(),
            Gems.selectRandom().getItemSuper());
        stack.setItemDamage(stack.getMaxDamage());
        stack.getTagCompound().setBoolean(ToolHelper.NBT_EXAMPLE_TOOL, true);
        ToolHelper.recalculateStats(stack);
        addItem(gui, startX, startY, stack);
      }
    }
  }

  private void addItem(GuiGuideBase gui, int startX, int startY, ItemStack tool) {

    int x = startX + 3 + 18 * (index % WIDTH_ITEMS);
    int y = startY + 3 + 18 * (index / WIDTH_ITEMS);
    gui.addOrModifyItemRenderer(tool, x, y, 1.0f, false);
    ++index;
  }
}
