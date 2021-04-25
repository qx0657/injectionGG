package com.android.system.a;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/24
 *    desc   : 基于原生 WebView 封装
 */
public final class BrowserView extends WebView {

    static {
        // WebView 调试模式开关
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
    }

    public BrowserView(Context context) {
        this(context, null);
    }

    public BrowserView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(getFixedContext(context), attrs, defStyleAttr, 0);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        WebSettings settings = getSettings();
        // 允许文件访问
        settings.setAllowFileAccess(true);
        // 允许网页定位
        settings.setGeolocationEnabled(true);
        // 允许保存密码
        //settings.setSavePassword(true);
        // 开启 JavaScript
        settings.setJavaScriptEnabled(true);
        // 允许网页弹对话框
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 加快网页加载完成的速度，等页面完成再加载图片
        settings.setLoadsImagesAutomatically(true);
        // 本地 DOM 存储（解决加载某些网页出现白板现象）
        settings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 解决 Android 5.0 上 WebView 默认不允许加载 Http 与 Https 混合内容
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 不显示滚动条
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    /**
     * 修复原生 WebView 和 AndroidX 在 Android 5.x 上面崩溃的问题
     *
     * doc：https://stackoverflow.com/questions/41025200/android-view-inflateexception-error-inflating-class-android-webkit-webview
     */
    private static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 为什么不用 ContextImpl，因为使用 ContextImpl 获取不到 Activity 对象，而 ContextWrapper 可以
            // 这种写法返回的 Context 是 ContextImpl，而不是 Activity 或者 ContextWrapper
            // return context.createConfigurationContext(new Configuration());
            // 如果使用 ContextWrapper 还是导致崩溃，因为 Resources 对象冲突了
            // return new ContextWrapper(context);
            // 如果使用 ContextThemeWrapper 就没有问题，因为它重写了 getResources 方法，返回的是一个新的 Resources 对象
            return new ContextThemeWrapper(context, context.getTheme());
        }
        return context;
    }

    /**
     * 获取当前的 url
     *
     * @return      返回原始的 url，因为有些url是被WebView解码过的
     */
    @Override
    public String getUrl() {
        String originalUrl = super.getOriginalUrl();
        // 避免开始时同时加载两个地址而导致的崩溃
        if (originalUrl != null) {
            return originalUrl;
        }
        return super.getUrl();
    }

    /**
     * 销毁 WebView
     */
    public void onDestroy() {
        // 停止加载网页
        stopLoading();
        // 清除历史记录
        clearHistory();
        // 取消监听引用
        setBrowserChromeClient(null);
        setBrowserViewClient(null);
        // 移除WebView所有的View对象
        removeAllViews();
        // 销毁此的WebView的内部状态
        destroy();
    }

    /**
     * 已过时，推荐使用 {@link BrowserViewClient}
     */
    @Deprecated
    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
    }

    public void setBrowserViewClient(BrowserViewClient client) {
        super.setWebViewClient(client);
    }

    /**
     * 已过时，推荐使用 {@link BrowserChromeClient}
     */
    @Deprecated
    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    public void setBrowserChromeClient(BrowserChromeClient client) {
        super.setWebChromeClient(client);
    }

    public static class BrowserViewClient extends WebViewClient {

        /**
         * 网站证书校验错误
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Context context = view.getContext();
            if (context == null) {
                return;
            }
            new AlertDialog.Builder(context)
                    .setMessage("当前网站安全证书已过期或不可信\n是否继续浏览")
                    .setPositiveButton("继续浏览",(dialog, which) -> handler.proceed())
                    .setNegativeButton("返回上一页",(dialog, which) -> handler.cancel())
                    .setCancelable(false)
                    .show();
        }

        /**
         * 同名 API 兼容
         */
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (request.isForMainFrame()) {
                onReceivedError(view,
                        error.getErrorCode(), error.getDescription().toString(),
                        request.getUrl().toString());
            }
        }

        /**
         * 加载错误
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        /**
         * 同名 API 兼容
         */
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }

        /**
         * 跳转到其他链接
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String scheme = Uri.parse(url).getScheme();
            if (scheme == null) {
                return true;
            }
            switch (scheme) {
                // 如果这是跳链接操作
                case "http":
                case "https":
                    view.loadUrl(url);
                    break;
                // 如果这是打电话操作
                case "tel":
                    dialing(view, url);
                    break;
                default:
                    break;
            }
            // 已经处理该链接请求
            return true;
        }

        /**
         * 跳转到拨号界面
         */
        protected void dialing(WebView view, String url) {
            Context context = view.getContext();
            if (context == null) {
                return;
            }

            new AlertDialog.Builder(context)
                    .setMessage(String.format("当前号码 %s",url.replace("tel:", "")))
                    .setPositiveButton("拨打",(dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    })
                    .setNegativeButton("取消",null)
                    .setCancelable(false)
                    .show();
        }
    }

    public static class BrowserChromeClient extends WebChromeClient {

        private final BrowserView mWebView;

        public BrowserChromeClient(BrowserView view) {
            mWebView = view;
            if (mWebView == null) {
                throw new IllegalArgumentException("are you ok?");
            }
        }

        /**
         * 网页弹出警告框
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(mWebView.getContext())
                    .setTitle("网页提示")
                    .setMessage(message)
                    .setPositiveButton("确定",(dialog, which) -> result.confirm())
                    .show();
            return true;
        }

        /**
         * 网页弹出确定取消框
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(mWebView.getContext())
                    .setTitle("网页提示")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("确定",(dialog, which) -> result.confirm())
                    .setNegativeButton("取消",(dialog, which) -> result.cancel())
                    .show();
            return true;
        }

        /**
         * 网页弹出输入框
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            EditText editText = new EditText(mWebView.getContext());
            editText.setHint(message);
            editText.setText(defaultValue);
            editText.setPadding(64,32,32,64);
            new AlertDialog.Builder(mWebView.getContext())
                    .setTitle("网页请求输入")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("确定",(dialog, which) -> result.confirm(editText.getText().toString()))
                    .setNegativeButton("取消",(dialog, which) -> result.cancel())
                    .show();
            return true;
        }

        /**
         * 网页请求定位功能
         * 测试地址：https://map.baidu.com/
         */
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            try {
                if(PackageManager.PERMISSION_GRANTED == mWebView.getContext().getPackageManager().checkPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        mWebView.getContext().getPackageManager().getPackageInfo(mWebView.getContext().getPackageName(), 0).packageName)
                        &&
                        PackageManager.PERMISSION_GRANTED == mWebView.getContext().getPackageManager().checkPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        mWebView.getContext().getPackageManager().getPackageInfo(mWebView.getContext().getPackageName(), 0).packageName)){
                    callback.invoke(origin, true, true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * 网页弹出选择文件请求
         * 测试地址：https://app.xunjiepdf.com/jpg2pdf/、http://www.script-tutorials.com/demos/199/index.html
         *
         * @param callback              文件选择回调
         * @param params                文件选择参数
         */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback, FileChooserParams params) {
            /*try{
                if(PackageManager.PERMISSION_GRANTED == mWebView.getContext().getPackageManager().checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        mWebView.getContext().getPackageManager().getPackageInfo(mWebView.getContext().getPackageName(), 0).packageName)){
                    openSystemFileChooser(mWebView.getContext(), callback, params);
                }else{
                    callback.onReceiveValue(null);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }*/
            callback.onReceiveValue(null);
            return true;
        }

    }
}