package org.eeat.knime.hmm.pointfinder.builder;

import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;

public enum Observation {
	
	    READ(0),
	    COMPOSE(1);
    	
    	private int id;
		
    	Observation(int id) {
    		
    		this.id = id;
    	}
    	
    	int getId() {
    	
    		return this.id;
    	}
    	
		public ObservationDiscrete<Observation> observation() {
			return new ObservationDiscrete<Observation>(this);
		}
};
