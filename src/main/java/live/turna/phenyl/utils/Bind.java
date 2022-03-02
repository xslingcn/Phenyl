package live.turna.phenyl.utils;

import com.mifmif.common.regex.Generex;
import live.turna.phenyl.bind.BindResult;
import live.turna.phenyl.config.PhenylConfiguration;

import static live.turna.phenyl.bind.BindHandler.handleConfirm;
import static live.turna.phenyl.message.I18n.i18n;

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

    /**
     * @param userName The Minecraft username to request a confirmation.
     * @param code     The code to be checked.
     * @return The binding success message. Which are 1). bindSuccess when a new binding is successfully added;
     * 2). bindNoChange when the binding already exists and no operation is done; 3). changeBind when an existing binding is updated.
     * @throws IllegalArgumentException invalidCode: Verification code is neither format-correct nor valid.
     * @throws IllegalArgumentException bindFail: The request is valid, but binding attempt failed while operating database.
     */
    public static String verifier(String userName, String code) throws IllegalArgumentException {
        // Code not matching generating regex.
        if (!isValidVerificationCode(code, PhenylConfiguration.verification))
            throw new IllegalArgumentException(i18n("invalidCode"));

        BindResult bindResult = handleConfirm(userName, code);

        // Code not found in binding queue.
        if (bindResult == null) throw new IllegalArgumentException(i18n("invalidCode"));

        // Failed while updating the database.
        if (!bindResult.querySucceeded()) throw new IllegalArgumentException(i18n("bindFail"));

        //The first time someone attempts binding and succeeded.
        if (bindResult.oldUserName() == null) return i18n("bindSuccess", bindResult.userName());

        // Binding found in database and the request is the same as the existing, not updating.
        if (bindResult.oldUserName().equals(bindResult.userName()))
            return i18n("bindNoChange");

        //Update binding succeeded.
        return i18n("changeBind", bindResult.oldUserName(), bindResult.userName());
    }

    /**
     * @param qqID The QQ ID to request a confirmation.
     * @param code The code to be checked.
     * @return The binding success message. Which are 1). bindSuccess when a new binding is successfully added;
     * 2). bindNoChange when the binding already exists and no operation is done; 3). changeBind when an existing binding is updated.
     * @throws IllegalArgumentException invalidCode: Verification code is neither format-correct nor valid.
     * @throws IllegalArgumentException bindFail: The request is valid, but binding attempt failed while operating database.
     */
    public static String verifier(Long qqID, String code) throws IllegalArgumentException {
        // Code not matching generating regex.
        if (!isValidVerificationCode(code, PhenylConfiguration.verification))
            throw new IllegalArgumentException(i18n("invalidCode"));

        BindResult bindResult = handleConfirm(qqID, code);

        // Code not found in binding queue.
        if (bindResult == null) throw new IllegalArgumentException(i18n("invalidCode"));

        // Failed while updating the database.
        if (!bindResult.querySucceeded()) throw new IllegalArgumentException(i18n("bindFail"));

        //The first time someone attempts binding and succeeded.
        if (bindResult.oldUserName() == null) return i18n("bindSuccess", bindResult.userName());

        // Binding found in database and the request is the same as the existing, not updating.
        if (bindResult.oldUserName().equals(bindResult.userName()))
            return i18n("bindNoChange");

        //Update binding succeeded.
        return i18n("changeBind", bindResult.oldUserName(), bindResult.userName());
    }
}