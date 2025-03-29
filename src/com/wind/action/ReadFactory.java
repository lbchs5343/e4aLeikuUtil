package com.wind.action;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.wind.action.util.ColorEnume;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/5 20:33
 */

public class ReadFactory implements ToolWindowFactory {
    static final String ID = "作者:随缘_QQ:874334395";
    static final Map<Integer, ConsView> CONS_VIEW_MAP = new HashMap<>();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ConsView consView = new ConsView(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(consView.getPanel1(), "Log", false);
        toolWindow.getContentManager().addContent(content);
        CONS_VIEW_MAP.put(toolWindow.hashCode(), consView);
    }

    public static void show(Project project, Runnable runnable) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ID);
        if (toolWindow != null) {
            toolWindow.show(runnable);
        }
    }

    public static void setDisplayName(Project project, String name) {
        Content content = getContent(project);
        if (content != null) {
            content.setDisplayName(name);
        }
    }

    private static Content getContent(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ID);
        return toolWindow.getContentManager().getContent(0);
    }

    private static ConsView getConsView(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ID);
        return CONS_VIEW_MAP.get(toolWindow.hashCode());
    }


    public static void clear(Project project) {
        try {
            getConsView(project).clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void printlnErr(Project project, String msc) {
        try {
            getConsView(project).printlnErr(msc);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void printlnErr(Project project,Throwable e) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(b);
            e.printStackTrace(pw);
            pw.close();
            println(project,new String(b.toByteArray()).replace("\n", "<br />"), ColorEnume.白色, ColorEnume.黑色);
        } catch (RuntimeException r) {

        }
    }


    public static void println(Project project,String msc) {

        try {
            getConsView(project).println(msc);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void println(Project project,String msc, ColorEnume backgroundColor, ColorEnume textColor) {
        try {
            getConsView(project).println(msc, backgroundColor, textColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
