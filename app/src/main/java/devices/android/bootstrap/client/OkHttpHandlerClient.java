package devices.android.bootstrap.client;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import static java.util.concurrent.TimeUnit.SECONDS;

public  class OkHttpHandlerClient {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static final String baseUrl = "http://localhost:8080";

    static {
        final int timeout = 15 * 1000;
        client.setConnectTimeout(timeout, SECONDS);
        client.setReadTimeout(timeout, SECONDS);
        client.setWriteTimeout(timeout, SECONDS);
    }

    public static void testCalculator(final String path) throws InterruptedException{
        String[] actions = {"5", "2","+","3","5"};
        String result="";
        for (int i = 0; i <actions.length ; i++) {
            Request request = new Request.Builder()
                    .url(baseUrl + path)
                    .post(RequestBody.create(JSON, "{\"cmd\": \"action\",\"action\": \"click\",\"params\": {\"byCategory\": \"TEXT\",\"byValue\": \""+actions[i]+"\"}}"))
                    .addHeader("Accept", "application/json")
                    .build();
            result=execute(request);
            Thread.sleep(2000);
        }
        if(result.contains("status\":13"))
            System.out.print("Open the Calculator app for testing");
    }

    private static String execute(Request request) {
        String result = "";
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            System.out.print("Post \"" + request.urlString() + "\" failed. " + e.getMessage());
        }
        return result;
    }

    public static void main(String a[]) throws  InterruptedException
    {
        testCalculator("/execute-command"); //execute after opening the calculator app on device
    }
}