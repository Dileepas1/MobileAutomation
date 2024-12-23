package test.com.automation.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.com.automation.core.CommonSteps;
import test.com.automation.core.DriverBase;

import java.io.IOException;

import static test.com.automation.utils.OCRUtils.*;

public class TestCase extends CommonSteps {

    @Test()
    public void test() throws InterruptedException, IOException {
        System.out.println("Test One");

        launchApp("com.apple.Preferences");

        Thread.sleep(5000);

        //tapOnText("Developer");

        scrollToText("Siri&Search");
        tapOnSubstring("Siri&Search");

        scrollToText("About Siri");
        tapOnSubstring("About Siri");
        scrollToText("www.apple.com/in/privacy");
        tapOnSubstring("www.apple.com/in/privacy");
    }





}
