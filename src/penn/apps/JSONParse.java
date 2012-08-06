package penn.apps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParse {

	public static String[] parse(String s) {
		String[] i;

		try {
			JSONObject j = new JSONObject(s);
			JSONArray ids = j.getJSONArray("data");
			double d = Math.random() * ids.length();

			i = new String[2];
			i[0] = ids.getJSONObject((int) d).getString("id")
					.toString();
			i[1] = ids.getJSONObject((int) d).getString("name")
					.toString();
			System.out.println(ids.length());
			System.out.println(i[0]);
			System.out.println(i[1]);
			return i;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public static String[] parse(String s, String t){
		String[] i;

		try {
			JSONObject j = new JSONObject(s);
			JSONArray ids = j.getJSONArray("data");
			int length = ids.length();

			i = new String[2];
			for(int k = 0; k < length; k+=1){
				if (t.matches("(?i).*"+ids.getJSONObject(k).getString("name")+"*")){
					i[0] = ids.getJSONObject(k).getString("id");
					i[1] = ids.getJSONObject(k).getString("name");
					return i;
				}
				else k++;
			}
			i[0] = "-1";
			i[1] = "-1";
			return i;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
