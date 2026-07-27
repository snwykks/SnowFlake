package io.snwykks.snowFlake.core.util

import com.charleskorn.kaml.MultiLineStringStyle
import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.KSerializer
import java.io.File

class FileFacade<T : Any>(fileName: String, dataFolder: File, private val serializer: KSerializer<T>, private val default: () -> T) {
    private val file = File(dataFolder, fileName)
    private val yaml = Yaml(
        configuration = YamlConfiguration(
            singleLineStringStyle = SingleLineStringStyle.Plain,
            breakScalarsAt = Int.MAX_VALUE,
            multiLineStringStyle = MultiLineStringStyle.SingleQuoted,
        )
    )

    var value: T = load()
        private set

    private fun load(): T {
        if (!file.exists()) {
            val defaultValue = default()
            save(defaultValue)
            return defaultValue
        }
        return yaml.decodeFromString(serializer, file.readText())
    }

    fun save(newValue: T = value) {
        file.parentFile?.mkdirs()
        file.writeText(formatter(newValue))
        value = newValue
    }

    fun reload() {
        value = load()
    }

    private fun formatter(newValue: T): String {
        return yaml.encodeToString(serializer, newValue)
            .replace("# ^ENTER", "\n# ")
    }
}