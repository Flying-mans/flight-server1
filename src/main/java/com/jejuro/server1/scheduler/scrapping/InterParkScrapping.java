package com.jejuro.server1.scheduler.scrapping;

import com.jejuro.server1.entity.Airline;
import com.jejuro.server1.entity.Flight;
import com.jejuro.server1.entity.FlightList;
import com.jejuro.server1.repository.AirlineRepository;
import com.jejuro.server1.repository.FlightListRepository;
import com.jejuro.server1.repository.FlightRepository;
import com.jejuro.server1.scheduler.scrapping.InterParkKeyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// TODO: test 코드 작성
// TODO: builder 적용
@Slf4j
@Component
@RequiredArgsConstructor
public class InterParkScrapping {

    private final FlightRepository flightRepository;
    private final FlightListRepository flightListRepository;
    private final AirlineRepository airlineRepository;
    private int DATE_RANGE = 42;
    private LocalDate now = LocalDate.now();
    private String collectedDate = now.toString().replaceAll("-", "");

    @Scheduled(cron = "0 0 8 * * *")
    public void crawlData() {
        ArrayList<Flight> flights = new ArrayList<>();

        for (int day=DATE_RANGE; day<DATE_RANGE*2; day++) {
            try {
                LocalDate localDate = now.plusDays(day);
                String date = localDate.toString().replaceAll("-","");
                // connection check
                Connection con = getConnection(date);

                JSONArray data = getFlightData(con);

                List<Flight> flightInfo = getFlightInfo(data);
                flights.addAll(flightInfo);
            } catch (JSONException e) {
                log.info("JSON NULL in day {}", day);
            }

        }

        addFlightList(flights);

        flightRepository.saveAll(flights);
    }

    private void addFlightList(ArrayList<Flight> flights) {
        for (Flight flight : flights) {
            String code = flight.getCode();
            String depDate = flight.getDepDate();

            FlightList f = flightListRepository.findByFlightCodeAndDepDate(code, depDate);
            log.info("flistList = {}", f);
            if (f != null) {
                // 있으면 새로 넣기
                flightListRepository.updateFee(f.getFee(), f.getId());
            }
            else {
                // 없으면 만들기
                String f_code = flight.getCode();
                String f_departure = flight.getDeparture();
                String f_arrival = flight.getArrival();
                String f_depDate = flight.getDepDate();
                String f_depTime = flight.getDepTime();
                String f_arrTime = flight.getArrTime();
                Airline f_airline = flight.getAirline();
                int f_fee = flight.getFee();

                FlightList flightList = new FlightList(
                        f_code,
                        f_departure,
                        f_arrival,
                        f_depDate,
                        f_depTime,
                        f_arrTime,
                        f_fee,
                        f_airline
                );
                flightListRepository.save(flightList);
            }
        }
    }

    public List<Flight> getFlightInfo(JSONArray availFareSet) {
        ArrayList<Flight> flightList = new ArrayList<>();

        for (int i=0; i<availFareSet.length(); i++) {
            JSONObject availFareSetJson = availFareSet.getJSONObject(i);
            JSONObject segFare = availFareSetJson.getJSONObject("segFare");

            /**
             * 항공편 기본 정보
             */
            String departure = (String)segFare.get("depCity"); // 출발지 코드
            String arrival = (String)segFare.get("arrCity"); // 도착지 코드
            String carCode = (String)segFare.get("carCode"); // 항공사 코드
            String code = (String)segFare.get("mainFlt"); // 비행기 번호
            String depDate = (String)segFare.get("depDate"); // 출발일자
            String depTime = (String)segFare.get("depTime"); // 출발시간
            String arrTime = (String)segFare.get("arrTime"); // 도착시간

            Airline airline = airlineRepository.findByCode(carCode);

            /**
             * 요금 정보 계산
             */
            int fuelChg = Integer.parseInt((String)segFare.get("fuelChg")); // 연료요금 oilTax
            int airTax = Integer.parseInt((String)segFare.get("airTax")); // 항공세
            int tasf = Integer.parseInt((String)segFare.get("tasf")); // 발권 수수료 commission

            JSONArray classDetail = (JSONArray) segFare.get("classDetail");
            for (int j=0; j<classDetail.length(); j++) {
                JSONObject classDetailJson = classDetail.getJSONObject(j);
                String classDesc = (String)classDetailJson.get("classDesc");
                int fare = Integer.parseInt((String)classDetailJson.get("fare"));
                int fee = fuelChg + airTax + fare + tasf;

                if (classDesc.equals("일반석")) {
                    Flight flight = new Flight(code, departure, arrival, depDate, depTime, arrTime, collectedDate, fee,airline);
                    flightList.add(flight);
                }
            }
        }
        return flightList;
    }

    public Connection getConnection(String date) {
        Connection con = Jsoup.connect(getUrl(date))
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");

        return con;
    }

    public String getUrl(String date) {
        String url = "https://sky.interpark.com/tourair/v1/schedule/domestic?dep=GMP&arr=CJU&depDate="
                +date+"&dep2=CJU&arr2=GMP&depDate2="+date+
                "&adt=1&inf=0&format=json&siteCode=WEBSTD&tripDivi=2";

        return url;
    }

    public JSONArray getFlightData(Connection con) {
        JSONArray availFareSet = new JSONArray();

        try {
            String bodyText = con.get().body().text();
            JSONObject json = new JSONObject(bodyText);
            JSONObject body = json.getJSONObject("body");

            JSONObject jsonObjectFare = body.getJSONObject(InterParkKeyType.REPLY_AVAIL_FARE.getKey());
            JSONObject jsonObjectFareRT = body.getJSONObject(InterParkKeyType.REPLY_AVAIL_FARE_RT.getKey());

            JSONArray availFareSet1 = (JSONArray)jsonObjectFare.get("availFareSet");
            JSONArray availFareSet2 = (JSONArray)jsonObjectFareRT.get("availFareSet");

            for (Object o : availFareSet1) {
                availFareSet.put(o);
            }
            for (Object o : availFareSet2) {
                availFareSet.put(o);
            }

        } catch (IOException e) {
            log.info("connection execute error = {}", e);
        }
        return availFareSet;
    }
}

