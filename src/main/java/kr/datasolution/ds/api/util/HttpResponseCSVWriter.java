package kr.datasolution.ds.api.util;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HttpResponseCSVWriter extends AbstractHttpResponseWriter {

    private PrintWriter writer;

    public HttpResponseCSVWriter(String fileName, HttpServletResponse httpServletResponse) {
        this.setFileName(httpServletResponse, fileName);
    }

    public void setHeaders(List<String> headers) throws IOException {
        this.writer = this.httpServletResponse.getWriter();
        this.writer.write(CommonUtils.listToCSVFormat(headers));
        this.writer.write("\n");
    }

    public void write(String row) {
        this.writer.write(row + "\n");
    }

    public void close() {
        this.writer.flush();
        this.writer.close();
    }
}
