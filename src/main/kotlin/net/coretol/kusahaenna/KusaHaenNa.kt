package net.coretol.kusahaenna

import net.coretol.kusahaenna.command.CommandKusa
import org.bukkit.plugin.java.JavaPlugin

class KusaHaenNa : JavaPlugin() {
    companion object {
        lateinit var instance: KusaHaenNa
    }

    override fun onEnable() {
        instance = this
        getCommand("kusa")!!.setExecutor(CommandKusa())
    }
}