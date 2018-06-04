package de.ameyering.wgplaner.wgplaner;

import android.app.Application;

import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;


public class WGPlanerApplication extends Application {
    private final DataProviderInterface dataProviderInterface;

    public WGPlanerApplication() {
        dataProviderInterface = DataProviderInterface.getInstance();
    }

    public WGPlanerApplication(DataProviderInterface dataProviderInterface) {
        this.dataProviderInterface = dataProviderInterface;
    }

    public DataProviderInterface getDataProviderInterface() {
        return dataProviderInterface;
    }
}
