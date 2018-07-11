package net.silentchaos512.gems.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gems.tile.TileChaosFlowerPot;
import net.silentchaos512.lib.client.render.BufferBuilderSL;
import net.silentchaos512.lib.client.render.tileentity.TileEntitySpecialRendererSL;
import net.silentchaos512.lib.util.StackHelper;

public class RenderTileChaosFlowerPot extends TileEntitySpecialRendererSL<TileChaosFlowerPot> {

  private static final double F = 0.4 * (1 - Math.sqrt(2) / 2);

  private ResourceLocation[] TEXTURES = new ResourceLocation[16];

  public RenderTileChaosFlowerPot() {

    for (int i = 0; i < TEXTURES.length; ++i) {
      TEXTURES[i] = new ResourceLocation("silentgems:textures/blocks/glowrose" + i + ".png");
    }
  }

  @Override
  public void clRender(TileChaosFlowerPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

    Tessellator tess = Tessellator.getInstance();
    BufferBuilderSL buff = BufferBuilderSL.INSTANCE.acquireBuffer(tess);

    int flowerId = te.getFlowerId();
    if (flowerId < 0 || flowerId >= TEXTURES.length) {
      return;
    }

    Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURES[flowerId]);

    double x1 = x + F;
    double x2 = x + 1 - F;
    double y1 = y + F;
    double y2 = y + 1.15 - F;
    double z1 = z + F;
    double z2 = z + 1 - F;

    GlStateManager.pushMatrix();
    GlStateManager.disableLighting();

    buff.begin(7, DefaultVertexFormats.POSITION_TEX);
    buff.pos(x1, y2, z1).tex(0, 0).endVertex();
    buff.pos(x2, y2, z2).tex(1, 0).endVertex();
    buff.pos(x2, y1, z2).tex(1, 1).endVertex();
    buff.pos(x1, y1, z1).tex(0, 1).endVertex();
    tess.draw();

    buff.begin(7, DefaultVertexFormats.POSITION_TEX);
    buff.pos(x1, y1, z1).tex(0, 1).endVertex();
    buff.pos(x2, y1, z2).tex(1, 1).endVertex();
    buff.pos(x2, y2, z2).tex(1, 0).endVertex();
    buff.pos(x1, y2, z1).tex(0, 0).endVertex();
    tess.draw();

    buff.begin(7, DefaultVertexFormats.POSITION_TEX);
    buff.pos(x2, y2, z1).tex(0, 0).endVertex();
    buff.pos(x1, y2, z2).tex(1, 0).endVertex();
    buff.pos(x1, y1, z2).tex(1, 1).endVertex();
    buff.pos(x2, y1, z1).tex(0, 1).endVertex();
    tess.draw();

    buff.begin(7, DefaultVertexFormats.POSITION_TEX);
    buff.pos(x2, y1, z1).tex(0, 1).endVertex();
    buff.pos(x1, y1, z2).tex(1, 1).endVertex();
    buff.pos(x1, y2, z2).tex(1, 0).endVertex();
    buff.pos(x2, y2, z1).tex(0, 0).endVertex();
    tess.draw();

    GlStateManager.popMatrix();
  }
}
