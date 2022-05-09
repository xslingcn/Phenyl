package live.turna.phenyl.common.utils;

import com.mifmif.common.regex.Generex;

/**
 * <b>BindUtils</b><br>
 * Provide utils for binding.
 *
 * @since 2021/12/5 3:05
 */
public class BindUtils {

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
     * Check whether the provided QQ ID or Group ID is valid.
     * A valid QQ ID should be of 5-11 digits and not start with 0.
     *
     * @param qqid The QQ ID to be checked
     * @return If the QQ ID is valid.
     */
    public static Boolean isValidQQID(String qqid) {
        String pattern = "^[1-9]\\d{4,10}$";
        return qqid.matches(pattern);
    }

    /**
     * Generates random string to proceed verify.
     *
     * @param pattern The pattern of generating code string.
     * @return The generated random string of given length to verify.
     */
    public static String generateVerificationCode(String pattern) {
        Generex generex = new Generex(pattern);
        return generex.random();
    }

    /**
     * Check whether the code matches the generating pattern.
     *
     * @param code    Code to be checked.
     * @param pattern The pattern of generating code string.
     * @return If the code is valid.
     */
    public static Boolean isValidVerificationCode(String code, String pattern) {
        return code.matches(pattern);
    }
}