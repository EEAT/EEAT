package org.eeat.knime.hmm.pointfinder.builder;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.NodeLogger;

import org.eeat.knime.hmm.pointfinder.learning.HMMLearningPointFinderNodeModel;


public class OptimizeHMM {
	
	private static final NodeLogger logger = NodeLogger
	.getLogger(HMMLearningPointFinderNodeModel.class);
	
	
	public static List<List<ObservationDiscrete<Observation>>> createSequence(ArrayList<Object[]> list) {
		
		ObservationDiscrete<Observation> obd = null;
		
		List<List<ObservationDiscrete<Observation>>> sequences = new ArrayList<List<ObservationDiscrete<Observation>>>();
		
		List<ObservationDiscrete<Observation>> listObs = new ArrayList<ObservationDiscrete<Observation>>();
		
		for (Object[] obj : list) {

			if (obj[0].toString().equalsIgnoreCase(
					
					Constant.EMAIL_READ)) {

				obd = new ObservationDiscrete<Observation>(Observation.READ);
				
				listObs.add(obd);

			} else if (obj[0].toString().equalsIgnoreCase(
					
					Constant.EMAIL_COMPOSE)) {

				obd = new ObservationDiscrete<Observation>(Observation.COMPOSE);
				
				listObs.add(obd);
			}
			
			// commented so no null will be in sequence
//			listObs.add(obd);
			
//			sequences.add(listObs);
		}
		sequences.add(listObs);

			
		return sequences;
	}

		
	
	
	public static Hmm<ObservationDiscrete<Observation>> optimizeHMM(Hmm<ObservationDiscrete<Observation>> hmm, List<List<ObservationDiscrete<Observation>>> sequences) {
		
		
		
		BaumWelchLearner bwl = new BaumWelchLearner();
		
		KullbackLeiblerDistanceCalculator klc = 
			new KullbackLeiblerDistanceCalculator();
		
		Hmm<ObservationDiscrete<Observation>> learntHmm =hmm;
		
		
		
		try {
			
			logger.info("SEQUENCE:" + sequences.size());
			
			for (ObservationDiscrete<Observation> k:sequences.get(0)) 
				logger.info("TTTTTTTTTTTTTT" + k.value);
		
		for (int i = 0; i < 10; i++) {

	//		logger.info("IIIII:" + i);
			logger.info("LEARNT HMM:" + learntHmm);
			
			learntHmm = bwl.iterate(learntHmm, sequences);
		//	learntHmm = bwl.learn(hmm, sequences);
//			System.out.println("Distance at iteration " + i + ": " +
//					klc.distance(learntHmm, hmm));
		}
		
		} catch (Exception ex) {
			
			//when sequence too short throows exception
			logger.info(ex);
			logger.info("SHOOOOOOOOOOOOOOOOOOOORTTTTTTTTTT" + sequences.get(0).size());
			return hmm;
			
		}
		
		if (Double.isNaN(learntHmm.getAij(0, 0)) ||
				Double.isNaN(learntHmm.getAij(1, 0)) ||	
			Double.isNaN(learntHmm.getAij(0, 1)) ||
			Double.isNaN(learntHmm.getAij(1, 1))
			)  {
			
			return hmm;
			
		}
		
		return learntHmm;
	}	
	
}

