invite-friends
==============

Invite friends dialog for Android using Facebook SDK invite dialog

Introduction
-----------

Provides an activity with the UI of a dialog to share a Google Play url with:
- Facebook: Facebook SDK invite dialog with your Facebook app
- WhatsApp: direct link to the conversation list
- Other: the standard share intent

Usage
-----------

- Create a new Facebook application, and link to your Android application
- Import the projects to your workspace, and import `invite-friend-lib` as a library project
- Add this to your manifest:
```
        <activity
            android:name="com.androidsx.core.invitefriends.InviteFriendsActivity"
            android:excludeFromRecents="true"
            android:theme="@style/MyAppCompatDialogTheme" >
        </activity>
        <activity
            android:name="com.facebook.LoginActivity"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />

        <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_app_id" />
```
- Override these resources in your strings.xml:
```
        <string name="flurry_key"></string>
        <string name="facebook_app_id"></string>
        <string name="facebook_app_namespace"></string>   
        <string name="invite_friends_market_url"></string>
        <string name="invite_friends_share_message"></string>
```
- Start the activity:
```
          final Intent intent = new Intent(context, InviteFriendsActivity.class);
          startActivity(intent);      
```

Screenshots
-----------

![Dialog example](http://www.androidsx.com/projects/android/invite-friends-dialog.png)

![Dialog facebook screen](http://www.androidsx.com/projects/android/invite-friends-dialog-facebook.png)

TODO
-----------
- Move to gradle and package it as a AAR
- Remove flurry dependency
