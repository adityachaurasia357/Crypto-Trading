package com.adi.trading.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.adi.trading.model.Coin;
import com.adi.trading.repository.CoinRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CoinServiceImpl implements CoinService {

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Coin> getCoinList(int page) throws Exception {
        String url="https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=10&page="+page;
        RestTemplate restTemplate=new RestTemplate();
        try {
            HttpHeaders headers=new HttpHeaders();
            HttpEntity<String> entity=new HttpEntity<>("parameters",headers);
            ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,entity,String.class);
            List<Coin> coinList=objectMapper.readValue(response.getBody(),new TypeReference<List<Coin>>(){});
            return coinList;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getMarketChart(String coinId, int days) throws Exception {
        String url="https://api.coingecko.com/api/v3/coins"+coinId+"/market_chart?vs_currency=usd&per_page=10&page="+days;
        RestTemplate restTemplate=new RestTemplate();
        try {
            HttpHeaders headers=new HttpHeaders();
            HttpEntity<String> entity=new HttpEntity<>("parameters",headers);
            ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,entity,String.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getCoinDetails(String coinId) throws Exception {
        String url="https://api.coingecko.com/api/v3/coins"+coinId;
        RestTemplate restTemplate=new RestTemplate();
        try {
            HttpHeaders headers=new HttpHeaders();
            HttpEntity<String> entity=new HttpEntity<>("parameters",headers);
            ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,entity,String.class);
            JsonNode JsonNode=objectMapper.readTree(response.getBody());
            Coin coin=new Coin();
            coin.setId(coinId);
            coin.setName(url);
            coin.setImage(url);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Coin findById(String coinId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public String searchCoin(String keyword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchCoin'");
    }

    @Override
    public String getTop50CoinsByMarketCapRank() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTop50CoinsByMarketCapRank'");
    }

    @Override
    public String GetTradingCoins() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'GetTradingCoins'");
    }
    
}
