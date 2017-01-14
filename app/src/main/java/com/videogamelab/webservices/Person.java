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

public class Person implements KvmSerializable, Parcelable {

    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String BIRTH_DATE = "birthDate";
    public static final String GENDER = "gender";
    public static final String ADMIN = "admin";
    public static final String AVATAR = "avatar";

    private Integer id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String gender;
    private Boolean admin;
    private VectorByte avatar;

    public Person() {
        id = 0;
        username = "";
        password = "";
        firstName = "";
        lastName = "";
        birthDate = new Date();
        gender = "";
        admin = Boolean.FALSE;
        avatar = new VectorByte();
    }

    private Person(Parcel in) {
        id = in.readInt();
        username = in.readString();
        password = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        birthDate = new Date(in.readLong());
        gender = in.readString();
        admin = Boolean.valueOf(in.readString());
        avatar = new VectorByte(in.readString());
    }

    public Person(SoapObject soap) {
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

        if (soap.hasProperty(USERNAME)) {
            Object obj = soap.getProperty(USERNAME);
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                username = obj.toString();
            } else if (obj!= null && obj instanceof String){
                username = (String) obj;
            }
        }

        if (soap.hasProperty(PASSWORD)) {
            Object obj = soap.getProperty(PASSWORD);

            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                password = obj.toString();
            } else if (obj!= null && obj instanceof String){
                password = (String) obj;
            }
        }

        if (soap.hasProperty(FIRST_NAME)) {
            Object prop = soap.getProperty(FIRST_NAME);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                firstName = prop.toString();
            } else if (prop!= null && prop instanceof String){
                firstName = (String) prop;
            }
        }

        if (soap.hasProperty(LAST_NAME)) {
            Object prop = soap.getProperty(LAST_NAME);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                lastName = prop.toString();
            } else if (prop != null && prop instanceof String) {
                lastName = (String) prop;
            }
        }

        if (soap.hasProperty(BIRTH_DATE)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Object prop = soap.getProperty(BIRTH_DATE);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                try {
                    birthDate = dateFormat.parse(prop.toString().substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (prop!= null && prop instanceof String) {
                try {
                    birthDate = dateFormat.parse(((String)prop).substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (soap.hasProperty(GENDER)) {
            Object prop = soap.getProperty(GENDER);

            if (prop != null && prop.getClass().equals(SoapPrimitive.class)) {
                gender = prop.toString();
            } else if (prop!= null && prop instanceof String) {
                gender = (String)prop;
            }
        }

        if (soap.hasProperty(ADMIN)) {
            Object obj = soap.getProperty(ADMIN);

            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                admin = Boolean.valueOf(obj.toString());
            } else if (obj!= null && obj instanceof Boolean) {
                admin = (Boolean) obj;
            }
        }

        if (soap.hasProperty(AVATAR))
        {
            Object obj = soap.getProperty(AVATAR);

            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                avatar = new VectorByte((SoapPrimitive) obj);
            } else {
                avatar = new VectorByte();
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
            case 1: return username;
            case 2: return password;
            case 3: return firstName;
            case 4: return lastName;
            case 5: return birthDate;
            case 6: return gender;
            case 7: return admin;
            case 8: return avatar.toBytes();
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 9;
    }

    public void setProperty(String attrib, Object value) { setProperty(convertAttribToIndex(attrib), value); }

    @Override
    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                id = (Integer) value;
                break;
            case 1:
                username = (String) value;
                break;
            case 2:
                password = (String) value;
                break;
            case 3:
                firstName = (String) value;
            case 4:
                lastName = (String) value;
                break;
            case 5:
                if (value instanceof String) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        birthDate = dateFormat.parse((String)value);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    birthDate = (Date) value;
                }
                break;
            case 6:
                gender = (String) value;
                break;
            case 7:
                admin = (Boolean) value;
                break;
            case 8:
                if (value instanceof Bitmap) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    Bitmap bmp = (Bitmap) value;
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    avatar = new VectorByte(stream.toByteArray());
                } else if (value instanceof byte[]) {
                    avatar = new VectorByte((byte[]) value);
                } else {
                    avatar = (VectorByte) value;
                }
                break;
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "id";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "username";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "password";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "firstName";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "lastName";
                break;
            case 5:
                info.type = Date.class;
                info.name = "birthDate";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "gender";
                break;
            case 7:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "admin";
                break;
            case 8:
                info.type = Byte.class;
                info.name = "avatar";
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
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeLong(birthDate.getTime());
        dest.writeString(gender);
        dest.writeString(admin.toString());
        dest.writeString(avatar.toString());
    }

    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    private int convertAttribToIndex(String attrib) {
        int index = getPropertyCount();

        switch(attrib) {
            case ID: index = 0; break;
            case USERNAME: index = 1; break;
            case PASSWORD: index = 2; break;
            case FIRST_NAME: index = 3; break;
            case LAST_NAME: index = 4; break;
            case BIRTH_DATE: index = 5; break;
            case GENDER: index = 6; break;
            case ADMIN: index = 7; break;
            case AVATAR: index = 8; break;
        }

        return index;
    }
}