package de.ameyering.wgplaner.wgplaner.section.splashscreen;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.section.home.JoinGroupActivity;
import de.ameyering.wgplaner.wgplaner.section.registration.RegistrationActivity;
import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.GroupApi;
import io.swagger.client.api.UserApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.model.Group;
import io.swagger.client.model.User;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String FIREBASE_AUTH_TAG = "FIREBASE_AUTH";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private boolean loadJoinGroup = false;
    private String joinGroupKey = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (currentUser == null) {
                        Log.d(FIREBASE_AUTH_TAG, "logInAnonymously:success");
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        onUser(currentUser);
                    }

                } else {
                    Log.d(FIREBASE_AUTH_TAG, "logInAnonymously:failure");
                    Toast.makeText(SplashScreenActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    onUser(null);
                }
            }
        });

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if(appLinkData != null){
            List<String> path = appLinkData.getPathSegments();
            if(path.size() != 0) {
                if(path.get(0).equals("v0.1")) {
                    if (path.get(1).equals("groups")) {
                        if (path.get(2).equals("join")) {

                            joinGroupKey = path.get(3);
                            loadJoinGroup = true;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();
        onUser(currentUser);
    }

    private void onUser(FirebaseUser user) {
        initialize();

        if (user != null) {
            String uid = user.getUid();
            DataContainer.Me.updateUid(uid);
            initializeUser(uid);
        }
    }

    private void initializeUser(String uid) {
        if (uid != null) {
            ApiClient client = io.swagger.client.Configuration.getDefaultApiClient();

            client.setBasePath("https://api.wgplaner.ameyering.de/v0.1");

            ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication("FirebaseIDAuth");
            firebaseAuth.setApiKey(uid);

            UserApi api = new UserApi();

            try {
                api.getUserAsync(uid, new ApiCallback<User>() {
                    @Override
                    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                        switch (statusCode) {
                            case 401:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SplashScreenActivity.this, getString(R.string.user_unauthorized),
                                            Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;

                            case 404:
                                loadRegistration();
                                break;

                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SplashScreenActivity.this, getString(R.string.server_connection_failed),
                                            Toast.LENGTH_LONG).show();
                                        //finish();
                                    }
                                });
                                break;
                        }
                    }

                    @Override
                    public void onSuccess(User result, int statusCode, Map<String, List<String>> responseHeaders) {
                        if (result != null) {
                            DataContainer.Me.setMe(result);
                            GetDataTask task = new GetDataTask();

                            try {
                                task.execute().get();

                            } catch (InterruptedException e) {
                                loadNext();

                            } catch (ExecutionException e) {
                                loadNext();
                            }

                            loadNext();
                        }
                    }

                    @Override
                    public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                    }

                    @Override
                    public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                    }
                });

            } catch (ApiException e) {
                Toast.makeText(this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initialize() {
        Configuration.initConfig(this);
        Money.initialize(Locale.getDefault());
    }

    private void loadHome() {
        Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadRegistration() {
        Intent intent = new Intent(SplashScreenActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    private static class GetDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            User user = DataContainer.Me.getMe();

            if (user != null) {
                if(user.getGroupUid() != null) {
                    GroupApi groupApi = new GroupApi();

                    ApiClient groupClient = groupApi.getApiClient();

                    ApiKeyAuth userAuth = (ApiKeyAuth) groupClient.getAuthentication("UserIDAuth");
                    userAuth.setApiKey(user.getUid());

                    groupClient.setBasePath("https://api.wgplaner.ameyering.de/v0.1");

                    try {
                        Group group = groupApi.getGroup(user.getGroupUid().toString());

                        if (group != null) {
                            DataContainer.Groups.setGroup(group);

                            List<String> memberUids = group.getMembers();
                            ArrayList<User> members = new ArrayList<>();

                            ApiClient userClient = io.swagger.client.Configuration.getDefaultApiClient();

                            userClient.setBasePath("https://api.wgplaner.ameyering.de/v0.1");

                            ApiKeyAuth firebaseAuth = (ApiKeyAuth) userClient.getAuthentication("FirebaseIDAuth");
                            firebaseAuth.setApiKey(user.getUid());

                            UserApi userApi = new UserApi();

                            for (String uid : memberUids) {
                                try {
                                    User member = userApi.getUser(uid);

                                    if (member != null) {
                                        members.add(member);
                                    }

                                } catch (ApiException e) {
                                    continue;
                                }
                            }

                            DataContainer.GroupMembers.setMembers(members);
                        }

                    } catch (ApiException e) {
                        //TODO: Implement failure
                    }
                }

                //TODO: Implement other Server connections
            }

            return null;
        }
    }

    private void loadNext(){
        if(loadJoinGroup){
            onLoadJoinGroup();
        } else {
            loadHome();
        }
    }

    private void onLoadJoinGroup(){
        Intent intent = new Intent(this, JoinGroupActivity.class);
        intent.putExtra("ACCESS_KEY", joinGroupKey);
        intent.setAction(Intent.ACTION_QUICK_VIEW);
        startActivity(intent);
        finish();
    }
}
