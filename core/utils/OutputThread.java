package core.utils;

import core.API;
import org.dreambot.api.methods.MethodProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class OutputThread implements Runnable {
    private volatile boolean running;
    public PrintWriter out;
    private API api;
    private ClientManager clientManager;

    public OutputThread(Socket socket, API api, ClientManager clientManager) {
        this.clientManager = clientManager;
        running = true;
        this.api = api;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            if (api.designated_id == -1) {
                send("hunt:" + api.getPlayerName());
                MethodProvider.sleepUntil(() -> api.designated_id != -1, 4000);
            }
        }
    }

    public void send(String json) {
        try {
            out.println(json);
        } catch (Exception e) {
            //clientManager.stop();
        }
    }

    public void stop() {
        running = false;

        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
