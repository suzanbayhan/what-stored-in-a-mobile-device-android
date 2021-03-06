package fi.hiit.whatisstoredinamobiledevice.data_handling.data_collection;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import fi.hiit.whatisstoredinamobiledevice.data_handling.database_utilities.DeviceDataContract;

public class ImageDataCollector extends MediaDataCollector {
    public ImageDataCollector(Context context) {
        super(context);
    }

    /**
     * Column names used by the application's database
     */
    public static final String[] imageColumnNames =
            {
                    DeviceDataContract.ImageDataEntry.COLUMN_NAME_DATE_TAKEN,
                    DeviceDataContract.ImageDataEntry.COLUMN_NAME_IS_PRIVATE,
                    DeviceDataContract.ImageDataEntry.COLUMN_NAME_LATITUDE,
                    DeviceDataContract.ImageDataEntry.COLUMN_NAME_LONGITUDE,
                    DeviceDataContract.ImageDataEntry.COLUMN_NAME_DATE_ADDED,
                    DeviceDataContract.ImageDataEntry.COLUMN_NAME_SIZE,
                    DeviceDataContract.ImageDataEntry.COLUMN_NAME_DATE_MODIFIED,
                    DeviceDataContract.ImageDataEntry.COLUMN_NAME_SENT,
                    DeviceDataContract.ImageDataEntry.COLUMN_NAME_DATETIME
            };

    /**
     * Android column names that define what data is taken from MediaStore
     */
    public static String[] projection =
            {
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.IS_PRIVATE,
                    MediaStore.Images.ImageColumns.LATITUDE,
                    MediaStore.Images.ImageColumns.LONGITUDE,
                    MediaStore.Images.ImageColumns.DATE_ADDED,
                    MediaStore.Images.ImageColumns.SIZE,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED
            };

    @Override
    protected Uri getUri() {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return projection;
    }

    @Override
    protected String getSelection() {
        return null;
    }

    @Override
    protected String[] getSelectionArgs() {
        return null;
    }

    @Override
    protected String getSortOrder() {
        return null;
    }

    @Override
    protected String[] getOwnColumnNames() {
        return imageColumnNames;
    }

    @Override
    public String getTableNameForData() {
        return DeviceDataContract.ImageDataEntry.TABLE_NAME;
    }
}
