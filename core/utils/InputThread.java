package core.utils;

import core.API;
import core.Areas;
import core.CreateWebNode;
import core.nodes.Mule;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class InputThread implements Runnable {
    public volatile boolean running;
    private BufferedReader in;
    private Socket socket;
    private API api;
    private ClientManager clientManager;

    public InputThread(Socket socket, API api, ClientManager clientManager) {
        running = true;
        this.clientManager = clientManager;
        this.socket = socket;
        this.api = api;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (socket.isConnected()) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.contains("true")) {
                            Mule.sent = true;
                            Mule.username = line.split(":")[1];
                        } else if (line.contains("hunt")) {
                            api.designated_id = Integer.parseInt(line.split(":")[1]);

                            switch(api.designated_id) {
                                case 1:
                                    api.selectedArea = Areas.DRAGON_AREA_EAST;
                                    break;
                                case 2:
                                    api.selectedArea = Areas.SLAYER_DUNGEON_NORTH;
                                    break;
                                case 3:
                                    api.selectedArea = Areas.SLAYER_DUNGEON_SOUTH;
                                    break;
                                default:
                                    api.selectedArea = Areas.DRAGON_AREA_NORTH;
                                    break;
                            }

                            execute();
                            clientManager.stop();
                        } else if (line.equals("-1")) {
                            clientManager.stop();
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                //MethodProvider.log(e.getMessage());
                //clientManager.stop();
            }
        }
    }

    private void execute() {
        CreateWebNode.init(api);
    }

    public void stop() {
        running = false;

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
