### 背景

在APP开发过程，图片加载是一个不可或缺的模块，一般我们都会使用比较成熟的开源库，如Fresco, ImageLoader, Glide等。 这些开源库也都提供了简洁的使用方式，使我们可以快速完成图片的加载过程。但是如果我们需要更换图片加载库，就会发现项目中到处都引用了，这无疑是一个庞大的工作量。

为了降低图片加载图在项目中的耦合度，特意对图片加载库进行了二次封装。通过这种方式，我们可快速完成图片加载库的更换工作。

#### 本次封装仅包含图片加载库的基本功能：

- 初始化图片加载库
- 显示图片(圆角，圆形等样式)
- 下载图片

![image](http://od186sz8s.bkt.clouddn.com/ImageLoaderManager.png)

从结构图中我们可以看出，更换图片加载库只需要设置相应的mLoaderInstance即可。

### 使用

    ImageLoaderManager.getInstance().init(getApplicationContext(), new FrescoInstance());
    ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKzUt", image);

### 说明

1. ImageLoaderManager提供了统一的接口，至于初始化中怎样配置，只需要在相应的Instance中去实现即可；

2. 如需要封装其他的图片加载库，如Picasso, 只需要实现ImageLoaderInstance接口即可；

3. 图片加载库在加载图片时都提供了配置项，如设置占位图，失败图，边框等。这些与View相关的配置是以自定义View的形式实现的；
4. 由于Fresco加载的目标是SimpleDraweeView，其他的图片加载库为ImageView, 为了提供统一的目标对象，我们使用继承自GenericDraweeView（SimpleDraweeView的父类）的WrapImage作为加载目标。如果你不需要封装Fresco, 那么可将WrapImageView的父类改成ImageView；

5. 项目中提供了高斯模糊的几种实现方案：Fresco自带，FastBlur算法，RenderScript。具体的效果可运行项目进行查看。说一下这几种方案使用过程中的感受：
    

> 1. Fresco提供的高斯模糊效果算是这3种方案中最好的，稳定，显示效果细腻；
> 2. FastBlur算法，虽然可通过缩放Bitmap来提高转换效率，但显示效果不如Fresco；
> 3. RenderScript虽然是Android提供的，但是使用体验并不好，在ImageLoader中使用，模糊效果显示异常。在Glide中使用时，对Bitmap缩放后模糊效果显示异常。同时由于有些厂商对这一功能进行了阉割，使用时需要添加so库，为了版本的兼容性，还需要引入v8的兼容包。
> 
> 高斯模糊效果是一个很耗性能的一个操作，很容易引起OOM，所以在使用时应将Bitmap尽可能的缩小，同时选择一个稳定的实现方式。


### 总结

此项目旨在介绍这种封装的思想，至于细节方面就交给各位小伙伴了，毕竟很少有APP会同时使用多个图片加载库。看完了不star一下么？