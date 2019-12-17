package kr.datastation.api.validator;

import kr.datastation.api.vo.TimePoint;
import kr.datastation.api.util.CommonUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateHandlerResolver implements HandlerMethodArgumentResolver {

    private final String DATE_FORMAT = "yyyyMMdd";

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(DateRequestParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
        DateRequestParam parameterAnnotation = methodParameter.getParameterAnnotation(DateRequestParam.class);
        TimePoint point = parameterAnnotation.point();
        String parameter = nativeWebRequest.getParameter(point.getTimePoint());

        if (parameter != null && !CommonUtils.isValidDateFormat(DATE_FORMAT, parameter))
            throw new IllegalArgumentException(String.format("Invalid Format! Correct format: %s format.", DATE_FORMAT)); // TODO: global
        else if (parameter == null)
            return setTime(DATE_FORMAT, point.getTimePoint());
        return parameter;
    }

    private static String setTime(String dateFormat, String timePoint) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("KST"));

        if (timePoint.equalsIgnoreCase("from"))
            return simpleDateFormat.format(new Date(0L)); // TODO: 1970
        return simpleDateFormat.format(new Date());
    }
}
