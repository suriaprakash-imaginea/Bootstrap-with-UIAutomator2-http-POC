package devices.android.bootstrap.handler;

import devices.android.bootstrap.AndroidCommand;

/**
 * Created by suria on 5/9/15.
 */
public class ElementDescriptorUtil {

    public static String getElementDescription(AndroidCommand cmd) {
        return "with " + cmd.params.get("byCategory") + " '" + cmd.params.get("byValue") + "'";
    }
}
