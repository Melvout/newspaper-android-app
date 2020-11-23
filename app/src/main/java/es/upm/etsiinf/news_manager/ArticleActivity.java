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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import es.upm.etsiinf.news_manager.model.Article;
import es.upm.etsiinf.news_manager.utils.network.ModelManager;
import es.upm.etsiinf.news_manager.utils.network.exceptions.ServerCommunicationError;

public class ArticleActivity extends AppCompatActivity{

    private static final int REQUEST_CODE_OPEN_IMAGE = 1;
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
        this.idArticle = intent.getIntExtra("idArticle",0);
        getArticle(idArticle);


        FloatingActionButton addImageButton = findViewById(R.id.btn_add_img);
        if(!ModelManager.isConnected()){
            addImageButton.setVisibility(FloatingActionButton.INVISIBLE);
        }

        addImageButton.setOnClickListener( v ->{

            takePicture();
        });

        FloatingActionButton uploadImageButton = findViewById(R.id.btn_add_img_file);
        if (!ModelManager.isConnected()) {
            uploadImageButton.setVisibility(FloatingActionButton.INVISIBLE);
        }

        uploadImageButton.setOnClickListener(v -> {
            uploadPictureFromInternalStorage();
        });

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == REQUEST_CODE_OPEN_IMAGE && resultCode == Activity.RESULT_OK) {
            InputStream stream = null;
            try {
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                //ImageView imageView = findViewById(R.id.img_article);
                //imageView.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                this.articleToDisplay.addImage(imageBase64,"Article image");
                new Thread( () ->{
                    try {
                        ModelManager.saveArticle(this.articleToDisplay);
                        getArticle(idArticle);
                    } catch (ServerCommunicationError error) {
                        error.printStackTrace();
                    }
                }).start();


            }
            catch (FileNotFoundException | ServerCommunicationError e) {
                e.printStackTrace();
            }
            finally {
                if( stream != null) {
                    try{stream.close();}
                    catch (IOException e){e.printStackTrace();}
                }
            }
        }

        if( requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == Activity.RESULT_OK){
            Bitmap pictureTaken = (Bitmap) data.getExtras().get("data");
            ((ImageView)findViewById(R.id.img_article)).setImageBitmap(pictureTaken);
        }
    }

    public void uploadPictureFromInternalStorage(){
        Intent uploadPictureIntent = new Intent();
        uploadPictureIntent.setType("image/*");
        uploadPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        uploadPictureIntent.addCategory(uploadPictureIntent.CATEGORY_OPENABLE);
        startActivityForResult(uploadPictureIntent, REQUEST_CODE_OPEN_IMAGE);
    }

    public void takePicture(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);
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
