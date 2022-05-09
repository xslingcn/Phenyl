package live.turna.phenyl.bungee

import live.turna.phenyl.bungee.instance.*
import live.turna.phenyl.bungee.listener.BungeeListenerManager
import live.turna.phenyl.bungee.listener.MiraiListenerManager
import live.turna.phenyl.common.config.Config
import live.turna.phenyl.common.dependency.Log4jLoader
import live.turna.phenyl.common.instance.PSender
import live.turna.phenyl.common.listener.AbstractMiraiListenerManager
import live.turna.phenyl.common.plugin.AbstractPhenyl
import live.turna.phenyl.common.utils.MessageUtils
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.logging.Logger

class BungeePhenyl(val loader: Plugin) : AbstractPhenyl() {

    private val miraiListenerManager = MiraiListenerManager()

    private lateinit var bungeeListenerManager: BungeeListenerManager

    private lateinit var bungeeConfig: BungeeConfig

    private lateinit var senderFactory: BungeeSenderFactory

    private lateinit var forwarder: BungeeForwarder

    private lateinit var messenger: BungeeMessenger

    fun onLoad() {
        super.load()
    }

    fun onEnable() {
        super.enable()
    }

    fun onDisable() {
        super.disable()
    }

    override fun getMiraiListenerManager(): AbstractMiraiListenerManager {
        return miraiListenerManager
    }

    override fun setupLog4j(): Boolean {
        return try {
            Log4jLoader(this).onLoad()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    override fun initSenderFactory() {
        senderFactory = BungeeSenderFactory(this)
    }

    override fun initForwarder() {
        forwarder = BungeeForwarder(this)
    }

    override fun initMessenger() {
        messenger = BungeeMessenger(this)
    }

    override fun loadConfig() {
        bungeeConfig = BungeeConfig(this)
        bungeeConfig.load()
    }

    override fun postConfig(): Boolean {
        return bungeeConfig.postLoad()
    }

    override fun registerCommand() {
        ProxyServer.getInstance().pluginManager.registerCommand(loader, BungeeCommand(this, "phenyl", "ph"))
    }

    override fun startListening() {
        bungeeListenerManager = BungeeListenerManager(this)
        bungeeListenerManager.start()
    }

    override fun stopListening() {
        bungeeListenerManager.end()
    }

    override fun initMetrics() {
        Metrics(loader, 14309)
    }

    override fun getForwarder(): BungeeForwarder {
        return forwarder
    }

    override fun getMessenger(): BungeeMessenger {
        return messenger
    }

    override fun getSenderFactory(): BungeeSenderFactory {
        return senderFactory
    }

    override fun getVersion(): String {
        return loader.description.version
    }

    override fun getPlayers(): Collection<PSender> {
        val players: MutableCollection<PSender> = ArrayList()
        ProxyServer.getInstance().players.forEach(Consumer { player: ProxiedPlayer ->
            players.add(
                senderFactory.wrap(player)
            )
        })
        return players
    }

    override fun getPlatform(): String {
        return "BUNGEE"
    }

    override fun getDir(): File {
        return loader.dataFolder
    }

    override fun getNativeLogger(): Logger {
        return loader.logger
    }

    override fun getPlayer(username: String): PSender {
        return getSenderFactory().wrap(ProxyServer.getInstance().getPlayer(username))
    }

    override fun getPlayer(uuid: UUID): PSender {
        return getSenderFactory().wrap(ProxyServer.getInstance().getPlayer(uuid))
    }

    override fun getOnlineList(): HashMap<String, String> {
        val result = HashMap<String, String>()
        ProxyServer.getInstance().servers.forEach { (_: String?, serverInfo: ServerInfo) ->
            val players = StringBuilder()
            serverInfo.players.forEach(Consumer { player: ProxiedPlayer -> players.append(player.name).append(",") })
            if (players.isEmpty()) return@forEach
            result[MessageUtils(this).getServerName(serverInfo.name)] = players.substring(0, players.length - 1)
        }
        return result
    }

    override fun getStatus(): HashMap<String, Boolean> {
        val messageUtils = MessageUtils(this)
        val serverStatus = HashMap<String, Boolean>()
        Config.enabled_servers.forEach(Consumer { server: String? ->
            if (!ProxyServer.getInstance().servers.containsKey(server)) {
                serverStatus[messageUtils.getServerName(server)] = false
                return@Consumer
            }
            ProxyServer.getInstance().servers[server]!!
                .ping { _: ServerPing?, error: Throwable? ->
                    serverStatus[messageUtils.getServerName(server)] = error == null
                }
        })
        while (serverStatus.size != Config.enabled_servers.size) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                if (Config.debug) e.printStackTrace()
            }
        }
        return serverStatus
    }

    override fun getOnlineCount(): Int {
        val onlineCount = AtomicInteger()
        onlineCount.getAndSet(0)
        ProxyServer.getInstance().servers.forEach { (_: String?, serverInfo: ServerInfo) ->
            serverInfo.players.forEach(
                Consumer { onlineCount.getAndIncrement() })
        }
        return onlineCount.get()
    }

    override fun isProxy(): Boolean {
        return true
    }

    override fun <T> registerListener(listener: T) {
        ProxyServer.getInstance().pluginManager.registerListener(loader, listener as Listener)
    }

    override fun unregisterListeners() {
        ProxyServer.getInstance().pluginManager.unregisterListeners(loader)
    }

    override fun getResourceAsStream(name: String): InputStream {
        return loader.getResourceAsStream(name)
    }
}