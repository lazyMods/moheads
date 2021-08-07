package lazy.moheads

import lazy.moheads.event.LivingMobDrops
import lazy.moheads.head.HeadUtils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import java.io.File
import java.io.InputStream


@Suppress("UNUSED")
object MoHeads : ModInitializer {

    const val MOD_ID = "moheads"

    override fun onInitialize() {
        ClientLifecycleEvents.CLIENT_STARTED.register(HeadUtils::load)

        LivingMobDrops.EVENT.register(object : LivingMobDrops {
            override fun addDrops(livingEntity: LivingEntity, damageSource: DamageSource): List<ItemEntity> {
                val list = mutableListOf<ItemEntity>()
                val itemEntity = ItemEntity(livingEntity.level, livingEntity.x, livingEntity.y, livingEntity.z, HeadUtils.headFor(livingEntity).second)
                list.add(itemEntity)
                println(itemEntity)
                return list
            }
        })
    }

    fun InputStream.toFile(path: String) {
        File(path).outputStream().use { this.copyTo(it) }
    }
}