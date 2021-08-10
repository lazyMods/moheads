package lazy.moheads.utils

data class SkinResponse(val id: String, val name: String, val properties: Array<Properties>) {

    data class Properties(val name: String, val value: String)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SkinResponse

        if (id != other.id) return false
        if (name != other.name) return false
        if (!properties.contentEquals(other.properties)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + properties.contentHashCode()
        return result
    }
}

