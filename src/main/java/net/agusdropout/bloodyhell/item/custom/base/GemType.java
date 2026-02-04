package net.agusdropout.bloodyhell.item.custom.base;

import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum GemType {
    EMPTY("empty", 0xFFFFFF, Items.AIR, null, ""),

    // 1. DAMAGE (Ruby)
    // Range: +2.0 to +10.0 Damage.
    // Bias 2.5: High damage is significantly rarer.
    PURE("pure_blood_gem", 0xFF2222, ModItems.SANGUINITE.get(), ModItems.PURE_BLOOD_GEM.get(), "damage"),

    // 2. SIZE (Lapis)
    // Range: +10% to +50% Size.
    // Bias 1.5: Bigger projectiles are uncommon, but not impossible.
    AVENTURINE("aventurine_blood_gem", 0x0088ff, Items.LAPIS_LAZULI, ModItems.AVENTURINE_BLOOD_GEM.get(), "size"),

    // 3. QUANTITY (Gold)
    // Range: +1 to +3 Projectiles.
    // Bias 3.0: Getting +3 is VERY rare (Legendary roll).
    CITRINE("citrine_blood_gem", 0xffb700, Items.GOLD_NUGGET, ModItems.CITRINE_BLOOD_GEM.get(), "quantity"),

    // 4. DURATION/SPEED (Amethyst)
    // Range: +0.5s to +3.0s Duration (or Speed multiplier).
    // Bias 1.2: Fairly balanced distribution.
    TANZARINE("tanzarine_blood_gem", 0xa600ff, Items.AMETHYST_SHARD, ModItems.TANZARINE_BLOOD_GEM.get(), "duration");

    // --- CONFIGURABLE STAT RANGES ---
    // Damage
    private static final double MIN_DAMAGE = 2.0;
    private static final double MAX_DAMAGE = 10.0;

    // Size (Multiplier, e.g., 0.1 = +10%)
    private static final double MIN_SIZE_BONUS = 0.1;
    private static final double MAX_SIZE_BONUS = 0.5;

    // Quantity (Flat extra projectiles)
    private static final double MIN_QUANTITY_BONUS = 1.0;
    private static final double MAX_QUANTITY_BONUS = 3.0; // Set to 3 to make the "Rare" roll meaningful

    // Duration (Seconds or Multiplier)
    private static final double MIN_DURATION_BONUS = 0.5;
    private static final double MAX_DURATION_BONUS = 3.0;

    private final String name;
    private final int color;
    private final Item ingredient;
    private final Item resultGem;
    private final String bonusType;

    GemType(String name, int color, Item ingredient, Item resultGem, String bonusType) {
        this.name = name;
        this.color = color;
        this.ingredient = ingredient;
        this.resultGem = resultGem;
        this.bonusType = bonusType;
    }

    public static GemType fromItem(ItemStack stack) {
        for (GemType t : values()) {
            if (stack.is(t.ingredient)) return t;
        }
        return EMPTY;
    }

    // getters...
    public String getName() { return name; }
    public int getColor() { return color; }
    public Item getIngredient() { return ingredient; }
    public Item getResultGem() { return resultGem; }
    public String getBonusType() { return bonusType; }

    /**
     * Calculates the random stat.
     */
    public double getBonusStat(RandomSource random) {
        return switch (this.bonusType) {
            case "damage" -> {
                double val = rollRareStat(random, MIN_DAMAGE, MAX_DAMAGE, 2.5);
                // Rounds UP to the nearest 0.1 (e.g., 4.12 -> 4.2)
                yield Math.ceil(val * 10.0) / 10.0;
            }
            case "size" -> {
                double val = rollRareStat(random, MIN_SIZE_BONUS, MAX_SIZE_BONUS, 1.5);
                // Rounds UP to 2 decimal places (e.g., 0.153 -> 0.16)
                yield Math.ceil(val * 100.0) / 100.0;
            }
            case "quantity" -> {
                double val = rollRareStat(random, MIN_QUANTITY_BONUS, MAX_QUANTITY_BONUS, 3.0);
                // Rounds UP to the nearest whole number (e.g., 1.1 -> 2.0)
                yield Math.ceil(val);
            }
            case "duration" -> {
                double val = rollRareStat(random, MIN_DURATION_BONUS, MAX_DURATION_BONUS, 1.2);
                // Rounds UP to 1 decimal place (e.g., 1.52 -> 1.6)
                yield Math.ceil(val * 10.0) / 10.0;
            }
            default -> 0.0;
        };
    }

    /**
     * Generating "Rarity":
     * Uses a Power Function to skew results.
     * * @param min  The worst possible stat
     * @param max  The god-roll stat
     * @param bias Power curve.
     * 1.0 = Linear (Equal chance for all).
     * 2.0 = Quadratic (High stats are rare).
     * 3.0 = Cubic (High stats are LEGENDARY).
     */
    private double rollRareStat(RandomSource random, double min, double max, double bias) {
        double t = random.nextDouble();
        double weightedT = Math.pow(t, bias);
        return min + (max - min) * weightedT;
    }

    public static GemType byName(String name) {
        if (name == null || name.isEmpty()) return EMPTY;


        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {

            for (GemType gem : values()) {
                if (gem.name.equals(name)) {
                    return gem;
                }
            }

            return EMPTY;
        }
    }
    public static boolean hasValidStat(ItemStack stack) {
        for( GemType gem : values()) {
            if( stack.hasTag() && stack.getTag().contains(gem.getBonusType()) && gem != GemType.EMPTY){
                return true;
            };
        }
        return false;

    }

    public static String getStatfromStack(ItemStack stack) {
        for( GemType gem : values()) {
            if(stack.hasTag() && stack.getTag().contains(gem.getBonusType()) && gem != GemType.EMPTY){
                return gem.getBonusType();
            };
        }
        return "";
    }

    public static String getFormattedBonus(String statType, double value) {
        return switch (statType) {
            case "damage" -> "+" + value + " Damage";
            case "size" -> "+" + (int)(value * 100) + "% Size";
            case "quantity" -> "+" + (int)value + " Projectiles";
            case "duration" -> "+" + value + "s Duration";
            default -> "";
        };
    }

    public static ChatFormatting getChatFormating(String bonusType) {
        return switch (bonusType) {
            case  "damage" -> ChatFormatting.RED;
            case  "size" -> ChatFormatting.BLUE;
            case  "quantity" -> ChatFormatting.GOLD;
            case  "duration" -> ChatFormatting.LIGHT_PURPLE;
            default -> ChatFormatting.WHITE;
        };
    }
}