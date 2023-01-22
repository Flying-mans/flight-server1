package com.jejuro.server1.scheduler.mail;

import com.jejuro.server1.entity.Alarm;
import com.jejuro.server1.entity.Flight;
import com.jejuro.server1.entity.Member;
import com.jejuro.server1.repository.AlarmRepository;
import com.jejuro.server1.repository.FlightRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;
    private final AlarmRepository alarmRepository;
    private final FlightRepository flightRepository;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * JavaMailSender API를 통해 알람을 메일로 보내는 메소드다.
     * id를 통해 메일을 송신하기 때문에 알람을 다중으로 설정하려면 추후 기능 추가 필요
     */
    @Scheduled(cron = "* * * * * *")
    public void mailSchedule() throws MessagingException, UnsupportedEncodingException, ParseException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // getIdList함수를 통해 메일을 보낼 id를 가져온다.
        List<Long> idList = getIdList();

        for (Long id : idList) {

            Optional<Alarm> alarm = alarmRepository.findById(id);
            Member member = alarm.get().getMember();

            // 날짜 포맷을 변경
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat newDtFormat = new SimpleDateFormat("yyy-MM-dd");

            Date formDate = dtFormat.parse(alarm.get().getDepDate());

            // 항공편 출발날짜
            String depDate = newDtFormat.format(formDate);
            // 항공편 고유 코드 번호
            String code = alarm.get().getFlightCode();
            // 사용자가 설정한 원하는 알람 가격
            int price = alarm.get().getPrice();
            // 출력을 위한 사용자 닉네임
            String nickname = member.getNickName();
            // 사용자 이메일
            String email = member.getEmail();
            // 메일 제목 설정
            helper.setSubject("[JEJURO] 알림하신 가격대가 도착했어요");
            // 메일 발신자 설정
            helper.setFrom("Info@Jejuro.com", "Jejuro");

            // 랜더링을 하기 위해 필요한 정보들
            Context context = new Context();
            context.setVariable("price", price);
            context.setVariable("depDate", depDate);
            context.setVariable("flightCode", code);
            context.setVariable("name", nickname);

            // 메일 내용 설정 : html페이지와 context객체 렌더링 작업
            String html = templateEngine.process("index.html", context);

            helper.setText(html, true);
            helper.setTo(email);

            // 메일 보내기
            javaMailSender.send(message);
        }
    }

    public List<Long> getIdList() {
        List<Alarm> alarmList = alarmRepository.findByStatus(1);
        List<Long> idList = new ArrayList<>();
        String now = LocalDate.now().toString().replaceAll("-", "");

        for (Alarm alarm : alarmList) {
            String code = alarm.getFlightCode(); // 항공편 고유 코드 번호
            String depDate = alarm.getDepDate(); // 항공편 출발날짜
            int wantPrice = alarm.getPrice(); // 사용자가 설정한 원하는 알람 가격
            // 만약 오늘 날짜가 항공편 출발날짜보다 크다면 지난 알림이므로 처리한거로 침
            if (Integer.parseInt(now) > Integer.parseInt(depDate)) {
                alarmRepository.setStatusDone(alarm.getId());
            }
            // 오늘 날짜보다 이전 알람이면 확인
            else {
                List<Flight> flights = flightRepository.findByFlightCodeAndDepDate(code, depDate);
                System.out.println("flightSize:" + flights.size());
                if (flights.size() > 0) {
                    Flight flight = flights.get(0);
                    if (wantPrice >= flight.getFee()) {
                        Long id = alarm.getId();
                        alarmRepository.setStatusDone(alarm.getId());
                        idList.add(id);
                    }
                }
            }
        }
        return idList;
    }

}
