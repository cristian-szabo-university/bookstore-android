package com.videogamelab.webservices;

import android.os.Parcel;
import android.os.Parcelable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class Rent implements KvmSerializable, Parcelable {

    public static final String ID = "id";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String BOOK = "book";
    public static final String PERSON = "person";

    private Integer id;
    private Date startDate;
    private Date endDate;
    private Book book;
    private Person person;

    public Rent() {
        id = 0;
        startDate = new Date();
        endDate = new Date();
        book = new Book();
        person = new Person();
    }

    private Rent(Parcel in) {
        id = in.readInt();
        startDate = new Date(in.readLong());
        endDate = new Date(in.readLong());
        book = in.readParcelable(Book.class.getClassLoader());
        person = in.readParcelable(Person.class.getClassLoader());
    }

    public Rent(SoapObject soap) {
        this();

        if (soap == null)
            return;

        if (soap.hasProperty(ID)) {
            Object obj = soap.getProperty(ID);

            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                id = Integer.parseInt(obj.toString());
            } else if (obj!= null && obj instanceof Number) {
                id = (Integer) obj;
            }
        }

        if (soap.hasProperty(START_DATE)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Object prop = soap.getProperty(START_DATE);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                try {
                    startDate = dateFormat.parse(prop.toString().substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (prop!= null && prop instanceof String) {
                try {
                    startDate = dateFormat.parse(((String)prop).substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (soap.hasProperty(END_DATE)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Object prop = soap.getProperty(END_DATE);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                try {
                    endDate = dateFormat.parse(prop.toString().substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (prop!= null && prop instanceof String) {
                try {
                    endDate = dateFormat.parse(((String) prop).substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if (soap.hasProperty(BOOK)) {
            Object prop = soap.getProperty(BOOK);

            if (prop != null && prop.getClass().equals(SoapObject.class)) {
                book = new Book((SoapObject) prop);
            }
        }

        if (soap.hasProperty(PERSON)) {
            Object prop = soap.getProperty(PERSON);

            if (prop != null && prop.getClass().equals(SoapObject.class)) {
                person = new Person((SoapObject) prop);
            }
        }
    }

    public Object getProperty(String attrib) {
        return getProperty(convertAttribToIndex(attrib));
    }

    @Override
    public Object getProperty(int index) {
        switch(index) {
            case 0: return id;
            case 1: return startDate;
            case 2: return endDate;
            case 3: return book;
            case 4: return person;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    public void setProperty(String attrib, Object value) { setProperty(convertAttribToIndex(attrib), value); }

    @Override
    public void setProperty(int index, Object value) {
        switch(index) {
            case 0:
                id = (Integer) value;
                break;
            case 1:
                if (value instanceof String) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        startDate = dateFormat.parse((String)value);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    startDate = (Date) value;
                }
                break;
            case 2:
                if (value instanceof String) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        endDate = dateFormat.parse((String)value);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    endDate = (Date) value;
                }
                break;
            case 3:
                book = (Book) value;
                break;
            case 4:
                person = (Person) value;
                break;
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = ID;
                break;
            case 1:
                info.type = Date.class;
                info.name = START_DATE;
                break;
            case 2:
                info.type = Date.class;
                info.name = END_DATE;
                break;
            case 3:
                info.type = Book.class;
                info.name = BOOK;
                break;
            case 4:
                info.type = Person.class;
                info.name = PERSON;
                break;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(startDate.getTime());
        dest.writeLong(endDate.getTime());
        dest.writeParcelable(book, flags);
        dest.writeParcelable(person, flags);
    }

    public static final Parcelable.Creator<Rent> CREATOR = new Parcelable.Creator<Rent>() {
        public Rent createFromParcel(Parcel in) {
            return new Rent(in);
        }

        public Rent[] newArray(int size) {
            return new Rent[size];
        }
    };

    private int convertAttribToIndex(String attrib) {
        int index = getPropertyCount();

        switch(attrib) {
            case ID: index = 0; break;
            case START_DATE: index = 1; break;
            case END_DATE: index = 2; break;
            case BOOK: index = 3; break;
            case PERSON: index = 4; break;
        }

        return index;
    }
}
