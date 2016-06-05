package save;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Nicky on 4/06/2016.
 */
public class Save {


    JSONObject obj = new JSONObject(" .... ");
    String pageName = obj.getJSONObject("pageInfo").getString("pageName");

    JSONArray arr = obj.getJSONArray("posts");



    public void test(){

        for(int i = 0; i < arr.length(); i++){
            String post_id = arr.getJSONObject(i).getString("post_id");
        }

    }


}
