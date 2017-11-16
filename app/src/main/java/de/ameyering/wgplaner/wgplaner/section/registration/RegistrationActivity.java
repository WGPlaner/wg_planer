package de.ameyering.wgplaner.wgplaner.section.registration;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.NavigationFragment;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.PickDisplayNameFragment;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.StateEMailFragment;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.UploadProfilePictureFragment;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.WelcomeFragment;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.UserApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.model.User;

public class RegistrationActivity extends AppCompatActivity {
    private static String ACTUAL_FRAGMENT_TAG = "ActualFragment";
    private static final int WELCOME_SCREEN = 0;
    private static final int PICK_DISPLAY_NAME_SCREEN = 1;
    private static final int UPLOAD_PROFILE_PICTURE_SCREEN = 2;
    private static final int STATE_EMAIL_SCREEN = 3;
    private Toolbar toolbar;
    private Stack<NavigationFragment> backStack = new Stack<>();
    private Stack<NavigationFragment> registrationFlow = new Stack<>();
    private NavigationFragment actualFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.INVISIBLE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationContentDescription(getString(R.string.nav_back));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBackNavigation();
            }
        });

        initializeRegistrationFlow(savedInstanceState);
    }

    private void initializeRegistrationFlow(Bundle savedInstanceState) {


        NavigationFragment.OnNavigationEventListener navigationEventListener = new
        NavigationFragment.OnNavigationEventListener() {
            @Override
            public void onForward() {
                if (registrationFlow.size() > 0) {
                    backStack.push(actualFragment);
                    actualFragment = registrationFlow.pop();

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right,
                        R.anim.anim_fragment_exit_to_left);

                    transaction.replace(R.id.container_registration, actualFragment);
                    transaction.commit();

                    if (backStack.size() > 0) {
                        toolbar.setVisibility(View.VISIBLE);
                    }

                } else {
                    UserApi api = new UserApi();

                    User user = new User();
                    user.setUid(Configuration.singleton.getConfig(Configuration.Type.USER_UID));
                    user.setDisplayName(Configuration.singleton.getConfig(Configuration.Type.USER_DISPLAY_NAME));
                    user.setEmail(Configuration.singleton.getConfig(Configuration.Type.USER_EMAIL_ADDRESS));

                    ApiClient client = api.getApiClient();

                    ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication("FirebaseIDAuth");
                    firebaseAuth.setApiKey(user.getUid());

                    client.setBasePath("https://api.wgplaner.ameyering.de/v0.1");

                    try {
                        api.createUserAsync(user, new ApiCallback<User>() {
                            @Override
                            public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegistrationActivity.this, getString(R.string.server_connection_failed),
                                            Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(User result, int statusCode, Map<String, List<String>> responseHeaders) {
                                Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                            }

                            @Override
                            public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                            }
                        });

                    } catch (ApiException e) {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.server_connection_failed),
                            Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onBack() {
                handleBackNavigation();
            }
        };

        int actualFragment = WELCOME_SCREEN;

        if (savedInstanceState != null) {
            actualFragment = savedInstanceState.getInt(ACTUAL_FRAGMENT_TAG);
        }

        StateEMailFragment stateEmailFragment = new StateEMailFragment();
        stateEmailFragment.setNavigationEventListener(navigationEventListener);

        PickDisplayNameFragment pickDisplayNameFragment = new PickDisplayNameFragment();
        pickDisplayNameFragment.setNavigationEventListener(navigationEventListener);

        UploadProfilePictureFragment uploadProfilePictureFragment = new UploadProfilePictureFragment();
        uploadProfilePictureFragment.setNavigationEventListener(navigationEventListener);

        WelcomeFragment welcomeFragment = new WelcomeFragment();
        welcomeFragment.setNavigationEventListener(navigationEventListener);

        switch (actualFragment) {
            case PICK_DISPLAY_NAME_SCREEN:
                toolbar.setVisibility(View.VISIBLE);

                this.actualFragment = pickDisplayNameFragment;

                registrationFlow.push(stateEmailFragment);
                registrationFlow.push(uploadProfilePictureFragment);
                backStack.push(welcomeFragment);

                break;

            case UPLOAD_PROFILE_PICTURE_SCREEN:
                toolbar.setVisibility(View.VISIBLE);

                this.actualFragment = uploadProfilePictureFragment;

                registrationFlow.push(stateEmailFragment);
                backStack.push(welcomeFragment);
                backStack.push(pickDisplayNameFragment);

                break;

            case STATE_EMAIL_SCREEN:
                toolbar.setVisibility(View.VISIBLE);

                this.actualFragment = stateEmailFragment;

                backStack.push(welcomeFragment);
                backStack.push(pickDisplayNameFragment);
                backStack.push(uploadProfilePictureFragment);

                break;

            default:
                toolbar.setVisibility(View.INVISIBLE);

                this.actualFragment = welcomeFragment;

                registrationFlow.push(stateEmailFragment);
                registrationFlow.push(uploadProfilePictureFragment);
                registrationFlow.push(pickDisplayNameFragment);

                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.container_registration, this.actualFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        handleBackNavigation();
    }

    private void handleBackNavigation() {
        if (backStack.size() == 0) {
            finish();

        } else if (backStack.size() <= 1) {
            toolbar.setVisibility(View.INVISIBLE);
        }

        if (backStack.size() > 0) {
            registrationFlow.push(actualFragment);
            actualFragment = backStack.pop();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_left,
                R.anim.anim_fragment_exit_to_right);

            transaction.replace(R.id.container_registration, actualFragment);
            transaction.commit();

        } else {
            //TODO: handle empty BackStack
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (registrationFlow != null) {
            switch (registrationFlow.size()) {
                case 3:
                    outState.putInt(ACTUAL_FRAGMENT_TAG, WELCOME_SCREEN);
                    break;

                case 2:
                    outState.putInt(ACTUAL_FRAGMENT_TAG, PICK_DISPLAY_NAME_SCREEN);
                    break;

                case 1:
                    outState.putInt(ACTUAL_FRAGMENT_TAG, UPLOAD_PROFILE_PICTURE_SCREEN);
                    break;

                case 0:
                    outState.putInt(ACTUAL_FRAGMENT_TAG, STATE_EMAIL_SCREEN);
                    break;

                default:
                    outState.putInt(ACTUAL_FRAGMENT_TAG, -1);
            }
        }
    }
}
