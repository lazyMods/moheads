package lazy.moheads.utils

enum class ParrotVariants(private val id: Int, private val variant: String) {
    RED(0, "red"),
    BLUE(1, "blue"),
    GREEN(2, "green"),
    CYAN(3, "cyan"),
    GRAY(4, "gray");

    companion object {
        fun fromId(id: Int): String {
            return values().first { it.id == id }.variant
        }
    }
}