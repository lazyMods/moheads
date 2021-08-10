package lazy.moheads

import lazy.moheads.event.LivingMobDrops
import lazy.moheads.head.HeadUtils
import net.fabricmc.api.ModInitializer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.enchantment.EnchantmentHelper


@Suppress("UNUSED")
object MoHeads : ModInitializer {

    const val MOD_ID = "moheads"

    override fun onInitialize() {
        LivingMobDrops.event.register(object : LivingMobDrops {
            override fun addDrops(livingEntity: LivingEntity, damageSource: DamageSource): List<ItemEntity> {
                val empty = emptyList<ItemEntity>()
                if (livingEntity is Player) return listOf(ItemEntity(livingEntity.level, livingEntity.x, livingEntity.y, livingEntity.z, HeadUtils.playerHead(livingEntity)))
                if (livingEntity.isBaby) return empty
                if (damageSource.entity is Player) {
                    val looting = EnchantmentHelper.getMobLooting(damageSource.entity as Player)
                    return listOf(ItemEntity(livingEntity.level, livingEntity.x, livingEntity.y, livingEntity.z, HeadUtils.headFor(livingEntity, looting)))
                }
                return empty
            }
        })
    }
}