package es.upm.etsiinf.news_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
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

    private String authtype;
    private String apikey;
    private String idUSer;
    private List<String> dataList = new ArrayList<>();
    private String[] data;

    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initProperties();
        login("us_1_3", "1331");
        downloadArticles();

        /* It will be used to navigate to the ArticleActivity - it's just a test */
        Button btn_nextActivity = findViewById(R.id.btn_testButton);
        btn_nextActivity.setOnClickListener(v ->
        {
            viewArticle(0);
        });

        ArrayList<Article> articleArrayList = new ArrayList<Article>();
        ArticleAdapter adapter = new ArticleAdapter(context);
        ListView listView = context.findViewById(R.id.lst_itemList);

        adapter.addArticles(articleArrayList);
        listView.setAdapter(adapter);

    }

    private void viewArticle(int articleId) {

        Intent i_nextActivity = new Intent(getApplicationContext(), ArticleActivity.class);
        i_nextActivity.putExtra("idArticle", 113); // 113 should be replaced later by : this.articleList.get(articleId).getId()
        MainActivity.this.startActivity(i_nextActivity);
    }

    /* Function to retrieve articles from the API */
    private void downloadArticles() {
        new Thread( () -> {
            try{
                this.articleList = ModelManager.getArticles();
                Log.e("Articles","Articles retrieved");
            }
            catch (ServerCommunicationError e){
                e.printStackTrace();
            }

        }).start();
    }

    /* Function to login */
    private void login(String username, String password) {
        new Thread ( () ->{
            try {
                ModelManager.login(username, password);
            }
            catch (AuthenticationError e){
                e.printStackTrace();
            }
            this.authtype = ModelManager.getLoggedAuthType();
            this.apikey = ModelManager.getLoggedApiKey();
            this.idUSer = ModelManager.getLoggedIdUSer();

            ModelManager.stayloggedin(this.idUSer,this.apikey,this.authtype);
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