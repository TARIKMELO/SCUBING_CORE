/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.gdal;

/**
 * @author garakl
 * @version $Id: Format.java 1 2011-07-16 23:22:47Z dcollins $
 */

public abstract class Format extends Option
{
    public Format( String value )
    {
        super( null, value );
    }
}