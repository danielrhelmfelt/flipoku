package com.flipoku.Repository;

import android.content.Context;

import com.flipoku.Domain.Statistics;

import java.util.ArrayList;
import java.util.List;

public class StatisticsRepository extends GenericRepository {

    private static List<Statistics> staticList = new ArrayList<>();
    private static Class<?> staticClass = Statistics.class;
    private static Context context = null;

    public StatisticsRepository(Context context) {
        this.context = context;
        staticList = init(context, staticList, staticClass);
    }

    public synchronized List<Statistics> findAll() {
        return findAll(staticList);
    }

    public synchronized Statistics findOne(Long id) {
        return findOne(staticList, id);
    }

    public synchronized Statistics findByClues(int clues) {
        for(Statistics each : findAll()) {
            if(each.getClues() == clues) {
                return each;
            }
        }
        return null;
    }

    public synchronized Statistics save(Statistics obj) {
        return save(context, staticList, staticClass, obj);
    }

    public synchronized void delete(Statistics obj) {
        delete(context, staticList, staticClass, obj);
    }

    public synchronized void deleteAll() {
        deleteAll(context, staticList, staticClass);
    }

    public synchronized Statistics findFirstByKey(String key) {
        List<Statistics> list = findByMethod(staticList, "getKey", key);
        if (list.size() != 0) {
            return list.get(0);
        }
        return null;
    }
}