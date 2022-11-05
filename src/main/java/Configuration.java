import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;


public class Configuration {
    JSONObject config = new JSONObject(new String(Files.readAllBytes(Paths.get("src/main/resources/config.json"))));
    String nodeUrl = config.getString("node_url");
    Map<String, String> oracles = new HashMap<>();

    JSONObject abi = config.getJSONObject("abi");

    public Configuration() throws IOException {
        oracles.put("ETH_USD", abi.getString("ETH_USD"));
        oracles.put("LINK_ETH", abi.getString("LINK_ETH"));
        oracles.put("USDT_ETH", abi.getString("USDT_ETH"));
    }
}
