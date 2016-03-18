
package com.thomsonreuters.wokmws.cxf.woksearch;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "SessionException", targetNamespace = "http://woksearch.cxf.wokmws.thomsonreuters.com")
public class SessionException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private SessionException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public SessionException_Exception(String message, SessionException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public SessionException_Exception(String message, SessionException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: com.thomsonreuters.wokmws.cxf.woksearch.SessionException
     */
    public SessionException getFaultInfo() {
        return faultInfo;
    }

}
