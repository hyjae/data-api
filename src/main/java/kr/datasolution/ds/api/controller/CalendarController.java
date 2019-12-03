package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import kr.datasolution.ds.api.domain.Calendar;
import kr.datasolution.ds.api.repository.CalendarRepository;
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
    @Transactional(readOnly = true) // TODO: ?
    public void downloadFullCSV(HttpServletResponse response,
                                @RequestParam(value = "from", required = false) String from,
                                @RequestParam(value = "to", required = false) String to) throws IOException {
        // TODO: header
        response.addHeader("Content-Type", "application/csv");
        response.addHeader("Content-Disposition", "attachment; filename=calendar.csv");
        response.setCharacterEncoding("UTF-8");

        Stream<Calendar> calendarStream = calendarRepository.getAllBetween(from, to);

        PrintWriter out = response.getWriter();
        calendarStream.forEach(
                cal -> {
                    out.write(cal.toString());
                    out.write("\n");
                    entityManager.detach(cal); // TODO:
                });
        out.flush();
        out.close();
    }
}
