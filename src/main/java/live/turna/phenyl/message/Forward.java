package live.turna.phenyl.message;

import com.google.gson.Gson;
import live.turna.phenyl.Phenyl;
import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.database.Database;
import live.turna.phenyl.database.Player;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.message.schema.TencentMiniAppMessage;
import live.turna.phenyl.message.schema.TencentStructMessage;
import live.turna.phenyl.mirai.event.CGroupMessageEvent;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.message.ImageMessage.drawImageMessage;
import static live.turna.phenyl.message.ImageMessage.getImageFromURL;
import static live.turna.phenyl.utils.Message.altColor;
import static live.turna.phenyl.utils.Message.broadcastMessage;

import static live.turna.phenyl.utils.Mirai.sendGroup;
import static live.turna.phenyl.utils.Mirai.sendImage;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Forward</b><br>
 * Message forwarder.
 *
 * @since 2021/12/23 18:34
 */
public class Forward extends PhenylBase {
    /**
     * Forward a message to bungee.<br>
     * Phenyl will replace all format-variables to corresponding value except %message%;
     * %message% would be produced by {@link #formatter}, parsing json card messages or URLs and attach click and hover events.
     *
     * @param group    Group instance of which the message is from.
     * @param senderID The sender's QQ ID.
     * @param message  The message content.
     * @param nickName The sender's in-group name card or nickname, used for {@code sync} mode.
     * @param userName The sender's Minecraft username, used for {@code bind/command} mode.
     * @see CGroupMessageEvent#getSenderNameCardOrNick()
     */
    public static void forwardToBungee(Group group, Long senderID, String message, @Nullable String nickName, @Nullable String userName, @Nullable List<SingleMessage> images) {
        for (Player it : Phenyl.getMutedPlayer()) {
            if (it == null || it.qqid() == null) break;
            if (senderID.equals(it.qqid())) return;
        }
        String[] format = PhenylConfiguration.qq_to_server_format.split("%message%");
        Matcher matcher = Pattern.compile("&(?![\\s\\S]*&)\\d{1}").matcher(PhenylConfiguration.qq_to_server_format); // match the last color code occurrence
        String color = matcher.find() ? matcher.group() : "&f"; // if no color specified, fallback to white
        format[0] = format[0]
                .replace("%group_id%", String.valueOf(group.getId()))
                .replace("%group_name%", group.getName())
                .replace("%username%", userName != null ? userName : "")
                .replace("%nickname%", nickName != null ? nickName : "");
        broadcastMessage(formatter(message, format, color, images));
        if (PhenylConfiguration.save_message) {
            message = message.replaceAll("'", "''");
            Database.addMessage(message, group.getId(), senderID);
        }
    }

