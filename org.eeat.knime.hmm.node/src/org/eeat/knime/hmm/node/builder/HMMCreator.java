package org.eeat.knime.hmm.node.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eeat.knime.hmm.node.builder.observation.Observation50;
import org.eeat.knime.hmm.node.so3_50.HMMNode_3_50NodeModel;
import org.knime.core.node.NodeLogger;



import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchScaledLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;

// NOT USED

public class HMMCreator {
	
	// the logger instance
	private static final NodeLogger logger = NodeLogger
			.getLogger(HMMNode_3_50NodeModel.class);

	/* The HMM this example is based on */

	@SuppressWarnings("unchecked")
	public static  Hmm<ObservationDiscrete<Observation50>> buildHmm7(
			double[][] opdfList, ArrayList<ArrayList<Object[]>> sequenceList, List<Double> initialStateProbs, Map<Integer, List<Double>> stateProbs, int numberOfStates, int numberOfObservations) {

		
			
			Hmm<ObservationDiscrete<Observation50>> hmm = null;
			
			hmm = new Hmm<ObservationDiscrete<Observation50>>(numberOfStates,
					new OpdfDiscreteFactory<Observation50>(Observation50.class));
			
			for (int i = 0; i < numberOfStates; i++) {
				
				hmm.setPi(i, initialStateProbs.get(i));
			}
			
			for (int i = 0; i < numberOfStates; i++) {
				
				// all rows the same -- a state has all same probs for observations
				hmm.setOpdf(
						i,
						new OpdfDiscrete<Observation50>(Observation50.class, opdfList[i]));
			}
			
			for (int i = 0; i < stateProbs.size(); i++) {
				
				List<Double> list = stateProbs.get(i);
				
				for (int j = 0; j < list.size(); j++) {
					
					hmm.setAij(i, j, list.get(j));
				}
			}
			
			logger.debug("LENGHTTTTTTTTTTFFFFF:" + sequenceList.get(0).size());

//			List<List<ObservationDiscrete<Observation2>>> sequence = OptimizeHMM
//					.createSequence(sequenceList.get(0));
			
			ObservationDiscrete<Observation50> obd = null;
			
			List<List<ObservationDiscrete<Observation50>>> sequences = new ArrayList<List<ObservationDiscrete<Observation50>>>();
			
			List<ObservationDiscrete<Observation50>> listObs = new ArrayList<ObservationDiscrete<Observation50>>();
			
			for (Object[] obj : sequenceList.get(0)) {

//				if (obj[0].toString().equalsIgnoreCase("1")) {

//					obd = new ObservationDiscrete<Observation2>(Observation2.OBS_0);
					
//					listObs.add(obd);

//				} else if (obj[0].toString().equalsIgnoreCase("5")) {

//					obd = new ObservationDiscrete<Observation2>(Observation2.OBS_1);
					
//					listObs.add(obd);
//				}
				
				if (obj[0].toString().equalsIgnoreCase("0")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_0);
					
					listObs.add(obd);

				} else if (obj[0].toString().equalsIgnoreCase("1")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_1);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("2")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_2);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("3")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_3);
					
					listObs.add(obd);
				} 
				
				else if (obj[0].toString().equalsIgnoreCase("4")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_4);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("5")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_5);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("6")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_6);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("7")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_7);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("8")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_8);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("9")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_9);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("10")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_10);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("11")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_11);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("12")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_12);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("13")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_13);
					
					listObs.add(obd);
				} 
				
				else if (obj[0].toString().equalsIgnoreCase("14")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_14);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("15")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_15);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("16")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_16);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("17")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_17);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("18")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_18);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("19")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_19);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("20")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_20);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("21")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_21);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("22")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_22);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("23")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_23);
					
					listObs.add(obd);
				} 
				
				else if (obj[0].toString().equalsIgnoreCase("24")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_24);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("25")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_25);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("26")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_26);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("27")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_27);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("28")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_28);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("29")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_29);
					
					listObs.add(obd);
				} 	else if (obj[0].toString().equalsIgnoreCase("30")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_30);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("31")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_31);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("32")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_32);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("33")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_33);
					
					listObs.add(obd);
				} 
				
				else if (obj[0].toString().equalsIgnoreCase("34")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_34);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("35")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_35);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("36")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_36);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("37")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_37);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("38")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_38);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("39")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_39);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("40")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_40);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("41")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_41);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("42")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_42);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("43")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_43);
					
					listObs.add(obd);
				} 
				
				else if (obj[0].toString().equalsIgnoreCase("44")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_44);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("45")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_45);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("46")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_46);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("47")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_47);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("48")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_48);
					
					listObs.add(obd);
				} else if (obj[0].toString().equalsIgnoreCase("49")) {

					obd = new ObservationDiscrete<Observation50>(Observation50.OBS_49);
					
					listObs.add(obd);
				} 
				
				// commented so no null will be in sequence
