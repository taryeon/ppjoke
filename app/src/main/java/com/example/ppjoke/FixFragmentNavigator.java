package com.example.ppjoke;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Map;

@Navigator.Name("fixfragment")
public class FixFragmentNavigator extends FragmentNavigator {
    private static final String TAG = "FixFragmentNavigator";
    private Context context;
    private FragmentManager manager;
    private int containerId;

    public FixFragmentNavigator(@NonNull @NotNull Context context, @NonNull @NotNull FragmentManager manager, int containerId) {
        super(context, manager, containerId);
        this.context = context;
        this.manager = manager;
        this.containerId = containerId;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public NavDestination navigate(@NonNull @NotNull Destination destination, @Nullable @org.jetbrains.annotations.Nullable Bundle args, @Nullable @org.jetbrains.annotations.Nullable NavOptions navOptions, @Nullable @org.jetbrains.annotations.Nullable Navigator.Extras navigatorExtras) {
        if (manager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already"
                    + " saved its state");
            return null;
        }
        String className = destination.getClassName();
        if (className.charAt(0) == '.') {
            className = context.getPackageName() + className;
        }
//        final Fragment frag = instantiateFragment(mContext, mFragmentManager,
//                className, args);
//        frag.setArguments(args);
        final FragmentTransaction ft = manager.beginTransaction();

        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = enterAnim != -1 ? enterAnim : 0;
            exitAnim = exitAnim != -1 ? exitAnim : 0;
            popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
            popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
        }

        Fragment fragment = manager.getPrimaryNavigationFragment();
        if (fragment!=null){
            ft.hide(fragment);
        }

        String tag = String.valueOf(destination.getId());
        Fragment frag = manager.findFragmentByTag(tag);
        if (frag!=null){
            ft.show(frag);
        }else {
            frag = instantiateFragment(context,manager,className,args);
            frag.setArguments(args);
            ft.add(containerId,frag,tag);
        }
        ft.setPrimaryNavigationFragment(frag);
//        ft.replace(mContainerId, frag);

        final @IdRes int destId = destination.getId();
        ArrayDeque<Integer> mBackStack = null;
        try {
            Field field = FragmentNavigator.class.getDeclaredField("mBackStack");
            field.setAccessible(true);
            mBackStack = (ArrayDeque<Integer>) field.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        final boolean initialNavigation = mBackStack.isEmpty();
        // TODO Build first class singleTop behavior for fragments
        final boolean isSingleTopReplacement = navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId;

        boolean isAdded;
        if (initialNavigation) {
            isAdded = true;
        } else if (isSingleTopReplacement) {
            // Single Top means we only want one instance on the back stack
            if (mBackStack.size() > 1) {
                // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place
                manager.popBackStack(
                        generateBackStackName(mBackStack.size(), mBackStack.peekLast()),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.addToBackStack(generateBackStackName(mBackStack.size(), destId));
            }
            isAdded = false;
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size() + 1, destId));
            isAdded = true;
        }
        if (navigatorExtras instanceof Extras) {
            Extras extras = (Extras) navigatorExtras;
            for (Map.Entry<View, String> sharedElement : extras.getSharedElements().entrySet()) {
                ft.addSharedElement(sharedElement.getKey(), sharedElement.getValue());
            }
        }
        ft.setReorderingAllowed(true);
        ft.commit();
        // The commit succeeded, update our view of the world
        if (isAdded) {
            mBackStack.add(destId);
            return destination;
        } else {
            return null;
        }
    }

    private String generateBackStackName(int backStackindex,int destid){
        return backStackindex+"-"+destid;
    }
}
