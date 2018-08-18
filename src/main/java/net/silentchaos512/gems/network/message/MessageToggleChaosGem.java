package net.silentchaos512.gems.network.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.gems.compat.BaublesCompat;
import net.silentchaos512.gems.item.ItemChaosGem;
import net.silentchaos512.gems.network.Message;
import net.silentchaos512.lib.util.PlayerHelper;

import java.util.function.Predicate;

public class MessageToggleChaosGem extends Message {
    public boolean all;

    public MessageToggleChaosGem() {
        this.all = false;
    }

    public MessageToggleChaosGem(boolean all) {
        this.all = all;
    }

    @Override
    public IMessage handleMessage(MessageContext context) {
        if (context.side != Side.SERVER)
            return null;

        EntityPlayer player = context.getServerHandler().player;

        Predicate<ItemStack> predicate = s -> s.getItem() instanceof ItemChaosGem;
        NonNullList<ItemStack> stacks = BaublesCompat.getBaubles(player, predicate);
        stacks.addAll(PlayerHelper.getNonEmptyStacks(player, predicate));

        for (ItemStack stack : stacks) {
            if (stack.getItem() instanceof ItemChaosGem) {
                ItemChaosGem item = (ItemChaosGem) stack.getItem();
                item.setEnabled(stack, !item.isEnabled(stack));

                if (item.isEnabled(stack))
                    item.applyEffects(stack, player);
                else
                    item.removeEffects(stack, player);

                if (!all)
                    return null;
            }
        }

        return null;
    }
}
