package es.upm.etsiinf.news_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import es.upm.etsiinf.news_manager.model.Article;

public class ArticleAdapter extends BaseAdapter {

    private List<Article> articleData = new LinkedList<>();
    private Context context;

    public ArticleAdapter(Context context) {
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

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_article, null);

        TextView tvCardTitle = view.findViewById(R.id.card_title);
        tvCardTitle.setText(articleData.get(i).getTitleText());

        TextView tvCardAbstract = view.findViewById(R.id.card_abstract);
        tvCardAbstract.setText(articleData.get(i).getAbstractText());

        TextView tvCardCategory = view.findViewById(R.id.card_category);
        tvCardCategory.setText(articleData.get(i).getCategory());

        return view;

    }
}
