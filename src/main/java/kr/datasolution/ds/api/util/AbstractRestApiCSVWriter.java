package kr.datasolution.ds.api.util;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;


public abstract class AbstractRestApiCSVWriter {

    HttpServletResponse httpServletResponse;

    public void setFileName(HttpServletResponse httpServletResponse, String fileName) {
        this.httpServletResponse = prepareResponse(httpServletResponse, fileName);
    }

    static HttpServletResponse prepareResponse(HttpServletResponse httpServletResponse, String fileName) {
        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        httpServletResponse.addHeader("Content-Type", "application/csv");
        httpServletResponse.addHeader("Content-Disposition", headerValue);
        httpServletResponse.setCharacterEncoding("UTF-8");
        return httpServletResponse;
    }

    protected abstract void buildCSVDocument(Stream<?> streamList, List<String> headers) throws IOException;

    protected abstract void buildCSVDocument(List<?> streamList, List<String> headers) throws IOException;
}
