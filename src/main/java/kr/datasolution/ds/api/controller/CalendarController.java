package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kr.datasolution.ds.api.model.Calendar;
import kr.datasolution.ds.api.vo.TimePoint;
import kr.datasolution.ds.api.repository.CalendarCustomView;
import kr.datasolution.ds.api.repository.CalendarRepository;
import kr.datasolution.ds.api.util.HttpResponseCSVWriter;
import kr.datasolution.ds.api.util.ReflectionUtils;
import kr.datasolution.ds.api.validator.DateRequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/calendar")
@Api(value="/calendar")
@Validated
public class CalendarController {

    private final CalendarRepository calendarRepository;

    @Autowired
    public CalendarController(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

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
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("calendar.csv", response);

        // TODO: need a logic to fetch view names
        List<String> columnNames = ReflectionUtils.getColumnNames(Calendar.class);
        columnNames.remove(columnNames.size()-1);
        columnNames.remove(columnNames.size()-1);
        httpResponseCsvWriter.setHeaders(columnNames);

        List<CalendarCustomView> bySolYmdBetween = calendarRepository.findBySolYmdBetweenOrderBySolYmd(from, to);
        bySolYmdBetween.forEach(
                element -> httpResponseCsvWriter.write(element.getCalendarCSVFormat())
        );
        httpResponseCsvWriter.close();
    }
}
