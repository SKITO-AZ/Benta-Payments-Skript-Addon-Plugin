package im.benta.minecraft.benta;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import im.benta.minecraft.benta.event.EvtCulturelandFail;
import im.benta.minecraft.benta.event.EvtCulturelandSuccess;
import im.benta.minecraft.benta.event.EvtDepositSuccess;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public final class Benta extends JavaPlugin {

    private String prefix;
    private String applicationKey;
    private String title;

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        this.applicationKey = getConfig().getString("appliction_key");
        this.title = getConfig().getString("title");
        this.prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix")+"&f ");

        getLogger().info(prefix+"Benta 결제 플러그인이 활성화되었습니다.");
        getCommand("후원").setExecutor(new BentaCommand(this));

        Skript.registerEvent("Benta Payment Evt Cultureland Success", SimpleEvent.class, EvtCulturelandSuccess.class, "[benta] cultureland success");
        EventValues.registerEventValue(EvtCulturelandSuccess.class, OfflinePlayer.class, new Getter<OfflinePlayer, EvtCulturelandSuccess>() {
            public OfflinePlayer get(EvtCulturelandSuccess e) {
                return Bukkit.getOfflinePlayer(UUID.fromString(e.getIdentifier()));
            }
        }, 0);
        EventValues.registerEventValue(EvtCulturelandSuccess.class, Number.class, new Getter<Number, EvtCulturelandSuccess>() {
            public Number get(EvtCulturelandSuccess e) {
                return e.getPaidAmount();
            }
        }, 0);

        Skript.registerEvent("Benta Payment Evt Cultureland Fail", SimpleEvent.class, EvtCulturelandFail.class, "[benta] cultureland fail");
        EventValues.registerEventValue(EvtCulturelandFail.class, OfflinePlayer.class, new Getter<OfflinePlayer, EvtCulturelandFail>() {
            public OfflinePlayer get(EvtCulturelandFail e) {
                return Bukkit.getOfflinePlayer(UUID.fromString(e.getIdentifier()));
            }
        }, 0);

        Skript.registerEvent("Benta Payment Evt Deposit Success", SimpleEvent.class, EvtDepositSuccess.class, "[benta] deposit success");
        EventValues.registerEventValue(EvtDepositSuccess.class, OfflinePlayer.class, new Getter<OfflinePlayer, EvtDepositSuccess>() {
            public OfflinePlayer get(EvtDepositSuccess e) {
                return Bukkit.getOfflinePlayer(UUID.fromString(e.getIdentifier()));
            }
        }, 0);
        EventValues.registerEventValue(EvtDepositSuccess.class, Number.class, new Getter<Number, EvtDepositSuccess>() {
            public Number get(EvtDepositSuccess e) {
                return e.getPaidAmount();
            }
        }, 0);

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new BentaScheduler(this), 10*20, 20*5);
    }

    @Override
    public void onDisable() {
        getLogger().info(prefix+"Benta 결제 플러그인이 비활성화되었습니다.");
    }

    public String getApplicationKey(){
        return this.applicationKey;
    }

    public String getTitle(){
        return this.title;
    }

    public String getPrefix() {return this.prefix; }



}
