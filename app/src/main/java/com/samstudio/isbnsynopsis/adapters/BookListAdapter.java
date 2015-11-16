package com.samstudio.isbnsynopsis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samstudio.isbnsynopsis.R;
import com.samstudio.isbnsynopsis.models.Book;
import com.samstudio.isbnsynopsis.utils.CommonConstants;
import com.samstudio.isbnsynopsis.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satryaway on 11/16/2015.
 */
public class BookListAdapter extends BaseAdapter {
    private Context context;
    private UniversalImageLoader imageLoader;
    private List<Book> bookList = new ArrayList<>();

    public BookListAdapter(Context context) {
        this.context = context;
        imageLoader = new UniversalImageLoader(context);
        imageLoader.initImageLoader();
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.book_lv_item_layout, parent, false);
            holder.judulTV = (TextView) convertView.findViewById(R.id.judul_tv);
            holder.penulisTV = (TextView) convertView.findViewById(R.id.penulis_tv);
            holder.sinopsisTV = (TextView) convertView.findViewById(R.id.sinopsis_tv);
            holder.coverIV = (ImageView) convertView.findViewById(R.id.cover_iv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.judulTV.setText(bookList.get(position).getJudul());
        holder.penulisTV.setText(bookList.get(position).getPenulis());
        holder.sinopsisTV.setText(bookList.get(position).getSinopsis());
        imageLoader.display(holder.coverIV, CommonConstants.SERVICE_GET_COVER_IMAGE + bookList.get(position).getCover());

        return convertView;
    }

    public void updateContent(List<Book> bookList) {
        this.bookList = bookList;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView judulTV;
        TextView penulisTV;
        TextView sinopsisTV;
        ImageView coverIV;
    }
}
