package io.ten1010.coaster.auth.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class LogUtil {

    public static String logMessage(Exception ex, boolean includeStackTrace) {
        StringBuilder sb = new StringBuilder();
        if (includeStackTrace) {
            Writer strWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(strWriter);
            ex.printStackTrace(printWriter);
            printWriter.close();
            sb.append("Exception stack trace : ")
                    .append("\n")
                    .append(strWriter.toString());
        } else {
            sb.append("Exception class : ")
                    .append(ex.getClass().getSimpleName())
                    .append("\n")
                    .append("Exception message : ")
                    .append(ex.getMessage())
                    .append("\n");
        }

        return sb.toString();
    }

}
