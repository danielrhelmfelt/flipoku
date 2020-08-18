package com.flipoku.Repository;

import android.content.Context;

import com.flipoku.Domain.Board;

import java.util.ArrayList;
import java.util.List;

public class BoardRepository extends GenericRepository {

    private static List<Board> staticList = new ArrayList<>();
    private static Class<?> staticClass = Board.class;
    private static Context context = null;

    public BoardRepository(Context context) {
        this.context = context;
        staticList = init(context, staticList, staticClass);
    }

    public synchronized List<Board> findAll() {
        return findAll(staticList);
    }

    public synchronized Board findOne(Long id) {
        return findOne(staticList, id);
    }

    public synchronized Board save(Board obj) {
        return save(context, staticList, staticClass, obj);
    }

    public synchronized void delete(Board obj) {
        delete(context, staticList, staticClass, obj);
    }

    public synchronized void deleteAll() {
        deleteAll(context, staticList, staticClass);
    }

    public synchronized Board findFirstByKey(String key) {
        List<Board> list = findByMethod(staticList, "getKey", key);
        if (list.size() != 0) {
            return list.get(0);
        }
        return null;
    }
}