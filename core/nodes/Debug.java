package core.nodes;

import core.API;
import core.utils.ClientManager;
import org.dreambot.api.script.TaskNode;

import java.util.Objects;

public class Debug extends TaskNode {
    private ClientManager clientManager;
    private API api;

    public Debug(API api, ClientManager clientManager) {
        this.api = api;
        this.clientManager = clientManager;
    }

    @Override
    public int priority() {
        return 199999;
    }

    @Override
    public boolean accept() {
        return Objects.nonNull(clientManager.client) && !api.muling;
    }

    @Override
    public int execute() {
        clientManager.stop();
        return 100;
    }
}