    /**
     * Match QQ card messages or messages with links.<br>
     * Card messages would be parsed as {@code [beginning string + prompt-[description] + trailing string]},
     * with a {@link ClickEvent.Action#OPEN_URL} and a {@link HoverEvent.Action#SHOW_TEXT} showing the message set in language file(clickToView).<br><br>
     * Messages containing links would be attached with {@link ClickEvent.Action#OPEN_URL} and {@link HoverEvent.Action#SHOW_TEXT} on each link as well.
     *
     * @param message The message content.
     * @param format  Possible beginning and trailing strings of %message% set in hte format pattern.
     * @param color   The last color code found before %message%.
     * @return A built {@link BaseComponent} message
     */
    private static BaseComponent[] formatter(String message, String[] format, String color, @Nullable List<SingleMessage> images) {
        // match QQ mini app messages
        if (message.contains("com.tencent.miniapp")) {
            TencentMiniAppMessage fromJson = new Gson().fromJson(message, TencentMiniAppMessage.class);
            TextComponent prompt = new TextComponent(fromJson.prompt + "-");
            prompt.setColor(ChatColor.GRAY);
            TextComponent title = new TextComponent("[" + fromJson.meta.detail1.desc + "]");
            title.setColor(ChatColor.DARK_AQUA);
            title.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, fromJson.meta.detail1.qqdocurl));
            title.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + i18n("clickToView") + fromJson.meta.detail1.qqdocurl)));

            return new ComponentBuilder()
                    .append(altColor(format[0]))
                    .append(prompt)
                    .append(title)
                    .append(format.length > 1 ? altColor(color + format[1]) : "")
                    .create();
        }
        // match QQ struct messages
        if (message.contains("com.tencent.structmsg")) {
            TencentStructMessage fromJson = new Gson().fromJson(message, TencentStructMessage.class);
            TextComponent prompt = new TextComponent(fromJson.prompt + "-");
            prompt.setColor(ChatColor.GRAY);
            TextComponent title = new TextComponent(fromJson.meta.news.desc);
            title.setColor(ChatColor.DARK_AQUA);
            title.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, fromJson.meta.news.jumpUrl));
            title.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + i18n("clickToView") + fromJson.meta.news.jumpUrl)));

            return new ComponentBuilder()
                    .append(altColor(format[0]))
                    .append(prompt)
                    .append(title)
                    .append(format.length > 1 ? altColor(color + format[1]) : "")
                    .create();
        }
        // match image messages
        if (images != null && !images.isEmpty()) {
            int matchCount = 0;
            String pattern = "\\u56fe\\u7247";
            Matcher match = Pattern.compile(pattern).matcher(message);
            List<String> other = List.of(message.split(pattern));
            ComponentBuilder result = new ComponentBuilder().append(altColor(format[0]));
            while (match.find()) {
                if (other.size() > matchCount) {
                    result.append(altColor(color + other.get(matchCount)))
                            .event((ClickEvent) null)
                            .event((HoverEvent) null);
                }
                String url = net.mamoe.mirai.Mirai.getInstance().queryImageUrl(Phenyl.getMiraiInstance().getBot(), (Image) images.get(matchCount));
                result.append(match.group())
                        .color(ChatColor.DARK_AQUA)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + i18n("clickToView") + url)));
                matchCount++;
            }
            if (other.size() > matchCount) {
                result.append(altColor(color + other.get(matchCount)))
                        .event((ClickEvent) null)
                        .event((HoverEvent) null);
            }
            return result.create();
        }

        // match links
        int matchCount = 0;
        String pattern = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Matcher match = Pattern.compile(pattern).matcher(message);
        List<String> other = List.of(message.split(pattern));
        ComponentBuilder result = new ComponentBuilder().append(altColor(format[0]));
        while (match.find()) {
            if (other.size() > matchCount) {
                result.append(altColor(color + other.get(matchCount)))
                        .event((ClickEvent) null)
                        .event((HoverEvent) null);
            }
            result.append(match.group())
                    .color(ChatColor.DARK_AQUA)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, match.group()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + i18n("clickToView") + match.group())));
            matchCount++;
        }
        if (other.size() > matchCount) {
            result.append(altColor(color + other.get(matchCount)))
                    .event((ClickEvent) null)
                    .event((HoverEvent) null);
        }
        if (matchCount != 0)
            return result.create();

        // random message
        return new ComponentBuilder()
                .append(altColor(format[0] + message + (format.length > 1 ? altColor(color + format[1]) : "")))
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
    public static void forwardToQQ(String message, String userName, String uuid, String subServer) {
        for (Player it : Phenyl.getMutedPlayer()) {
            if (uuid.equals(it.uuid())) return;
        }
        if (PhenylConfiguration.save_message) {
            message = message.replaceAll("'", "''");
            Database.addMessage(message, uuid);
        }

        String pattern = "https?:/(?:/[^/]+)+\\.(?:jpg|jpeg|gif|png)";
        Matcher matcher = Pattern.compile(pattern).matcher(message);
        if (matcher.matches() && PhenylConfiguration.forward_image) {
            if (PhenylConfiguration.server_to_qq_format.equals("image"))
                forwardImageMessage(i18n("imageMessage"), userName, uuid);
            else forwardPlainMessage(i18n("imageMessage"), userName, subServer);

            // retrieve and send image
            CompletableFuture<Boolean> futureGet = CompletableFuture.supplyAsync(() -> getImageFromURL(matcher.group()))
                    .orTimeout(5, TimeUnit.SECONDS).thenApplyAsync((@NotNull BufferedImage image) -> {
                        sendImage(image);
                        return true;
                    }).orTimeout(3, TimeUnit.SECONDS);
            return;
        }

        if (PhenylConfiguration.server_to_qq_format.equals("image"))
            forwardImageMessage(message, userName, uuid);
        else forwardPlainMessage(message, userName, subServer);

    }

    private static void forwardPlainMessage(String message, String userName, String subServer) {
        String format = PhenylConfiguration.server_to_qq_format
                .replace("%sub_server%", subServer)
                .replace("%username%", userName)
                .replace("%message%", message);
        MessageChain messageChain = new MessageChainBuilder().append(format).build();
        try {
            sendGroup(messageChain);
        } catch (NoSuchElementException e) {
            LOGGER.error(i18n("noSuchGroup"));
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
    }

    private static void forwardImageMessage(String message, String userName, String uuid) {
        try {
            sendImage(drawImageMessage(message, userName, uuid));
        } catch (NoSuchElementException e) {
            LOGGER.error(i18n("noSuchGroup"));
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
    }
}
