package de.ameyering.wgplaner.test.runner;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import org.mockito.Mockito;

import cucumber.api.android.CucumberInstrumentationCore;
import de.ameyering.wgplaner.test.TestVars;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;

public class Instrumentation extends android.support.test.runner.AndroidJUnitRunner {

    private final CucumberInstrumentationCore instrumentationCore = new CucumberInstrumentationCore(
        this);

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        instrumentationCore.create(bundle);
    }

    @Override
    public void onStart() {
        waitForIdleSync();
        instrumentationCore.start();
    }

    @Override
    public Application newApplication(ClassLoader cl, String className,
        Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        DataProviderInterface dataProviderInterface = Mockito.mock(DataProviderInterface.class);
        TestVars.setMockInterface(dataProviderInterface);

        WGPlanerApplication application = new WGPlanerApplication(
            TestVars.getMockInterface()
        );

        return application;
    }
}
