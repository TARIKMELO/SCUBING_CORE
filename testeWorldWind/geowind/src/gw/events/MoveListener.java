/*
 * $Id: MoveListener.java 4 2008-07-21 17:57:48Z od $
 * 
 * This file is a part of the GeoWind package, a library for visualizing
 * data from GeoTools in Nasa WorldWind.
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *     1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 * 
 *     2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 * 
 *     3. This notice may not be removed or altered from any source
 *     distribution.
 */
package gw.events;

import gov.nasa.worldwind.event.PositionEvent;
import java.util.EventListener;

/**
 * Move Listener for mouse events.
 * 
 * @author Ian Overgard
 * @author Olaf David
 *
 */
public interface MoveListener extends EventListener {

    /** The mouse was moved
     * 
     * @param event the position event.
     */
    public void moved(PositionEvent event);
}
