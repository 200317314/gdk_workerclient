package core.utils;

public class Packet {
    private String handler, manager;
    private MulePacket mulePacket;

    public Packet(String handler, String manager, MulePacket mulePacket) {
        this.handler = handler;
        this.manager = manager;
        this.mulePacket = mulePacket;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public MulePacket getMulePacket() {
        return mulePacket;
    }

    public void setMulePacket(MulePacket mulePacket) {
        this.mulePacket = mulePacket;
    }
}
