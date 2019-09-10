package perf.ui.junit.agent.helper;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import perf.ui.junit.agent.annotations.PerfUI;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class PerfUIHelper {

    public static boolean checkForAnnotationIsPresent(Description description) {
        return Objects.nonNull(description.getAnnotation(PerfUI.class));
    }

    public static String getAuditResult(WebDriver driver,long startTime) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        return (String) jsExecutor.executeScript(String.format("var testStartTime=%d; return %s", startTime, getScript()));
    }

    private static String getScript(){
        String script = "";
        try {
            script = FileUtils.readFileToString(new File("src/main/java/perf/ui/junit/agent/scripts/check_ui_performance.js"), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return script;
    }

    public static long getTime() {
        return new Date().getTime();
    }

    private static String getReportName(Description description,String folder){
        String fileName = description.getAnnotation(PerfUI.class).name().length() != 0 ? description.getAnnotation(PerfUI.class).name() : description.getMethodName();
        fileName = folder+"/"+fileName+"_"+getTime()+".html";
        return fileName;
    }

    public static void writeHtmlToFile(HttpResponse response,Description description,String folder){
        try {
            FileUtils.copyInputStreamToFile(response.getEntity().getContent(),new File(getReportName(description,folder)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
