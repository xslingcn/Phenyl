package live.turna.phenyl.message;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import live.turna.phenyl.Phenyl;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.message.schema.*;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MusicShare;
import net.mamoe.mirai.message.data.SingleMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Message.altColor;

/**
 * <b>Formatter</b><br>
 * Formats special messages.
 *
 * @since 2022/2/23 16:28
 */
record Formatter(String[] format, String color, String message) {

    BaseComponent[] groupMiniApp() {
        GroupMiniAppMessage fromJson = new Gson().fromJson(message, GroupMiniAppMessage.class);
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

    BaseComponent[] groupAnnounce() {
        GroupAnnounceMessage fromJson = new Gson().fromJson(message, GroupAnnounceMessage.class);
        TextComponent prompt = new TextComponent(fromJson.prompt);
        prompt.setColor(ChatColor.GRAY);

        return new ComponentBuilder()
                .append(altColor(format[0]))
                .append(prompt)
                .append(format.length > 1 ? altColor(color + format[1]) : "")
                .create();
    }

    BaseComponent[] groupStruct() {
        GroupStructMessage fromJson = new Gson().fromJson(message, GroupStructMessage.class);
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

    BaseComponent[] groupXML() {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            GroupXMLMessage.msg fromXML = xmlMapper.readValue(message, GroupXMLMessage.msg.class);
            TextComponent prompt = new TextComponent(fromXML.brief + "-");
            prompt.setColor(ChatColor.GRAY);
            TextComponent title = new TextComponent("[" + fromXML.item.summary + "]");
            title.setColor(ChatColor.DARK_AQUA);
            title.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, fromXML.url));
            title.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + i18n("clickToView") + fromXML.url)));

            return new ComponentBuilder()
                    .append(altColor(format[0]))
                    .append(prompt)
                    .append(title)
                    .append(format.length > 1 ? altColor(color + format[1]) : "")
                    .create();
        } catch (Exception e) {
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return new BaseComponent[]{};
    }

    BaseComponent[] groupCardFallback() {
        GroupCardFallbackMessage fromJson = new Gson().fromJson(message, GroupCardFallbackMessage.class);
        TextComponent prompt = new TextComponent(fromJson.prompt + "-");
        prompt.setColor(ChatColor.GRAY);
        TextComponent title = new TextComponent(fromJson.desc);
        title.setColor(ChatColor.DARK_AQUA);

        return new ComponentBuilder()
                .append(altColor(format[0]))
                .append(prompt)
                .append(title)
                .append(format.length > 1 ? altColor(color + format[1]) : "")
                .create();
    }

    BaseComponent[] groupMusicShare(MusicShare music) {
        TextComponent summary = new TextComponent(music.getSummary() + " - ");
        summary.setColor(ChatColor.GRAY);
        TextComponent title = new TextComponent(music.getTitle());
        title.setColor(ChatColor.DARK_AQUA);
        title.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, music.getJumpUrl()));
        title.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + i18n("clickToView") + music.getJumpUrl())));

        return new ComponentBuilder()
                .append(altColor(format[0]))
                .append(summary)
                .append(title)
                .append(format.length > 1 ? altColor(color + format[1]) : "")
                .create();
    }

    BaseComponent[] groupImage(List<SingleMessage> images) {
        int matchCount = 0;
        String pattern = "\\u56fe\\u7247";
        Matcher match = Pattern.compile(pattern).matcher(message);
        List<String> other = List.of(message.split(pattern));
        ComponentBuilder result = new ComponentBuilder().append(altColor(format[0]));
        while (match.find()) {
            if (other.size() > matchCount) {    // append the words other than "\\u56fe\\u7247" and clear events
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

    BaseComponent[] groupLink() {
        int linkCount = 0;
        String pattern = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Matcher match = Pattern.compile(pattern).matcher(message);
        List<String> other = List.of(message.split(pattern));
        ComponentBuilder result = new ComponentBuilder().append(altColor(format[0]));
        while (match.find()) {
            if (other.size() > linkCount) {
                result.append(altColor(color + other.get(linkCount)))
                        .event((ClickEvent) null)
                        .event((HoverEvent) null);
            }
            result.append(match.group())
                    .color(ChatColor.DARK_AQUA)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, match.group()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + i18n("clickToView") + match.group())));
            linkCount++;
        }
        if (other.size() > linkCount) {
            result.append(altColor(color + other.get(linkCount)))
                    .event((ClickEvent) null)
                    .event((HoverEvent) null);
        }
        if (linkCount != 0) return result.create();
        return null;
    }

}