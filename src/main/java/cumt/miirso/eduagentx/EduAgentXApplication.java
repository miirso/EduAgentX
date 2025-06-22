package cumt.miirso.eduagentx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cumt.miirso.eduagentx.mapper")
public class EduAgentXApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduAgentXApplication.class, args);
    }

}
