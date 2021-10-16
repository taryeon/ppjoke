package com.example.ppjoke.util;

import android.content.ComponentName;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.example.libnavannotation.FragementDestination;
import com.example.ppjoke.FixFragmentNavigator;
import com.example.ppjoke.model.Destination;

import java.util.HashMap;

public class NavGraphBuilder {

    public static void build(NavController controller, FragmentActivity activity,int containerId){
        NavigatorProvider provider = controller.getNavigatorProvider();
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

        //FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);
        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity,activity.getSupportFragmentManager(),containerId);
        provider.addNavigator(fragmentNavigator);

        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);


        HashMap<String, Destination> destConfig = AppConfig.getsDestConfig();

        for (Destination value : destConfig.values()) {
            if (value.isFragement()){
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setClassName(value.getClazName());
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());

                navGraph.addDestination(destination);
            }else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());
                destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(),value.getClazName()));

                navGraph.addDestination(destination);
            }

            if (value.isAsStarter()){
                navGraph.setStartDestination(value.getId());
            }
        }
        controller.setGraph(navGraph);
    }
}
