package lazy.moheads.mixin;

import lazy.moheads.event.LivingMobDrops;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    public void dropAllDeathLoot(DamageSource source, CallbackInfo ci) {
        var result = LivingMobDrops.Companion.getEvent().invoker().addDrops((LivingEntity) (Object) this, source);
        if (source.getEntity() != null) {
            result.forEach(source.getEntity().level::addFreshEntity);
        }
    }
}