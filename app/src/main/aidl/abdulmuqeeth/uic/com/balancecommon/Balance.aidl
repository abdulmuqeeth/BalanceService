package abdulmuqeeth.uic.com.balancecommon;

import java.util.List;
import abdulmuqeeth.uic.com.balancecommon.DailyCash;

// Declare any non-default types here with import statements

interface Balance {
    boolean createDatabase();
    List<DailyCash> dailyCash(int day, int month, int year , int range);
}
