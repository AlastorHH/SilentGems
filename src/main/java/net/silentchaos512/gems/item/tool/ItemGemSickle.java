package net.silentchaos512.gems.item.tool;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.api.ITool;
import net.silentchaos512.gems.config.ConfigOptionToolClass;
import net.silentchaos512.gems.config.GemsConfig;
import net.silentchaos512.gems.init.ModItems;
import net.silentchaos512.gems.item.ToolRenderHelper;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.gems.util.ToolHelper;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.ItemHelper;
import net.silentchaos512.lib.util.StackHelper;

import java.util.List;
import java.util.Map;

public class ItemGemSickle extends ItemTool implements IRegistryObject, ITool {

    public static final Material[] effectiveMaterials = new Material[]{Material.CACTUS,
            Material.LEAVES, Material.PLANTS, Material.VINE, Material.WEB};

    public static final int DURABILITY_USAGE = 2;
    public static final int BREAK_RANGE = 4;
    public static final int HARVEST_RANGE = 2;

    public ItemGemSickle() {

        super(ToolHelper.FAKE_MATERIAL, Sets.newHashSet(new Block[]{}));
        setTranslationKey(SilentGems.RESOURCE_PREFIX + Names.SICKLE);
        setNoRepair();
    }

    public ItemStack constructTool(boolean supercharged, ItemStack material) {

        return constructTool(supercharged, material, material, material);
    }

    public ItemStack constructTool(boolean supercharged, ItemStack... materials) {

        ItemStack rod = supercharged ? ModItems.craftingMaterial.toolRodGold
                : new ItemStack(Items.STICK);
        return ToolHelper.constructTool(this, rod, materials);
    }

    // ===============
    // ITool overrides
    // ===============

    public ConfigOptionToolClass getConfig() {

        return GemsConfig.sickle;
    }

    @Override
    public ItemStack constructTool(ItemStack rod, ItemStack... materials) {

        if (getConfig().isDisabled)
            return StackHelper.empty();
        return ToolHelper.constructTool(this, rod, materials);
    }

    @Override
    public float getMeleeDamageModifier() {

        return 1.0f;
    }

    @Override
    public float getMagicDamageModifier() {

        return 0.0f;
    }

    @Override
    public float getMeleeSpeedModifier() {

        return -1.8f;
    }

