package devices.android.bootstrap.server.netty;

import org.json.JSONException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import devices.android.bootstrap.AndroidCommand;
import devices.android.bootstrap.AndroidCommandExecutor;
import devices.android.bootstrap.AndroidCommandResult;
import devices.android.bootstrap.AndroidCommandType;
import devices.android.bootstrap.Bootstrap;
import devices.android.bootstrap.Logger;
import devices.android.bootstrap.WDStatus;
import devices.android.bootstrap.exceptions.CommandTypeException;

/**
 * Created by anands on 08-09-2015.
 */
public class HttpRequestHandler implements  HttpServlet{

    private AndroidCommandExecutor executor;
    private HttpServer httpServer;

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse)
            throws Exception{

        String res;
        String method = httpRequest.method();
        String uri = httpRequest.uri();
        String body = httpRequest.body();
        httpResponse.setContentType("application/json");

        if(method!="POST") {
            res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR,
                    "Only POST requests are supported by BootStrap server").toString();
            httpResponse.setContent(res);
            httpResponse.setStatus(500);
        }
        else if(null == body) {
            res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR,
                    "Only application/json mime-type is supported by BootStrap server").toString();
            httpResponse.setContent(res);
            httpResponse.setStatus(500);
        }
        else if (!"/execute-command".equalsIgnoreCase(uri)) {
            res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR,
                    "HTTP URI can only be http://host:port/execute-command").toString();
            httpResponse.setContent(res);
            httpResponse.setStatus(500);
        }
        else {
            String jsonBody = body;
            try {
                AndroidCommand cmd = new AndroidCommand(jsonBody);
                Logger.debug("Got command of type " + cmd.commandType().toString());
                res = runCommand(cmd);
                Logger.debug("Returning result: " + res);
                httpResponse.setContent(res);
                httpResponse.setStatus(200);
            } catch (CommandTypeException e) {
                res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR, e.getMessage())
                        .toString();
                httpResponse.setStatus(500);
            } catch (final JSONException e) {
                res = new AndroidCommandResult(WDStatus.UNKNOWN_ERROR,
                        "Error running and parsing command" + e.toString()).toString();
                httpResponse.setStatus(500);
            }
            httpResponse.setContent(res);
            httpResponse.end();
        }
    }

    private String runCommand(final AndroidCommand cmd) {
        AndroidCommandResult res;
        executor = new AndroidCommandExecutor();
        if (cmd.commandType() == AndroidCommandType.SHUTDOWN) {
            httpServer.stopServer();
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

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

}