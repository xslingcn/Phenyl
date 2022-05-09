package live.turna.phenyl.common.message;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.message.schema.*;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static live.turna.phenyl.common.message.I18n.i18n;
import static live.turna.phenyl.common.message.messenger.AbstractMessenger.altColor;

/**
 * <b>Formatter</b><br>
 * *
 *
 * @since 2022/4/11 12:33
 */
public class Formatter<P extends AbstractPhenyl> {
    private final transient P phenyl;
    private final transient String[] format;
    private final transient String color;
    private final transient MessageChain message;
    private final transient String messageString;
    private final transient List<SingleMessage> images;

    public Formatter(P plugin, String[] format, String color, MessageChain message, @Nullable List<SingleMessage> images) {
        phenyl = plugin;
        this.format = format;
        this.color = color;
        this.message = message;
        this.messageString = message.contentToString();
        this.images = images;
    }

    public Component get() {
        String messageString = message.contentToString();
        // match QQ music share messages
        if (message.size() == 3 && (message.get(2) instanceof MusicShare music)) {
            return groupMusicShare(music);
        }

        // match card messages
        if (message.size() == 2 && (message.get(1) instanceof LightApp || message.get(1) instanceof SimpleServiceMessage)) {
            // QQ XML messages
            if (messageString.startsWith("<?xml"))
                return groupXML();
            // QQ mini app messages
            if (messageString.contains("com.tencent.miniapp"))
                return groupMiniApp();
            // QQ struct messages
            if (messageString.contains("com.tencent.structmsg"))
                return groupStruct();
            // QQ group announcement
            if (messageString.contains("com.tencent.mannounce"))
                return groupAnnounce();
            return groupCardFallback();
        }

        if (message.size() == 2 && message.get(1) instanceof FlashImage flashImage)
            return groupFlashImage(flashImage);

        // match messages with images
        if (images != null && !images.isEmpty()) {
            return groupImage();
        }
        // match links
        Component linkFormat = groupLink();
        if (linkFormat != null) return linkFormat;
        // random message
        return groupRandom();
    }

    Component groupMiniApp() {
        GroupMiniAppMessage fromJson = new Gson().fromJson(messageString, GroupMiniAppMessage.class);
        TextComponent prompt = Component.text(fromJson.prompt + "-", NamedTextColor.GRAY);
        TextComponent title = Component.text("[" + fromJson.meta.detail1.desc + "]")
                .color(NamedTextColor.DARK_AQUA)
                .clickEvent(ClickEvent.openUrl(fromJson.meta.detail1.qqdocurl))
                .hoverEvent(HoverEvent.showText(Component.text(i18n("clickToView") + fromJson.meta.detail1.qqdocurl, NamedTextColor.YELLOW)));

        return Component.text(altColor(format[0]))
                .append(prompt)
                .append(title)
                .append(Component.text(format.length > 1
                        ? altColor(color + format[1])
                        : ""));
    }


    Component groupAnnounce() {
        GroupAnnounceMessage fromJson = new Gson().fromJson(messageString, GroupAnnounceMessage.class);
        TextComponent prompt = Component.text(fromJson.prompt, NamedTextColor.GRAY);

        return Component.text(altColor(format[0]))
                .append(prompt)
                .append(Component.text(format.length > 1
                        ? altColor(color + format[1])
                        : ""));
    }


    Component groupStruct() {
        GroupStructMessage fromJson = new Gson().fromJson(messageString, GroupStructMessage.class);
        TextComponent prompt = Component.text(fromJson.prompt + "-", NamedTextColor.GRAY);
        TextComponent title = Component.text(fromJson.meta.news.desc)
                .color(NamedTextColor.DARK_AQUA)
                .clickEvent(ClickEvent.openUrl(fromJson.meta.news.jumpUrl))
                .hoverEvent(HoverEvent.showText(Component.text(i18n("clickToView") + fromJson.meta.news.jumpUrl, NamedTextColor.YELLOW)));

        return Component.text(altColor(format[0]))
                .append(prompt)
                .append(title)
                .append(Component.text(format.length > 1
                        ? altColor(color + format[1])
                        : ""));
    }

