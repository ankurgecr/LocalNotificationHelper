package android.helper.entities;

import android.support.annotation.DrawableRes;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LocalNotification implements Serializable {

    //region: gson stuff

    public static LocalNotification fromTag(String jsonString) {
        try {
            return new Gson().fromJson(jsonString, LocalNotification.class);
        } catch (Exception ignored) {
        }
        return null;
    }

    public String toTag() {
        return new Gson().toJson(this);
    }

    //endregion

    @SerializedName("f")
    public int notificationId;

    @SerializedName("c")
    public String channelId;

    @SerializedName("t")
    public String textTitle;

    @SerializedName("n")
    public String textContent;

    @SerializedName("s")
    @DrawableRes
    public int smallIcon;

    @SerializedName("l")
    @DrawableRes
    public int largeIcon;

    @SerializedName("m")
    public long triggerTime;

    @SerializedName("d")
    public long triggerDelay;

    @SerializedName("p")
    public long repeatDelay;

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
