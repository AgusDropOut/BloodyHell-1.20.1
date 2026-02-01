package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.entity.custom.SelioraRestingBlockEntity;
import net.agusdropout.bloodyhell.entity.ModEntityTypes; // Asegúrate de importar tus entidades
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SelioraRestingBlock extends BaseEntityBlock {

    public SelioraRestingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    // Usamos ENTITYBLOCK_ANIMATED porque usas Geckolib o un render especial
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SelioraRestingBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Ejecutamos la lógica solo en el servidor
        if (!level.isClientSide) {

            // 1. Invocar a Seliora
            // Asegúrate de que ModEntityTypes.SELIORA sea el nombre correcto en tu registro
            var bossEntity = ModEntityTypes.SELIORA.get().create(level);

            if (bossEntity != null) {
                // Centramos la entidad en el bloque
                bossEntity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
                level.addFreshEntity(bossEntity);
            }

            // 2. Efectos Visuales y Sonoros de Invocación (Server Side)
            // Sonido de explosión o magia oscura al transformarse
            level.playSound(null, pos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 1.0f, 1.0f);
            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0f, 0.5f);

            // Generar partículas en el servidor para que todos las vean
            if (level instanceof ServerLevel serverLevel) {
                // Una nube de humo grande
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                        1, 0.0, 0.0, 0.0, 0.0);

                // Partículas de magia (Witch o Portal)
                serverLevel.sendParticles(ParticleTypes.WITCH,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        20, 0.5, 0.5, 0.5, 0.1);
            }

            // 3. Eliminar el bloque (Se "transforma" en el jefe)
            // Usamos removeBlock con false para que NO suelte ítem (drop)
            level.removeBlock(pos, false);

            return InteractionResult.CONSUME;
        }

        // En el cliente devolvemos SUCCESS para que se vea la animación de la mano
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // --- EFECTOS VISUALES PASIVOS (Lectura) ---

        // Aumenté la probabilidad (nextInt(2)) para que salgan más partículas si hay más dispersión
        if (random.nextInt(2) == 0) {
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 2.0D; // Bajé un poco la altura base (2.5 a 2.0) para dar margen hacia arriba
            double d2 = (double)pos.getZ() + 0.5D;

            // --- CONFIGURACIÓN DE DISPERSIÓN Y VELOCIDAD ---

            // Dispersión de origen: (0.4 -> 1.2) Ahora aparecen en un radio más amplio
            double spreadX = (random.nextDouble() - 0.5D) * 1.2D;
            double spreadY = (random.nextDouble() * 1.0D); // Altura variable entre 0 y 1 bloque extra
            double spreadZ = (random.nextDouble() - 0.5D) * 1.2D;

            // Velocidad de movimiento: (0.1 -> 0.4) Se mueven mucho más rápido/lejos
            double velocityX = (random.nextDouble() - 0.5D) * 0.4D;
            double velocityY = (random.nextDouble() - 0.5D) * 0.4D; // Ahora también pueden moverse hacia abajo o arriba
            double velocityZ = (random.nextDouble() - 0.5D) * 0.4D;

            level.addParticle(ParticleTypes.ENCHANT,
                    d0 + spreadX,    // Posición X
                    d1 + spreadY,    // Posición Y
                    d2 + spreadZ,    // Posición Z
                    velocityX,       // Velocidad X
                    velocityY,       // Velocidad Y
                    velocityZ);      // Velocidad Z
        }

        // --- SONIDO AMBIENTAL ---
        if (random.nextInt(100) < 10) {
            // TODO: DESCOMENTAR Y PONER TU SONIDO AQUI ABAJO
            // level.playLocalSound(pos, ModSounds.SELIORA_READING_AMBIENT.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);

            // Placeholder temporal (Sonido de pasar página):
            level.playLocalSound(pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 0.8F, false);
        }
    }
}