package kr.datasolution.ds.api.util;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Stream;

import static kr.datasolution.ds.api.util.ReflectionUtils.objectToCSVFormat;

public class RestApiCSVWriter extends AbstractRestApiCSVWriter {

    public RestApiCSVWriter(String fileName, HttpServletResponse httpServletResponse) {
        this.setFileName(httpServletResponse, fileName);
    }

    @Override
    public void buildCSVDocument(Stream<?> streamList, List<String> headers) throws IOException {
        PrintWriter writer = this.httpServletResponse.getWriter();
        writer.write(CommonUtils.listToCSVFormat(headers));
        writer.write("\n");
        streamList.forEach(
                element -> {
                    writer.write(objectToCSVFormat(element));
                    writer.write("\n");
//                    entityManager.detach(element); // TODO:
                });
        writer.flush();
        writer.close();
    }

    @Override
    public void buildCSVDocument(List<?> arrayList, List<String> headers) throws IOException {
        PrintWriter writer = this.httpServletResponse.getWriter();
        writer.write(CommonUtils.listToCSVFormat(headers));
        writer.write("\n");
        arrayList.forEach(
                element -> {
                    writer.write(objectToCSVFormat(element));
                    writer.write("\n");
//                    entityManager.detach(element); // TODO:
                });
        writer.flush();
        writer.close();
    }
}
