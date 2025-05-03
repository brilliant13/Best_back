package bestDAOU.PicMessage_backend.config;

import bestDAOU.PicMessage_backend.service.TonesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class DataInitializer {

    @Autowired
    private TonesService tonesService;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 기본 말투 데이터 로드
            Resource resource = new ClassPathResource("default-tones.json");
            try {
                String jsonData = new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
                tonesService.initializeDefaultTones(jsonData);
                System.out.println("기본 말투 데이터 초기화 완료");
            } catch (IOException e) {
                System.err.println("기본 말투 데이터 로드 중 오류 발생: " + e.getMessage());
            }
        };
    }
}
