package core.utils;

import core.API;
import org.dreambot.api.methods.MethodProvider;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class ClientManager {
    public Socket client;
    public InputThread inputThread;
    public OutputThread outputThread;
    private Thread input, output;
    private API api;

    public ClientManager(API api) {
        this.api = api;
    }

    public void connect() {
        try {
            client = new Socket("localhost", 7804);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MethodProvider.sleepUntil(() -> Objects.nonNull(client) && client.isConnected(), 2400);
        inputThread = new InputThread(client, api, this);
        outputThread = new OutputThread(client, api, this);

        input = new Thread(inputThread);
        input.start();

        output = new Thread(outputThread);
        output.start();
    }

    public void stop() {
        outputThread.send("-1");
        
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (inputThread.running) {
                inputThread.stop();
            }

            outputThread.stop();

            input.stop();
            output.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        client = null;
        inputThread = null;
        outputThread = null;
    }
}
