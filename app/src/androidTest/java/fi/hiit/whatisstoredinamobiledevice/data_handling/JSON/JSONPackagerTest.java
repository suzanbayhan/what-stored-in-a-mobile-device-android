package fi.hiit.whatisstoredinamobiledevice.data_handling.JSON;

import android.test.InstrumentationTestCase;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import fi.hiit.whatisstoredinamobiledevice.data_handling.DataHandler;
import fi.hiit.whatisstoredinamobiledevice.data_handling.database_utilities.DeviceDataOpenHelper;
import fi.hiit.whatisstoredinamobiledevice.data_handling.database_utilities.SQLiteDatabaseAccessor;

public class JSONPackagerTest extends InstrumentationTestCase {
    private Map<String, Map<String, String>> testMap;
    private JSONPackager jsonPackager;
    private JSONObject joe;

    protected void setUp() {
        jsonPackager = new JSONPackager(getInstrumentation().getTargetContext());
        testMap = new HashMap<String, Map<String, String>>();
        Map<String, String> innerTestMap = new HashMap<String, String>();
        innerTestMap.put("1", "2");
        innerTestMap.put("3", "4");
        testMap.put("0", innerTestMap);
        joe = jsonPackager.createJsonObjectFromMap(testMap);
        new DataHandler(getInstrumentation().getTargetContext(), new SQLiteDatabaseAccessor(new DeviceDataOpenHelper(getInstrumentation().getTargetContext()))).collectAllData();
    }

    public void testJSONObjectFromHashMapNotNull() {
        assertTrue(joe != null);
    }

    public void testJSONObjectFromStoredData() {
        // todo:Change this when we add tables
        JSONObject jobj = jsonPackager.createJsonObjectFromStoredData();

        assertTrue(jobj.length() == 8);
        assertTrue(jobj.has("uid"));
        assertTrue(jobj.has("image_data"));
        assertTrue(jobj.has("device_info"));
        assertTrue(jobj.has("personal_info"));
        assertTrue(jobj.has("application_data"));
        assertTrue(jobj.has("text_data"));
        assertTrue(jobj.has("audio_data"));
        assertTrue(jobj.has("video_data"));
    }
}
