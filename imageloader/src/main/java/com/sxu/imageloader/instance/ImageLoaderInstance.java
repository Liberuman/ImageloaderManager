package com.sxu.imageloader.instance;

import android.content.Context;

import com.sxu.imageloader.listener.ImageLoaderListener;
import com.sxu.imageloader.WrapImageView;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/5
 */

public interface ImageLoaderInstance {

	void init(Context context);

	void displayImage(String url, WrapImageView imageView);

	void displayImage(String url, WrapImageView imageView, final ImageLoaderListener listener);

	void downloadImage(Context context, String url, ImageLoaderListener listener);

	void destroy();
}
