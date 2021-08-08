package lazy.moheads.utils

enum class LlamaVariants(private val id: Int, private val variant: String) {
    CREAMY(0, "creamy"),
    WHITE(1, "white"),
    BROWN(2, "brown"),
    GRAY(3, "gray");

    companion object {
        fun fromId(id: Int): String {
            return values().first { it.id == id }.variant
        }
    }
}