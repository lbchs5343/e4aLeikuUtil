package com.wind.lib.util;

import java.io.File;
import java.util.ArrayList;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/9/4 20:33
 */

public class MyArrayList<E> extends ArrayList<E> {
    public MyArrayList() {

    }
    public boolean contains(Object o) {
        if (o instanceof File) {
            if (size() > 0) {
                for (Object item : this) {
                    File file = (File) item;
                    File file1 = (File) o;
                    if (file1.getAbsolutePath().replace("\\", "/").equals(file.getAbsolutePath().replace("\\", "/"))) {
                        return true;
                    }
                }
            }
        }
        return indexOf(o) >= 0;
    }
}
