package com.wind.action.logicLayer;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/4 5:24
 */

public class JavaE4AText {

    public static final String 测试窗口 = "package %s;\n" +
            "\n" +
            "import android.annotation.SuppressLint;\n" +
            "import android.graphics.Color;\n" +
            "import android.graphics.drawable.Drawable;\n" +
            "import android.graphics.drawable.GradientDrawable;\n" +
            "import android.graphics.drawable.StateListDrawable;\n" +
            "import android.view.Gravity;\n" +
            "import android.view.View;\n" +
            "import android.view.ViewGroup;\n" +
            "import android.widget.LinearLayout;\n" +
            "import android.widget.RelativeLayout;\n" +
            "import android.widget.TextView;\n" +
            "\n" +
            "import com.e4a.runtime.android.mainActivity;\n" +
            "\n" +
            "import com.e4a.runtime.系统相关类;\n" +
            "import com.suiyuan.activity.SuiYuanActivity;\n" +
            "\n" +
            "/**\n" +
            " * 创建日期： 2021/5/14\n" +
            " *\n" +
            " * @author 随缘_QQ:874334395\n" +
            " * @version 1.0\n" +
            " * @since JDK 1.8.0_79\n" +
            " * <p>\n" +
            " * 类说明：   模拟E4A应用程序!此类为真实调试E4A类库 1比1还原E4A环境\n" +
            " * 类说明：   SuiYuanActivity 为E4A的窗口类, 具体需要自行研究!\n" +
            " */\n" +
            "\n" +
            "public class %s extends SuiYuanActivity {\n" +
            "\n" +
            "\n" +
            "\n" +
            "    private int 屏宽, 屏高;\n" +
            "\n" +
            "    @SuppressLint(\"ResourceAsColor\")\n" +
            "    @Override\n" +
            "    public void onCreate() {\n" +
            "        背景颜色(Color.parseColor(\"#0B1022\"));\n" +
            "        系统相关类.隐藏状态栏();\n" +
            "        屏宽 = 系统相关类.取屏幕宽度();\n" +
            "        屏高 = 系统相关类.取屏幕高度() - 取状态栏高度();\n" +
            "        //SuiYuanActivity 是相对布局窗口 ,你可以使用他封装E4A窗口程序\n" +
            "        //***************************************************************\n" +
            "//        hx = new 随缘_V7剧集详情列表Impl(this);\n" +
            "//        addView(hx, 屏宽 - dpPx(60), 屏高 - dpPx(60), dpPx(30), dpPx(15));\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "        initUi();\n" +
            "    }\n" +
            "    private void initUi(){\n" +
            "        ViewGroup viewGroup = (ViewGroup) relativeLayouts.getView();\n" +
            "        viewGroup.setClipChildren(false);\n" +
            "        viewGroup.setClipToPadding(false);\n" +
            "        LinearLayout linearLayout = new LinearLayout(mainActivity.getContext());\n" +
            "        linearLayout.setBackgroundColor(Color.TRANSPARENT);\n" +
            "        linearLayout.setOrientation(LinearLayout.VERTICAL);\n" +
            "        linearLayout.addView(newText(\"设置行间距\", -1, dpPx(30), new View.OnClickListener() {\n" +
            "            @Override\n" +
            "            public void onClick(View v) {\n" +
            "\n" +
            "            }\n" +
            "        }));\n" +
            "        linearLayout.addView(newText(\"添加项目\", -1, dpPx(30), new View.OnClickListener() {\n" +
            "            @Override\n" +
            "            public void onClick(View v) {\n" +
            "               // hx.添加项目(\"奇门遁甲\", \"电影\", \"2022\", \"中国\", \"导演: 随缘\", \"主演: 张三/李四/王五/刘雪/邓敏/范冰\", \"剧情: 宋辽太平时期,原本是秘密组织\\\"风火军\\\"培养的特工唐紫\", \"HD\", \"\");\n" +
            "            }\n" +
            "        }));\n" +
            "        linearLayout.addView(newText(\"添加项目\", -1, dpPx(30), new View.OnClickListener() {\n" +
            "            @Override\n" +
            "            public void onClick(View v) {\n" +
            "\n" +
            "            }\n" +
            "        }));\n" +
            "        linearLayout.addView(newText(\"添加项目\", -1, dpPx(30), new View.OnClickListener() {\n" +
            "            @Override\n" +
            "            public void onClick(View v) {\n" +
            "\n" +
            "            }\n" +
            "        }));\n" +
            "        RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(-2,-2);\n" +
            "        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);\n" +
            "        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);\n" +
            "        linearLayout.setLayoutParams(lp);\n" +
            "        addView(linearLayout);\n" +
            "\n" +
            "    }\n" +
            "    public int 取状态栏高度() {\n" +
            "        int statusBarHeight2 = -1;\n" +
            "        try {\n" +
            "            Class<?> clazz = Class.forName(\"com.android.internal.R$dimen\");\n" +
            "            Object object = clazz.newInstance();\n" +
            "            int height = Integer.parseInt(clazz.getField(\"status_bar_height\")\n" +
            "                    .get(object).toString());\n" +
            "            statusBarHeight2 = mainActivity.getContext().getResources().getDimensionPixelSize(height);\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "        }\n" +
            "        return statusBarHeight2;\n" +
            "    }\n" +
            "    private TextView newText(String title, int width, int height, View.OnClickListener onClickListener) {\n" +
            "        TextView textView = new TextView(mainActivity.getContext());\n" +
            "        textView.setLayoutParams(new LinearLayout.LayoutParams(width, height));\n" +
            "        textView.setBackground(getBackground(GradientDrawable.Orientation.LEFT_RIGHT));\n" +
            "        textView.setGravity(Gravity.CENTER);\n" +
            "        textView.setFocusable(true);\n" +
            "        textView.setTextColor(Color.parseColor(\"#FFFFFF\"));\n" +
            "        textView.setText(title);\n" +
            "        textView.setOnClickListener(onClickListener);\n" +
            "        return textView;\n" +
            "    }\n" +
            "    public Drawable getBackground(GradientDrawable.Orientation orientation) {\n" +
            "        //圆角设置\n" +
            "        float[] radii = new float[]{\n" +
            "                dpPx(5), dpPx(5),\n" +
            "                dpPx(5), dpPx(5),\n" +
            "                dpPx(5), dpPx(5),\n" +
            "                dpPx(5), dpPx(5)\n" +
            "        };\n" +
            "        GradientDrawable gradientDrawable = new GradientDrawable();\n" +
            "        gradientDrawable.setColors(new int[]{Color.parseColor(\"#FF5444\"), Color.parseColor(\"#FF7643\")});\n" +
            "        gradientDrawable.setOrientation(orientation);\n" +
            "        gradientDrawable.setStroke(0, Color.TRANSPARENT);\n" +
            "        GradientDrawable gradientDrawable2 = new GradientDrawable();\n" +
            "        gradientDrawable2.setColors(new int[]{Color.TRANSPARENT, Color.TRANSPARENT});\n" +
            "        gradientDrawable2.setStroke(0, Color.TRANSPARENT);\n" +
            "        gradientDrawable.setCornerRadii(radii);\n" +
            "        gradientDrawable2.setCornerRadii(radii);\n" +
            "        StateListDrawable stateListDrawable = new StateListDrawable();\n" +
            "        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, gradientDrawable);\n" +
            "        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, gradientDrawable);\n" +
            "        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, gradientDrawable);\n" +
            "        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, gradientDrawable2);\n" +
            "        return stateListDrawable;\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "}\n";
    public static final String ANDROIDMANIFEST = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "    xmlns:androID=\"http://schemas.android.com/apk/distribution\"\n" +
            "    xmlns:tools=\"http://schemas.android.com/tools\"\n" +
            "    package=\"%s\"\n" +
            "    android:versionCode=\"1\"\n" +
            "    android:versionName=\"1.0\">\n" +
            "\n" +
            "    <uses-permission android:name=\"com.android.launcher.permission.READ_SETTINGS\" />\n" +
            "    <uses-permission android:name=\"android.permission.FOREGROUND_SERVICE\" />\n" +
            "    <uses-permission android:name=\"android.permission.SYSTEM_ALERT_WINDOW\" />\n" +
            "    <uses-permission android:name=\"android.permission.SYSTEM_OVERLAY_WINDOW\" />\n" +
            "    <uses-permission android:name=\"android.permission.INTERNET\" />\n" +
            "    <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\" />\n" +
            "    <uses-permission android:name=\"android.permission.WAKE_LOCK\" />\n" +
            "    <uses-permission android:name=\"android.permission.ACCESS_WIFI_STATE\" />\n" +
            "    <uses-permission android:name=\"android.permission.REORDER_TASKS\" />\n" +
            "    <uses-permission android:name=\"android.permission.WRITE_EXTERNAL_STORAGE\" />\n" +
            "    <uses-permission android:name=\"android.permission.READ_EXTERNAL_STORAGE\" />\n" +
            "    <uses-permission android:name=\"android.permission.MANAGE_EXTERNAL_STORAGE\"\n" +
            "        tools:ignore=\"ScopedStorage\" />\n" +
            "\n" +
            "    <supports-screens\n" +
            "        android:anyDensity=\"true\"\n" +
            "        android:largeScreens=\"true\"\n" +
            "        android:normalScreens=\"true\"\n" +
            "        android:resizeable=\"true\"\n" +
            "        android:smallScreens=\"true\" />\n" +
            "    <application\n" +
            "        android:name=\"com.e4a.runtime.android.MyE4Aapplication\"\n" +
            "        android:allowBackup=\"false\"\n" +
            "        android:hardwareAccelerated=\"true\"\n" +
            "        android:icon=\"@mipmap/ic_launcher\"\n" +
            "        android:label=\"@string/app_name\"\n" +
            "        android:networkSecurityConfig=\"@xml/network_security_config\"\n" +
            "        android:persistent=\"true\"\n" +
            "        android:requestLegacyExternalStorage=\"true\"\n" +
            "        android:supportsRtl=\"true\"\n" +
            "        android:theme=\"@style/WhiteTheme\"\n" +
            "        android:usesCleartextTraffic=\"true\"\n" +
            "        android:allowNativeHeapPointerTagging=\"false\"\n" +
            "        tools:replace=\"android:allowBackup\">\n" +
            "        <activity\n" +
            "            android:name=\"com.e4a.runtime.android.StartActivity\"\n" +
            "            android:icon=\"@mipmap/ic_launcher\"\n" +
            "            android:label=\"@string/app_name\"\n" +
            "            android:theme=\"@style/StartTheme\">\n" +
            "            <intent-filter>\n" +
            "                <action android:name=\"android.intent.action.MAIN\" />\n" +
            "                <category android:name=\"android.intent.category.DEFAULT\" />\n" +
            "                <category android:name=\"android.intent.category.LAUNCHER\" />\n" +
            "            </intent-filter>\n" +
            "        </activity>\n" +
            "        <activity\n" +
            "            android:name=\"com.e4a.runtime.android.mainActivity\"\n" +
            "            android:configChanges=\"orientation|screenSize|smallestScreenSize|keyboard|keyboardHidden|navigation\"\n" +
            "            android:icon=\"@mipmap/ic_launcher\"\n" +
            "            android:label=\"@string/app_name\"\n" +
            "            android:launchMode=\"singleTask\"\n" +
            "            android:screenOrientation=\"portrait\"\n" +
            "            android:windowSoftInputMode=\"adjustPan\">\n" +
            "            <meta-data\n" +
            "                android:name=\"MainForm\"\n" +
            "                android:value=\"%s\" />\n" +
            "            <meta-data\n" +
            "                android:name=\"FontSize\"\n" +
            "                android:value=\"true\" />\n" +
            "        </activity>\n" +
            "\n" +
            "        <uses-library\n" +
            "            android:name=\"org.apache.http.legacy\"\n" +
            "            android:required=\"false\" />\n" +
            "        <meta-data\n" +
            "            android:name=\"android.max_aspect\"\n" +
            "            android:value=\"2.9\" />\n" +
            "        <provider\n" +
            "            android:name=\"android.support.v4.content.FileProvider\"\n" +
            "            android:authorities=\"%s.fileprovider\"\n" +
            "            android:exported=\"false\"\n" +
            "\n" +
            "            android:grantUriPermissions=\"true\">\n" +
            "            <meta-data\n" +
            "                android:name=\"android.support.FILE_PROVIDER_PATHS\"\n" +
            "                android:resource=\"@xml/file_paths_android\" />\n" +
            "        </provider>\n" +
            "\n" +
            "\n" +
            "    </application>\n" +
            "</manifest>\n";
    public static final String 可视接口 = "package %s;\n" +
            "\n" +
            "import com.e4a.runtime.annotations.SimpleComponent;\n" +
            "import com.e4a.runtime.annotations.SimpleEvent;\n" +
            "import com.e4a.runtime.annotations.SimpleFunction;\n" +
            "import com.e4a.runtime.annotations.SimpleObject;\n" +
            "import com.e4a.runtime.annotations.UsesPermissions;\n" +
            "import com.e4a.runtime.components.VisibleComponent;\n" +
            "\n" +
            "@SimpleComponent//必须保留\n" +
            "@SimpleObject//必须保留\n" +
            "@UsesPermissions(permissionNames = \"android.permission.INTERNET,android.permission.WRITE_EXTERNAL_STORAGE,android.permission.ACCESS_NETWORK_STATE\")\n" +
            "public interface %s extends VisibleComponent {\n" +
            "    @SimpleFunction\n" +
            "    void 设置图片(int 资源id);\n" +
            "}\n";
    public static final String 可视实现 = "package %s;\n" +
            "\n" +
            "\n" +
            "import android.view.View;\n" +
            "import android.widget.ImageView;\n" +
            "//全部异常捕获类 相当于打不死的小强 CatchException.install(new ExceptionHandler(){});\n" +
            "import com.suiyuan.util.CatchException; \n" +
            "//调试客户端 连接客户端: TCPClient.getTcpClient().communication(new xxxxx(){}).connect(ip, port, timeOut); \n" +
            "//调试客户端 发送消息TCPClient.getTcpClient().send(xxxx); \n" +
            "import com.suiyuan.service.TCPClient; \n" +
            "//日志打印依赖  开启 Log_e4a.开启全部日志(true)\n" +
            "//日志打印依赖  打印 Log_e4a.i(xxxx,obj)\n" +
            "import com.suiyuan.util.Log_e4a; \n" +
            "import com.suiyuan.util.EventDispatcher; \n" +
            "import com.e4a.runtime.android.mainActivity;\n" +
            "import com.e4a.runtime.components.ComponentContainer;\n" +
            "import com.e4a.runtime.components.impl.android.ViewComponent;\n" +
            "import com.e4a.runtime.components.impl.android.n4.图片框Impl;\n" +
            "import com.e4a.res.%s.R;//此R来源与当前类库下AndroidManifest.xml中配置的包名,非app下AndroidManifest.xml配置的包名\n" +
            "import com.suiyuan.util.E4aUtil;\n" +
            "\n" +
            "//作者:874334395\n" +
            "public class %sImpl extends ViewComponent implements %s {\n" +
            "    private ComponentContainer container;\n" +
            "\n" +
            "    public %sImpl(ComponentContainer container) {\n" +
            "        super(container);\n" +
            "        this.container = container;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected View createView() {\n" +
            "         //注意:请尽量不要再类中使用系统相关类.取资源索引()!容易导致R资源智能检测失效!\n" +
            "         //当然你也可以使用, 如果使用系统相关类.取资源索引() 则内置R资源检测失效\n" +
            "        try {\n" +
            "            //直接使用E4A 的可视控件,如果你想使用E4A控件的事件,那么你必须继承控件\n" +
            "            图片框Impl imageView = new 图片框Impl(container);\n" +
            "        } catch (Exception e) {\n" +
            "            System.out.println(\"不是E4A可视控件\");\n" +
            "        }\n" +
            "        ImageView imageView = new ImageView(mainActivity.getContext());\n" +
            "        //imageView.setBackgroundResource(R.drawable.a);\n" +
            "        return imageView;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void 设置图片(int 资源id) {\n" +
            "        getView().setBackgroundResource(资源id);\n" +
            "    }\n" +
            "}\n";


    public static final String 不可视接口 = "package %s;\n" +
            "\n" +
            "import com.e4a.runtime.annotations.SimpleComponent;\n" +
            "import com.e4a.runtime.annotations.SimpleFunction;\n" +
            "import com.e4a.runtime.annotations.SimpleObject;\n" +
            "import com.e4a.runtime.annotations.UsesPermissions;\n" +
            "import com.e4a.runtime.components.Component;\n" +
            "\n" +
            "@SimpleComponent//必须保留\n" +
            "@SimpleObject\n//必须保留" +
            "@UsesPermissions(permissionNames = \"android.permission.INTERNET,android.permission.WRITE_EXTERNAL_STORAGE,android.permission.ACCESS_NETWORK_STATE\")\n" +
            "public interface %s extends Component {\n" +
            "    @SimpleFunction\n" +
            "    int 计算两数和(int a, int b);\n" +
            "}\n";
    public static final String 不可视实现 = "package %s;\n" +
            "\n" +
            "\n" +
            "import com.e4a.runtime.components.ComponentContainer;\n" +
            "//全部异常捕获类 相当于打不死的小强 CatchException.install(new ExceptionHandler(){});\n" +
            "import com.suiyuan.util.CatchException; \n" +
            "//调试客户端 连接客户端: TCPClient.getTcpClient().communication(new xxxxx(){}).connect(ip, port, timeOut); \n" +
            "//调试客户端 发送消息TCPClient.getTcpClient().send(xxxx); \n" +
            "import com.suiyuan.service.TCPClient; \n" +
            "//日志打印依赖  开启 Log_e4a.开启全部日志(true)\n" +
            "//日志打印依赖  打印 Log_e4a.i(xxxx,obj)\n" +
            "import com.suiyuan.util.Log_e4a; \n" +
            "import com.suiyuan.util.EventDispatcher; \n" +
            "import com.e4a.runtime.android.mainActivity;\n" +
            "import com.e4a.runtime.components.impl.ComponentImpl;\n" +
            "\n" +
            "\n" +
            "//作者:874334395\n" +
            "//这个是类库的实现类\n" +
            "public class %sImpl extends ComponentImpl implements %s {\n" +
            "    public %sImpl(ComponentContainer container) {\n" +
            "        super(container);\n" +
            "    }\n" +
            "    @Override\n" +
            "    public int 计算两数和(int a, int b) {\n" +
            "        return a + b;\n" +
            "    }\n" +
            "}\n";
    public static final String R_TEMPLET = "import android.content.Context;\n" +
            "import java.lang.reflect.Field;\n" +
            "import java.lang.reflect.Method;\n" +
            "\n" +
            "public final class R {\n" +
            "\n" +
            "    private static Context sContext;\n" +
            "\n" +
            "    public static Context getsContext() {\n" +
            "        if (sContext == null) {\n" +
            "            try {\n" +
            "                Method method = Class.forName(\"android.app.ActivityThread\").getDeclaredMethod(\"currentApplication\");\n" +
            "                sContext = (Context) method.invoke(null,(Object[]) null);\n" +
            "            } catch (Exception e) {\n" +
            "                e.printStackTrace();\n" +
            "            }\n" +
            "        }\n" +
            "        return sContext;\n" +
            "    }\n" +
            "\n" +
            "    public static int getRsId(String defType, String paramString) {\n" +
            "        return getsContext().getResources().getIdentifier(paramString, defType, sContext.getPackageName());\n" +
            "    }\n" +
            "\n" +
            "    public static int[] getStyleableIntArray(String name) {\n" +
            "        try {\n" +
            "            Field field = Class.forName(getsContext().getPackageName() + \".R$styleable\").getDeclaredField(name);\n" +
            "            return (int[]) field.get(null);\n" +
            "        } catch (Throwable t) {\n" +
            "        }\n" +
            "        return null;\n" +
            "    }\n" +
            "\n" +
            "    public static int getStyleableIntArrayIndex(String name) {\n" +
            "        try {\n" +
            "            Field field = Class.forName(getsContext().getPackageName() + \".R$styleable\").getDeclaredField(name);\n" +
            "            int ret = (Integer) field.get(null);\n" +
            "            return ret;\n" +
            "        } catch (Throwable t) {\n" +
            "        }\n" +
            "        return -1;\n" +
            "    }\n\n";
    public static final String E4ASTYPE = "<resources>\n" +
            "\n" +
            "    <!--请不要再此文件内添加代码,打包时会跳过,因为这是E4A自带的!,  如需要添加请另外创建文件-->\n" +
            "    \n" +
            "    <style name=\"WhiteBaseTheme\" parent=\"android:Theme.Light\">\n" +
            "\n" +
            "    </style>\n" +
            "\n" +
            "    <style name=\"BlackBaseTheme\" parent=\"android:Theme.Light\">\n" +
            "\n" +
            "    </style>\n" +
            "\n" +
            "    <style name=\"WhiteTheme\" parent=\"WhiteBaseTheme\">\n" +
            "\n" +
            "        <item name=\"android:windowNoTitle\">true</item>\n" +
            "        <item name=\"android:windowFullscreen\">false</item>\n" +
            "    </style>\n" +
            "\n" +
            "    <style name=\"BlackTheme\" parent=\"BlackBaseTheme\">\n" +
            "\n" +
            "        <item name=\"android:windowNoTitle\">true</item>\n" +
            "        <item name=\"android:windowFullscreen\">false</item>\n" +
            "    </style>\n" +
            "\n" +
            "    <style name=\"ClassicTheme\">\n" +
            "\n" +
            "        <item name=\"android:windowNoTitle\">true</item>\n" +
            "        <item name=\"android:windowFullscreen\">false</item>\n" +
            "    </style>\n" +
            "\n" +
            "    <style name=\"StartTheme\">\n" +
            "        <item name=\"android:windowIsTranslucent\">true</item>\n" +
            "        <item name=\"android:windowBackground\">@android:color/transparent</item>\n" +
            "        <item name=\"android:windowNoTitle\">true</item>\n" +
            "        <item name=\"android:windowFullscreen\">false</item>\n" +
            "    </style>\n" +
            "</resources>";

}
