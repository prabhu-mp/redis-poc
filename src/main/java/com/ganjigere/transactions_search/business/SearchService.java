package com.ganjigere.transactions_search.business;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganjigere.transactions_search.delegate.GenericDao;
import com.ganjigere.transactions_search.delegate.JedisDao;
import com.ganjigere.transactions_search.delegate.LettuceDao;
import com.ganjigere.transactions_search.delegate.LettuceModDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Component
public class SearchService {

    GenericDao dao;
    Gson gson = new Gson();

    ObjectMapper mapper;

    public SearchService(GenericDao dao) {
        this.dao = dao;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void addRecords() {
        try {
            Path path = Paths.get("src/main/resources/laureate.json");
            String read = Files.readAllLines(path).toString();
            log.error("read laurates {}", read.substring(0, 500) );
            //gson.fromJson(read, Map.class);
            JsonNode node = mapper.readTree(read);
            dao.addUpdateRecords("laureats", read);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public Object retrieveRecords(String key, String path) {
        Object recordValue = dao.getRecords(key, path);
        log.error("retrieved record : {}", recordValue);
        return recordValue;
    }

    public void deleteRecords() {

    }

    public boolean updateRecords() {

        return false;
    }

    public void createIndex(){
        ((LettuceModDao)dao).createIndex();
    }

    public Object search(String index, String path) {
        Object result = ((LettuceModDao)dao).search(index, path);
        log.error("search results : {} ", result);
        return result;
    }

    public static void main(String[] args) {
        //SearchService service = new SearchService(new LettuceDao());
        SearchService service = new SearchService(new LettuceModDao());
        //service.addRecords();
        //service.createIndex();
        service.retrieveRecords("sample_bicycle:1001", "$");
        service.search( "bicycleIdx", "@description:{speed}");
    }
}
