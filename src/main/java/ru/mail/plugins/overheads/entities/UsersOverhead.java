/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.entities;


import net.java.ao.Entity;
import net.java.ao.Preload;


@Preload
public interface UsersOverhead extends Entity
{
    void setOverhead(Long overhead);

    Long getOverhead();

    void setUserName(String userName);

    String getUserName();

    void setQaName(String qaName);

    String getQaName();
}
