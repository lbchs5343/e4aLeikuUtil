package com.wind.action;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/6/29 17:55
 */

public class Configure {
    public static final String BUILE = "plugins {\n" +
            "    id 'com.android.application'\n" +
            "}\n" +
            "android {\n" +
            "    compileSdkVersion 30\n" +
            "    buildToolsVersion \"30.0.3\"\n" +
            "    defaultConfig {\n" +
            "        applicationId \"%s\"\n" +
            "        minSdkVersion 19\n" +
            "        targetSdkVersion 30\n" +
            "        versionCode 1\n" +
            "        versionName \"1.0\"\n" +
            "        //安卓4.4需要\n" +
            "        multiDexEnabled true\n" +
            "        repositories {\n" +
            "            flatDir {\n" +
            "                dirs 'libs'\n" +
            "            }\n" +
            "        }\n" +
            "        ndk {\n" +
            "            // add support lib\n" +
            "//            abiFilters 'armeabi' //, 'arm64-v8a'//, \"mips\"  //,'armeabi''x86',, 'x86_64',\n" +
            "            abiFilters   'armeabi','arm64-v7a'\n" +
            "        }\n" +
            "        testInstrumentationRunner \"android.support.test.runner.AndroidJUnitRunner\"\n" +
            "    }\n" +
            "    buildTypes {\n" +
            "        release {\n" +
            "            minifyEnabled false\n" +
            "            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'\n" +
            "        }\n" +
            "    }\n" +
            "    compileOptions {\n" +
            "        sourceCompatibility JavaVersion.VERSION_1_8\n" +
            "        targetCompatibility JavaVersion.VERSION_1_8\n" +
            "    }\n" +
            "}\n" +
            "dependencies {\n" +
            "    //noinspection GradleCompatible\n" +
            "    implementation 'com.android.support:appcompat-v7:28.0.0'\n" +
            "    //noinspection GradleCompatible\n" +
            "    implementation 'com.android.support:support-v4:27.1.1'\n" +
            "    implementation 'com.android.support.constraint:constraint-layout:2.0.1'\n" +
            "    testImplementation 'junit:junit:4.13.2'\n" +
            "    //noinspection GradleCompatible\n" +
            "    androidTestImplementation 'com.android.support.test:runner:1.0.2'\n" +
            "    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'\n" +
            "    implementation files('libs/E4ARuntime.jar')\n" +
            "    implementation files('libs/SuiyuanUtil-release.jar')\n" +
            "\n" +
            "}";


    public static final String MAVEN_BUILD = "// Top-level build file where you can add configuration options common to all sub-projects/modules.\n" +
            "buildscript {\n" +
            "    repositories {\n" +
            "        maven{url 'http://maven.aliyun.com/nexus/content/groups/public/'}\n" +
            "        maven{url \"https://jitpack.io\"}\n" +
            "        google()\n" +
            "    }\n" +
            "    dependencies {\n" +
            "        classpath \"com.android.tools.build:gradle:4.2.1\"\n" +
            "\n" +
            "        // NOTE: Do not place your application dependencies here; they belong\n" +
            "        // in the individual module build.gradle files\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "allprojects {\n" +
            "    repositories {\n" +
            "        maven{url 'http://maven.aliyun.com/nexus/content/groups/public/'}\n" +
            "        google()\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "task clean(type: Delete) {\n" +
            "    delete rootProject.buildDir\n" +
            "}";

    public static final String GRADLE_WRAPPER = "#Mon Jun 13 13:54:19 CST 2022\n" +
            "distributionBase=GRADLE_USER_HOME\n" +
            "distributionUrl=https\\://services.gradle.org/distributions/gradle-6.7.1-bin.zip\n" +
            "distributionPath=wrapper/dists\n" +
            "zipStorePath=wrapper/dists\n" +
            "zipStoreBase=GRADLE_USER_HOME\n";

    public static final String ALI_BUILD = "// Top-level build file where you can add configuration options common to all sub-projects/modules.\n" +
            "buildscript {\n" +
            "    repositories {\n" +
            "        maven { url 'https://maven.aliyun.com/repository/public' }\n" +
            "        maven { url 'https://maven.aliyun.com/repository/central' }\n" +
            "        maven { url 'https://maven.aliyun.com/repository/jcenter' }\n" +
            "        maven { url 'https://maven.aliyun.com/repository/google' }\n" +
            "        maven { url 'https://repo1.maven.org/maven2' }\n" +
            "        maven { url 'https://mirrors.huaweicloud.com/repository/maven' }\n" +
            "        maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }\n" +
            "        mavenLocal()\n" +
            "        mavenCentral()\n" +
            "    }\n" +
            "    dependencies {\n" +
            "        替换\n" +
            "\n" +
            "        // NOTE: Do not place your application dependencies here; they belong\n" +
            "        // in the individual module build.gradle files\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "allprojects {\n" +
            "    repositories {\n" +
            "        maven { url 'https://maven.aliyun.com/repository/public' }\n" +
            "        maven { url 'https://maven.aliyun.com/repository/central' }\n" +
            "        maven { url 'https://maven.aliyun.com/repository/jcenter' }\n" +
            "        maven { url 'https://maven.aliyun.com/repository/google' }\n" +
            "        maven { url 'https://repo1.maven.org/maven2' }\n" +
            "        maven { url 'https://mirrors.huaweicloud.com/repository/maven' }\n" +
            "        maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }\n" +
            "        maven { url 'https://www.jitpack.io' }\n" +
            "        mavenLocal()\n" +
            "        mavenCentral()\n" +
            "\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "task clean(type: Delete) {\n" +
            "    delete rootProject.buildDir\n" +
            "}";
}
