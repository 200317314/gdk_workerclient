package core.nodes;

import core.API;
import core.Areas;
import core.utils.ClientManager;
import core.utils.MuleItem;
import core.utils.MulePacket;
import core.utils.Packet;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;

import java.util.*;

import static org.dreambot.api.Client.getLocalPlayer;

public class Mule extends TaskNode {
    public static String username = "";
    private Player mule;
    public static boolean checkedBank;
    public static List<MuleItem> items = new ArrayList<>();
    public static boolean sent = false;
    private API api;
    private ClientManager clientManager;

    public Mule(API api, ClientManager clientManager) {
        this.api = api;
        this.clientManager = clientManager;
    }

    @Override
    public int priority() {
        return 999999999;
    }

    @Override
    public boolean accept() {
        return api.muling;
    }

    @Override
    public int execute() {
        api.STATUS = "Muling...";

        if (!Areas.EDGEVILLE.contains(getLocalPlayer())) {
            if (org.dreambot.api.methods.container.impl.bank.Bank.isOpen()) {
                org.dreambot.api.methods.container.impl.bank.Bank.close();
                sleep(api.sleep());
            }
        }

        if (Areas.EDGEVILLE.contains(getLocalPlayer())) {
            if (checkedBank) {
                if (org.dreambot.api.methods.container.impl.bank.Bank.isOpen()) {
                    org.dreambot.api.methods.container.impl.bank.Bank.close();
                    sleep(api.sleep());
                }

                if (!sent) {
                    clientManager.connect();
                    sleepUntil(() -> clientManager.client.isConnected(), 3400);

                    if (clientManager.client.isConnected()) {
                        sendRequest(items);
                        sent = true;
                        items.clear();
                    } else {
                        clientManager.stop();
                        return Calculations.random(3000, 5000);
                    }
                }

                if (!username.equals("")) {
                    if (Objects.nonNull(mule) && mule.exists()) {
                        if (Trade.isOpen()) {
                            if (!containsLoot()) {
                                Trade.acceptTrade();
                                sleep(3000 + api.sleep(), 6000 + api.sleep());
                            } else {
                                for (Item i : Inventory.all()) {
                                    if (Objects.nonNull(i)) {
                                        if (Trade.addItem(i.getName(), 900000000)) {
                                            sleepUntil(() -> !Inventory.contains(i.getName()), 2600 + api.sleep());
                                            sleep(api.sleep());
                                        }
                                    }
                                }
                            }
                        } else {
                            if (Camera.rotateToEntity(mule) && Trade.tradeWithPlayer(username)) {
                                sleepUntil(() -> Trade.isOpen(), 3000 + api.sleep());
                            }
                        }
                    } else {
                        mule = Players.closest(p -> Objects.nonNull(p) && p.getName().contains(username));
                        sleep(api.sleep());

                        if (Objects.isNull(mule)) {
                            Mule.username = "";
                            api.muling = false;
                            Mule.items.clear();
                            Mule.checkedBank = false;
                            Mule.sent = false;
                            clientManager.stop();
                        }
                    }
                }
            } else if (Walking.shouldWalk(5)) {
                if (org.dreambot.api.methods.container.impl.bank.Bank.isOpen()) {
                    if (!Inventory.isEmpty()) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.depositAllItems()) {
                            sleepUntil(() -> Inventory.isEmpty(), 3000 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    List<String> temp = api.lootList;
                    Collections.shuffle(temp);
                    for (String loot : temp) {
                        if (Bank.getWithdrawMode() != BankMode.NOTE ) {
                            if (org.dreambot.api.methods.container.impl.bank.Bank.setWithdrawMode(BankMode.NOTE)) {
                                sleepUntil(() -> org.dreambot.api.methods.container.impl.bank.Bank.getWithdrawMode() == BankMode.NOTE, 2400 + api.sleep());
                                sleep(api.sleep());
                            }   
                        }

                        if (!loot.contains("Looting bag") && org.dreambot.api.methods.container.impl.bank.Bank.contains(i -> Objects.nonNull(i) && i.getName().contains(loot) && i.getAmount() > 0)) {
                            if (org.dreambot.api.methods.container.impl.bank.Bank.withdrawAll(i -> Objects.nonNull(i) && i.getName().contains(loot))) {
                                sleepUntil(() -> Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains(loot)), 2600 + api.sleep());
                                sleep(api.sleep());
                            }
                        }
                        sleep(api.sleep()*Calculations.random(2,6));
                    }
                    temp.clear();

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains("Coins")) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdrawAll("Coins")) {
                            sleepUntil(() -> Inventory.contains("Coins"), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains("Amulet of glory")) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdrawAll("Amulet of glory")) {
                            sleepUntil(() -> Inventory.contains("Amulet of glory"), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains("Mithril bolts") && org.dreambot.api.methods.container.impl.bank.Bank.get("Mithril bolts").getAmount() > 2000) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw("Mithril bolts", (org.dreambot.api.methods.container.impl.bank.Bank.count("Mithril bolts") - 2000))) {
                            sleepUntil(() -> Inventory.contains("Mithril bolts"), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains("Lobster") && org.dreambot.api.methods.container.impl.bank.Bank.get("Lobster").getAmount() > 2000) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw("Lobster", (org.dreambot.api.methods.container.impl.bank.Bank.count("Lobster") - 2000))) {
                            sleepUntil(() -> Inventory.contains("Lobster"), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains("Bass") && org.dreambot.api.methods.container.impl.bank.Bank.get("Bass").getAmount() > 2000) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw("Bass", (org.dreambot.api.methods.container.impl.bank.Bank.count("Bass") - 2000))) {
                            sleepUntil(() -> Inventory.contains("Bass"), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains(api.CANNONBALL) && org.dreambot.api.methods.container.impl.bank.Bank.get(api.CANNONBALL).getAmount() > 5400) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw(api.CANNONBALL, (org.dreambot.api.methods.container.impl.bank.Bank.count(api.CANNONBALL) - 5400))) {
                            sleepUntil(() -> Inventory.contains(api.CANNONBALL), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains(i -> Objects.nonNull(i) && i.getName().contains("Amulet of glory(")) && org.dreambot.api.methods.container.impl.bank.Bank.get(i -> Objects.nonNull(i) && i.getName().contains("Amulet of glory(")).getAmount() > 20) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw(i -> Objects.nonNull(i) && i.getName().contains("Amulet of glory("), (org.dreambot.api.methods.container.impl.bank.Bank.count(i -> Objects.nonNull(i) && i.getName().contains("Amulet of glory(")) - 20))) {
                            sleepUntil(() -> Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Amulet of glory(")), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains(i -> Objects.nonNull(i) && i.getName().contains("Games necklace")) && org.dreambot.api.methods.container.impl.bank.Bank.get(i -> Objects.nonNull(i) && i.getName().contains("Games necklace")).getAmount() > 20) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw(i -> Objects.nonNull(i) && i.getName().contains("Games necklace"), (org.dreambot.api.methods.container.impl.bank.Bank.count(i -> Objects.nonNull(i) && i.getName().contains("Games necklace")) - 20))) {
                            sleepUntil(() -> Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Games necklace")), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains("Cannon base") && (org.dreambot.api.methods.container.impl.bank.Bank.get("Cannon base").getAmount() > 1 || Cannon.isPlaced)) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw("Cannon base", 1)) {
                            sleepUntil(() -> Inventory.contains("Cannon base"), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains("Cannon furnace") && (org.dreambot.api.methods.container.impl.bank.Bank.get("Cannon furnace").getAmount() > 1 || Cannon.isPlaced)) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw("Cannon furnace", 1)) {
                            sleepUntil(() -> Inventory.contains("Cannon furnace"), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains("Cannon stand") && (org.dreambot.api.methods.container.impl.bank.Bank.get("Cannon stand").getAmount() > 1|| Cannon.isPlaced)) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw("Cannon stand", 1)) {
                            sleepUntil(() -> Inventory.contains("Cannon stand"), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    if (org.dreambot.api.methods.container.impl.bank.Bank.contains("Cannon barrels") && (org.dreambot.api.methods.container.impl.bank.Bank.get("Cannon barrels").getAmount() > 1 || Cannon.isPlaced)) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.withdraw("Cannon barrels", 1)) {
                            sleepUntil(() -> Inventory.contains("Cannon barrels"), 2600 + api.sleep());
                            sleep(api.sleep());
                        }
                    }

                    checkedBank = true;
                    org.dreambot.api.methods.container.impl.bank.Bank.close();
                } else {
                    org.dreambot.api.methods.container.impl.bank.Bank.openClosest();
                }
            }
        } else if (Walking.shouldWalk(5)) {
            if (api.hasAmulet()) {
                if (api.inEquipment(api.AMULET)) {
                    if (api.hasAmulet()) {
                        if (Tabs.isOpen(Tab.EQUIPMENT)) {
                            if (Equipment.get(i -> Objects.nonNull(i) && i.getName().contains(api.AMULET)).interact("Edgeville")) {
                                sleep(4800,5900 + api.sleep());
                            }
                        } else {
                            Tabs.open(Tab.EQUIPMENT);
                        }
                    }
                } else {
                    if (Tabs.isOpen(Tab.INVENTORY)) {
                        if (api.getInventoryItem(api.AMULET).interact("Wear")) {
                            sleepUntil(() -> api.inEquipment(api.AMULET), 2400);
                        }
                    } else {
                        Tabs.open(Tab.INVENTORY);
                    }
                }
            } else {
                Walking.walk(Areas.EDGEVILLE.getCenter());
            }
        }
        return api.sleep();
    }

    private boolean containsLoot() {
        if (api.inWilderness()) {
            return false;
        }

        return Inventory.contains(i -> Objects.nonNull(i) && api.lootList.contains(i.getName()) && !i.getName().contains("Looting bag"));
    }

    private void sendRequest(List<MuleItem> items) {
        MulePacket mulePacket = new MulePacket(Client.getCurrentWorld(), getLocalPlayer().getX(), getLocalPlayer().getY(), getLocalPlayer().getName(), items);

        Packet packet = new Packet("request", "request", mulePacket);
        clientManager.outputThread.send(api.gson.toJson(packet));
    }
}
