package live.turna.phenyl.message;

import live.turna.phenyl.Phenyl;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Player;
import live.turna.phenyl.mirai.event.CGroupMessageEvent;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static live.turna.phenyl.Phenyl.LOGGER;
import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.message.ImageMessage.drawImageMessage;
import static live.turna.phenyl.message.ImageMessage.getImageFromURL;
import static live.turna.phenyl.utils.Message.altColor;
import static live.turna.phenyl.utils.Message.broadcastMessage;
import static live.turna.phenyl.utils.Mirai.sendGroup;
import static live.turna.phenyl.utils.Mirai.sendImage;

/**
 * <b>Forward</b><br>
 * Message forwarder.
 *
 * @since 2021/12/23 18:34
 */
public class Forward {
    private static final Phenyl phenyl = Phenyl.getInstance();

    /**
     * Forward a message to bungee.<br>
     * Phenyl will replace all format-variables to corresponding value except %message%;
     * %message% would be produced by {@link #matchMessageType}, parsing json card messages or URLs and attach click and hover events.
     *
     * @param group    Group instance of which the message is from.
     * @param senderID The sender's QQ ID.
     * @param message  The message content.
     * @param nickName The sender's in-group name card or nickname, used for {@code sync} mode.
     * @see CGroupMessageEvent#getSenderNameCardOrNick()
     */
    public static void forwardToBungee(Group group, Long senderID, MessageChain message, @Nullable String nickName, @Nullable List<SingleMessage> images) {
        String messageString = message.contentToString();

        for (Player it : phenyl.getMutedPlayer()) {
            if (it == null) break;
            if (it.qqid() == null) continue;
            if (senderID.equals(it.qqid())) return;
        }
        if (PhenylConfiguration.save_message) {
            phenyl.getStorage().addMessage(messageString.replaceAll("'", "''"), group.getId(), senderID);
        }
        String userName = phenyl.getStorage().getBinding(senderID).mcname();
        if (PhenylConfiguration.forward_mode.equals("bind") && userName == null) return;

        String preText = PhenylConfiguration.qq_to_server_format
                .replace("%group_id%", String.valueOf(group.getId()))
                .replace("%group_name%", group.getName())
                .replace("%username%", userName != null ? userName : "")
                .replace("%nickname%", nickName != null ? nickName : "");
        // get the pattern before and after %message%
        String[] format = preText.split("%message%");
        Matcher matcher = Pattern.compile("&(?![\\s\\S]*&)\\d").matcher(PhenylConfiguration.qq_to_server_format); // get the last color code occurrence before %message%
        String color = matcher.find() ? matcher.group() : "&f"; // if no color specified, fallback to white
        broadcastMessage(matchMessageType(message, format, color, images));
    }

    public static void forwardToBungee(Group group, Long senderID, String message, @Nullable String nickName, @Nullable List<SingleMessage> images) {
        forwardToBungee(group, senderID, new MessageChainBuilder().append(message).build(), nickName, images);
    }

