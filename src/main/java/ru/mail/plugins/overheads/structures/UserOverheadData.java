/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.structures;


public class UserOverheadData
{
    private String overhead;

    private String qaName;

    public String getOverhead()
    {
        return overhead;
    }

    public void setOverhead(String overhead)
    {
        this.overhead = overhead;
    }

    public String getQaName()
    {
        return qaName;
    }

    public void setQaName(String qaName)
    {
        this.qaName = qaName;
    }
}
