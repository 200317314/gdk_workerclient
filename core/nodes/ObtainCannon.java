package core.nodes;

import core.API;
import core.Areas;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;

import java.util.Arrays;
import java.util.Objects;

public class ObtainCannon extends TaskNode {
    private API api;

    public ObtainCannon(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 999999999;
    }

    @Override
    public boolean accept() {
        if (api.muling || !Client.isLoggedIn()) {
            return false;
        }

        return canCollectNew();
    }

    @Override
    public int execute() {
        api.STATUS = "Obtaining new cannon...";
        if (Dialogues.inDialogue() && Areas.NULODION.contains(getLocalPlayer())) {
            if (Dialogues.canContinue()) {
                Dialogues.clickContinue();
            } else {
                Keyboard.type(4);
                sleepUntil(() -> Dialogues.canContinue(), 2400 + api.sleep());
            }

            if (api.hasFullCannon()) {
                Cannon.isPlaced = false;
                Cannon.placedTile = null;
                Cannon.canObtain = false;
                return api.sleep();
            }
        } else {
            if (!api.inWilderness() && !Areas.CORP.contains(getLocalPlayer())) {
                if (Areas.NULODION.contains(getLocalPlayer())) {

                    if (Objects.nonNull(NPCs.closest(n -> Objects.nonNull(n) && n.getName().contains("Nulodion")))) {
                        if (NPCs.closest(n -> Objects.nonNull(n) && n.getName().contains("Nulodion")).isOnScreen()) {
                            if (NPCs.closest(n -> Objects.nonNull(n) && n.getName().contains("Nulodion")).interact("Talk-to")) {
                                sleepUntil(() -> Dialogues.canContinue(), 4400 + api.sleep());
                            }
                        } else {
                            Camera.rotateToEntity(NPCs.closest(n -> Objects.nonNull(n) && n.getName().contains("Nulodion")));
                        }
                    }
                } else if (api.shouldWalk()) {
                    if (containsLoot() || org.dreambot.api.methods.container.impl.bank.Bank.isOpen() || api.hasFood()) {
                        if (org.dreambot.api.methods.container.impl.bank.Bank.isOpen()) {
                            Inventory.all().forEach(i -> {
                                if (Objects.nonNull(i)) {
                                    if (i.isNoted()) {
                                        if (org.dreambot.api.methods.container.impl.bank.Bank.depositAll(i.getName())) {
                                            sleepUntil(() -> !Inventory.contains(i.getName()), 2400 + api.sleep());
                                            sleep(api.sleep());
                                        }
                                    } else {
                                        if (!i.getName().contains("Looting bag") && !i.getName().contains("Games necklace") && !i.getName().contains("Ranging potion")) {
                                            if (org.dreambot.api.methods.container.impl.bank.Bank.depositAll(i.getName())) {
                                                sleepUntil(() -> !Inventory.contains(i.getName()), 2400 + api.sleep());
                                                sleep(api.sleep());
                                            }
                                        }
                                    }

                                    if (Cannon.canObtain) {
                                        if (i.getName().contains("Cannon ")) {
                                            if (org.dreambot.api.methods.container.impl.bank.Bank.depositAll(i.getName())) {
                                                sleepUntil(() -> !Inventory.contains(i.getName()), 2400 + api.sleep());
                                                sleep(api.sleep());
                                            }
                                        }
                                    }
                                }
                            });

                            sleep(api.sleep());
                            org.dreambot.api.methods.container.impl.bank.Bank.close();
                        } else {
                            org.dreambot.api.methods.container.impl.bank.Bank.openClosest();
                        }
                    } else {
                        Walking.walk(Areas.NULODION.getCenter());
                    }
                }
            } else if (api.hasAmulet()) {
                if (!Tabs.isOpen(Tab.EQUIPMENT)) {
                    if (Tabs.open(Tab.EQUIPMENT)) {
                        sleep(api.sleep());
                    }
                }

                if (Equipment.get(i -> Objects.nonNull(i) && i.getName().contains(api.AMULET)).interact("Edgeville")) {
                    sleep(4800,5900 + api.sleep());
                }
            }
        }
        return api.sleep();
    }

    private boolean canCollectNew() {
        return Cannon.canObtain;
    }

    private boolean containsLoot() {
        if (!api.inWilderness()) {
            return false;
        }

        return Inventory.contains(i -> Objects.nonNull(i) && Arrays.asList(api.lootList).contains(i.getName()) && !i.getName().contains("Looting bag"));
    }
}
