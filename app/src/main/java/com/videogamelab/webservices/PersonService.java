package com.videogamelab.webservices;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class PersonService {

    public final String URL = "http://videogamelab.co.uk:8080/Bookstore/PersonService";
    public final String NAMESPACE = "http://videogamelab.co.uk/";
    public final String SERVICE_INTERFACE = "PersonInterface";

    protected final String METHOD_PERSON_CREATE = "CreatePerson";
    protected final String METHOD_PERSON_FIND_BY_ID = "FindPersonById";
    protected final String METHOD_PERSON_FIND_BY_USERNAME_AND_PASSWORD = "FindPersonByUsernameAndPassword";
    protected final String METHOD_PERSON_UPDATE = "UpdatePerson";
    protected final String METHOD_PERSON_DELETE = "DeletePerson";

    protected final String ACTION_PERSON_CREATE = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_PERSON_CREATE + "Request\"";
    protected final String ACTION_PERSON_FIND_BY_ID = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_PERSON_FIND_BY_ID + "Request\"";
    protected final String ACTION_PERSON_FIND_BY_USERNAME_AND_PASSWORD = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_PERSON_FIND_BY_USERNAME_AND_PASSWORD + "Request\"";
    protected final String ACTION_PERSON_UPDATE = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_PERSON_UPDATE + "Request\"";
    protected final String ACTION_PERSON_DELETE_BY_ID = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_PERSON_DELETE + "Request\"";

    private HttpTransportSE httpTransport;

    public PersonService() {
        httpTransport = new HttpTransportSE(URL, 20000);
        httpTransport.debug = true;
    }

    public Person createPerson(String username, String password, String firstName, String lastName) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_PERSON_CREATE);
        request.addProperty("Username", username);
        request.addProperty("Password", password);
        request.addProperty("FirstName", firstName);
        request.addProperty("LastName", lastName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        new MarshalDate().register(envelope);
        new MarshalBase64().register(envelope);

        try {
            httpTransport.call(ACTION_PERSON_CREATE, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }

        SoapObject result = (SoapObject) response;

        return new Person((SoapObject) result.getProperty("Person"));
    }

    public Person findPersonById(Integer personId) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_PERSON_FIND_BY_ID);
        request.addProperty("PersonId", personId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            httpTransport.call(ACTION_PERSON_FIND_BY_ID, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }

        SoapObject result = (SoapObject) response;

        return new Person((SoapObject) result.getProperty("Person"));
    }

    public Person findPersonByUsernameAndPassword(String username, String password) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_PERSON_FIND_BY_USERNAME_AND_PASSWORD);
        request.addProperty("Username", username);
        request.addProperty("Password", password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            httpTransport.call(ACTION_PERSON_FIND_BY_USERNAME_AND_PASSWORD, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }

        SoapObject result = (SoapObject) response;

        return new Person((SoapObject) result.getProperty("Person"));
    }

    public void updatePerson(Person object) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_PERSON_UPDATE);
        request.addProperty("Person", object);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.addMapping(NAMESPACE, "Person", Person.class);
        envelope.setOutputSoapObject(request);

        new MarshalDate().register(envelope);
        new MarshalBase64().register(envelope);

        try {
            httpTransport.call(ACTION_PERSON_UPDATE, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }
    }

    public void deletePerson(Integer personId) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_PERSON_DELETE);
        request.addProperty("PersonId", personId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            httpTransport.call(ACTION_PERSON_DELETE_BY_ID, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }
    }

}
