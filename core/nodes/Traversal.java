package core.nodes;

import core.API;
import core.Areas;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.path.impl.GlobalPath;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Locatable;

import java.util.Objects;

public class Traversal extends TaskNode {
    private API api;
    private GameObject cave;
    private GlobalPath<AbstractWebNode> path;

    public Traversal(API api) {
        this.api = api;
    }

    @Override
    public boolean accept() {
        if (api.muling || api.selectedArea.contains(getLocalPlayer()) || !Client.isLoggedIn()) {
            return false;
        }

        if (Cannon.canObtain) {
            return false;
        }

        return canTraverse() && !Pker.teleBlocked && api.inEquipment(api.AMULET);
    }

    @Override
    public int execute() {
        if (Areas.CORP.contains(getLocalPlayer())) {
            if (Objects.nonNull(cave) && cave.exists()) {
                if (!Dialogues.inDialogue()) {
                    if (cave.interact("Exit")) {
                        sleepUntil(Dialogues::inDialogue, 3600);
                    }
                }

                if (Dialogues.inDialogue()) {
                    sleep(api.sleep());
                    if (Calculations.random(2) == 1) {
                        Keyboard.type("1");
                    } else {
                        Dialogues.clickOption(1);
                    }
                    sleepUntil(() -> api.inWilderness(), 4000 + api.sleep());
                    sleep(api.sleep()*Calculations.random(1,6));
                }
            } else {
                cave = GameObjects.closest(o -> Objects.nonNull(o) && o.getName().contains("Cave exit") && o.hasAction("Exit"));
                return api.sleep();
            }
        } else {
            if (api.inWilderness()) {
                if (api.shouldWalk()) {
                    if (slayerDungeon() && !Areas.SLAYER_DUNGEON.contains(getLocalPlayer())) {
                        if (Areas.DUNGEON_STAIRS.contains(getLocalPlayer())) {
                            GameObject stairs = GameObjects.closest(o -> Objects.nonNull(o) && o.getName().contains("Stairs"));

                            if (Objects.nonNull(stairs)) {
                                if (stairs.interact("Walk-down")) {
                                    sleepUntil(() -> Areas.SLAYER_DUNGEON.contains(getLocalPlayer()), 4600 + api.sleep());
                                }
                            }
                        } else {
                            //Walking.walk(Areas.DUNGEON_STAIRS.getCenter().getRandomizedTile(2));
                            customWalk(Areas.DUNGEON_STAIRS);
                        }
                    } else {
                        //Walking.walk(api.selectedArea.getCenter().getRandomizedTile(2));
                        if (slayerDungeon()) {
                            Walking.walk(api.selectedArea.getCenter().getRandomizedTile(2));
                        } else {
                            customWalk(api.selectedArea);
                        }
                    }
                }
            } else {
                if (Tabs.isOpen(Tab.INVENTORY)) {
                    if (!Dialogues.inDialogue()) {
                        if (Inventory.get(i -> Objects.nonNull(i) && i.getName().contains(api.RING)).interact("Rub")) {
                            sleepUntil(Dialogues::inDialogue, 2400 + api.sleep());
                        }
                    }

                    if (Dialogues.inDialogue()) {
                        sleep(api.sleep());
                        Keyboard.type("3");
                        sleepUntil(() -> Areas.CORP.contains(getLocalPlayer()), 6000 + api.sleep());
                    }
                } else {
                    Tabs.open(Tab.INVENTORY);
                }
            }
        }
        return api.sleep();
    }

    private boolean canTraverse() {
        if (api.hasFood() && api.inWilderness()) {
            return true;
        }

        return api.hasFood() && api.hasShield() && api.hasSword() && api.hasBoots() && api.hasChest() && api.hasLegs() && (api.hasRing() ||  Areas.CORP.contains(getLocalPlayer()) && !api.hasRing()) && api.hasHelmet() && !api.selectedArea.contains(getLocalPlayer())
                && api.hasCannon() && api.hasCannonBalls();
    }

    private void customWalk(Area area) {
        if (Objects.isNull(path) || path.size() == 0) {
            path = Walking.getWebPathFinder().calculate(getLocalPlayer().getTile(), area.getCenter().getRandomizedTile(Calculations.random(2,4)));
        }

        try {
            Locatable next = path.next();

            if (Objects.nonNull(next) && canWalk()) {
               Walking.clickTileOnMinimap(Walking.getClosestTileOnMap(path.next().getTile().getRandomizedTile(2)));
               sleep(200, 400);
            }
        } catch (Exception e) {
            path = null;
            //Walking.walk(area.getCenter().getRandomizedTile(3));
        }
    }

    private boolean canWalk() {
        if (Objects.isNull(Walking.getDestination())) {
            return true;
        } else {
            if (Walking.getDestination().distance(getLocalPlayer()) <= 5) {
                return true;
            }
        }

        return false;
    }

    private boolean slayerDungeon() {
        return api.designated_id == 2 || api.designated_id == 3;
    }
}
