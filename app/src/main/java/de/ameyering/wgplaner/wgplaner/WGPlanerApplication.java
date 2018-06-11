package de.ameyering.wgplaner.wgplaner;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;


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
        if(dataProviderInterface == null) {
            dataProviderInterface = DataProviderInterface.getInstance(getApplicationContext());
        }

        super.onCreate();
    }

    public DataProviderInterface getDataProviderInterface() {
        return dataProviderInterface;
    }
}
