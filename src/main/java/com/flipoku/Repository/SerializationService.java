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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class SerializationService {
    private String nombreArchivo;

    public SerializationService(Class<?> classToSerialize) {
        nombreArchivo = classToSerialize.getSimpleName() + ".drh";
    }

    public List obtenerLista(Context context) {
        List datos = null;
        try {
            FileInputStream fileIn = context.openFileInput(nombreArchivo);
            ObjectInputStream entrada = new ObjectInputStream(fileIn);
            datos = (List) entrada.readObject();
            entrada.close();
            fileIn.close();
        } catch (Exception e) {
            System.out.println("Error en Obtener Lista en SerializationService: " + e.toString());
        }
        return datos;
    }

    public void grabarLista(Context context, List lista) {
        try {
            FileOutputStream fileOut = context.openFileOutput(nombreArchivo, Context.MODE_PRIVATE);
            ObjectOutputStream salida = new ObjectOutputStream(fileOut);
            salida.writeObject(lista);
            salida.close();
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Error en Grabar Lista en SerializationService: " + e.toString());
        }
    }
}