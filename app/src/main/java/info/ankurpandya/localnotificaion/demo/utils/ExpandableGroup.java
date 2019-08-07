package info.ankurpandya.localnotificaion.demo.utils;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class ExpandableGroup {

    private static Map<Integer, Integer> group = new HashMap<>();
    private static View parent;

    public static void setParent(View view) {
        parent = view;
    }

    public static void add(int viewHeader, int viewContent) {
        group.put(viewHeader, viewContent);
    }



}
