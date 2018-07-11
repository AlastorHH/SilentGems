package net.silentchaos512.gems.item;

import java.util.Map;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.init.ModBlocks;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.StackHelper;

public class ItemFluffyPuffSeeds extends ItemSeeds implements IRegistryObject {

  public ItemFluffyPuffSeeds() {

    super(ModBlocks.fluffyPuffPlant, Blocks.FARMLAND);
    setUnlocalizedName(Names.FLUFFY_PUFF_SEEDS);
    MinecraftForge.addGrassSeed(new ItemStack(this), 2);
  }

  @Override
  public IBlockState getPlant(IBlockAccess world, BlockPos pos) {

    return ModBlocks.fluffyPuffPlant.getDefaultState();
  }

  // 1.10.2 compatibility
  public EnumActionResult func_180614_a(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

    return onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
  }

  @Override
  public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos,
      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

    // Identical to ItemSeeds.onItemUse, except using getPlant instead of this.crops, which
    // does not work.
    IBlockState state = worldIn.getBlockState(pos);
    ItemStack stack = playerIn.getHeldItem(hand);
    if (facing == EnumFacing.UP && playerIn.canPlayerEdit(pos.offset(facing), facing, stack)
        && state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this)
        && worldIn.isAirBlock(pos.up())) {
      worldIn.setBlockState(pos.up(), getPlant(worldIn, pos));
      if (playerIn instanceof EntityPlayerMP)
        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) playerIn, pos.up(), stack);
      StackHelper.shrink(stack, 1);
      return EnumActionResult.SUCCESS;
    } else {
      return EnumActionResult.FAIL;
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {

    return "item.silentgems:" + Names.FLUFFY_PUFF_SEEDS;
  }

  @Override
  public void addRecipes(RecipeMaker recipes) {

  }

  @Override
  public void addOreDict() {

  }

  @Override
  public String getName() {

    return Names.FLUFFY_PUFF_SEEDS;
  }

  @Override
  public String getFullName() {

    return getModId() + ":" + getName();
  }

  @Override
  public String getModId() {

    return SilentGems.MODID;
  }

  @Override
  public void getModels(Map<Integer, ModelResourceLocation> models) {

    models.put(0, new ModelResourceLocation(getFullName().toLowerCase(), "inventory"));
  }

  @Override
  public boolean registerModels() {

    return false;
  }
}
