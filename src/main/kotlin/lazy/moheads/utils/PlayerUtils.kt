package lazy.moheads.utils

import com.google.gson.Gson
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

object PlayerUtils {

    private const val profileNamesEndpoint = "https://api.mojang.com/user/profiles/%s/names"
    private const val playerEndpoint = "https://api.mojang.com/users/profiles/minecraft/"
    private const val skinEndpoint = "https://sessionserver.mojang.com/session/minecraft/profile/"

    fun getSkinData(gson: Gson, uuid: String): SkinResponse {
        if (getPlayerProfileNames(gson, uuid).isEmpty()) return SkinResponse("", "", emptyArray())
        val response = pageContentToString(skinEndpoint + getPlayerId(gson, getPlayerProfileNames(gson, uuid)))
        if (response.isEmpty()) return SkinResponse("", "", emptyArray())
        return gson.fromJson(response, SkinResponse::class.java)
    }

    private fun getPlayerId(gson: Gson, names: Array<ProfileResponse>): String {
        if (names.isNotEmpty()) {
            var index = 0
            do {
                val response = pageContentToString(playerEndpoint + names[index].name)
                if (response.isEmpty()) {
                    index++
                } else break
            } while (index < names.size)
            val response = pageContentToString(playerEndpoint + names[index].name)
            if (response.isEmpty()) return ""
            val playerData = gson.fromJson(response, PlayerResponse::class.java)
            return playerData.id
        }
        return ""
    }

    private fun getPlayerProfileNames(gson: Gson, uuid: String): Array<ProfileResponse> {
        val response = pageContentToString(profileNamesEndpoint.replace("%s", uuid))
        if (response == "") emptyArray<ProfileResponse>()
        return if (gson.fromJson(response, Array<ProfileResponse>::class.java) == null) emptyArray() else gson.fromJson(
            response,
            Array<ProfileResponse>::class.java
        )
    }

    private fun pageContentToString(url: String): String {
        val scanner = Scanner(URL(url).openStream(), StandardCharsets.UTF_8.toString())
        scanner.useDelimiter("\\A")
        return if (scanner.hasNext()) scanner.next() else ""
    }
}