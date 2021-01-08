package core;

import com.google.gson.Gson;
import core.nodes.Cannon;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.util.*;

import static org.dreambot.api.Client.getLocalPlayer;

public class API {
    public final Gson gson = new Gson();
    public final List<String> lootList;
    public String FOOD = "Bass", FOOD2 = "Lobster", HELMET = "Snakeskin bandana",
            SHIELD = "Anti-dragon shield", BOOTS = "Snakeskin boots", AMULET = "Amulet of glory(", RING = "Games necklace", CAPE = "Ava's accumulator", BOLTS = "Mithril bolts",
    BASE = "Cannon base", STAND = "Cannon stand", FURNACE = "Cannon furnace", BARRELS = "Cannon barrels", CANNONBALL = "Cannonball";
    public String STATUS = "Loading...";
    public boolean muling = false, canAntifire = true;
    public int designated_id = -1;
    public Area selectedArea = Areas.DRAGON_AREA_NORTH;
    private GameObject cannon;
    private WidgetChild wildyLevel;
    private Player pker;
    public List<GroundItem> items = new ArrayList<>();
    public Timer sleepTimer, shouldBreak;

    public API() {
        designated_id = -1;
        sleepTimer = null;
        shouldBreak = null;
        lootList = new ArrayList<>(Arrays.asList("Dragon bones", "Green dragonhide", "Ensouled dragon head", "Rune dagger", "Nature rune", "Adamant full helm", "Looting bag", "Uncut diamond", "Tooth half of key", "Loop half of key", "Adamantite ore", "Uncut ruby", "Grimy ranarr weed", "Grimy avantoe", "Grimy kwuarm", "Grimy cadantine", "Grimy lantadyme", "Death rune", "Blood rune", "Adamant kiteshield"));
    }

    public int sleep() {
        return Calculations.random(80, 140);
    }

    public boolean hasFood() {
        return inInventory(FOOD) || inInventory(FOOD2);
    }

    public boolean hasHelmet() {
        if (Skills.getRealLevel(Skill.DEFENCE) < 30) {
            return true;
        }

        return inInventory(HELMET) || inEquipment(HELMET);
    }

    public boolean hasChest() {
        return inInventory(getChestName()) || inEquipment(getChestName());
    }

    public boolean hasLegs() {
        return inInventory(getLegsName()) || inEquipment(getLegsName());
    }

    public boolean hasShield() {
        return inInventory(SHIELD) || inEquipment(SHIELD);
    }

    public boolean hasBoots() {
        if (Skills.getRealLevel(Skill.DEFENCE) < 30) {
            return true;
        }

        return inInventory(BOOTS) || inEquipment(BOOTS);
    }

    public boolean hasSword() {
        return inInventory(getCrossbowType()) || inEquipment(getCrossbowType());
    }

    public boolean hasAmulet() {
        return inInventory(AMULET) || inEquipment(AMULET);
    }

    public boolean hasRing() {
        return inInventory(RING) || inEquipment(RING) || wildyLevel() != 0;
    }

    public boolean hasBolts() {
        return inInventory(BOLTS) && Inventory.get(BOLTS).getAmount() <= 250 || inEquipment(BOLTS);
    }

    public boolean hasGloves() {
        return inInventory(getGlovesName()) || inEquipment(getGlovesName());
    }

    public boolean hasCape() {
        return inInventory(CAPE) || inEquipment(CAPE);
    }

    public boolean hasBase() {
        return inInventory(BASE);
    }

    public boolean hasStand() {
        return inInventory(STAND);
    }

    public boolean hasBarrels() {
        return inInventory(BARRELS);
    }

    public boolean hasFurnace() {
        return inInventory(FURNACE);
    }

    public boolean hasCannonBalls() {
        return inInventory(CANNONBALL);
    }

    public boolean hasLootBag() {
        return Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Looting bag"));
    }

    public boolean shouldWalk() {
        if (Objects.nonNull(Walking.getDestination())) {
            if (Walking.getDestination().distance(getLocalPlayer()) <= Calculations.random(4,6)) {
                return true;
            }
        } else {
            return Walking.shouldWalk(Calculations.random(4,6));
        }

        return false;
    }