    /**
     * Match the types of messages.<br>
     * Card messages would be parsed as {@code [beginning string + prompt-[description] + trailing string]},
     * with a {@link net.md_5.bungee.api.chat.ClickEvent.Action#OPEN_URL} and a {@link net.md_5.bungee.api.chat.HoverEvent.Action#SHOW_TEXT} showing the message set in language file(clickToView).<br><br>
     * Messages containing links would be attached with {@link net.md_5.bungee.api.chat.ClickEvent.Action#OPEN_URL} and {@link net.md_5.bungee.api.chat.HoverEvent.Action#SHOW_TEXT} on each link as well.
     *
     * @param message The message content.
     * @param format  Possible beginning and trailing strings of %message% set in the configuration.
     * @param color   The last color code found before %message%.
     * @param images  The images in a single message.
     * @return A built {@link BaseComponent} message
     */
    private static BaseComponent[] matchMessageType(MessageChain message, String[] format, String color, @Nullable List<SingleMessage> images) {
        String messageString = message.contentToString();
        Formatter formatter = new Formatter(format, color, messageString);
        // match QQ music share messages
        if (message.size() == 3 && (message.get(2) instanceof MusicShare music)) {
            return formatter.groupMusicShare(music);
        }

        // match card messages
        if (message.size() == 2 && (message.get(1) instanceof LightApp || message.get(1) instanceof SimpleServiceMessage)) {
            // QQ XML messages
            if (messageString.startsWith("<?xml"))
                return formatter.groupXML();
            // QQ mini app messages
            if (messageString.contains("com.tencent.miniapp"))
                return formatter.groupMiniApp();
            // QQ struct messages
            if (messageString.contains("com.tencent.structmsg"))
                return formatter.groupStruct();
            // QQ group announcement
            if (messageString.contains("com.tencent.mannounce"))
                return formatter.groupAnnounce();
            return formatter.groupCardFallback();
        }

        // match messages with images
        if (images != null && !images.isEmpty()) {
            return formatter.groupImage(images);
        }
        // match links
        BaseComponent[] linkFormat = formatter.groupLink();
        if (linkFormat != null) return linkFormat;
        // random message
        return new ComponentBuilder()
                .append(altColor(format[0] + messageString + (format.length > 1 ? altColor(color + format[1]) : "")))
                .create();
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
    public static boolean forwardToQQ(String message, String userName, String uuid, String subServer) {
        for (Player it : phenyl.getMutedPlayer()) {
            if (uuid.equals(it.uuid())) return false;
        }
        if (PhenylConfiguration.save_message) {
            phenyl.getStorage().addMessage(message.replaceAll("'", "''"), uuid);
        }

        // if matches an image url
        String pattern = "https?:/(?:/[^/]+)+\\.(?:jpg|jpeg|gif|png)";
        Matcher matcher = Pattern.compile(pattern).matcher(message);
        if (matcher.matches() && PhenylConfiguration.forward_image)
            return forwardSingleImage(matcher.group(), userName, uuid, subServer);

        if (PhenylConfiguration.server_to_qq_format.equals("image"))
            return forwardImageMessage(message, userName, uuid);
        else return forwardPlainMessage(message, userName, subServer);
    }

    private static boolean forwardSingleImage(String url, String userName, String uuid, String subServer) {
        if (PhenylConfiguration.server_to_qq_format.equals("image"))
            forwardImageMessage(i18n("imageMessage"), userName, uuid);
        else forwardPlainMessage(i18n("imageMessage"), userName, subServer);

        // retrieve and send image
        CompletableFuture<Boolean> futureGet = CompletableFuture.supplyAsync(() -> getImageFromURL(url))
                .orTimeout(PhenylConfiguration.get_image_timeout, TimeUnit.SECONDS).thenApplyAsync((@NotNull BufferedImage image) -> {
                    try {
                        sendImage(image);
                        return true;
                    } catch (NoSuchElementException e) {
                        LOGGER.error(i18n("noSuchGroup", e.getLocalizedMessage()));
                        if (PhenylConfiguration.debug) e.printStackTrace();
                        return false;
                    }
                }).orTimeout(3, TimeUnit.SECONDS);
        return !futureGet.isCompletedExceptionally();
    }

    private static boolean forwardPlainMessage(String message, String userName, String subServer) {
        String format = PhenylConfiguration.server_to_qq_format
                .replace("%sub_server%", subServer)
                .replace("%username%", userName)
                .replace("%message%", message);
        MessageChain messageChain = new MessageChainBuilder().append(format).build();
        try {
            sendGroup(messageChain);
            return true;
        } catch (NoSuchElementException e) {
            LOGGER.error(i18n("noSuchGroup", e.getLocalizedMessage()));
            if (PhenylConfiguration.debug) e.printStackTrace();
            return false;
        }
    }

    private static boolean forwardImageMessage(String message, String userName, String uuid) {
        try {
            sendImage(drawImageMessage(message, userName, uuid));
            return true;
        } catch (NoSuchElementException e) {
            LOGGER.error(i18n("noSuchGroup", e.getLocalizedMessage()));
            if (PhenylConfiguration.debug) e.printStackTrace();
            return false;
        }
    }
}
