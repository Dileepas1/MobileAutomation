package test.com.automation.core;

import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.io.FileHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class CommonSteps {

    public static void launchApp(String appID) {
        waitOnPage(3);
        ((IOSDriver)DriverBase.getDriver()).terminateApp(appID);
        waitOnPage(3);
        ((IOSDriver)DriverBase.getDriver()).activateApp(appID);
    }

    public static void waitOnPage(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isElementPresent(String locator) {
        try {
            DriverBase.getDriver().findElement(By.xpath(locator)).isDisplayed();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void scrollToElement(String locator, int... noOfSwipes) {
        int swipeCount = (noOfSwipes.length == 0) ? 10 : noOfSwipes[0];

        Dimension size = DriverBase.getDriver().manage().window().getSize();
        int width = size.getWidth();
        int height = size.getHeight();

        int startX = width / 2;
        int startY = (int) (height * 0.70);
        int endY = (int) (height * 0.68);

        PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scrollSequence = new Sequence(input, 1);
        scrollSequence.addAction(input.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        scrollSequence.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scrollSequence.addAction(input.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), startX, endY));
        scrollSequence.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        while (!isElementPresent(locator) && swipeCount > 0) {
            DriverBase.getDriver().perform(List.of(scrollSequence));
            swipeCount--;
        }
    }



}
