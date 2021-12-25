package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.ImageUploadEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * <b>CImageUploadEvent</b><br>
 *
 * @since 2021/12/4 4:59
 */
public class CImageUploadEvent extends Event {
    private final ImageUploadEvent event;
    private boolean result;

    public CImageUploadEvent(ImageUploadEvent event) {
        this.event = event;
        this.result = event.getClass().toString().split("\\$")[1].equalsIgnoreCase("Succeed");
    }

}