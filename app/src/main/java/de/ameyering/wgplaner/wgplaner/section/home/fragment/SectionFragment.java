package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

public abstract class SectionFragment extends Fragment {
    public ActionBar actionBar;
    public FloatingActionButton floatingActionButton;
    public String title;

    public void setActionBar(ActionBar toolbar) {
        if (toolbar != null) {
            this.actionBar = toolbar;
        }
    }

    public void setFloatingActionButton(FloatingActionButton floatingActionButton) {
        if (floatingActionButton != null) {
            this.floatingActionButton = floatingActionButton;
        }
    }

    public void setTitle(String title) {
        if (title != null) {
            this.title = title;
        }
    }

    public String getTitle() {
        return title;
    }
}
