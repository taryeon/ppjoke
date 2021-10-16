package com.example.ppjoke.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ppjoke.R;
import com.example.ppjoke.model.BottomBar;
import com.example.ppjoke.model.Destination;
import com.example.ppjoke.util.AppConfig;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AppBottomBar extends BottomNavigationView {
    private static final int[] sIcons = new int[]{R.drawable.icon_tab_home, R.drawable.icon_tab_sofa, R.drawable.icon_tab_publish, R.drawable.icon_tab_find,R.drawable.icon_tab_mine};

    public AppBottomBar(@NonNull @NotNull Context context) {
        this(context,null);
    }

    public AppBottomBar(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    @SuppressLint("RestrictedApi")
    public AppBottomBar(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BottomBar bottomBar = AppConfig.getsBottomBar();
        List<BottomBar.Tabs> tabs = bottomBar.getTabs();

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};

        int[] colors = new int[]{Color.parseColor(bottomBar.getActiveColor()), Color.parseColor(bottomBar.getInActiveColor())};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        setItemIconTintList(colorStateList);
        setItemTextColor(colorStateList);
        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        setSelectedItemId(bottomBar.getSelectTab());

        for (BottomBar.Tabs tab : tabs) {
            if (!tab.isEnable())
                continue;
            int itemId = getItemId(tab.getPageUrl());
            if (itemId < 0) {
                continue;
            }
            MenuItem item = getMenu().add(0, itemId, tab.getIndex(), tab.getTitle());
            item.setIcon(sIcons[tab.getIndex()]);
        }

        for (BottomBar.Tabs tab : tabs) {
            int iconSize = dp2px(tab.getSize());

            BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(tab.getIndex());
            itemView.setIconSize(iconSize);

            if (TextUtils.isEmpty(tab.getTitle())) {
                itemView.setIconTintList(ColorStateList.valueOf(Color.parseColor(tab.getTintColor())));
                itemView.setShifting(false);
            }
        }
    }

    private int dp2px(int dpValue) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return (int) (metrics.density * dpValue + 0.5f);
    }

    private int getItemId(String pageUrl) {
        Destination destination = AppConfig.getsDestConfig().get(pageUrl);
        if (destination == null) {
            return -1;
        }
        return destination.getId();
    }
}
