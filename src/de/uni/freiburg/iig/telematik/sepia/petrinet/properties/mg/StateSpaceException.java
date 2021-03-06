package de.uni.freiburg.iig.telematik.sepia.petrinet.properties.mg;

import de.uni.freiburg.iig.telematik.sepia.exception.PNException;

public class StateSpaceException extends PNException {

	private static final long serialVersionUID = 4677058392850492167L;

	public StateSpaceException() {
		super();
	}

	public StateSpaceException(String message) {
		super(message);
	}

	public StateSpaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public StateSpaceException(Throwable cause) {
		super(cause);
	}
}
