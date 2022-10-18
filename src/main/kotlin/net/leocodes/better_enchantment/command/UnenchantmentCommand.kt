package net.leocodes.better_enchantment.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.minecraft.command.argument.EnchantmentArgumentType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TranslatableText


object UnenchantmentCommand {
    val FAILED_NOENCHANTMENT_EXCEPTION = DynamicCommandExceptionType { enchantmentName: Any ->
        TranslatableText(
            "commands.enchant.failed.noenchantment",
            *arrayOf(enchantmentName)
        )
    }
    val FAILED_NOT_VALID_ENTITY_EXCEPTION = DynamicCommandExceptionType { entityName: Any ->
        TranslatableText(
            "commands.enchant.failed.not_valid_entity",
            *arrayOf(entityName)
        )
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
        dispatcher.register(
            CommandManager.literal("disenchant").requires {
                it.hasPermissionLevel(2)
            }.then(
                CommandManager.argument("targets", EntityArgumentType.entities()).then(
                    CommandManager.argument("enchantment", EnchantmentArgumentType.enchantment()).executes {
                        UnenchantmentCommand.execute(
                            it.source,
                            EntityArgumentType.getEntities(it, "targets"),
                            EnchantmentArgumentType.getEnchantment(it, "enchantment"),
                            0
                        )
                    }.then(
                        CommandManager.argument("level", IntegerArgumentType.integer()).executes {
                            UnenchantmentCommand.execute(
                                it.source,
                                EntityArgumentType.getEntities(it, "targets"),
                                EnchantmentArgumentType.getEnchantment(it, "enchantment"),
                                IntegerArgumentType.getInteger(it, "level")
                            )
                        }
                    )
                )
            )
        )
    }

    private fun keyToId(key: String): String {
        val key = key.split(".")
        return key[key.size - 1]
    }

    private fun idToName(id: String): String {
        val id = id.split(":")
        //get the last element of the array
        var finiName = id[id.size - 1]
        //replace all " with nothing
        finiName = finiName.replace("\"", "")
        return finiName
    }

    private fun keyToName(key: String): String {
        val key = key.split(".")
        return key[key.size - 1]
    }

    @Throws(CommandSyntaxException::class)
    private fun execute(
        source: ServerCommandSource,
        targets: Collection<Entity>,
        enchantment: Enchantment,
        level: Int
    ): Int {
        var i = 0
        val var5: Iterator<*> = targets.iterator()
        //send message to player
        while (var5.hasNext()) {
            val entity = var5.next() as Entity
            if (entity is LivingEntity) {
                if (entity.mainHandStack.hasEnchantments()) {
                    val enchantmentList = entity.mainHandStack.enchantments
                    val enchantmentListIterator = enchantmentList.iterator()
                    while (enchantmentListIterator.hasNext()) {
                        val enchantmentNbt = enchantmentListIterator.next()
                        if (idToName((enchantmentList.getCompound(enchantmentList.indexOf(enchantmentNbt)).get("id")).toString()) == keyToId(enchantment.translationKey)) {
                            if (level != 0) {
                                val enchantmentLevel =
                                    enchantmentList.getCompound(enchantmentList.indexOf(enchantmentNbt)).getInt("lvl")
                                //if the level of the enchantment is greater than the level we want to remove
                                if (level < enchantmentLevel) {
                                    val newLevel = enchantmentLevel - level
                                    //remove the enchantment
                                    //set the level of the enchantment
                                    enchantmentList.getCompound(enchantmentList.indexOf(enchantmentNbt))
                                        .putInt("lvl", newLevel)
                                    if (level == 1) {
                                        source.sendFeedback(
                                            TranslatableText(
                                                "commands.enchant.success.level.single",
                                                *arrayOf(
                                                    level,
                                                    keyToName(enchantment.translationKey)
                                                )
                                            ), true
                                        )
                                    } else {
                                        source.sendFeedback(
                                            TranslatableText(
                                                "commands.enchant.success.level.multiple",
                                                *arrayOf(
                                                    level,
                                                    keyToName(enchantment.translationKey)
                                                )
                                            ), true
                                        )
                                    }
                                } else {
                                    enchantmentListIterator.remove()
                                    source.sendFeedback(
                                        TranslatableText(
                                            "commands.enchant.success",
                                            *arrayOf(
                                                keyToName(enchantment.translationKey)
                                            )
                                        ), true
                                    )
                                }
                            } else {
                                enchantmentListIterator.remove()
                                source.sendFeedback(
                                    TranslatableText(
                                        "commands.enchant.success",
                                        *arrayOf(
                                            keyToName(enchantment.translationKey)
                                        )
                                    ), true
                                )
                            }
                            i++
                        }
                    }
                }
                if (i == 0) {
                    throw FAILED_NOENCHANTMENT_EXCEPTION.create(keyToName(enchantment.translationKey))
                }
            } else {
                throw FAILED_NOT_VALID_ENTITY_EXCEPTION.create(entity.displayName)
            }
        }
    return i
    }
}
