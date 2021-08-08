package lazy.moheads.head

import com.google.gson.GsonBuilder
import lazy.moheads.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.animal.horse.Horse
import net.minecraft.world.entity.animal.horse.Llama
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.io.InputStreamReader


object HeadUtils {

    val headData: MutableList<HeadData> = mutableListOf()
    val FALSE = Pair(false, ItemStack.EMPTY)

    fun load(mc: Minecraft) {
        val gson = GsonBuilder().create()
        mc.resourceManager.listResources("heads/") { s -> s.endsWith(".json") }.forEach {
            val inputStream = mc.resourceManager.getResource(it).inputStream
            val data = gson.fromJson(InputStreamReader(inputStream), HeadData::class.java)
            headData.add(HeadData(data.regName, data.dropChance, data.uuid, data.hash))
        }
    }

    fun headFor(entity: Entity): Pair<Boolean, ItemStack> {
        var regKey = EntityType.getKey(entity.type).path

        when(regKey){
            "sheep" -> {
                val sheep = entity as Sheep
                regKey = sheep.color.name.lowercase() + "_sheep"
            }
            "axolotl" -> {
                val axolotl = entity as Axolotl
                regKey = axolotl.variant.getName() + "_axolotl"
            }
            "cat" -> {
                val cat = entity as Cat
                val variant = cat.resourceLocation.path.split('/').last().split(".")[0]
                regKey = variant + "_cat"
            }
            "horse" -> {
                val horse = entity as Horse
                regKey = horse.variant.name.lowercase() + "_horse"
            }
            "llama" -> {
                val llama = entity as Llama
                regKey = LlamaVariants.fromId(llama.variant) + "_llama"
            }
        }

        println("$regKey and contains ${containsKey(regKey)}")
        if (!containsKey(regKey)) return FALSE
        val (regName, _, uuid, hash) = get(regKey)
        if (isMcHead(regKey)) return Pair(true, getMcHead(regKey))
        val playerHead = createHead(regName, uuid, hash)
        return Pair(true, playerHead)
    }

    private fun containsKey(key: String): Boolean {
        return headData.any { it.regName == key }
    }

    private fun get(key: String): HeadData {
        return headData.first { it.regName == key }
    }

    private fun isMcHead(key: String): Boolean {
        return key == "creeper" || key == "zombie" || key == "skeleton"
    }

    private fun getMcHead(key: String): ItemStack {
        when (key) {
            "creeper" -> return ItemStack(Items.CREEPER_HEAD)
            "zombie" -> return ItemStack(Items.CREEPER_HEAD)
            "skeleton" -> return ItemStack(Items.CREEPER_HEAD)
        }
        return ItemStack.EMPTY
    }

    private fun createHead(regName: String, uuid: String, hash: String): ItemStack {
        val head = ItemStack(Items.PLAYER_HEAD)

        val tag = CompoundTag()
            .withIntArray("Id", stringToIntList(uuid))
            .with("Properties", CompoundTag()
                .withList("textures", ListTag()
                    .with(CompoundTag()
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
        return regName.replace("_", " ").replaceFirstChar { s -> s.uppercase() }
    }
}