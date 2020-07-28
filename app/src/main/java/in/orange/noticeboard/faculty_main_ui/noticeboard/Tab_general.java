package in.orange.noticeboard.faculty_main_ui.noticeboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import in.orange.noticeboard.CustomRecyclerAdapter;
import in.orange.noticeboard.DisplayNotice;
import in.orange.noticeboard.Notice;
import in.orange.noticeboard.NoticeDatabase;
import in.orange.noticeboard.R;
import in.orange.noticeboard.ViewNotice;

public class Tab_general extends Fragment implements CustomRecyclerAdapter.OnNoticeClick {

    private CustomRecyclerAdapter myadapter;

    private RecyclerView myRecyclerView;
    private SwipeRefreshLayout pullToRefresh;
    private List<DisplayNotice> l;
    private List<DisplayNotice> lSearch;
    private List<DisplayNotice> temp;
    private FrameLayout.LayoutParams params;

    private EditText search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceStste) {
        View root = inflater.inflate(R.layout.tab_general, container, false);

        search=(EditText)root.findViewById(R.id.search);

        lSearch=new ArrayList<DisplayNotice>();
        l=NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices("General", -1,"0");
        if(l==null)
        {
            l=new ArrayList<DisplayNotice>();
        }
        temp=new ArrayList<DisplayNotice>();
        temp.addAll(l);

        myadapter=new CustomRecyclerAdapter(l,this);
        myRecyclerView=(RecyclerView)root.findViewById(R.id.listview);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setAdapter(myadapter);
        myadapter.notifyDataSetChanged();

        pullToRefresh=root.findViewById(R.id.swiperefreshgeneral);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    List<DisplayNotice> ltemp = NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices("General", Integer.parseInt(temp.get(0).id),"0");
                    l.addAll(0,ltemp);
                }catch(Exception e){
                    try{
                        l.addAll(NoticeDatabase.getNoticeDatabaseInstance(getContext()).getAllNotices("General", -1,"0"));
                    }catch(Exception ee){}
                }
                myadapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });

        params=(FrameLayout.LayoutParams)pullToRefresh.getLayoutParams();
        params.topMargin=0;
        pullToRefresh.setLayoutParams(params);
        pullToRefresh.requestLayout();


        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search.setVisibility(View.VISIBLE);
                params.topMargin=140;
                pullToRefresh.setLayoutParams(params);
                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after){
                        lSearch.clear();
                        for(int i=0;i<temp.size();i++){
                            if(temp.get(i).subject.toLowerCase().contains(search.getText().toString().toLowerCase())){
                                lSearch.add(temp.get(i));
                            }
                        }
                        l.clear();
                        l.addAll(lSearch);
                        myadapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        lSearch.clear();
                        for(int i=0;i<temp.size();i++){
                            if(temp.get(i).subject.toLowerCase().contains(search.getText().toString().toLowerCase())){
                                lSearch.add(temp.get(i));
                            }
                        }
                        l.clear();
                        l.addAll(lSearch);
                        myadapter.notifyDataSetChanged();
                    }
                    @Override
                    public void afterTextChanged(Editable s){
                        lSearch.clear();
                        for(int i=0;i<temp.size();i++){
                            if(temp.get(i).subject.toLowerCase().contains(search.getText().toString().toLowerCase())){
                                lSearch.add(temp.get(i));
                            }
                        }
                        l.clear();
                        l.addAll(lSearch);
                        myadapter.notifyDataSetChanged();
                    }
                });
            }
        });

        return root;
    }

    @Override
    public void onNoticeClick(int position) {
        Intent intent=new Intent(getContext(), ViewNotice.class);
        intent.putExtra("id",l.get(position).id);
        intent.putExtra("category",l.get(position).name);
        startActivity(intent);
        params.topMargin=0;
        pullToRefresh.setLayoutParams(params);
        l.clear();
        l.addAll(temp);
        search.setText("");
        search.setVisibility(View.INVISIBLE);
    }
}