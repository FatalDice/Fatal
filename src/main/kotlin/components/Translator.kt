package uk.akane.fatal.components

import net.mamoe.mirai.utils.MiraiLogger
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.module.CommandModule

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

    fun getTranslation(
        templateName: VanillaStringContent.StringTypes,
        commandModule: CommandModule? = null
    ): String {
        var targetTemplate = stringTypesMap[templateName] ?: "Undefined variable"
        commandModule?.generateKeywordReplacements()?.forEach { pair ->
            targetTemplate = targetTemplate.replace(
                '{' + pair.key + '}',
                pair.value
            )
        }
        return targetTemplate

    }

}