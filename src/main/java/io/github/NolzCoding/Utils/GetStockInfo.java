package io.github.NolzCoding.Utils;

import com.google.gson.Gson;
import io.github.NolzCoding.JSON.JSONCompanyProfile;
import io.github.NolzCoding.JSON.JSONQuoute;
import io.github.NolzCoding.Main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GetStockInfo {

    private Gson gson = new Gson();

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public Float getCurrentPrice(String sym) {
        String uri = "https://finnhub.io/api/v1/quote?symbol=" + sym +"&token=" + Main.token;

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();
        HttpResponse
                <String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }



        JSONQuoute jsonQuoute = gson.fromJson(response.body(), JSONQuoute.class);

        return jsonQuoute.getC();
    }

    public String getInfo(String symbol) {
        String uri =
                "https://finnhub.io/api/v1/stock/profile2?symbol=" +
                symbol +
                "&token=" + Main.token;


        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();
        HttpResponse
                <String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        JSONCompanyProfile companyProfile = gson.fromJson(response.body(), JSONCompanyProfile.class);

        return companyProfile.getName();

    }




}
