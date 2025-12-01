/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
 *
 * This mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this mod. If not, see <https://www.gnu.org/licenses/>.
 */

package opekope2.avm_staff.buildscript

import groovy.json.JsonSlurper
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateI18nEnum : AbstractCodegenTask() {
    init {
        enumName.convention("I18n")
    }

    @get:Input
    abstract val enumPackage: Property<String>

    @get:Input
    abstract val enumName: Property<String>

    @TaskAction
    fun run() {
        deleteOutputDir()

        val enumPackage = enumPackage.get()
        val enumName = enumName.get()

        val packageDir = outputDir.dir(enumPackage.replace('.', '/')).get()
        packageDir.asFile.mkdirs()

        val json = JsonSlurper().parse(inputs.files.singleFile) as Map<String, String>
        val members = json.entries.joinToString(separator = ",\n") { (key, value) ->
            val k = key.uppercase().replace("""[^a-zA-Z0-9]""".toRegex(), "_")
            """    $k("$key", "$value")"""
        }.trimStart()

        // language=kotlin
        val content = """
            |package $enumPackage
            |
            |import java.util.function.Supplier
            |import net.minecraft.text.Text
            |import org.jetbrains.annotations.ApiStatus
            |
            |@ApiStatus.Internal
            |enum class $enumName(private val key: String, private val fallback: String) {
            |    $members;
            |
            |    fun getText(vararg args: Any?) = Text.translatableWithFallback(key, fallback, *args)
            |
            |    fun getTranslation(vararg args: Any?) = getText(*args).getString()
            |
            |    fun supplyTranslation(vararg args: Any?) = Supplier { getTranslation(*args) }
            |}
        """.trimMargin()
        packageDir.file("$enumName.kt").asFile.writeText(content)
    }
}
