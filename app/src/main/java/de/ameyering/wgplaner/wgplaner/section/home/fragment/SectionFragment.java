package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;


public abstract class SectionFragment extends Fragment {
    public Toolbar toolbar;
    public FloatingActionButton floatingActionButton;
    public String title;

    public void setToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            this.toolbar = toolbar;
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

            if (toolbar != null) {
                toolbar.setSubtitle(title);
            }
        }
    }

    public String getTitle() {
        return title;
    }
}
