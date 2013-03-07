package util;

import exception.PNException;
import graph.Graph;
import graph.exception.VertexNotFoundException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import petrinet.AbstractFlowRelation;
import petrinet.AbstractMarking;
import petrinet.AbstractPetriNet;
import petrinet.AbstractPetriNet.Boundedness;
import petrinet.AbstractPlace;
import petrinet.AbstractTransition;
import validate.ParameterException;
import validate.ParameterException.ErrorCode;
import validate.Validate;

/**
 * This class provides methods concerned with reachability issues of Petri nets.<br>
 * It allows to build the reachability graph of a Petri net containing all reachable markings and their relation,<br>
 * but also provides methods for checking reachability oriented properties of Petri nets,<br>
 * such as dead transitions.
 * 
 * @author Thomas Stocker
 */
public class ReachabilityUtils {
	
	/**
	 * Checks if the transition within the given net with the given name is dead,<br>
	 * i.e. if there is not reachable marking where the transition is enabled.<br>
	 * <br>
	 * Attention: Only works for bounded nets, i.e. nets for which the {@link AbstractPetriNet#isBounded()} method returns {@link Boundedness#BOUNDED}<br>
	 * If the boundedness property is set explicitly, the method may not halt.
	 * 
	 * @param petriNet The basic Petri net.
	 * @param transitionName The name of the transition of interest.
	 * @return <code>true</code> if the transition is dead;<br>
	 * <code>false</code> otherwise.
	 * @throws ParameterException If some parameters are <code>null</code>, the Petri net
	 * does not contain a transition with the given name or the given net is not bounded.
	 * 
	 * @see #getDeadTransitions(AbstractPetriNet)
	 */
	public static <P extends AbstractPlace<F,S>, 
	   			   T extends AbstractTransition<F,S>, 
	   			   F extends AbstractFlowRelation<P,T,S>, 
	   			   M extends AbstractMarking<S>, 
	   			   S extends Object> 
	
				   boolean isDead(AbstractPetriNet<P,T,F,M,S> petriNet, String transitionName) 
						   throws ParameterException{
		
		Validate.notNull(petriNet);
		Validate.notNull(transitionName);
		if(!petriNet.containsTransition(transitionName))
			throw new ParameterException(ErrorCode.INCOMPATIBILITY, "Unknown transition");
		
		T transition = petriNet.getTransition(transitionName);
		return getDeadTransitions(petriNet).contains(transition);
	}
	
	/**
	 * Checks if the given Petri net contains dead transitions,<br>
	 * i.e. transitions which are never enabled (in no reachable marking).<br>
	 * <br>
	 * Attention: Only works for bounded nets, i.e. nets for which the {@link AbstractPetriNet#isBounded()} method returns {@link Boundedness#BOUNDED}<br>
	 * If the boundedness property is set explicitly, the method may not halt.
	 * 
	 * @param petriNet The basic Patri net for operation.
	 * @return <code>true</code> if the Petri net contains dead transitions;<br>
	 * <code>false</code> otherwise.
	 * @throws ParameterException If the Petri net parameter is <code>null</code> or the given net is not bounded.
	 * 
	 * @see #getDeadTransitions(AbstractPetriNet)
	 */
	public static <P extends AbstractPlace<F,S>, 
	   T extends AbstractTransition<F,S>, 
	   F extends AbstractFlowRelation<P,T,S>, 
	   M extends AbstractMarking<S>, 
	   S extends Object> 

	   boolean containsDeadTransitions(AbstractPetriNet<P,T,F,M,S> petriNet) 
			   throws ParameterException{
		
		return !getDeadTransitions(petriNet).isEmpty();
	}
	
	/**
	 * REturns all dead transition of the given Petri net,<br>
	 * i.e. all transitions which are never enabled (in no reachable marking).<br>
	 * <br>
	 * Attention: Only works for bounded nets, i.e. nets for which the {@link AbstractPetriNet#isBounded()} method returns {@link Boundedness#BOUNDED}<br>
	 * If the boundedness property is set explicitly, the method may not halt.
	 *  
	 * 
	 * @param petriNet The basic Petri net for operation
	 * @return A set of all dead transitions.
	 * @throws ParameterException If the Petri net parameter is <code>null</code> or the given net is not bounded.
	 */
	public static <P extends AbstractPlace<F,S>, 
	   T extends AbstractTransition<F,S>, 
	   F extends AbstractFlowRelation<P,T,S>, 
	   M extends AbstractMarking<S>, 
	   S extends Object> 

