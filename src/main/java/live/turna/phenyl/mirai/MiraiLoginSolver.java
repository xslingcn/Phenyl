package live.turna.phenyl.mirai;

import kotlin.coroutines.Continuation;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.LoginSolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Scanner;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>MiraiLoginSolver</b><br>
 * Custom login solver for mirai.
 *
 * @see net.mamoe.mirai.utils.LoginSolver
 * @since 2022/1/23 0:21
 */
public class MiraiLoginSolver extends LoginSolver {
    private final Logger LOGGER = LogManager.getLogger("MIRAI");

    @Nullable
    @Override
    public Object onSolvePicCaptcha(@NotNull Bot bot, @NotNull byte[] bytes, @NotNull Continuation<? super String> continuation) {
        LOGGER.warn(i18n("miraiPic"));
        String input = new Scanner(System.in).nextLine();
        return null;
    }

    @Nullable
    @Override
    public Object onSolveSliderCaptcha(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        LOGGER.warn(i18n("miraiSlider"));
        String input = new Scanner(System.in).nextLine();
        return null;
    }

    @Nullable
    @Override
    public Object onSolveUnsafeDeviceLoginVerify(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        LOGGER.warn(i18n("miraiVerify", url));
        String input = new Scanner(System.in).nextLine();
        if (input.equals("cancel")) return null;
        LOGGER.info(i18n("miraiSubmit"));
        return input;
    }
}