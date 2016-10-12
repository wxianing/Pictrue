package com.meidp.pictures.utils;

import android.content.pm.PackageManager;

/**
 * Package： com.meidp.crmim.utils
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/8/25
 */
public class PermissionUtils {
    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
