package in.orange.noticeboard;

public class Notice{
    public String subject,summary,data,release_date,end_date,file;
    public Notice(String subject,String summary,String release_date,String end_date,String file,String data)
    {
        this.subject=subject;
        this.summary=summary;
        this.release_date=release_date;
        this.end_date=end_date;
        this.file=file;
        this.data=data;
    }
    public Notice()
    {}
}
