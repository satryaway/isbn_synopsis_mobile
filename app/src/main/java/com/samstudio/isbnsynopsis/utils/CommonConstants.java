package com.samstudio.isbnsynopsis.utils;

/**
 * Created by satryaway on 11/15/2015.
 */
public class CommonConstants {
    public static String CONTENT_CODE = "content_code";
    public static String COVER_IMAGE = "cover_image";
    public static String BASE_URL = "http://192.168.43.84/isbn/";
    public static String SERVICE_POST_BOOK = BASE_URL + "buku";
    public static String SERVICE_GET_BOOK_BY_CODE = BASE_URL + "buku/kode/";
    public static String SERVICE_GET_BOOK_BY_ID = BASE_URL + "buku/id/";
    public static Object SERVICE_GET_BOOK = BASE_URL + "buku/";
    public static String SERVICE_GET_BOOK_BY_JUDUL = BASE_URL + "buku/judul/";
    public static String SERVICE_GET_BOOK_BY_ISBN = BASE_URL + "buku/isbn/";
    public static String SERVICE_GET_COVER_IMAGE = BASE_URL + "assets/images/";
    public static String KODE = "kode";
    public static String JUDUL = "judul";
    public static String PENULIS = "penulis";
    public static String ISBN = "isbn";
    public static String SINOPSIS = "sinopsis";
    public static String COVER = "cover";
    public static String STATUS = "status";
    public static int RESULT_OK = 1;
    public static String MESSAGE = "message";
    public static String ID = "id";
    public static String RETURN_DATA = "return_data";
    public static int SCAN_BOOK_CODE = 27;
    public static int INPUT_BOOK_CODE = 8;
    public static String IS_PASSING_ID = "is_passing_id";
    public static String SERVICE_LOGIN = BASE_URL + "login";
    public static String EMAIL = "email";
    public static String PASSWORD = "password";
    public static String ISBN_APP = "isbn_app";
    public static String IS_LOGGED_IN = "is_logged_in";
    public static String NAME = "name";
}
