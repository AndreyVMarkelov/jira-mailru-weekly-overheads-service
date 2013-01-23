/*
 * Created by Dmitry Miroshnichenko 30-11-2012. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.common;


import java.util.Collection;

import javax.servlet.http.HttpServletRequest;


public class Utils
{
    public static String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":"
            + req.getServerPort() + req.getContextPath());
    }

    public static boolean isValidStr(String str)
    {
        return (str != null && str.length() > 0);
    }

    public static boolean isEmpty(Collection<?> collection)
    {
        return collection == null || collection.size() <= 0;
    }

    public static boolean isEmpty(Object[] array)
    {
        return array == null || array.length <= 0;
    }

    private Utils()
    {

    }
}
