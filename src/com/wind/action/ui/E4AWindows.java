package com.wind.action.ui;

import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.wind.action.logicLayer.E4AModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/4 4:07
 */

public class E4AWindows implements ItemListener {
    ArrayList<String> list = new ArrayList<String>();
    Typenum typenum = Typenum.LIB_VISIBLE;
    JCheckBox visible = new JCheckBox("可视类库");
    JCheckBox invisible = new JCheckBox("不可视库");
    JCheckBox dependencyLibrary = new JCheckBox("纯依赖库");
    JCheckBox lib = new JCheckBox("com.google.code.gson:gson:2.8.6");
    JCheckBox lib2 = new JCheckBox("com.github.bumptech.glide:glide:3.7.0");

    JEditorPane jEditorPane = new JEditorPane();
    JFrame frame;
    AnActionEvent event;
    PsiElement appDependenciesElement=null;
    PsiElement settingsElement=null;
    public E4AWindows(AnActionEvent event,PsiElement appDependenciesElement,PsiElement settingsElement) {
        this.event = event;
        this.appDependenciesElement=appDependenciesElement;
        this.settingsElement=settingsElement;
        frame = new JFrame("E4A类库开发工具");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(404, 350);
        frame.setBackground(Color.black);
        JPanel panel = (JPanel) frame.getContentPane();
        panel.setBackground(Color.getColor("#00000000"));//设置背景颜色
        JLabel jLabel1 = new JLabel("类库名称:");
        setType(jLabel1, Color.WHITE, Color.getColor("#00000000"), 14, false);

        setType(jEditorPane, Color.white, Color.getColor("#00000000"), 14, true);
        jLabel1.setBounds(5, 5, 75, 30);
        jEditorPane.setBounds(80, 5, 300, 30);
        visible.addItemListener(this);
        invisible.addItemListener(this);
        dependencyLibrary.addItemListener(this);
        visible.setSelected(true);
        setType(visible, Color.white, Color.getColor("#00000000"), 14, false);
        setType(invisible, Color.white, Color.getColor("#00000000"), 14, false);
        setType(dependencyLibrary, Color.white, Color.getColor("#00000000"), 14, false);
        visible.setBounds(280, 35, 100, 30);
        invisible.setBounds(180, 35, 100, 30);
        dependencyLibrary.setBounds(80, 35, 100, 30);

        JPanel pl = new JPanel();
        pl.setLayout(new FlowLayout(FlowLayout.LEFT));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("依赖选择");
        titledBorder.setTitleColor(Color.CYAN);
        pl.setBorder(titledBorder);
        pl.setBackground(Color.getColor("#00000000"));
        pl.setBounds(5, 70, 380, 200);
        lib.setBounds(0, 0, 380, 30);
        setType(lib, Color.PINK, Color.getColor("#00000000"), 14, false);
        pl.add(lib);
        lib2.setBounds(0, 0, 380, 30);
        lib2.addItemListener(this);
        lib.addItemListener(this);
        setType(lib2, Color.PINK, Color.getColor("#00000000"), 14, false);
        pl.add(lib2);
        JButton jButton = new JButton("创建");
        setType(jButton, Color.WHITE, Color.gray, 16, false);
        jButton.setBounds(305, 275, 78, 30);
        panel.add(jLabel1);
        panel.add(jButton);
        panel.add(jEditorPane);
        panel.add(visible);
        panel.add(invisible);
        panel.add(dependencyLibrary);
        panel.add(pl);
        JLabel authorInformation = new JLabel("作者:随缘 QQ:874334395 QQ群:476412098");
        setType(authorInformation, Color.lightGray, Color.getColor("#00000000"), 14, false);
        authorInformation.setBounds(10,270,300,30);
        panel.add(authorInformation);
        frame.setBounds(300, 300, 404, 350);
        frame.setLayout(null);
        frame.setVisible(true);
        jButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (jButton) {
                    String name = jEditorPane.getText().trim().replace(" ","");
                    if (!"".equals(name)) {
                        Pattern p = Pattern.compile("^[0-9][0-9].*");
                        Matcher m = p.matcher(name);
                        if (m.matches() || name.contains("类库")) {
                            JOptionPane.showMessageDialog(frame, "类库名称不合法:不能包含'类库'或不能以数字开头");
                        } else {
                            try {
                                jButton.setEnabled(false);
                                E4AModel e4AModel = new E4AModel(event, appDependenciesElement,settingsElement);
                                e4AModel.createClassLibrary(typenum, name, list);
                                JOptionPane.showMessageDialog(frame, "项目创建完毕!请点击AS右上角小乌龟进行同步更新");

                                FileDocumentManager.getInstance().saveAllDocuments();
                                SaveAndSyncHandler.getInstance().refreshOpenFiles();

                                VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);

                                frame.dispose();
                            } catch (Exception err) {
                                jButton.setEnabled(true);
                                JOptionPane.showMessageDialog(frame, "创建类库时发生错误! " + err.getMessage());
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "类库名称不能为空");
                    }
                }
            }
        });

    }



    private void setType(JComponent view, Color textColor, Color backgroundColor, int fontSize, boolean rim) {
        Font font = new Font("谐体", Font.PLAIN, fontSize);//创建1个字体实例

        view.setFont(font);
        if (rim) {
            view.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        }
        view.setForeground(textColor);//设置文字的颜色
        view.setBackground(backgroundColor);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == visible && e.getStateChange() == ItemEvent.SELECTED) {
            typenum = Typenum.LIB_VISIBLE;
            invisible.setSelected(false);
            dependencyLibrary.setSelected(false);
        }
        if (e.getItem() == invisible && e.getStateChange() == ItemEvent.SELECTED) {
            typenum = Typenum.LIB_ON_VISIBLE;
            visible.setSelected(false);
            dependencyLibrary.setSelected(false);
        }
        if (e.getItem() == dependencyLibrary && e.getStateChange() == ItemEvent.SELECTED) {
            typenum = Typenum.LIB_SIMPLE;
            visible.setSelected(false);
            invisible.setSelected(false);
        }
        if (e.getItem() != invisible && e.getItem() != visible && e.getItem() != dependencyLibrary) {
            String libName = ((JCheckBox) e.getItem()).getText();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (!list.contains(libName)) {
                    list.add(libName);
                }
            } else {
                list.remove(libName);
            }
        }

    }
}

