package de.ameyering.wgplaner.wgplaner.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

public class CircularImageView extends AppCompatImageView {
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;

    private Bitmap mBitmap;
    private RectF mBoundsRect = new RectF();
    private Paint mBitmapPaint = new Paint();
    private float mDrawableRadius = 0f;
    private int mBitmapHeight = 0;
    private int mBitmapWidth = 0;


    public CircularImageView(Context context) {
        super(context);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap == null){
            return;
        } else {
            canvas.drawCircle(mBoundsRect.centerX(), mBoundsRect.centerY(), mDrawableRadius, mBitmapPaint);
        }
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    private void init(){
        setup();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setOutlineProvider(new OutlineProvider());
        }
    }

    private void initializeBitmap(){
        Drawable drawable = getDrawable();

        if(drawable == null){
            return;
        } else if (drawable instanceof BitmapDrawable) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
            setup();

            return;
        } else if (drawable instanceof ColorDrawable) {
            mBitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
        } else {
            mBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
        }

        Canvas canvas = new Canvas(mBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        setup();
    }

    private void setup(){
        initializeBitmapPaint();
    }

    private void initializeBitmapPaint(){
        if(getWidth() == 0 && getHeight() == 0){
            return;
        } else if(mBitmap == null){
            return;
        }

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        BitmapShader bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader = updateShaderMatrix(bitmapShader);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(bitmapShader);

        invalidate();
    }

    private BitmapShader updateShaderMatrix(BitmapShader shader){
        float scale = 0;
        float dx = 0;
        float dy = 0;

        calculateBounds();

        Matrix shaderMatrix = new Matrix();
        shaderMatrix.set(null);

        if(mBitmapWidth * mBoundsRect.height() > mBitmapHeight * mBoundsRect.width()){
            scale = mBoundsRect.height() / (float) mBitmapHeight;
            dx = (mBoundsRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mBoundsRect.width() / (float) mBitmapWidth;
            dy = (mBoundsRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        shaderMatrix.setScale(scale, scale);
        shaderMatrix.postTranslate((int) (dx + 0.5f) + mBoundsRect.left, (int) (dy + 0.5f) + mBoundsRect.top);

        shader.setLocalMatrix(shaderMatrix);

        return shader;
    }

    private void calculateBounds(){
        int maxHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        int maxWidth = getWidth() - getPaddingRight() - getPaddingLeft();

        int diameter = Math.min(maxHeight, maxWidth);

        float left = getPaddingLeft() + (maxWidth - diameter) / 2f;
        float top = getPaddingTop() + (maxHeight - diameter) / 2f;

        mBoundsRect = new RectF(left, top, left + diameter, top + diameter);

        mDrawableRadius = Math.min(mBoundsRect.height() / 2.0f, mBoundsRect.width() / 2.0f);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class OutlineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            Rect bounds = new Rect();
            mBoundsRect.roundOut(bounds);
            outline.setRoundRect(bounds, bounds.width() / 2.0f);
        }
    }
}
