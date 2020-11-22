package es.upm.etsiinf.news_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import es.upm.etsiinf.news_manager.utils.network.ModelManager;
import es.upm.etsiinf.news_manager.utils.network.exceptions.AuthenticationError;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        EditText usernameInput = findViewById(R.id.textField_name);
        EditText passwordInput = findViewById(R.id.textField_password);

        Button viewArticleButton = findViewById(R.id.btn_login);
        viewArticleButton.setOnClickListener(v -> {
            //TODO

            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            login(username, password);
        });


    }

    /* Function to login */
    private void login(String username, String password) {
        new Thread ( () ->{
            try {
                ModelManager.login(username, password);

                Intent i_nextActivity = new Intent(this, MainActivity.class);
                this.startActivity(i_nextActivity);
            }
            catch (AuthenticationError e){
                // TODO : display error message
                e.printStackTrace();
            }
            ModelManager.stayloggedin(ModelManager.getLoggedAuthType(), ModelManager.getLoggedApiKey(), ModelManager.getLoggedIdUSer());
        }).start();
    }
}
