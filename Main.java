import core.API;
import core.nodes.*;
import core.utils.ClientManager;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.widgets.message.Message;

import java.util.Objects;

@ScriptManifest(name = "Green Dragon Killer", author = "7804364", version = 3, description = "Kills green dragons", category = Category.COMBAT)
public class Main extends TaskScript implements ChatListener {
    private final API api = new API();
    private ClientManager clientManager;

    @Override
    public void onStart() {
        clientManager = new ClientManager(api);

        clientManager.connect();
        sleepUntil(() -> clientManager.client.isConnected(), 3400);

        addNodes(new NewBank(api));
        addNodes(new NewGear(api));
        addNodes(new LootBag(api));
        addNodes(new Eat(api));
        addNodes(new Collect(api));
        addNodes(new Attack(api));
        addNodes(new Cannon(api));
        addNodes(new Traversal(api));
        addNodes(new Pker(api));
        addNodes(new Dialouge());
        addNodes(new ObtainCannon(api));
        addNodes(new Potion(api));
        addNodes(new Mule(api, clientManager));
        addNodes(new ToggleAttackMode(api));
        addNodes(new Antifire(api));
        //addNodes(new Debug(api, clientManager));
        //addNodes(new Connect(api, clientManager));
        addNodes(new TimerNode());
    }

    @Override
    public void onExit() {
        clientManager.outputThread.send("-1");
        clientManager.stop();
        Client.setRenderingDisabled(false);
    }

    @Override
    public void onGameMessage(Message message) {
        if (Objects.nonNull(message)) {
            if (message.getMessage().contains("You drink some of your extended antifire")) {
                api.canAntifire = false;
            }

            if (message.getMessage().contains("Accepted trade.")) {
                Mule.username = "";
                api.muling = false;
                Mule.items.clear();
                Mule.checkedBank = false;
                Mule.sent = false;
                clientManager.stop();

                if (Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Cannon ") && i.isNoted())) {
                    api.worldHop();
                }
            }

            if (message.getMessage().contains("You cannot construct more than one Cannon") || message.getMessage().contains("go and see the Dwarf Cannon engineer.") || message.getMessage().contains("you have lost your Cannon") || message.getMessage().contains("Your cannon has decayed. Speak")) {
                Cannon.canObtain = true;
            }

            if (message.getMessage().contains("load the cannon with") || message.getMessage().contains("cannon is already firing")) {
                if (Cannon.override) {
                    Cannon.override = false;
                } else {
                    Cannon.refill = false;
                }

                if (!Cannon.obtainable) {
                    Cannon.obtainable = true;
                }
            }

            if (message.getMessage().contains("Your cannon is out of ammo!")) {
                Cannon.refill = true;
            }

            if (message.getMessage().contains("cannon has broken!")) {
                Cannon.repair = true;
            }

            if (message.getMessage().contains("restoring it to working order")) {
                Cannon.repair = false;
            }

            if (message.getMessage().contains("Welcome to Old School")) {
                if (Cannon.isPlaced) {
                    Cannon.refill = true;
                }
            }
        }
    }
}
