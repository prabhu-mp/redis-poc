package com.ganjigere.transactions_search.delegate;

public interface GenericDao {

    public void addUpdateRecords(String key, Object value);

    public Object getRecords(String key, String path);

    public void deleteRecords(String key);
}
