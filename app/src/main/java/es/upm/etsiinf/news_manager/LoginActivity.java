package es.upm.etsiinf.news_manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import es.upm.etsiinf.news_manager.utils.network.ModelManager;
import es.upm.etsiinf.news_manager.utils.network.exceptions.AuthenticationError;

public class LoginActivity extends AppCompatActivity{

    private static final String REMEMBER_PREF = "rememberPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        EditText usernameInput = findViewById(R.id.textField_name);
        EditText passwordInput = findViewById(R.id.textField_password);

        /* Button to submit the login form */
        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            login(username, password);
        });
    }

    /* Function to login */
    private void login(String username, String password) {
        new Thread ( () ->{
            try {
                CheckBox rememberMeCheckbox = findViewById(R.id.checkbox_remember_me);
                ModelManager.login(username, password);
                if( rememberMeCheckbox.isChecked()){
                    //TODO : save username and password in a file
                    SharedPreferences rememberPreferences = getApplicationContext().getSharedPreferences(REMEMBER_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor rememberPreferencesEditor = rememberPreferences.edit();
                    rememberPreferencesEditor.putString("username", ModelManager.getIdUser());
                    rememberPreferencesEditor.putString("apiKey", ModelManager.getLoggedApiKey());
                    Log.e("APIKEY", ModelManager.getLoggedApiKey());
                    rememberPreferencesEditor.putString("authType", ModelManager.getLoggedAuthType());
                    rememberPreferencesEditor.commit();
                }
                Intent i_nextActivity = new Intent(this, MainActivity.class);
                this.startActivity(i_nextActivity);
            }
            catch (AuthenticationError e){
                // TODO : display error message
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
