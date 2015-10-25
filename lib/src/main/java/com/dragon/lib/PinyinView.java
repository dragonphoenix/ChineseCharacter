/*
 *  Copyright (C) 2012 Dragon
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.dragon.lib;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.dragon.lib.utils.CharacterUtil;

import java.util.List;
import java.util.Map;

/**
 * TODO: document your custom view class.
 */
public class PinyinView extends View {

    private float mWidth;
    private float mPinyinWidth;
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPinyinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mDefaultPadding = getResources().getDimension(R.dimen.pinyin_default_padding);
    private String mText;
    private float mTextSize;
    private int mEachLineCharacterNum;
    private boolean mShowPinyin = false;
    private Map<String, List<String>> mCharacters;

    public PinyinView(Context context) {
        this(context, null);
    }

    public PinyinView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinyinView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            return;
        }

        Resources res = getResources();
        float defaultWidth = res.getDimension(R.dimen.pinyin_width);
        float defaultTextSize = res.getDimension(R.dimen.pinyin_text_size);
        float scale = defaultTextSize / defaultWidth;
        mCharacters = CharacterUtil.getInstance().getCharacters(res);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PinyinView);
        mText = a.getString(R.styleable.PinyinView_text);
        mWidth = a.getDimension(R.styleable.PinyinView_width, defaultWidth);
        mPinyinWidth = mWidth * 0.46f;
        mTextSize = scale * mWidth;
        mShowPinyin = a.getBoolean(R.styleable.PinyinView_showPinyin, true);
        a.recycle();

        mLinePaint.setColor(res.getColor(R.color.pinyin_text));
        mLinePaint.setStrokeWidth(10f);
        mTextPaint.setColor(Color.BLACK);
        mEachLineCharacterNum = (int) ((getScreenWidth() - mDefaultPadding - getPaddingLeft() - getPaddingLeft()) / mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawGrids(canvas);

        drawCharacters(canvas);

    }

    public int getScreenWidth(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    private void drawCharacters(Canvas canvas) {
        float paddingTop = getPaddingTop();
        float paddingLeft = getPaddingLeft();
        String character;

        for (int i = 0; i < mText.length(); i ++) {
            character = mText.charAt(i) + "";
            if (mShowPinyin == true) {
                drawPinyin(canvas, paddingLeft, paddingTop, character);
            }

            drawCharacter(canvas, paddingLeft, paddingTop, character);
            paddingLeft += mWidth;
            if ((i +1) % mEachLineCharacterNum == 0){
                paddingTop += mWidth;
                if (mShowPinyin == true){
                    paddingTop += mPinyinWidth;
                }
                paddingLeft = getPaddingLeft();
            }
        }
    }

    private void drawPinyin(Canvas canvas, float paddingLeft, float paddingTop, String text){
        float height,  width;
        float startX, startY;
        List<String> pinyinList = mCharacters.get(text);
        if (pinyinList == null){
            text = "";
        } else {
            text = pinyinList.get(0);
        }
        mPinyinPaint.setColor(Color.MAGENTA);
        mPinyinPaint.setTextSize(mTextSize * 0.35f);
        Paint.FontMetrics fontMetrics = mPinyinPaint.getFontMetrics();
        height = fontMetrics.descent - fontMetrics.ascent;
        width = mPinyinPaint.measureText(text);
        startX = paddingLeft + (mDefaultPadding + mWidth - width) / 2;
        startY = paddingTop + mDefaultPadding +  mPinyinWidth * 0.25f + height * 0.6f ;

        canvas.drawText(text, startX, startY, mPinyinPaint);
    }

    private void drawCharacter(Canvas canvas, float paddingLeft, float paddingTop, String text) {
        float height,  width;
        float startX, startY;
        mTextPaint.setColor(Color.MAGENTA);
        mTextPaint.setTextSize(mTextSize);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        height = fontMetrics.descent - fontMetrics.ascent;
        width = mTextPaint.measureText(text);
        startX = paddingLeft + (mDefaultPadding + mWidth - width) / 2;
        startY = paddingTop + mDefaultPadding + (mWidth + (height * 3) / 5) / 2;
        if (mShowPinyin == true){
            startY += + mPinyinWidth;
        }

        canvas.drawText(text, startX, startY, mTextPaint);
    }

    private void drawGrids(Canvas canvas) {
        float paddingLeft = getPaddingLeft();
        float paddingTop = getPaddingTop();
        float startX, startY;
        float endX, endY ;
        float midX, midY ;

        for (int i = 0; i < mText.length(); i ++) {
            if (mShowPinyin == true) {
                startX = paddingLeft + mDefaultPadding;
                startY = paddingTop + mDefaultPadding;
                endX = paddingLeft + mWidth;
                endY = paddingTop + mPinyinWidth;
                drawPinyinGrid(canvas, startX, startY, endX, endY);
            }

            startX = paddingLeft + mDefaultPadding;
            startY = paddingTop + mDefaultPadding;
            if (mShowPinyin == true){
                startY += mPinyinWidth;
            }
            endX = paddingLeft + mWidth;
            endY = paddingTop + mWidth;
            if (mShowPinyin == true){
                endY += mPinyinWidth;
            }
            midX = paddingLeft + (mDefaultPadding + mWidth) / 2;
            midY = paddingTop + (mDefaultPadding + mWidth) / 2;
            if (mShowPinyin == true){
                midY += mPinyinWidth;
            }
            drawCharacterGrid(canvas, startX, startY, midX, midY, endX, endY);
            paddingLeft = endX;
            if ((i +1) % mEachLineCharacterNum == 0){
                paddingTop += mWidth;
                if (mShowPinyin == true){
                    paddingTop += mPinyinWidth;
                }
                paddingLeft = getPaddingLeft();
            }
        }
        /*canvas.drawLine(midX/2, startY, midX/2, endY, mLinePaint);
        canvas.drawLine(midX*3/2, startY, midX*3/2, endY, mLinePaint);
        canvas.drawLine(startX, midY/2, endX, midY/2, mLinePaint);
        canvas.drawLine(startX, midY*3/2, endX, midY*3/2, mLinePaint);*/
    }

    private void drawPinyinGrid(Canvas canvas, float startX, float startY, float endX, float endY){
        mLinePaint.setStrokeWidth(10f);
        canvas.drawLine(startX, startY, endX, startY, mLinePaint);
        canvas.drawLine(startX, startY, startX, endY, mLinePaint);
        canvas.drawLine(endX, startY, endX, endY, mLinePaint);
        mLinePaint.setStrokeWidth(5f);
        canvas.drawLine(startX, startY + mPinyinWidth / 3, endX, startY + mPinyinWidth / 3, mLinePaint);
        canvas.drawLine(startX, startY + (mPinyinWidth * 2) / 3, endX, startY + (mPinyinWidth * 2) / 3, mLinePaint);
        mLinePaint.setStrokeWidth(10f);

    }

    private void drawCharacterGrid(Canvas canvas, float startX, float startY, float midX, float midY, float endX, float endY) {
        canvas.drawLine(startX, startY, endX, startY, mLinePaint);
        canvas.drawLine(endX, startY, endX, endY, mLinePaint);
        canvas.drawLine(startX, startY, startX, endY, mLinePaint);
        canvas.drawLine(startX, endY, endX, endY, mLinePaint);

        mLinePaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{2, 2, 15, 20}, 0);
        PathEffect old = mLinePaint.setPathEffect(effects);
        mLinePaint.setStrokeWidth(5f);
        canvas.drawLine(startX, startY, endX, endY, mLinePaint);
        canvas.drawLine(startX, endY, endX, startY, mLinePaint);
        canvas.drawLine(startX, midY, endX, midY, mLinePaint);
        canvas.drawLine(midX, startY, midX, endY, mLinePaint);
        mLinePaint.setPathEffect(old);
        mLinePaint.setStrokeWidth(10f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width;
        int length;

        length = mText.length();
        width = (int) (getPaddingLeft() + mWidth * mEachLineCharacterNum + mDefaultPadding + getPaddingBottom());
        height = (int) (getPaddingTop() + (mWidth + mPinyinWidth) * Math.ceil(length * 1.0/mEachLineCharacterNum) + mDefaultPadding + getPaddingBottom());
        setMeasuredDimension(width, height);
    }
}
