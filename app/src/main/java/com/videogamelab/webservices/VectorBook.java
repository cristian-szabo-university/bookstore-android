package com.videogamelab.webservices;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Hashtable;
import java.util.Vector;

public class VectorBook extends Vector<Book> implements KvmSerializable {

    public VectorBook() {}

    public VectorBook(SoapObject soap) {
        if (soap == null)
            return;

        if (soap != null){
            int size = soap.getPropertyCount();

            for (int i = 0; i < size; i++) {
                Object obj = soap.getProperty(i);

                if (obj != null && obj.getClass().equals(SoapObject.class)) {
                    SoapObject j = (SoapObject) soap.getProperty(i);

                    add(new Book(j));
                }
            }
        }
    }


    @Override
    public Object getProperty(int index) {
        return get(index);
    }

    @Override
    public int getPropertyCount() {
        return size();
    }

    @Override
    public void setProperty(int index, Object value) {
        set(index, (Book) value);
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        info.name = "book";
        info.type = Book.class;
    }

}
