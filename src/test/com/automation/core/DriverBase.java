package test.com.automation.core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

public class DriverBase {

    private static final ThreadLocal<AppiumDriver> drivers = new ThreadLocal<>();

    public static void setUp(String deviceName, String udid, String wdaLocalPort) throws MalformedURLException {
        XCUITestOptions options = new XCUITestOptions();
        options.setPlatformName("iOS");
        options.setAutomationName("XCUITest");
        options.setUdid(udid);
        options.setDeviceName(deviceName);
        options.setWdaLocalPort(Integer.parseInt(wdaLocalPort));
        options.setBundleId("com.apple.mobilecal");
        options.setNewCommandTimeout(Duration.ofSeconds(60));

        IOSDriver driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
        driver.setLogLevel(Level.INFO);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));

        drivers.set(driver);

        System.out.println("Driver set up for device: " + deviceName + ", Session ID: " + driver.getSessionId());
    }

    public static AppiumDriver getDriver() {
        return drivers.get();
    }

    public static void tearDown() {
        AppiumDriver driver = DriverBase.getDriver();
        if (driver != null) {
            try {
                if (isSessionActive(driver)) {
                    System.out.println("Tearing down driver with Session ID: " + driver.getSessionId());
                    driver.quit();
                } else {
                    System.out.println("Session is no longer active. Skipping teardown.");
                }
            } catch (Exception e) {
                System.out.println("Error while tearing down driver: " + e.getMessage());
            } finally {
                drivers.remove();
                System.out.println("Driver torn down.");
            }
        }
    }

    public static boolean isSessionActive(AppiumDriver driver) {
        try {
            String sessionId = driver.getSessionId().toString();
            return sessionId != null && !sessionId.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}






































//package test.com.automation.core;
//
//import io.appium.java_client.AppiumDriver;
//import io.appium.java_client.ios.IOSDriver;
//import io.appium.java_client.ios.options.XCUITestOptions;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Dimension;
//import org.openqa.selenium.interactions.PointerInput;
//import org.openqa.selenium.interactions.Sequence;
//import org.testng.annotations.*;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.time.Duration;
//import java.util.List;
//import java.util.logging.Level;
//
//public class DriverBase {
//
//    private static final ThreadLocal<AppiumDriver> drivers = new ThreadLocal<>();
//
//    @BeforeMethod
//    @Parameters(value = {"deviceName", "udid", "wdaLocalPort"})
//    public void setUp(String deviceName, String udid, String wdaLocalPort) throws MalformedURLException {
//        XCUITestOptions options = new XCUITestOptions();
//        options.setPlatformName("iOS");
//        options.setAutomationName("XCUITest");
//        options.setUdid(udid);
//        options.setDeviceName(deviceName);
//        options.setWdaLocalPort(Integer.parseInt(wdaLocalPort));
//        options.setBundleId("com.apple.mobilecal");
//        options.setNewCommandTimeout(Duration.ofSeconds(60));
//
//        IOSDriver driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
//        driver.setLogLevel(Level.INFO);
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
//
//        drivers.set(driver);
//
//        System.out.println("Driver set up for device: " + deviceName + ", Session ID: " + driver.getSessionId());
//
//    }
//
//    public static AppiumDriver getDriver() {
//        return drivers.get();
//    }
//
//    public boolean isSessionActive(AppiumDriver driver) {
//        try {
//            // Attempt to get the session ID
//            String sessionId = driver.getSessionId().toString();
//            // Check if the session is active
//            return sessionId != null && !sessionId.isEmpty();
//        } catch (Exception e) {
//            // If an exception occurs, the session might be invalid
//            return false;
//        }
//    }
//
//
//    @AfterMethod
//    public void tearDown() {
//        AppiumDriver driver = DriverBase.getDriver();
//        if (driver != null) {
//            try {
//                if (isSessionActive(driver)) {
//                    System.out.println("Tearing down driver with Session ID: " + driver.getSessionId());
//                    driver.quit();
//                } else {
//                    System.out.println("Session is no longer active. Skipping teardown.");
//                }
//            } catch (Exception e) {
//                System.out.println("Error while tearing down driver: " + e.getMessage());
//            } finally {
//                drivers.remove(); // Ensure to remove the driver from thread-local storage
//                System.out.println("Driver torn down.");
//            }
//        }
//    }
//
//    public void launchApp(String appID) {
//        ((IOSDriver)getDriver()).activateApp(appID);
//    }
//
//    public boolean isElementPresent(String locator) {
//        try {
//            ((IOSDriver)getDriver()).findElement(By.xpath(locator)).isDisplayed();
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public void scrollToElement(String locator, int... noOfSwipes) {
//
//        int swipeCount = 0;
//
//        if (noOfSwipes.length == 0) {
//            swipeCount = 10;
//        } else {
//            swipeCount = noOfSwipes[0];
//        }
//
//        Dimension size = ((IOSDriver)getDriver()).manage().window().getSize();
//
//        int width = size.getWidth();
//        int height = size.getHeight();
//
//        int startX = width / 2;
//        int startY = (int) (height * 0.70);
//        int endY = (int) (height * 0.60);
//
//        PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "finger");
//
//        Sequence scrollSequence = new Sequence(input, 1);
//        scrollSequence.addAction(input.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
//        scrollSequence.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
//        scrollSequence.addAction(input.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), startX, endY));
//        scrollSequence.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
//
//        while (isElementPresent(locator) && swipeCount > 0) {
//            ((IOSDriver)getDriver()).perform(List.of(scrollSequence));
//            swipeCount--;
//        }
//    }
//}
