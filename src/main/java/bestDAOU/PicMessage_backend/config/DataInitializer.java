package bestDAOU.PicMessage_backend.config;

import bestDAOU.PicMessage_backend.repository.TonesRepository;
import bestDAOU.PicMessage_backend.service.TonesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class DataInitializer {

    @Autowired
    private TonesService tonesService;

    @Autowired
    private TonesRepository tonesRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 기본 말투가 이미 존재하는지 확인
            long defaultTonesCount = tonesRepository.countByIsDefaultTrue();

            // 기본 말투가 없는 경우에만 초기화 실행
            if (defaultTonesCount == 0) {
                // 기본 말투 데이터 로드
                Resource resource = new ClassPathResource("default-tones.json");
                try {
                    String jsonData = new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
                    tonesService.initializeDefaultTones(jsonData);
                    System.out.println("기본 말투 데이터 초기화 완료");
                } catch (IOException e) {
                    System.err.println("기본 말투 데이터 로드 중 오류 발생: " + e.getMessage());
                }
            } else {
                System.out.println("기본 말투 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            }
        };
    }
}