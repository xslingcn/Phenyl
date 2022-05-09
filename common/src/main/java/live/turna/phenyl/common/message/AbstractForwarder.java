package live.turna.phenyl.common.message;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.MiraiUtils;
import net.kyori.adventure.text.Component;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SingleMessage;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>AbstractForwarder</b><br>
 * *
 *
 * @since 2022/4/8 0:30
 */
public abstract class AbstractForwarder<P extends AbstractPhenyl> {
    private final transient P phenyl;
    private final transient Logger LOGGER;

    public AbstractForwarder(P plugin) {
        phenyl = plugin;
        LOGGER = phenyl.getLogger();
    }

    public void forwardToServer(Group group, Long senderID, MessageChain message, @Nullable String nickName, @Nullable List<SingleMessage> images) {
        String messageString = message.contentToString();

        for (Player it : phenyl.getMutedPlayer()) {
            if (it == null) break;
            if (it.qqid() == null) continue;
            if (senderID.equals(it.qqid())) return;
        }
        if (Config.save_message) {
            phenyl.getStorage().addMessage(messageString.replaceAll("'", "''"), group.getId(), senderID);
        }
        String userName = phenyl.getStorage().getBinding(senderID).mcname();
        if (Config.forward_mode.equals("bind") && userName == null) return;

        String preText = Config.qq_to_server_format
                .replace("%group_id%", String.valueOf(group.getId()))
                .replace("%group_name%", group.getName())
                .replace("%username%", userName != null ? userName : "")
                .replace("%nickname%", nickName != null ? nickName : "");
        // get the pattern before and after %message%
        String[] format = preText.split("%message%");
        Matcher matcher = Pattern.compile("&(?![\\s\\S]*&)\\d").matcher(Config.qq_to_server_format); // get the last color code occurrence before %message%
        String color = matcher.find() ? matcher.group() : "&f"; // if no color specified, fallback to white
        phenyl.getMessenger().sendAllServer(matchMessageType(message, format, color, images));
    }

    public void forwardToServer(Group group, Long senderID, String message, @Nullable String nickName, @Nullable List<SingleMessage> images) {
        forwardToServer(group, senderID, new MessageChainBuilder().append(message).build(), nickName, images);
    }

    public Component matchMessageType(MessageChain message, String[] format, String color, @Nullable List<SingleMessage> images) {
        return new Formatter<>(phenyl, format, color, message, images).get();
    }

    /**
     * Forward a message to QQ group.<br>
     * This will only send messages to group which is in {@code PhenylConfiguration.enabled_groups}.
     *
     * @param message   The message content.
     * @param userName  The sender's Minecraft username.
     * @param uuid      The sender's Minecraft UUID.
     * @param subServer In which sub server the message is sent.
     */
    public boolean forwardToQQ(String message, String userName, String uuid, String subServer) {
        for (Player it : phenyl.getMutedPlayer()) {
            if (uuid.equals(it.uuid())) return false;
        }
        if (Config.save_message) {
            phenyl.getStorage().addMessage(message.replaceAll("'", "''"), uuid);
        }

        // if matches an image url
        String pattern = "https?:/(?:/[^/]+)+\\.(?:jpg|jpeg|gif|png)";
        Matcher matcher = Pattern.compile(pattern).matcher(message);
        if (matcher.matches() && Config.forward_image)
            return forwardSingleImage(matcher.group(), userName, uuid, subServer);

        if (Config.server_to_qq_format.equals("image"))
            return forwardImageMessage(message, userName, uuid);
        else return forwardPlainMessage(message, userName, subServer);
    }

    private boolean forwardSingleImage(String url, String userName, String uuid, String subServer) {
        if (Config.server_to_qq_format.equals("image"))
            forwardImageMessage(i18n("imageMessage"), userName, uuid);
        else forwardPlainMessage(i18n("imageMessage"), userName, subServer);

        // retrieve and send image
        CompletableFuture<Boolean> futureGet = CompletableFuture.supplyAsync(() -> new ImageMessage(phenyl).getImageFromURL(url))
                .orTimeout(Config.get_image_timeout, TimeUnit.SECONDS).thenApplyAsync((@NotNull BufferedImage image) -> {
                    try {
                        new MiraiUtils(phenyl).sendImage(image);
                        return true;
                    } catch (NoSuchElementException e) {
                        LOGGER.error(i18n("noSuchGroup", e.getLocalizedMessage()));
                        if (Config.debug) e.printStackTrace();
                        return false;
                    }
                }).orTimeout(3, TimeUnit.SECONDS);
        return !futureGet.isCompletedExceptionally();
    }

    private boolean forwardPlainMessage(String message, String userName, String subServer) {
        String format = Config.server_to_qq_format
                .replace("%sub_server%", subServer)
                .replace("%username%", userName)
                .replace("%message%", message);
        MessageChain messageChain = new MessageChainBuilder().append(format).build();
        try {
            new MiraiUtils(phenyl).sendGroup(messageChain);
            return true;
        } catch (NoSuchElementException e) {
            LOGGER.error(i18n("noSuchGroup", e.getLocalizedMessage()));
            if (Config.debug) e.printStackTrace();
            return false;
        }
    }

    private boolean forwardImageMessage(String message, String userName, String uuid) {
        try {
            new MiraiUtils(phenyl).sendImage(new ImageMessage(phenyl).drawImageMessage(message, userName, uuid));
            return true;
        } catch (NoSuchElementException e) {
            LOGGER.error(i18n("noSuchGroup", e.getLocalizedMessage()));
            if (Config.debug) e.printStackTrace();
            return false;
        }
    }
}