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

import java.awt.GridLayout;

import java.text.DateFormat;

import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * @author Chris Tilden
 * @author muti
 * @version $Id$
 */
public class RecentPanel extends JPanel {
    /**
     * Required for serializable classes.
     */
    public static final long serialVersionUID = 200705252321L;
    private JTable table;
    private RecentModel model;

    /**
     * Constructs the panel for recently played tracks.
     */
    public RecentPanel() {
        super(new GridLayout(1, 1));

        this.model = new RecentModel();
        this.table = new JTable(this.model);
        this.table.getColumnModel().getColumn(0).setMaxWidth(30);
        this.table.getColumnModel().getColumn(1).setMaxWidth(60);
        this.table.getColumnModel().getColumn(5).setMaxWidth(60);
        this.table.getColumnModel().getColumn(1)
                  .setCellRenderer(table.getDefaultRenderer(Boolean.class));
        this.table.getColumnModel().getColumn(1).setCellEditor(table.getDefaultEditor(Boolean.class));

        JScrollPane scrollpane = new JScrollPane(this.table);

        add(scrollpane);
    }

    public void newTrackListAvailable(List recentlyPlayed) {
        model.setRecentlyPlayed(recentlyPlayed);
        model.fireTableDataChanged();
    }

    private class RecentModel extends AbstractTableModel {
        /**
         * Required for serializable classes.
         */
        public static final long serialVersionUID = 200705252320L;

        /**
         * A list of recently played tracks.
         */
        private List recentlyPlayed = null;
        private String[] columnData =
            new String[] { "#", "Submit", "Artist", "Album", "Track", "Length", "Play Time" };

        /**
         * Sets the list of recenlty played tracks.
         * @param recentlyPlayed  The list of recently played tracks.
         */
        public void setRecentlyPlayed(List recentlyPlayed) {
            this.recentlyPlayed = recentlyPlayed;
        }

        public int getColumnCount() {
            return this.columnData.length;
        }

        public int getRowCount() {
            if (recentlyPlayed != null) {
                return recentlyPlayed.size();
            }

            return 0;
        }

        public String getColumnName(int col) {
            return this.columnData[col];
        }

        public boolean isCellEditable(int row, int col) {
            /* Only column 1 is editable. */
            return (col == 1);
        }

        public Object getValueAt(int row, int col) {
            TrackItem track;

            if (recentlyPlayed != null) {
                track = (TrackItem) recentlyPlayed.get(row);
            } else {
                return new Object();
            }

            switch (col) {
            case 0:
                return new Integer(row + 1);

            case 1:
                return track.isActive();

            case 2:
                return track.getArtist();

            case 3:
                return track.getAlbum();

            case 4:
                return track.getTrack();

            case 5:
                return this.convertMS(track.getLength());

            case 6:

                Date date = new Date(track.getLastplayed() * 1000);

                return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
                                 .format(date);
            }

            return new Object(); //if not found, return empty object
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == 1) {
                TrackItem track;

                if (!(value instanceof Boolean)) {
                    throw new RuntimeException("Active must be a Boolean.");
                }

                if (recentlyPlayed == null) {
                    throw new RuntimeException("Recent Played list is NULL!");
                }

                track = (TrackItem) recentlyPlayed.get(row);
                track.setActive(((Boolean) value));
                fireTableCellUpdated(row, col);
            }
        }

        private String convertMS(long length) {
            long minutes = length / 60;
            String seconds = new Long(length - (minutes * 60)).toString();

            if (seconds.length() == 1) {
                seconds = "0" + seconds;
            }

            return minutes + ":" + seconds;
        }
    }
}
