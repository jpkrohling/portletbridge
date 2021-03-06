/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.richfaces.simple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.richfaces.component.SortOrder;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@ManagedBean
@ViewScoped
public class UserSorter implements Serializable {

    private static final long serialVersionUID = -9189227920939669284L;

    private Map<String, SortOrder> sortsOrders;
    private List<String> sortPriorities;

    private boolean multipleSorting = false;

    private static final String SORT_PROPERTY_PARAMETER = "sortProperty";

    public UserSorter() {
        sortsOrders = new HashMap<String, SortOrder>();
        sortPriorities = new ArrayList<String>();
    }

    public void sort() {
        String property = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get(SORT_PROPERTY_PARAMETER);
        if (property != null) {
            SortOrder currentPropertySortOrder = sortsOrders.get(property);
            if (multipleSorting) {
                if (!sortPriorities.contains(property)) {
                    sortPriorities.add(property);
                }
            } else {
                sortsOrders.clear();
            }
            if (currentPropertySortOrder == null || currentPropertySortOrder.equals(SortOrder.descending)) {
                sortsOrders.put(property, SortOrder.ascending);
            } else {
                sortsOrders.put(property, SortOrder.descending);
            }
        }
    }

    public void modeChanged(ValueChangeEvent event) {
        reset();
    }

    public void reset() {
        sortPriorities.clear();
        sortsOrders.clear();
    }

    public boolean isMultipleSorting() {
        return multipleSorting;
    }

    public void setMultipleSorting(boolean multipleSorting) {
        this.multipleSorting = multipleSorting;
    }

    public List<String> getSortPriorities() {
        return sortPriorities;
    }

    public Map<String, SortOrder> getSortsOrders() {
        return sortsOrders;
    }
}
