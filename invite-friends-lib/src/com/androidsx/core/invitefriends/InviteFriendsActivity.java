package com.androidsx.core.invitefriends;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.listeners.OnInviteListener;
import com.sromku.simple.fb.listeners.OnLoginListener;

/**
 * Invites friends to use your application with Facebook, WhatsApp or the standard share intent.
 */
public class InviteFriendsActivity extends Activity {

    private static final String TAG = InviteFriendsActivity.class.getCanonicalName();

    private SimpleFacebook simpleFacebook;

    private OnTouchListener shareButtonTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                ((ImageButton) arg0).getDrawable().setColorFilter(Color.argb(150, 155, 155, 155),
                        PorterDuff.Mode.DST_IN);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                ((ImageButton) arg0).getDrawable().clearColorFilter();
                ((ImageButton) arg0).invalidate();
                break;
            }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_invite_friends);

        configureFacebook();

        findViewById(R.id.invite_friends_close).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent(Tracking.Events.INVITE,
                        Tracking.build(Tracking.Properties.METHOD, Tracking.Values.NONE,
                                Tracking.Properties.STATUS, Tracking.Values.OK));

                finish();
            }
        });

        ImageButton imageButtonFacebook = (ImageButton) findViewById(R.id.invite_friends_facebook);
        imageButtonFacebook.setOnTouchListener(shareButtonTouchListener);

        ImageButton imageButtonWhatsApp = (ImageButton) findViewById(R.id.invite_friends_whatsapp);
        imageButtonWhatsApp.setOnTouchListener(shareButtonTouchListener);

        ImageButton imageButtonOther = (ImageButton) findViewById(R.id.invite_friends_other);
        imageButtonOther.setOnTouchListener(shareButtonTouchListener);
        
        FlurryAgent.logEvent(Tracking.Events.INVITE_SHOWN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_key));
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        simpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onWhatsAppInvite(View v) {
        Toast.makeText(this, R.string.invite_friends_toast_after_share, Toast.LENGTH_LONG).show();

        final String shareBody = getResources().getString(R.string.invite_friends_market_url);
        try {
            Intent shareToWhatsApp = new Intent(Intent.ACTION_SEND);
            shareToWhatsApp.setType("text/plain");

            shareToWhatsApp.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

            shareToWhatsApp.setClassName("com.whatsapp", "com.whatsapp.ContactPicker");
            startActivity(shareToWhatsApp);
        } catch (Exception e) {
            Intent shareGeneric = new Intent(Intent.ACTION_SEND);
            shareGeneric.setType("text/plain");
            shareGeneric.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

            startActivity(Intent.createChooser(shareGeneric, getResources().getString(R.string.invite_friends_share_chooser)));
        }

        FlurryAgent.logEvent(Tracking.Events.INVITE,
                Tracking.build(Tracking.Properties.METHOD, Tracking.Values.WHATSAPP,
                        Tracking.Properties.STATUS, Tracking.Values.OK));
    }

    public void onOtherInvite(View v) {
        final String marketUrl = getResources().getString(R.string.invite_friends_market_url);
        final String shareBody = String.format(getResources().getString(R.string.invite_friends_share_other_message), marketUrl);
        
        Intent shareGeneric = new Intent(Intent.ACTION_SEND);
        shareGeneric.setType("text/plain");
        shareGeneric.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(shareGeneric, "Share"));

        FlurryAgent.logEvent(Tracking.Events.INVITE,
                Tracking.build(Tracking.Properties.METHOD, Tracking.Values.OTHER,
                        Tracking.Properties.STATUS, Tracking.Values.OK));
    }

    public void onFacebookInvite(View v) {
        if (!simpleFacebook.isLogin()) {
            OnLoginListener onLoginListener = new OnLoginListener() {
                @Override
                public void onLogin() {
                    Log.v(TAG, "Logged in with facebook succesfully");

                    inviteFriends();
                }

                @Override
                public void onNotAcceptingPermissions(Permission.Type type) {
                    Log.w(TAG, String.format("You didn't accept %s permissions", type.name()));
                    
                    FlurryAgent.logEvent(Tracking.Events.INVITE,
                            Tracking.build(Tracking.Properties.METHOD, Tracking.Values.FACEBOOK,
                                    Tracking.Properties.STATUS, Tracking.Values.ERROR,
                                    Tracking.Properties.MESSAGE, "You didn't accept permissions: " + type.name()));
                }

                @Override
                public void onThinking() {
                }

                @Override
                public void onException(Throwable throwable) {
                    Log.w(TAG, "Exception thrown while accepting permissions", throwable);
                }

                @Override
                public void onFail(String reason) {
                    Log.e(TAG, "Error during login: " + reason);
                    
                    FlurryAgent.logEvent(Tracking.Events.INVITE,
                            Tracking.build(Tracking.Properties.METHOD, Tracking.Values.FACEBOOK,
                                    Tracking.Properties.STATUS, Tracking.Values.ERROR,
                                    Tracking.Properties.MESSAGE, "During login: " + reason));
                }
            };

            simpleFacebook.login(onLoginListener);
        } else {
            inviteFriends();
        }
    }

    private void inviteFriends() {
        OnInviteListener onInviteListener = new OnInviteListener() {
            @Override
            public void onComplete(List<String> invitedFriends, String requestId) {
                Log.i(TAG, "Invitation was sent to " + invitedFriends.size() + " users with request id " + requestId);

                FlurryAgent.logEvent(Tracking.Events.INVITE,
                        Tracking.build(Tracking.Properties.METHOD, Tracking.Values.FACEBOOK,
                                Tracking.Properties.STATUS, Tracking.Values.OK,
                                Tracking.Properties.FRIENDS_INVITED, String.valueOf(invitedFriends.size())));
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Canceled the dialog");
            }

            @Override
            public void onException(Throwable throwable) {
                Log.w(TAG, "Exception thrown while inviting friends", throwable);
            }

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "Failed: " + reason);
                
                FlurryAgent.logEvent(Tracking.Events.INVITE,
                        Tracking.build(Tracking.Properties.METHOD, Tracking.Values.FACEBOOK,
                                Tracking.Properties.STATUS, Tracking.Values.ERROR,
                                Tracking.Properties.MESSAGE, "During inviting: " + reason));
            }
        };
        
        simpleFacebook.invite(getResources().getString(R.string.invite_friends_facebook_dialog), onInviteListener, null);
    }
    
    private void configureFacebook() {
        Permission[] permissions = new Permission[] {};
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getResources().getString(R.string.facebook_app_id))
                .setNamespace(getResources().getString(R.string.facebook_app_namespace))
                .setPermissions(permissions)
                .build();

        SimpleFacebook.setConfiguration(configuration);
    }
}
