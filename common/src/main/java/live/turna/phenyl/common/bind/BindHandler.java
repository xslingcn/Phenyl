package live.turna.phenyl.common.bind;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import org.jetbrains.annotations.Nullable;

import static live.turna.phenyl.common.message.I18n.i18n;
import static live.turna.phenyl.common.utils.BindUtils.generateVerificationCode;
import static live.turna.phenyl.common.utils.BindUtils.isValidVerificationCode;

/**
 * <b>BindHandler</b><br>
 * Handles all binding and verification requests.<br/>
 * To call a request, provide in-game username and QQ ID; <br/>
 * To verify a request, provide either username or ID with the verification code.
 *
 * @since 2021/12/5 2:58
 */
public class BindHandler {
    private final BindArray userBindings = new BindArray();
    private final transient AbstractPhenyl phenyl;

    public BindHandler(AbstractPhenyl plugin) {
        phenyl = plugin;
    }

    /**
     * Generate verification code for a and add the request to array.
     *
     * @param userName The username.
     * @param userId   The QQ ID.
     * @return Verification code.
     */
    public String handleRequest(String userName, Long userId) {
        String code = generateVerificationCode(Config.verification);
        userBindings.add(new BindMap(userName, userId, code));
        return code;
    }

    /**
     * @param userName The username.
     * @param code     The verification code.
     * @return The success message. <br/>
     * 1). bindSuccess when a new binding is successfully added;<br/>
     * 2). bindNoChange when the binding already exists and no operation is done; <br/>
     * 3). changeBind when an existing binding is updated.
     * @throws IllegalArgumentException The failing message. <br/>
     *                                  1). invalidCode: Verification code is neither format-correct nor valid.<br/>
     *                                  2). bindFail: The request is valid, but binding attempt failed while operating database.
     */
    public String handleConfirm(String userName, String code) throws IllegalArgumentException {
        return new Verify(userName, code).check();
    }

    public String handleConfirm(Long userId, String code) throws IllegalArgumentException {
        return new Verify(userId, code).check();
    }

    private class Verify {
        private final transient Object identifier;
        private final transient String code;

        /**
         * @param i The player identifier. Could be username(String) or QQ ID(Long).
         * @param c The code to be checked.
         */
        public Verify(Object i, String c) {
            identifier = i;
            code = c;
        }

        public String check() throws IllegalArgumentException {
            // Code not matching generating regex.
            if (!isValidVerificationCode(code, Config.verification))
                throw new IllegalArgumentException(i18n("invalidCode"));

            BindResult bindResult = new Confirm(identifier, code).get();
            phenyl.updateBoundPlayerList();

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

    private class Confirm {
        private transient String userName;
        private transient Long userId;
        private transient String code;

        /**
         * @param i The player identifier, as {@link Object}.
         * @param c The verification code.
         */
        public Confirm(Object i, String c) {
            if (i.getClass().equals(String.class)) {
                userName = (String) i;
                userId = null;
            } else {
                userName = null;
                userId = (Long) i;
            }
            code = c;
        }

        /**
         * Complete the verification and proceed binding and end the binding request from array.
         * Called when a player attempts to verify in game.
         *
         * @return If the confirmation is valid, return username. Or return null to indicate not succeeding.
         */
        @Nullable
        public BindResult get() {
            BindArray result = userBindings.get(code);
            if (result != null) {
                for (BindMap entry : result) {
                    BindMap match = entry.match(code) ? entry : null;
                    if (match != null) {
                        if ((userName != null && match.match(userName)) || (userId != null && match.match(userId))) {
                            userName = match.userName();
                            userId = match.userID();
                            String uuid = phenyl.getPlayer(userName).getUUID().toString();
                            Player oldBinding = phenyl.getStorage().getBinding(userId);
                            userBindings.remove(match);
                            if (oldBinding.uuid() == null)
                                return new BindResult(null, phenyl.getStorage().addBinding(uuid, userId), userName);
                            if (oldBinding.uuid().equals(uuid))
                                return new BindResult(oldBinding.mcname(), true, userName);
                            phenyl.getStorage().removeBinding(oldBinding.uuid());
                            return new BindResult(oldBinding.mcname(), phenyl.getStorage().addBinding(uuid, userId), userName);
                        }
                    }
                }
            }
            return null;
        }
    }
}