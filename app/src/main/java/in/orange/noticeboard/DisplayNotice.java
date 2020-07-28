package in.orange.noticeboard;

public class DisplayNotice{
    public String id,name,subject,summary,data,release_date,end_date,file,pin;
    public DisplayNotice(String id,String name,String subject,String summary,String release_date,String end_date,String file,String data,String pin)
    {
        this.id=id;
        this.name=name;
        this.subject=subject;
        this.summary=summary;
        this.release_date=release_date;
        this.end_date=end_date;
        this.file=file;
        this.data=data;
        this.pin=pin;
    }
    public DisplayNotice()
    {}
}
