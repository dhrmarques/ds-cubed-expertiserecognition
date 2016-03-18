
package com.thomsonreuters.wokmws.cxf.woksearch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for citedReference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="citedReference">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="articleID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="citedAuthor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="citedTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="citedWork" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="recID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="refID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timesCited" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="volume" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="year" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "citedReference", propOrder = {
    "articleID",
    "citedAuthor",
    "citedTitle",
    "citedWork",
    "page",
    "recID",
    "refID",
    "timesCited",
    "volume",
    "year"
})
public class CitedReference {

    protected String articleID;
    protected String citedAuthor;
    protected String citedTitle;
    protected String citedWork;
    protected String page;
    protected String recID;
    protected String refID;
    protected String timesCited;
    protected String volume;
    protected String year;

    /**
     * Gets the value of the articleID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArticleID() {
        return articleID;
    }

    /**
     * Sets the value of the articleID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArticleID(String value) {
        this.articleID = value;
    }

    /**
     * Gets the value of the citedAuthor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitedAuthor() {
        return citedAuthor;
    }

    /**
     * Sets the value of the citedAuthor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitedAuthor(String value) {
        this.citedAuthor = value;
    }

    /**
     * Gets the value of the citedTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitedTitle() {
        return citedTitle;
    }

    /**
     * Sets the value of the citedTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitedTitle(String value) {
        this.citedTitle = value;
    }

    /**
     * Gets the value of the citedWork property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitedWork() {
        return citedWork;
    }

    /**
     * Sets the value of the citedWork property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitedWork(String value) {
        this.citedWork = value;
    }

    /**
     * Gets the value of the page property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPage(String value) {
        this.page = value;
    }

    /**
     * Gets the value of the recID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecID() {
        return recID;
    }

    /**
     * Sets the value of the recID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecID(String value) {
        this.recID = value;
    }

    /**
     * Gets the value of the refID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefID() {
        return refID;
    }

    /**
     * Sets the value of the refID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefID(String value) {
        this.refID = value;
    }

    /**
     * Gets the value of the timesCited property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimesCited() {
        return timesCited;
    }

    /**
     * Sets the value of the timesCited property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimesCited(String value) {
        this.timesCited = value;
    }

    /**
     * Gets the value of the volume property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVolume() {
        return volume;
    }

    /**
     * Sets the value of the volume property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVolume(String value) {
        this.volume = value;
    }

    /**
     * Gets the value of the year property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYear(String value) {
        this.year = value;
    }

}
