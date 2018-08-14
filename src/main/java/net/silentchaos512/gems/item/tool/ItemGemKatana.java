package net.silentchaos512.gems.item.tool;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.config.ConfigOptionToolClass;
import net.silentchaos512.gems.config.GemsConfig;
import net.silentchaos512.gems.init.ModItems;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.gems.util.ToolHelper;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.ItemHelper;
import net.silentchaos512.lib.util.StackHelper;

import java.util.List;

public class ItemGemKatana extends ItemGemSword {

    public ItemGemKatana() {

        super();
        setTranslationKey(SilentGems.RESOURCE_PREFIX + Names.KATANA);
    }

    public ConfigOptionToolClass getConfig() {

        return GemsConfig.katana;
    }

    @Override
    public ItemStack constructTool(ItemStack rod, ItemStack... materials) {

        if (getConfig().isDisabled)
            return StackHelper.empty();
        ItemStack result = ToolHelper.constructTool(this, rod, materials);
        return addDefaultGrip(result);
    }

    @Override
    public ItemStack constructTool(boolean supercharged, ItemStack... materials) {

        if (getConfig().isDisabled)
            return StackHelper.empty();
        ItemStack rod = supercharged ? ModItems.craftingMaterial.toolRodGold
                : new ItemStack(Items.STICK);
        ItemStack result = ToolHelper.constructTool(this, rod, materials);
        return addDefaultGrip(result);
    }

    @Deprecated
    public ItemStack addDefaultGrip(ItemStack katana) {

//    if (StackHelper.isEmpty(katana))
//      return StackHelper.empty();
//    EnumMaterialTier tier = ToolHelper.getToolTier(katana);
//    ItemStack wool = new ItemStack(Blocks.WOOL, 1, tier == EnumMaterialTier.SUPER
//        ? EnumDyeColor.BLACK.getMetadata() : EnumDyeColor.SILVER.getMetadata());
//    ToolHelper.setPart(katana, ToolPartRegistry.fromStack(wool), EnumMaterialGrade.NONE,
//        EnumPartPosition.ROD_GRIP);
        return katana;
    }

    @Override
    public float getMeleeDamageModifier() {

        return 2.0f;
    }

    @Override
    public float getMagicDamageModifier() {

        return 3.0f;
    }

    @Override
    public float getMeleeSpeedModifier() {

        return -2.2f;
    }

    @Override
    public float getDurabilityMultiplier() {

        return 0.75f;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSuperTool() {

        return true;
    }

    @Override
    public void clGetSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {

        if (!ItemHelper.isInCreativeTab(item, tab))
            return;

        List<ItemStack> subItems = ToolHelper.getSubItems(item, 3);
        for (ItemStack stack : subItems) {
            stack = addDefaultGrip(stack);
        }
        list.addAll(subItems);
    }

    @Override
    public void addRecipes(RecipeMaker recipes) {

        if (!getConfig().isDisabled)
            ToolHelper.addExampleRecipe(this, "hh", "h ", "r ");
    }

    @Override
    public String getName() {

        return Names.KATANA;
    }
}
