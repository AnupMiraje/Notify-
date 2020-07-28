package in.orange.noticeboard.faculty_main_ui.archives;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private String[] Title={"GENERAL","DEPARTMENTAL","CENTRAL","STAFF"};

    public SectionsPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){return new Tab_general();}
        else if(position==1){return new Tab_departmental();}
        else if(position==2){return new Tab_central();}
        else {return new Tab_staff();}
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Title[position];
    }

    @Override
    public int getCount() {
        return Title.length;
    }
}