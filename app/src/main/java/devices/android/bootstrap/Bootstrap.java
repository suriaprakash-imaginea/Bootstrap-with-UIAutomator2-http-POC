package devices.android.bootstrap;

import android.support.test.uiautomator.UiDevice;
import android.test.InstrumentationTestCase;

import devices.android.bootstrap.server.v2_2.HttpServer;

public class Bootstrap extends InstrumentationTestCase {

    private static UiDevice device;
    public static boolean keepListening = true;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        device = UiDevice.getInstance(getInstrumentation());
        device.pressHome();
        new HttpServer(8080);
    }

    /**
     * This test will run forever till the server shutdown signal is received from the client
     */
    public void testUIActions() throws Exception {
        while (keepListening) {
            // Do Nothing. Just wait for requests from client through JSON-WP
        }
    }

    public static UiDevice getDevice() {
        return device;
    }


    /*device.wait(Until.hasObject(By.desc("Apps")), 3000);
        UiObject2 appsButton = device.findObject(By.desc("Apps"));
        appsButton.click();
        device.wait(Until.hasObject(By.text("Calculator")), 3000);
        UiObject2 calculatorApp = device.findObject(By.text("Calculator"));
        calculatorApp.click();*/

        /*// Wait till the Calculator's buttons are on the screen
        device.wait(Until.hasObject(By.text("9")), 3000);

        // Select the button for 9
        UiObject2 buttonNine = device.findObject(By.text("9"));
        buttonNine.click();

        // Select the button for +
        UiObject2 buttonPlus = device.findObject(By.desc("plus"));
        buttonPlus.click();

        // Press 9 again as we are calculating 9+9
        buttonNine.click();

        // Select the button for =
        UiObject2 buttonEquals = device.findObject(By.desc("equals"));
        buttonEquals.click();
        device.waitForIdle(3000);

        UiObject2 resultText = device.findObject(By.clazz("android.widget.EditText"));
        String result = resultText.getText();
        assertTrue(result.equals("18"));*/
}
