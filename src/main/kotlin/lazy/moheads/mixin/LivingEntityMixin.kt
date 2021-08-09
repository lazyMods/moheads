package lazy.moheads.mixin

import lazy.moheads.event.LivingMobDrops
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(LivingEntity::class)
class LivingEntityMixin {

    @Inject(method = ["dropAllDeathLoot"], at = [At("TAIL")])
    fun dropAllDeathLoot(source: DamageSource, ci: CallbackInfo) {
        val result = LivingMobDrops.EVENT.invoker().addDrops((this as LivingEntity), source)
        source.entity?.run {
            result.forEach { this.level.addFreshEntity(it) }
        }
    }
}