package ${package};

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

/**
 * @author ${projectAuthor}
 */
@EnableApolloConfig
@SpringBootApplication
@MapperScan(annotationClass = Repository.class, basePackages = "${package}.dal.mapper")
public class BootStrapApplication {


    public static void main(String[] args) {
        SpringApplication.run(BootStrapApplication.class, args);
    }

}
