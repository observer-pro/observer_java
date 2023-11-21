package pro.sky.observer_java.mapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.model.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonMapper {
    public static JSONObject stepStatusToJson(Map<String,Step> statusMap){
        JSONObject result = new JSONObject();

        for(Step step : statusMap.values()){
            try {
                result.put(step.getName(), step.getStatus());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public static List<String> jsonArrayToStringList(JSONArray jsonArray) throws JSONException {
        List<String> directoryList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            directoryList.add(jsonArray.getString(i));
        }
        return directoryList;
    }
}