//				listObs.add(obd);
				
//				sequences.add(listObs);
			}
			sequences.add(listObs);			
			

			logger.debug("BEFORE OPTIMIZE:" + hmm);
			logger.debug("LENGHTTTTTTTTTT:" + sequences.get(0).size());
			
//			hmm = OptimizeHMM.optimizeHMM(hmm, sequences);
			
			
			BaumWelchScaledLearner bwl = new BaumWelchScaledLearner();
			
			KullbackLeiblerDistanceCalculator klc = 
				new KullbackLeiblerDistanceCalculator();
			
			Hmm<ObservationDiscrete<Observation50>> learntHmm =hmm;
			
			try {
				
				logger.debug("SEQUENCE:" + sequences.size());
				
				for (ObservationDiscrete<Observation50> k:sequences.get(0)) 
					logger.debug("TTTTTTTTTTTTTT" + k.value);
			
			for (int i = 0; i < 10; i++) {

		//		logger.debug("IIIII:" + i);
				logger.debug("LEARNT HMM:" + learntHmm);
				
				learntHmm = bwl.iterate(learntHmm, sequences);
			//	learntHmm = bwl.learn(hmm, sequences);
//				System.out.println("Distance at iteration " + i + ": " +
//						klc.distance(learntHmm, hmm));
			}
			
			} catch (Exception ex) {
				
				//when sequence too short throows exception
				logger.debug(ex);
				logger.debug("SHOOOOOOOOOOOOOOOOOOOORTTTTTTTTTT" + sequences.get(0).size());
				return hmm;
				
			}
			
			ObservationDiscrete<Observation50> obd_0 = new ObservationDiscrete<Observation50>(Observation50.OBS_0);
			ObservationDiscrete<Observation50> obd_1 = new ObservationDiscrete<Observation50>(Observation50.OBS_1);
			ObservationDiscrete<Observation50> obd_2 = new ObservationDiscrete<Observation50>(Observation50.OBS_2);
			ObservationDiscrete<Observation50> obd_3 = new ObservationDiscrete<Observation50>(Observation50.OBS_3);
			ObservationDiscrete<Observation50> obd_4 = new ObservationDiscrete<Observation50>(Observation50.OBS_4);
			ObservationDiscrete<Observation50> obd_5 = new ObservationDiscrete<Observation50>(Observation50.OBS_5);
			ObservationDiscrete<Observation50> obd_6 = new ObservationDiscrete<Observation50>(Observation50.OBS_6);
			ObservationDiscrete<Observation50> obd_7 = new ObservationDiscrete<Observation50>(Observation50.OBS_7);
			ObservationDiscrete<Observation50> obd_8 = new ObservationDiscrete<Observation50>(Observation50.OBS_8);
			ObservationDiscrete<Observation50> obd_9 = new ObservationDiscrete<Observation50>(Observation50.OBS_9);
			ObservationDiscrete<Observation50> obd_10 = new ObservationDiscrete<Observation50>(Observation50.OBS_10);
			ObservationDiscrete<Observation50> obd_11 = new ObservationDiscrete<Observation50>(Observation50.OBS_11);
			ObservationDiscrete<Observation50> obd_12 = new ObservationDiscrete<Observation50>(Observation50.OBS_12);
			ObservationDiscrete<Observation50> obd_13 = new ObservationDiscrete<Observation50>(Observation50.OBS_13);
			ObservationDiscrete<Observation50> obd_14 = new ObservationDiscrete<Observation50>(Observation50.OBS_14);
			ObservationDiscrete<Observation50> obd_15 = new ObservationDiscrete<Observation50>(Observation50.OBS_15);
			ObservationDiscrete<Observation50> obd_16 = new ObservationDiscrete<Observation50>(Observation50.OBS_16);
			ObservationDiscrete<Observation50> obd_17 = new ObservationDiscrete<Observation50>(Observation50.OBS_17);
			ObservationDiscrete<Observation50> obd_18 = new ObservationDiscrete<Observation50>(Observation50.OBS_18);
			ObservationDiscrete<Observation50> obd_19 = new ObservationDiscrete<Observation50>(Observation50.OBS_19);
			ObservationDiscrete<Observation50> obd_20 = new ObservationDiscrete<Observation50>(Observation50.OBS_20);	
			ObservationDiscrete<Observation50> obd_21 = new ObservationDiscrete<Observation50>(Observation50.OBS_21);
			ObservationDiscrete<Observation50> obd_22 = new ObservationDiscrete<Observation50>(Observation50.OBS_22);
			ObservationDiscrete<Observation50> obd_23 = new ObservationDiscrete<Observation50>(Observation50.OBS_23);
			ObservationDiscrete<Observation50> obd_24 = new ObservationDiscrete<Observation50>(Observation50.OBS_24);
			ObservationDiscrete<Observation50> obd_25 = new ObservationDiscrete<Observation50>(Observation50.OBS_25);
			ObservationDiscrete<Observation50> obd_26 = new ObservationDiscrete<Observation50>(Observation50.OBS_26);
			ObservationDiscrete<Observation50> obd_27 = new ObservationDiscrete<Observation50>(Observation50.OBS_27);
			ObservationDiscrete<Observation50> obd_28 = new ObservationDiscrete<Observation50>(Observation50.OBS_28);
			ObservationDiscrete<Observation50> obd_29 = new ObservationDiscrete<Observation50>(Observation50.OBS_29);
			ObservationDiscrete<Observation50> obd_30 = new ObservationDiscrete<Observation50>(Observation50.OBS_30);
			ObservationDiscrete<Observation50> obd_31 = new ObservationDiscrete<Observation50>(Observation50.OBS_31);
			ObservationDiscrete<Observation50> obd_32 = new ObservationDiscrete<Observation50>(Observation50.OBS_32);
			ObservationDiscrete<Observation50> obd_33 = new ObservationDiscrete<Observation50>(Observation50.OBS_33);
			ObservationDiscrete<Observation50> obd_34 = new ObservationDiscrete<Observation50>(Observation50.OBS_34);
			ObservationDiscrete<Observation50> obd_35 = new ObservationDiscrete<Observation50>(Observation50.OBS_35);
			ObservationDiscrete<Observation50> obd_36 = new ObservationDiscrete<Observation50>(Observation50.OBS_36);
			ObservationDiscrete<Observation50> obd_37 = new ObservationDiscrete<Observation50>(Observation50.OBS_37);
			ObservationDiscrete<Observation50> obd_38 = new ObservationDiscrete<Observation50>(Observation50.OBS_38);
			ObservationDiscrete<Observation50> obd_39 = new ObservationDiscrete<Observation50>(Observation50.OBS_39);
			ObservationDiscrete<Observation50> obd_40 = new ObservationDiscrete<Observation50>(Observation50.OBS_40);	
			ObservationDiscrete<Observation50> obd_41 = new ObservationDiscrete<Observation50>(Observation50.OBS_41);
			ObservationDiscrete<Observation50> obd_42 = new ObservationDiscrete<Observation50>(Observation50.OBS_42);
			ObservationDiscrete<Observation50> obd_43 = new ObservationDiscrete<Observation50>(Observation50.OBS_43);
			ObservationDiscrete<Observation50> obd_44 = new ObservationDiscrete<Observation50>(Observation50.OBS_44);
			ObservationDiscrete<Observation50> obd_45 = new ObservationDiscrete<Observation50>(Observation50.OBS_45);
			ObservationDiscrete<Observation50> obd_46 = new ObservationDiscrete<Observation50>(Observation50.OBS_46);
			ObservationDiscrete<Observation50> obd_47 = new ObservationDiscrete<Observation50>(Observation50.OBS_47);
			ObservationDiscrete<Observation50> obd_48 = new ObservationDiscrete<Observation50>(Observation50.OBS_48);
			ObservationDiscrete<Observation50> obd_49 = new ObservationDiscrete<Observation50>(Observation50.OBS_49);

				
			
			
			if (Double.isNaN(learntHmm.getAij(0, 0)) ||
				Double.isNaN(learntHmm.getAij(0, 1)) ||	
				Double.isNaN(learntHmm.getAij(0, 2)) ||
				Double.isNaN(learntHmm.getAij(1, 0)) ||
				Double.isNaN(learntHmm.getAij(1, 1)) ||	
				Double.isNaN(learntHmm.getAij(1, 2)) ||	
				Double.isNaN(learntHmm.getAij(2, 0)) ||
				Double.isNaN(learntHmm.getAij(2, 1)) ||	
				Double.isNaN(learntHmm.getAij(2, 2)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_0)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_1)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_2)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_3)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_4)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_5)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_6)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_7)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_8)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_9)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_10)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_11)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_12)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_13)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_14)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_15)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_16)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_17)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_18)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_19)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_20)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_21)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_22)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_23)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_24)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_25)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_26)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_27)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_28)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_29)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_30)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_31)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_32)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_33)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_34)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_35)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_36)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_37)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_38)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_39)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_40)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_41)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_42)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_43)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_44)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_45)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_46)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_47)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_48)) ||
				Double.isNaN(learntHmm.getOpdf(0).probability(obd_49)) ||

				Double.isNaN(learntHmm.getOpdf(1).probability(obd_0)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_1)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_2)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_3)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_4)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_5)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_6)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_7)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_8)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_9)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_10)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_11)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_12)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_13)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_14)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_15)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_16)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_17)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_18)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_19)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_20)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_21)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_22)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_23)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_24)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_25)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_26)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_27)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_28)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_29)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_30)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_31)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_32)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_33)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_34)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_35)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_36)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_37)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_38)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_39)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_40)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_41)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_42)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_43)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_44)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_45)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_46)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_47)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_48)) ||
				Double.isNaN(learntHmm.getOpdf(1).probability(obd_49)) ||
				

				Double.isNaN(learntHmm.getOpdf(2).probability(obd_0)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_1)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_2)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_3)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_4)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_5)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_6)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_7)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_8)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_9)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_10)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_11)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_12)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_13)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_14)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_15)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_16)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_17)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_18)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_19)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_20)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_21)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_22)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_23)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_24)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_25)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_26)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_27)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_28)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_29)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_30)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_31)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_32)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_33)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_34)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_35)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_36)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_37)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_38)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_39)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_40)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_41)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_42)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_43)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_44)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_45)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_46)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_47)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_48)) ||
				Double.isNaN(learntHmm.getOpdf(2).probability(obd_49)))   {
				
				return hmm;
				
			}
			
			
			for (int i = 0; i < numberOfStates; i++) {
				
				for (int j = 0; j < numberOfStates; j++) {
					
					if (Double.isNaN(learntHmm.getAij(i, j))) {
						
						return hmm;
					}
				}
				
			}
			
			
			return learntHmm;			

	}
}
