package devices.android.bootstrap;

import org.json.JSONException;

/**
 * Base class for all handlers.
 *
 */
public abstract class CommandHandler {

    /**
     * Abstract method that handlers must implement.
     *
     * @param command A {@link AndroidCommand}
     * @return {@link AndroidCommandResult}
     * @throws JSONException
     */
    public abstract AndroidCommandResult execute(final AndroidCommand command)
            throws JSONException;

    /**
     * Returns a generic unknown error message along with your own message.
     *
     * @param msg
     * @return {@link AndroidCommandResult}
     */
    protected AndroidCommandResult getErrorResult(final String msg) {
        return new AndroidCommandResult(WDStatus.UNKNOWN_ERROR, msg);
    }

    /**
     * Returns success along with the payload.
     *
     * @param value
     * @return {@link AndroidCommandResult}
     */
    protected AndroidCommandResult getSuccessResult(final Object value) {
        return new AndroidCommandResult(WDStatus.SUCCESS, value);
    }

}

