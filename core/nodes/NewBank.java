package core.nodes;

import core.API;
import core.Areas;
import core.utils.MuleItem;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;

import java.util.Objects;

public class NewBank extends TaskNode {
    private API api;

    public NewBank(API api) {
        this.api = api;
    }

    @Override
    public boolean accept() {
        if (api.muling || !Client.isLoggedIn()) {
            return false;
        }

        return canBank();
    }

    @Override
    public int execute() {
        if (!api.inWilderness()) {
            if (Pker.teleBlocked) {
                Cannon.refill = true;
                Pker.teleBlocked = false;
            }

            if (Bank.isOpen()) {
                api.canAntifire = true;
                api.STATUS = "Banking [0]...";
                Cannon.refill = true;
                Mule.items.clear();
                if (api.hasLootBag()) {
                    if (api.isLootingBagFull() || Objects.nonNull(api.lootingBagInterface()) && api.lootingBagInterface().isVisible()) {
                        api.STATUS = "Depositing looting bag...";
                        if (Objects.nonNull(api.lootingBagInterface()) && api.lootingBagInterface().isVisible()) {
                            if (api.lootingBagInterface().interact()) {
                                sleep(950 + api.sleep(), 1200 + api.sleep());
                            }

                            if (api.lootingBagInterfaceClose() != null && api.lootingBagInterfaceClose().interact()) {
                                sleepUntil(() -> api.lootingBagInterfaceClose() == null || !api.lootingBagInterface().isVisible(), 5_000);
                                sleep(920+ api.sleep(), 1400+ api.sleep());
                            }
                        } else {
                            if (Inventory.get("Looting bag").interact()) {
                                sleepUntil(() -> Objects.nonNull(api.lootingBagInterface()) && api.lootingBagInterface().isVisible(), 3500 + api.sleep());
                                sleep(api.sleep()*6);
                            }
                        }
                    }
                }

                if (Objects.isNull(api.lootingBagInterface()) || !api.lootingBagInterface().isVisible()) {
                    if (!Inventory.isEmpty()) {
                        if (Inventory.contains(i -> Objects.nonNull(i) && i.isNoted())) {
                            if (Bank.depositAllItems()) {
                                sleepUntil(() -> Inventory.isEmpty(), 2400 + api.sleep());
                                sleep(api.sleep());
                            }
                        } else {
                            Inventory.all().forEach(i -> {
                                if (Objects.nonNull(i)) {
                                    if (i.isNoted()) {
                                        if (Bank.depositAll(i.getName())) {
                                            sleepUntil(() -> !Inventory.contains(i.getName()), 2400 + api.sleep());
                                            sleep(api.sleep()*3);
                                        }
                                    } else {
                                        if (!i.getName().contains("Looting bag") && !i.getName().contains("Games necklace") && !i.getName().contains("Ranging potion") && !i.getName().contains("Extended antifire")) {
                                            if (Bank.depositAll(i.getName())) {
                                                sleepUntil(() -> !Inventory.contains(i.getName()), 2400 + api.sleep());
                                                sleep(api.sleep()*3);
                                            }
                                        }
                                    }

                                    if (Cannon.canObtain) {
                                        if (i.getName().contains("Cannon ")) {
                                            if (Bank.depositAll(i.getName())) {
                                                sleepUntil(() -> !Inventory.contains(i.getName()), 2400 + api.sleep());
                                                sleep(api.sleep()*3);
                                            }
                                        }
                                    }
                                }
                            });
                        }


                        if (Inventory.all(i -> Objects.nonNull(i) && i.getName().contains("Ranging potion")).size() > 1) {
                            api.STATUS = "Banking [3]...";
                            if (Bank.depositAll(i -> Objects.nonNull(i) && i.getName().contains("Ranging potion"))) {
                                sleepUntil(() -> !Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Ranging potion")), 3600 + api.sleep());
                                sleep(api.sleep()*3);
                            }
                        }

                        if (Inventory.all(i -> Objects.nonNull(i) && i.getName().contains("Extended antifire")).size() > 1) {
                            api.STATUS = "Banking [77]...";
                            if (Bank.depositAll(i -> Objects.nonNull(i) && i.getName().contains("Extended antifire"))) {
                                sleepUntil(() -> !Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Extended antifire")), 3600 + api.sleep());
                                sleep(api.sleep()*3);
                            }
                        }

                        if (Inventory.all(i -> Objects.nonNull(i) && i.getName().contains("Games necklace")).size() > 1) {
                            api.STATUS = "Banking [4]...";
                            if (Bank.depositAll(i -> Objects.nonNull(i) && i.getName().contains("Games necklace"))) {
                                sleepUntil(() -> !Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Games necklace")), 3600 + api.sleep());
                                sleep(api.sleep()*3);
                            }
                        }
                    }

                    if (!bankMissingItems()) {
                        for (String item : api.getBankItems()) {
                            if (!api.inEquipment(item) && !api.inInventory(item) || (item.contains("Mithril bolts") && Equipment.contains("Mithril bolts") && Equipment.get("Mithril bolts").getAmount() <= 25)) {
                                if (item.contains("Cannon ")) {
                                    if (Cannon.isPlaced) {
                                        continue;
                                    }
                                }

                                if (inBank(item)) {
                                    if (Bank.getCurrentTab() != Bank.getTab(Bank.get(i -> Objects.nonNull(i) && i.getName().contains(item) && i.getAmount() > 0))) {
                                        Bank.openTab(Bank.getTab(Bank.get(i -> Objects.nonNull(i) && i.getName().contains(item) && i.getAmount() > 0)));
                                        sleep(800, 1300);
                                    }

                                    if (item.contains("Mithril bolts")) {
                                        if (withdraw(item, getBoltAmount())) {
                                            sleepUntil(() -> api.inInventory(item), 6400 + api.sleep());
                                            sleep(600 + api.sleep(), 900 + api.sleep());
                                        }
                                        continue;
                                    }

                                    if (item.contains("Bass") || item.contains("Lobster")) {
                                        if (withdraw(item, getFoodAmount())) {
                                            sleepUntil(() -> api.inInventory(item), 6400 + api.sleep());
                                            sleep(600 + api.sleep(), 900 + api.sleep());
                                        }
                                        continue;
                                    }

                                    if (item.contains("Cannonball")) {
                                        if (withdraw(item, 180)) {
                                            sleepUntil(() -> api.inInventory(item), 6400 + api.sleep());
                                            sleep(600 + api.sleep(), 900 + api.sleep());
                                        }
                                        continue;
                                    }

                                    if (withdraw(item, 1)) {
                                        sleepUntil(() -> api.inInventory(item), 6400 + api.sleep());
                                        sleep(600 + api.sleep(), 900 + api.sleep());
                                    }
                                }
                            }
                        }
                    } else {
                        if (!Inventory.isEmpty()) {
                            Bank.depositAllItems();
                            sleep(1200, 1500 + api.sleep());
                        }

                        for (String item : api.getBankItems()) {
                            if (!inBank(item)) {
                                if (item.contains("Looting bag") || item.contains("Ava's accumulator")) {
                                    continue;
                                }

                                if (item.contains("Mithril bolts")) {
                                    Mule.items.add(new MuleItem(item, Calculations.random(1600, 2100)));
                                    continue;
                                }

                                if (item.contains("Bass") || item.contains("Lobster")) {
                                    if (!muleItemsContains("Lobster")) {
                                        Mule.items.add(new MuleItem("Lobster", Calculations.random(2000, 2400)));
                                        continue;
                                    }
                                }

                                if (item.contains("Cannonball")) {
                                    Mule.items.add(new MuleItem(item, Calculations.random(5400, 6000)));
                                    continue;
                                }

                                if (item.contains("Ranging potion")) {
                                    Mule.items.add(new MuleItem(item, Calculations.random(32, 46)));
                                    continue;
                                }

                                if (item.contains("Extended antifire")) {
                                    Mule.items.add(new MuleItem(item, Calculations.random(32, 46)));
                                    continue;
                                }

                                if (item.contains("Amulet of glory(")) {
                                    Mule.items.add(new MuleItem(item, Calculations.random(20, 26)));
                                    continue;
                                }

                                if (item.contains("Games necklace")) {
                                    Mule.items.add(new MuleItem(item, Calculations.random(20, 26)));
                                    continue;
                                }

                                if (item.contains("Cannon ")) {
                                    Mule.items.add(new MuleItem(item, 1));
                                    Cannon.isPlaced = false;
                                    Cannon.refill = false;
                                    Cannon.repair = false;
                                    Cannon.placedTile = null;
                                    continue;
                                }


                                Mule.items.add(new MuleItem(item, Calculations.random(4, 8)));
                            }
                        }
                    }

                    if (Mule.items.size() != 0) {
                        api.muling = true;
                        return api.sleep();
                    }

                    Bank.close();
                }
            } else if (api.shouldWalk()) {
                if (api.hasAmulet() && (Areas.CORP.contains(getLocalPlayer()) || Areas.NULODION.contains(getLocalPlayer()))) {
                    if (Tabs.isOpen(Tab.EQUIPMENT)) {
                        if (Equipment.get(i -> Objects.nonNull(i) && i.getName().contains(api.AMULET)).interact("Edgeville")) {
                            //sleepUntil(() -> api.wildyLevel() == 0, 3600);
                            sleep(3000 + api.sleep(), 4500 + api.sleep());
                        }
                    } else {
                        Tabs.open(Tab.EQUIPMENT);
                    }
                } else {
                    Bank.openClosest();
                }
            }
        } else if (api.hasAmulet()) {
            api.STATUS = "Banking [1]...";
            if (Tabs.isOpen(Tab.EQUIPMENT)) {
                if (api.hasAmulet()) {
                    if (Equipment.get(i -> Objects.nonNull(i) && i.getName().contains(api.AMULET)).interact("Edgeville")) {
                        //sleepUntil(() -> api.wildyLevel() == 0, 3600);
                        sleep(3000 + api.sleep(), 4500 + api.sleep());
                    }
                }
            } else {
                Tabs.open(Tab.EQUIPMENT);
            }
        }

        return api.sleep();
    }

    private boolean withdraw(String name, int amount) {
        return Bank.withdraw((i -> Objects.nonNull(i) && i.getName().contains(name) && i.getAmount() > 0), amount);
    }

    private boolean muleItemsContains(String name) {
        for (MuleItem m : Mule.items) {
            if (m.getName().contains(name)) {
                return true;
            }
        }

        return false;
    }

    private int getFoodAmount() {
        if (api.designated_id == 2) {
            return 18;
        } else if (api.designated_id == 3) {
            return 23;
        }

        if (Skills.getRealLevel(Skill.RANGED) >= 70 && Skills.getRealLevel(Skill.DEFENCE) >= 40) {
            return 14;
        } else if (Skills.getRealLevel(Skill.RANGED) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 40) {
            return 16;
        }

        return 18;
    }

    private boolean canBank() {
        if (org.dreambot.api.methods.container.impl.bank.Bank.isOpen()) {
            return true;
        }

        if (api.inWilderness()) {
            return !api.hasFood() || !api.hasCannonBalls() && Cannon.refill || !api.hasBolts();
        }

        return !api.hasHelmet() ||
                !api.hasChest() ||
                !api.hasLegs() ||
                !api.hasSword() ||
                !api.hasShield() ||
                !api.hasRing() && !Areas.CORP.contains(getLocalPlayer()) ||
                !api.hasBoots() ||
                !api.hasGloves() ||
                //!api.hasCape() ||
                !api.hasBolts() || Inventory.contains("Mithril bolts") && Inventory.get("Mithril bolts").getAmount() > 250 || Equipment.contains("Mithril bolts") && Equipment.get("Mithril bolts").getAmount() <= 25 ||
                !api.hasAmulet() && !Areas.CORP.contains(getLocalPlayer()) ||
                !api.hasFood() ||
                !api.hasCannon() ||
                !api.hasCannonBalls() && Cannon.refill ||
                !api.hasCannonBalls() && api.wildyLevel() == 0;
    }

    private boolean inBank(String name) {
        return Bank.contains(i -> Objects.nonNull(i) && i.getName().contains(name) && i.getAmount() > 0);
    }

    private int getBoltAmount() {
        return (api.hasCape()) ? 150 : 250;
    }

    private boolean bankMissingItems() {
        for (String s : api.getBankItems()) {
            if (!s.contains("Ava's accumulator") && !s.contains("Bass") && !s.contains("Lobster") && !s.contains("Looting bag")) {
//                if (api.inInventory(s) || api.inEquipment(s)) {
//                    return false;
//                }

                if (!inBank(s)) {
                    return true;
                }
            }
        }

        return false;
    }
}
