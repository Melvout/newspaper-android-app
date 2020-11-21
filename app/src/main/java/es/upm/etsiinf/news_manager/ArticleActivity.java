package es.upm.etsiinf.news_manager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

                this.runOnUiThread( () ->{

                    TextView textViewTitle = findViewById(R.id.article_title);
                    TextView textViewSubtitle = findViewById(R.id.article_subtitle);
                    TextView textViewAbstract = findViewById(R.id.article_abstract);
                    TextView textViewBody = findViewById(R.id.article_body);
                    TextView textViewCategory = findViewById(R.id.article_category);
                    TextView textViewUserId = findViewById(R.id.article_user_id);
                    TextView textViewDate = findViewById(R.id.article_date);


                    textViewTitle.setText(this.articleToDisplay.getTitleText());
                    textViewSubtitle.setText(this.articleToDisplay.getSubtitleText());

                    textViewAbstract.setText(this.articleToDisplay.getAbstractText());
                    textViewBody.setText(this.articleToDisplay.getBodyText());
                    textViewCategory.setText(this.articleToDisplay.getCategory());
                    textViewUserId.setText(Integer.toString(this.articleToDisplay.getIdUser()));
                    //textViewDate.setText(this.articleToDisplay.get);

                });
            }
            catch (ServerCommunicationError error){
                error.printStackTrace();
            }
        }).start();
    }
}
