/*
 * The MIT License
 *
 * Copyright (C) 2020 Daniel R Helmfelt.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipoku.Repository;

import android.content.Context;

import com.flipoku.Domain.UserPreferences;

import java.util.ArrayList;
import java.util.List;

public class UserPreferencesRepository extends GenericRepository {

    private static List<UserPreferences> staticList = new ArrayList<>();
    private static Class<?> staticClass = UserPreferences.class;
    private static Context context = null;

    public UserPreferencesRepository(Context context) {
        this.context = context;
        staticList = init(context, staticList, staticClass);
    }

    public synchronized List<UserPreferences> findAll() {
        return findAll(staticList);
    }

    public synchronized UserPreferences findOne(Long id) {
        return findOne(staticList, id);
    }

    public synchronized UserPreferences save(UserPreferences obj) {
        return save(context, staticList, staticClass, obj);
    }

    public synchronized void delete(UserPreferences obj) {
        delete(context, staticList, staticClass, obj);
    }

    public synchronized void deleteAll() {
        deleteAll(context, staticList, staticClass);
    }

    public synchronized UserPreferences findFirstByKey(String key) {
        List<UserPreferences> list = findByMethod(staticList, "getKey", key);
        if (list.size() != 0) {
            return list.get(0);
        }
        return null;
    }
}