package com.example.covidtracker.services;

import java.io.StringReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.example.covidtracker.models.*;
import javax.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CoronaVirusDataService {
	
	private static String url_data = "https://raw.githubusercontent.com/bumbeishvili/covid19-daily-data/master/time_series_19-covid-Confirmed.csv";
	
	private List<LocationStats> allStats = new ArrayList<>();
	
	
	public List<LocationStats> getAllStats() {
		return allStats;
	}


	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchCovidData() throws IOException, InterruptedException {
		
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				   .uri(URI.create(url_data))
				   .build();
		HttpResponse<String> httpresponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		StringReader csvReader = new StringReader(httpresponse.body());
		@SuppressWarnings("deprecation")
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
		for (CSVRecord record : records) {
			LocationStats locationStat = new LocationStats();
		    locationStat.setState(record.get("Province/State"));
		    locationStat.setCountry(record.get("Country/Region"));
		    int latestCases = Integer.parseInt(record.get(record.size() - 1));
		    int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
		    locationStat.setLatestTotalCases(latestCases);
		    locationStat.setDiffFromPrevDay(latestCases - prevDayCases); 
		    newStats.add(locationStat);
		}
		this.allStats = newStats;
	}
}
