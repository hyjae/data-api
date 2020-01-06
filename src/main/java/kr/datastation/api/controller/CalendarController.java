package kr.datastation.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kr.datastation.api.entity.dataset.Calendar;
import kr.datastation.api.vo.TimePoint;
import kr.datastation.api.repository.dataset.CalendarCustomView;
import kr.datastation.api.repository.dataset.CalendarRepository;
import kr.datastation.api.util.HttpResponseCSVWriter;
import kr.datastation.api.util.ReflectionUtils;
import kr.datastation.api.validator.DateRequestParam;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = {"/calendar/download", "/spss/download"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    public void downloadFullCalendar(HttpServletResponse response,
                                     @DateRequestParam(point = TimePoint.FROM) String from,
                                     @DateRequestParam(point = TimePoint.TO) String to,
                                     @RequestParam(defaultValue = "csv") String format) throws IOException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("calendar.csv", response);

        List<String> columnNames = ReflectionUtils.getColumnNames(Calendar.class);
        columnNames.remove(columnNames.size()-1);
        columnNames.remove(columnNames.size()-1);
        httpResponseCsvWriter.setHeaders(columnNames);

        List<CalendarCustomView> bySolYmdBetween = calendarRepository.findBySolYmdBetweenOrderBySolYmd(from, to);
        bySolYmdBetween.forEach(element -> httpResponseCsvWriter.write(element.toCSV()));
        httpResponseCsvWriter.close();
    }
}
