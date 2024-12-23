package test.com.automation.test;

import net.sourceforge.tess4j.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        //System.setProperty("jna.library.path", "/opt/homebrew/Cellar/tesseract/5.4.1/lib");
        //System.setProperty("jna.library.path", "lib/lib");
        //System.setProperty("jna.library.path", "-");
        System.out.println(System.getProperty("jna.library.path"));

        ITesseract iTesseract = new Tesseract();
        iTesseract.setDatapath("Tess4J/tessdata");
        iTesseract.setLanguage("eng");

        String fullText = "";
        List<Rectangle> textBoxes = null;
        List<Word> words = new ArrayList<>();

        try {

            BufferedImage img = ImageIO.read(new File("screenshot.png"));

            words = iTesseract.getWords(img, ITessAPI.TessPageIteratorLevel.RIL_WORD);

            fullText = iTesseract.doOCR(new File("screenshot.png"));

        } catch (TesseractException | IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(words);
    }
}
