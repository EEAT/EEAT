package org.eeat.knime.hmm.pointfinder.builder;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.NodeLogger;

import org.eeat.knime.hmm.pointfinder.learning.HMMLearningPointFinderNodeModel;


import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;

public class HMMCreator {
	
	// the logger instance
	private static final NodeLogger logger = NodeLogger
			.getLogger(HMMLearningPointFinderNodeModel.class);

	/* The HMM this example is based on */

	public static Hmm<ObservationDiscrete<Observation>> buildHmm(
			List<Double[]> opdfList, ArrayList<ArrayList<Object[]>> sequenceList) {

		Hmm<ObservationDiscrete<Observation>> hmm = null;

//		for (int i = 0; i < opdfList.size(); i++) {

			hmm = new Hmm<ObservationDiscrete<Observation>>(2,
					new OpdfDiscreteFactory<Observation>(Observation.class));

			hmm.setPi(0, Constant.INITIAL_PROBABILITY_A);
			hmm.setPi(1, Constant.INITIAL_PROBABILITY_B);

			hmm.setOpdf(
					0,
					new OpdfDiscrete<Observation>(Observation.class,
							new double[] { (opdfList.get(0))[0],
									(opdfList.get(0))[1] }));
			hmm.setOpdf(
					1,
					new OpdfDiscrete<Observation>(Observation.class,
							new double[] { (opdfList.get(0))[1],
									(opdfList.get(0))[0] }));

			hmm.setAij(0, 1, Constant.STATE_A_STATE_B);
			hmm.setAij(0, 0, Constant.STATE_A_STATE_A);
			hmm.setAij(1, 0, Constant.STATE_B_STATE_A);
			hmm.setAij(1, 1, Constant.STATE_B_STATE_B);
			
			logger.info("LENGHTTTTTTTTTTFFFFF:" + sequenceList.get(0).size());

			List<List<ObservationDiscrete<Observation>>> sequence = OptimizeHMM
					.createSequence(sequenceList.get(0));

			logger.info("BEFORE OPTIMIZE:" + hmm);
			logger.info("LENGHTTTTTTTTTT:" + sequence.get(0).size());
			
			hmm = OptimizeHMM.optimizeHMM(hmm, sequence);

			System.out.println(hmm.getAij(0, 0));

//		}

		return hmm;
	}
	
	/*
	 * creates all error for 2 week ones
	 * compare sunday, daysegment 0 in first 2 week with sunday, daysegment 1 in second 2 week and for all the two week 
	 * and add them up for the two week. It goes for other two week. So total 52 (2 week)
	 * 
	 */
	public static List<Double> diffHMMAverage(
			List<List<Hmm<ObservationDiscrete<Observation>>>> hmmListList) {

		List<Hmm<ObservationDiscrete<Observation>>> hmmList1;
		List<Hmm<ObservationDiscrete<Observation>>> hmmList2;

		List<Double> errorList = new ArrayList<Double>();

		// we want to find out all the differences in the 2 week with another
		// two week and for all weeks
		List<List<Double>> errorAll = new ArrayList<List<Double>>();

		List<Double> twoWeekError = new ArrayList<Double>();

		double error = 0;
		double err = 0;

		for (int i = 0; i < hmmListList.size() - 1; i++) {

			error = 0;
			err = 0;
			hmmList1 = hmmListList.get(i);
			hmmList2 = hmmListList.get(i + 1);

			// twoWeekError = new ArrayList<Double>();
			// double diff = 0;

			for (int j = 0; j < hmmList1.size(); j++) {

				// diff = diffHMMTwo (hmmList1.get(j), hmmList2.get(j));

				// /twoWeekError.add(diff);

				err = diffHMMTwo(hmmList1.get(j), hmmList2.get(j));

				if (Double.isNaN(err)) {

					err = 0;
				}

				// error += diffHMMTwo (hmmList1.get(j), hmmList2.get(j));

				error += err;
				// error += diff;

			}

			// errorAll.add(twoWeekError);

			errorList.add(error);
		}

		return errorList;

		// return errorAll;

	}
	
	public static double diffHMMTwo(Hmm<ObservationDiscrete<Observation>> hmm1,
			Hmm<ObservationDiscrete<Observation>> hmm2) {

		KullbackLeiblerDistanceCalculator klc = new KullbackLeiblerDistanceCalculator();

		return klc.distance(hmm1, hmm2);

	}
	
}
