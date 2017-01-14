package com.videogamelab.webservices;

import android.util.Base64;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.Hashtable;
import java.util.Vector;

public class VectorByte extends Vector<Byte> implements KvmSerializable {

    public VectorByte() {

    }

    public VectorByte(byte[] bytes) {
        for (final byte b : bytes) {
            add(b);
        }
    }

    public VectorByte(String image) {
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);

        for (final byte b : bytes) {
            add(b);
        }
    }

    public VectorByte(SoapPrimitive primitive) {
        if (primitive == null) {
            return;
        }

        String result = primitive.toString();

        if (!result.isEmpty()) {
            byte[] bytes = Base64.decode(result, Base64.DEFAULT);

            for (final byte b : bytes) {
                add(b);
            }
        }
    }

    @Override
    public Object getProperty(int index) {
        return this.get(index);
    }

    @Override
    public int getPropertyCount() {
        return this.size();
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        info.name = "Byte";
        info.type = Byte.class;
    }

    @Override
    public void setProperty(int index, Object value) {
        this.set(index, (Byte) value);
    }

    @Override
    public String toString() {
        byte[] byteToString = toBytes();

        return Base64.encodeToString(byteToString, Base64.DEFAULT);
    }

    public byte[] toBytes(){
        byte[] bytes = new byte[this.size()];
        int i = 0;

        for (Byte b : this) {
            bytes[i++] = b;
        }

        return bytes;
    }

}
