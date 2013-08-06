/*
 * Created by Dmitry Miroshnichenko 11-03-2012. Copyright Mail.Ru Group 2013.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.rest;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class OverheadDataEntity
{
    @XmlElement
    String time;

    @XmlElement
    String qa;

    public OverheadDataEntity()
    {
    }
    
    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getQa()
    {
        return qa;
    }

    public void setQa(String qa)
    {
        this.qa = qa;
    }
}
