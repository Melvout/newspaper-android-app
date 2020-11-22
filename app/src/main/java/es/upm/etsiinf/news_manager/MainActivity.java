package es.upm.etsiinf.news_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import es.upm.etsiinf.news_manager.model.Article;
import es.upm.etsiinf.news_manager.utils.Logger;
import es.upm.etsiinf.news_manager.utils.network.ModelManager;
import es.upm.etsiinf.news_manager.utils.network.RESTConnection;
import es.upm.etsiinf.news_manager.utils.network.exceptions.AuthenticationError;
import es.upm.etsiinf.news_manager.utils.network.exceptions.ServerCommunicationError;

public class MainActivity extends AppCompatActivity
{
    private List<Article> articleList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* If the RESTConnection is already configured, skip it */
        if(!ModelManager.isConfigured()){
            initProperties();
        }

        /* Button to go to the login activity */
        ((Button)findViewById(R.id.btn_goToLogin)).setOnClickListener(v -> {
            Intent goToLogin = new Intent(this, LoginActivity.class);
            this.startActivity(goToLogin);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        downloadArticles();
    }

    /* Function to retrieve articles from the API */
    private void downloadArticles() {
        new Thread( () -> {
            try{
                this.articleList = ModelManager.getArticles();
            }
            catch (ServerCommunicationError e){
                e.printStackTrace();
            }
            this.runOnUiThread( () -> {
                ListView listView = this.findViewById(R.id.lst_itemList);
                ArticleAdapter articleAdapter = new ArticleAdapter(this);
                articleAdapter.addArticles(this.articleList);
                listView.setAdapter(articleAdapter);
            });
        }).start();
    }

    /* Function to init properties of the REST app */
    private void initProperties() {
        Properties RESTProperties = new Properties();
        RESTProperties.setProperty(RESTConnection.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pmd-task/");
        RESTProperties.setProperty(RESTConnection.ATTR_REQUIRE_SELF_CERT, "TRUE");
        ModelManager.configureConnection(RESTProperties);
    }

}