    public boolean inInventory(String name) {
        return Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains(name) && !i.isNoted());
    }

    public boolean inEquipment(String name) {
        return Equipment.contains(i -> Objects.nonNull(i) && i.getName().contains(name));
    }

    private WidgetChild getLootingBag() {
        List<WidgetChild> c = Widgets.getWidgets(w -> w != null && w.isVisible() && w.getItemId() == 22586);
        return c.size() > 0 ? c.get(0) : null;
    }

    public boolean isLootingBagFull() {
        WidgetChild b = getLootingBag();
        if (b != null) {
            for (String s : b.getActions()) {
                if (s != null && s.contains("View")) {
                    return true;
                }
            }
        }
        return false;
    }

    public WidgetChild lootingBagInterface() {
        return Widgets.getWidgetChild(15, 8);
    }

    public WidgetChild lootingBagInterfaceClose() {
        return Widgets.getWidgetChild(15, 10);
    }

    public int wildyLevel() {
        if (!Client.isLoggedIn()) {
            return 0;
        }

        if (Objects.isNull(wildyLevel) || !wildyLevel.isVisible()) {
            wildyLevel = Widgets.getMatchingWidget(w -> w != null && w.isVisible() && w.getText() != null && w.getText().contains("Level:"));
        }

        if (Objects.nonNull(wildyLevel)) {
            if (wildyLevel.getText().equals("")) {
                return 0;
            }
        }

        return (Objects.nonNull(wildyLevel) && wildyLevel.isVisible()) ? Integer.parseInt(wildyLevel.getText().replaceAll("Level:","").trim())
                : 0;
    }

    public boolean attackableExists() {
        int myLevel = getLocalPlayer().getLevel();
        int wildylevel = wildyLevel();

        if (Objects.nonNull(pker) && pker.exists()) {
            if (wildylevel == 0) {
                pker = null;
                return false;
            } else {
                return true;
            }
        }

        if (wildylevel == 0) {
            return false;
        }

        int min = myLevel - wildylevel, max = myLevel + wildylevel;
        pker = Players.closest(p -> p != null && !p.equals(getLocalPlayer()) && p.isSkulled() && p.distance(getLocalPlayer().getTile()) < 48 && p.getLevel() >= min && p.getLevel() <= max);

        return wildylevel > 0 && Objects.nonNull(pker) && pker.exists();
    }

    public String getChestName() {
        if (Skills.getRealLevel(Skill.DEFENCE) < 40) {
            return "Leather body";
        }

        return getGearPrefix() + "d'hide body";
    }

    public String getLegsName() {
        return getGearPrefix() + "d'hide chaps";
    }

    public String getGlovesName() {
        return getGearPrefix() + "d'hide vambraces";
    }

    public String getCrossbowType() {
        return (Skills.getRealLevel(Skill.RANGED) >= 61) ? "Rune crossbow" : "Adamant crossbow";
    }

    public String getGearPrefix() {
        if (Skills.getRealLevel(Skill.RANGED) < 60) {
            return "Blue ";
        }

        return (Skills.getRealLevel(Skill.RANGED) >= 70) ? "Black " : "Red ";
    }

    public boolean hasCannon() {
        if (Cannon.isPlaced) {
            return true;
        }

        if (inInventory("Cannon base") && inInventory("Cannon barrels") && inInventory("Cannon furnace") && inInventory("Cannon stand")) {
            return true;
        }

        return false;
    }

    public boolean hasFullCannon() {
        if (inInventory("Cannon base") && inInventory("Cannon barrels") && inInventory("Cannon furnace") && inInventory("Cannon stand")) {
            return true;
        }

        return false;
    }

    public GameObject getCannon() {
        if (Objects.nonNull(cannon) && cannon.exists()) {
            return cannon;
        }

        cannon = GameObjects.closest(o -> {
            if (Objects.nonNull(o)) {
                if (o.getName().contains("multicannon")) {
                    if (o.distance(Cannon.placedTile) <= 2) {
                        return true;
                    }
                }
            }
            return false;
        });

        return cannon;
    }

    public boolean inWilderness() {
        return getLocalPlayer().getY() > 3550;
    }

    public Item getInventoryItem(String name) {
        return Inventory.get(i -> Objects.nonNull(i) && i.getName().contains(name) && !i.isNoted());
    }

    public boolean isLoggedIn() {
        return Client.isLoggedIn();
    }

    public String getPlayerName() {
        return getLocalPlayer().getName();
    }

    public Area getCannonArea() {
        if (designated_id == 0) {
            return Areas.CANNON_NORTH;
        } else if (designated_id == 1) {
            return Areas.CANNON_EAST;
        } else if (designated_id == 2) {
            return Areas.CANNON_DUNGEON_NORTH;
        } else if (designated_id == 3) {
            return Areas.CANNON_DUNGEON_SOUTH;
        }

        return Areas.CANNON_NORTH;
    }

    public void disableLogin() {
        ScriptManager.getScriptManager().getCurrentScript().getRandomManager().disableSolver(RandomEvent.LOGIN);
    }

    public void enableLogin() {
        ScriptManager.getScriptManager().getCurrentScript().getRandomManager().enableSolver(RandomEvent.LOGIN);
    }

    public List<String> getBankItems() {
        List<String> items = new ArrayList<>(getCannonItems());
        items.add("Snakeskin bandana");
        items.add("Snakeskin boots");
        items.add("Anti-dragon shield");
        items.add("Amulet of glory(");
        items.add("Games necklace");
        items.add("Ava's accumulator");
        items.add("Mithril bolts");
        items.add("Cannonball");
        items.add("Looting bag");
        items.add(getChestName());
        items.add(getCrossbowType());
        items.add(getGlovesName());
        items.add(getLegsName());

        Collections.shuffle(items);
        items.addAll(getPotionItems());
        items.add("Bass");
        items.add("Lobster");
        return items;
    }

    public List<String> getCannonItems() {
        List<String> items = new ArrayList<>();
        items.add("Cannon base");
        items.add("Cannon stand");
        items.add("Cannon furnace");
        items.add("Cannon barrels");
        Collections.shuffle(items);
        return items;
    }

    public List<String> getPotionItems() {
        List<String> items = new ArrayList<>();
        items.add("Extended antifire");
        items.add("Ranging potion");
        Collections.shuffle(items);
        return items;
    }

    public List<String> getGearItems() {
        List<String> items = new ArrayList<>();
        items.add("Snakeskin bandana");
        items.add("Snakeskin boots");
        items.add("Anti-dragon shield");
        items.add("Amulet of glory(");
        items.add("Ava's accumulator");
        items.add("Mithril bolts");
        items.add(getChestName());
        items.add(getCrossbowType());
        items.add(getGlovesName());
        items.add(getLegsName());

        Collections.shuffle(items);
        return items;
    }

    public void worldHop() {
        World world = Worlds.getRandomWorld(f -> f.isMembers() && !f.isDeadmanMode() && !f.isPVP() && f.getMinimumLevel() == 0
                && f.getID() != 318 && f.getID() != Client.getCurrentWorld() && !f.isTournamentWorld() && !f.isTargetWorld() && !f.isTwistedLeague());
        MethodProvider.sleep(1400,1600);
        WorldHopper.hopWorld(world.getWorld());
        MethodProvider.sleep(400,600);
    }
}
