package com.wind.action;

import com.wind.action.util.AssertionUtil;
import com.wind.action.util.ClipboardUtil;
import com.wind.lib.Dependency;
import com.wind.lib.util.DependencyUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author ：zhuYi
 * @date ：Created in 2022/9/5 15:35
 */

public class DependencyUi implements ItemListener {
    private JPanel panel1;

    private JButton search;
    private JTextField textFiel;

    private JList<String> listView;
    private JCheckBox className;
    private JCheckBox baoName;
    private JButton shuax;
    private JTextField pathView;
    private JProgressBar progressBar1;
    private JPopupMenu jPopupMenu;
    private DefaultListModel<String> leftListModel;

    public DependencyUi() {
        leftListModel = new DefaultListModel<String>();
        listView.setModel(leftListModel);
        if (DependencyUtil.dependencyMap.size() == 0) {
            DependencyUtil.init2(pathView.getText().trim(),
                    () -> DependencyUtil.dependencyMap.forEach((key, list) -> {
                        list.forEach(dependency -> {
                            String str = dependency.getMavenCoordinate();
                            if (!str.contains("ideaIC-")) {
                                leftListModel.addElement(str);
                            }
                        });
                    }));
        }
        baoName.addItemListener(this);
        className.addItemListener(this);
        search.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                suspend = true;
                downloadData(textFiel.getText().trim(), className.isSelected());
                suspend = false;
            }
        });
        shuax.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DependencyUtil.init2(pathView.getText().trim(), () -> {
                    leftListModel.clear();
                    DependencyUtil.dependencyMap.forEach((key, list) -> {
                        list.forEach(dependency -> {
                            String str = dependency.getMavenCoordinate();
                            if (!str.contains("ideaIC-")) {
                                leftListModel.addElement(str);
                            }
                        });
                    });
                });
            }
        });

        textFiel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    if (className.isSelected()) {
                        updata();
                    } else if (textFiel.getText().trim().length() > 5) {
                        downloadData(textFiel.getText().trim(), className.isSelected());
                    }
                }
            }


        });
        listView.addListSelectionListener(selectionEvent -> {
            String str = listView.getSelectedValue();
            if (selectionEvent.getValueIsAdjusting() && str != null) {
                listView.removeSelectionInterval(listView.getLeadSelectionIndex(), listView.getLeadSelectionIndex());
                if (str.endsWith(".class")) {
                    downloadData(str, true);
                }
            }
        });
        jPopupMenu = new JPopupMenu();

        JMenuItem jMenuItem1 = new JMenuItem("定位到仓库");
        JMenuItem jMenuItem2 = new JMenuItem("复制引用");
        JMenuItem jMenuItem3 = new JMenuItem("复制jarAar");
        JMenuItem jMenuItem4 = new JMenuItem("遍历class");

        jMenuItem1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLib(listView.getSelectedValue().trim());
            }
        });
        jMenuItem2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClipboardUtil.setClipboardObj(listView.getSelectedValue().trim());
            }
        });
        jMenuItem3.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = listView.getSelectedValue().trim();
                String key = str.substring(str.indexOf(":") + 1, str.lastIndexOf("'")).replace(":", "-");
                List<Dependency> dependencyList = DependencyUtil.dependencyMap.get(key);
                if (AssertionUtil.notEmpty(dependencyList)) {
                    for (Dependency dependency : dependencyList) {
                        if (dependency.isValid() && dependency.getClassList() != null && dependency.getClassList().size() > 0) {
                            ClipboardUtil.setClipboardObj(dependency.getJarAar());
                            return;
                        }
                    }
                }
            }
        });
        jMenuItem4.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                revealClassLib(listView.getSelectedValue().trim());
            }
        });
        jPopupMenu.add(jMenuItem1);
        jPopupMenu.add(jMenuItem2);
        jPopupMenu.add(jMenuItem3);
        jPopupMenu.add(jMenuItem4);
        listView.add(jPopupMenu);
        listView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    int index = listView.locationToIndex(e.getPoint());
                    listView.setSelectedIndex(index);
                    if (listView.getLeadSelectionIndex() >= 0 && !listView.getSelectedValue().endsWith(".class")) {
                        jPopupMenu.show(listView, e.getX(), e.getY());
                    }
                }
            }
        });


    }
    String oldStr="";
    private void updata() {
        if (className.isSelected() ) {
            if (!oldStr.equals(textFiel.getText().trim()) && textFiel.getText().trim().length()>4) {
                oldStr = textFiel.getText().trim();
                leftListModel.clear();
                String keyWords = oldStr;
                List<String> keyList = new ArrayList<>();
                DependencyUtil.dependencyMap.forEach((key, list) -> {
                    list.forEach(dependency -> {
                        try {
                            if (dependency.isValid() && dependency.getClassList() != null && dependency.getClassList().size() > 0) {
                                for (String name : dependency.getClassList()) {
                                    if ((name.startsWith(keyWords)||name.endsWith(keyWords)) && !keyList.contains(name)) {
                                        keyList.add(name);
                                        leftListModel.addElement(name);
                                    }
                                }
                            }
                        } catch (NullPointerException n) {
                            n.printStackTrace();
                        }
                    });
                });

            }
        }

    }



    private void revealClassLib(String str) {
        new Thread(){
            @Override
            public void run() {
                try {
                    String key = str.substring(str.indexOf(":") + 1, str.lastIndexOf("'")).replace(":", "-");
                    List<Dependency> dependencyList = DependencyUtil.dependencyMap.get(key);
                    List<String> keyList = new ArrayList<>();
                    if (AssertionUtil.notEmpty(dependencyList)) {
                        leftListModel.clear();
                        dependencyList.forEach(dependency -> {
                            System.out.println(dependency);
                            if (dependency.isValid() && dependency.getClassList() != null && dependency.getClassList().size() > 0) {
                                for (String name : dependency.getClassList()) {
                                    if (!keyList.contains(name)) {
                                        keyList.add(name);
                                        leftListModel.addElement(name);
                                    }
                                }
                            }
                        });

                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }.start();

    }

    private void openLib(String str) {
        try {
            String key = str.substring(str.indexOf(":") + 1, str.lastIndexOf("'")).replace(":", "-");
            List<Dependency> dependencyList = DependencyUtil.dependencyMap.get(key);
            if (AssertionUtil.notEmpty(dependencyList)) {
                dependencyList.forEach(dependency -> {
                    try {
                        if (dependency.isValid()) {
                            java.awt.Desktop.getDesktop().open(dependency.getJarAar().getParentFile());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception ignored) {

        }
    }

    boolean suspend = false;

    private void downloadData(String keyWords, boolean typeClassKey) {
        try {
            leftListModel.clear();
            if (typeClassKey) {
                DependencyUtil.dependencyMap.forEach((key, list) -> {
                    list.forEach(dependency -> {
                        try {
                            if (dependency.isValid()) {
                                for (String name : dependency.getClassList()) {
                                    if ((name.startsWith(keyWords) || name.endsWith(keyWords))) {
                                        if (!dependency.getMavenCoordinate().contains("ideaIC-")) {
                                            leftListModel.addElement(dependency.getMavenCoordinate());
                                        }
                                        break;
                                    }
                                }
                            }
                        } catch (NullPointerException n) {

                        }
                    });
                });
            } else {
                List<File> fileList = DependencyUtil.inquiryDependency(keyWords, false);
                if (AssertionUtil.notEmpty(fileList)) {
                    fileList.forEach(file -> {
                        Dependency dependency = new Dependency(file);
                        if (!dependency.getMavenCoordinate().contains("ideaIC-")) {
                            leftListModel.addElement(dependency.getMavenCoordinate());
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public JPanel getPanel1() {
        return panel1;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == className && e.getStateChange() == ItemEvent.SELECTED) {
            baoName.setSelected(false);
        }
        if (e.getItem() == baoName && e.getStateChange() == ItemEvent.SELECTED) {
            className.setSelected(false);
        }

    }



}
