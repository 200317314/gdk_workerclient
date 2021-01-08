package core.utils;

import java.util.List;

public class MulePacket {
    private int world, x, y;
    private String name;
    private List<MuleItem> items;

    public MulePacket(int world, int x, int y, String name, List<MuleItem> items) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.name = name;
        this.items = items;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MuleItem> getItems() {
        return items;
    }

    public void setItems(List<MuleItem> items) {
        this.items = items;
    }
}
