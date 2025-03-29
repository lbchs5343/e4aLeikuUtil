package com.wind.action.dialog;


import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Created by 谭健 on 2020/8/16. 星期日. 11:22.
 * © All Rights Reserved.
 *
 * IDEA 插件开发 一个只有一个输入框的弹出框
 */
public class InputDialog extends DialogWrapper {


    private JTextField input = new JTextField();

    public JTextField getInput() {
        return input;
    }

    public InputDialog(String title) {
        super(true);
        init();
        setTitle(title);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        input.setPreferredSize(new Dimension(400, 28));
        dialogPanel.add(input);
        return dialogPanel;
    }

    @Nullable
    @Override
    public ValidationInfo doValidate() {
        String text = input.getText();
        if (StringUtils.isNotBlank(text)) {
            return null;
        }
        return new ValidationInfo("输入值不能为空");
    }
}

