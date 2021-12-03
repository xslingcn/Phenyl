package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.ImageUploadEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * @author xsling
 * @version 1.0
 * @since 2021/12/4 4:59
 */
public class CImageUploadEvent extends Event {
    private final ImageUploadEvent event;
    private final Boolean result;

    public CImageUploadEvent(ImageUploadEvent event, Boolean result) {
        this.event = event;
        this.result = result;
    }

}