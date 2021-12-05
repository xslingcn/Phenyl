package live.turna.phenyl.bind;

import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.utils.Bind;
import org.jetbrains.annotations.Nullable;

/**
 * <b>BindHandler</b><br>
 * Handles all binding and confirming requests.
 *
 * @since 2021/12/5 2:58
 */
public class BindHandler {

    private static final BindArray userBindings = new BindArray();

    /**
     * Generate verification code for a request from QQ group and add the request to array.
     *
     * @param userName The username to bind.
     * @param userID The QQ ID to bind.
     * @return Verification code.
     */
    public static String handleRequest(String userName, Long userID) {
        String code = Bind.generateVerificationCode(PhenylConfiguration.verification);
        userBindings.add(new BindMap(userName, userID, code));
        return code;
    }

    /**
     * Complete the verification and proceed binding and remove the binding request from array.
     *
     * @param userName The username to bind.
     * @param code The verification code.
     * @return If the confirmation is valid, return username. Or return null to indicate not succeeding.
     */
    @Nullable
    public static String handleConfirm(String userName, String code) {
        BindArray result = userBindings.get(code);
        if (result != null) {
            for (BindMap entry : result) {
                BindMap match = entry.match(code);
                if (match != null) {
                    if (match.getUserName().equals(userName)) {
                        userBindings.remove(match.getUserName());
                        userBindings.remove(match.getUserID());
                        userBindings.remove(match.getCode());
                        return userName;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Complete the verification and proceed binding and remove the binding request from array.
     *
     * @param userID The userID to bind.
     * @param code The verification code.
     * @return Corresponding username if the code is valid. Or null indicating not succeeding.
     */
    @Nullable
    public static String handleConfirm(Long userID, String code) {
        BindArray result = userBindings.get(code);
        if (result != null) {
            for (BindMap entry : result) {
                BindMap match = entry.match(code);
                if (match != null) {
                    if (match.getUserID().equals(userID)) {
                        String userName = match.getUserName();
                        userBindings.remove(match.getUserName());
                        userBindings.remove(match.getUserID());
                        userBindings.remove(match.getCode());
                        return userName;
                    }
                }
            }
        }
        return null;
    }
}