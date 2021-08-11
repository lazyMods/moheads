package lazy.moheads.head

import com.google.gson.GsonBuilder
import lazy.moheads.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.Parrot
import net.minecraft.world.entity.animal.Rabbit
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.animal.horse.Horse
import net.minecraft.world.entity.animal.horse.Llama
import net.minecraft.world.entity.animal.horse.TraderLlama
import net.minecraft.world.entity.monster.ZombieVillager
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.io.InputStreamReader
import java.lang.Long
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.emptyArray


object HeadUtils {

    private const val DEBUG = true

    private val headData: MutableList<HeadData> = mutableListOf()
    private val playerCache: MutableMap<String, SkinResponse> = mutableMapOf()

    private val gson = GsonBuilder().create()

    fun load(mc: Minecraft) {
        val gson = GsonBuilder().create()
        mc.resourceManager.listResources("heads/") { s -> s.endsWith(".json") }.forEach {
            val inputStream = mc.resourceManager.getResource(it).inputStream
            val data = gson.fromJson(InputStreamReader(inputStream), HeadData::class.java)
            headData.add(HeadData(data.regName, data.dropChance, data.uuid, data.hash))
        }
    }

    fun headFor(entity: Entity, looting: Int): ItemStack {
        var regKey = EntityType.getKey(entity.type).path

        when (regKey) {
            "sheep" -> regKey = (entity as Sheep).color.name.lowercase() + "_sheep"
            "axolotl" -> regKey = (entity as Axolotl).variant.getName() + "_axolotl"
            "cat" -> regKey = (entity as Cat).resourceLocation.path.split('/').last().split(".")[0] + "_cat"
            "horse" -> regKey = (entity as Horse).variant.name.lowercase() + "_horse"
            "llama" -> regKey = LlamaVariants.fromId((entity as Llama).variant) + "_llama"
            "trader_llama" -> regKey = LlamaVariants.fromId((entity as TraderLlama).variant) + "_trader_llama"
            "parrot" -> regKey = ParrotVariants.fromId((entity as Parrot).variant) + "_parrot"
            "rabbit" -> regKey = RabbitVariants.fromId((entity as Rabbit).rabbitType) + "_rabbit"
            "villager" -> regKey = getVillagerName((entity as Villager).villagerData.profession, "villager")
            "zombie_villager" -> regKey =
                "zombie_" + getVillagerName((entity as ZombieVillager).villagerData.profession, "zombie_villager")
        }

        if (!containsKey(regKey)) return ItemStack.EMPTY

        val (regName, chance, uuid, hash) = get(regKey)
        val headChance = chance + (0.05 + (looting / 100))
        if (entity.level.random.nextDouble() > headChance) return ItemStack.EMPTY

        return createHead(regName, uuid, hash)
    }

    fun playerHead(player: Player): ItemStack {
        val skinData = playerCache.getOrDefault(player.stringUUID, SkinResponse("", "", emptyArray()))
        if (skinData.id == "") return ItemStack.EMPTY
        return createHead(skinData.name, uuidToIntListString(skinData.id), skinData.properties[0].value)
    }

    private fun containsKey(key: String): Boolean {
        return headData.any { it.regName == key }
    }

    private fun get(key: String): HeadData {
        return headData.first { it.regName == key }
    }

    private fun createHead(regName: String, uuid: String, hash: String): ItemStack {
        val head = ItemStack(Items.PLAYER_HEAD)

        val tag = CompoundTag()
            .withIntArray("Id", stringToIntList(uuid))
            .with(
                "Properties", CompoundTag()
                    .withList(
                        "textures", ListTag()
                            .with(
                                CompoundTag()
                                    .withString("Value", hash)
                            )
                    )
            )

        head.addTagElement("SkullOwner", tag)
        head.hoverName = TextComponent(styleRegName(regName))
        return head
    }

    private fun stringToIntList(s: String): List<Int> {
        val intList = mutableListOf<Int>()
        val elements = s.substring(1, s.length - 1).split(",")
        elements.forEach { intList.add(it.trim().toInt()) }
        return intList
    }

    private fun styleRegName(regName: String): String {
        val worlds = regName.split("_")
        val builder = StringBuilder()
        worlds.forEach { builder.append(it.replaceFirstChar { char -> char.uppercase() }).append(" ") }
        return builder.toString()
    }

    private fun getVillagerName(profession: VillagerProfession, fallback: String): String {
        return if (profession.name != "none") profession.name else fallback
    }


    private fun uuidToIntListString(uuid: String): String {
        val intList = mutableListOf<Int>()
        var index = 0
        while (index < uuid.length) {
            intList.add(Long.valueOf(uuid.substring(index, uuid.length.coerceAtMost(index + 8)), 16).toInt())
            index += 8
        }
        return intList.toString()
    }

    fun addPlayerToCache(entity: Entity, world: ServerLevel) {
        if (entity is Player) {
            if (!playerCache.containsKey(entity.stringUUID)) {
                val skinData = PlayerUtils.getSkinData(gson, if (DEBUG) "420616b4-af00-493b-ae80-cde41186e299" else entity.stringUUID)
                playerCache[entity.stringUUID] = skinData
            }
        }
    }

    fun removePlayerFromCache(entity: Entity, world: ServerLevel) {
        if (entity is Player) {
            if (playerCache.containsKey(entity.stringUUID)) {
                playerCache.remove(entity.stringUUID)
            }
        }
    }
}