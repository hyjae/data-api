package kr.datastation.api.validator;


import kr.datastation.api.vo.EntitySort;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class EntitySortHandlerResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(EntitySortRequestParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
        EntitySortRequestParam parameterAnnotation = methodParameter.getParameterAnnotation(EntitySortRequestParam.class);
        EntitySort entitySort = parameterAnnotation.entityOrder();
//        nativeWebRequest.getParameter()
//        TimePoint point = parameterAnnotation.point();
//        String parameter = nativeWebRequest.getParameter(point.getTimePoint());
//
//        if (parameter != null && !CommonUtils.isValidDateFormat(DATE_FORMAT, parameter))
//            throw new IllegalArgumentException(String.format("Invalid Format! Correct format: %s format.", DATE_FORMAT)); // TODO: global
//        else if (parameter == null)
//            return setTime(DATE_FORMAT, point.getTimePoint());
//        return parameter;

        return null;
    }
}
