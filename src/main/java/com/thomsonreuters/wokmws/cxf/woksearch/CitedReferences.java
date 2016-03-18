
package com.thomsonreuters.wokmws.cxf.woksearch;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for citedReferences complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="citedReferences">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="databaseId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="uid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="editions" type="{http://woksearch.cxf.wokmws.thomsonreuters.com}editionDesc" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="timeSpan" type="{http://woksearch.cxf.wokmws.thomsonreuters.com}timeSpan" minOccurs="0"/>
 *         &lt;element name="queryLanguage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retrieveParameters" type="{http://woksearch.cxf.wokmws.thomsonreuters.com}retrieveParameters" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "citedReferences", propOrder = {
    "databaseId",
    "uid",
    "editions",
    "timeSpan",
    "queryLanguage",
    "retrieveParameters"
})
public class CitedReferences {

    protected String databaseId;
    protected String uid;
    protected List<EditionDesc> editions;
    protected TimeSpan timeSpan;
    protected String queryLanguage;
    protected RetrieveParameters retrieveParameters;

    /**
     * Gets the value of the databaseId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * Sets the value of the databaseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatabaseId(String value) {
        this.databaseId = value;
    }

    /**
     * Gets the value of the uid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the value of the uid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUid(String value) {
        this.uid = value;
    }

    /**
     * Gets the value of the editions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the editions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEditions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EditionDesc }
     * 
     * 
     */
    public List<EditionDesc> getEditions() {
        if (editions == null) {
            editions = new ArrayList<EditionDesc>();
        }
        return this.editions;
    }

    /**
     * Gets the value of the timeSpan property.
     * 
     * @return
     *     possible object is
     *     {@link TimeSpan }
     *     
     */
    public TimeSpan getTimeSpan() {
        return timeSpan;
    }

    /**
     * Sets the value of the timeSpan property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeSpan }
     *     
     */
    public void setTimeSpan(TimeSpan value) {
        this.timeSpan = value;
    }

    /**
     * Gets the value of the queryLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryLanguage() {
        return queryLanguage;
    }

    /**
     * Sets the value of the queryLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryLanguage(String value) {
        this.queryLanguage = value;
    }

    /**
     * Gets the value of the retrieveParameters property.
     * 
     * @return
     *     possible object is
     *     {@link RetrieveParameters }
     *     
     */
    public RetrieveParameters getRetrieveParameters() {
        return retrieveParameters;
    }

    /**
     * Sets the value of the retrieveParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link RetrieveParameters }
     *     
     */
    public void setRetrieveParameters(RetrieveParameters value) {
        this.retrieveParameters = value;
    }

}
