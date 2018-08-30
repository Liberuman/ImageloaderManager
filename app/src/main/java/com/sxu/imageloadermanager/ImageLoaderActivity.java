package com.sxu.imageloadermanager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sxu.imageloader.instance.FrescoInstance;
import com.sxu.imageloader.instance.GlideInstance;
import com.sxu.imageloader.ImageLoaderManager;
import com.sxu.imageloader.instance.UILInstance;
import com.sxu.imageloader.WrapImageView;
import com.sxu.imageloader.listener.SimpleImageLoaderListener;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/19
 */


public class ImageLoaderActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_loader_layout);

		WrapImageView image = (WrapImageView) findViewById(R.id.image);
		WrapImageView blurImage = (WrapImageView) findViewById(R.id.blur_image);
		WrapImageView rectangleImage = (WrapImageView) findViewById(R.id.rectangle_image);
		WrapImageView circleImage = (WrapImageView) findViewById(R.id.circle_image);

		int type = getIntent().getIntExtra("type", 0);
		if (type == 0) {
			ImageLoaderManager.getInstance().init(getApplicationContext(), new FrescoInstance());
		} else if (type == 1) {
			ImageLoaderManager.getInstance().init(getApplicationContext(), new UILInstance());
		} else if (type == 2) {
			ImageLoaderManager.getInstance().init(getApplicationContext(), new GlideInstance());
		} else {
			/**
			 * Nothing
			 */
		}

		ImageLoaderManager.getInstance().displayImage("http://img5.imgtn.bdimg.com/it/u=3604858831,3357525860&fm=26&gp=0.jpg", image);
		ImageLoaderManager.getInstance().displayImage("http://img3.imgtn.bdimg.com/it/u=2942272814,2073526115&fm=26&gp=0.jpg", blurImage);
		ImageLoaderManager.getInstance().displayImage("http://img0.imgtn.bdimg.com/it/u=3290825633,1704161465&fm=26&gp=0.jpg", rectangleImage);
		ImageLoaderManager.getInstance().displayImage("http://img4.imgtn.bdimg.com/it/u=2408754315,4176997451&fm=26&gp=0.jpg", circleImage);

		findViewById(R.id.download_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageLoaderManager.getInstance().downloadImage(ImageLoaderActivity.this,
						"http://img5.imgtn.bdimg.com/it/u=640974225,3115876790&fm=26&gp=0.jpg",
						new SimpleImageLoaderListener() {
							@Override
							public void onCompleted(Bitmap bitmap) {
								Toast.makeText(getBaseContext(), "下载成功", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onFailure(Exception e) {
								Toast.makeText(getBaseContext(),
										"下载失败", Toast.LENGTH_SHORT).show();
							}
						});
			}
		});
	}
}
