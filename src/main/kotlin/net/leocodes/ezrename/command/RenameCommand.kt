package net.leocodes.ezrename.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

object RenameCommand {
    private val FAILED_ENTITY_EXCEPTION = SimpleCommandExceptionType(TranslatableText("commands.rename.failed.entity"))

    private val FAILED_ITEMLESS_EXCEPTION = SimpleCommandExceptionType(TranslatableText("commands.rename.failed.itemless"))

    fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
        dispatcher.register(
            CommandManager.literal("rename").then(
                CommandManager.argument("targets", EntityArgumentType.entities()).then(
                    CommandManager.argument("name", StringArgumentType.greedyString())
                        .executes{
                            execute(it, EntityArgumentType.getEntities(it, "targets"), StringArgumentType.getString(it, "name"))
                        }
                )
            )
        )
    }

    @Throws(CommandSyntaxException::class)
    private fun execute(source: CommandContext<ServerCommandSource?>, targets: Collection<Entity>, name: String): Int {
        val var5: Iterator<*> = targets.iterator()
        while (var5.hasNext()) {
            val entity = var5.next() as Entity
            if (entity is LivingEntity) {
                val itemStack = entity.mainHandStack
                if (!itemStack.isEmpty) {
                    itemStack.setCustomName(Text.of(name))
                } else if (targets.size == 1) {
                    throw FAILED_ITEMLESS_EXCEPTION.create()
                }
            } else if (targets.size == 1) {
                throw FAILED_ENTITY_EXCEPTION.create()
            }
        }
        return 1
    }
}