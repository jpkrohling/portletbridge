/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.portletbridge.example;

import org.jboss.portletbridge.bridge.context.BridgeContext;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Juraci Paixão Kröhling
 */
public class CustomLifecycleImpl extends Lifecycle {
    private Lifecycle delegated;
    private ThreadLocal<List<PhaseListener>> requestPhaseListenersThreadLocal = new ThreadLocal<List<PhaseListener>>();

    public CustomLifecycleImpl(Lifecycle delegated) {
        this.delegated = delegated;
    }

    @Override
    public void addPhaseListener(PhaseListener listener) {
        if (null == BridgeContext.getCurrentInstance()) { // this should be quick and easy to determine whether we are being called during init or request
            System.out.println("Adding phase listener to delegated: " + listener.getClass().getName() + " for phase " + listener.getPhaseId());
            delegated.addPhaseListener(listener);
        } else {
            if (null == requestPhaseListenersThreadLocal.get()) {
                requestPhaseListenersThreadLocal.set(new ArrayList<PhaseListener>());
            }

            List<PhaseListener> listeners = requestPhaseListenersThreadLocal.get();
            if (!listeners.contains(listener)) {
                System.out.println("Adding listener " + listener.toString());
                requestPhaseListenersThreadLocal.get().add(listener);
            }
        }
    }

    @Override
    public void execute(FacesContext context) throws FacesException {
        delegated.execute(context);
    }

    @Override
    public void attachWindow(FacesContext context) {
        delegated.attachWindow(context);
    }

    @Override
    public PhaseListener[] getPhaseListeners() {
        List<PhaseListener> listeners = new ArrayList<PhaseListener>(requestPhaseListenersThreadLocal.get());
        Collections.addAll(listeners, delegated.getPhaseListeners());
        return listeners.toArray(new PhaseListener[listeners.size()]);
    }

    @Override
    public void removePhaseListener(PhaseListener listener) {
        System.out.println("Removing phase listener: " + listener.getClass().getName());
        delegated.removePhaseListener(listener);
        requestPhaseListenersThreadLocal.get().remove(listener);
    }

    @Override
    public void render(FacesContext context) throws FacesException {
        delegated.render(context);
    }
}
