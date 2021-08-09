package lazy.moheads.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity

interface LivingMobDrops {

    fun addDrops(livingEntity: LivingEntity, damageSource: DamageSource): List<ItemEntity>

    companion object {
        val EVENT: Event<LivingMobDrops> = EventFactory.createArrayBacked(LivingMobDrops::class.java)
        { listeners ->
            object : LivingMobDrops {
                override fun addDrops(livingEntity: LivingEntity, damageSource: DamageSource): List<ItemEntity> {
                    for (listener in listeners) {
                        val result: List<ItemEntity> = listener.addDrops(livingEntity, damageSource)
                        if (result.isNotEmpty()) {
                            return result
                        }
                    }
                    return emptyList()
                }
            }
        }
    }
}