package lazy.moheads.utils

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag

fun CompoundTag.with(key: String, tag: CompoundTag): CompoundTag {
    this.put(key, tag)
    return this
}

fun CompoundTag.withIntArray(key: String, intArray: List<Int>): CompoundTag {
    this.putIntArray(key, intArray)
    return this
}

fun CompoundTag.withString(key: String, string: String): CompoundTag {
    this.putString(key, string)
    return this
}

fun CompoundTag.withList(key: String, list: ListTag): CompoundTag {
    this.put(key, list)
    return this
}

fun ListTag.with(tag: CompoundTag): ListTag {
    this.add(tag)
    return this
}

