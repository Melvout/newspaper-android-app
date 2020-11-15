package es.upm.etsiinf.news_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

import es.upm.etsiinf.news_manager.model.Article;
import es.upm.etsiinf.news_manager.utils.Logger;
import es.upm.etsiinf.news_manager.utils.network.ModelManager;
import es.upm.etsiinf.news_manager.utils.network.exceptions.ServerCommunicationError;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Article> articleList = null;


        try
        {
            articleList = ModelManager.getArticles();
        }
        catch (ServerCommunicationError e)
        {
            Logger.log(Logger.ERROR, "An error occurred :" + e.getMessage());
        }

        System.out.println(articleList.toString());

    }
}