package android.helper.entities;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {LocalNotification.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    static final Migration MIGRATION_0_1 = new Migration(0, 1) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL(
//                    "CREATE TABLE `userdata`" +
//                            " (aid TEXT, modelNum TEXT, appversion TEXT, lat TEXT, lng TEXT, postcode TEXT, totaldiskspace TEXT, freediskspace TEXT," +
//                            " PRIMARY KEY(aid))"
//            );
        }
    };

    public static AppDatabase getInstance(Context context) {
        return Room
                .databaseBuilder(
                        context,
                        AppDatabase.class,
                        "notification_db"
                )
                .allowMainThreadQueries()
                .addMigrations(
                        MIGRATION_0_1
                )
                .build();
    }

    public abstract NotificationDao notificationDao();
}