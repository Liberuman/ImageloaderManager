package com.sxu.imageloader.instance;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sxu.imageloader.listener.ImageLoaderListener;
import com.sxu.imageloader.WrapImageView;
import com.sxu.transform.GlideBlurTransform;
import com.sxu.transform.GlideCircleBitmapTransform;
import com.sxu.transform.GlideRoundBitmapTransform;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/5
 */


public class GlideInstance implements ImageLoaderInstance {

	@Override
	public void init(Context context) {

	}

	@Override
	public void displayImage(String url, WrapImageView imageView) {
		displayImage(url, imageView, null);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView, final ImageLoaderListener listener) {
		RequestOptions options = new RequestOptions()
				.centerInside()
				.placeholder(imageView.getPlaceHolder())
				.error(imageView.getFailureHolder())
				.dontAnimate();
		// 根据imageView的形状，设置相应的transform
		if (imageView.getShape() == WrapImageView.SHAPE_CIRCLE) {
			if (imageView.isBlur()) {
				options.transforms(new GlideBlurTransform(imageView.getContext(), url, imageView.getBlurRadius()),
						new GlideCircleBitmapTransform(imageView.getBorderWidth(),
								imageView.getBorderColor()));
			} else {
				options.transforms(new GlideCircleBitmapTransform(imageView.getBorderWidth(), imageView.getBorderColor()));
			}
		} else {
			if (imageView.isBlur()) {
				options.transforms(new GlideBlurTransform(imageView.getContext(), url, imageView.getBlurRadius()),
						new GlideRoundBitmapTransform(imageView.getRadius(), imageView.getBorderWidth(),
								imageView.getBorderColor()));
			} else {
				options.transforms(new GlideRoundBitmapTransform(imageView.getRadius(), imageView.getBorderWidth(),
						imageView.getBorderColor()));
			}
		}
		Glide.with(imageView.getContext())
				.load(url)
				.apply(options)
				.into(imageView);
		RequestBuilder builder = Glide.with(imageView.getContext()).load(url);
		builder.load(url).apply(options).into(imageView);
		if (listener != null) {
			builder.listener(new RequestListener() {
				@Override
				public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
					listener.onFailure(e);
					return false;
				}

				@Override
				public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
					if (resource instanceof BitmapDrawable) {
						listener.onCompleted(((BitmapDrawable) resource).getBitmap());
					}
					return false;
				}
			});
		}
	}

	@Override
	public void downloadImage(Context context, String url, final ImageLoaderListener listener) {
		Glide.with(context)
				.load(url)
				.listener(new RequestListener<Drawable>() {
			@Override
			public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
				if (listener != null) {
					listener.onFailure(e);
				}
				return false;
			}

			@Override
			public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
				if (listener != null) {
					if (resource instanceof BitmapDrawable) {
						listener.onCompleted(((BitmapDrawable) resource).getBitmap());
					}
				}
				return false;
			}
		});
	}

	@Override
	public void destroy() {
		Glide.tearDown();
	}
}
