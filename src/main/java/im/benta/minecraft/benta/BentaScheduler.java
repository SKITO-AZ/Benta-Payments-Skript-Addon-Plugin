package im.benta.minecraft.benta;

import im.benta.minecraft.benta.event.EvtCulturelandFail;
import im.benta.minecraft.benta.event.EvtCulturelandSuccess;
import im.benta.minecraft.benta.event.EvtDepositSuccess;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BentaScheduler implements Runnable {

    private static Benta plugin;

    public BentaScheduler(Benta plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        JSONObject response = getPayment("?token=" + this.plugin.getApplicationKey());
        if (response == null) {
            this.plugin.getLogger().info(this.plugin.getPrefix() + "Benta API 서버와의 연결에 실패하여 결제 정보를 가져오지 못 하였습니다.");
        } else if (response.get("error") == null) {
            String identifier = (String) response.get("identifier");
            long paidAmount = (long) response.get("paid_amount");

            String PAYMENT_METHOD = (String) response.get("method");
            if (PAYMENT_METHOD.equals("CULTURE")) {
                if (paidAmount == 0L)
                    this.plugin.getServer().getScheduler().runTask(this.plugin, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().getPluginManager().callEvent(new EvtCulturelandFail(identifier));
                        }
                    });
                else
                    this.plugin.getServer().getScheduler().runTask(this.plugin, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().getPluginManager().callEvent(new EvtCulturelandSuccess(identifier, paidAmount));
                        }
                    });
            } else if (PAYMENT_METHOD.equals("DEPOSIT"))
                this.plugin.getServer().getScheduler().runTask(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().getPluginManager().callEvent(new EvtDepositSuccess(identifier, paidAmount));
                    }
                });
        } else if (!response.get("error").equals("NO_PROCESSED_PAYMENT")) {
            this.plugin.getLogger().info(this.plugin.getPrefix() + "오류 코드 : " + response.get("error"));
            this.plugin.getLogger().info(this.plugin.getPrefix() + "오류 메시지 : " + response.get("error_msg"));
        }
    }

    private JSONObject getPayment(String query ){
        try {

            URL url = new URL("https://api.benta.im/callback/rest"+query);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestMethod("GET");

            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-type", "application/json");

            con.setDoInput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            StringBuilder sb = new StringBuilder();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseData = (JSONObject) jsonParser.parse(sb.toString());
                return responseData;
            }
        } catch (Exception e){
            this.plugin.getLogger().info(this.plugin.getPrefix()+e);
        }
        return null;
    }


}
