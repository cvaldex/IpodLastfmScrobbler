/*
 * LastPod is an application used to publish one's iPod play counts to Last.fm.
 * Copyright (C) 2007  Chris Tilden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.lastpod;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;

/**
 * @author muti
 * @version $Id$
 */
public class LogHandler extends Handler {
    /**
     * The user interface.
     */
    private UI userInterface = null;

    /**
     * Constructs this object.
     * @param userInterface  The user interface.
     */
    public LogHandler(UI userInterface) {
        this.userInterface = userInterface;
    }

    public void close() {
        return;
    }

    public void flush() {
        return;
    }

    public void publish(LogRecord record) {
        if ((getFilter() != null) && !getFilter().isLoggable(record)) {
            return;
        }

        JTextArea logtextarea = userInterface.getLogtextarea();
        logtextarea.append(record.getMessage() + "\n");
    }
}
