package devices.android.bootstrap.handler;

import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;

import devices.android.bootstrap.AndroidCommand;
import devices.android.bootstrap.AndroidCommandResult;
import devices.android.bootstrap.CommandHandler;
import devices.android.bootstrap.WDStatus;

import static  devices.android.bootstrap.handler.ElementDescriptorUtil.*;

/*
 * @param command The {@link AndroidCommand}
 *
 * @return {@link AndroidCommandResult}
 *
 * @throws JSONException
 *
 * @see io.appium.android.bootstrap.CommandHandler#execute(io.appium.android.
 * bootstrap.AndroidCommand)
 */
public class Click extends CommandHandler {

    @Override
    public AndroidCommandResult execute(AndroidCommand command) throws JSONException {
        try {
            UiObject2 element = command.getElement();
            element.click();
            return getSuccessResult(true);
        } catch (final NullPointerException e) {
            return getErrorResult("Element " + getElementDescription(command) + " not found in the page");
        } catch (final Exception e) { // handle NullPointerException
            return getErrorResult("Unknown error while clicking element " + getElementDescription(command));
        }
    }
}
