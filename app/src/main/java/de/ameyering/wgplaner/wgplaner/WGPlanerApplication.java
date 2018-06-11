package de.ameyering.wgplaner.wgplaner;

import android.app.Application;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;


public class WGPlanerApplication extends Application {
    private DataProviderInterface dataProviderInterface;

    public WGPlanerApplication() {
        dataProviderInterface = null;
    }

    public WGPlanerApplication(DataProviderInterface dataProviderInterface) {
        this.dataProviderInterface = dataProviderInterface;
    }

    @Override
    public void onCreate() {
        if (dataProviderInterface == null) {
            dataProviderInterface = DataProviderInterface.getInstance(getApplicationContext());
        }

        super.onCreate();
    }

    public DataProviderInterface getDataProviderInterface() {
        return dataProviderInterface;
    }
}
