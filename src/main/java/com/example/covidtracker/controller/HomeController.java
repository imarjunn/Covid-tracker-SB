package com.example.covidtracker.controller;

import java.text.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.covidtracker.models.LocationStats;
import com.example.covidtracker.services.CoronaVirusDataService;

@Controller
public class HomeController {

	@Autowired
	CoronaVirusDataService coronaVirusDataService;
	
	@GetMapping("/")
	public String home(Model model) {
		List<LocationStats> allStats = coronaVirusDataService.getAllStats();
		int totalCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		model.addAttribute("locationStats",allStats);
		model.addAttribute("totalReportedCases",nf.format(totalCases));
		model.addAttribute("totalNewCases",nf.format(totalNewCases));
		return "home";
	}
}
