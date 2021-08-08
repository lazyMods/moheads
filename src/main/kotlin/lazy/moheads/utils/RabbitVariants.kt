package lazy.moheads.utils

enum class RabbitVariants(private val id: Int, private val variant: String) {
    BROWN(0, "brown"),
    WHITE(1, "white"),
    BLACK(2, "black"),
    BLACK_WHITE(3, "black_and_white"),
    GOLD(4, "gold"),
    SALT_PEPPER(5, "salt_and_pepper"),
    KILLER(99, "killer");

    companion object {
        fun fromId(id: Int): String {
            return values().first { it.id == id }.variant
        }
    }
}
