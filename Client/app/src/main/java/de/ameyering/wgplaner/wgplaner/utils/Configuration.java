package de.ameyering.wgplaner.wgplaner.utils;

/**
 * Created by D067867 on 06.10.2017.
 */

public class Configuration {
    public static Configuration singleton;


    static {
        singleton = new Configuration();
    }

    private Configuration(){
        init();
    }

    private void init(){

    }
}
