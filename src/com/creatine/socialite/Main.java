package com.creatine.socialite;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.Toast;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;

public class Main extends Activity {

    Facebook facebook = new Facebook("223686664426439");

    private int mAuthAttempts = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Authorizes the user's FB account
        facebook.authorize(this, new String[] { "email", "publish_stream", "offline_access" },
        		
        	new DialogListener() {
            
            public void onComplete(Bundle values) {}
            
            public void onFacebookError(FacebookError error) {}
            
            public void onError(DialogError e) {}
            
            public void onCancel() {}
        });
    }
    
    // Extends access_token if needed
    public void onResume() {    
        super.onResume();
        facebook.extendAccessTokenIfNeeded(this, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.menu_post_status) {
	    	Intent post = new Intent(this, StatusEntry.class);
	    	post.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(post);
	        return true;
	    }
		return false;
	}		        
    
    public void updateStatus(String accessToken, String status) {
		try {         
	        Bundle bundle = new Bundle();
	        bundle.putString("status", status);         
	        bundle.putString(Facebook.TOKEN,accessToken);         
	        String response = facebook.request("me/feed",bundle,"POST");         
	        Log.d("UPDATE RESPONSE",""+response);
	        showToast("Update process complete. Respose:"+response);
	        if(response.indexOf("OAuthException") > -1){
	            if(mAuthAttempts==0){
	                mAuthAttempts++;
	                facebook.authorize(this, new String[] { "email", "publish_stream", "offline_access" },
	                		
	                    	new DialogListener() {
	                        
	                        public void onComplete(Bundle values) {}
	                        
	                        public void onFacebookError(FacebookError error) {}
	                        
	                        public void onError(DialogError e) {}
	                        
	                        public void onCancel() {}
	                    });
	                updateStatus(facebook.getAccessToken(), status);
	            }else{
	                showToast("OAuthException:");
	            }
	        }
	    } catch (MalformedURLException e) {         
	        Log.e("MALFORMED URL",""+e.getMessage());
	        showToast("MalformedURLException:"+e.getMessage());
	    } catch (IOException e) {         
	        Log.e("IOEX",""+e.getMessage());
	        showToast("IOException:"+e.getMessage());
	    }

	    String s = facebook.getAccessToken()+"\n";
	    s += String.valueOf(facebook.getAccessExpires())+"\n";
	    s += "Now:"+String.valueOf(System.currentTimeMillis())+"\n";
	}
    private void showToast(String message){
	    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
}
