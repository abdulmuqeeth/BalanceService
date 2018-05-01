package abdulmuqeeth.uic.com.balanceservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

public class BalanceImpl extends Service {

    private DatabaseHelper dbHelper;

    public BalanceImpl() {
    }

    private boolean createDatabase() {
        return dbHelper.loadData();
    }

    private List<DailyCash> dailyCash(int day, int month, int year , int range) {
        return dbHelper.getData(day, month, year, range);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
