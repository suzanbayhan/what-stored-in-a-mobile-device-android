package fi.hiit.whatisstoredinamobiledevice.data_handling.database_utilities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SQLiteDatabaseAccessor implements DatabaseAccessor {
    private SQLiteQueryBuilder mSQLiteQueryBuilder;
    private SQLiteOpenHelper mDeviceDataOpenHelper;
    private SQLiteDatabase db;

    public SQLiteDatabaseAccessor(SQLiteOpenHelper deviceDataOpenHelper) {
        mSQLiteQueryBuilder = new SQLiteQueryBuilder();
        mDeviceDataOpenHelper = deviceDataOpenHelper;
    }

    /**
     * Opens database, saves data to the database, and closes the database
     * @param allDataMap map of data to be saved
     * @return true if save successful, false otherwise
     */
    @Override
    public boolean saveAllData(Map<String, Map<String, Map<String, String>>> allDataMap) {
        db = mDeviceDataOpenHelper.getWritableDatabase();

        boolean saveSuccessful = saveAll(allDataMap);

        db.close();

        return saveSuccessful;
    }

    /**
     * Saves the data in the map to the database
     * @param allDataMap
     * @return
     */
    private boolean saveAll(Map<String, Map<String, Map<String, String>>> allDataMap) {
        String datetime = datetime();
        for(String tableName : allDataMap.keySet()) {
            for(String tempRowIndex : allDataMap.get(tableName).keySet()) {
                if (!saveRow(tableName, tempRowIndex, allDataMap.get(tableName), datetime)) return false;
            }
        }
        return true;
    }

    /**
     * Saves a single row to the database
     * @param tableName
     * @param tempRowIndex
     * @param tableMap
     * @param datetime
     * @return
     */
    private boolean saveRow(String tableName, String tempRowIndex, Map<String, Map<String, String>> tableMap, String datetime) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        insertValues(tempRowIndex, tableMap, values, datetime);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                tableName,
                null,
                values);
        return newRowId != -1;
    }

    /**
     * Saves map data to ContentValues
     * @param tempRowIndex
     * @param tableMap
     * @param values
     * @param datetime
     */
    private void insertValues(String tempRowIndex, Map<String, Map<String, String>> tableMap, ContentValues values, String datetime) {
        values.put(DeviceDataContract.DeviceInfoEntry.COLUMN_NAME_DATETIME, datetime);
        values.put(DeviceDataContract.DeviceInfoEntry.COLUMN_NAME_SENT, 0);
        for(String columnName : tableMap.get(tempRowIndex).keySet()) {
            if (dateInWrongFormat(columnName, tableMap, tempRowIndex)) {
                values.put(columnName, ""+(Long.parseLong(tableMap.get(tempRowIndex).get(columnName))/1000));
            } else {
                values.put(columnName, tableMap.get(tempRowIndex).get(columnName));
            }
        }
    }

    /**
     * Set all data as sent, so it won't be sent again
     */
    public void setAllSent() {
        boolean dbWasNull = false;
        if (db == null) {
            db = mDeviceDataOpenHelper.getWritableDatabase();
            dbWasNull = true;
        }
        ContentValues values = new ContentValues();
        values.put(DeviceDataContract.DeviceInfoEntry.COLUMN_NAME_SENT, "sent");
        for(String table : DeviceDataContract.TABLE_NAMES) {
            db.update(table, values, null, null);
        }
        if (dbWasNull) {
            db.close();
        }
    }

    /**
     * Convert dates to be in the same format
     * @param columnName
     * @param tableMap
     * @param tempRowIndex
     * @return
     */
    private boolean dateInWrongFormat(String columnName, Map<String, Map<String, String>> tableMap, String tempRowIndex) {
        return tableMap.get(tempRowIndex).get(columnName) != null && (
                columnName.equals(DeviceDataContract.ImageDataEntry.COLUMN_NAME_DATE_TAKEN) ||
                columnName.equals(DeviceDataContract.ApplicationDataEntry.COLUMN_NAME_FIRST_INSTALLED)
        );
    }


    /**
     * Returns the current date in the same format used by other dates in the database
     * @return
     */
    private String datetime() {
        Date date = new Date();
        return date.getTime()/1000+"";
    }

    @Override
    public Map<String, Map<String, String>> getData(String tablename, String[] columnNames, String sortOrder) {
        Cursor c = getCursor(tablename, columnNames, sortOrder, null, null);
        return putDataIntoMap(c);
    }

    private Cursor getCursor(String tablename, String[] columnNames, String sortOrder, String selection, String limit) {
        db = mDeviceDataOpenHelper.getReadableDatabase();
        return db.query(
                    tablename,
                    columnNames,
                    selection,
                    null,
                    null,
                    null,
                    sortOrder,
                    limit
            );
    }

    /**
     * Get latest collected data only
     * @param tablename
     * @param columnNames
     * @return
     */
    @Override
    public Map<String, Map<String, String>> getLatestData(String tablename, String[] columnNames) {
        String sortOrder = DeviceDataContract.ImageDataEntry.COLUMN_NAME_DATETIME + " DESC";
        Cursor c = getCursor(tablename, new String[] {DeviceDataContract.ImageDataEntry.COLUMN_NAME_DATETIME}, sortOrder, null, "1");
        c.moveToNext();
        if (c.getCount() == 0) return putDataIntoMap(c);
        String latestDate = c.getString(0);
        String latestDateQuery = "DATETIME = " + latestDate;
        c = getCursor(tablename, columnNames, sortOrder, latestDateQuery, null);
        return putDataIntoMap(c);
    }

    private Map<String, Map<String, String>> putDataIntoMap(Cursor cursor) {
        Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
        int dataCounter = 0;
        while(cursor.moveToNext()) {
            data.put("" + dataCounter, getSingleObjectHashMap(cursor));
            dataCounter++;
        }
        db.close();
        return data;
    }

    /**
     * Contructs a map containing information of a single object (application, image, etc.)
     * @param cursor
     * @return
     */
    private Map<String, String> getSingleObjectHashMap(Cursor cursor) {
        HashMap<String, String> data = new HashMap<String, String>();
        for (int i=0; i< cursor.getColumnCount(); i++) {
            data.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
        }
        return data;
    }

}
