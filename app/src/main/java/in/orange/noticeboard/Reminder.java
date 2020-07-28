package in.orange.noticeboard;

import android.app.Activity;
import android.os.Bundle;

public class Reminder extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_reminders);
    }
}
