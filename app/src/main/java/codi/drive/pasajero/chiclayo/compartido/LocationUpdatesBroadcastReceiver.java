package codi.drive.pasajero.chiclayo.compartido;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import com.google.android.gms.location.LocationResult;
import java.util.List;

/**
 * By: El Bryant
 */

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LUBroadcastReceiver";
    public static final String ACTION_PROCESS_UPDATES = "com.google.android.gms.location.sample.locationupdatespendingintent.action" + ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    Utils.setLocationUpdatesResult(context, locations);
                }
            }
        }
    }
}
