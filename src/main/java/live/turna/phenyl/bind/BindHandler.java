package live.turna.phenyl.bind;

import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Database;
import live.turna.phenyl.database.Player;
import live.turna.phenyl.utils.Bind;
import net.md_5.bungee.api.ProxyServer;
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
     * @param userID   The QQ ID to bind.
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
     * @param code     The verification code.
     * @return If the confirmation is valid, return username. Or return null to indicate not succeeding.
     */
    @Nullable
    public static BindResult handleConfirm(String userName, String code) {
        BindArray result = userBindings.get(code);
        if (result != null && result.instance != null) {
            for (BindMap entry : result.instance) {
                BindMap match = entry.match(code) ? entry : null;
                if (match != null) {
                    if (match.getUserName().equals(userName)) {
                        String uuid = ProxyServer.getInstance().getPlayer(userName).getUniqueId().toString();
                        Long userID = match.userID();
                        Player oldBinding = Database.getBinding(userID);
                        userBindings.remove(match);
                        if (oldBinding.uuid() == null)
                            return new BindResult(null, Database.addBinding(uuid, userID), userName);
                        if (oldBinding.uuid().equals(uuid))
                            return new BindResult(oldBinding.mcname(), true, userName);
                        Database.removeBinding(oldBinding.uuid());
                        return new BindResult(oldBinding.mcname(), Database.addBinding(uuid, userID), userName);
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
     * @param code   The verification code.
     * @return Corresponding username if the code is valid. Or null indicating not succeeding.
     */
    @Nullable
    public static BindResult handleConfirm(Long userID, String code) {
        BindArray result = userBindings.get(code);
        if (result != null && result.instance != null) {
            for (BindMap entry : result.instance) {
                BindMap match = entry.match(code) ? entry : null;
                if (match != null) {
                    if (match.getUserID().equals(userID)) {
                        String userName = match.getUserName();
                        String uuid = ProxyServer.getInstance().getPlayer(userName).getUniqueId().toString();
                        Player oldBinding = Database.getBinding(userID);
                        userBindings.remove(match);
                        if (oldBinding.uuid() == null)
                            return new BindResult(null, Database.addBinding(uuid, userID), userName);
                        if (oldBinding.uuid().equals(uuid))
                            return new BindResult(oldBinding.mcname(), true, userName);
                        Database.removeBinding(oldBinding.uuid());
                        return new BindResult(oldBinding.mcname(), Database.addBinding(uuid, userID), userName);
                    }
                }
            }
        }
        return null;
    }
}