package es.upm.etsiinf.news_manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import es.upm.etsiinf.news_manager.model.Article;
import es.upm.etsiinf.news_manager.utils.SerializationUtils;
import es.upm.etsiinf.news_manager.utils.network.ModelManager;
import es.upm.etsiinf.news_manager.utils.network.exceptions.ServerCommunicationError;

public class ArticleActivity extends AppCompatActivity{

    private static final int REQUEST_CODE_OPEN_IMAGE_INTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 2;
    private Article articleToDisplay;
    private int idArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        this.idArticle = intent.getIntExtra("idArticle",0); // retrieve the article id to display
        getArticle(idArticle);

        /* ------------------ Button to take directly a picture ------------------ */
        FloatingActionButton takePictureButton = findViewById(R.id.btn_take_picture);
        if(!ModelManager.isConnected()){
            takePictureButton.setVisibility(FloatingActionButton.INVISIBLE);
        }
        takePictureButton.setOnClickListener( v ->{
            takePicture();
        });
        /* ----------------------------------------------------------------------- */

        /* ------------------ Button to upload a picture from storage ------------------ */
        FloatingActionButton uploadImageButton = findViewById(R.id.btn_add_img_file);
        if (!ModelManager.isConnected()) {
            uploadImageButton.setVisibility(FloatingActionButton.INVISIBLE);
        }
        uploadImageButton.setOnClickListener(v -> {
            uploadPictureFromInternalStorage();
        });
        /* ----------------------------------------------------------------------- */
    }

    /* Method to retrieve all the information related to a specific article from the API */
    private void getArticle(int idArticle){
        new Thread( () ->{
            try{
                this.articleToDisplay = ModelManager.getArticle(idArticle);
                Log.e("TITLE",">>>> " + this.articleToDisplay.getId());

                this.runOnUiThread( () ->{

                    ImageView imageViewArticle = findViewById(R.id.img_article);
                    TextView textViewTitle = findViewById(R.id.article_title);
                    TextView textViewSubtitle = findViewById(R.id.article_subtitle);
                    TextView textViewAbstract = findViewById(R.id.article_abstract);
                    TextView textViewBody = findViewById(R.id.article_body);
                    TextView textViewCategory = findViewById(R.id.article_category);
                    TextView textViewUserId = findViewById(R.id.article_user_id);
                    TextView textViewDate = findViewById(R.id.article_date);

                    updateImageViewArticle(imageViewArticle);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        /* Activity result after uploading a picture from internal storage */
        if( requestCode == REQUEST_CODE_OPEN_IMAGE_INTERNAL_STORAGE && resultCode == Activity.RESULT_OK){

            InputStream stream = null;
            Bitmap imageUploadedFromInternalStorage = null;

            try{
                imageUploadedFromInternalStorage = BitmapFactory.decodeStream( getContentResolver().openInputStream(data.getData()) ); // Retrieving data from result
                changeArticleImage(imageUploadedFromInternalStorage, "New article image");
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
            finally{
                if( stream != null) {
                    try{stream.close();}
                    catch (IOException e){e.printStackTrace();}
                }
            }
        }

        /* Activity result after taking a picture with the camera */
        if( requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == Activity.RESULT_OK){

            Bitmap pictureTaken = (Bitmap) data.getExtras().get("data");
            changeArticleImage(pictureTaken, "New article image");
        }
    }

    /* Method to change the image of an article */
    public void changeArticleImage(Bitmap newImage, String description){

        String imageBase64 = SerializationUtils.encodeImage(newImage); // Change format from bitmap to base64 String
        imageBase64 = imageBase64.replace("\n","").replace("\r",""); // Remove bad characters from the base64 string

        try{
            this.articleToDisplay.addImage(imageBase64, description); // Overwriting the current image with the newest.
        }
        catch (ServerCommunicationError error){
            error.printStackTrace();
        }

        /* New thread to upload the article to the API */
        new Thread( () ->{
            try{
                ModelManager.saveArticle(this.articleToDisplay);
                ImageView imageViewArticle = findViewById(R.id.img_article);
                this.runOnUiThread( () ->{
                    updateImageViewArticle(imageViewArticle);
                });
            }
            catch (ServerCommunicationError error) { error.printStackTrace(); }
        }).start();
    }

    /* Method to upload a picture from internal storage */
    public void uploadPictureFromInternalStorage(){
        Intent uploadPictureIntent = new Intent();
        uploadPictureIntent.setType("image/*");
        uploadPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        uploadPictureIntent.addCategory(uploadPictureIntent.CATEGORY_OPENABLE);
        startActivityForResult(uploadPictureIntent, REQUEST_CODE_OPEN_IMAGE_INTERNAL_STORAGE);
    }

    /* Method to directly take a picture using the camera */
    public void takePicture(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);
    }

    /* Method to update the image view of the article */
    public void updateImageViewArticle(ImageView imageViewArticle){
        try{
            if( this.articleToDisplay.getImage() != null ){
                byte[] decodedString = Base64.decode(this.articleToDisplay.getImage().getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageViewArticle.setImageBitmap(decodedByte);
            }
        }
        catch (ServerCommunicationError error){
            error.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
