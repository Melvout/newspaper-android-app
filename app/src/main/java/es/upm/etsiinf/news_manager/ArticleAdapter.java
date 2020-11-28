package es.upm.etsiinf.news_manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.LinkedList;
import java.util.List;

import es.upm.etsiinf.news_manager.model.Article;
import es.upm.etsiinf.news_manager.utils.SerializationUtils;
import es.upm.etsiinf.news_manager.utils.network.ModelManager;
import es.upm.etsiinf.news_manager.utils.network.exceptions.ServerCommunicationError;

public class ArticleAdapter extends BaseAdapter {

    private List<Article> articleData = new LinkedList<>();
    private Activity context;

    public ArticleAdapter(Activity context) {
        this.context = context;
    }

    public void addArticles(List<Article> articleList) {
        articleData.addAll(articleList);
    }

    @Override
    public int getCount() {
        return articleData.size();
    }

    @Override
    public Object getItem(int i) {
        return articleData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        Article article = articleData.get(i);
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.card_article, null);
        }

        TextView tvCardTitle = convertView.findViewById(R.id.card_title);
        tvCardTitle.setText(article.getTitleText());

        TextView tvCardAbstract = convertView.findViewById(R.id.card_abstract);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            tvCardAbstract.setText( Html.fromHtml(article.getAbstractText(), Html.FROM_HTML_MODE_COMPACT) );
        }
        else{
            tvCardAbstract.setText(article.getAbstractText());
        }

        TextView tvCardCategory = convertView.findViewById(R.id.card_category);
        tvCardCategory.setText(article.getCategory());

        ImageView tvCardImage = convertView.findViewById(R.id.card_thumbnail);
        Bitmap bitmapArticle = null;
        try{
            if( article.getImage() != null){
                bitmapArticle = SerializationUtils.base64StringToImg(article.getImage().getImage());
                tvCardImage.setImageBitmap(bitmapArticle);
            }
        }
        catch (ServerCommunicationError error){
            error.printStackTrace();
        }

        CardView viewArticle = convertView.findViewById(R.id.card_article);
        viewArticle.setOnClickListener(v -> viewArticle(article.getId()));

        return convertView;
    }

    private void viewArticle(int articleId){

        Intent i_nextActivity = new Intent(context, ArticleActivity.class);
        i_nextActivity.putExtra("idArticle", articleId); // 113 should be replaced later by : this.articleList.get(articleId).getId()
        context.startActivity(i_nextActivity);
    }

}
