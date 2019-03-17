package net.silentchaos512.gems.lib.soul;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.utils.MathUtils;

import java.util.Random;

public enum SoulElement {
    NONE(1, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0xFFFFFF),
    FIRE(13, 0.00f, 0.00f, +0.15f, 0.00f, -0.05f, 0xF48C42),
    WATER(12, 0.00f, 0.00f, -0.05f, +0.15f, 0.00f, 0x4189F4),
    EARTH(11, +0.15f, 0.00f, 0.00f, -0.05f, 0.00f, 0x1FC121),
    WIND(10, 0.00f, +0.15f, -0.05f, 0.00f, 0.00f, 0x83F7C1),
    METAL(18, +0.20f, 0.00f, 0.00f, -0.10f, +0.10f, 0xAAAAAA),
    ICE(17, -0.05f, 0.00f, 0.00f, +0.20f, -0.05f, 0x8EFFEC),
    LIGHTNING(16, -0.05f, +0.10f, +0.10f, +0.05f, -0.10f, 0xFFFF47),
    VENOM(15, +0.10f, -0.15f, +0.15f, 0.00f, 0.00f, 0x83C14D),
    FLORA(5, -0.05f, 0.00f, -0.10f, +0.10f, 0.00f, 0x277C2F),
    FAUNA(6, 0.00f, 0.00f, +0.10f, -0.10f, -0.05f, 0xFFA3D7),
    MONSTER(7, +0.10f, -0.05f, 0.00f, -0.10f, 0.00f, 0x635538),
    ALIEN(8, 0.00f, -0.10f, 0.00f, +0.15f, +0.05f, 0x8E42A5);

    public final int weight;
    public final float durabilityModifier;
    public final float harvestSpeedModifier;
    public final float meleeDamageModifier;
    public final float magicDamageModifier;
    public final float protectionModifier;
    public final int color;

    SoulElement(int weight, float durability, float harvestSpeed, float meleeDamage, float magicDamage, float protection, int color) {
        this.weight = weight;
        this.durabilityModifier = durability;
        this.harvestSpeedModifier = harvestSpeed;
        this.meleeDamageModifier = meleeDamage;
        this.magicDamageModifier = magicDamage;
        this.protectionModifier = protection;
        this.color = color;
    }

    public ITextComponent getDisplayName() {
        if (this == NONE)
            return new TextComponentTranslation("soul.silentgems.element.none");
        return new TextComponentTranslation("soul.silentgems.element." + this.ordinal());
    }

    public static SoulElement selectRandom(Random random) {
        return selectRandom(random, 0);
    }

    public static SoulElement selectRandom(Random random, float chanceOfNone) {
        if (MathUtils.tryPercentage(random, chanceOfNone))
            return NONE;
        return values()[MathUtils.nextIntInclusive(random, 1, SoulElement.values().length - 1)];
    }
}