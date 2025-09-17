package alicewritesthings.proton.me.survivalasterisk.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Shadow
    private float exhaustionLevel;
    @Shadow
    private float saturationLevel;
    @Shadow
    private int foodLevel;
    @Shadow
    private int tickTimer;
    @Shadow
    public abstract void addExhaustion(float exhaustion);

    /**
     * @author Survival Asterisk
     * @reason completely overhauling the relation between hunger and health. Sorry for the inconvenience
     */
    @Overwrite
    public void tick(ServerPlayer player) {
        ServerLevel serverlevel = player.level();
        Difficulty difficulty = serverlevel.getDifficulty();
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean flag = serverlevel.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (flag && this.foodLevel >= 10 && player.isHurt()) {
            /* Removed hunger consumption. If no more than 3 shanks are empty, regen at a modest rate
             * 60 ticks means plenty of grace, but it's very possible to gain negative momentum with inefficient fighting
             */
            this.tickTimer++;
            if (this.tickTimer >= 60) {
                player.heal(1.0F);
                this.tickTimer = 0;
            }
        } else if (flag && this.foodLevel >= 1 && player.isHurt()) {
            /* Removed hunger consumption. If at least some food remains, regen at a pitiful rate
             * 100 ticks is a mercy to prevent death by a thousand cuts and little more
             */
            this.tickTimer++;
            if (this.tickTimer >= 100) {
                player.heal(1.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            this.tickTimer++;
            if (this.tickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurtServer(serverlevel, player.damageSources().starve(), 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }
}
