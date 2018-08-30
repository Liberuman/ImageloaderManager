package com.sxu.imageloader;

import android.content.Context;
import android.util.Log;

import com.sxu.imageloader.instance.ImageLoaderInstance;
import com.sxu.imageloader.listener.ImageLoaderListener;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/5
 */


public class ImageLoaderManager {

	private ImageLoaderInstance mLoaderInstance;

	protected ImageLoaderManager() {

	}

	public static ImageLoaderManager getInstance() {
		return Singleton.instance;
	}

	public void init(Context context, ImageLoaderInstance loaderInstance) {
		if (loaderInstance == null) {
			throw new IllegalArgumentException("loaderInstance can't be null");
		} else if (mLoaderInstance != null) {
			Log.w("out", "ImageLoaderManager has initialized!!!");
		} else {
			synchronized (this) {
				mLoaderInstance = loaderInstance;
				mLoaderInstance.init(context);
			}
		}
	}

	public boolean isInited() {
		return mLoaderInstance != null;
	}

	public void displayImage(String url, WrapImageView imageView) {
		mLoaderInstance.displayImage(url, imageView);
	}

	public void displayImage(String url, WrapImageView imageView, ImageLoaderListener listener) {
		mLoaderInstance.displayImage(url, imageView, listener);
	}

	public void downloadImage(Context context, String url, ImageLoaderListener listener) {
		mLoaderInstance.downloadImage(context, url, listener);
	}

	public void onDestroy() {
		mLoaderInstance.destroy();
		mLoaderInstance = null;
	}

	public static class Singleton {
		final static ImageLoaderManager instance = new ImageLoaderManager();
	}
}
