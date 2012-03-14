/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-platform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2012 geoSDI Group (CNR IMAA - Potenza - ITALY).
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. This program is distributed in the 
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. You should have received a copy of the GNU General 
 * Public License along with this program. If not, see http://www.gnu.org/licenses/ 
 *
 * ====================================================================
 *
 * Linking this library statically or dynamically with other modules is 
 * making a combined work based on this library. Thus, the terms and 
 * conditions of the GNU General Public License cover the whole combination. 
 * 
 * As a special exception, the copyright holders of this library give you permission 
 * to link this library with independent modules to produce an executable, regardless 
 * of the license terms of these independent modules, and to copy and distribute 
 * the resulting executable under terms of your choice, provided that you also meet, 
 * for each linked independent module, the terms and conditions of the license of 
 * that module. An independent module is a module which is not derived from or 
 * based on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obligated to do so. If you do not 
 * wish to do so, delete this exception statement from your version. 
 *
 */
package org.geosdi.geoplatform.gui.impl.containers.pagination.listview;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.widget.ListView;
import org.geosdi.geoplatform.gui.containers.pagination.LayoutPaginationContainer;
import org.geosdi.geoplatform.gui.model.GeoPlatformBeanModel;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 */
public abstract class ListViewLayoutPaginationContainer<T extends GeoPlatformBeanModel>
        extends LayoutPaginationContainer<ListView<T>, T> {

    protected ListView<T> listView;

    public ListViewLayoutPaginationContainer(boolean lazy, int thePageSize) {
        super(lazy, thePageSize);
    }

    public ListViewLayoutPaginationContainer(boolean lazy) {
        super(lazy);
    }

    @Override
    public ListView<T> initWidget() {
        listView = new ListView<T>();
        listView.addStyleName("overview-page");
        listView.setItemSelector(".project-box");
        listView.setOverStyle("sample-over");
        listView.setSelectStyle("none");
        listView.setBorders(false);
        listView.setStore(store);

        setListViewProperties();

        return listView;
    }

    public abstract void changeSelection(SelectionChangedEvent<T> se);

    public abstract void setListViewProperties();
}
