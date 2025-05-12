package im.benta.minecraft.benta;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.bukkit.Bukkit.getLogger;

public class BentaCommand implements CommandExecutor {

    private Benta plugin;

    public BentaCommand(Benta plugin){
        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {


                    player.sendMessage(plugin.getPrefix() + "결제 페이지를 생성 중입니다. 잠시만 기다려 주세요.");
                    JSONObject response =  generateBentaPayment("{ \"token\" : \""+plugin.getApplicationKey()+"\",  \"title\" : \""+plugin.getTitle()+"\", \"identifier\" : \""+player.getUniqueId().toString()+"\" }");
                    if (response == null) {
                        player.sendMessage(plugin.getPrefix() + "결제 페이지 생성 중에 오류가 발생하였습니다.");
                    }
                    else if (response.get("error") != null) {
                        player.sendMessage(plugin.getPrefix() + "오류 코드 : "+response.get("error"));
                        player.sendMessage(plugin.getPrefix() + "오류 메세지 : "+response.get("error_msg"));
                    }
                    else {
                        player.sendMessage(plugin.getPrefix() + "아래의 링크를 클릭하여 결제를 진행하여 주세요.");
                        player.sendMessage(plugin.getPrefix() + "https://pay.benta.im/" + response.get("payToken"));
                    }
                }
            });

            return true;
        }
        else if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(this.plugin.getPrefix()+"해당 명령어는 인게임에서만 이용하실 수 있습니다.");
            return false;
        }
        return false;
    }

    private JSONObject generateBentaPayment(String jsonMessage ){
        try {

            URL url = new URL("https://api.benta.im/payments");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000); //서버에 연결되는 Timeout 시간 설정
            con.setReadTimeout(5000); // InputStream 읽어 오는 Timeout 시간 설정
            con.setRequestMethod("POST"); //어떤 요청으로 보낼 것인지?

            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-type", "application/json");

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(jsonMessage);
            wr.flush();

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
            getLogger().info(this.plugin.getPrefix()+e);
        }
        return null;
    }
}
