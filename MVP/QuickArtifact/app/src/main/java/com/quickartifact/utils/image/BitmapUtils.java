package com.quickartifact.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.quickartifact.exception.runtime.InitializationException;
import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.utils.file.FileUtils;
import com.quickartifact.utils.file.FileConfig;
import com.quickartifact.utils.log.LogUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 类描述：Android bitmap工具类
 * 创建人：mark.lin
 * 创建时间：2016/10/8 11:18
 * 修改备注：
 */
public final class BitmapUtils {

    private BitmapUtils() {

    }

    //========================
    //bitmap解析
    //========================

    /**
     * 解析期望宽高的图片，无法100%符合期望宽高
     *
     * @param imageFile 图片文件
     * @param reqWidth  期望的宽度
     * @param reqHeight 期望的高度
     */
    public static Bitmap decodeBitmapFromFile(File imageFile, int reqWidth, int reqHeight) {

        if (!CheckUtils.checkFileExists(imageFile)) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            //根据期望宽高，计算出bitmap应该缩放的倍速
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 计算图片缩放的倍数，返回值是2的幂次方值
     *
     * @param options   BitmapFactory.Options
     * @param reqWidth  期望的宽度
     * @param reqHeight 期望的高度
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }


    ///////////////////////////
    //bitmap压缩
    //////////////////////////

    /**
     * 压缩图片文件imageFile，另存为storageFile。
     *
     * @param imageFile   原文件
     * @param limitWidth  限制的宽度
     * @param limitHeight 限制的高度
     * @param limitSize   限制的大小
     */
    public static boolean compressImageFile(File imageFile, File storageFile, int limitWidth, int limitHeight, int limitSize) {

        //解析出指定宽高的图片
        Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(imageFile, limitWidth, limitHeight);
        if (!CheckUtils.checkBitmapAvailable(bitmap)) {
            return false;
        }

        //旋转图片方向，确保是竖着的
        int rotate = BitmapUtils.getPhotoOrientation(imageFile);
        bitmap = rotateBitmap(bitmap, rotate);

        //最后按质量压缩
        return compressBitmapToFile(bitmap, storageFile, limitSize);

    }

    /**
     * 1、对bitmap压缩，并保存到file
     * 2、文件已存在会删除重建
     * 3、限制压缩后的大小，size<=0不对bitmap进行压缩
     * 3、压缩失败返回false
     *
     * @param bitmap      要压缩的bitmap
     * @param storageFile 压缩后写的文件
     * @param limitSize   限制压缩后的大小 KB
     */
    public static boolean compressBitmapToFile(Bitmap bitmap, File storageFile, int limitSize) {
        storageFile = FileUtils.createNewFile(storageFile);
        if (!CheckUtils.checkFileExists(storageFile)) {
            return false;
        }

        if (limitSize > 0) {

            Bitmap.CompressFormat compressFormat = getBitmapFormat(storageFile);
            byte[] data = compressBitmapToByte(bitmap, compressFormat, limitSize);
            return FileUtils.writeByteToFile(data, storageFile, false);

        } else {

            return compressBitmapToFile(bitmap, storageFile);

        }

    }

    /**
     * 将bitmap压缩成byte[],自动释放bitmap
     *
     * @param bitmap    bitmap图片
     * @param format    图片压缩后的格式 jpeg、png等
     * @param limitSize 限制压缩后的大小KB
     */
    private static byte[] compressBitmapToByte(Bitmap bitmap, Bitmap.CompressFormat format, int limitSize) {

        if (!CheckUtils.checkBitmapAvailable(bitmap) || format == null) {
            return null;
        }

        int offset = 5;
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, quality, baos);
        while (baos.toByteArray().length / 1024 > limitSize) {
            baos.reset();
            quality -= offset;
            if (quality > 0) {
                bitmap.compress(format, quality, baos);
            } else {
                quality += offset;
                bitmap.compress(format, quality, baos);
                break;
            }
        }
        recycle(bitmap);
        return baos.toByteArray();
    }

    /**
     * 1、将bitmap原图保存到指定文件
     * 2、质量100%保存
     * 3、已存在的文件会删除重新创建
     *
     * @param bitmap      Bitmap 图片
     * @param storageFile File 指定文件
     */
    public static boolean compressBitmapToFile(Bitmap bitmap, File storageFile) {

        storageFile = FileUtils.createNewFile(storageFile);
        if (!CheckUtils.checkBitmapAvailable(bitmap) || !CheckUtils.checkFileExists(storageFile)) {
            return false;
        }
        Bitmap.CompressFormat compressFormat = getBitmapFormat(storageFile);
        BufferedOutputStream bos = null;
        try {
            //装饰缓冲区，提高效率
            bos = new BufferedOutputStream(new FileOutputStream(storageFile), 1024);
            bitmap.compress(compressFormat, 100, bos);
            bos.flush();
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(bos);
        }
        return false;
    }


    ///////////////////////////
    //常用方法
    //////////////////////////

    /**
     * 1、旋转图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (!CheckUtils.checkBitmapAvailable(bitmap)) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (CheckUtils.checkBitmapAvailable(bitmap)) {
            LogUtils.d("rotateBitmap bitmap W:" + bitmap.getWidth() + " H:" + bitmap.getHeight());
        }
        return bitmap;
    }


    /**
     * 获取照片文件的方向
     *
     * @param photo 照片文件
     * @return 方向, 正常竖着照的是0
     */
    public static int getPhotoOrientation(File photo) {

        int rotate = 0;
        try {

            ExifInterface exif = new ExifInterface(photo.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    /**
     * 1、虚化图片
     *
     * @param sentBitmap
     * @param radius
     * @param canReuseInBitmap 是否在原图上处理
     * @return
     */
    @SuppressWarnings({"checkstyle:methodlength", "checkstyle:arraytypestyle", "checkstyle:multiplevariabledeclarations", "checkstyle:innerassignment"})
    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        if (!CheckUtils.checkBitmapAvailable(sentBitmap)) {
            return null;
        }

        Bitmap bitmap;

        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    /**
     * 1、释放图片
     */
    public static void recycle(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /**
     * 根据文件,获取对应的图片压缩格式，无法识别返回JPEG压缩格式
     */
    public static Bitmap.CompressFormat getBitmapFormat(File imageFile) {
        if (imageFile == null) {
            throw new InitializationException("imageFile must be not null");
        }
        return getBitmapFormat(imageFile.getAbsolutePath());
    }

    /**
     * 根据文件名,获取对应的图片压缩格式，无法识别返回JPEG压缩格式
     *
     * @param imageFileName 图片文件完成名称
     * @return 返回对应的压缩格式，无法识别返回jpeg压缩格式
     */
    public static Bitmap.CompressFormat getBitmapFormat(String imageFileName) {
        String extension = FileUtils.getImageSuffix(imageFileName);
        switch (extension) {
            case FileConfig.FILE_TYPE_JPEG:
            case FileConfig.FILE_TYPE_JPG:
                return Bitmap.CompressFormat.JPEG;
            case FileConfig.FILE_TYPE_PNG:
                return Bitmap.CompressFormat.PNG;
            case FileConfig.FILE_TYPE_WEBP:
                return Bitmap.CompressFormat.WEBP;
            default:
                return Bitmap.CompressFormat.JPEG;
        }
    }
}
