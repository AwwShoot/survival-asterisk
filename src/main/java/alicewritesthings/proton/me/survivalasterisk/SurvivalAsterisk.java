package alicewritesthings.proton.me.survivalasterisk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;
import java.util.Random;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SurvivalAsterisk.MODID)
public class SurvivalAsterisk {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "survivalasterisk";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SurvivalAsterisk(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);



        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);


        NeoForge.EVENT_BUS.addListener(this::randomizeRespawn);
        NeoForge.EVENT_BUS.addListener(this::applyRespawnEffects);

        NeoForge.EVENT_BUS.addListener(this::applyMinimumDamage);

    }

    private void commonSetup(FMLCommonSetupEvent event) {

        if (Config.RANDOM_RESPAWNS.getAsBoolean()) {
            LOGGER.info("Respawns are randomized sucker!");
        }

        LOGGER.info("Minimum damage is {} be careful taking more than {} damage", Config.MINIMUM_DAMAGE.get(), Config.MIN_DAMAGE_THRESHOLD.getAsInt());
    }

    private void randomizeRespawn(PlayerRespawnPositionEvent event) {
        if(event.isFromEndFight() || !Config.RANDOM_RESPAWNS.get()) {
            return; // let the winner go home to spawn
        }
        int minRespawnDistance = Config.MIN_RESPAWN_RADIUS.getAsInt();
        int maxRespawnDistance = Config.MAX_RESPAWN_RADIUS.getAsInt();
        Random random = new Random();//I'm sure I should use the in-game in-built randomization but that sounds like a hassle to drag into here
        int xDistance = random.nextInt(minRespawnDistance, maxRespawnDistance);
        int zDistance = random.nextInt(minRespawnDistance, maxRespawnDistance);
        TeleportTransition currentTeleport = event.getOriginalTeleportTransition();
        BlockPos deathPos = event.getEntity().getOnPos();
        BlockPos newPos = deathPos.offset(xDistance, 300, zDistance);
        currentTeleport = currentTeleport.withPosition(new Vec3(newPos));


        event.setTeleportTransition(currentTeleport);
    }

    private void applyRespawnEffects(PlayerEvent.PlayerRespawnEvent event) {
        if(!Config.RANDOM_RESPAWNS.get()) {
            return;
        }
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 900));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 900));
        }

    }

    private void applyMinimumDamage(LivingDamageEvent.Pre event) {
        if(!Config.MINIMUM_DAMAGE.get()) {
            return;
        }
        if(event.getOriginalDamage() >= Config.MIN_DAMAGE_THRESHOLD.getAsInt() && event.getNewDamage() < 1) {
            event.setNewDamage(1);
        }
    }




}
