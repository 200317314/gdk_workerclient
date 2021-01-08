package core.nodes;

import core.API;
import core.Areas;
import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Objects;

public class Cannon extends TaskNode {
    public static boolean isPlaced = false, refill = false, override = false, worldHop = false, repair = false, obtainable = false, canObtain = false;
    public static Tile placedTile;
    private static Timer decayTimer;
    private API api;

    public Cannon(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public boolean accept() {
        if (!Client.isLoggedIn()) {
            return false;
        }

        if (Objects.nonNull(decayTimer) && decayTimer.finished()) {
            if (!canObtain) {
                canObtain = true;
            }
        }

        if (canObtain) {
            return false;
        }

        return canSetup() || canRefill() && api.items.isEmpty() || canRepair();
    }

    @Override
    public int execute() {
        if (canRefill()) {
            api.STATUS = "Refilling cannon...";
            if (Objects.nonNull(api.getCannon())) {
                if (api.getCannon().distance(getLocalPlayer()) < 5) {
                    if (api.getCannon().hasAction("Repair")) {
                        if (api.getCannon().interact("Repair")) {
                            decayTimer = new Timer(151_000_000);
                            sleepUntil(() -> !refill, 3600 + api.sleep());
                        }
                    } else {
                        if (api.getCannon().interact("Fire")) {
                            sleepUntil(() -> !refill, 3600 + api.sleep());
                        }
                    }
                } else if (api.shouldWalk()) {
                    Walking.walk(api.getCannon().getTile());
                }
            } else {
                if (api.getCannonArea().contains(getLocalPlayer())) {
                    if (Objects.isNull(api.getCannon())) {
                        resetCannonStuff();
                    }
                } else if (api.shouldWalk()) {
                    Walking.walk(api.getCannonArea().getCenter());
                }
            }
        }

        if (canRepair()) {
            api.STATUS = "Repairing cannon...";
            if (Objects.nonNull(api.getCannon())) {
                if (api.getCannon().distance(getLocalPlayer()) < 5) {
                    if (api.getCannon().interact("Repair")) {
                        decayTimer = new Timer(151_000_000);
                        sleepUntil(() -> !repair, 3600 + api.sleep());
                    }
                } else if (api.shouldWalk()) {
                    Walking.walk(api.getCannon().getTile());
                }
            } else {
                if (api.getCannonArea().contains(getLocalPlayer())) {
                    if (Objects.isNull(api.getCannon())) {
                        resetCannonStuff();
                    }
                } else if (api.shouldWalk()) {
                    Walking.walk(api.getCannonArea().getCenter());
                }
            }
        }

        if (canSetup()) {
            if (!containsOtherCannon() && !worldHop) {
                if (api.getCannonArea().contains(getLocalPlayer())) {
                    api.STATUS = "Setting up cannon 0...";
                    if (!getLocalPlayer().isMoving() || getLocalPlayer().isStandingStill()) {
                        if (Tabs.isOpen(Tab.INVENTORY)) {
                            api.STATUS = "Setting up cannon 1...";
                            if (Inventory.get(api.BASE).interact("Set-up")) {
                                placedTile = getLocalPlayer().getTile();
                                isPlaced = true;
                                //override = true;
                                decayTimer = new Timer(151_000_000);
                                refill = true;
                                sleepUntil(() -> !api.hasFurnace(), 6400);
                            }

                            sleep(3800 + api.sleep(), 4000 + api.sleep());

                            if (api.hasFullCannon()) {
                                Walking.clickTileOnMinimap(api.getCannonArea().getRandomTile());
                                return api.sleep();
                            }

                            GameObject cannon = api.getCannon();
                            if (Objects.nonNull(cannon)) {
                                if (cannon.interactForceRight("Fire")) {
                                    sleepUntil(() -> !refill, 3600);
                                }

                                placedTile = cannon.getTile();
                            }
                        } else {
                            Tabs.open(Tab.INVENTORY);
                        }
                    }
                } else if (api.shouldWalk()) {
                    api.STATUS = "Walking cannon area...";
                    if (Objects.nonNull(Walking.getDestination())) {
                        if (!api.getCannonArea().contains(Walking.getDestination())) {
                            if (api.getCannonArea().getCenter().distance(getLocalPlayer()) <= 14) {
                                Walking.clickTileOnMinimap(api.getCannonArea().getRandomTile());
                            } else {
                                Walking.walk(api.getCannonArea().getRandomTile());
                            }
                            return api.sleep();
                        }
                    } else {
                        if (api.getCannonArea().getCenter().distance(getLocalPlayer()) <= 14) {
                            Walking.clickTileOnMinimap(api.getCannonArea().getRandomTile());
                        } else {
                            Walking.walk(api.getCannonArea().getRandomTile());
                        }
                        return api.sleep();
                    }
                }
            } else {
                api.STATUS = "World hopping...";
                worldHop = true;

                if (api.wildyLevel() <= 21 || !getLocalPlayer().isInCombat() && !getLocalPlayer().isHealthBarVisible()) {
                    if (!getLocalPlayer().isInCombat()) {
                        World world = Worlds.getRandomWorld(f -> f.isMembers() && !f.isDeadmanMode() && !f.isPVP() && f.getMinimumLevel() == 0
                                && f.getID() != 318 && f.getID() != Client.getCurrentWorld() && !f.isTournamentWorld() && !f.isTargetWorld() && !f.isTwistedLeague());
                        api.STATUS = "World hopping " + world.getID();
                        WorldHopper.hopWorld(world);
                        sleepUntil(() -> Client.getGameState() != GameState.HOPPING && Client.getGameState() != GameState.LOADING && Client.isLoggedIn(), 4000 + api.sleep());
                        sleep(api.sleep());

                        if (Client.getCurrentWorld() == world.getID()) {
                            worldHop = false;
                        }
                    }
                } else if (api.shouldWalk()) {
                    Walking.walk(Areas.SAFE_SPOT);
                }
            }
        }
        return api.sleep();
    }

    private boolean canSetup() {
        if (isPlaced) {
            return false;
        }

        if (api.hasFullCannon() && api.selectedArea.contains(getLocalPlayer())) {
            return true;
        }

        return !isPlaced && api.selectedArea.contains(getLocalPlayer()) && api.hasFullCannon() || worldHop;
    }

    private boolean canRefill() {
        if (!isPlaced || !refill) {
            return false;
        }

        if (!api.hasCannonBalls()) {
            return false;
        }

        return refill && api.selectedArea.contains(getLocalPlayer()) && api.hasCannonBalls() && isPlaced && !repair && api.hasFood();
    }

    private boolean canRepair() {
        if (!isPlaced || !repair) {
            return false;
        }

//        if (api.selectedArea.contains(getLocalPlayer())) {
//            if (Objects.nonNull(cannon) && cannon.exists()) {
//                if (cannon.hasAction("Repair") && isPlaced) {
//                    return true;
//                }
//            }
//        }

        if (api.selectedArea.contains(getLocalPlayer())) {
            if (isPlaced) {
                if (Objects.nonNull(api.getCannon())) {
                    if (api.getCannon().hasAction("Repair")) {
                        return true;
                    }
                }
            }
        }

        return api.selectedArea.contains(getLocalPlayer()) && repair && isPlaced && api.hasFood();
    }

    private boolean containsOtherCannon() {
        if (isPlaced) {
            return false;
        }

        if (worldHop) {
            return true;
        }

        return Objects.nonNull(GameObjects.closest(o -> Objects.nonNull(o) && o.getName().contains("multicannon") && api.getCannonArea().contains(o))) || worldHop;
    }

    private void resetCannonStuff() {
        isPlaced = false;
        refill = false;
        obtainable = false;
        override = false;
        repair = false;
        placedTile = null;
        Cannon.canObtain = true;
    }
}
