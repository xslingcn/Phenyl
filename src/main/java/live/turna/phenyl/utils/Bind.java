package live.turna.phenyl.utils;

import com.mifmif.common.regex.Generex;

/**
 * <b>BindUtils</b><br>
 * Provide utils for binding.
 *
 * @since 2021/12/5 3:05
 */
public class Bind {

    /**
     * Check whether the provided username is valid for Minecraft.
     *
     * @param userName The username to be checked.
     * @return If the username is valid.
     */
    public static Boolean isValidUsername(String userName) {
        String pattern = "[_a-zA-Z0-9]{3,16}";
        return userName.matches(pattern);
    }

    /**
     * Generates random string to proceed verify.
     *
     * @param length The length of string.
     * @return The generated random string of given length to verify.
     */
    public static String generateVerificationCode(Integer length) {
        String pattern = "[0-9]{" + length.toString() + "}";
        Generex generex = new Generex(pattern);
        return generex.random();
    }

    /**
     * Check whether the code matches the generating pattern.
     *
     * @param code   Code to be checked.
     * @param length The length of code string.
     * @return If the code is valid.
     */
    public static Boolean isValidVerificationCode(String code, Integer length) {
        String pattern = "[0-9]{" + length.toString() + "}";
        return code.matches(pattern);
    }
}