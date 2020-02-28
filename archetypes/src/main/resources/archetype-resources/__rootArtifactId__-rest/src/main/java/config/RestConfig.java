package ${package}.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.TimeZone;

/**
 * 说明：MVC配置
 * @author ${projectAuthor}
 * 创建时间： 2020年02月13日 9:57 上午
 **/
@Configurable
@RestControllerAdvice
public class RestConfig  implements WebMvcConfigurer {


//    @Bean
//    public ServletListenerRegistrationBean<SystemContextListener> systemContextListenerServletListenerRegistrationBean(){
//        return new ServletListenerRegistrationBean(new SystemContextListener());
//    }

    /**
     * 定制json的输出格式
     * @return 定制json的输出格式
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(){
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.indentOutput(true)
                .timeZone(TimeZone.getTimeZone("GMT+" + 8))
                .build();
    }


}
