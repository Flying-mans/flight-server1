package com.jejuro.server1.scheduler.mail;

import com.jejuro.server1.entity.Alarm;
import com.jejuro.server1.entity.Flight;
import com.jejuro.server1.repository.AlarmRepository;
import com.jejuro.server1.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;
    private final AlarmRepository alarmRepository;
    private final FlightRepository flightRepository;

//    이메일 알림 시간 설정
    @Scheduled(cron = "0 32 16 * * *")
    public void mailSchedule() {
        // 알람 테이블에서 처리 안된 알람 구하기
        // 항공편 정보와 가격 비교
        List<String> mailList = getMailList();
        for(String mail : mailList)
            sendMail(mail);
    }

    public List<String> getMailList() {
        List<Alarm> alarmList = alarmRepository.findByStatus(1);
        List<String> emailList = new ArrayList<>();
        String now =  LocalDate.now().toString().replaceAll("-", "");

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
                System.out.println("flightSize:"+flights.size());
                if (flights.size()>0) {
                    Flight flight = flights.get(0);
                    if (wantPrice >= flight.getFee()) {
                        String email = alarm.getMember().getEmail();
                        alarmRepository.setStatusDone(alarm.getId());
                        emailList.add(email);
                    }
                }
            }
        }
        return emailList;
    }

    public void sendMail(String mail) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mail);
        message.setSubject("[JEJURO] 알림하신 가격대가 도착했어요");
        message.setText(" http://localhost:8081 에서 가격을 확인해 보세요");

        javaMailSender.send(message);
    }
}
