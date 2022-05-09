package live.turna.phenyl.common.mirai;

import kotlin.coroutines.Continuation;
import live.turna.phenyl.common.config.Config;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.LoginSolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>MiraiLoginSolver</b><br>
 * Custom login solver for mirai.
 *
 * @see LoginSolver
 * @since 2022/1/23 0:21
 */
public class MiraiLoginSolver extends LoginSolver {
    private final Logger LOGGER = LogManager.getLogger("MIRAI");
    private final static BlockingQueue<String> ticketQueue = new ArrayBlockingQueue<>(1);

    @Override
    public boolean isSliderCaptchaSupported() {
        return true;
    }

    @Nullable
    @Override
    public Object onSolvePicCaptcha(@NotNull Bot bot, @NotNull byte[] bytes, @NotNull Continuation<? super String> continuation) {
        LOGGER.warn(i18n("miraiPic"));
        return null;
    }

    @Nullable
    @Override
    public Object onSolveSliderCaptcha(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        LOGGER.warn(i18n("miraiSlider"));
        LOGGER.warn(url);

        try {
            return ticketQueue.take();
        } catch (InterruptedException e) {
            if (Config.debug) e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @Override
    public Object onSolveUnsafeDeviceLoginVerify(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        LOGGER.warn(i18n("miraiVerify", url));
        String input = new Scanner(System.in).nextLine();
        LOGGER.info(i18n("miraiSubmit"));
        return input;
    }

    public static void addTicket(String ticket) {
        ticketQueue.clear();
        try {
            ticketQueue.put(ticket);
        } catch (InterruptedException e) {
            if (Config.debug) e.printStackTrace();
        }
    }
}