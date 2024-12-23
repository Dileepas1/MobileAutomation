package test.com.automation.utils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;
import net.sourceforge.tess4j.Word;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import test.com.automation.core.DriverBase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class OCRUtils {

    private static final String TESSERACT_LIB_PATH = "/opt/homebrew/Cellar/tesseract/5.4.1/lib";
    private static final String TESSERACT_DATA_PATH = "Tess4J/tessdata";
    private static final String LANGUAGE = "eng";
    private static final String SCREENSHOT_PATH = "screenshot.png";

    public static void tapOnText(String targetText) {
        ITesseract iTesseract = initTess();

        BufferedImage img = takeScreenshot(SCREENSHOT_PATH);
        if (img == null) {
            System.err.println("Failed to read screenshot image.");
            return;
        }

        double[] scaleFactors = getScaleFactors(img);
        if (scaleFactors == null) {
            System.err.println("Failed to calculate scaling factors.");
            return;
        }

       // List<Word> words = iTesseract.getWords(img, TessPageIteratorLevel.RIL_BLOCK);
        List<Word> words = iTesseract.getWords(img, TessPageIteratorLevel.RIL_WORD);

        System.out.println(words);
        Word targetWord = findTextCoordinates(words, targetText);

        if (targetWord != null) {
            tapOnCoordinates(targetWord, scaleFactors);
        } else {
            System.out.println("Text: '" + targetText + "' not found.");
        }
    }

    private static ITesseract initTess() {
        System.setProperty("jna.library.path", TESSERACT_LIB_PATH);
        ITesseract iTesseract = new Tesseract();
        iTesseract.setDatapath(TESSERACT_DATA_PATH);
        iTesseract.setLanguage(LANGUAGE);
        return iTesseract;
    }

    private static double[] getScaleFactors(BufferedImage img) {
        try {
            Dimension viewportSize = ((RemoteWebDriver) DriverBase.getDriver()).manage().window().getSize();
            double scaleX = (double) viewportSize.getWidth() / img.getWidth();
            double scaleY = (double) viewportSize.getHeight() / img.getHeight();
            return new double[]{scaleX, scaleY};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Word findTextCoordinates(List<Word> words, String targetText) {
        for (Word word : words) {
            if (word.getText().contains(targetText)) {
                return word;
            }
        }
        return null;
    }

    private static void tapOnCoordinates(Word word, double[] scaleFactors) {
        int adjustedX = (int) (word.getBoundingBox().x * scaleFactors[0]);
        int adjustedY = (int) (word.getBoundingBox().y * scaleFactors[1]);
        int adjustedWidth = (int) (word.getBoundingBox().width * scaleFactors[0]);
        int adjustedHeight = (int) (word.getBoundingBox().height * scaleFactors[1]);

        int clickX = adjustedX + adjustedWidth / 2;
        int clickY = adjustedY + adjustedHeight / 2;

        System.out.println("Text: " + word.getText() + " found at adjusted coordinates:");
        System.out.println("X: " + clickX + ", Y: " + clickY);

        PointerInput pointerInput = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence touchSequence = new Sequence(pointerInput, 0)
                .addAction(pointerInput.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), clickX, clickY))
                .addAction(pointerInput.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(pointerInput.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        DriverBase.getDriver().perform(Collections.singletonList(touchSequence));
    }

    public static BufferedImage takeScreenshot(String filePath) {
        File destFile = new File(filePath);

        try {
            File srcFile = ((TakesScreenshot) DriverBase.getDriver()).getScreenshotAs(OutputType.FILE);

            if (destFile.exists() && !destFile.delete()) {
                System.err.println("Failed to delete existing file: " + filePath);
                return null;
            }

            FileHandler.copy(srcFile, destFile);
            System.out.println("Screenshot saved at: " + filePath);

            return ImageIO.read(destFile);

        } catch (IOException e) {
            System.err.println("Failed to save or read the screenshot: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    public static void scrollToText(String targetText) {
        ITesseract iTesseract = initTess();

        boolean isTextFound = false;
        int maxScrolls = 40;
        int scrollAttempts = 0;

        while (!isTextFound && scrollAttempts < maxScrolls) {
            BufferedImage img = takeScreenshot(SCREENSHOT_PATH);
            if (img == null) {
                System.err.println("Failed to read screenshot image.");
                return;
            }

            double[] scaleFactors = getScaleFactors(img);
            if (scaleFactors == null) {
                System.err.println("Failed to calculate scaling factors.");
                return;
            }

            List<Word> words = iTesseract.getWords(img, TessPageIteratorLevel.RIL_TEXTLINE);
            System.out.println(words);
            Word targetWord = findTextCoordinates(words, targetText);

            if (targetWord != null) {
                // Text found, stop scrolling
                isTextFound = true;
                System.out.println("Text: '" + targetText + "' found, stopping scroll.");
                // Optionally: Tap on text or highlight it
                //tapOnCoordinates(targetWord, scaleFactors);
            } else {
                System.out.println("Text: '" + targetText + "' not found, scrolling down.");
                scrollDown();
                scrollAttempts++;
            }
        }

        if (!isTextFound) {
            System.out.println("Text: '" + targetText + "' not found after " + maxScrolls + " scroll attempts.");
        }
    }



    private static void scrollDown() {
        Dimension size = DriverBase.getDriver().manage().window().getSize();
        int width = size.getWidth();
        int height = size.getHeight();

        int startX = width / 2;
        int startY = (int) (height * 0.70);
        int endY = (int) (height * 0.60);

        PointerInput pointerInput = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scrollSequence = new Sequence(pointerInput, 0)
                .addAction(pointerInput.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY)) // Starting point at the bottom of the screen
                .addAction(pointerInput.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(pointerInput.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), startX, endY)) // Move to top of the screen
                .addAction(pointerInput.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        DriverBase.getDriver().perform(Collections.singletonList(scrollSequence));



    }

    public static void tapOnSubstring(String targetSubstring) {
        ITesseract iTesseract = initTess();

        BufferedImage img = takeScreenshot(SCREENSHOT_PATH);
        if (img == null) {
            System.err.println("Failed to read screenshot image.");
            return;
        }

        double[] scaleFactors = getScaleFactors(img);
        if (scaleFactors == null) {
            System.err.println("Failed to calculate scaling factors.");
            return;
        }

        List<Word> words = iTesseract.getWords(img, TessPageIteratorLevel.RIL_WORD);
        String[] targetWords = targetSubstring.split(" ");

        for (int i = 0; i < words.size() - targetWords.length + 1; i++) {
            boolean found = true;

            for (int j = 0; j < targetWords.length; j++) {
                if (!words.get(i + j).getText().equalsIgnoreCase(targetWords[j])) {
                    found = false;
                    break;
                }
            }

            if (found) {
                int startX = words.get(i).getBoundingBox().x;
                int startY = words.get(i).getBoundingBox().y;

                int endX = words.get(i + targetWords.length - 1).getBoundingBox().x +
                        words.get(i + targetWords.length - 1).getBoundingBox().width;

                int clickX = (startX + endX) / 2;
                int clickY = startY + (words.get(i).getBoundingBox().height / 2);

                int adjustedClickX = (int) (clickX * scaleFactors[0]);
                int adjustedClickY = (int) (clickY * scaleFactors[1]);

                System.out.println("Clicking on: " + targetSubstring + " at adjusted coordinates: X: " + adjustedClickX + ", Y: " + adjustedClickY);

                performClick(adjustedClickX, adjustedClickY);
                return;
            }
        }

        System.out.println("Substring: '" + targetSubstring + "' not found.");
    }



    private static void performClick(int clickX, int clickY) {
        PointerInput pointerInput = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence touchSequence = new Sequence(pointerInput, 0)
                .addAction(pointerInput.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), clickX, clickY))
                .addAction(pointerInput.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(pointerInput.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        DriverBase.getDriver().perform(Collections.singletonList(touchSequence));
    }

}
