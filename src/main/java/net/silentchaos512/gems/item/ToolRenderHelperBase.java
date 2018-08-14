package net.silentchaos512.gems.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.api.lib.EnumMaterialTier;
import net.silentchaos512.gems.init.ModItems;
import net.silentchaos512.gems.util.ToolHelper;
import net.silentchaos512.lib.util.LogHelper;

public class ToolRenderHelperBase extends Item {
    protected static LogHelper log;

    public static ToolRenderHelperBase instance;

    public static void init() {
        if (instance == null) {
            instance = ModItems.toolRenderHelper;
            log = SilentGems.logHelper;
        }
    }

    public ToolRenderHelperBase() {
    }

    /*
     * Constants
     */

    public static final int BOW_STAGE_COUNT = 4;

    // Render pass IDs and count
    // TODO: Remove these!
    public static final int PASS_ROD = 0;
    public static final int PASS_HEAD = 1;
    public static final int PASS_TIP = 2;
    public static final int PASS_ROD_DECO = 3;
    public static final int RENDER_PASS_COUNT = 4;

    public void updateModelCache(ItemStack tool) {
        // Do nothing, we only have models on the client-side! See ToolRenderHelper.
    }

    public boolean hasEffect(ItemStack tool) {
        return false;
    }

    public EnumRarity getRarity(ItemStack tool) {
        EnumMaterialTier tier = ToolHelper.getToolTier(tool);
        boolean enchanted = tool.isItemEnchanted();

        switch (tier) {
            case SUPER:
                return EnumRarity.EPIC;
            case REGULAR:
                return enchanted ? EnumRarity.RARE : EnumRarity.UNCOMMON;
            case MUNDANE:
            default:
                return enchanted ? EnumRarity.RARE : EnumRarity.COMMON;
        }
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !oldStack.isItemEqual(newStack)
                || (oldStack.hasTagCompound() && newStack.hasTagCompound()
                && !oldStack.getTagCompound().equals(newStack.getTagCompound()));
    }
}
