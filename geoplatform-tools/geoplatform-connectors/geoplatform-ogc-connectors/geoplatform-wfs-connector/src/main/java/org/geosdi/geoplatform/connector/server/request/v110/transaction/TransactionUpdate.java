/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-platform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2013 geoSDI Group (CNR IMAA - Potenza - ITALY).
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
package org.geosdi.geoplatform.connector.server.request.v110.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.geosdi.geoplatform.connector.server.request.ITransactionOperationStrategy;
import org.geosdi.geoplatform.connector.server.request.WFSTransactionRequest;
import org.geosdi.geoplatform.exception.IllegalParameterFault;
import org.geosdi.geoplatform.gui.responce.AttributeDTO;
import org.geosdi.geoplatform.xml.filter.v110.AbstractIdType;
import org.geosdi.geoplatform.xml.filter.v110.FeatureIdType;
import org.geosdi.geoplatform.xml.filter.v110.FilterType;
import org.geosdi.geoplatform.xml.wfs.v110.PropertyType;
import org.geosdi.geoplatform.xml.wfs.v110.UpdateElementType;

/**
 *
 * @author Vincenzo Monteverde <vincenzo.monteverde@geosdi.org>
 */
public class TransactionUpdate implements ITransactionOperationStrategy {

    private org.geosdi.geoplatform.xml.filter.v110.ObjectFactory filterFactory;

    public TransactionUpdate() {
        filterFactory = new org.geosdi.geoplatform.xml.filter.v110.ObjectFactory();
    }

    @Override
    public Object getOperation(WFSTransactionRequest request)
            throws IllegalParameterFault {
        UpdateElementType elementType = new UpdateElementType();

        assert (request.getTypeName() != null);
        elementType.setTypeName(request.getTypeName());

        List<PropertyType> properties = this.getPropertyToUpdate(request.getAttributes());
        elementType.setProperty(properties);

        if (request.getSRS() != null) {
            elementType.setSrsName(request.getSRS());
        }

        elementType.setInputFormat(request.getInputFormat() != null
                ? request.getInputFormat() : "x-application/gml:3");

        assert (request.getFID() != null);
        FeatureIdType fid = new FeatureIdType();
        fid.setFid(request.getFID());

        JAXBElement<FeatureIdType> fidElement = filterFactory.createFeatureId(fid);

        FilterType filter = new FilterType();
        filter.setId(Arrays.<JAXBElement<? extends AbstractIdType>>asList(fidElement));

        elementType.setFilter(filter);

        return elementType;
    }

    private List<PropertyType> getPropertyToUpdate(List<AttributeDTO> attributes)
            throws IllegalParameterFault {
        assert (attributes != null && attributes.size() > 0);

        List<PropertyType> properties = new ArrayList<PropertyType>(attributes.size());
        for (AttributeDTO attribute : attributes) {
            if (!attribute.isNillable() && attribute.getValue() == null) {
                throw new IllegalParameterFault("Property '" + attribute.getName() + "' cannot be null.");
            }

            PropertyType property = new PropertyType();
            QName qName = new QName(attribute.getName());
            property.setName(qName);
            property.setValue(attribute.getValue()); // TODO Check wrt type
            properties.add(property);
        }

        return properties;
    }
}
