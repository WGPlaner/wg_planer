package de.ameyering.wgplaner.wgplaner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.swagger.client.ApiResponse;
import io.swagger.client.model.Group;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.SuccessResponse;
import io.swagger.client.model.User;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DataProviderTest {
    private static File standardImage;
    private static byte[] standardImageBytes;

    private String testCurrentUserUid = "testCurrentUserUid";
    private String testCurrentUserDisplayName = "testCurrentUserDisplayName";
    private String testCurrentUserEmail = "testCurrentUserEmail";
    private String testCurrentUserFirebaseInstanceId = "testCurrentUserFirebaseInstanceId";
    private User testCurrentUser;

    private UUID testCurrentGroupUid = new UUID(1L, 0L);
    private String testCurrentGroupDisplayName = "testCurrentGroupUid";
    private String testCurrentGroupCurrency = "EUR";
    private List<String> testCurrentGroupMembers;
    private List<String> testCurrentGroupAdmins;
    private Group testCurrentGroup;

    private List<ListItem> testCurrentShoppingList;
    private List<ListItem> testCurrentBoughtItems;

    private ServerCallsInterface mockServerCallsInterface;
    private ImageStoreInterface mockImageStoreInterface;
    private Configuration mockConfiguration;
    private FirebaseInstanceId mockFirebaseInstanceId;

    @BeforeClass
    public static void setUpClass() throws IllegalStateException {
        try {
            URL url = DataProvider.class.getClassLoader().getResource("blank_profile_picture.png");
            standardImage = new File(url.toURI());

        } catch (URISyntaxException e) {
            standardImage = null;
        }

        if (standardImage != null && standardImage.exists()) {
            FileInputStream fileInputStream = null;

            try {
                fileInputStream = new FileInputStream(standardImage);
                standardImageBytes = new byte[(int) standardImage.length()];
                fileInputStream.read(standardImageBytes);

            } catch (FileNotFoundException e) {
                standardImage = null;

            } catch (IOException e) {
                standardImageBytes = null;

            } finally {
                try {
                    fileInputStream.close();

                } catch (IOException e) {
                    Log.e("GroupPicture", ":InputStreamCloseFailed");
                }
            }

        } else {
            standardImage = null;
            standardImageBytes = null;
        }

        if (standardImage == null || standardImageBytes == null) {
            throw new IllegalStateException();
        }
    }


    @Before
    public void setUp() {
        mockServerCallsInterface = mock(ServerCallsInterface.class);
        mockImageStoreInterface = mock(ImageStoreInterface.class);
        mockConfiguration = mock(Configuration.class);
        mockFirebaseInstanceId = mock(FirebaseInstanceId.class);

        doReturn(testCurrentUserFirebaseInstanceId).when(mockFirebaseInstanceId).getToken();

        testCurrentUser = mock(User.class);
        doReturn(testCurrentUserUid).when(testCurrentUser).getUid();
        doReturn(testCurrentGroupUid).when(testCurrentUser).getGroupUID();
        doReturn(testCurrentUserDisplayName).when(testCurrentUser).getDisplayName();
        doReturn(testCurrentUserEmail).when(testCurrentUser).getEmail();
        doReturn(testCurrentUserFirebaseInstanceId).when(testCurrentUser).getFirebaseInstanceID();

        testCurrentGroupMembers = new ArrayList<>();
        testCurrentGroupMembers.add(testCurrentUserUid);

        testCurrentGroupAdmins = new ArrayList<>();
        testCurrentGroupAdmins.add(testCurrentUserUid);

        testCurrentGroup = mock(Group.class);
        doReturn(testCurrentGroupUid).when(testCurrentGroup).getUid();
        doReturn(testCurrentGroupDisplayName).when(testCurrentGroup).getDisplayName();
        doReturn(testCurrentGroupCurrency).when(testCurrentGroup).getCurrency();
        doReturn(testCurrentGroupMembers).when(testCurrentGroup).getMembers();
        doReturn(testCurrentGroupAdmins).when(testCurrentGroup).getAdmins();
    }

    @After
    public void tearDown() {
        testCurrentGroupMembers = null;
        testCurrentGroupAdmins = null;
        testCurrentUser = null;
        testCurrentGroup = null;
    }

    @Test
    public void testInstantiation() {
        DataProvider dataProvider = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );
        assertNotNull(dataProvider);
    }

    @Test
    public void testInitializationSetupCompleted() {
        DataProvider objectUnderTest = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );

        doNothing().when(mockServerCallsInterface).setCurrentUserUid(eq(testCurrentUserUid));
        doNothing().when(mockConfiguration).addConfig(eq(Configuration.Type.USER_UID),
            eq(testCurrentUserUid));

        ApiResponse<User> mockUserResponse = mock(ApiResponse.class);

        doReturn(mockUserResponse).when(mockServerCallsInterface).getUser(eq(testCurrentUserUid));

        doReturn(testCurrentUser).when(mockUserResponse).getData();

        doReturn(standardImage).when(mockImageStoreInterface).getGroupMemberPictureFile(eq(
                testCurrentUserUid));
        doReturn(standardImage).when(mockImageStoreInterface).getGroupPictureFile();

        ApiResponse<Group> mockGroupResponse = mock(ApiResponse.class);

        doReturn(mockGroupResponse).when(mockServerCallsInterface).getGroup();
        doReturn(testCurrentGroup).when(mockGroupResponse).getData();

        //Everything works as expected
        assertEquals(DataProviderInterface.SetUpState.SETUP_COMPLETED,
            objectUnderTest.initialize(testCurrentUserUid));
        verify(mockServerCallsInterface, times(1)).setCurrentUserUid(eq(testCurrentUserUid));
        verify(mockConfiguration, times(1)).addConfig(eq(Configuration.Type.USER_UID),
            eq(testCurrentUserUid));
        verify(mockServerCallsInterface, times(1)).getUser(eq(testCurrentUserUid));
        verify(mockImageStoreInterface, times(1)).getGroupMemberPictureFile(eq(testCurrentUserUid));
        verify(mockServerCallsInterface, times(1)).getGroup();
        verify(mockServerCallsInterface, times(1)).getGroupImageAsync(any());
        verify(mockServerCallsInterface, times(1)).getUserAsync(eq(testCurrentUserUid), any());

        ArgumentCaptor<OnAsyncCallListener<byte[]>> groupImageCaptor = ArgumentCaptor.forClass(
                OnAsyncCallListener.class);
        verify(mockServerCallsInterface).getGroupImageAsync(groupImageCaptor.capture());

        groupImageCaptor.getValue().onFailure(null);


        ArgumentCaptor<OnAsyncCallListener<User>> userCaptor = ArgumentCaptor.forClass(
                OnAsyncCallListener.class);
        verify(mockServerCallsInterface).getUserAsync(anyString(), userCaptor.capture());

        userCaptor.getValue().onFailure(null);
    }

    @Test
    public void testInitializationRegistered() {
        DataProvider objectUnderTest = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );

        doNothing().when(mockServerCallsInterface).setCurrentUserUid(eq(testCurrentUserUid));
        doNothing().when(mockConfiguration).addConfig(eq(Configuration.Type.USER_UID),
            eq(testCurrentUserUid));

        ApiResponse<User> mockUserResponse = mock(ApiResponse.class);

        doReturn(mockUserResponse).when(mockServerCallsInterface).getUser(eq(testCurrentUserUid));

        doReturn(testCurrentUser).when(mockUserResponse).getData();

        doReturn(standardImage).when(mockImageStoreInterface).getGroupMemberPictureFile(testCurrentUserUid);

        //user has no group
        doReturn(null).when(testCurrentUser).getGroupUID();
        assertEquals(DataProviderInterface.SetUpState.REGISTERED,
            objectUnderTest.initialize(testCurrentUserUid));
    }

    @Test
    public void testInitializationUnregistered() {
        DataProvider objectUnderTest = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );

        Context context = mock(Context.class);

        doNothing().when(mockServerCallsInterface).setCurrentUserUid(eq(testCurrentUserUid));
        doNothing().when(mockConfiguration).addConfig(eq(Configuration.Type.USER_UID),
            eq(testCurrentUserUid));

        ApiResponse<User> mockUserResponse = mock(ApiResponse.class);

        doReturn(mockUserResponse).when(mockServerCallsInterface).getUser(eq(testCurrentUserUid));

        //user is unregistered
        doReturn(null).when(mockUserResponse).getData();
        doReturn(404).when(mockUserResponse).getStatusCode();

        assertEquals(DataProviderInterface.SetUpState.UNREGISTERED,
            objectUnderTest.initialize(testCurrentUserUid));
    }

    @Test
    public void testInitializationGetUserFailed() {
        DataProvider objectUnderTest = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );

        Context context = mock(Context.class);

        doNothing().when(mockServerCallsInterface).setCurrentUserUid(eq(testCurrentUserUid));
        doNothing().when(mockConfiguration).addConfig(eq(Configuration.Type.USER_UID),
            eq(testCurrentUserUid));

        ApiResponse<User> mockUserResponse = mock(ApiResponse.class);

        doReturn(mockUserResponse).when(mockServerCallsInterface).getUser(eq(testCurrentUserUid));

        //getting the user failed
        doReturn(null).when(mockUserResponse).getData();
        doReturn(303).when(mockUserResponse).getStatusCode();

        assertEquals(DataProviderInterface.SetUpState.GET_USER_FAILED,
            objectUnderTest.initialize(testCurrentUserUid));

        //get user call failed
        doReturn(null).when(mockUserResponse).getData();
        doReturn(0).when(mockUserResponse).getStatusCode();

        assertEquals(DataProviderInterface.SetUpState.CONNECTION_FAILED,
            objectUnderTest.initialize(testCurrentUserUid));

        //get user call failed
        doReturn(null).when(mockServerCallsInterface).getUser(eq(testCurrentUserUid));

        assertEquals(DataProviderInterface.SetUpState.CONNECTION_FAILED,
            objectUnderTest.initialize(testCurrentUserUid));

        //uid is null or empty
        assertEquals(DataProviderInterface.SetUpState.GET_USER_FAILED, objectUnderTest.initialize(null));
        assertEquals(DataProviderInterface.SetUpState.GET_USER_FAILED, objectUnderTest.initialize(""));
    }

    @Test
    public void testInitializationGetGroupFailed() {
        DataProvider objectUnderTest = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );

        doNothing().when(mockServerCallsInterface).setCurrentUserUid(eq(testCurrentUserUid));
        doNothing().when(mockConfiguration).addConfig(eq(Configuration.Type.USER_UID),
            eq(testCurrentUserUid));

        ApiResponse<User> mockUserResponse = mock(ApiResponse.class);

        doReturn(mockUserResponse).when(mockServerCallsInterface).getUser(eq(testCurrentUserUid));

        doReturn(testCurrentUser).when(mockUserResponse).getData();

        doReturn(standardImage).when(mockImageStoreInterface).getGroupMemberPictureFile(eq(
                testCurrentUserUid));

        ApiResponse<Group> mockGroupResponse = mock(ApiResponse.class);

        doReturn(mockGroupResponse).when(mockServerCallsInterface).getGroup();

        //Group call failed
        doReturn(null).when(mockGroupResponse).getData();
        assertEquals(DataProviderInterface.SetUpState.GET_GROUP_FAILED,
            objectUnderTest.initialize(testCurrentUserUid));
    }

    @Test
    public void testRegisterUser() {
        DataProvider objectUnderTest = new DataProvider(mockServerCallsInterface, mockImageStoreInterface,
            mockConfiguration, mockFirebaseInstanceId);
        objectUnderTest.setCurrentUserDisplayName(testCurrentUserDisplayName, null);
        objectUnderTest.setCurrentUserEmail(testCurrentUserEmail, null);

        ArgumentCaptor<OnAsyncCallListener<User>> userCaptor = ArgumentCaptor.forClass(
                OnAsyncCallListener.class);

        objectUnderTest.registerUser(null);

        verify(mockServerCallsInterface).createUserAsync(any(), userCaptor.capture());

        userCaptor.getValue().onSuccess(testCurrentUser);
    }

    @Test
    public void testSetFirebaseInstanceId() {
        DataProvider objectUnderTest = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );

        objectUnderTest.setFirebaseInstanceId(testCurrentUserFirebaseInstanceId);
        assertEquals(testCurrentUserFirebaseInstanceId, objectUnderTest.getFirebaseInstanceId());
        verify(mockServerCallsInterface, times(1)).updateUserAsync(any(), any());
        verify(mockConfiguration, times(1)).addConfig(eq(Configuration.Type.FIREBASE_INSTANCE_ID),
            eq(testCurrentUserFirebaseInstanceId));
    }

    @Test
    public void testSetCurrentUserDisplayName() {
        DataProvider objectUnderTest = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );

        objectUnderTest.setCurrentUserDisplayName(testCurrentUserDisplayName, null);
        verify(mockServerCallsInterface, times(1)).updateUserAsync(any(), any());
        clearInvocations(mockServerCallsInterface);

        objectUnderTest.setCurrentUserDisplayName(testCurrentUserDisplayName, null);
        verifyZeroInteractions(mockServerCallsInterface);

        OnAsyncCallListener<User> mockListener = mock(OnAsyncCallListener.class);

        objectUnderTest.setCurrentUserDisplayName("another", mockListener);

        ArgumentCaptor<OnAsyncCallListener<User>> userCaptor = ArgumentCaptor.forClass(
                OnAsyncCallListener.class);
        verify(mockServerCallsInterface).updateUserAsync(any(), userCaptor.capture());

        userCaptor.getValue().onSuccess(null);

        verify(mockServerCallsInterface, times(1)).updateUserAsync(any(), any());
        verify(mockListener, times(1)).onSuccess(isNull());

        userCaptor.getValue().onFailure(null);
        verify(mockListener, times(1)).onFailure(isNull());
    }

    @Test
    public void testSetCurrentUserImage() {
        DataProvider objectUnderTest = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );

        Bitmap mockBitmap = mock(Bitmap.class);

        doReturn(standardImage).when(mockImageStoreInterface).getGroupMemberPictureFile(anyString());

        objectUnderTest.setCurrentUserImage(mockBitmap, null);

        verify(mockImageStoreInterface, times(1)).setGroupMemberPicture(anyString(), eq(mockBitmap));
        verify(mockImageStoreInterface, times(1)).getGroupMemberPictureFile(anyString());

        ArgumentCaptor<OnAsyncCallListener<SuccessResponse>> captor = ArgumentCaptor.forClass(
                OnAsyncCallListener.class);

        verify(mockServerCallsInterface).updateUserImageAsync(eq(standardImage), captor.capture());
        captor.getValue().onFailure(null);
        captor.getValue().onSuccess(null);

        objectUnderTest.setCurrentUserImage(null, null);
    }

    @Test
    public void testSetCurrentUserEmail() {
        DataProvider objectUnderTest = new DataProvider(
            mockServerCallsInterface,
            mockImageStoreInterface,
            mockConfiguration,
            mockFirebaseInstanceId
        );

        objectUnderTest.setCurrentUserEmail(testCurrentUserEmail, null);
        verify(mockServerCallsInterface, times(1)).updateUserAsync(any(), any());
        verify(mockConfiguration, times(1)).addConfig(eq(Configuration.Type.USER_EMAIL_ADDRESS),
            eq(testCurrentUserEmail));
    }
}
