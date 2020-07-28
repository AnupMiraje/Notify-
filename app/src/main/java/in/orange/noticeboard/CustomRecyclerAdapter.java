package in.orange.noticeboard;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> implements Filterable {
    private List<DisplayNotice> l;
    private List<DisplayNotice> searchList;

    private OnNoticeClick onnoticeClick;

    public CustomRecyclerAdapter(List<DisplayNotice> l,OnNoticeClick onnoticeClick)
    {
        this.l=l;
        this.searchList=new ArrayList<DisplayNotice>(l);
        this.onnoticeClick=onnoticeClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.notice, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem,onnoticeClick);
        return viewHolder;
    }

    public String cal(long l1)
    {
        Date d2=NoticeBoard.gettime();
        long l2=d2.getTime();
        Date d3=new Date(l2-l1-19800000);
        Calendar c=Calendar.getInstance();
        c.setTime(d3);
        int year=c.get(Calendar.YEAR)-1970;
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH)-1;
        int hrs=c.get(Calendar.HOUR_OF_DAY);
        int min=c.get(Calendar.MINUTE);
        if(year>1)
        {
            return year+" years ago";
        }
        else if(year==1)
        {
            return year+" year ago";
        }
        else if(month>1)
        {
            return month+" months ago";
        }
        else if(month==1)
        {
            return year+" month ago";
        }
        else if(day>1)
        {
            return day+" days ago";
        }
        else if(day==1)
        {
            return day+" day ago";
        }
        else if(hrs>1)
        {
            return hrs+" hrs ago";
        }
        else if(hrs==1)
        {
            return hrs+" hr ago";
        }
        else if(min>1)
        {
            return min+" mins ago";
        }
        else if(min==1)
        {
            return min+" min ago";
        }
        return "0 mins ago";
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        holder.subject.setText(l.get(position).subject);
        holder.summary.setText(l.get(position).summary);
        holder.rel_date.setText(sdf.format(new Date(Long.parseLong(l.get(position).release_date))));
        holder.rel_time.setText(cal(Long.parseLong(l.get(position).release_date)));
        if(l.get(position).pin.equals("0")) {
            holder.pin.setImageResource(R.drawable.notice_pin_red);
        }
        else{
            holder.pin.setImageResource(R.drawable.notice_pin_green);
        }
        holder.pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(l.get(position).pin.equals("0")){
                    l.get(position).pin="1";
                    holder.pin.setImageResource(R.drawable.notice_pin_green);
                    NoticeDatabase.getNoticeDatabaseInstance(holder.pin.getContext()).updatepin(l.get(position).name,l.get(position).id,"1");
                    Toast.makeText(holder.pin.getContext(), "Notice pinned", Toast.LENGTH_LONG).show();
                }
                else{
                    l.get(position).pin="0";
                    holder.pin.setImageResource(R.drawable.notice_pin_red);
                    NoticeDatabase.getNoticeDatabaseInstance(holder.pin.getContext()).updatepin(l.get(position).name,l.get(position).id,"0");
                    Toast.makeText(holder.pin.getContext(), "Notice unpinned", Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(v.getContext(),CreateReminders.class);
                //intent.putExtra("subject",l.get(position).subject);
                //intent.putExtra("department",l.get(position).name);
                //holder.download.getContext().startActivity(intent);
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent=new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,l.get(position).subject);
                sendIntent.setType("text/plain");
                Intent.createChooser(sendIntent,"Share via");
                v.getContext().startActivity(sendIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return l.size();
    }

    @Override
    public Filter getFilter() {
        return noticeFilter;
    }

    private Filter noticeFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DisplayNotice> filterList=new ArrayList<DisplayNotice>();
            if(constraint==null || constraint.length()==0){
                filterList.addAll(searchList);
            }else{
                String filterPattern=constraint.toString().toLowerCase().trim();
                for(DisplayNotice item: searchList){
                    if(item.subject.toLowerCase().contains(filterPattern)){
                        filterList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            l.clear();
            l.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView subject,summary,rel_date,rel_time;
        public ImageView pin,download,share;
        OnNoticeClick onnoticeClick;

        public ViewHolder(View ItemView,OnNoticeClick onnoticeClick)
        {
            super(ItemView);
            this.subject=(TextView)ItemView.findViewById(R.id.subject);
            this.summary=(TextView)ItemView.findViewById(R.id.summary);
            this.rel_date=(TextView)ItemView.findViewById(R.id.date);
            this.rel_time=(TextView)ItemView.findViewById(R.id.time);
            this.pin=(ImageView)ItemView.findViewById(R.id.pin);
            this.download=(ImageView)ItemView.findViewById(R.id.download);
            this.share=(ImageView)ItemView.findViewById(R.id.share);
            this.onnoticeClick=onnoticeClick;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onnoticeClick.onNoticeClick(getAdapterPosition());
        }
    }

    public interface OnNoticeClick{
        void onNoticeClick(int position);
    }
}
