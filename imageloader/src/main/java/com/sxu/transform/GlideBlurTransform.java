package com.sxu.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.sxu.utils.FastBlurUtil;

import java.security.MessageDigest;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/19
 */


public class GlideBlurTransform extends BitmapTransformation {

	private Context mContext;
	private int mBlurRadius;

	public GlideBlurTransform(Context context, int blurRadius) {
		this.mContext = context;
		this.mBlurRadius = blurRadius;
	}

	@Override
	protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
		return FastBlurUtil.doBlur(toTransform, 8, mBlurRadius);
	}

	@Override
	public void updateDiskCacheKey(MessageDigest messageDigest) {

	}
}
