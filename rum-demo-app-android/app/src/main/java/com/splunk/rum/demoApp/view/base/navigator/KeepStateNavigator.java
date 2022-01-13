package com.splunk.rum.demoApp.view.base.navigator;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

@Navigator.Name("keep_state_fragment")
public class KeepStateNavigator extends FragmentNavigator {
    private final FragmentManager manager;
    private final Context context;
    private final int containerId;

    public KeepStateNavigator(Context context, FragmentManager manager, int containerId) {
        super(context, manager, containerId);
        this.manager = manager;
        this.context = context;
        this.containerId = containerId;
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        //return super.navigate(destination, args, navOptions, navigatorExtras);
        String tag = String.valueOf(destination.getId());
        FragmentTransaction transaction = manager.beginTransaction();

        boolean initialNavigate = false;
        Fragment currentFragment = manager.getPrimaryNavigationFragment();

        if (currentFragment != null) {
            transaction.detach(currentFragment);
        } else {
            initialNavigate = true;
        }

        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null) {
            String className = destination.getClassName();
            fragment = manager.getFragmentFactory().instantiate(context.getClassLoader(), className);
            transaction.add(containerId, fragment, tag);
        } else {
            transaction.attach(fragment);
        }

        transaction.setPrimaryNavigationFragment(fragment);
        transaction.setReorderingAllowed(true);
        transaction.commitNow();

        if (initialNavigate) {
            return destination;
        } else {
            return null;
        }

    }
}
