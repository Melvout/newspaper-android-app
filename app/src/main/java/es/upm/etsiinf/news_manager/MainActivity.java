package es.upm.etsiinf.news_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initProperties();
        login("us_1_3", "1331");

        downloadArticles();
    }

    private void downloadArticles(){
        new Thread( () ->{
            try{
                this.articleList = ModelManager.getArticles();
                Log.e("Articles","Articles retrieved");
                this.articleList.forEach( article ->{
                    try{
                        if( article.getImage() != null){
                            Log.e("Image",">>>" + article.getImage());
                        }
                        else{
                            Log.e("Image","NO IMAGE");
                        }
                    }
                    catch (ServerCommunicationError serverCommunicationError){
                        serverCommunicationError.printStackTrace();
                    }
                });
            }
            catch (ServerCommunicationError e){
                e.printStackTrace();
            }
        }).start();
    }

    private void login(String username, String password){
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

    private void initProperties(){
        Properties RESTProperties = new Properties();
        RESTProperties.setProperty(RESTConnection.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pmd-task/");
        RESTProperties.setProperty(RESTConnection.ATTR_REQUIRE_SELF_CERT, "TRUE");
        ModelManager.configureConnection(RESTProperties);
    }
}