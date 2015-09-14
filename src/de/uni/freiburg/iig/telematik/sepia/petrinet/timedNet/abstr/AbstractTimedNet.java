/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uni.freiburg.iig.telematik.sepia.petrinet.timedNet.abstr;

import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPetriNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.abstr.AbstractPTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.timedNet.concepts.ResourceContext;
import de.uni.freiburg.iig.telematik.sepia.petrinet.timedNet.concepts.TimeRessourceContext;
import de.uni.freiburg.iig.telematik.sewol.context.process.ProcessContext;

/**
 *
 * @author richard
 */
public abstract class AbstractTimedNet<P extends AbstractTimedPlace<F>, T extends AbstractTimedTransition<F>, F extends AbstractTimedFlowRelation<P, T>, M extends AbstractTimedMarking>
        extends AbstractPTNet<P, T, F, M> {

    protected double clock = 0;

    private ResourceContext accessContext;
    
    private ProcessContext accessControl;

    public ProcessContext getAccessControl() {
		return accessControl;
	}

	public void setAccessControl(ProcessContext accessControl) {
		this.accessControl = accessControl;
	}

	private TimeRessourceContext timeRessourceContext;

    public TimeRessourceContext getTimeRessourceContext() {
        return timeRessourceContext;
    }

    public void setTimeRessourceContext(TimeRessourceContext timeRessourceContext) {
        this.timeRessourceContext = timeRessourceContext;
    }

    public void setResourceContext(ResourceContext accessContext) {
        this.accessContext = accessContext;
        for (T transition : getTransitions()) {
            transition.setAccessContext(accessContext);
        }
    }

    public ResourceContext getResourceContext() {
        return accessContext;
    }

    @Override
    public boolean addTransition(String transitionName) {
        boolean result = super.addTransition(transitionName); //To change body of generated methods, choose Tools | Templates.
        getTransition(transitionName).setAccessContext(accessContext);
        return result;
    }

}