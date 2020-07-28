package in.orange.noticeboard.faculty_main_ui.noticeboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import in.orange.noticeboard.CreateNotice;
import in.orange.noticeboard.R;

public class NoticeFragment extends Fragment {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.faculty_main_noticeboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item= menu.findItem(R.id.menu_item_add);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i=new Intent(getContext(), CreateNotice.class);
                startActivity(i);
                return false;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }

}