package com.quickartifact.utils.image;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;

import com.quickartifact.BaseApplication;
import com.quickartifact.exception.runtime.InitializationException;
import com.quickartifact.manager.cache.trans_cache.BitmapDownloadCache;
import com.quickartifact.manager.cache.trans_cache.TransCache;
import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.exception.runtime.DownloadException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;


/**
 * 类描述：图片显示下载工具类，引用第三方框架Picasso加载网络图片
 * 创建人：mark.lin
 * 创建时间：2016/10/8 10:36
 * 修改备注：
 */
public final class ImageUtils {


    private ImageUtils() {
       /* Picasso.Builder builder = new Picasso.Builder(BaseApplication.getContext());
        Picasso picasso = builder.build();
        picasso.setIndicatorsEnabled(false);
        Picasso.setSingletonInstance(picasso);*/
    }


    //    /**
    //     * 设置显示头像
    //     *
    //     * @param imgUrl    头像的地址
    //     * @param imageView ImageView
    //     */
    //    public void displayUserIcon(String imgUrl, ImageView imageView) {
    //        displayURL(imgUrl, imageView, R.drawable.default_user_icon);
    //    }
    //
    //
    //    /**
    //     * 加载预览图片
    //     *
    //     * @param imgUrl    网络地址
    //     * @param imageView ImageView
    //     */
    //    public void displayPic(String imgUrl, ImageView imageView) {
    //        displayURL(imgUrl, imageView, R.drawable.default_img);
    //    }
    //    public void displayDesignerIcon(String imgUrl, final ImageView imageView, final ImageView blurView) {
    //
    //        displayDesignerIcon(imgUrl, imageView, new Callback() {
    //            @Override
    //            public void onSuccess() {
    //                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    //                blur(bitmap, blurView);
    //            }
    //
    //            @Override
    //            public void onError() {
    //
    //            }
    //        });
    //    }
    //
    //    /**
    //     * 显示设计师的头像
    //     */
    //    public void displayDesignerIcon(String imgUrl, ImageView imageView, Callback callback) {
    //        displayURL(imgUrl, imageView, R.drawable.default_designer_icon, callback);
    //    }
    //
    //    /**
    //     * 显示设计师的头像
    //     */
    //    public void displayDesignerIcon(String imgUrl, ImageView imageView) {
    //        displayURL(imgUrl, imageView, R.drawable.default_designer_icon);
    //    }
    //
    //
    //    /**
    //     * 显示图片,宽度拉伸，高度自适应
    //     */
    //    public void displayPic(File file, ImageView imageView) {
    //        displayFile(file, imageView, R.drawable.default_img);
    //    }


    //================================
    //图片显示方法
    //================================

    /**
     * 显示drawable资源
     *
     * @param resId
     * @param imageView
     */
    public static void displayResource(@DrawableRes int resId, ImageView imageView) {
        Picasso.with(BaseApplication.getContext())
                .load(resId)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    /**
     * 显示url图片
     *
     * @param imgUrl      图片的网络地址
     * @param imageView   ImageView
     * @param placeHolder 占位图片
     */
    public static void displayURL(String imgUrl, ImageView imageView, @DrawableRes int placeHolder) {
        displayURL(imgUrl, imageView, placeHolder, null);
    }

    /**
     * 加载url图片
     *
     * @param imgUrl      图片的网络地址
     * @param imageView   ImageView
     * @param placeHolder 占位图片
     */
    public static void displayURL(String imgUrl, ImageView imageView, @DrawableRes int placeHolder, Callback callback) {
        try {
            String url = checkURL(imgUrl);
            Picasso.with(BaseApplication.getContext())
                    .load(url)
                    .fit()
                    .centerCrop()
                    .placeholder(placeHolder)
                    .into(imageView, callback);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
    }

    /**
     * 显示文件图片
     * CenterCrop()是一种尺度图像的裁剪技术,填补了ImageView的要求范围,然后修剪其余的范围。ImageView将被完全填满,但整个图像可能不会显示。
     * CenterInside()是一种尺度图像的裁剪技术,这样两个尺寸等于或小于请求的ImageView的界限。图像将显示完全,但可能不会填满整个ImageView
     */
    public static void displayFile(File file, ImageView imageView, @DrawableRes int placeHolder) {
        try {
            if (CheckUtils.checkFileExists(file)) {
                Picasso.with(BaseApplication.getContext())
                        .load(file)
                        .fit()
                        .centerInside()
                        .placeholder(placeHolder)
                        .into(imageView);
            }
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
    }

    /**
     * 显示需要虚化的图片
     *
     * @param bkg
     * @param view
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void blur(Bitmap bkg, ImageView view) {
        try {
            float scaleFactor = 8;
            float radius = 2;
            Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor), (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(overlay);
            canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
            canvas.scale(1 / scaleFactor, 1 / scaleFactor);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            RectF rectF = new RectF(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            canvas.drawBitmap(bkg, null, rectF, paint);
            overlay = BitmapUtils.doBlur(overlay, (int) radius, true);
            view.setImageDrawable(new BitmapDrawable(overlay));
            view.setScaleType(ImageView.ScaleType.FIT_XY);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

    }

    /**
     * 取消某一view的图片加载
     *
     * @param imageView
     */
    public static void cancel(ImageView imageView) {
        Picasso.with(BaseApplication.getContext()).cancelRequest(imageView);
    }

    private static String checkURL(String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return "default";
        }
        return imgUrl;
    }


    //======================================
    //图片文件下载方法
    //======================================

    /**
     * 图片下载
     *
     * @param imageUrl 图片url地址
     * @param cache    缓存到指定的cache
     * @param callback 下载回调
     */
    public static void downloadImage(final String imageUrl, final TransCache<Bitmap, File> cache, final ImageDownloadCallback callback) {

        if (cache == null) {
            throw new InitializationException("cache must be not null");
        }

        imageWithTarget(imageUrl, new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                if (CheckUtils.checkBitmapAvailable(bitmap)) {

                    boolean b = cache.put(imageUrl, bitmap);
                    if (callback == null) {
                        return;
                    } else if (b) {
                        callback.finish(bitmap);
                    } else {
                        callback.error(new DownloadException(imageUrl));
                    }
                } else {
                    if (callback != null) {
                        callback.error(new DownloadException(imageUrl));
                    }
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                if (callback != null) {
                    callback.error(new DownloadException(imageUrl));
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

    }

    /**
     * 从缓存中获取下载的图片
     *
     * @param imageUrl
     * @return
     */
    public static File getDownloadImage(String imageUrl) {
        return BitmapDownloadCache.getInstance().get(imageUrl);
    }

    /**
     * 加载url图片，通过target处理
     *
     * @param url
     * @param target
     */
    public static void imageWithTarget(String url, Target target) {
        Picasso.with(BaseApplication.getContext()).load(url).into(target);
    }

    /**
     * 图片下载的回调
     */
    public interface ImageDownloadCallback {

        /**
         * 下载完成
         *
         * @param bitmap 下载到的图片
         */
        void finish(Bitmap bitmap);


        /**
         * 下载失败
         *
         * @param exception
         */
        void error(Exception exception);

    }

}
