package test.com.automation.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class RetryAnalyzerForClass implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        //IAnnotationTransformer.super.transform(annotation, testClass, testConstructor, testMethod);
        annotation.setRetryAnalyzer(RetryAnalyzerForTests.class);
    }
}
