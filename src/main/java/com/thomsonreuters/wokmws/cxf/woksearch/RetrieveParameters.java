
package com.thomsonreuters.wokmws.cxf.woksearch;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for retrieveParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="retrieveParameters">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="collectionFields" type="{http://woksearch.cxf.wokmws.thomsonreuters.com}collectionFields" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="fields" type="{http://woksearch.cxf.wokmws.thomsonreuters.com}queryField" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="firstRecord" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="options" type="{http://woksearch.cxf.wokmws.thomsonreuters.com}keyValuePair" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "retrieveParameters", propOrder = {
    "collectionFields",
    "count",
    "fields",
    "firstRecord",
    "options"
})
public class RetrieveParameters {

    @XmlElement(nillable = true)
    protected List<CollectionFields> collectionFields;
    protected int count;
    @XmlElement(nillable = true)
    protected List<QueryField> fields;
    protected int firstRecord;
    @XmlElement(nillable = true)
    protected List<KeyValuePair> options;

    /**
     * Gets the value of the collectionFields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the collectionFields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCollectionFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CollectionFields }
     * 
     * 
     */
    public List<CollectionFields> getCollectionFields() {
        if (collectionFields == null) {
            collectionFields = new ArrayList<CollectionFields>();
        }
        return this.collectionFields;
    }

    /**
     * Gets the value of the count property.
     * 
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     */
    public void setCount(int value) {
        this.count = value;
    }

    /**
     * Gets the value of the fields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QueryField }
     * 
     * 
     */
    public List<QueryField> getFields() {
        if (fields == null) {
            fields = new ArrayList<QueryField>();
        }
        return this.fields;
    }

    /**
     * Gets the value of the firstRecord property.
     * 
     */
    public int getFirstRecord() {
        return firstRecord;
    }

    /**
     * Sets the value of the firstRecord property.
     * 
     */
    public void setFirstRecord(int value) {
        this.firstRecord = value;
    }

    /**
     * Gets the value of the options property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the options property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOptions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeyValuePair }
     * 
     * 
     */
    public List<KeyValuePair> getOptions() {
        if (options == null) {
            options = new ArrayList<KeyValuePair>();
        }
        return this.options;
    }

}
