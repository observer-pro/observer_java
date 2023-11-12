package pro.sky.observer_java.mapper;

import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.model.Step;

import java.util.Map;

public class JsonMapper {
    public JSONObject stepStatusToJson(Map<String,Step> statusMap){
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
}
