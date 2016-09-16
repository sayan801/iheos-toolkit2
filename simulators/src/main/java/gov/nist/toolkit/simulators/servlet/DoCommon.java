package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.http.HttpHeader;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.utilities.io.Io;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DoCommon {
    HttpServletRequest request;
    HttpServletResponse response;
    static ServletConfig config;
    Map<String, String> headers = new HashMap<String, String>();
    String contentType;
    HttpHeader contentTypeHeader;
    String bodyCharset;

    DoCommon(ServletConfig config) {
        this.config = config;
    }

    void logRequest(HttpServletRequest request, SimDb db, String actor, String transaction)
            throws FileNotFoundException, IOException, HttpHeader.HttpHeaderParseException, ParseException {
        StringBuffer buf = new StringBuffer();

        buf.append(request.getMethod() + " " + request.getRequestURI() + " " + request.getProtocol() + "\r\n");
        for (Enumeration<String> en = request.getHeaderNames(); en.hasMoreElements(); ) {
            String name = en.nextElement();
            String value = request.getHeader(name);
            if (name.equals("Transfer-Encoding"))
                continue;  // log will not include transfer encoding so don't include this
            headers.put(name.toLowerCase(), value);
            buf.append(name).append(": ").append(value).append("\r\n");
        }
        //		bodyCharset = request.getCharacterEncoding();
        String ctype = headers.get("content-type");
        if (ctype == null || ctype.equals(""))
            throw new IOException("Content-Type header not found");
        contentTypeHeader = new HttpHeader("content-type: " + ctype);
        bodyCharset = contentTypeHeader.getParam("charset");
        contentType = contentTypeHeader.getValue();

        if (bodyCharset == null || bodyCharset.equals(""))
            bodyCharset = "UTF-8";

        buf.append("\r\n");


        db.putRequestHeaderFile(buf.toString().getBytes());

        db.putRequestBodyFile(Io.getBytesFromInputStream(request.getInputStream()));

    }

    void closeOut(HttpServletResponse response) {
        try {
            response.getOutputStream().close();
        } catch (IOException e) {

        }
    }
}
