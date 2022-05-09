package live.turna.phenyl.bungee.loader

import live.turna.phenyl.bungee.BungeePhenyl
import net.md_5.bungee.api.plugin.Plugin

class PhenylBungeeLoader : Plugin() {
    var plugin = BungeePhenyl(this)
    override fun onLoad() {
        plugin.onLoad()
    }

    override fun onEnable() {
        plugin.onEnable()
    }

    override fun onDisable() {
        plugin.onDisable()
    }
}