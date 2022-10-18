package net.leocodes.better_enchantment.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.argument.EnchantmentArgumentType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TranslatableText

object EnchantmentCommand {
    val FAILED_ENTITY_EXCEPTION = DynamicCommandExceptionType { entityName: Any ->
        TranslatableText(
            "commands.enchant.failed.entity",
            *arrayOf(entityName)
        )
    }
    val FAILED_ITEMLESS_EXCEPTION = DynamicCommandExceptionType { entityName: Any ->
        TranslatableText(
            "commands.enchant.failed.itemless",
            *arrayOf(entityName)
        )
    }
    val FAILED_EXCEPTION = SimpleCommandExceptionType(TranslatableText("commands.enchant.failed"))

    fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
        dispatcher.register(
            CommandManager.literal("enchant").requires {
                it.hasPermissionLevel(2)}.then(
                    CommandManager.argument("targets", EntityArgumentType.entities()).then(
                        CommandManager.argument("enchantment", EnchantmentArgumentType.enchantment()).executes {
                            execute(it.source, EntityArgumentType.getEntities(it, "target"), EnchantmentArgumentType.getEnchantment(it, "enchantment"), 1)}.then(
                                CommandManager.argument("level", IntegerArgumentType.integer()).executes {
                                    execute(it.source, EntityArgumentType.getEntities(it, "targets"), EnchantmentArgumentType.getEnchantment(it, "enchantment"), IntegerArgumentType.getInteger(it, "level")
                                    )
                        }
                    )
                )
            )
        )
    }

    @Throws(CommandSyntaxException::class)
    private fun execute(source: ServerCommandSource, targets: Collection<Entity>, enchantment: Enchantment, level: Int): Int {
            var i = 0
            val var5: Iterator<*> = targets.iterator()
            while (true) {
                while (true) {
                    while (true) {
                        while (var5.hasNext()) {
                            val entity = var5.next() as Entity
                            if (entity is LivingEntity) {
                                val livingEntity = entity
                                val itemStack = livingEntity.mainHandStack
                                if (!itemStack.isEmpty) {
                                    itemStack.addEnchantment(enchantment, level)
                                    ++i
                                } else if (targets.size == 1) {
                                    throw FAILED_ITEMLESS_EXCEPTION.create(livingEntity.name.string)
                                }
                            } else if (targets.size == 1) {
                                throw FAILED_ENTITY_EXCEPTION.create(entity.name.string)
                            }
                        }
                        if (i == 0) {
                            throw FAILED_EXCEPTION.create()
                        }
                        if (targets.size == 1) {
                            source.sendFeedback(
                                TranslatableText(
                                    "commands.enchant.success.single",
                                    *arrayOf<Any>(enchantment.getName(level), targets.iterator().next().displayName)
                                ), true
                            )
                        } else {
                            source.sendFeedback(
                                TranslatableText(
                                    "commands.enchant.success.multiple",
                                    *arrayOf(enchantment.getName(level), targets.size)
                                ), true
                            )
                        }
                        return i
                    }
                }
            }
        }
    }
