package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import kr.datasolution.ds.api.domain.Calendar;
import kr.datasolution.ds.api.repository.CalendarRepository;
import kr.datasolution.ds.api.util.CommonUtils;
import kr.datasolution.ds.api.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/calendar")
@Api(value="/calendar")
@Validated
public class CalendarController {

    @Autowired
    CalendarRepository calendarRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public void downloadFullCSV(HttpServletResponse response,
                                @RequestParam(value = "from", required = false) String from,
                                @RequestParam(value = "to", required = false) String to,
                                @RequestParam(value = "format", required = false, defaultValue = "csv") String format) throws IOException {
        if (format.equalsIgnoreCase("csv")) {
            // TODO: func
            // TODO: header
            // TODO: json
            response.addHeader("Content-Type", "application/csv");
            response.addHeader("Content-Disposition", "attachment; filename=calendar.csv");
            response.setCharacterEncoding("UTF-8");

            Stream<Calendar> calendarStream = calendarRepository.getAllBetween(from, to);

            List<String> tableColumnNames = ReflectionUtils.getTableColumnNames(Calendar.class);
            PrintWriter out = response.getWriter();
            out.write(CommonUtils.listToCSVFormat(tableColumnNames));
            out.write("\n");
            calendarStream.forEach(
                    calendar -> {
                        out.write(calendar.toString());
                        out.write("\n");
                        entityManager.detach(calendar);
                    });
            out.flush();
            out.close();
        }
    }
}
