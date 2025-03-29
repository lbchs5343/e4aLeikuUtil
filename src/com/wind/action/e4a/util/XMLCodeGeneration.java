package com.wind.action.e4a.util;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.wind.action.dialog.InputDialog;
import com.wind.action.util.ClipboardUtil;
import com.wind.action.util.FileUtil;
import com.wind.action.util.LayoutXmlUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/10/15 9:05
 */

public class XMLCodeGeneration extends AnAction {
    private static final String CODE="\n" +
            "import android.content.Context;\n" +
            "import android.view.View;\n" +
            "import android.view.ViewGroup;\n" +
            "import android.widget.BaseAdapter;\n" +
            "import android.widget.ImageView;\n" +
            "import android.widget.TextView;\n" +
            "\n" +
            "import com.bumptech.glide.Glide;\n" +
            "import %s.R;\n" +
            "import java.io.File;\n" +
            "import java.util.List;\n" +
            "\n" +
            "public class %s extends BaseAdapter {\n" +
            "    private ItemConf itemConf;\n" +
            "    private List<Item> itemList;\n" +
            "    private Context ctx;\n" +
            "\n" +
            "\n" +
            "\n" +
            "    public %s(ItemConf itemConf, List<Item> itemList, Context ctx) {\n" +
            "        this.itemConf = itemConf;\n" +
            "        this.itemList = itemList;\n" +
            "        this.ctx = ctx;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public int getCount() {\n" +
            "        return itemList.size();\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    @Override\n" +
            "    public Object getItem(int position) {\n" +
            "        return itemList.get(position);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public long getItemId(int position) {\n" +
            "        return position;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public View getView(int position, View cv, ViewGroup parent) {\n" +
            "        Item item = itemList.get(position);\n" +
            "        %s\n" +
            "//            if (item.image.startsWith(\"http\")) {\n" +
            "//                Glide.with(ctx).load(item.image).placeholder(itemConf.staicIco).error(itemConf.errIco).into(holder.image);\n" +
            "//            } else {\n" +
            "//                Glide.with(ctx).load(new File(item.image)).placeholder(itemConf.staicIco).error(itemConf.errIco).into(holder.image);\n" +
            "//            }\n" +
            "        return cv;\n" +
            "    }\n" +
            "    %s\n" +
            "}\n";
    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            FileDocumentManager.getInstance().saveAllDocuments();
            this.project = event.getData(LangDataKeys.PROJECT);
            project = event.getData(LangDataKeys.PROJECT);
            VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
            File file = new File(virtualFile.getPath());
            String genCatalog=file.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath().replace("\\","/");
            //F:/gong/umei_backend
            String projectPath = genCatalog+ "/build/tmpCode/BaseAdapterCode";
            String androidManifest = genCatalog+ "/src/main/AndroidManifest.xml";
            String packageName = AndroidManifestUtil.parsePackageName(new File(androidManifest));
            File fileCatalog = new File(projectPath);
            if (!fileCatalog.exists()) {
                fileCatalog.mkdirs();
            }

            if (file.exists()) {
                String id = event.getActionManager().getId(this);
                String code;
                switch (id) {
                    case "ViewHolder":
                        code = LayoutXmlUtil.getViewHolder(file);
                        if (!code.equals("")) {
                            ClipboardUtil.setClipboardObj(code);
                            Messages.showMessageDialog(project, "ViewHolder代码生成成功!已复制到剪辑板", "提示", Messages.getWarningIcon());
                        }
                        break;
                    case "BaseAdapter_getView":
                        code = LayoutXmlUtil.getBringInCode(file);
                        if (!code.equals("")) {
                            ClipboardUtil.setClipboardObj(code);
                            Messages.showMessageDialog(project, "BaseAdapter_getView代码生成成功!已复制到剪辑板", "提示", Messages.getWarningIcon());
                        }
                        break;
                    case "BaseAdapter":
                        InputDialog inputDialog = new InputDialog("欲创建类名_注意创建的是BaseAdapter适配器");
                        if (inputDialog.showAndGet()) {
                            String nameClass = inputDialog.getInput().getText();
                            String javaCode=String.format(CODE,packageName,nameClass,nameClass,LayoutXmlUtil.getBringInCode(file),LayoutXmlUtil.getViewHolder(file));
                            FileUtil.写出文件s(javaCode,projectPath+"/"+nameClass+".java");
                            ClipboardUtil.setClipboardObj(new File(projectPath+"/"+nameClass+".java"));
                            Messages.showMessageDialog(project, "BaseAdapter类文件创建成功!已复制到剪辑板", "提示", Messages.getWarningIcon());
                        }
                        break;
                    case "variableName":
                        code = LayoutXmlUtil.getVariableNameCode(file);
                        if (!code.equals("")) {
                            ClipboardUtil.setClipboardObj(code);
                            Messages.showMessageDialog(project, "变量代码生成成功!已复制到剪辑板", "提示", Messages.getWarningIcon());
                        }
                        break;
                    case "variableNames":
                        code = LayoutXmlUtil.getVariableNameAssignmentCode(file);
                        if (!code.equals("")) {
                            ClipboardUtil.setClipboardObj(code);
                            Messages.showMessageDialog(project, "变量赋值代码生成成功!已复制到剪辑板", "提示", Messages.getWarningIcon());
                        }
                        break;
                }


            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public void update(AnActionEvent event) {
        VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
        if (virtualFile != null) {
            File file = new File(virtualFile.getPath());
            if (file.getParentFile().getName().equals("layout") && file.getName().endsWith(".xml")) {
                event.getPresentation().setEnabledAndVisible(true);
            } else {
                event.getPresentation().setEnabledAndVisible(false);
            }
        }
    }
}