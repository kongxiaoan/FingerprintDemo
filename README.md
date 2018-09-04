### 使用指纹
https://blog.csdn.net/qq_32648731/article/details/82385031 博客地址
> 说明

### 需要知道的

- FingerprintManager : 指纹管理工具类
- FingerprintManager.AuthenticationCallback :使用验证的时候传入该接口，通过该接口进行验证结果回调
- FingerprintManager.CryptoObject: FingerprintManager 支持的分装加密对象的类

> 以上是28以下API 中使用的类 在Android 28版本中google 宣布使用Androidx 库代替Android库，所以在28版本中Android 推荐使用androidx库中的类 所以在本文中我 使用的是推荐是用的FingerprintManagerCompat 二者的使用的方式基本相似

### 如何使用指纹

- 添加权限(这个权限不需要在6.0中做处理)
- 判断硬件是否支持
- 是否已经设置了锁屏 并且已经有一个被录入的指纹
- 判断是否至少存在一条指纹信息

> 最后一条其实可以不用哪个判断

- 开始验证 ，系统默认的每段时间验证指纹次数为5次 次数用完之后自动关闭验证，并且30秒之内不允行在使用验证

验证的方法是authenticate()
```java
/**
*
*@param crypto object associated with the call or null if none required.
* @param flags optional flags; should be 0
* @param cancel an object that can be used to cancel authentication
* @param callback an object to receive authentication events
* @param handler an optional handler for events
**/
@RequiresPermission(android.Manifest.permission.USE_FINGERPRINT)
    public void authenticate(@Nullable CryptoObject crypto, int flags,
            @Nullable CancellationSignal cancel, @NonNull AuthenticationCallback callback,
            @Nullable Handler handler) {
        if (Build.VERSION.SDK_INT >= 23) {
            final FingerprintManager fp = getFingerprintManagerOrNull(mContext);
            if (fp != null) {
                android.os.CancellationSignal cancellationSignal = cancel != null
                        ? (android.os.CancellationSignal) cancel.getCancellationSignalObject()
                        : null;
                fp.authenticate(
                        wrapCryptoObject(crypto),
                        cancellationSignal,
                        flags,
                        wrapCallback(callback),
                        handler);
            }
        }
    }

```

> arg1: 用于通过指纹验证取出AndroidKeyStore中key的值
arg2: 系统建议为0 

>arg3: 取消指纹验证 手动关闭验证 可以调用该参数的cancel方法

> arg4:返回验证结果

>arg5: Handler fingerprint 中的
消息都是通过handler来传递的 如果不需要则传null 会自动默认创建一个主线程的handler来传递消息

---

### 通过零碎的知识完成一个Demo 


博客地址 https://blog.csdn.net/qq_32648731/article/details/82385031


