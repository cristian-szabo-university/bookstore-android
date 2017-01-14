package com.videogamelab.webservices;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class BookService {

    public static final String URL = "http://videogamelab.co.uk:8080/Bookstore/BookService";
    public static final String NAMESPACE = "http://videogamelab.co.uk/";
    public static final String SERVICE_INTERFACE = "BookInterface";

    protected static final String METHOD_BOOK_CREATE = "CreateBook";
    protected static final String METHOD_BOOK_FIND_BY_ID = "FindBookById";
    protected static final String METHOD_BOOK_FIND_ALL = "FindBookAll";
    protected static final String METHOD_BOOK_UPDATE = "UpdateBook";
    protected static final String METHOD_BOOK_DELETE = "DeleteBook";

    protected static final String ACTION_BOOK_CREATE = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_BOOK_CREATE + "Request\"";
    protected static final String ACTION_BOOK_FIND_BY_ID = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_BOOK_FIND_BY_ID + "Request\"";
    protected static final String ACTION_BOOK_FIND_ALL = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_BOOK_FIND_ALL + "Request\"";
    protected static final String ACTION_BOOK_UPDATE = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_BOOK_UPDATE + "Request\"";
    protected static final String ACTION_BOOK_DELETE_BY_ID = "\"" + NAMESPACE + SERVICE_INTERFACE + "/" + METHOD_BOOK_DELETE + "Request\"";

    private HttpTransportSE httpTransport;

    public BookService() {
        httpTransport = new HttpTransportSE(URL, 20000);
        httpTransport.debug = true;
    }

    public Book createBook(String title, String author) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_BOOK_CREATE);
        request.addProperty("Title", title);
        request.addProperty("Author", author);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        new MarshalDate().register(envelope);
        new MarshalBase64().register(envelope);

        try {
            httpTransport.call(ACTION_BOOK_CREATE, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }

        SoapObject result = (SoapObject) response;

        return new Book((SoapObject) result.getProperty("Book"));
    }

    public Book findBookById(Integer bookId) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_BOOK_FIND_BY_ID);
        request.addProperty("BookId", bookId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            httpTransport.call(ACTION_BOOK_FIND_BY_ID, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }

        SoapObject result = (SoapObject) response;

        return new Book((SoapObject) result.getProperty("Book"));
    }

    public VectorBook findBookAll() throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_BOOK_FIND_ALL);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try {
            httpTransport.call(ACTION_BOOK_FIND_ALL, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }

        return new VectorBook((SoapObject) response);
    }

    public void updateBook(Book object) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_BOOK_UPDATE);
        request.addProperty("Book", object);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.addMapping(NAMESPACE, "Book", Book.class);
        envelope.setOutputSoapObject(request);

        new MarshalDate().register(envelope);
        new MarshalBase64().register(envelope);

        try {
            httpTransport.call(ACTION_BOOK_UPDATE, envelope);
        } catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }
    }

    public void deleteBook(Integer bookId) throws BookstoreException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_BOOK_DELETE);
        request.addProperty("BookId", bookId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        new MarshalDate().register(envelope);
        new MarshalBase64().register(envelope);

        try {
            httpTransport.call(ACTION_BOOK_DELETE_BY_ID, envelope);
        }catch (Exception e) {
            throw new BookstoreException(BookstoreFault.ERROR_SERVER, e.getMessage());
        }

        Object response = envelope.bodyIn;

        if (response instanceof SoapFault) {
            throw new BookstoreException((SoapFault) response);
        }
    }

}
