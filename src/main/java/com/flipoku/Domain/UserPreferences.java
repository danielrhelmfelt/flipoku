package com.flipoku.Domain;

import android.content.Context;

import com.flipoku.Repository.RepositoryEntity;
import com.flipoku.Repository.UserPreferencesRepository;

public class UserPreferences extends RepositoryEntity {
    /*Repository Static Structure*/
    private static final long serialVersionUID = 1L;
    public static UserPreferences clone(UserPreferences obj) {
        try {return (UserPreferences) obj.clone();} catch (CloneNotSupportedException e) {}
        return null;
    }
    public static UserPreferencesRepository getRepository(Context context) {
        return new UserPreferencesRepository(context);
    }
    /*Repository Static Structure*/

    private String key;
    private Object value;

    public UserPreferences(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}