package com.jiexi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GetRes {
	
	public static int getJasonArray(StringBuffer sb) {
        int id = 0;
//		List<Vocation> lc = new ArrayList<Vocation>();
		String jason = new String(sb);
		// Log.e("dcccccccc", "wwwwwwwwwwwwwwwwwsssssssss");
		try {
				JSONObject ja =new JSONObject(jason);
				 id = ja.getInt("score");
				 Log.e("url", ja.getInt("score")+"fgdfhkkkkkkkkkkkkkkkkkkkkkkkkkk");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	public static JSONObject getJasonObject(String json) {
		JSONObject jsonObject=null;
		try {
			jsonObject =new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
}

