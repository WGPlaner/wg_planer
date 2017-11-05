package de.ameyering.wgplaner.wgplaner.utils;


import android.os.AsyncTask;
import android.support.annotation.Nullable;

import de.ameyering.wgplaner.wgplaner.structure.User;

public abstract class Server {
    private static final String baseUrl = "https://api.wgplaner.ameyerfdsing.de";

    @Nullable
    public static User getUser(String uid) {
        GetUser task = new GetUser();
        User user = null;

        try {
            //user = task.execute();
        } catch (Exception e) {
            return null;
        }

        return user;
    }

    private static class GetUser extends AsyncTask<Void, Void, User> {

        @Override
        protected User doInBackground(Void... voids) {
            return null;
        }
    }
}
