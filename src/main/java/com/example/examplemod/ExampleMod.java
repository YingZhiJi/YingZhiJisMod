package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.entity.player.Player;


@Mod(ExampleMod.MODID)
public class ExampleMod
{
    public static final String MODID = "yingzhiji_s_mod";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> REPAIR_KIT = ITEMS.register("repair_kit",
            () -> new RepairKitItem(new Item.Properties().durability(1250)));

    public static final RegistryObject<Item> IRON_ARROW = ITEMS.register("iron_arrow",
            () -> new IronArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> IRON_SPECTRAL_ARROW = ITEMS.register("iron_spectral_arrow",
            () -> new IronSpectralArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> IRON_BURNING_ARROW = ITEMS.register("iron_burning_arrow",
            () -> new IronBurningArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> IRON_POISON_ARROW = ITEMS.register("iron_poison_arrow",
            () -> new IronPoisonArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> IRON_COOLING_ARROW = ITEMS.register("iron_cooling_arrow",
            () -> new IronCoolingArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> IRON_FRAGMENT_ARROW = ITEMS.register("iron_fragment_arrow",
            () -> new IronFragmentArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> DENSE_ARROW = ITEMS.register("dense_arrow",
            () -> new DenseArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> DENSE_PIERCING_ARROW = ITEMS.register("dense_piercing_arrow",
            () -> new DensePiercingArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> DENSE_OVERPRESSURE_ARROW = ITEMS.register("dense_overpressure_arrow",
            () -> new DenseOverpressureArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> DENSE_DECAYING_ARROW = ITEMS.register("dense_decaying_arrow",
            () -> new DenseDecayingArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> DENSE_DEEPSCULK_ARROW = ITEMS.register("dense_deepsculk_arrow",
            () -> new DenseDeepSculkArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> DENSE_APOCALYPSE_ARROW = ITEMS.register("dense_apocalypse_arrow",
            () -> new DenseApocalypseArrowItem(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> IRON_BOW = ITEMS.register("iron_bow",
            () -> new IronBowItem(new Item.Properties().durability(640)));

    public static final RegistryObject<Item> OLD_SABER = ITEMS.register("old_saber",
            () -> new OldSaberItem(new Item.Properties()));

    public static final RegistryObject<Item> OLD_SABER_SHEATH = ITEMS.register("old_saber_sheath",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SWORD_BOX = ITEMS.register("sword_box",
            () -> new SwordBoxItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> ASHBLAZITE_ORE_ITEM = ITEMS.register("ashblazite_ore",
            () -> new BlockItem(ModBlocks.ASHBLAZITE_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ASHBLAZITE_DEEPSLATE_ORE_ITEM = ITEMS.register("ashblazite_deepslate_ore",
            () -> new BlockItem(ModBlocks.ASHBLAZITE_DEEPSLATE_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ASHBLAZITE_BLOCK_ITEM = ITEMS.register("ashblazite_block",
            () -> new BlockItem(ModBlocks.ASHBLAZITE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTALLIZER_ITEM = ITEMS.register("crystallizer",
            () -> new BlockItem(ModBlocks.CRYSTALLIZER.get(), new Item.Properties()));

    // ===== 斩剑系列 =====
    public static final RegistryObject<Item> WOODEN_SLASH_SWORD = ITEMS.register("wooden_slash_sword",
            () -> new SlashSwordItem(net.minecraft.world.item.Tiers.WOOD, new Item.Properties()));

    public static final RegistryObject<Item> STONE_SLASH_SWORD = ITEMS.register("stone_slash_sword",
            () -> new SlashSwordItem(net.minecraft.world.item.Tiers.STONE, new Item.Properties()));

    public static final RegistryObject<Item> IRON_SLASH_SWORD = ITEMS.register("iron_slash_sword",
            () -> new SlashSwordItem(net.minecraft.world.item.Tiers.IRON, new Item.Properties()));

    public static final RegistryObject<Item> GOLDEN_SLASH_SWORD = ITEMS.register("golden_slash_sword",
            () -> new SlashSwordItem(net.minecraft.world.item.Tiers.GOLD, new Item.Properties()));

    public static final RegistryObject<Item> DIAMOND_SLASH_SWORD = ITEMS.register("diamond_slash_sword",
            () -> new SlashSwordItem(net.minecraft.world.item.Tiers.DIAMOND, new Item.Properties()));

    public static final RegistryObject<Item> NETHERITE_SLASH_SWORD = ITEMS.register("netherite_slash_sword",
            () -> new SlashSwordItem(net.minecraft.world.item.Tiers.NETHERITE,
                    new Item.Properties().fireResistant()));

    // ===== 重镐系列 =====
    public static final RegistryObject<Item> WOODEN_HEAVY_PICKAXE = ITEMS.register("wooden_heavy_pickaxe",
            () -> new HeavyPickaxeItem(net.minecraft.world.item.Tiers.WOOD, new Item.Properties()));

    public static final RegistryObject<Item> STONE_HEAVY_PICKAXE = ITEMS.register("stone_heavy_pickaxe",
            () -> new HeavyPickaxeItem(net.minecraft.world.item.Tiers.STONE, new Item.Properties()));

    public static final RegistryObject<Item> IRON_HEAVY_PICKAXE = ITEMS.register("iron_heavy_pickaxe",
            () -> new HeavyPickaxeItem(net.minecraft.world.item.Tiers.IRON, new Item.Properties()));

    public static final RegistryObject<Item> GOLDEN_HEAVY_PICKAXE = ITEMS.register("golden_heavy_pickaxe",
            () -> new HeavyPickaxeItem(net.minecraft.world.item.Tiers.GOLD, new Item.Properties()));

    public static final RegistryObject<Item> DIAMOND_HEAVY_PICKAXE = ITEMS.register("diamond_heavy_pickaxe",
            () -> new HeavyPickaxeItem(net.minecraft.world.item.Tiers.DIAMOND, new Item.Properties()));

    public static final RegistryObject<Item> NETHERITE_HEAVY_PICKAXE = ITEMS.register("netherite_heavy_pickaxe",
            () -> new HeavyPickaxeItem(net.minecraft.world.item.Tiers.NETHERITE,
                    new Item.Properties().fireResistant()));

    // ===== 燃灰工具武器 =====
    public static final RegistryObject<Item> ASHBLAZITE_SWORD = ITEMS.register("ashblazite_sword",
            () -> new SwordItem(ModTiers.ASHBLAZITE, 3, -2.4F, new Item.Properties()));

    public static final RegistryObject<Item> ASHBLAZITE_PICKAXE = ITEMS.register("ashblazite_pickaxe",
            () -> new PickaxeItem(ModTiers.ASHBLAZITE, 1, -2.8F, new Item.Properties()));

    public static final RegistryObject<Item> ASHBLAZITE_AXE = ITEMS.register("ashblazite_axe",
            () -> new AxeItem(ModTiers.ASHBLAZITE, 5.0F, -3.0F, new Item.Properties()));

    public static final RegistryObject<Item> ASHBLAZITE_SHOVEL = ITEMS.register("ashblazite_shovel",
            () -> new ShovelItem(ModTiers.ASHBLAZITE, 1.5F, -3.0F, new Item.Properties()));

    public static final RegistryObject<Item> ASHBLAZITE_HOE = ITEMS.register("ashblazite_hoe",
            () -> new HoeItem(ModTiers.ASHBLAZITE, -4, 0.0F, new Item.Properties()));

    public static final RegistryObject<Item> ASHBLAZITE_SLASH_SWORD = ITEMS.register("ashblazite_slash_sword",
            () -> new SlashSwordItem(ModTiers.ASHBLAZITE, new Item.Properties()));

    public static final RegistryObject<Item> ASHBLAZITE_HEAVY_PICKAXE = ITEMS.register("ashblazite_heavy_pickaxe",
            () -> new HeavyPickaxeItem(ModTiers.ASHBLAZITE, new Item.Properties()));

    // ===== 末地闪光 =====
    public static final RegistryObject<Item> END_FLASH = ITEMS.register("end_flash",
            () -> new EndFlashItem(new Item.Properties().fireResistant().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> MOBILE_SHIELD = ITEMS.register("mobile_shield",
            () -> new MobileShieldItem(new Item.Properties().durability(2000)));

    public static final RegistryObject<Item> ABYSSAL_EYE = ITEMS.register("abyssal_eye",
            () -> new AbyssalEyeItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> DESTINY_CRYSTAL = ITEMS.register("destiny_crystal",
            () -> new DestinyCrystalItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> MAIDEN_HONEY_POTION = ITEMS.register("maiden_honey_potion",
            () -> new MaidenHoneyPotionItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> MUSIC_DISC_INITIAL_PLAY = ITEMS.register("music_disc_initial_play",
            () -> new net.minecraft.world.item.RecordItem(15,
                    () -> ModSounds.MUSIC_DISC_INITIAL_PLAY.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4500));


    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES, MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<RecipeSerializer<RepairKitRecipe>> REPAIR_KIT_SERIALIZER =
            RECIPE_SERIALIZERS.register("repair_kit", () -> RepairKitRecipeSerializer.INSTANCE);

    public static final RegistryObject<EntityType<IronArrow>> IRON_ARROW_ENTITY =
            ENTITY_TYPES.register("iron_arrow",
                    () -> EntityType.Builder.<IronArrow>of(IronArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .build("iron_arrow"));

    public static final RegistryObject<EntityType<IronSpectralArrow>> IRON_SPECTRAL_ARROW_ENTITY =
            ENTITY_TYPES.register("iron_spectral_arrow",
                    () -> EntityType.Builder.<IronSpectralArrow>of(IronSpectralArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .build("iron_spectral_arrow"));

    public static final RegistryObject<EntityType<IronBurningArrow>> IRON_BURNING_ARROW_ENTITY =
            ENTITY_TYPES.register("iron_burning_arrow",
                    () -> EntityType.Builder.<IronBurningArrow>of(IronBurningArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .build("iron_burning_arrow"));

    public static final RegistryObject<EntityType<IronPoisonArrow>> IRON_POISON_ARROW_ENTITY =
            ENTITY_TYPES.register("iron_poison_arrow",
                    () -> EntityType.Builder.<IronPoisonArrow>of(IronPoisonArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("iron_poison_arrow"));

    public static final RegistryObject<EntityType<IronCoolingArrow>> IRON_COOLING_ARROW_ENTITY =
            ENTITY_TYPES.register("iron_cooling_arrow",
                    () -> EntityType.Builder.<IronCoolingArrow>of(IronCoolingArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("iron_cooling_arrow"));

    public static final RegistryObject<EntityType<IronFragmentArrow>> IRON_FRAGMENT_ARROW_ENTITY =
            ENTITY_TYPES.register("iron_fragment_arrow",
                    () -> EntityType.Builder.<IronFragmentArrow>of(IronFragmentArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("iron_fragment_arrow"));

    public static final RegistryObject<EntityType<DenseArrow>> DENSE_ARROW_ENTITY =
            ENTITY_TYPES.register("dense_arrow",
                    () -> EntityType.Builder.<DenseArrow>of(DenseArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("dense_arrow"));

    public static final RegistryObject<EntityType<DensePiercingArrow>> DENSE_PIERCING_ARROW_ENTITY =
            ENTITY_TYPES.register("dense_piercing_arrow",
                    () -> EntityType.Builder.<DensePiercingArrow>of(DensePiercingArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("dense_piercing_arrow"));

    public static final RegistryObject<EntityType<DenseOverpressureArrow>> DENSE_OVERPRESSURE_ARROW_ENTITY =
            ENTITY_TYPES.register("dense_overpressure_arrow",
                    () -> EntityType.Builder.<DenseOverpressureArrow>of(DenseOverpressureArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("dense_overpressure_arrow"));

    public static final RegistryObject<EntityType<DenseDecayingArrow>> DENSE_DECAYING_ARROW_ENTITY =
            ENTITY_TYPES.register("dense_decaying_arrow",
                    () -> EntityType.Builder.<DenseDecayingArrow>of(DenseDecayingArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("dense_decaying_arrow"));

    public static final RegistryObject<EntityType<DenseDeepSculkArrow>> DENSE_DEEPSCULK_ARROW_ENTITY =
            ENTITY_TYPES.register("dense_deepsculk_arrow",
                    () -> EntityType.Builder.<DenseDeepSculkArrow>of(DenseDeepSculkArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("dense_deepsculk_arrow"));

    public static final RegistryObject<EntityType<DenseApocalypseArrow>> DENSE_APOCALYPSE_ARROW_ENTITY =
            ENTITY_TYPES.register("dense_apocalypse_arrow",
                    () -> EntityType.Builder.<DenseApocalypseArrow>of(DenseApocalypseArrow::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("dense_apocalypse_arrow"));

    public static final RegistryObject<EntityType<ApocalypseCloudEntity>> APOCALYPSE_CLOUD_ENTITY =
            ENTITY_TYPES.register("apocalypse_cloud",
                    () -> EntityType.Builder.<ApocalypseCloudEntity>of(ApocalypseCloudEntity::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.1F, 0.1F).clientTrackingRange(10).updateInterval(1).build("apocalypse_cloud"));

    public static final RegistryObject<EntityType<ApocalypseAnimationEntity>> APOCALYPSE_ANIMATION_ENTITY =
            ENTITY_TYPES.register("apocalypse_animation",
                    () -> EntityType.Builder.<ApocalypseAnimationEntity>of(ApocalypseAnimationEntity::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.1F, 0.1F).clientTrackingRange(64).updateInterval(1).build("apocalypse_animation"));

    public static final RegistryObject<EntityType<DecayingSoulEntity>> DECAYING_SOUL_ENTITY =
            ENTITY_TYPES.register("decaying_soul",
                    () -> EntityType.Builder.<DecayingSoulEntity>of(DecayingSoulEntity::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).build("decaying_soul"));

    public static final RegistryObject<EntityType<DecayingCloudEntity>> DECAYING_CLOUD_ENTITY =
            ENTITY_TYPES.register("decaying_cloud",
                    () -> EntityType.Builder.<DecayingCloudEntity>of(DecayingCloudEntity::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).build("decaying_cloud"));

    public static final RegistryObject<EntityType<ZomperiumCommander>> ZOMPERIUM_COMMANDER_ENTITY =
            ENTITY_TYPES.register("zomperium_commander",
                    () -> EntityType.Builder.<ZomperiumCommander>of(ZomperiumCommander::new, net.minecraft.world.entity.MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("zomperium_commander"));

    public static final RegistryObject<EntityType<ZomperiumSummonerEntity>> ZOMPERIUM_SUMMONER_ENTITY =
            ENTITY_TYPES.register("zomperium_summoner",
                    () -> EntityType.Builder.<ZomperiumSummonerEntity>of(ZomperiumSummonerEntity::new, net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .clientTrackingRange(32)
                            .updateInterval(1)
                            .build("zomperium_summoner"));

    public static final RegistryObject<EntityType<FlyingZombieHeadEntity>> FLYING_ZOMBIE_HEAD_ENTITY =
            ENTITY_TYPES.register("flying_zombie_head",
                    () -> EntityType.Builder.<FlyingZombieHeadEntity>of(
                                    FlyingZombieHeadEntity::new,
                                    net.minecraft.world.entity.MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .build("flying_zombie_head"));

    public static final RegistryObject<EntityType<ZomperiumPrivate>> ZOMPERIUM_PRIVATE_ENTITY =
            ENTITY_TYPES.register("zomperium_private",
                    () -> EntityType.Builder.<ZomperiumPrivate>of(ZomperiumPrivate::new, net.minecraft.world.entity.MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("zomperium_private"));

    public static final RegistryObject<EntityType<ZomperiumScout>> ZOMPERIUM_SCOUT_ENTITY =
            ENTITY_TYPES.register("zomperium_scout",
                    () -> EntityType.Builder.<ZomperiumScout>of(ZomperiumScout::new, net.minecraft.world.entity.MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("zomperium_scout"));

    public static final RegistryObject<EntityType<LibrarianEntity>> LIBRARIAN_ENTITY =
            ENTITY_TYPES.register("librarian",
                    () -> EntityType.Builder.<LibrarianEntity>of(LibrarianEntity::new,
                                    net.minecraft.world.entity.MobCategory.CREATURE)
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(10)
                            .build("librarian"));

    public static final RegistryObject<Item> LIBRARIAN_SPAWN_EGG = ITEMS.register("librarian_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    LIBRARIAN_ENTITY,
                    0x8B5A2B,
                    0xFFF8DC,
                    new Item.Properties()));


    public static final RegistryObject<Item> ZOMPERIUM_HANDSET_RADIO = ITEMS.register("zomperium_handset_radio",
            () -> new ZomperiumHandsetRadioItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ZOMPERIUM_COMMANDER_SPAWN_EGG = ITEMS.register("zomperium_commander_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ZOMPERIUM_COMMANDER_ENTITY,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()));

    public static final RegistryObject<Item> ZOMPERIUM_PRIVATE_SPAWN_EGG = ITEMS.register("zomperium_private_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ZOMPERIUM_PRIVATE_ENTITY,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()));

    public static final RegistryObject<Item> ZOMPERIUM_SCOUT_SPAWN_EGG = ITEMS.register("zomperium_scout_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ZOMPERIUM_SCOUT_ENTITY,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()));

    // ===== 7 张唱片 =====
    public static final RegistryObject<Item> MUSIC_DISC_UNIVERSAL_COLLAPSE = ITEMS.register("music_disc_universal_collapse",
            () -> new net.minecraft.world.item.RecordItem(12,
                    () -> ModSounds.MUSIC_DISC_UNIVERSAL_COLLAPSE.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 2920));

    public static final RegistryObject<Item> MUSIC_DISC_THE_FINALS = ITEMS.register("music_disc_the_finals",
            () -> new net.minecraft.world.item.RecordItem(12,
                    () -> ModSounds.MUSIC_DISC_THE_FINALS.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 2960));

    public static final RegistryObject<Item> MUSIC_DISC_ARKNIGHTS_OST3 = ITEMS.register("music_disc_arknights_ost3",
            () -> new net.minecraft.world.item.RecordItem(10,
                    () -> ModSounds.MUSIC_DISC_ARKNIGHTS_OST3.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 2520));

    public static final RegistryObject<Item> MUSIC_DISC_X_COOL = ITEMS.register("music_disc_x_cool",
            () -> new net.minecraft.world.item.RecordItem(11,
                    () -> ModSounds.MUSIC_DISC_X_COOL.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 2700));

    public static final RegistryObject<Item> MUSIC_DISC_SOMNIOMANCER = ITEMS.register("music_disc_somniomancer",
            () -> new net.minecraft.world.item.RecordItem(15,
                    () -> ModSounds.MUSIC_DISC_SOMNIOMANCER.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4360));

    public static final RegistryObject<Item> MUSIC_DISC_BE_MY_STAR = ITEMS.register("music_disc_be_my_star",
            () -> new net.minecraft.world.item.RecordItem(7,
                    () -> ModSounds.MUSIC_DISC_BE_MY_STAR.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 1600));

    public static final RegistryObject<Item> MUSIC_DISC_BATTLEPLAN_ARCLIGHT = ITEMS.register("music_disc_battleplan_arclight",
            () -> new net.minecraft.world.item.RecordItem(15,
                    () -> ModSounds.MUSIC_DISC_BATTLEPLAN_ARCLIGHT.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4460));

    // ===== 指挥官宝藏袋 =====
    public static final RegistryObject<Item> COMMANDER_TREASURE_BAG = ITEMS.register("commander_treasure_bag",
            () -> new CommanderTreasureBagItem(new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> IRON_TAB = CREATIVE_MODE_TABS.register("iron_tab",
            () -> CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.yingzhiji_s_mod.iron_tab"))
                    .icon(() -> new ItemStack(IRON_BOW.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(IRON_BOW.get());
                        output.accept(OLD_SABER.get());
                        output.accept(WOODEN_SLASH_SWORD.get());
                        output.accept(STONE_SLASH_SWORD.get());
                        output.accept(IRON_SLASH_SWORD.get());
                        output.accept(GOLDEN_SLASH_SWORD.get());
                        output.accept(DIAMOND_SLASH_SWORD.get());
                        output.accept(NETHERITE_SLASH_SWORD.get());
                        output.accept(WOODEN_HEAVY_PICKAXE.get());
                        output.accept(STONE_HEAVY_PICKAXE.get());
                        output.accept(IRON_HEAVY_PICKAXE.get());
                        output.accept(GOLDEN_HEAVY_PICKAXE.get());
                        output.accept(DIAMOND_HEAVY_PICKAXE.get());
                        output.accept(NETHERITE_HEAVY_PICKAXE.get());
                        output.accept(ASHBLAZITE_SWORD.get());
                        output.accept(ASHBLAZITE_PICKAXE.get());
                        output.accept(ASHBLAZITE_AXE.get());
                        output.accept(ASHBLAZITE_SHOVEL.get());
                        output.accept(ASHBLAZITE_HOE.get());
                        output.accept(ASHBLAZITE_SLASH_SWORD.get());
                        output.accept(ASHBLAZITE_HEAVY_PICKAXE.get());
                        output.accept(SWORD_BOX.get());

                        output.accept(ModMaterials.HEAVY_MOLTEN_BLANK.get());
                        output.accept(ModMaterials.DRAGON_BREATH_SMITHING_TEMPLATE.get());
                        output.accept(ModMaterials.END_FLASH_BLANK.get());
                        output.accept(END_FLASH.get());

                        output.accept(MOBILE_SHIELD.get());

                        output.accept(IRON_ARROW.get());
                        output.accept(IRON_SPECTRAL_ARROW.get());
                        output.accept(IRON_BURNING_ARROW.get());
                        output.accept(IRON_POISON_ARROW.get());
                        output.accept(IRON_COOLING_ARROW.get());
                        output.accept(IRON_FRAGMENT_ARROW.get());
                        output.accept(DENSE_ARROW.get());
                        output.accept(DENSE_PIERCING_ARROW.get());
                        output.accept(DENSE_OVERPRESSURE_ARROW.get());
                        output.accept(DENSE_DECAYING_ARROW.get());
                        output.accept(DENSE_DEEPSCULK_ARROW.get());
                        output.accept(DENSE_APOCALYPSE_ARROW.get());

                        output.accept(ModMaterials.KINETIC_CORE.get());
                        output.accept(ModMaterials.CRYSTAL_FRAME.get());
                        output.accept(ModMaterials.HIGH_STRENGTH_WIRE.get());
                        output.accept(ModMaterials.DEEPSCULK_CRYSTAL.get());
                        output.accept(ModMaterials.DECAYING_SOUL.get());
                        output.accept(ModMaterials.DRAGON_BREATH_EXTRACT.get());
                        output.accept(ModMaterials.HEAVY_ALLOY.get());
                        output.accept(ModMaterials.APOCALYPSE_CHARGE.get());

                        output.accept(REPAIR_KIT.get());

                        output.accept(ZOMPERIUM_HANDSET_RADIO.get());
                        output.accept(ZOMPERIUM_COMMANDER_SPAWN_EGG.get());
                        output.accept(ZOMPERIUM_PRIVATE_SPAWN_EGG.get());
                        output.accept(ZOMPERIUM_SCOUT_SPAWN_EGG.get());

                        output.accept(MUSIC_DISC_UNIVERSAL_COLLAPSE.get());
                        output.accept(MUSIC_DISC_THE_FINALS.get());
                        output.accept(MUSIC_DISC_ARKNIGHTS_OST3.get());
                        output.accept(MUSIC_DISC_X_COOL.get());
                        output.accept(MUSIC_DISC_SOMNIOMANCER.get());
                        output.accept(MUSIC_DISC_BE_MY_STAR.get());
                        output.accept(MUSIC_DISC_BATTLEPLAN_ARCLIGHT.get());

                        output.accept(COMMANDER_TREASURE_BAG.get());

                        output.accept(ModMaterials.ASHBLAZITE.get());
                        output.accept(ASHBLAZITE_ORE_ITEM.get());
                        output.accept(ASHBLAZITE_DEEPSLATE_ORE_ITEM.get());
                        output.accept(ASHBLAZITE_BLOCK_ITEM.get());

                        output.accept(CRYSTALLIZER_ITEM.get());

                        output.accept(ABYSSAL_EYE.get());
                        output.accept(DESTINY_CRYSTAL.get());
                        output.accept(MAIDEN_HONEY_POTION.get());
                        output.accept(MUSIC_DISC_INITIAL_PLAY.get());

                    })
                    .build());

    public ExampleMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        ModMaterials.MATERIALS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);

        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.SERIALIZERS.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModFluidTypes.FLUID_TYPES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM COMMON SETUP");

        event.enqueueWork(() -> {
            SpawnPlacements.register(
                    ZOMPERIUM_PRIVATE_ENTITY.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    ZomperiumPrivate::checkSpawnRules
            );

            SpawnPlacements.register(
                    ZOMPERIUM_SCOUT_ENTITY.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    ZomperiumScout::checkSpawnRules
            );

            ModNetwork.register();
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void onAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(ExampleMod.ZOMPERIUM_COMMANDER_ENTITY.get(),
                    ZomperiumCommander.createAttributes().build());
            event.put(ExampleMod.ZOMPERIUM_PRIVATE_ENTITY.get(),
                    ZomperiumPrivate.createAttributes().build());
            event.put(ExampleMod.ZOMPERIUM_SCOUT_ENTITY.get(),
                    ZomperiumScout.createAttributes().build());
            event.put(ExampleMod.LIBRARIAN_ENTITY.get(),
                    LibrarianEntity.createAttributes().build());
        }

        @SubscribeEvent
        public static void onBuildTab(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
                event.accept(LIBRARIAN_SPAWN_EGG.get());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onRegisterRenderers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ExampleMod.IRON_ARROW_ENTITY.get(),
                    com.example.examplemod.IronArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.IRON_SPECTRAL_ARROW_ENTITY.get(), IronSpectralArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.IRON_BURNING_ARROW_ENTITY.get(), IronBurningArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.IRON_POISON_ARROW_ENTITY.get(), IronPoisonArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.IRON_COOLING_ARROW_ENTITY.get(), IronCoolingArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.IRON_FRAGMENT_ARROW_ENTITY.get(), IronFragmentArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.DENSE_ARROW_ENTITY.get(), DenseArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.DENSE_PIERCING_ARROW_ENTITY.get(), DensePiercingArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.DENSE_OVERPRESSURE_ARROW_ENTITY.get(), DenseOverpressureArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.DENSE_DECAYING_ARROW_ENTITY.get(), DenseDecayingArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.DENSE_DEEPSCULK_ARROW_ENTITY.get(), DenseDeepSculkArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.DENSE_APOCALYPSE_ARROW_ENTITY.get(), DenseApocalypseArrowRenderer::new);
            event.registerEntityRenderer(ExampleMod.APOCALYPSE_CLOUD_ENTITY.get(), ApocalypseCloudRenderer::new);
            event.registerEntityRenderer(ExampleMod.APOCALYPSE_ANIMATION_ENTITY.get(), ApocalypseAnimationRenderer::new);
            event.registerEntityRenderer(ExampleMod.DECAYING_SOUL_ENTITY.get(), DecayingSoulRenderer::new);
            event.registerEntityRenderer(ExampleMod.DECAYING_CLOUD_ENTITY.get(), DecayingCloudRenderer::new);

            event.registerEntityRenderer(ExampleMod.ZOMPERIUM_COMMANDER_ENTITY.get(),
                    context -> new ZomperiumCommanderRenderer(context, ZomperiumCommanderModel.LAYER_LOCATION));

            event.registerEntityRenderer(ExampleMod.ZOMPERIUM_SUMMONER_ENTITY.get(), ZomperiumSummonerRenderer::new);

            event.registerEntityRenderer(ExampleMod.FLYING_ZOMBIE_HEAD_ENTITY.get(),
                    FlyingZombieHeadRenderer::new);

            event.registerEntityRenderer(ExampleMod.ZOMPERIUM_PRIVATE_ENTITY.get(), ZomperiumPrivateRenderer::new);

            event.registerEntityRenderer(ExampleMod.ZOMPERIUM_SCOUT_ENTITY.get(), ZomperiumScoutRenderer::new);

            event.registerEntityRenderer(ExampleMod.LIBRARIAN_ENTITY.get(), LibrarianRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterLayers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ZomperiumCommanderModel.LAYER_LOCATION,
                    ZomperiumCommanderModel::createBodyLayer);
            event.registerLayerDefinition(ZomperiumPrivateModel.LAYER_LOCATION,
                    ZomperiumPrivateModel::createBodyLayer);
            event.registerLayerDefinition(ZomperiumScoutModel.LAYER_LOCATION,
                    ZomperiumScoutModel::createBodyLayer);

            event.registerLayerDefinition(LibrarianModel.LAYER_LOCATION, LibrarianModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenus.CRYSTALLIZER.get(), CrystallizerScreen::new);
            });

            net.minecraft.client.renderer.item.ItemProperties.register(
                    ExampleMod.IRON_BOW.get(),
                    new net.minecraft.resources.ResourceLocation("pull"),
                    (stack, level, entity, seed) -> {
                        if (entity == null) return 0.0F;
                        return entity.getUseItem() != stack ? 0.0F
                                : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                    });

            net.minecraft.client.renderer.item.ItemProperties.register(
                    ExampleMod.IRON_BOW.get(),
                    new net.minecraft.resources.ResourceLocation("pulling"),
                    (stack, level, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

            net.minecraft.client.renderer.item.ItemProperties.register(
                    ExampleMod.OLD_SABER.get(),
                    new net.minecraft.resources.ResourceLocation("drawn"),
                    (stack, level, entity, seed) -> entity != null ? 1.0F : 0.0F);

            net.minecraft.client.renderer.item.ItemProperties.register(
                    ExampleMod.MOBILE_SHIELD.get(),
                    new net.minecraft.resources.ResourceLocation("blocking"),
                    (stack, level, entity, seed) -> {
                        if (entity instanceof Player player) {
                            // 普通举盾（正在使用）或 强效举盾（按住Shift）时，返回 1.0
                            boolean normal = player.isUsingItem() && player.getUseItem() == stack;
                            boolean strong = player.isShiftKeyDown();
                            if (normal || strong) return 1.0F;
                        }
                        return 0.0F;
                    });

            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}