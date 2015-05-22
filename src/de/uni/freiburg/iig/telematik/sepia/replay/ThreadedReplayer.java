package de.uni.freiburg.iig.telematik.sepia.replay;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import de.invation.code.toval.thread.AbstractCallable;
import de.uni.freiburg.iig.telematik.sepia.mg.abstr.AbstractMarkingGraphRelation;
import de.uni.freiburg.iig.telematik.sepia.mg.abstr.AbstractMarkingGraphState;
import de.uni.freiburg.iig.telematik.sepia.petrinet.AbstractFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.AbstractMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.AbstractPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.AbstractTransition;
import de.uni.freiburg.iig.telematik.sepia.property.AbstractThreadedPNPropertyChecker;
import de.uni.freiburg.iig.telematik.sewol.log.LogEntry;

public class ThreadedReplayer<P extends AbstractPlace<F,S>, 
										T extends AbstractTransition<F,S>, 
										F extends AbstractFlowRelation<P,T,S>, 
										M extends AbstractMarking<S>, 
										S extends Object,
										X extends AbstractMarkingGraphState<M,S>,
										Y extends AbstractMarkingGraphRelation<M,X,S>,
										E extends LogEntry> extends AbstractThreadedPNPropertyChecker<P,T,F,M,S,X,Y,ReplayResult<E>>{
	
	protected ThreadedReplayer(ReplayCallableGenerator<P,T,F,M,S,X,Y,E> generator){
		super(generator);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ReplayCallableGenerator<P,T,F,M,S,X,Y,E> getGenerator() {
		return (ReplayCallableGenerator<P,T,F,M,S,X,Y,E>) super.getGenerator();
	}

	@Override
	protected AbstractCallable<ReplayResult<E>> getCallable() {
		return new ReplayCallable<P,T,F,M,S,X,Y,E>(getGenerator());
	}
	
	public void runCalculation(){
		setUpAndRun();
	}
	
	public ReplayResult<E> getReplayResult() throws ReplayException{
		try {
			return getResult();
		} catch (CancellationException e) {
			throw new ReplayException("Replaying cancelled.", e);
		} catch (InterruptedException e) {
			throw new ReplayException("Replaying interrupted.", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if(cause == null){
				throw new ReplayException("Exception during replay.\n" + e.getMessage(), e);
			}
			if(cause instanceof ReplayException){
				throw (ReplayException) cause;
			}
			throw new ReplayException("Exception during replay.\n" + e.getMessage(), e);
		} catch(Exception e){
			throw new ReplayException("Exception during replay.\n" + e.getMessage(), e);
		}
	}
}
