package es.upm.etsiinf.news_manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

                    ImageView imageViewArticle = findViewById(R.id.img_article);
                    TextView textViewTitle = findViewById(R.id.article_title);
                    TextView textViewSubtitle = findViewById(R.id.article_subtitle);
                    TextView textViewAbstract = findViewById(R.id.article_abstract);
                    TextView textViewBody = findViewById(R.id.article_body);
                    TextView textViewCategory = findViewById(R.id.article_category);
                    TextView textViewUserId = findViewById(R.id.article_user_id);
                    TextView textViewDate = findViewById(R.id.article_date);

                    try {
                        if( this.articleToDisplay.getImage() != null ){
                            byte[] decodedString = Base64.decode(this.articleToDisplay.getImage().getImage(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageViewArticle.setImageBitmap(decodedByte);
                        }
                    } catch (ServerCommunicationError error) {
                        error.printStackTrace();
                    }

                    textViewTitle.setText(this.articleToDisplay.getTitleText());
                    textViewSubtitle.setText(this.articleToDisplay.getSubtitleText());

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        textViewAbstract.setText( Html.fromHtml(this.articleToDisplay.getAbstractText(), Html.FROM_HTML_MODE_COMPACT) );
                        textViewBody.setText( Html.fromHtml(this.articleToDisplay.getBodyText(), Html.FROM_HTML_MODE_COMPACT) );
                    }
                    else{
                        textViewAbstract.setText(this.articleToDisplay.getAbstractText());
                        textViewBody.setText(this.articleToDisplay.getBodyText());
                    }

                    textViewCategory.setText(this.articleToDisplay.getCategory());
                    textViewUserId.setText("Author : " + Integer.toString(this.articleToDisplay.getIdUser()));
                    textViewDate.setText(this.articleToDisplay.getLastUpdate().toString());

                });
            }
            catch (ServerCommunicationError error){
                error.printStackTrace();
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
