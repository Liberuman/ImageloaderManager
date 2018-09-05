package com.sxu.imageloader.instance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sxu.imageloader.listener.ImageLoaderListener;
import com.sxu.imageloader.WrapImageView;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/5
 */


public class FrescoInstance implements ImageLoaderInstance {

	public void init(Context context) {
		Fresco.initialize(context);
	}

	@Override
	public void displayImage(String url, final WrapImageView imageView) {
		displayImage(url, imageView, null);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView, final ImageLoaderListener listener) {
		if (url == null) {
			url = "";
		}

		// 设置图片形状
		RoundingParams params;
		if (imageView.getShape() == WrapImageView.SHAPE_CIRCLE) {
			params = RoundingParams.asCircle();
		} else if (imageView.getShape() == WrapImageView.SHAPE_ROUND) {
			if (imageView.getRadii() != null) {
				params = RoundingParams.fromCornersRadii(imageView.getRadii());
			} else {
				params = RoundingParams.fromCornersRadius(imageView.getRadius());
			}
		} else {
			params = RoundingParams.fromCornersRadius(0);
		}
		if (imageView.getBorderColor() != 0 && imageView.getBorderWidth() != 0) {
			params.setBorder(imageView.getBorderColor(), imageView.getBorderWidth());
			params.setPadding(imageView.getBorderWidth());
		}

		GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(imageView.getContext().getResources());
		builder.setRoundingParams(params);
		// 设置占位图，失败图，遮罩
		if (imageView.getPlaceHolder() != 0) {
			builder.setPlaceholderImage(imageView.getPlaceHolder());
		}
		if (imageView.getFailureHolder() != 0) {
			builder.setFailureImage(imageView.getFailureHolder());
		}
		if (imageView.getOverlayImageId() != 0) {
			builder.setOverlay(imageView.getContext().getResources().getDrawable(imageView.getOverlayImageId()));
		}

		ScalingUtils.ScaleType scaleType = getScaleType(imageView.getScaleType());
		builder.setPlaceholderImageScaleType(scaleType)
				.setFailureImageScaleType(scaleType)
				.setActualImageScaleType(scaleType);
		imageView.setHierarchy(builder.build());
		PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();

		ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
		if (imageView.isBlur()) {
			// 高斯模糊效果使用RGB_565时无效
			requestBuilder.setPostprocessor(new IterativeBoxBlurPostProcessor(imageView.getBlurRadius() * 4));
		} else {
			ImageDecodeOptionsBuilder optionsBuilder = new ImageDecodeOptionsBuilder();
			optionsBuilder.setBitmapConfig(Bitmap.Config.RGB_565);
			ImageDecodeOptions options = new ImageDecodeOptions(optionsBuilder);
			requestBuilder.setImageDecodeOptions(options);
		}
		controller.setImageRequest(requestBuilder.build());

		if (listener != null) {
			controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
				@Override
				public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
					if (imageInfo != null) {
						listener.onCompleted(Bitmap.createBitmap(imageInfo.getWidth(), imageInfo.getHeight(), Bitmap.Config.RGB_565));
					} else {
						listener.onFailure(new Exception("Image Info is null"));
					}
				}

				@Override
				public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
				}

				@Override
				public void onFailure(String id, Throwable throwable) {
					listener.onFailure(new Exception(throwable));
				}
			});
		}
		imageView.setController(controller.build());
	}

	@Override
	public void downloadImage(Context context, String url, final ImageLoaderListener listener) {
		if (TextUtils.isEmpty(url)) {
			if (listener != null) {
				listener.onFailure(new Exception("url can't be null"));
			}
			return;
		}

		DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
			@Override
			public void onNewResultImpl(DataSource<CloseableReference<CloseableBitmap>> dataSource) {
				if (listener == null) {
					return;
				}

				if (dataSource != null && dataSource.isFinished()) {
					if (dataSource.getResult() != null) {
						try {
							CloseableBitmap closeableBitmap = dataSource.getResult().get();
							Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
							if (bitmap != null && !bitmap.isRecycled()) {
								listener.onCompleted(bitmap);
							} else {
								listener.onFailure(new Exception("bitmap is null or it's recycled"));
							}
						} catch (Exception e) {
							listener.onFailure(e);
						} finally {
							dataSource.getResult().close();
						}
					} else {
						listener.onFailure(new Exception("bitmap is null or it's recycled"));
					}
				} else {
					listener.onFailure(new Exception("bitmap is null or it's recycled"));
				}
			}

			@Override
			public void onFailureImpl(DataSource dataSource) {
				if (listener != null) {
					listener.onFailure(new Exception("url can't be null"));
				}
			}
		};

		ImagePipeline imagePipeline = Fresco.getImagePipeline();
		ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build();
		DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(request, context);
		dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance());
	}

	private ScalingUtils.ScaleType getScaleType(ImageView.ScaleType scaleType) {
		ScalingUtils.ScaleType newScaleType;
		switch (scaleType) {
			case CENTER:
				newScaleType = ScalingUtils.ScaleType.CENTER;
				break;
			case CENTER_CROP:
				newScaleType = ScalingUtils.ScaleType.FOCUS_CROP;
				break;
			case CENTER_INSIDE:
				newScaleType = ScalingUtils.ScaleType.CENTER_INSIDE;
				break;
			case FIT_CENTER:
				newScaleType = ScalingUtils.ScaleType.FIT_CENTER;
				break;
			case FIT_START:
				newScaleType = ScalingUtils.ScaleType.FIT_START;
				break;
			case FIT_END:
				newScaleType = ScalingUtils.ScaleType.FIT_END;
				break;
			case FIT_XY:
				newScaleType = ScalingUtils.ScaleType.FIT_XY;
				break;
			default:
				newScaleType = ScalingUtils.ScaleType.CENTER;
				break;
		}

		return newScaleType;
	}

	@Override
	public void destroy() {
		if (Fresco.hasBeenInitialized()) {
			Fresco.shutDown();
		}
	}
}
