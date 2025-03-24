package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabLayoutAdapter extends FragmentStateAdapter {
    public static final int NUM_TABS = 2;

    public TabLayoutAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        Bundle args = new Bundle();

        switch (position) {
            case 0:
                fragment = new PuzzleFragment();
                args.putInt(PuzzleFragment.ARG_ID, 0);
                break;
            case 1:
                fragment = new ClueFragment();
                args.putInt(ClueFragment.ARG_ID, 1);
                break;
            default:
                throw new IllegalArgumentException("Invalid position. Value must be from 0 to " + (NUM_TABS - 1) + " (counting from 0). Given position = " + position);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}
