package android.helper.entities;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM LocalNotification")
    List<LocalNotification> getAll();

    @Query("SELECT * FROM LocalNotification WHERE triggerTime < :currentTime")
    List<LocalNotification> loadTriggerable(long currentTime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(LocalNotification localNotification);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(LocalNotification... localNotifications);

    @Delete
    void delete(LocalNotification localNotification);

    @Query("DELETE FROM LocalNotification WHERE notificationId = :notificationId")
    void delete(int notificationId);

    @Query("SELECT EXISTS (SELECT * FROM LocalNotification WHERE notificationId = :notificationId LIMIT 1)")
    boolean isScheduled(int notificationId);

    @Query("DELETE from LocalNotification")
    void deleteAll();
}