	   Set<T> getDeadTransitions(AbstractPetriNet<P,T,F,M,S> petriNet) 
			  throws ParameterException{

		Validate.notNull(petriNet);
		
		Set<T> netTransitions = new HashSet<T>(petriNet.getTransitions());
		for(M reachableMarking: buildMarkingGraph(petriNet).getElementSet()){
			try{
				petriNet.setMarking(reachableMarking);
				netTransitions.removeAll(petriNet.getEnabledTransitions());
			} catch(ParameterException e){
				e.printStackTrace();
			}
			if(netTransitions.isEmpty()){
				break;
			}
		}
		return netTransitions;
	}
	
	/**
	 * Builds the marking graph for the given Petri net,<br>
	 * i.e. a graph containing all reachable markings and their relation.<br>
	 * <br>
	 * Attention: Only works for bounded nets, i.e. nets for which the {@link AbstractPetriNet#isBounded()} method returns {@link Boundedness#BOUNDED}<br>
	 * If the boundedness property is set explicitly, the method may not halt.
	 *  
	 * @param petriNet The basic Petri net for operation.
	 * @return The marking graph of the given Petri net.
	 * @throws ParameterException If the Petri net parameter is <code>null</code> or the given net is nor bounded.
	 */
	@SuppressWarnings("unchecked")
	public static <P extends AbstractPlace<F,S>, 
				   T extends AbstractTransition<F,S>, 
				   F extends AbstractFlowRelation<P,T,S>, 
				   M extends AbstractMarking<S>, 
				   S extends Object> 
	
				   Graph<M> buildMarkingGraph(AbstractPetriNet<P,T,F,M,S> petriNet) throws ParameterException{
		
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		
		
		Validate.notNull(petriNet);
		if(petriNet.isBounded() != Boundedness.BOUNDED)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY, "Cannot determine marking graph for unbounded nets.");
		
		ArrayBlockingQueue<M> queue = new ArrayBlockingQueue<M>(10);
		Set<M> allKnownStates = new HashSet<M>();
		//LinkedList<M> allKnownStates = new LinkedList<M>();
		
		allKnownStates.add(petriNet.getInitialMarking());
		
		Graph<M> markingGraph = new Graph<M>();
		queue.offer(petriNet.getInitialMarking());
		

		System.out.println("Initial queue:" + queue);
		
		try {
			while(!queue.isEmpty()){
				M queueMarking = queue.poll();
				
				System.out.println("next queue marking: " + queueMarking);
				
				petriNet.setMarking(queueMarking);
								
				markingGraph.addElement((M) queueMarking.clone());			
				
				for(T enabledTransition: petriNet.getEnabledTransitions()){
					
					System.out.println("enabledTransition: " + enabledTransition);
					
					M newMarking = petriNet.fireCheck(enabledTransition.getName());
					
					System.out.println("marking reched by firing: " + newMarking);
					if(!allKnownStates.contains(newMarking)){
						
						System.out.println("öööööööööööööööööööööööööööööööööööö");
						System.out.println("AllKnownStates: " + allKnownStates);
						System.out.println("The new State: " + newMarking);
						System.out.println("öööööööööööööööööööööööööööööööööööö");
						
						System.out.println("Add unseen marking: ");
						
						queue.offer((M) newMarking.clone());
						allKnownStates.add((M) newMarking.clone());
						
						markingGraph.addElement((M) newMarking.clone());
					}
					markingGraph.addEdge(queueMarking, newMarking);
				}
			}
		} catch(ParameterException e){
			e.printStackTrace();
		} catch (PNException e) {
			e.printStackTrace();
		} catch (VertexNotFoundException e) {
			e.printStackTrace();
		}
		return markingGraph;
	}

}
