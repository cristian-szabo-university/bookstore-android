package com.videogamelab.webservices;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class Book implements KvmSerializable, Parcelable {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String PUBLISH_DATE = "publishDate";
    public static final String DESCRIPTION = "description";
    public static final String COVER = "cover";

    private Integer id;
    private String title;
    private String author;
    private Date publishDate;
    private String description;
    private VectorByte cover;

    public Book() {
        id = 0;
        title = "";
        author = "";
        publishDate = new Date();
        description = "";
        cover = new VectorByte();
    }

    private Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();
        publishDate = new Date(in.readLong());
        description = in.readString();
        cover = new VectorByte(in.readString());
    }

    Book(SoapObject soap) {
        this();

        if (soap == null) {
            return;
        }

        if (soap.hasProperty(ID)) {
            Object obj = soap.getProperty(ID);

            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                id = Integer.parseInt(obj.toString());
            } else if (obj!= null && obj instanceof Number) {
                id = (Integer) obj;
            }
        }

        if (soap.hasProperty(TITLE))
        {
            Object prop = soap.getProperty(TITLE);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                title = prop.toString();
            } else if (prop!= null && prop instanceof String) {
                title = (String) prop;
            }
        }

        if (soap.hasProperty(AUTHOR))
        {
            Object prop = soap.getProperty(AUTHOR);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                author = prop.toString();
            } else if (prop!= null && prop instanceof String) {
                author = (String)prop;
            }
        }

        if (soap.hasProperty(PUBLISH_DATE))
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Object prop = soap.getProperty(PUBLISH_DATE);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                try {
                    publishDate = dateFormat.parse(prop.toString().substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else if (prop!= null && prop instanceof String) {
                try {
                    publishDate = dateFormat.parse(((String)prop).substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (soap.hasProperty(DESCRIPTION))
        {
            Object prop = soap.getProperty(DESCRIPTION);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                description = prop.toString();
            } else if (prop!= null && prop instanceof String) {
                description = (String)prop;
            }
        }

        if (soap.hasProperty(COVER))
        {
            Object obj = soap.getProperty(COVER);

            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                cover = new VectorByte((SoapPrimitive) obj);
            } else {
                cover = new VectorByte();
            }
        }
    }

    public Object getProperty (String attrib) {
        return getProperty(convertAttribToIndex(attrib));
    }

    @Override
    public Object getProperty(int index) {
        switch(index) {
            case 0: return id;
            case 1: return title;
            case 2: return author;
            case 3: return publishDate;
            case 4: return description;
            case 5: return cover.toBytes();
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 6;
    }

    public void setProperty(String attrib, Object value) {
        setProperty(convertAttribToIndex(attrib), value);
    }

    @Override
    public void setProperty(int index, Object value) {
        switch(index) {
            case 0:
                id = (Integer) value;
                break;
            case 1:
                title = (String)value;
                break;
            case 2:
                author = (String)value;
                break;
            case 3:
                if (value instanceof String) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        publishDate = dateFormat.parse((String)value);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    publishDate = (Date) value;
                }
                break;
            case 4:
                description = (String)value;
                break;
            case 5:
                if (value instanceof Bitmap) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    Bitmap bmp = (Bitmap) value;
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    cover = new VectorByte(stream.toByteArray());
                } else {
                    cover = (VectorByte) value;
                }
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
                info.type = PropertyInfo.STRING_CLASS;
                info.name = TITLE;
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = AUTHOR;
                break;
            case 3:
                info.type = Date.class;
                info.name = PUBLISH_DATE;
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = DESCRIPTION;
                break;
            case 5:
                info.type = Byte.class;
                info.name = COVER;
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
        dest.writeString(title);
        dest.writeString(author);
        dest.writeLong(publishDate.getTime());
        dest.writeString(description);
        dest.writeString(cover.toString());
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private int convertAttribToIndex(String attrib) {
        int index = getPropertyCount();

        switch(attrib) {
            case ID: index = 0; break;
            case TITLE: index = 1; break;
            case AUTHOR: index = 2; break;
            case PUBLISH_DATE: index = 3; break;
            case DESCRIPTION: index = 4; break;
            case COVER: index = 5; break;
        }

        return index;
    }

}
