package in.orange.noticeboard.faculty_main_ui.archives;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import in.orange.noticeboard.CustomRecyclerAdapter;
import in.orange.noticeboard.DisplayNotice;
import in.orange.noticeboard.NoticeDatabase;
import in.orange.noticeboard.R;
import in.orange.noticeboard.ViewNotice;

public class Tab_central extends Fragment implements CustomRecyclerAdapter.OnNoticeClick{

    private CustomRecyclerAdapter myadapter;
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
        View view = inflater.inflate(R.layout.tab_general, container, false);

        l=NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices("Central", -1,"1");
        if(l==null)
        {
            l=new ArrayList<DisplayNotice>();
        }
        myadapter=new CustomRecyclerAdapter(l,this);
        myRecyclerView=(RecyclerView) view.findViewById(R.id.listview);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setAdapter(myadapter);
        myadapter.notifyDataSetChanged();

        pullToRefresh=view.findViewById(R.id.swiperefreshgeneral);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    List<DisplayNotice> temp = NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices("Central", Integer.parseInt(l.get(0).id),"1");
                    l.addAll(0,temp);
                }catch(Exception e){
                    try{
                        l.addAll(NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices("Central", -1,"1"));
                    }catch(Exception ee){}
                }
                myadapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onNoticeClick(int position) {
        Intent intent=new Intent(getContext(), ViewNotice.class);
        intent.putExtra("id",l.get(position).id);
        intent.putExtra("category",l.get(position).name);
        startActivity(intent);
    }
}