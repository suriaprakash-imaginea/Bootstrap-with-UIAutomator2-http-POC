package devices.android.bootstrap.server.v2_2;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import devices.android.bootstrap.AndroidCommand;
import devices.android.bootstrap.AndroidCommandExecutor;
import devices.android.bootstrap.AndroidCommandResult;
import devices.android.bootstrap.AndroidCommandType;
import devices.android.bootstrap.Bootstrap;
import devices.android.bootstrap.Logger;
import devices.android.bootstrap.WDStatus;
import devices.android.bootstrap.exceptions.CommandTypeException;
import devices.android.bootstrap.server.v2_2.NanoHTTPD;
import devices.android.bootstrap.server.v2_2.NanoHTTPD.Response.Status;



/**
 * Created by suria on 4/9/15.
 */
public class HttpServer extends NanoHTTPD {

    private AndroidCommandExecutor executor;

    public HttpServer(int port) throws IOException {
        super(port);
        this.start();
        executor = new AndroidCommandExecutor();
    }

    @Override
    public Response serve(IHTTPSession session) {
        super.serve(session);
        String res;
        Map<String,String> params = session.getParms();
        Method method = session.getMethod();
        String uri = session.getUri();

        if(Method.POST != method) {
            res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR,
                    "Only POST requests are supported by BootStrap server").toString();
            return newFixedLengthResponse(Status.INTERNAL_ERROR, "application/json", res);
        }

        if(null == params.get("jsonBody")) {
            res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR,
                    "Only application/json mime-type is supported by BootStrap server").toString();
            return newFixedLengthResponse(Status.INTERNAL_ERROR, "application/json", res);
        }

        if(!"/execute-command".equalsIgnoreCase(uri)) {
            res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR,
                    "HTTP URI can only be http://host:port/execute-command").toString();
            return newFixedLengthResponse(Status.INTERNAL_ERROR, "application/json", res);
        }

        String jsonBody = params.get("jsonBody");
        try {
            AndroidCommand cmd = new AndroidCommand(jsonBody);
            Logger.debug("Got command of type " + cmd.commandType().toString());
            res = runCommand(cmd);
            Logger.debug("Returning result: " + res);
            return newFixedLengthResponse(Status.OK, "application/json", res);
        } catch (CommandTypeException e) {
            res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR, e.getMessage())
                    .toString();
        } catch (final JSONException e) {
            res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR,
                    "Error running and parsing command" + e.toString()).toString();
        }

        return newFixedLengthResponse(Status.INTERNAL_ERROR, "application/json", res);
    }

    /**
     * This method delegates the command from the json body
     *
     * @param cmd
     *     AndroidCommand
     * @return Result
     */
    private String runCommand(final AndroidCommand cmd) {
        AndroidCommandResult res;
        if (cmd.commandType() == AndroidCommandType.SHUTDOWN) {
            Bootstrap.keepListening = false;
            res = new AndroidCommandResult(WDStatus.SUCCESS, "OK, shutting down");
        } else if (cmd.commandType() == AndroidCommandType.ACTION) {
            try {
                res = executor.execute(cmd);
            } catch (final NoSuchElementException e) {
                res = new AndroidCommandResult(WDStatus.NO_SUCH_ELEMENT, e.getMessage());
            } catch (final Exception e) { // Here we catch all possible exceptions and return a JSON Wire Protocol UnknownError
                // This prevents exceptions from halting the bootstrap app
                Logger.debug("Command returned error:" + e);
                res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR, e.getMessage());
            }
        } else {
            // this code should never be executed, here for future-proofing
            res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR,
                    "Unknown command type, could not execute!");
        }
        return res.toString();
    }
}
