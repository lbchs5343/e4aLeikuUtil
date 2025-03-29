package com.wind.action;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.wind.action.e4a.Configure;
import com.wind.action.util.AssertionUtil;
import com.wind.action.util.ColorEnume;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/6 18:48
 */

public class ConsView {
    private JPanel panel1;
    private JEditorPane editorPane1;
    private Element body = null;
    private HTMLDocument doc;
    private static final String PATTERN = "(^\\D:\\S+\\.[xmljav]{3,4}:\\d+:){1}(.*)";
    private static final Pattern r = Pattern.compile(PATTERN);
    private Project project;

    public ConsView(Project project) {
        this.project = project;
        try {

            HTMLEditorKit hek = new HTMLEditorKit();
            editorPane1.setContentType("text/html");
            editorPane1.setEditorKit(hek);
            doc = (HTMLDocument) editorPane1.getDocument();
            Element[] roots = doc.getRootElements();
            for (int i = 0; i < roots[0].getElementCount(); i++) {
                Element element = roots[0].getElement(i);
                if (element.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY) {
                    body = element;
                    break;
                }
            }
            editorPane1.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        try {
                            if (AssertionUtil.notEmpty(e.getURL())) {
                                String code = e.getURL().toString().replace("\\", "/");
//                                E:/SuiyuanPlay/suiyuan_duohebofangqi/src/main/java/com/suiyuan/play/Jzvd.java:265: 错误: 程序包R不存
                                code = code.substring(code.indexOf(":") + 1, code.length() - 1);
                                int row = Integer.parseInt(code.substring(code.lastIndexOf(":") + 1).trim());
                                File file = new File(code.substring(0, code.lastIndexOf(":")));
                                if (file.exists()) {
                                    if (file.getName().endsWith(".xml")) {
                                        File file2 = Configure.fileMap.get(file.getName());
                                        if (file2 != null && file2.exists()) {
                                            openFile(file2,row);
                                            return;
                                        }
                                    }
                                    openFile(file,row);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            editorPane1.setEditable(false);
            clear();
        } catch (Exception ignored) {

        }

    }

    public void openFile(File file, int row) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        virtualFile.refresh(false, true);
//                                    Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, psiFile.getVirtualFile(),), true);
        new OpenFileDescriptor(project, virtualFile, row - 1, -1).navigate(true);
    }

    public void printlnErr(String msc) {
        Matcher m = r.matcher(msc.replace("\\", "/"));
        String code;
        if (m.find()) {
            try {
                code = String.format("<a style='background-color: #000000;color: #03DAC5;display: block;text-align:left;margin-left:1px;' href='%s'>%s</a><span style='color: #ffffff'>%s</span>",
                        "file://" + m.group(1), m.group(1).replace("\n","").replace(" ",""), m.group(2).replace("error: Resource entry","错误:资源条目")
                                .replace("is already defined","已经被定义!建议屏蔽此条")
                .replace("Originally defined here","最终被定义!一般不建议在此文件中屏蔽"));
            } catch (Exception ignored) {
                code = String.format("<a style='background-color: #000000;color: #03DAC5;display: block;text-align:left;margin-left:1px;' href='%s'>%s</a>",
                        "file://" + m.group(1), m.group(1).replace("\n","").replace(" ",""));
            }
        } else {
            code = String.format("<div style='background-color: #000000;color: #ffffff;text-align:left;'>%s</div>",
                    msc);
        }
        insertAfterEnd(code);

    }

    private void insertAfterEnd(String str) {
        try {
            doc.insertBeforeEnd(body, str);

        } catch (Exception e) {

        }
    }

    public void println(String msc) {
        String code = String.format("<div style='background-color: #000000;color: #ffffff;'>%s</div>", msc);
        insertAfterEnd(code);
    }

    public void println(String msc, ColorEnume backgroundColor, ColorEnume textColor) {
        String code = String.format("<div style='background-color: %s;color: %s;'>%s</div>", backgroundColor.getName(), textColor.getName(), msc);
        insertAfterEnd(code);
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public void clear() {
        editorPane1.setText("");
    }

}
