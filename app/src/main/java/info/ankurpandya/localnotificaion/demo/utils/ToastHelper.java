package info.ankurpandya.localnotificaion.demo.utils;

import android.support.annotation.StringRes;

public interface ToastHelper {
    void showToast(String message);

    void showToast(@StringRes int messageId);
}
