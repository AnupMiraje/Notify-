package in.orange.noticeboard.faculty_main_ui.archives;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import in.orange.noticeboard.R;

public class ArchiveFragment extends Fragment{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabs;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.noticeboard, container, false);
        setHasOptionsMenu(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        mViewPager = root.findViewById(R.id.notice_view_pager);
        setupViewPager(mViewPager);

        tabs = root.findViewById(R.id.notice_tabs);
        tabs.setupWithViewPager(mViewPager);

        return root;
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }
}