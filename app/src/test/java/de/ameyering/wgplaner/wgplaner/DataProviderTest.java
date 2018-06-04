package de.ameyering.wgplaner.wgplaner;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;
import de.ameyering.wgplaner.wgplaner.utils.OnAsyncCallListener;
import de.ameyering.wgplaner.wgplaner.utils.ServerCallsInterface;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.Group;
import io.swagger.client.model.User;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DataProviderTest {

    @Test
    public void testInstantiation() {
        ServerCallsInterface serverCallsInterface = mock(ServerCallsInterface.class);
        ImageStore imageStore = mock(ImageStore.class);
        Configuration configuration = mock(Configuration.class);
        FirebaseInstanceId firebaseInstanceId = mock(FirebaseInstanceId.class);

        DataProvider dataProvider = new DataProvider(serverCallsInterface, imageStore, configuration, firebaseInstanceId);
        assertNotNull(dataProvider);
    }

    @Test
    public void testInitialization() {
        ServerCallsInterface serverCallsInterface = mock(ServerCallsInterface.class);
        ImageStore imageStore = mock(ImageStore.class);
        Configuration configuration = mock(Configuration.class);
        FirebaseInstanceId firebaseInstanceId = mock(FirebaseInstanceId.class);

        String testUid = "thisIsATestUid";

        DataProvider objectUnderTest = new DataProvider(serverCallsInterface, imageStore, configuration, firebaseInstanceId);

        Context context = mock(Context.class);

        doNothing().when(serverCallsInterface).setCurrentUserUid(eq(testUid));
        doNothing().when(configuration).addConfig(eq(Configuration.Type.USER_UID), eq(testUid));

        ApiResponse<User> mockUserResponse = mock(ApiResponse.class);

        doReturn(mockUserResponse).when(serverCallsInterface).getUser(eq(testUid));

        User mockUser = mock(User.class);
        UUID mockUUID = new UUID(1L, 10L);
        String mockDisplayName = "testDisplayName";
        String mockEmail = "testEmail";
        String mockInstanceId = "testFirebaseInstanceId";

        doReturn(mockUser).when(mockUserResponse).getData();

        doReturn(testUid).when(mockUser).getUid();
        doReturn(mockUUID).when(mockUser).getGroupUID();
        doReturn(mockDisplayName).when(mockUser).getDisplayName();
        doReturn(mockEmail).when(mockUser).getEmail();
        doReturn(null).when(mockUser).getFirebaseInstanceID();
        doReturn(mockInstanceId).when(configuration).getConfig(Configuration.Type.FIREBASE_INSTANCE_ID);
        doReturn(mockInstanceId).when(firebaseInstanceId).getToken();

        File mockUserImage = mock(File.class);

        doReturn(mockUserImage).when(imageStore).getProfilePictureFile();

        doReturn(10000L).when(mockUserImage).length();

        ApiResponse<Group> mockGroupResponse = mock(ApiResponse.class);

        doReturn(mockGroupResponse).when(serverCallsInterface).getGroup();

        Group mockGroup = mock(Group.class);
        String mockName = "testGroupName";
        String mockCurrencyCode = "EUR";

        List<String> mockUids = new ArrayList<>();
        mockUids.add(testUid);

        doReturn(mockGroup).when(mockGroupResponse).getData();
        doReturn(mockName).when(mockGroup).getDisplayName();
        doReturn(mockCurrencyCode).when(mockGroup).getCurrency();
        doReturn(mockUids).when(mockGroup).getMembers();
        doReturn(mockUids).when(mockGroup).getAdmins();

        //Everything works as expected
        assertEquals(DataProviderInterface.SetUpState.SETUP_COMPLETED, objectUnderTest.initialize(testUid, context));
        verify(serverCallsInterface, times(1)).setCurrentUserUid(eq(testUid));
        verify(configuration, times(1)).addConfig(eq(Configuration.Type.USER_UID), eq(testUid));
        verify(serverCallsInterface, times(1)).getUser(eq(testUid));
        verify(imageStore, times(1)).getProfilePictureFile();
        verify(serverCallsInterface, times(1)).getGroup();
        verify(serverCallsInterface, times(1)).getGroupImageAsync(any());
        verify(serverCallsInterface, times(1)).getUserAsync(eq(testUid), any());
        verify(serverCallsInterface, times(1)).updateUserAsync(any(), any());

        ArgumentCaptor<OnAsyncCallListener<byte[]>> groupImageCaptor = ArgumentCaptor.forClass(OnAsyncCallListener.class);
        verify(serverCallsInterface).getGroupImageAsync(groupImageCaptor.capture());

        groupImageCaptor.getValue().onFailure(null);


        ArgumentCaptor<OnAsyncCallListener<User>> userCaptor = ArgumentCaptor.forClass(OnAsyncCallListener.class);
        verify(serverCallsInterface).getUserAsync(anyString(), userCaptor.capture());

        userCaptor.getValue().onFailure(null);

        //Group call failed
        doReturn(null).when(mockGroupResponse).getData();
        assertEquals(DataProviderInterface.SetUpState.GET_GROUP_FAILED, objectUnderTest.initialize(testUid, context));

        //user has no group
        doReturn(null).when(mockUser).getGroupUID();
        assertEquals(DataProviderInterface.SetUpState.REGISTERED, objectUnderTest.initialize(testUid, context));

        //user is unregistered
        doReturn(null).when(mockUserResponse).getData();
        doReturn(404).when(mockUserResponse).getStatusCode();

        assertEquals(DataProviderInterface.SetUpState.UNREGISTERED, objectUnderTest.initialize(testUid, context));

        //getting the user failed
        doReturn(null).when(mockUserResponse).getData();
        doReturn(303).when(mockUserResponse).getStatusCode();

        assertEquals(DataProviderInterface.SetUpState.GET_USER_FAILED, objectUnderTest.initialize(testUid, context));

        //get user call failed
        doReturn(null).when(mockUserResponse).getData();
        doReturn(0).when(mockUserResponse).getStatusCode();

        assertEquals(DataProviderInterface.SetUpState.CONNECTION_FAILED, objectUnderTest.initialize(testUid, context));

        //get user call failed
        doReturn(null).when(serverCallsInterface).getUser(eq(testUid));

        assertEquals(DataProviderInterface.SetUpState.CONNECTION_FAILED, objectUnderTest.initialize(testUid, context));

        //uid is null or empty
        assertEquals(DataProviderInterface.SetUpState.GET_USER_FAILED, objectUnderTest.initialize(null, context));
        assertEquals(DataProviderInterface.SetUpState.GET_USER_FAILED, objectUnderTest.initialize("", context));
    }

    @Test
    public void testRegisterUser() {
        ServerCallsInterface serverCallsInterface = mock(ServerCallsInterface.class);
        ImageStore imageStore = mock(ImageStore.class);
        Configuration configuration = mock(Configuration.class);
        FirebaseInstanceId firebaseInstanceId = mock(FirebaseInstanceId.class);

        String testUid = "thisIsATestUid";
        String testDisplayName = "thisIsATestDisplayName";
        String testEmail = "thisIsATestEmail";
        String testInstanceId = "thisIsATestInstanceId";

        DataProvider objectUnderTest = new DataProvider(serverCallsInterface, imageStore, configuration, firebaseInstanceId);
        objectUnderTest.setCurrentUserDisplayName(testDisplayName, null);
        objectUnderTest.setCurrentUserEmail(testEmail, null);

        doReturn(testInstanceId).when(firebaseInstanceId).getToken();


        ArgumentCaptor<OnAsyncCallListener<User>> userCaptor = ArgumentCaptor.forClass(OnAsyncCallListener.class);

        objectUnderTest.registerUser(null);

        verify(serverCallsInterface).createUserAsync(any(), userCaptor.capture());

        User mockUser = mock(User.class);

        doReturn(testUid).when(mockUser).getUid();
        doReturn(testDisplayName).when(mockUser).getDisplayName();
        doReturn(testEmail).when(mockUser).getEmail();
        doReturn(testInstanceId).when(mockUser).getFirebaseInstanceID();

        userCaptor.getValue().onSuccess(mockUser);
    }

    @Test
    public void testSetFirebaseInstanceId() {
        ServerCallsInterface serverCallsInterface = mock(ServerCallsInterface.class);
        ImageStore imageStore = mock(ImageStore.class);
        Configuration configuration = mock(Configuration.class);
        FirebaseInstanceId firebaseInstanceId = mock(FirebaseInstanceId.class);

        String testInstanceId = "thisIsATestInstanceId";

        DataProvider objectUnderTest = new DataProvider(serverCallsInterface, imageStore, configuration, firebaseInstanceId);

        doReturn(testInstanceId).when(firebaseInstanceId).getToken();

        objectUnderTest.setFirebaseInstanceId(testInstanceId);
        assertEquals(testInstanceId, objectUnderTest.getFirebaseInstanceId());
        verify(serverCallsInterface, times(1)).updateUserAsync(any(), any());
        verify(configuration, times(1)).addConfig(eq(Configuration.Type.FIREBASE_INSTANCE_ID), eq(testInstanceId));
    }

    @Test
    public void testSetCurrentUserDisplayName() {
        ServerCallsInterface serverCallsInterface = mock(ServerCallsInterface.class);
        ImageStore imageStore = mock(ImageStore.class);
        Configuration configuration = mock(Configuration.class);
        FirebaseInstanceId firebaseInstanceId = mock(FirebaseInstanceId.class);

        String testDisplayName = "thisIsATestDisplayName";
        String testInstanceId = "thisIsATestInstanceId";

        DataProvider objectUnderTest = new DataProvider(serverCallsInterface, imageStore, configuration, firebaseInstanceId);

        doReturn(testInstanceId).when(firebaseInstanceId).getToken();

        objectUnderTest.setCurrentUserDisplayName(testDisplayName, null);
        verify(serverCallsInterface, times(1)).updateUserAsync(any(), any());
        clearInvocations(serverCallsInterface);

        objectUnderTest.setCurrentUserDisplayName(testDisplayName, null);
        verifyZeroInteractions(serverCallsInterface);

        OnAsyncCallListener<User> mockListener = mock(OnAsyncCallListener.class);

        objectUnderTest.setCurrentUserDisplayName("another", mockListener);

        ArgumentCaptor<OnAsyncCallListener<User>> userCaptor = ArgumentCaptor.forClass(OnAsyncCallListener.class);
        verify(serverCallsInterface).updateUserAsync(any(), userCaptor.capture());

        userCaptor.getValue().onSuccess(null);

        verify(serverCallsInterface, times(1)).updateUserAsync(any(), any());
        verify(mockListener, times(1)).onSuccess(isNull());

        userCaptor.getValue().onFailure(null);
        verify(mockListener, times(1)).onFailure(isNull());
    }
}
