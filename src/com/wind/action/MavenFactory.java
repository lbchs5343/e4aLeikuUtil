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

/**
 * @author ：zhuYi
 * @date ：Created in 2022/9/5 15:56
 */

public class MavenFactory implements ToolWindowFactory {

    private static ToolWindow toolWindow;
    private static DependencyUi dependencyUi;
    private static Content content;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        dependencyUi=new DependencyUi();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        content = contentFactory.createContent(dependencyUi.getPanel1(), "", false);
        toolWindow.getContentManager().addContent(content);
        MavenFactory.toolWindow = toolWindow;
    }
//
//    public static void show(Project project, Runnable runnable) {
//        if (toolWindow == null) {
//            toolWindow = ToolWindowManager.getInstance(project).getToolWindow("作者:随缘_QQ:874334395");
//        }
//        toolWindow.show(runnable);
//    }
//
//    public static void setDisplayName(String name) {
//        if (dependencyUi != null) {
//            content.setDisplayName(name);
//
//        }
//    }


}
