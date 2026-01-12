package net.agusdropout.bloodyhell.worldgen.structure; // Ajusta tu paquete

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;

import java.util.Map;
import java.util.Optional;

// Importa tus clases de init
// import net.mcreator.bloodyhell.init.ModStructures;
// import net.mcreator.bloodyhell.init.ModEntities;

public class PoolStructure extends Structure {

    public static final Codec<PoolStructure> CODEC = simpleCodec(PoolStructure::new);

    // Ajusta "bloodyhell" a tu MODID exacto
    private static final ResourceLocation PART_0 = new ResourceLocation("bloodyhell", "pyramid/pool_parte_0");
    private static final ResourceLocation PART_1 = new ResourceLocation("bloodyhell", "pyramid/pool_parte_1");
    private static final ResourceLocation PART_2 = new ResourceLocation("bloodyhell", "pyramid/pool_parte_2");
    private static final ResourceLocation PART_3 = new ResourceLocation("bloodyhell", "pyramid/pool_parte_3");
    private static final ResourceLocation PART_4 = new ResourceLocation("bloodyhell", "pyramid/pool_parte_4"); // Esquina especial
    private static final ResourceLocation PART_5 = new ResourceLocation("bloodyhell", "pyramid/pool_parte_5"); // Puerta
    private static final ResourceLocation PART_6 = new ResourceLocation("bloodyhell", "pyramid/pool_parte_6");
    private static final ResourceLocation PART_7 = new ResourceLocation("bloodyhell", "pyramid/pool_parte_7");
    private static final ResourceLocation PART_8 = new ResourceLocation("bloodyhell", "pyramid/pool_parte_8"); // CENTRO

    // Map de offsets verticales por si alguna pieza necesita subirse o bajarse (actualmente en 0)
    private static final Map<ResourceLocation, BlockPos> OFFSET = ImmutableMap.<ResourceLocation, BlockPos>builder()
            .put(PART_0, new BlockPos(0, 0, 0))
            .put(PART_1, new BlockPos(0, 0, 0))
            .put(PART_2, new BlockPos(0, 0, 0))
            .put(PART_3, new BlockPos(0, 0, 0))
            .put(PART_4, new BlockPos(0, 0, 0))
            .put(PART_5, new BlockPos(0, 0, 0))
            .put(PART_6, new BlockPos(0, 0, 0))
            .put(PART_7, new BlockPos(0, 0, 0))
            .put(PART_8, new BlockPos(0, 0, 0))
            .build();

    public PoolStructure(StructureSettings settings) {
        super(settings);
    }

