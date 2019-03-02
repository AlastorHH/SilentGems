/*
 * Silent's Gems -- Glowrose
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

package net.silentchaos512.gems.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.silentchaos512.gems.lib.Gems;
import net.silentchaos512.gems.lib.IGem;

import javax.annotation.Nullable;
import java.util.List;

public class Glowrose extends BlockFlower implements IGem {
    private final Gems gem;

    public Glowrose(Gems gem) {
        super(Properties.create(Material.PLANTS)
                .sound(SoundType.PLANT)
                .lightValue(10)
                .hardnessAndResistance(0)
                .doesNotBlockMovement()
        );
        this.gem = gem;
    }

    @Override
    public Gems getGem() {
        return gem;
    }

    @Override
    protected boolean isValidGround(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        if (gem.getSet() == Gems.Set.DARK) {
            Block block = state.getBlock();
            if (block == Blocks.NETHERRACK || block == Blocks.NETHER_QUARTZ_ORE) {
                return true;
            }
        } else if (gem.getSet() == Gems.Set.LIGHT) {
            Block block = state.getBlock();
            if (block == Blocks.END_STONE) {
                return true;
            }
        }

        return super.isValidGround(state, worldIn, pos);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(gem.getSet().getDisplayName());
    }

    @Override
    public ITextComponent getNameTextComponent() {
        return new TextComponentTranslation("block.silentgems.glowrose", this.gem.getDisplayName());
    }
}
