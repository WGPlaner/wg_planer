package de.ameyering.wgplaner.test;

import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;

public abstract class TestVars {
    private static DataProviderInterface mockInterface;

    public static void setMockInterface(DataProviderInterface dataProviderInterface) {
        TestVars.mockInterface = mockInterface;
    }

    public static DataProviderInterface getMockInterface() {
        return mockInterface;
    }

}
