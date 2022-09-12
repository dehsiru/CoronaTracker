package com.coronaTracker.app;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@Controller
public class MainController {

    CoronaService coronaService;

    public MainController(CoronaService coronaService) {
        this.coronaService = coronaService;
    }

    @GetMapping("/populate")
    public String populate(Model model) throws IOException {

        coronaService.populateDatabase();

        return "populateTemplate";
    }

    @GetMapping("/")
    public String findByDate(Model model) throws IOException {

      model.addAttribute("coronaData",coronaService.findByLastUpdate(LocalDate.now().minusDays(10)));

//        model.addAttribute("coronaData",coronaService.findAll());

        return "mainTemplate";
    }


    @GetMapping("/findAll")
    public String findAll(Model model) throws IOException {

        model.addAttribute("coronaData",coronaService.findAll());

        return "mainTemplate";
    }





}
