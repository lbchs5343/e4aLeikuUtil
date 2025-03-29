package com.wind.action.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/10/15 9:17
 */

public class ClipboardUtil {



    /**
     * 把文本内容设置到系统剪贴板
     */
    public static void setClipboardObj(Object object) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = null;
        if (object instanceof String) {
            trans = new StringSelection((String) object);
        } else if (object instanceof File) {
            trans = new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.javaFileListFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.javaFileListFlavor.equals(flavor);
                }

                @NotNull
                @Override
                public Object getTransferData(DataFlavor flavor) {
                    List<File> l = new ArrayList<File>();
                    l.add((File) object);
                    return l;
                }
            };
        }
        if (trans != null) {
            clipboard.setContents(trans, null);
        }
    }
}
