# FriedEgg
    煎蛋客户端
    下载了好多次MarkDown插件都不成功，这怎么突然就成功了...

## git忽略问题
   - .idea文件夹
   - .gradle文件夹
   - 所有的build文件夹
   - 所有的.iml文件夹
   - 所有的local.properties

    添加的.gitignore并不完整，导致有几个iml文件没有忽略，以后可以用git工具手动添加
    还需要注意的一点，git使用时不要用大小写区分文件或者文件夹，
    windows下不区分大小写，文件夹首字符应该小写

##  在布局中使用android:gravity="left/right"提示使用start/end
    在AndroidStudio中，在布局中写下gravity＝“left|bottom”时，会提示将left替换为start
    left和right代表一种绝对的对齐，而start和end表示基于阅读顺序的对齐。
    主要的阅读顺序有两种：从左向右（LTR）和从右向左（RTL）。
    当使用left的时候，无论是LTR还是RTL，总是左对齐的；而使用start，在LTR中是左对齐，而在RTL中则是右对齐。
    当我们的minSdkVersion>=17时，使用start/end来代替left/right；
    当minSdkVersion<17时，旧的平台不支持RTL，start/end属性是未知的，会被忽略，所以要同时使用start/end和left/right。

## EventBus
    EventBus.getDefault().register(this);//订阅事件
    EventBus.getDefault().post(object);//发布事件
    EventBus.getDefault().unregister(this);//取消订阅
    register会把当前类中匹配的方法，存入一个map，而post会根据实参去map查找进行反射调用
    一个单例内部维持着一个map对象存储了一堆的方法；post无非就是根据参数去查找方法，进行反射调用。


   EventBus包含4个ThreadMode：PostThread，MainThread，BackgroundThread，Async
   - onEventMainThread代表这个方法会在UI线程执行
   - onEventPostThread代表这个方法会在当前发布事件的线程执行
   - BackgroundThread这个方法，如果在非UI线程发布的事件，则直接执行，和发布在同一个线程中。如果在UI线程发布的事件，则加入后台任务队列，使用线程池一个接一个调用。
   - Async 加入后台任务队列，使用线程池调用，注意没有BackgroundThread中的一个接一个。