    // ==============
    // Item overrides
    // ==============

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing side, float hitX, float hitY, float hitZ) {

        ItemStack stack = player.getHeldItem(hand);

        if (ToolHelper.isBroken(stack)) {
            return EnumActionResult.PASS;
        }

        boolean flag = false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IGrowable) {
            // Harvest a group of fully grown crops!
            final int radius = HARVEST_RANGE;
            BlockPos targetPos;
            Block block;

            for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z) {
                for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x) {
                    targetPos = new BlockPos(x, pos.getY(), z);
                    state = world.getBlockState(targetPos);
                    block = state.getBlock();

                    if (block instanceof IGrowable && !(block instanceof BlockDoublePlant)) {
                        IGrowable crop = (IGrowable) block;
                        if (!crop.canGrow(world, targetPos, state, world.isRemote)) {
                            // Fully grown crop, get the drops
                            NonNullList<ItemStack> drops = NonNullList.create();
                            int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
                            block.getDrops(drops, world, targetPos, state, fortune);

                            // Fire block harvesting event, mostly for soul gem drops.
                            ForgeEventFactory.fireBlockHarvesting(drops, world, targetPos, state, fortune, 1.0f,
                                    false, player);

                            // Spawn drops in world, remove first seed.
                            boolean foundSeed = false;
                            for (ItemStack dropStack : drops) {
                                if (!foundSeed && dropStack.getItem() instanceof IPlantable) {
                                    IPlantable seed = (IPlantable) dropStack.getItem();
                                    if (seed.getPlant(world, targetPos) == block.getDefaultState()) {
                                        foundSeed = true;
                                    }
                                } else {
                                    Block.spawnAsEntity(world, targetPos, dropStack);
                                }
                            }

                            // Reset to default state.
                            world.setBlockState(targetPos, block.getDefaultState(), 2);
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag) {
            ToolHelper.attemptDamageTool(stack, DURABILITY_USAGE, player);
            player.addExhaustion(0.02f);
        }
        return flag ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    public static boolean isEffectiveOnMaterial(Material material) {

        for (Material m : effectiveMaterials) {
            if (material == m) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack sickle, BlockPos pos, EntityPlayer player) {

        return onSickleStartBreak(sickle, pos, player, BREAK_RANGE);
    }

    public boolean onSickleStartBreak(ItemStack sickle, BlockPos pos, EntityPlayer player,
                                      int range) {

        if (ToolHelper.isBroken(sickle)) {
            return false;
        }

        World world = player.world;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!isEffectiveOnMaterial(state.getMaterial())) {
            ToolHelper.incrementStatBlocksMined(sickle, 1);
            return false;
        }

        int blocksBroken = 1;

        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();

        for (int xPos = x - range; xPos <= x + range; ++xPos) {
            for (int zPos = z - range; zPos <= z + range; ++zPos) {
                if ((xPos == x && zPos == z) || world.getBlockState(new BlockPos(xPos, y, zPos)) != state) {
                    continue;
                }

                if (breakExtraBlock(sickle, world, xPos, y, zPos, 0, player, x, y, z)) {
                    ++blocksBroken;
                }
            }
        }

        ToolHelper.incrementStatBlocksMined(sickle, blocksBroken);
        return super.onBlockStartBreak(sickle, pos, player);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos,
                                    EntityLivingBase entityLiving) {

        return ToolHelper.onBlockDestroyed(stack, world, state, pos, entityLiving);
    }

    private boolean breakExtraBlock(ItemStack sickle, World world, int x, int y, int z, int sideHit,
                                    EntityPlayer player, int refX, int refY, int refZ) {

        BlockPos pos = new BlockPos(x, y, z);

        if (world.isAirBlock(pos) || !(player instanceof EntityPlayerMP)) {
            return false;
        }

        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();

        boolean effectiveOnBlock = false;
        for (Material mat : effectiveMaterials) {
            if (mat == state.getMaterial()) {
                effectiveOnBlock = true;
                break;
            }
        }
        if (!effectiveOnBlock) {
            return false;
        }

        int xpDropped = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(),
                playerMP, pos);
        boolean canceled = xpDropped == -1;
        if (canceled) {
            return false;
        }

        if (playerMP.capabilities.isCreativeMode) {
            block.onBlockHarvested(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, playerMP, false)) {
                block.onPlayerDestroy(world, pos, state);
            }
            if (!world.isRemote) {
                playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
            }
            return true;
        }

        if (!world.isRemote) {
            block.onBlockHarvested(world, pos, state, playerMP);

            if (block.removedByPlayer(state, world, pos, playerMP, true)) {
                block.onPlayerDestroy(world, pos, state);
                block.harvestBlock(world, player, pos, state, null, sickle);
                block.dropXpOnBlockBreak(world, pos, xpDropped);
            }

            playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
        } else {
            int meta = block.getMetaFromState(state);
            world.playEvent(2001, pos, Block.getIdFromBlock(block) + (meta << 12));
            if (block.removedByPlayer(state, world, pos, playerMP, true)) {
                block.onPlayerDestroy(world, pos, state);
            }

            sickle.onBlockDestroyed(world, state, pos, playerMP);
        }

        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {

        return ToolHelper.getMaxDamage(stack);
    }

    // @Override
    // public int getColorFromItemStack(ItemStack stack, int pass) {
    //
    // return ToolRenderHelper.getInstance().getColorFromItemStack(stack, pass);
    // }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack,
                                               boolean slotChanged) {

        return ToolRenderHelper.instance.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {

        return ToolRenderHelper.instance.hasEffect(stack);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {

        return ToolRenderHelper.instance.getRarity(stack);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {

        return ToolHelper.getItemEnchantability(stack);
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot,
                         boolean isSelected) {

        ToolHelper.onUpdate(tool, world, entity, itemSlot, isSelected);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {

        return ToolHelper.onEntityItemUpdate(entityItem);
    }

    // ==================
    // ItemTool overrides
    // ==================

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {

        for (Material material : effectiveMaterials) {
            if (state.getMaterial() == material) {
                return this.efficiency;
            }
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean canHarvestBlock(IBlockState state) {

        return state.getMaterial() == Material.WEB;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, EntityPlayer player,
                               IBlockState state) {

        if (super.getHarvestLevel(stack, toolClass, player, state) < 0) {
            return 0;
        }
        return ToolHelper.getHarvestLevel(stack);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot,
                                                                     ItemStack stack) {

        return ToolHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entity1, EntityLivingBase entity2) {

        return ToolHelper.hitEntity(stack, entity1, entity2);
    }

    @Override
    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {

        return ToolHelper.getIsRepairable(stack1, stack2);
    }

    // ===============
    // IRegistryObject
    // ===============

    @Override
    public void addRecipes(RecipeMaker recipes) {

        if (!getConfig().isDisabled)
            ToolHelper.addExampleRecipe(this, " h", "hh", "r ");
    }

    @Override
    public void addOreDict() {

    }

    @Override
    public String getName() {

        return Names.SICKLE;
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

        models.put(0, ToolRenderHelper.SMART_MODEL);
    }

    @Override
    public boolean registerModels() {

        return false;
    }

    // =================================
    // Cross Compatibility (MC 10/11/12)
    // =================================

    @Override
    public void addInformation(ItemStack stack, World world, List list, ITooltipFlag flag) {

        ToolRenderHelper.getInstance().addInformation(stack, world, list, flag);
    }

    // getSubItems 1.10.2
    public void func_150895_a(Item item, CreativeTabs tab, List<ItemStack> list) {

        clGetSubItems(item, tab, list);
    }

    // getSubItems 1.11.2
    public void func_150895_a(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {

        clGetSubItems(item, tab, list);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {

        clGetSubItems(this, tab, list);
    }

    protected void clGetSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {

        if (!ItemHelper.isInCreativeTab(item, tab))
            return;

        list.addAll(ToolHelper.getSubItems(item, 3));
    }

    // onItemUse
    public EnumActionResult func_180614_a(ItemStack stack, EntityPlayer player, World world,
                                          BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

        return onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
    }
}