    Component groupXML() {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            GroupXMLMessage.msg fromXML = xmlMapper.readValue(messageString, GroupXMLMessage.msg.class);
            TextComponent prompt = Component.text(fromXML.brief + "-", NamedTextColor.GRAY);
            TextComponent title = Component.text("[" + fromXML.item.summary + "]")
                    .color(NamedTextColor.DARK_AQUA)
                    .clickEvent(ClickEvent.openUrl(fromXML.url))
                    .hoverEvent(HoverEvent.showText(Component.text(i18n("clickToView") + fromXML.url, NamedTextColor.YELLOW)));

            return Component.text(altColor(format[0]))
                    .append(prompt)
                    .append(title)
                    .append(Component.text(format.length > 1
                            ? altColor(color + format[1])
                            : ""));
        } catch (Exception e) {
            if (Config.debug) e.printStackTrace();
        }
        return Component.text("");
    }

    Component groupCardFallback() {
        GroupCardFallbackMessage fromJson = new Gson().fromJson(messageString, GroupCardFallbackMessage.class);
        TextComponent prompt = Component.text(fromJson.prompt + "-", NamedTextColor.GRAY);
        TextComponent title = Component.text(fromJson.desc)
                .color(NamedTextColor.DARK_AQUA);

        return Component.text(altColor(format[0]))
                .append(prompt)
                .append(title)
                .append(Component.text(format.length > 1
                        ? altColor(color + format[1])
                        : ""));
    }

    Component groupMusicShare(MusicShare music) {
        TextComponent summary = Component.text(music.getSummary() + " - ", NamedTextColor.GRAY);
        TextComponent title = Component.text(music.getTitle())
                .color(NamedTextColor.DARK_AQUA)
                .clickEvent(ClickEvent.openUrl(music.getJumpUrl()))
                .hoverEvent(HoverEvent.showText(Component.text(i18n("clickToView") + music.getJumpUrl(), NamedTextColor.YELLOW)));

        return Component.text(altColor(format[0]))
                .append(summary)
                .append(title)
                .append(Component.text(format.length > 1
                        ? altColor(color + format[1])
                        : ""));
    }

    Component groupImage() {
        int matchCount = 0;
        String pattern = "\\u56fe\\u7247|\\u52a8\\u753b\\u8868\\u60c5";
        Matcher match = Pattern.compile(pattern).matcher(messageString);
        List<String> other = List.of(messageString.split(pattern));
        TextComponent.Builder result = Component.text().content(altColor(format[0]));
        while (match.find()) {
            if (other.size() > matchCount) {    // append the words other than "\\u56fe\\u7247" and clear events
                result.append(
                        Component.text(altColor(color + other.get(matchCount)))
                                .clickEvent(null)
                                .hoverEvent(null));
            }
            String url = net.mamoe.mirai.Mirai.getInstance().queryImageUrl(phenyl.getMirai().getBot(), (Image) images.get(matchCount));
            result.append(
                    Component.text(match.group(), NamedTextColor.DARK_AQUA)
                            .clickEvent(ClickEvent.openUrl(url))
                            .hoverEvent(HoverEvent.showText(Component.text(i18n("clickToView") + url, NamedTextColor.YELLOW))));
            matchCount++;
        }
        if (other.size() > matchCount) {
            result.append(
                    Component.text(altColor(color + other.get(matchCount)))
                            .clickEvent(null)
                            .hoverEvent(null));
        }
        return result.build();
    }

    Component groupFlashImage(FlashImage flashImage) {
        String url = net.mamoe.mirai.Mirai.getInstance().queryImageUrl(phenyl.getMirai().getBot(), flashImage.getImage());
        return Component.text(altColor(format[0]))
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text(messageString.substring(1, messageString.length() - 1), NamedTextColor.DARK_AQUA)
                        .clickEvent(ClickEvent.openUrl(url))
                        .hoverEvent(HoverEvent.showText(Component.text(i18n("clickToView") + url, NamedTextColor.YELLOW))))
                .append(Component.text("]", NamedTextColor.GRAY))
                .append(Component.text(format.length > 1
                        ? altColor(color + format[1])
                        : ""));
    }

    Component groupLink() {
        int linkCount = 0;
        String pattern = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Matcher match = Pattern.compile(pattern).matcher(messageString);
        List<String> other = List.of(messageString.split(pattern));
        TextComponent.Builder result = Component.text().content(altColor(format[0]));
        while (match.find()) {
            if (other.size() > linkCount) {
                result.append(
                        Component.text(altColor(color + other.get(linkCount)))
                                .clickEvent(null)
                                .hoverEvent(null)
                );
            }
            result.append(
                    Component.text(match.group(), NamedTextColor.DARK_AQUA)
                            .clickEvent(ClickEvent.openUrl(match.group()))
                            .hoverEvent(HoverEvent.showText(Component.text(i18n("clickToView") + match.group(), NamedTextColor.YELLOW)))
            );
            linkCount++;
        }
        if (other.size() > linkCount) {
            result.append(
                    Component.text(altColor(color + other.get(linkCount)))
                            .clickEvent(null)
                            .hoverEvent(null)
            );
        }
        if (linkCount != 0) return result.build();
        return null;
    }

    Component groupRandom() {
        return Component.text(altColor(format[0] + messageString))
                .append(Component.text(format.length > 1
                        ? altColor(color + format[1])
                        : ""));
    }
}