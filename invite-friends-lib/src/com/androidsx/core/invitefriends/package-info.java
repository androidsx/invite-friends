/**
 * Provides an activity with the UI of a dialog to share the Google Play url with:
 *  - Facebook: Facebook SDK invite dialog with your Facebook app
 *  - WhatsApp: direct link to the conversation list
 *  - Other: the standard share intent
 *  
 * Usage:
 * 
 *  1) Create a new Facebook application, and link to your Android application
 *  
 *  2) Override these resources in your strings.xml:
 *       <string name="flurry_key"></string>
 *       <string name="facebook_app_id"></string>
 *       <string name="facebook_app_namespace"></string>   
 *       <string name="invite_friends_market_url"></string>
 *       <string name="invite_friends_share_message"></string>
 *       
 *  3) Start the activity:
 *       final Intent intent = new Intent(context, InviteFriendsActivity.class);
 *       startActivity(intent);      
 *    
 */
package com.androidsx.core.invitefriends;
