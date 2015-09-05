package devices.android.bootstrap.finder;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

/**
 * This utility finds the UI element from the given info
 */
public class AndroidElementFinder {

    /**
     * Finds an UI object from the given info
     *
     * @param device
     * @param byCategory
     * @param value
     * @return
     */
    public static UiObject2 findUIObject(UiDevice device, ByCategory byCategory, String value) {
        switch (byCategory) {
            case TEXT:
                return device.findObject(By.text(value));

            case DESC:
                return device.findObject(By.desc(value));

            case CLAZZ:
                return device.findObject(By.clazz(value));
        }
        return null;
    }
}
