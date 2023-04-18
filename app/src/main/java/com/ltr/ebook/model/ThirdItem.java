package com.ltr.ebook.model;

public class ThirdItem {
    private int mImageResId;
    private String mText;

    public ThirdItem(int imageResId, String text) {
        mImageResId = imageResId;
        mText = text;
    }

    public int getImageResId() {
        return mImageResId;
    }

    public String getText() {
        return mText;
    }
}