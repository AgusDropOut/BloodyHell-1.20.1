package net.agusdropout.bloodyhell.item;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.custom.*;
import net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText.AmuletOfAncestralBlood;
import net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText.BlasphemousRing;
import net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText.CrimsonWardRing;
import net.agusdropout.bloodyhell.item.custom.OnlyAppendHoverText.GreatAmuletOfAncestralBlood;
import net.agusdropout.bloodyhell.item.potions.BloodFlaskItem;
import net.agusdropout.bloodyhell.item.potions.BloodPotionItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BloodyHell.MODID);

    //Sanguinite Items
    public static final RegistryObject<Item> SANGUINITE = ITEMS.register("sanguinite", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> SANGUINITE_NUGGET = ITEMS.register("sanguinite_nugget", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> RAW_SANGUINITE = ITEMS.register("raw_sanguinite", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> SANGUINITE_SWORD = ITEMS.register("sanguinite_sword",() -> new SwordItem(ModToolTiers.SANGUINITE,3,-2.4F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINITE_PICKAXE = ITEMS.register("sanguinite_pickaxe",() -> new PickaxeItem(ModToolTiers.SANGUINITE,1,-2.8F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINITE_AXE = ITEMS.register("sanguinite_axe",() -> new AxeItem(ModToolTiers.SANGUINITE,5,-3F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINITE_HOE = ITEMS.register("sanguinite_hoe",() -> new HoeItem(ModToolTiers.SANGUINITE,0,0,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINITE_SHOVEL = ITEMS.register("sanguinite_shovel",() -> new ShovelItem(ModToolTiers.SANGUINITE,1,-3F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_BOW = ITEMS.register("blood_bow",() -> new ModBow(
            new Item.Properties().stacksTo(1).durability(500)));
    public static final RegistryObject<Item> BLOOD_SCYTHE = ITEMS.register("blood_scythe", () -> new BloodScytheItem(Tiers.NETHERITE, 10, 5f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_ARROW = ITEMS.register("blood_arrow", () -> new BloodArrowItem(
            new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_HELMET = ITEMS.register("blood_helmet",
            () -> new BloodArmorItem(ModArmorMaterials.BLOOD, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_CHESTPLATE = ITEMS.register("blood_chestplate",
            () -> new BloodArmorItem(ModArmorMaterials.BLOOD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_LEGGINGS = ITEMS.register("blood_leggings",
            () -> new BloodArmorItem(ModArmorMaterials.BLOOD, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_BOOTS = ITEMS.register("blood_boots",
            () -> new BloodArmorItem(ModArmorMaterials.BLOOD, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_BUCKET = ITEMS.register("blood_bucket", () -> new BucketItem(ModFluids.SOURCE_BLOOD,
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_BLOOD_BUCKET = ITEMS.register("rhnull_blood_bucket", () -> new BucketItem(ModFluids.SOURCE_RHNULL_BLOOD,
            new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));


    //Rhnull Items
    public static final RegistryObject<Item> RHNULL = ITEMS.register("rhnull", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_NUGGET = ITEMS.register("rhnull_nugget", () ->
            new Item( new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_SWORD = ITEMS.register("rhnull_sword",() -> new SoulItem(ModToolTiers.RHNULL,3,-2.4F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_PICKAXE = ITEMS.register("rhnull_pickaxe",() -> new PickaxeItem(ModToolTiers.RHNULL,1,-2.8F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_AXE = ITEMS.register("rhnull_axe",() -> new AxeItem(ModToolTiers.RHNULL,5,-3F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_HOE = ITEMS.register("rhnull_hoe",() -> new HoeItem(ModToolTiers.RHNULL,0,0,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RHNULL_SHOVEL = ITEMS.register("rhnull_shovel",() -> new ShovelItem(ModToolTiers.RHNULL,1,-3F,
            new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RHNULL_HELMET = ITEMS.register("rhnull_helmet",
            () -> new RhnullArmorItem(ModArmorMaterials.RHNULL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_CHESTPLATE = ITEMS.register("rhnull_chestplate",
            () -> new RhnullArmorItem(ModArmorMaterials.RHNULL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_LEGGINGS = ITEMS.register("rhnull_leggings",
            () -> new RhnullArmorItem(ModArmorMaterials.RHNULL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> RHNULL_BOOTS = ITEMS.register("rhnull_boots",
            () -> new RhnullArmorItem(ModArmorMaterials.RHNULL, ArmorItem.Type.BOOTS, new Item.Properties()));

    //Food Items
    public static final RegistryObject<Item> GLOW_MUSHROOM = ITEMS.register("glow_mushroom", () -> new BlockItem( ModBlocks.LIGHT_MUSHROOM_BLOCK.get(),new Item.Properties().stacksTo(64).food(new FoodProperties.Builder().nutrition(3)
            .effect(
                    () -> new MobEffectInstance(MobEffects.GLOWING,200,0),1)
            .effect(
                    () -> new MobEffectInstance(MobEffects.CONFUSION,200,0),1
            ).build())));
    public static final RegistryObject<Item> GLOW_FRUIT = ITEMS.register("glow_fruit", () -> new BlockItem( ModBlocks.DROOPVINE.get(),new Item.Properties().stacksTo(64).food(new FoodProperties.Builder().nutrition(3)
            .effect(
                    () -> new MobEffectInstance(MobEffects.GLOWING,200,0),1)
            .effect(
                    () -> new MobEffectInstance(MobEffects.CONFUSION,200,0),1
            ).build())));
    public static final RegistryObject<Item> Eyeball = ITEMS.register("eyeball", () ->
            new Item(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(3).saturationMod(2f)
                            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 6000, 0), 1.0f)
                            .build())
            )
    );
    public static final RegistryObject<Item> SCARLET_RAW_CHICKEN = ITEMS.register("scarlet_raw_chicken", () ->
            new Item(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(2).saturationMod(2f)
                            .build())
            )
    );
    public static final RegistryObject<Item> SCARLET_COOKED_CHICKEN = ITEMS.register("scarlet_cooked_chicken", () ->
            new Item(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(4).saturationMod(2f)
                            .build())
            )
    );
    public static final RegistryObject<Item> GOREHOG_RAW_STEAK = ITEMS.register("gorehog_raw_steak", () ->
            new Item(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(2).saturationMod(2f)
                            .build())
            )
    );
    public static final RegistryObject<Item> GOREHOG_COOKED_STEAK = ITEMS.register("gorehog_cooked_steak", () ->
            new Item(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(5).saturationMod(2f)
                            .build())
            )
    );



    //Spawn eggs
    public static final RegistryObject<Item> BLOODTHIRSTYBEAST_SPAWN_EGG = ITEMS.register("bloodthirstybeast_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.BLOODTHIRSTYBEAST,0x400303,0x00FFA0,
            new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_SEEKER_SPAWN_EGG = ITEMS.register("bloodseeker_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.BLOOD_SEEKER,0x660000 ,0xFFDD00,
            new Item.Properties()));
    public static final RegistryObject<Item> EYESHELLSNAIL_SPAWN_EGG = ITEMS.register("eyeshellsnail_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.EYESHELL_SNAIL,0xFF0000  ,0xFFB400 ,
            new Item.Properties()));
    public static final RegistryObject<Item> CRIMSON_RAVEN_SPAWN_EGG = ITEMS.register("crimsonraven_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.CRIMSON_RAVEN,0xFF3434 ,0xFCFF00,
            new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_PIG_SPAWN_EGG = ITEMS.register("bloodpig_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.BLOODPIG,0x000000 ,0xDA0000,
            new Item.Properties()));
    public static final RegistryObject<Item> SCARLETSPECKLED_FISH_SPAWN_EGG = ITEMS.register("scarletspeckledfish_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.SCARLETSPECKLED_FISH,0xFF3434 ,0xFCFF00,
            new Item.Properties()));
    public static final RegistryObject<Item> OMEN_GAZER_ENTITY_SPAWN_EGG = ITEMS.register("omen_gazer_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.OMEN_GAZER_ENTITY,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> VEINRAVER_ENTITY_SPAWN_EGG = ITEMS.register("veinraver_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.VEINRAVER_ENTITY,0xfc2403 ,0xfcd303,
            new Item.Properties()));
    public static final RegistryObject<Item> OFFSPRING_OF_THE_UNKNOWN_SPAWN_EGG = ITEMS.register("offspring_of_the_unknown_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.OFFSPRING_OF_THE_UNKNOWN,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> BLASPHEMOUS_MALFORMATION_SPAWN_EGG = ITEMS.register("blasphemous_malformation_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.BLASPHEMOUS_MALFORMATION,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> SELIORA_SPAWN_EGG = ITEMS.register("seliora_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.SELIORA,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> HORNED_WORM_SPAWN_EGG = ITEMS.register("horned_worm_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.HORNED_WORM,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> VEIL_STALKER_SPAWN_EGG = ITEMS.register("veil_stalker_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.VEIL_STALKER,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> CYCLOPS_ENTITY_SPAWN_EGG = ITEMS.register("cyclops_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.CYCLOPS_ENTITY,0x000000 ,0xd4a600,
            new Item.Properties()));
    public static final RegistryObject<Item> GRAVE_WALKER_SPAWN_EGG = ITEMS.register("grave_walker_entity_spawn_egg",() -> new ForgeSpawnEggItem(ModEntityTypes.GRAVE_WALKER_ENTITY,0x000000 ,0xd4a600,
            new Item.Properties()));


    //Mobs Drops
    public static final RegistryObject<Item> VEINREAVER_HORN = ITEMS.register("veinreaver_horn", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> AUREAL_REVENANT_DAGGER = ITEMS.register("aureal_revenant_dagger", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> CRIMSON_SHELL = ITEMS.register("crimson_shell", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> SCARLET_FEATHER = ITEMS.register("scarlet_feather", () -> new Item(
            new Item.Properties()));

    //Misc Items
    public static final RegistryObject<Item> Eight_ball = ITEMS.register("eight_ball", () -> new EightBallItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOODY_SOUL_DUST = ITEMS.register("bloody_soul_dust", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> Eyeball_seed = ITEMS.register("eyeball_seed", () -> new ItemNameBlockItem(ModBlocks.EYEBALL_CROP.get(),
            new Item.Properties()));
    public static final RegistryObject<Item> MATERIALIZED_SOUL = ITEMS.register("materialized_soul", () -> new CatalystItem());
    public static final RegistryObject<Item> CHALICE_OF_THE_DAMMED = ITEMS.register("chalice_of_the_dammed", () -> new CatalystItem());
    public static final RegistryObject<Item> CRIMSON_IDOL_COIN = ITEMS.register("crimson_idol_coin", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANGUINE_CRUCIBLE_CORE = ITEMS.register("sanguine_crucible_core", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIRTY_BLOOD_FLOWER = ITEMS.register("dirty_blood_flower", () -> new Item(
            new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_LILY = ITEMS.register("blood_lily", () -> new PlaceOnWaterBlockItem(
            ModBlocks.BLOOD_LILY_BLOCK.get(),new Item.Properties()));

    // Animated Items
    public static final RegistryObject<Item> BLOOD_ALTAR = ITEMS.register("blood_altar", () -> new BloodAltarItem(
            ModBlocks.BLOOD_ALTAR.get(),new Item.Properties()));
    public static final RegistryObject<Item> MAIN_BLOOD_ALTAR = ITEMS.register("main_blood_altar", () -> new MainBloodAltarItem(
            ModBlocks.MAIN_BLOOD_ALTAR.get(),new Item.Properties()));

    //Flasks
    public static final RegistryObject<Item> BLOOD_FLASK = ITEMS.register("blood_flask", () -> new BloodFlaskItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CORRUPTED_BLOOD_FLASK = ITEMS.register("corrupted_blood_flask", () -> new BloodPotionItem(
            new Item.Properties().stacksTo(1),new MobEffectInstance(ModEffects.BLOOD_LUST.get(), 6000, 0)));
    public static final RegistryObject<Item> FILLED_BLOOD_FLASK = ITEMS.register("filled_blood_flask", () -> new BloodPotionItem(
            new Item.Properties().stacksTo(1), new MobEffectInstance(ModEffects.BLOOD_LUST.get(), 6000, 0)));
    public static final RegistryObject<Item> FILLED_RHNULL_BLOOD_FLASK = ITEMS.register("filled_rhnull_blood_flask", () -> new BloodPotionItem(
            new Item.Properties().stacksTo(1), new MobEffectInstance(ModEffects.BLOOD_LUST.get(), 6000, 0)));



    //Dagger
    public static final RegistryObject<Item> SACRIFICIAL_DAGGER = ITEMS.register("sacrificial_dagger",() -> new SacrificialDagger(Tiers.WOOD,1,5,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HERETIC_SACRIFICIAL_DAGGER = ITEMS.register("heretic_sacrificial_dagger",() -> new HereticSacrificialDagger(Tiers.IRON,1,5,
            new Item.Properties().stacksTo(1)));

    //Unknown Entity
    public static final RegistryObject<Item> UNKNOWN_ENTITY_FINGER = ITEMS.register("unknown_entity_finger", () -> new Item(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMOUS_TWIN_DAGGERS = ITEMS.register("blasphemous_twin_daggers", () -> new BlasphemousTwinDaggerItem(ModToolTiers.RHNULL,10,-2.4F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMOUS_HULKING_MASS_OF_IRON = ITEMS.register("blasphemous_hulking_mass_of_iron", () -> new BlasphemousHulkingMassOfIronItem(ModToolTiers.RHNULL,10,-2.4F,
            new Item.Properties().stacksTo(1)));


    //Spell Books
    public static final RegistryObject<Item> BLOOD_SPELL_BOOK_SCRATCH = ITEMS.register("blood_spell_book_scratch", () -> new BloodSpellBookScratchItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_SPELL_BOOK_BLOODBALL = ITEMS.register("blood_spell_book_bloodball", () -> new BloodSpellBookBloodBallItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_SPELL_BOOK_BLOODNOVA = ITEMS.register("blood_spell_book_bloodnova", () -> new BloodSpellBookBloodNovaItem(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_SPELL_BOOK_DAGGERSRAIN = ITEMS.register("blood_spell_book_daggersrain", () -> new BloodSpellBookDaggersRainItem(
            new Item.Properties().stacksTo(1)));

    //Crimson Veil Items
    public static final RegistryObject<Item> AMULET_OF_ANCESTRAL_BLOOD = ITEMS.register("amulet_of_ancestral_blood", () -> new AmuletOfAncestralBlood(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ANCIENT_GEM = ITEMS.register("ancient_gem", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> GREAT_AMULET_OF_ANCESTRAL_BLOOD = ITEMS.register("great_amulet_of_ancestral_blood", () -> new GreatAmuletOfAncestralBlood(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GREAT_ANCIENT_GEM = ITEMS.register("great_ancient_gem", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> CRIMSON_WARD_RING = ITEMS.register("crimson_ward_ring", () -> new CrimsonWardRing(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMOUS_RING = ITEMS.register("blasphemous_ring", () -> new BlasphemousRing(
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE = ITEMS.register("blasphemite", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> RAW_BLASPHEMITE = ITEMS.register("raw_blasphemite", () -> new Item(
            new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> BLASPHEMITE_NUGGET = ITEMS.register("blasphemite_nugget", () ->
            new Item( new Item.Properties()));

    public static final RegistryObject<Item> BLASPHEMITE_SWORD = ITEMS.register("blasphemite_sword",() -> new SwordItem(ModToolTiers.BLASPHEMITE,3,-2.4F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE_PICKAXE = ITEMS.register("blasphemite_pickaxe",() -> new PickaxeItem(ModToolTiers.BLASPHEMITE,1,-2.8F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE_AXE = ITEMS.register("blasphemite_axe",() -> new AxeItem(ModToolTiers.BLASPHEMITE,5,-3F,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE_HOE = ITEMS.register("blasphemite_hoe",() -> new HoeItem(ModToolTiers.BLASPHEMITE,0,0,
            new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLASPHEMITE_SHOVEL = ITEMS.register("blasphemite_shovel",() -> new ShovelItem(ModToolTiers.BLASPHEMITE,1,-3F,
            new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLASPHEMITE_HELMET = ITEMS.register("blasphemite_helmet",
            () -> new BlasphemiteArmorItem(ModArmorMaterials.BLASPHEMITE, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> BLASPHEMITE_CHESTPLATE = ITEMS.register("blasphemite_chestplate",
            () -> new BlasphemiteArmorItem(ModArmorMaterials.BLASPHEMITE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> BLASPHEMITE_LEGGINGS = ITEMS.register("blasphemite_leggings",
            () -> new BlasphemiteArmorItem(ModArmorMaterials.BLASPHEMITE, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> BLASPHEMITE_BOOTS = ITEMS.register("blasphemite_boots",
            () -> new BlasphemiteArmorItem(ModArmorMaterials.BLASPHEMITE, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<Item> BLASPHEMOUS_IMPALER = ITEMS.register("blasphemous_impaler",
            () -> new BlasphemousImpalerItem(
                    ModToolTiers.BLASPHEMITE,
                    10,
                    -3.0F,
                    new Item.Properties().stacksTo(0)
            ));
    public static final RegistryObject<Item> SELIORA_ESSENCE = ITEMS.register("seliora_essence",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
