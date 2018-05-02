package abdulmuqeeth.uic.com.balanceservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;

import abdulmuqeeth.uic.com.balancecommon.BalanceService;
import abdulmuqeeth.uic.com.balancecommon.DailyCash;

public class BalanceServiceImpl extends Service {

    private DatabaseHelper dbHelper = new DatabaseHelper(this);

    Context mContext = this;
    private final BalanceService.Stub mBinder = new BalanceService.Stub() {

        @Override
        public boolean createDatabase() throws RemoteException {
            return dbHelper.loadData();
        }

        @Override
        public DailyCash[] dailyCash(int day, int month, int year, int range) throws RemoteException {
            return dbHelper.getData(day,month,year,range);
        }
    };

    public BalanceServiceImpl() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        System.out.println("Destroyed DB");
        dbHelper.getWritableDatabase().close();
        dbHelper.deleteDatabase();
        super.onDestroy();

    }
}
