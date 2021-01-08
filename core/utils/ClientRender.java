package core.utils;

import org.dreambot.api.Client;
import org.dreambot.api.methods.MethodContext;

public class ClientRender {
    public static void setRender(MethodContext m) {
        if (Client.isLoggedIn()) {
            Client.setRenderingDisabled(false);
            MethodContext.sleep(125,250);
            Client.setRenderingDisabled(true);
        } else {
            Client.setRenderingDisabled(false);
            MethodContext.sleep(500,750);
            Client.setRenderingDisabled(true);
        }
    }
}