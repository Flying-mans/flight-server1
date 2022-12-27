package com.jejuro.server1.scheduler.mail;

import com.jejuro.server1.entity.Alarm;
import com.jejuro.server1.entity.Flight;
import com.jejuro.server1.entity.Member;
import com.jejuro.server1.repository.AlarmRepository;
import com.jejuro.server1.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final AlarmRepository alarmRepository;
    private final FlightRepository flightRepository;

    @Scheduled(cron = "10 * * * * *")
    public void mailSchedule() {
        // 알람 테이블에서 처리 안된 알람 구하기
        // 항공편 정보와 가격 비교
        List<String> mailList = getMailList();
        sendMail(mailList);
    }

    public List<String> getMailList() {
        List<Alarm> alarmList = alarmRepository.findByStatus(1);
        List<String> emailList = new ArrayList<>();
        for (Alarm alarm : alarmList) {
            String code = alarm.getFlightCode();
            String depDate = alarm.getDepDate();
            int wantPrice = alarm.getPrice();

            System.out.println(code);
            System.out.println(depDate);
            List<Flight> flights = flightRepository.findByFlightCodeAndDepDate(code, depDate);

            Flight flight = flights.get(0);
            System.out.println("flightId:"+flight.getId());
            System.out.println("wantPrice:"+wantPrice);

            if (wantPrice >= flight.getFee()) {
                System.out.println("alarm.getMember():"+alarm.getMember());
                String email = alarm.getMember().getEmail();
                System.out.println("alarmId:"+alarm.getId());
                alarmRepository.setStatusDone(alarm.getId());
                emailList.add(email);
                System.out.println("email:"+email);
            }
        }
        return emailList;
    }

    public void sendMail(List<String> mailList) {
        String[] stringMailList = mailList.toArray(String[]::new);
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(stringMailList);
        message.setSubject("축하합니다");
        message.setText("지정하신 가격대가 왔어요");

        javaMailSender.send(message);
    }
}
