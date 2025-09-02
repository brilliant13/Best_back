# 📱 개인 맞춤형 문자 및 이미지 생성 서비스 "너를 위해"
## 📝 프로젝트 소개
우리 프로젝트는 단순히 문자 서비스를 제공하는 것을 넘어, **사용자와 지인의 관계**와 **개인적인 특징**을 반영하여 더욱 의미 있는 교류를 가능하게 합니다. 지인의 성격,지인과의 추억, 말투, 관계를 사전에 설정하여, 형식적인 단체 문자 대신 **상대방이 직접 작성한 것처럼 느껴지는 따뜻한 메시지**를 보낼 수 있습니다.
또한, 문자 내용에 따라 자동으로 이미지를 생성하거나, 직접 이미지를 업로드할 수 있어 **시각적으로 더욱 매력적인 메시지**를 전달할 수 있습니다.
### 📱 제품 데모
[![ForU 시연 영상](https://img.youtube.com/vi/07zpJxMxpYQ/0.jpg)](https://youtu.be/e0_yZCZvwcs)

> **2분만에 보는 ForU 핵심 기능**  
> AI 개인화 메시지 생성부터 개인 맞춤화 발송까지 전체 워크플로우를 확인하세요.
## 🌟 주요 기능
- **개인 맞춤 문자 서비스**  
  지인과의 관계, 지인의 특징, 말투 등을 설정하여 개인화된 문자를 작성 및 전송합니다.
- **문자 및 이미지 자동 생성**  
  입력한 텍스트를 기반으로 AI를 활용하여 문자와 이미지를 자동 생성합니다.
- **이미지 업로드 및 관리**  
  이미지를 업로드하거나, 드래그 앤 드롭 방식으로 간편히 추가할 수 있습니다.
- **다양한 설정 옵션**  
  메시지 톤, 감정, 배경 등을 선택하여 지인에게 최적화된 콘텐츠를 만듭니다.
- **다중 사용자 관리**  
  주소록에서 여러 명의 지인을 선택하여 각각 맞춤화된 메시지를 개별적으로 발송할 수 있습니다.
## 🏆 프로젝트 특장점
- **개인화된 소통**  
  수신자는 형식적인 단체 문자가 아닌, 1:1로 작성된 메시지를 받은 것 같은 느낌을 받습니다.
- **따뜻한 메시지 전달**  
  메시지에 담긴 개인화된 내용이 교류의 따뜻함과 진심을 전달합니다.
- **사용자 친화적 인터페이스**  
  직관적인 UI로 메시지 작성과 전송 과정을 쉽게 진행할 수 있습니다.
## 👥 팀원 소개
- **정웅** (팀장) ([brilliant13](https://github.com/brilliant13))  
- **김소룡** ([SoRyong-Kim](https://github.com/SoRyong-Kim))  
- **임차민** ([ckals413](https://github.com/ckals413))  
- **김문권** ([mk011011](https://github.com/mk011011))  
- **안예찬** ([yes6686](https://github.com/yes6686))  
## 🚀 기술 스택
- **프론트엔드**: React
- **백엔드**: Spring Boot
- **데이터베이스**: MySQL
- **AI 연동**: OpenAI GPT-4 API
- **UI 라이브러리**: react-icons, react-image-gallery
## 🛠️ 설치 및 실행 방법
### 1. 프로젝트 클론
```bash
git clone https://github.com/your-repo/your-project.git
cd your-project
```
### 2. 환경 변수 설정
`application.properties` 파일에 아래 내용을 추가합니다:
```
spring.application.name=PicMessage-backend
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/<당신의_스키마_이름>
spring.datasource.username=<당신의_DB_사용자명>
spring.datasource.password=<당신의_DB_비밀번호>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.format_sql=true;
spring.jpa.show-sql=true

gpt.model=gpt-4o
gpt.api.key=<당신의_GPT_API_키>
gpt.api.url=https://api.openai.com/v1/chat/completions

ppurio.api.key=<당신의_뿌리오_API_키>
```
## 💡 사용 방법
1. **지인 등록 및 설정**  
   - 지인의 특징, 말투, 기억을 설정하여 저장합니다.
2. **문자 내용 작성**  
   - 메시지를 입력하거나, AI 기반 자동 생성 기능을 사용하여 메시지를 작성합니다.
3. **이미지 추가**  
   - 드래그 앤 드롭 또는 업로드 버튼을 통해 이미지를 추가하거나, AI를 사용하여 이미지를 생성합니다. 
4. **개인맞춤화 버튼 클릭**  
   - 사전에 설정된 지인의 특징, 말투, 기억 등을 기반으로 메시지를 자동으로 변형해줍니다.
5. **전송하기**  
   - 작성된 메시지와 이미지를 선택한 지인에게 전송합니다.
## 🌟 기여하기
기여를 환영합니다! 새로운 기능 추가, 버그 수정, 문서 개선 등을 위한 PR(Pull Request)을 보내주세요.
## 📧 문의
프로젝트 관련 문의는 아래 이메일로 연락주세요:
- **이메일**: yes490411@gmail.com
