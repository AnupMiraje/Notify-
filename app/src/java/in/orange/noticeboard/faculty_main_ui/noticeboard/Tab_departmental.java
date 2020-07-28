package in.orange.noticeboard.faculty_main_ui.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import in.orange.noticeboard.CustomRecyclerAdapter;
import in.orange.noticeboard.DisplayNotice;
import in.orange.noticeboard.NoticeDatabase;
import in.orange.noticeboard.R;
import in.orange.noticeboard.ViewNotice;

public class Tab_departmental extends Fragment implements CustomRecyclerAdapter.OnNoticeClick{

    private BottomSheetBehavior bottomSheetBehavior;
    private ListView department;
    String[] departmentList;
    private int i=0;
    private ArrayAdapter<String> arrayAdapter;

    public CustomRecyclerAdapter myadapter;
    private RecyclerView myRecyclerView;
    private SwipeRefreshLayout pullToRefresh;
    private List<DisplayNotice> l;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceStste) {
        View view = inflater.inflate(R.layout.tab_departmental, container, false);

        LinearLayout bottomSheetLayout = view.findViewById(R.id.bs);
        bottomSheetBehavior= BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setPeekHeight(120);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        departmentList=getResources().getStringArray(R.array.cat);
        departmentList[0]="All";

        l=NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices(departmentList,-1,"0");
        if(l==null){
            l=new ArrayList<DisplayNotice>();
        }
        myadapter=new CustomRecyclerAdapter(l,this);
        myRecyclerView=(RecyclerView) view.findViewById(R.id.listview);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setAdapter(myadapter);
        myadapter.notifyDataSetChanged();

        department=(ListView)view.findViewById(R.id.departmental);
        department.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_single_choice,departmentList);
        department.setAdapter(arrayAdapter);
        department.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemChange(departmentList[position]);
                i=position;
                Log.d("notices",l.size()+"");
                myadapter.notifyDataSetChanged();
                bottomSheetBehavior.setPeekHeight(120);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        pullToRefresh=view.findViewById(R.id.swiperefreshdepartmental);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(i==0)
                {
                    onItemChange("All");
                    bottomSheetBehavior.setPeekHeight(120);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else {
                    try {
                        List<DisplayNotice> temp = NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices(departmentList[i],Integer.parseInt(l.get(0).id),"0");
                        l.addAll(0, temp);
                    } catch (Exception e) {
                        try {
                            l.addAll(NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices(departmentList[i], -1,"0"));
                        } catch (Exception ee) {
                        }
                    }
                }
                myadapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    public void onItemChange(String item) {
        l.clear();
        if (!item.equals("All")) {
            List<DisplayNotice> temp = NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices(item, -1,"0");
            l.addAll(temp);
        }
        else{
            List<DisplayNotice> temp;
            temp=NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices(departmentList,-1,"0");
            l.addAll(temp);
        }
    }

    @Override
    public void onNoticeClick(int position) {
        Intent intent=new Intent(getContext(), ViewNotice.class);
        intent.putExtra("id",l.get(position).id);
        intent.putExtra("category",l.get(position).name);
        startActivity(intent);
    }
}
