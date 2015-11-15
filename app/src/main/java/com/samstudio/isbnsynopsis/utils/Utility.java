package com.samstudio.isbnsynopsis.utils;

import com.samstudio.isbnsynopsis.models.Book;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by satryaway on 11/15/2015.
 */
public class Utility {

    public static Book parseBook(JSONObject jsonObject) {
        Book book = new Book();
        try {
            book.setId(jsonObject.getString(CommonConstants.ID));
            book.setKode(jsonObject.getString(CommonConstants.KODE));
            book.setJudul(jsonObject.getString(CommonConstants.JUDUL));
            book.setPenulis(jsonObject.getString(CommonConstants.PENULIS));
            book.setIsbn(jsonObject.getString(CommonConstants.ISBN));
            book.setSinopsis(jsonObject.getString(CommonConstants.SINOPSIS));
            book.setCover(jsonObject.getString(CommonConstants.COVER));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return book;
    }
}
