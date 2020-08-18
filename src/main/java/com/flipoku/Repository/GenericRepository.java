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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class GenericRepository {

    public <T> List<T> init(Context context, List<T> staticList, Class<?> staticClass) {
        synchronized (staticList) {
            if (staticList.size() == 0) {// es posible que la lista, al ser estatica, ya tenga datos guardados, si no los tiene (tamaño==0), se realiza la lectura del archivo
                SerializationService serializator = new SerializationService(staticClass);
                List<T> aux = serializator.obtenerLista(context);
                if (aux != null) {
                    staticList = aux;
                } else {// no esta creado el archivo de la lista
                    serializator.grabarLista(context, staticList);
                }
            }
            return staticList;
        }
    }

    private <T> T cloneObject(T obj) {
        T clonedObject = null;
        try {
            Method methodClone = obj.getClass().getMethod("clone", obj.getClass());
            clonedObject = (T) methodClone.invoke(obj.getClass(), obj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al ejecutar metodo cloneObject");
        }
        return clonedObject;
    }

    private <T> Long getObjectId(T obj) {
        Long objectId = null;
        try {
            Method methodGetId = obj.getClass().getMethod("getId");
            objectId = (Long) methodGetId.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al ejecutar metodo getObjectId");
        }
        return objectId;
    }

    protected <T> List<T> findAll(List<T> staticList) {
        synchronized (staticList) {
            List<T> list = new ArrayList<>();
            for (T each : staticList) {
                list.add(cloneObject(each));
            }
            return list;
        }
    }

    protected <T> T findOne(List<T> staticList, Long id) {
        synchronized (staticList) {
            int size = staticList.size();
            for (int i = 0; i < size; i++) {
                T aux = staticList.get(i);
                try {
                    Method methodGetId = aux.getClass().getMethod("getId");
                    Long auxId = (Long) methodGetId.invoke(aux);
                    if (auxId.equals(id)) {
                        return cloneObject(aux);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    protected <T> T save(Context context, List<T> staticList, Class<?> staticClass, T obj) {
        synchronized (staticList) {
            if (getObjectId(obj) == null) {
                Long newId = getNewId(staticList);
                try {
                    Method methodSetId = obj.getClass().getMethod("setId", Long.class);
                    methodSetId.invoke(obj, newId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                obj = cloneObject(obj);
                staticList.add(obj);
                SerializationService serializator = new SerializationService(staticClass);
                serializator.grabarLista(context, staticList);
            } else {
                obj = update(context, staticList, staticClass, obj);
            }
            return obj;
        }
    }

    private Comparator<Object> getIdComparator() {
        return new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Long id1 = getObjectId(o1);
                Long id2 = getObjectId(o2);
                return id1.compareTo(id2);
            }
        };
    }

    private <T> T update(Context context, List<T> staticList, Class<?> staticClass, T obj) {
        synchronized (staticList) {
            if (getObjectId(obj) == null) {
                throw new IllegalArgumentException("can't update a null-id Object, save it first");
            } else {
                obj = cloneObject(obj);
                // en lugar que recorrer la lista hasta encontrar el objeto se realiza una busqueda binaria ya que la lista debería estar ordenada por id
                //Collections.sort(staticList, getIdComparator());
                int pos = Collections.binarySearch(staticList, obj, getIdComparator());
                if (pos >= 0) {
                    staticList.set(pos, obj);
                    SerializationService serializator = new SerializationService(staticClass);
                    serializator.grabarLista(context, staticList);
                    return obj;
                }
                //				int size = staticList.size();
                //				for(int i = 0; i < size; i++) {
                //					T aux = staticList.get(i);
                //					if(getObjectId(aux).equals(getObjectId(obj))) {
                //						staticList.set(i, obj);
                //						SerializationService serializator = new SerializationService(staticClass);
                //						serializator.grabarLista(staticList);
                //						return obj;
                //					}
                //				}
                throw new RuntimeException("Object not found " + getObjectId(obj));
            }
        }
    }

    public <T> void delete(Context context, List<T> staticList, Class<?> staticClass, T obj) {
        synchronized (staticList) {
            if (getObjectId(obj) != null) {
                SerializationService serializator = new SerializationService(staticClass);
                int size = staticList.size();
                for (int i = 0; i < size; i++) {
                    T aux = staticList.get(i);
                    if (getObjectId(aux).equals(getObjectId(obj))) {
                        staticList.remove(i);
                        serializator.grabarLista(context, staticList);
                        return;
                    }
                }
            }
        }
    }

    public <T> void deleteAll(Context context, List<T> staticList, Class<?> staticClass) {
        synchronized (staticList) {
            SerializationService serializator = new SerializationService(staticClass);
            List<T> list = findAll(staticList);// clonamos la lista para luego eliminar recorriendo los clones
            for (T each : list) {
                delete(context, staticList, staticClass, each);
            }
        }
    }

    private <T> Long getNewId(List<T> staticList) {
        synchronized (staticList) {
            Long lastId = Long.valueOf("0");
            for (int i = 0; i < staticList.size(); i++) {
                Long eachId = getObjectId(staticList.get(i));
                if (lastId < eachId) {
                    lastId = eachId;
                }
            }
            return lastId + 1;
        }
    }

    protected <T, S> List<T> findByMethod(List<T> staticList, String methodName, S attributeValue) {
        synchronized (staticList) {
            List<T> list = new ArrayList<>();
            if (staticList.size() > 0) {
                for (T each : staticList) {
                    try {
                        Method method = each.getClass().getMethod(methodName);
                        Object attributeOfObjectInList = (Object) method.invoke(each);
                        if (attributeOfObjectInList != null && attributeOfObjectInList.equals(attributeValue)) {
                            list.add(cloneObject(each));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error in findByMethod");
                    }

                }
            }
            return list;
        }
    }

    public <T> void saveList(Context context, List<T> staticList, Class<?> staticClass) {
        synchronized (staticList) {
            SerializationService serializator = new SerializationService(staticClass);
            serializator.grabarLista(context, staticList);
            return;
        }
    }
}