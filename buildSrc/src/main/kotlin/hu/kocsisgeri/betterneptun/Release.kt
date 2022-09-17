import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Release {
    private const val versionMajor = 0
    private const val versionMinor = 2
    private const val versionPatch = 0

    val versionCode = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")).toInt()
    const val versionName = "$versionMajor.$versionMinor.$versionPatch"
}