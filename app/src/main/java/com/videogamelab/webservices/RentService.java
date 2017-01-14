package com.videogamelab.webservices;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class RentService {

    protected final String URL = "http://videogamelab.co.uk:8080/Bookstore/RentService";
    protected final String NAMESPACE = "http://videogamelab.co.uk/";
    protected final String SERVICE_INTERFACE = "RentInterface";

    protected final String METHOD_RENT_CREATE = "CreateRent";
    protected final String METHOD_RENT_FIND_BY_BOOK = "FindRentByBook";
    protected final String METHOD_RENT_FIND_BY_PERSON = "FindRentByPerson";
    protected final String METHOD_RENT_UPDATE = "UpdateRent";
    protected final String METHOD_RENT_DELETE = "DeleteRent";

    protected final String ACTION_RENT_CREATE = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_RENT_CREATE + "Request\"";
    protected final String ACTION_RENT_FIND_BY_BOOK = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_RENT_FIND_BY_BOOK + "Request\"";
    protected final String ACTION_RENT_FIND_BY_PERSON = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_RENT_FIND_BY_PERSON + "Request\"";
    protected final String ACTION_RENT_UPDATE = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_RENT_UPDATE + "Request\"";
    protected final String ACTION_RENT_DELETE_BY_ID = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_RENT_DELETE + "Request\"";

    private HttpTransportSE httpTransport;

    public RentService() {
        httpTransport = new HttpTransportSE(URL, 20000);
        httpTransport.debug = true;
    }

    public Rent createRent(Integer bookId, Integer personId) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_RENT_CREATE);
        request.addProperty("BookId", bookId);
        request.addProperty("PersonId", personId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            httpTransport.call(ACTION_RENT_CREATE, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }

        SoapObject result = (SoapObject) response;

        return new Rent((SoapObject) result.getProperty("Rent"));
    }

    public VectorRent findRentByBook(Integer bookId) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_RENT_FIND_BY_BOOK);
        request.addProperty("BookId", bookId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            httpTransport.call(ACTION_RENT_FIND_BY_BOOK, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }

        return new VectorRent((SoapObject) response);
    }

    public VectorRent findRentByPerson(Integer personId) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_RENT_FIND_BY_PERSON);
        request.addProperty("PersonId", personId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            httpTransport.call(ACTION_RENT_FIND_BY_PERSON, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }

        return new VectorRent((SoapObject) response);
    }

    public void updateRent(Rent object) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_RENT_UPDATE);
        request.addProperty("Rent", object);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.addMapping(NAMESPACE, "Rent", Rent.class);
        envelope.addMapping(NAMESPACE, "Book", Book.class);
        envelope.addMapping(NAMESPACE, "Person", Person.class);
        envelope.setOutputSoapObject(request);

        new MarshalDate().register(envelope);
        new MarshalBase64().register(envelope);

        try {
            httpTransport.call(ACTION_RENT_UPDATE, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }
    }

    public void deleteRent(Integer rentId) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_RENT_DELETE);
        request.addProperty("RentId", rentId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            httpTransport.call(ACTION_RENT_DELETE_BY_ID, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }
    }

}
