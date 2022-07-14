package im.benta.minecraft.benta.event;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EvtCulturelandSuccess extends Event {

    private static final HandlerList handlers = new HandlerList();

    private String identifier;
    private long paidAmount;

    public EvtCulturelandSuccess(String identifier, long paidAmount){
        this.identifier = identifier;
        this.paidAmount = paidAmount;

    }

    public String getIdentifier(){
        return this.identifier;
    }

    public long getPaidAmount(){
        return this.paidAmount;
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
