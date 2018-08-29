package info.ankurpandya.localnotificaion.demo.entities;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM mynotification")
    List<MyNotification> getAll();

    @Query("SELECT * FROM mynotification WHERE triggerTime < :currentTime")
    List<MyNotification> loadTriggerable(long currentTime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(MyNotification mynotification);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(MyNotification... mynotifications);

    @Delete
    void delete(MyNotification myNotification);

    @Query("DELETE FROM mynotification WHERE notificationId = :notificationId")
    void delete(int notificationId);

    @Query("SELECT EXISTS (SELECT * FROM mynotification WHERE notificationId = :notificationId LIMIT 1)")
    boolean isScheduled(int notificationId);

    @Query("DELETE from mynotification")
    void deleteAll();
}
