package com.wind.action.util;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class ResourceUtils {


    public static InputStream getResourceAsStream(@NonNls @NotNull String basePath, @NonNls @NotNull String fileName) throws FileNotFoundException {
        return new FileInputStream(getResourceAsStreamFile(basePath, fileName));
    }
    public static File getResourceAsStreamFile(@NonNls @NotNull String basePath, @NonNls @NotNull String fileName) throws FileNotFoundException {
        PluginId pluginId = PluginId.getId("com.wind.action.suiyuan.id");
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
        File path = plugin.getPath();
        String pluginPath = path.getAbsolutePath();
        if (pluginPath == null) {

            return null;
        }
        String fixedPath = basePath.replace("\\", "/");
        if (fixedPath.endsWith("/")) {
            fixedPath = fixedPath.substring(0, fixedPath.length() - 1);
        }
        if (fixedPath.startsWith("/")) {
            fixedPath = fixedPath.substring(1);
        }
        pluginPath = pluginPath + "/classes/" + fixedPath + "/" + fileName;
        return new File(pluginPath.replace("\\", "/"));
    }



    private static void AssertionNull(Object o) {
        if (o == null) {
            throw new NullPointerException("读取资源文件时发生异常!");
        }

    }

}
