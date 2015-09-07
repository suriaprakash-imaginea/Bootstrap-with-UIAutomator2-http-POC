package devices.android.bootstrap;

import android.support.test.uiautomator.UiObject2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;

import devices.android.bootstrap.exceptions.CommandTypeException;
import devices.android.bootstrap.finder.AndroidElementFinder;
import devices.android.bootstrap.finder.ByCategory;

/**
 * This proxy embodies the command that the handlers execute.
 *
 */
public class AndroidCommand {

    public Hashtable<String, Object> params;
    private JSONObject json;
    private AndroidCommandType cmdType;

    public AndroidCommand(final String jsonStr) throws JSONException,
            CommandTypeException {
        json = new JSONObject(jsonStr);
        setType(json.getString("cmd"));
        if(AndroidCommandType.ACTION == cmdType) {
            params = params();
        }
    }

    /**
     * Return the action string for this command.
     *
     * @return String
     * @throws JSONException
     */
    public String action() throws JSONException {
        if (isElementCommand()) {
            return json.getString("action").substring(8);
        }
        return json.getString("action");
    }

    public AndroidCommandType commandType() {
        return cmdType;
    }
/*
    *//**
     * Get the {@link UiObject2 destEl} this command is to operate on (must
     * provide the "desElId" parameter).
     *
     * @return {@link UiObject2}
     * @throws JSONException
     *//*
    public UiObject2 getDestElement() throws JSONException {
        String destElId = (String) params().get("destElId");
        return AndroidElementsHash.getInstance().getElement(destElId);
    }*/

    /**
     * Get the {@link UiObject2 element} this command is to operate on (must
     * provide the "elementId" parameter).
     *
     * @return {@link UiObject2}
     * @throws JSONException
     */
    public UiObject2 getElement() throws JSONException {
        ByCategory byCategory = ByCategory.valueOf((String) params.get("byCategory"));
        String value = (String) params.get("byValue");
        return AndroidElementFinder.findUIObject(Bootstrap.getDevice(), byCategory, value);
    }

    /**
     * Returns whether or not this command is on an element (true) or device
     * (false).
     *
     * @return boolean
     */
    public boolean isElementCommand() {
        if (cmdType == AndroidCommandType.ACTION) {
            try {
                return json.getString("action").startsWith("element:");
            } catch (final JSONException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Return a hash table of name, value pairs as arguments to the handlers
     * executing this command.
     *
     * @return Hashtable<String, Object>
     * @throws JSONException
     */
    private Hashtable<String, Object> params() throws JSONException {
        final JSONObject paramsObj = json.getJSONObject("params");
        final Hashtable<String, Object> newParams = new Hashtable<String, Object>();
        final Iterator<?> keys = paramsObj.keys();

        while (keys.hasNext()) {
            final String param = (String) keys.next();
            newParams.put(param, paramsObj.get(param));
        }
        return newParams;
    }

    /**
     * Set the command {@link AndroidCommandType type}
     *
     * @param stringType
     *          The string of the type (i.e. "shutdown" or "action")
     * @throws CommandTypeException
     */
    public void setType(final String stringType) throws CommandTypeException {
        if (stringType.equals("shutdown")) {
            cmdType = AndroidCommandType.SHUTDOWN;
        } else if (stringType.equals("action")) {
            cmdType = AndroidCommandType.ACTION;
        } else {
            throw new CommandTypeException("Got bad command type: " + stringType);
        }
    }
}
