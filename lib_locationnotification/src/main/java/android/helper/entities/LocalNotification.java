package android.helper.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.DrawableRes;

import com.google.gson.Gson;

import java.io.Serializable;

@Entity
public class LocalNotification implements Serializable {

    private static Gson mGson;

    public static void initGson() {
        mGson = new Gson();
    }

    public static LocalNotification fromTag(String jsonString) {
        if (mGson == null) {
            throw new RuntimeException("'NotificationHelper' not initialised");
        }
        try {
            return mGson.fromJson(jsonString, LocalNotification.class);
        } catch (Exception ignored) {
        }
        return null;
    }

    public String toTag() {
        if (mGson == null) {
            throw new RuntimeException("'NotificationHelper' not initialised");
        }
        return mGson.toJson(this);
    }

    @PrimaryKey
    public int notificationId;

    @ColumnInfo(name = "channelId")
    public String channelId;

    @ColumnInfo(name = "textTitle")
    public String textTitle;

    @ColumnInfo(name = "textContent")
    public String textContent;

    @ColumnInfo(name = "smallIcon")
    @DrawableRes
    public int smallIcon;

    @ColumnInfo(name = "largeIcon")
    @DrawableRes
    public int largeIcon;

    @ColumnInfo(name = "triggerTime")
    public long triggerTime;

    @ColumnInfo(name = "delay")
    public long delay;

    @ColumnInfo(name = "repeatCount")
    public int repeatCount = -1;

    @ColumnInfo(name = "isRepeat")
    public boolean isRepeat = false;

    @Override
    public boolean equals(Object obj) {
        try {
            return this.notificationId == ((LocalNotification) obj).notificationId;
        } catch (Exception e) {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return notificationId + "-" + textTitle + "\n" + textContent;
    }
}
