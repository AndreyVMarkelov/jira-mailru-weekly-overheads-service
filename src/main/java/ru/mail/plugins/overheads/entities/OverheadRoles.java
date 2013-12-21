/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.entities;


import net.java.ao.Entity;
import net.java.ao.Preload;


@Preload
public interface OverheadRoles extends Entity
{
    void setMasterRole(Long id);

    Long getMasterRole();

    void setDetailRole(Long id);

    Long getDetailRole();
}
