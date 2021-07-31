package codi.drive.pasajero.chiclayo.models;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * By: el-bryant
 */

public class PlaceApi {

    public ArrayList<String> autoComplete(String input) {
        ArrayList arrayList = new ArrayList();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            sb.append("input=" + input + ",%20Chiclayo");
            sb.append("&key=AIzaSyAgKEH4L5QcjUf-3vl4dgXyUF6VoFFgU48");
            URL url = new URL(sb.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = inputStreamReader.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            JSONArray prediction = jsonObject.getJSONArray("predictions");
            for (int i = 0; i < prediction.length(); i++) {
                arrayList.add(prediction.getJSONObject(i)
                        .getString("description")
                        .substring(0, prediction.getJSONObject(i)
                                        .getString("description")
                                        .indexOf(",", prediction.getJSONObject(i)
                                                        .getString("description")
                                                        .indexOf(",") + 1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
