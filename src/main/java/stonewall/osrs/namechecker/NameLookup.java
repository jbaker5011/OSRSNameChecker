package stonewall.osrs.namechecker;

import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

public class NameLookup implements Callable<Boolean>
{
    private String name;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:60.0) Gecko/20100101 Firefox/60.0";

    public NameLookup(String name)
    {
        this.name = name;
    }

    @Override
    public Boolean call() throws Exception
    {
        String url = "https://secure.runescape.com/m=account-creation/g=oldscape/check_displayname.ajax";
        URL obj = new URL(url);
        HttpsURLConnection httpsURLConnection = null;

        httpsURLConnection = (HttpsURLConnection) obj.openConnection();

        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.setRequestProperty("Host", "secure.runescape.com");
        httpsURLConnection.setRequestProperty("User-Agent", USER_AGENT);
        httpsURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        httpsURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        httpsURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        httpsURLConnection.setRequestProperty("Referer", "https://secure.runescape.com/m=account-creation/g=oldscape/create_account");
        httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpsURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        httpsURLConnection.setRequestProperty("Content-Length", "17");
        httpsURLConnection.setRequestProperty("Cookie", "settings=_gali=display-name");
        httpsURLConnection.setRequestProperty("Connection", "keep-alive");
        httpsURLConnection.setRequestProperty("Pragma", "no-cache");
        httpsURLConnection.setRequestProperty("Cache-Control", "no-cache");
        String urlParameters = "displayname=" + name;

        httpsURLConnection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(httpsURLConnection.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        StringBuilder response = new StringBuilder();
        Reader reader = new InputStreamReader(new GZIPInputStream(httpsURLConnection.getInputStream()));
        while (true)
        {
            int ch = reader.read();
            if (ch == -1)
            {
                break;
            }
            response.append((char) ch);
        }

        JSONObject jsonObject = new JSONObject(String.valueOf(response.toString()));
        return jsonObject.getBoolean("displayNameIsValid");
    }
}
