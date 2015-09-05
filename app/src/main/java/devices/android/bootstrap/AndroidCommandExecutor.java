package devices.android.bootstrap;

import org.json.JSONException;

import java.util.HashMap;

import devices.android.bootstrap.handler.Click;

/**
 * Created by suria on 5/9/15.
 */
public class AndroidCommandExecutor {

    private static HashMap<String, CommandHandler> map = new HashMap<String, CommandHandler>();

    static {
        map.put("click", new Click());
    }

    /**
     * Gets the handler out of the map, and executes the command.
     *
     * @param command
     *          The {@link AndroidCommand}
     * @return {@link AndroidCommandResult}
     */
    public AndroidCommandResult execute(final AndroidCommand command) {
        try {
            Logger.debug("Got command action: " + command.action());

            if (map.containsKey(command.action())) {
                return map.get(command.action()).execute(command);
            } else {
                return new AndroidCommandResult(WDStatus.UNKNOWN_COMMAND,
                        "Unknown command: " + command.action());
            }
        } catch (final JSONException e) {
            Logger.error("Could not decode action/params of command");
            return new AndroidCommandResult(WDStatus.JSON_DECODER_ERROR,
                    "Could not decode action/params of command, please check format!");
        }
    }
}
