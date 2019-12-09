package kr.datasolution.ds.api.util;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HttpResponseCSVWriter extends AbstractHttpResponseWriter {

    public HttpResponseCSVWriter(String fileName, HttpServletResponse httpServletResponse) {
        this.setFileName(httpServletResponse, fileName);
    }

    public void setHeaders(List<String> headers) throws IOException {
        PrintWriter writer = this.httpServletResponse.getWriter();
        writer.write(CommonUtils.listToCSVFormat(headers));
        writer.write("\n");
    }

    public void write(String row) {
        this.write(row);
        this.write("\n");
    }

    public void close() {
        this.writer.flush();
        this.writer.close();
    }
}
