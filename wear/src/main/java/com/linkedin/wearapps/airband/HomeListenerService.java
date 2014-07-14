package com.linkedin.wearapps.airband;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Listens to DataItem events on the home device.
 */
public class HomeListenerService extends WearableListenerService {

    private String TAG = "HomeListenerService";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents + " for " + getPackageName());
        }
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                UpdateBackgroundOrInstrumentFromDataItem(event.getDataItem());
            }
        }
        dataEvents.close();
    }

    private void UpdateBackgroundOrInstrumentFromDataItem(DataItem dataItem) {
        DataMapItem mapDataItem = DataMapItem.fromDataItem(dataItem);
        DataMap data = mapDataItem.getDataMap();

        byte currentInstrument = data.getByte(Constants.CURRENT_INSTRUMENT);
        int currentBackground = data.getInt(Constants.CURRENT_BACKGROUND);

        Intent startActivity = new Intent(this, MyActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Constants.CURRENT_INSTRUMENT, currentInstrument)
                .putExtra(Constants.CURRENT_BACKGROUND, currentBackground);
        startActivity(startActivity);
    }

}