    // Lógica para encontrar dónde generar (Biome check, altura, etc)
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        // Aquí podrías chequear biomas específicos si no lo haces en el JSON
        // Retornamos la generación en la superficie (o suelo del océano si fuera necesario)
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> {
            generatePieces(builder, context);
        });
    }

    private static void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
        int centerX = context.chunkPos().getMinBlockX();
        int centerZ = context.chunkPos().getMinBlockZ();

        // Radio aproximado de tu estructura (47 de un ala + 3 del centro = 50 bloques)
        // Usamos 45 para no irnos demasiado al borde, pero chequear las esquinas.
        int radius = 45;

        // Buscamos la altura (OCEAN_FLOOR ignora árboles) en el centro y en las 4 esquinas
        int yCenter = context.chunkGenerator().getFirstFreeHeight(centerX, centerZ, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        int yCorner1 = context.chunkGenerator().getFirstFreeHeight(centerX + radius, centerZ + radius, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        int yCorner2 = context.chunkGenerator().getFirstFreeHeight(centerX - radius, centerZ - radius, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        int yCorner3 = context.chunkGenerator().getFirstFreeHeight(centerX + radius, centerZ - radius, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        int yCorner4 = context.chunkGenerator().getFirstFreeHeight(centerX - radius, centerZ + radius, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());

        // Calculamos la altura MÍNIMA encontrada.
        // Esto asegura que la base de la estructura esté en el punto más bajo del terreno.
        // El resto de la estructura quedará enterrada en las zonas altas, pero NADA flotará.
        int minY = Math.min(yCenter, Math.min(yCorner1, Math.min(yCorner2, Math.min(yCorner3, yCorner4))));

        // Bajamos 1 bloque extra para asegurar que el suelo se conecte bien y no queden huecos de hierba flotando.
        int finalY = minY - 1;

        BlockPos blockpos = new BlockPos(centerX, finalY, centerZ);
        Rotation rotation = Rotation.getRandom(context.random());

        PoolStructure.start(context.structureTemplateManager(), blockpos, rotation, builder, context.random());
    }

    /**
     * AQUÍ OCURRE LA MAGIA DEL ENSAMBLAJE (STITCHING)
     */
    public static void start(StructureTemplateManager templateManager, BlockPos pos, Rotation rotation, StructurePiecesBuilder pieceList, RandomSource random) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        // Variables para los tamaños
        int armLength = 47; // Largo de las alas
        int centerSize = 6; // Ancho del centro

        BlockPos rotationOffset;
        BlockPos finalPos;

        // 1. COLOCAR EL CENTRO (PART 8) - Es el ancla (0,0,0)
        rotationOffset = new BlockPos(0, 0, 0).rotate(rotation);
        finalPos = rotationOffset.offset(x, y, z);
        pieceList.addPiece(new Piece(templateManager, PART_8, finalPos, rotation));

        // --- FILA SUPERIOR (NORTE / Z negativo) ---

        // Part 0 (Esquina NW): -47 X, -47 Z
        rotationOffset = new BlockPos(-armLength, 0, -armLength).rotate(rotation);
        finalPos = rotationOffset.offset(x, y, z);
        pieceList.addPiece(new Piece(templateManager, PART_0, finalPos, rotation));

        // Part 1 (Lado N): 0 X, -47 Z
        rotationOffset = new BlockPos(0, 0, -armLength).rotate(rotation);
        finalPos = rotationOffset.offset(x, y, z);
        pieceList.addPiece(new Piece(templateManager, PART_1, finalPos, rotation));

        // Part 2 (Esquina NE): +6 X, -47 Z (El offset X es el ancho del centro)
        rotationOffset = new BlockPos(centerSize, 0, -armLength).rotate(rotation);
        finalPos = rotationOffset.offset(x, y, z);
        pieceList.addPiece(new Piece(templateManager, PART_2, finalPos, rotation));

        // --- FILA MEDIO (OESTE Y ESTE) ---

        // Part 7 (Lado W): -47 X, 0 Z
        rotationOffset = new BlockPos(-armLength, 0, 0).rotate(rotation);
        finalPos = rotationOffset.offset(x, y, z);
        pieceList.addPiece(new Piece(templateManager, PART_7, finalPos, rotation));

        // Part 3 (Lado E): +6 X, 0 Z
        rotationOffset = new BlockPos(centerSize, 0, 0).rotate(rotation);
        finalPos = rotationOffset.offset(x, y, z);
        pieceList.addPiece(new Piece(templateManager, PART_3, finalPos, rotation));

        // --- FILA INFERIOR (SUR / Z positivo) ---

        // Part 6 (Esquina SW): -47 X, +6 Z
        rotationOffset = new BlockPos(-armLength, 0, centerSize).rotate(rotation);
        finalPos = rotationOffset.offset(x, y, z);
        pieceList.addPiece(new Piece(templateManager, PART_6, finalPos, rotation));

        // Part 5 (PUERTA - Sur): 0 X, +6 Z
        // NOTA: Si la puerta mide 48 de ancho, se solapará. Asumimos posición standard.
        rotationOffset = new BlockPos(0, 0, centerSize).rotate(rotation);
        finalPos = rotationOffset.offset(x, y, z);
        pieceList.addPiece(new Piece(templateManager, PART_5, finalPos, rotation));

        // Part 4 (Esquina SE): +6 X, +6 Z
        rotationOffset = new BlockPos(centerSize, 0, centerSize).rotate(rotation);
        finalPos = rotationOffset.offset(x, y, z);
        pieceList.addPiece(new Piece(templateManager, PART_4, finalPos, rotation));
    }

    @Override
    public StructureType<?> type() {
        // Asegúrate de registrar esto en tu ModStructures
        // return ModStructures.PPOL_STRUCTURE.get();
        return null; // Placeholder para que compile, reemplázalo
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    // --- CLASE PIECE INTERNA ---
    public static class Piece extends TemplateStructurePiece {

        public Piece(StructureTemplateManager manager, ResourceLocation location, BlockPos pos, Rotation rotation) {
            // Reemplaza ModStructures.PPOL_PIECE.get() con tu registro de pieza
            super(ModStructures.POOL_PIECE.get(), 0, manager, location, location.toString(), makeSettings(rotation), makePosition(location, pos));
        }

        public Piece(StructurePieceSerializationContext context, CompoundTag tag) {
            super(ModStructures.POOL_PIECE.get(), tag, context.structureTemplateManager(), (resourceLocation) -> {
                return makeSettings(Rotation.valueOf(tag.getString("Rot")));
            });
        }

        private static StructurePlaceSettings makeSettings(Rotation rotation) {
            return (new StructurePlaceSettings())
                    .setRotation(rotation)
                    .setMirror(Mirror.NONE)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE));
        }

        private static BlockPos makePosition(ResourceLocation location, BlockPos pos) {
            return pos.offset(OFFSET.getOrDefault(location, BlockPos.ZERO));
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            super.addAdditionalSaveData(context, tag);
            tag.putString("Rot", this.placeSettings.getRotation().name());
        }

        @Override
        protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor level, RandomSource random, net.minecraft.world.level.levelgen.structure.BoundingBox box) {
            // Aquí puedes añadir tus mobs usando bloques de estructura en modo DATA
            /*
            if ("boss_spawn".equals(function)) {
                // Spawn boss logic
            }
            */
        }
    }
}