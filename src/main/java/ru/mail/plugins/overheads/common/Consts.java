/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.common;



public class Consts
{
    public static final String PLUGIN_KEY_MAILRU_OVERHEADS = "MAILRU-OVERHEADS";
    
    public static String getConstant(String key)
    {
        return OverheadsUtils.getConstantValue(key);
    }
}
