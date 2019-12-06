package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kr.datasolution.ds.api.domain.Calendar;
import kr.datasolution.ds.api.domain.TimePoint;
import kr.datasolution.ds.api.repository.CalendarRepository;
import kr.datasolution.ds.api.util.CommonUtils;
import kr.datasolution.ds.api.util.ReflectionUtils;
import kr.datasolution.ds.api.validator.DateRequestParam;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    public void downloadFullCSV(HttpServletResponse response,
                                @DateRequestParam(point = TimePoint.FROM) String from,
                                @DateRequestParam(point = TimePoint.TO) String to,
                                @RequestParam(defaultValue = "csv") String format) throws IOException {
        if (format.equalsIgnoreCase("csv")) {
            // TODO: func
            // TODO: json
            response.addHeader("Content-Type", "application/csv");
            response.addHeader("Content-Disposition", "attachment; filename=calendar.csv");
            response.setCharacterEncoding("UTF-8");

            Stream<Calendar> calendarStream = calendarRepository.getAllBetween(from, to);

            List<String> columnNames = ReflectionUtils.getColumnNames(Calendar.class);
            PrintWriter out = response.getWriter();
            out.write(CommonUtils.listToCSVFormat(columnNames));
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
