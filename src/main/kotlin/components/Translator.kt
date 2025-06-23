package uk.akane.fatal.components

import net.mamoe.mirai.utils.MiraiLogger
import uk.akane.fatal.data.VanillaStringContent

class Translator(private val logger: MiraiLogger) {

    private val stringTypesMap: Map<VanillaStringContent.StringTypes, String> =
        VanillaStringContent.StringTypes.entries.associateWith { type ->
            val constantName = type.name
            try {
                VanillaStringContent::class.java.getDeclaredField(constantName).get(null) as String
            } catch (e: NoSuchFieldException) {
                logger.warning("Did not find original string with type name $constantName.")
                "Undefined variable"
            }
        }

    fun getTemplate(templateName: VanillaStringContent.StringTypes): String {
        // Get chosen template string
        // is user customizable.
        // If user customized a template string, then show user's customized string.
        // Else, use vanilla string.
        return stringTypesMap[templateName] ?: "Undefined variable"
    }

}