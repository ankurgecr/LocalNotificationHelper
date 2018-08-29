package info.ankurpandya.localnotificaion.demo.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.DrawableRes;

import java.io.Serializable;

@Entity
public class MyNotification implements Serializable {

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

}
