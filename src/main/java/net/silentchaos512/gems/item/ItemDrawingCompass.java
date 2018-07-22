/*
 * Silent's Gems
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gems.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.lib.EnumModParticles;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.StackHelper;

public class ItemDrawingCompass extends Item {

    public enum State {
        EMPTY, BLOCK1, BLOCK2
    }

    public ItemDrawingCompass() {
        setMaxStackSize(1);
    }

    public State getState(ItemStack stack) {
        if (getBlock2(stack).getY() > 0) {
            return State.BLOCK2;
        } else if (getBlock1(stack).getY() > 0) {
            return State.BLOCK1;
        }
        return State.EMPTY;
    }

    public BlockPos getBlock1(ItemStack stack) {
        return getBlock(stack, 1);
    }

    public BlockPos getBlock2(ItemStack stack) {
        return getBlock(stack, 2);
    }

    protected BlockPos getBlock(ItemStack stack, int index) {
        NBTTagCompound tags = StackHelper.getTagCompound(stack, true);
        int x = tags.getInteger("X" + index);
        int y = tags.getInteger("Y" + index);
        int z = tags.getInteger("Z" + index);
        return new BlockPos(x, y, z);
    }

    public void setBlock1(ItemStack stack, BlockPos pos) {
        setBlock(stack, pos, 1);
    }

    public void setBlock2(ItemStack stack, BlockPos pos) {
        setBlock(stack, pos, 2);
    }

    private void setBlock(ItemStack stack, BlockPos pos, int index) {
        NBTTagCompound tags = StackHelper.getTagCompound(stack, true);
        tags.setInteger("X" + index, pos.getX());
        tags.setInteger("Y" + index, pos.getY());
        tags.setInteger("Z" + index, pos.getZ());
    }

    public void clearBlocks(ItemStack stack) {
        if (stack.hasTagCompound()) {
            setBlock1(stack, BlockPos.ORIGIN);
            setBlock2(stack, BlockPos.ORIGIN);
        }
    }

    public Color getColor(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Color")) {
            return Color.WHITE;
        }
        return new Color(stack.getTagCompound().getInteger("Color"));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // Only allow changes when in main hand?
        if (hand == EnumHand.OFF_HAND) {
            return EnumActionResult.PASS;
        }

        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS; // Swing on client-side.
        }

        ItemStack stack = playerIn.getHeldItem(hand);

        // Get current state.
        State state = getState(stack);

        if (state == State.BLOCK1) {
            // Set block 2
            setBlock2(stack, pos.offset(facing));
            // Display distance.
            int radius = (int) Math.sqrt(getBlock1(stack).distanceSq(getBlock2(stack)));
            String str = SilentGems.i18n.subText(this, "radius", radius);
            ChatHelper.sendMessage(playerIn, str);
        } else if (state == State.EMPTY) {
            // Set block 1
            setBlock1(stack, pos.offset(facing));
        } else {
            String str = SilentGems.i18n.subText(this, "howToReset");
            ChatHelper.sendMessage(playerIn, str);
        }

        stack.setItemDamage(0);
        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (hand == EnumHand.MAIN_HAND && playerIn.isSneaking() && !worldIn.isRemote) {
            clearBlocks(stack);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    public boolean spawnParticles(ItemStack stack, EntityPlayer player, World world) {
        BlockPos pos1 = getBlock1(stack);
        BlockPos pos2 = getBlock2(stack);

        if (pos1.getY() <= 0 || pos2.getY() <= 0 || pos1.equals(pos2)) {
            return false;
        }

        Color color = getColor(stack);

        // Spawn circle.
        BlockPos center = new BlockPos(pos1.getX(), pos2.getY(), pos1.getZ());
        float radius = (float) Math.sqrt(center.distanceSq(pos2));
        int count = (int) (5 * radius);
        float increment = (float) (2 * Math.PI / count);
        float start = increment * (world.getTotalWorldTime() % 30) / 30f;

        Vec3d vec;
        for (float angle = start; angle < 2 * Math.PI + start; angle += increment) {
            vec = new Vec3d(radius, 0, 0).rotateYaw(angle);
            particle(player, world, color, center.getX() + 0.5 + vec.x, center.getY() + 0.5 + vec.y, center.getZ() + 0.5 + vec.z);
        }

        // Spawn line.
        double distance = Math.sqrt(pos1.distanceSq(pos2));
        count = (int) (2 * distance);
        double dx = (double) (pos2.getX() - pos1.getX()) / count;
        double dy = (double) (pos2.getY() - pos1.getY()) / count;
        double dz = (double) (pos2.getZ() - pos1.getZ()) / count;

        for (int i = 0; i < count; ++i) {
            vec = new Vec3d(pos1.getX() + 0.5 + i * dx, pos1.getY() + 0.5 + i * dy, pos1.getZ() + 0.5 + i * dz);
            particle(player, world, color, vec.x, vec.y, vec.z);
        }

        return true;
    }

    private void particle(EntityPlayer player, World world, Color color, double x, double y, double z) {
        if (player.getDistanceSq(x, y, z) < 2048)
            SilentGems.proxy.spawnParticles(EnumModParticles.DRAWING_COMPASS, color, world, x, y, z, 0, 0, 0);
    }
}
