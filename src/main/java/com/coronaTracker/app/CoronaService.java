package com.coronaTracker.app;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@Service
public class CoronaService {

    CoronaRepository coronaRepository;

    public CoronaService(CoronaRepository coronaRepository){
        this.coronaRepository = coronaRepository;
    }

    public void save (Corona corona){
        coronaRepository.save(corona);
    }

    public void populateDatabase() throws IOException {

        URL url = new URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/06-06-2020.csv");

//   data from 06-06-2020: https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/06-06-2020.csv
//   data from 08-31-2022: https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/08-31-2022.csv


        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        int responseCode = huc.getResponseCode();

        if(responseCode == 200){
            log.info("---- Successfully connected to Github -----");


            CSVReader reader = null;
            try {

                BufferedReader input = new BufferedReader(new InputStreamReader(huc.getInputStream()), 8192);

                reader = new CSVReader(input);

                String[] line;
                int i=0;
                while ((line = reader.readNext()) != null && i<500) {
                    if (i == 0){
                        i++;
                        continue;
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    Corona corona = new Corona();


                    corona.setLastUpdate(LocalDateTime.parse(line[4],formatter));
                    corona.setConfirmed(Long.valueOf(line[7]));
                    corona.setRecovered((line[9]) == "" ? 0 : (Long.valueOf(line[9])));
                    corona.setActive((line[10]) == "" ? 0 : (Long.valueOf(line[10])));
                    corona.setCombinedKey(line[11]);

                    coronaRepository.save(corona);
                    log.info(corona.toString());

                }
            }catch(IOException | CsvValidationException e){
                e.printStackTrace();
            }  finally{
                if (reader !=null){
                    try{
                        reader.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }





    }


    public List<Corona> findByLastUpdate(LocalDate localDate) {

        return coronaRepository.findByLastUpdateBetween(LocalDateTime.of(localDate, LocalTime.MIN), LocalDateTime.of(localDate,LocalTime.MAX));
    }

    public List<Corona> findAll() {

        return coronaRepository.findAll();
    }

}
