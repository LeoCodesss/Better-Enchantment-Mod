package net.leocodes.better_enchantment

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.leocodes.better_enchantment.command.EnchantmentCommand
import net.leocodes.better_enchantment.command.UnenchantmentCommand
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

@Suppress("unused")

object BetterEnchantmentMod: ModInitializer {
    const val MOD_ID = "better_enchantment"

    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register( CommandRegistrationCallback { commandDispatcher: CommandDispatcher<ServerCommandSource>, _: CommandRegistryAccess, _: CommandManager.RegistrationEnvironment ->
            EnchantmentCommand.register(commandDispatcher)
            UnenchantmentCommand.register(commandDispatcher)
        })
    }
}


