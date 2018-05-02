package abdulmuqeeth.uic.com.balanceservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;

import abdulmuqeeth.uic.com.balancecommon.Balance;
import abdulmuqeeth.uic.com.balancecommon.DailyCash;

public class BalanceImpl extends Service {

    private DatabaseHelper dbHelper = new DatabaseHelper(this);

    Context mContext = this;
    private final Balance.Stub mBinder = new Balance.Stub() {
        @Override
        public boolean createDatabase() throws RemoteException {
            return dbHelper.loadData();
            //return new DatabaseHelper(mContext).loadData();
        }

        @Override
        public DailyCash[] dailyCash(int day, int month, int year, int range) throws RemoteException {
            return dbHelper.getData(day,month,year,range);
            //return new DatabaseHelper(mContext).getData(day, month, year, range);
        }
    };

    public BalanceImpl() {
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
