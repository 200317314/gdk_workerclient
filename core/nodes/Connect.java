package core.nodes;

import core.API;
import core.utils.ClientManager;
import core.utils.InputThread;
import core.utils.OutputThread;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.script.TaskNode;

import java.io.IOException;
import java.net.Socket;

public class Connect extends TaskNode {
    private ClientManager clientManager;
    private API api;

    public Connect(API api, ClientManager clientManager) {
        this.clientManager = clientManager;
        this.api = api;
    }

    @Override
    public int priority() {
        return 10000000;
    }

    @Override
    public boolean accept() {
        return clientManager.client.isClosed();
    }

    @Override
    public int execute() {
        try {
            clientManager.outputThread.stop();
            clientManager.inputThread.stop();
            clientManager.client = new Socket("localhost", 7804);
            MethodProvider.sleepUntil(() -> clientManager.client.isConnected(), 2400);

            clientManager.inputThread = new InputThread(clientManager.client, api, clientManager);
            clientManager.outputThread = new OutputThread(clientManager.client, api, clientManager);

            new Thread(clientManager.inputThread).start();
            new Thread(clientManager.outputThread).start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return 100;
    }
}
