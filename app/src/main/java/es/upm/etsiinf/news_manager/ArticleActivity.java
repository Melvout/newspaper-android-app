package es.upm.etsiinf.news_manager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import es.upm.etsiinf.news_manager.model.Article;
import es.upm.etsiinf.news_manager.utils.network.ModelManager;
import es.upm.etsiinf.news_manager.utils.network.exceptions.ServerCommunicationError;

public class ArticleActivity extends AppCompatActivity{

    private Article articleToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        int idArticle = intent.getIntExtra("idArticle",0);
        getArticle(idArticle);


    }

    /* Method to retrieve all the information related to a specific article from the API */
    private void getArticle(int idArticle){
        new Thread( () ->{
            try{
                this.articleToDisplay = ModelManager.getArticle(idArticle);
                Log.e("TITLE",">>>> " + this.articleToDisplay.getTitleText());
            }
            catch (ServerCommunicationError error){
                error.printStackTrace();
            }
        }).start();
    }
}
