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
package org.geosdi.geoplatform.connector.server.request;

import java.util.List;
import javax.xml.namespace.QName;
import org.geosdi.geoplatform.connector.server.GPServerConnector;
import org.geosdi.geoplatform.gui.responce.AttributeDTO;
import org.geosdi.geoplatform.gui.shared.wfs.TransactionOperation;

/**
 *
 * @author Vincenzo Monteverde <vincenzo.monteverde@geosdi.org>
 */
public abstract class AbstractTransactionRequest<T> extends WFSRequest<T>
        implements WFSTransactionRequest<T> {

    protected TransactionOperation operation;
    protected QName typeName;
    protected String srs;
    protected String inputFormat;
    protected String fid;
    protected List<AttributeDTO> attributes;

    public AbstractTransactionRequest(GPServerConnector server) {
        super(server);
    }

    @Override
    public TransactionOperation getOperation() {
        return operation;
    }

    @Override
    public void setOperation(TransactionOperation operation) {
        this.operation = operation;
    }

    @Override
    public QName getTypeName() {
        return typeName;
    }

    @Override
    public void setTypeName(QName typeName) {
        this.typeName = typeName;
    }

    @Override
    public String getSRS() {
        return srs;
    }

    @Override
    public void setSRS(String srs) {
        this.srs = srs;
    }

    @Override
    public String getInputFormat() {
        return inputFormat;
    }

    @Override
    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    @Override
    public String getFID() {
        return fid;
    }

    @Override
    public void setFID(String fid) {
        this.fid = fid;
    }

    @Override
    public List<AttributeDTO> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(List<AttributeDTO> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{"
                + "typeName=" + typeName
                + ", srs=" + srs
                + ", outputFormat=" + inputFormat
                + ", FID=" + fid
                + ", attributes=" + attributes
                + '}';
    }
}
