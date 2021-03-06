package net.silentchaos512.gems.item.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.silentchaos512.gems.init.GemsItemGroups;

public class GemBagItem extends GemContainerItem {
    public GemBagItem() {
        super(new Properties().group(GemsItemGroups.UTILITY).maxStackSize(1));
    }

    @Override
    public int getInventorySize(ItemStack stack) {
        return 54;
    }

    @Override
    public boolean canStore(ItemStack stack) {
        return stack.getItem().isIn(Tags.Items.GEMS);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            playerIn.openContainer(new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent("container.silentgems.gem_bag");
                }

                @Override
                public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
                    return new GemBagContainer(id, playerInventory);
                }
            });
        }

        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
    }
}
