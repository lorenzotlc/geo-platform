/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-plartform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2011 geoSDI Group (CNR IMAA - Potenza - ITALY).
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
package org.geosdi.geoplatform.gui.client.widget;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import java.util.ArrayList;
import java.util.List;
import org.geosdi.geoplatform.gui.client.ServerWidgetResources;
import org.geosdi.geoplatform.gui.client.model.GPLayerBeanModel.GPLayerBeanKeyValue;
import org.geosdi.geoplatform.gui.client.widget.expander.GPServerExpander;
import org.geosdi.geoplatform.gui.client.widget.grid.GeoPlatformGridWidget;
import org.geosdi.geoplatform.gui.configuration.message.GeoPlatformMessage;
import org.geosdi.geoplatform.gui.model.GPLayerBean;
import org.geosdi.geoplatform.gui.model.tree.AbstractFolderTreeNode;

/**
 *
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email  giuseppe.lascaleia@geosdi.org
 */
public class GridLayersWidget<L extends GPLayerBean> extends GeoPlatformGridWidget<L> {

    private FormPanel formPanel;
    private TreePanel tree;
    private Button done;
    private RowExpander rowExpander;
    private DisplayServerWidget displayWidget;
    private GPServerExpander expander;

    /**
     * @Constructor
     */
    public GridLayersWidget(TreePanel theTree) {
        super(false);
        this.initServerWidget();
        this.initFormPanel();
        this.tree = theTree;
        this.expander = new GPServerExpander(this);
    }

    private void initServerWidget() {
        this.displayWidget = new DisplayServerWidget(this);
    }

    private void initFormPanel() {
        this.formPanel = new FormPanel();
        formPanel.setHeaderVisible(false);
        formPanel.setFrame(true);
        formPanel.setLayout(new FlowLayout());

        this.formPanel.setTopComponent(this.displayWidget.getToolbar());

        this.formPanel.add(this.grid);

        this.formPanel.setButtonAlign(HorizontalAlignment.RIGHT);

        this.done = new Button("Done", ServerWidgetResources.ICONS.done());

        this.done.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (getTree().getSelectionModel().getSelectedItem() instanceof AbstractFolderTreeNode) {
                    expander.checkNodeState();
                } else {
                    GeoPlatformMessage.alertMessage("GPCababilitiesWidget",
                            "You can put layers into Folders only."
                            + "Please select the correct node");
                }
            }
        });

        this.done.disable();

        this.formPanel.getButtonBar().add(this.done);
    }

    @Override
    public void setGridProperties() {
        grid.setAutoExpandColumn(GPLayerBeanKeyValue.GPLAYER_NAME.getValue());
        grid.setBorders(false);

        grid.getView().setForceFit(true);
        grid.setLoadMask(true);

        grid.addPlugin(this.rowExpander);

        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);

        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                done.enable();
            }
        });
    }

    @Override
    public ColumnModel prepareColumnModel() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        XTemplate tpl = XTemplate.create(
                "<p><b>Abstract:</b> {label}</p>");

        rowExpander = new RowExpander(tpl);

        configs.add(rowExpander);

        ColumnConfig name = new ColumnConfig();
        name.setId(GPLayerBeanKeyValue.GPLAYER_NAME.getValue());
        name.setHeader("Layer Name");
        name.setWidth(200);
        configs.add(name);

        ColumnConfig title = new ColumnConfig();
        title.setId(GPLayerBeanKeyValue.GPLAYER_LABEL.getValue());
        title.setHeader("Title");
        title.setWidth(150);
        configs.add(title);

        return new ColumnModel(configs);
    }

    @Override
    public void createStore() {
        this.store = new ListStore<L>();
    }

    /**
     * 
     */
    public void loadServers() {
        this.displayWidget.loadServers();
    }

    /**
     * Clean The Store
     */
    public void cleanUpTheStore() {
        if (this.grid.getView().getBody().isMasked()) {
            unMaskGrid();
        }
        this.store.removeAll();
    }

    /**
     * Create mask effect on Grid
     */
    public void maskGrid() {
        this.grid.getView().getBody().mask("Loading Layers");
    }

    /**
     * Remove mask effect from the Grid
     */
    public void unMaskGrid() {
        this.grid.getView().getBody().unmask();
    }

    /**
     *
     * @param beans
     *            {@link ArrayList} of GeocodingBean to fill the Store
     */
    public void fillStore(ArrayList<L> beans) {
        this.store.add(beans);
    }

    /**
     * 
     */
    public void cleanStore() {
        this.store.removeAll();
    }

    public void resetComponents() {
        this.store.removeAll();
        unMaskGrid();
        this.done.disable();
        this.displayWidget.resetComponents();
    }

    /**
     * @return the formPanel
     */
    public FormPanel getFormPanel() {
        return formPanel;
    }

    /**
     * @return the tree
     */
    public TreePanel getTree() {
        return tree;
    }

    public List<L> getSelectedItems() {
        return this.grid.getSelectionModel().getSelectedItems();
    }
}