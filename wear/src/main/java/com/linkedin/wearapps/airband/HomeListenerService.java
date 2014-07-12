package com.linkedin.wearapps.airband;

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
                UpdateNoteOrInstrumentFromDataItem(event.getDataItem());
            }
        }
        dataEvents.close();
    }

    private void UpdateNoteOrInstrumentFromDataItem(DataItem dataItem) {
        DataMapItem mapDataItem = DataMapItem.fromDataItem(dataItem);
        DataMap data = mapDataItem.getDataMap();

        switch (data.getByte(Constants.CURRENT_INSTRUMENT)) {
            case Constants.INSTRUMENT_GUITAR:
                break;
            case Constants.INSTRUMENT_DRUM:
                break;
            default:
                break;
        }

        switch (data.getByte(Constants.CURRENT_NOTE)) {
            case Constants.NOTE_RED:
                break;
            case Constants.NOTE_GREEN:
                break;
            case Constants.NOTE_BLUE:
                break;
            case Constants.NOTE_YELLOW:
                break;
            case Constants.NOTE_ORANGE:
                break;
            default:
                break;
        }
    }

}
