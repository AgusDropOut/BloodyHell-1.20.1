package net.agusdropout.bloodyhell.worldgen.structure;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStructures {

    // Registro para TIPOS DE ESTRUCTURA (La definición lógica general)
    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, BloodyHell.MODID);

    // NUEVO: Registro para TIPOS DE PIEZAS (Los fragmentos individuales)
    public static final DeferredRegister<StructurePieceType> PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, BloodyHell.MODID);

    // ResourceKeys (Referencias para usar en JSONs)
    public static final ResourceKey<Structure> CATACOMBS = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(BloodyHell.MODID, "portal"));
    public static final ResourceKey<Structure> MAUSOLEUM = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(BloodyHell.MODID, "mausoleum"));
    public static final ResourceKey<Structure> VESPERS_HUT = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(BloodyHell.MODID, "vespers_hut"));

    // Registros de las Estructuras
    public static final RegistryObject<StructureType<BiggerJigsawStructure>> BIGGER_JIGSAW = STRUCTURES.register("bigger_jigsaw", () -> () -> BiggerJigsawStructure.CODEC);
    public static final RegistryObject<StructureType<PoolStructure>> POOL_STRUCTURE = STRUCTURES.register("pool", () -> () -> PoolStructure.CODEC);

    // CORRECCIÓN AQUÍ: Usamos PIECE_TYPES.register en lugar de llamar a un método estático en la clase
    // Nota: Esto asume que PpolStructure.Piece tiene el constructor (StructurePieceSerializationContext, CompoundTag)
    public static final RegistryObject<StructurePieceType> POOL_PIECE = PIECE_TYPES.register("pool_piece", () -> PoolStructure.Piece::new);
    public static final ResourceKey<Structure> POOL_KEY = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(BloodyHell.MODID, "pool"));

    public static void register(IEventBus eventBus){
        STRUCTURES.register(eventBus);
        PIECE_TYPES.register(eventBus); // ¡No olvides registrar el nuevo DeferredRegister!
    }
}