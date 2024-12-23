package test.com.automation.listeners;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.bidi.log.LogLevel;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import test.com.automation.core.DriverBase;

import java.util.concurrent.atomic.AtomicInteger;

public class TestListener implements ITestListener {

    private static final AtomicInteger activeTests = new AtomicInteger(0);
    private static final Object lock = new Object();
    private static AppiumDriverLocalService service;

    @Override
    public void onStart(ITestContext context) {
        synchronized (lock) {
            if (activeTests.incrementAndGet() == 1) {
                System.out.println("Starting Appium server.");

                try {
                    AppiumServiceBuilder builder = new AppiumServiceBuilder();

                    // Configure Appium server options
                    builder.usingPort(4723)
                            .withArgument(GeneralServerFlag.BASEPATH, "/wd/hub")
                            .withArgument(GeneralServerFlag.LOG_LEVEL, LogLevel.INFO.toString());

                    // Build and start the service
                    service = builder.build();
                    service.start();

                    System.out.println("Appium server started.");

                } catch (Exception e) {
                    System.err.println("Failed to start Appium server: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        synchronized (lock) {
            if (activeTests.decrementAndGet() == 0) {
                if (service != null && service.isRunning()) {
                    System.out.println("Stopping Appium server.");
                    try {
                        service.stop();
                        System.out.println("Appium server stopped.");
                    } catch (Exception e) {
                        System.err.println("Failed to stop Appium server: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        DriverBase.tearDown();
    }

    @Override
    public void onTestStart(ITestResult result) {

        ITestContext context = result.getTestContext();
        String deviceName = context.getCurrentXmlTest().getParameter("deviceName");
        String udid = context.getCurrentXmlTest().getParameter("udid");
        String wdaLocalPort = context.getCurrentXmlTest().getParameter("wdaLocalPort");

        try {
            DriverBase.setUp(deviceName, udid, wdaLocalPort);
        } catch (Exception e) {
            System.err.println("Failed to set up driver: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Test started: " + result.getName());
        //System.out.println("Test started: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test passed: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test skipped: " + result.getName());
        System.out.println("Skip cause: " + result.getSkipCausedBy());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("Test failed but within success percentage: " + result.getName());
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        System.out.println("Test failed with timeout: " + result.getName());
    }
}
