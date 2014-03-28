package org.eeat.knime.hmm.reader.observation;

import java.util.HashMap;
import java.util.Map;

import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;

public enum Observation50 {
	
    OBS_0(0),
    OBS_1(1),
    OBS_2(2),
    OBS_3(3),
    OBS_4(4),
    OBS_5(5),
    OBS_6(6),
    OBS_7(7),
    OBS_8(8),
    OBS_9(9),
    OBS_10(10),
    OBS_11(11),
    OBS_12(12),
    OBS_13(13),
    OBS_14(14),
    OBS_15(15),
    OBS_16(16),
    OBS_17(17),
    OBS_18(18),
    OBS_19(19),
    OBS_20(20),
    OBS_21(21),
    OBS_22(22),
    OBS_23(23),
    OBS_24(24),
    OBS_25(25),
    OBS_26(26),
    OBS_27(27),
    OBS_28(28),
    OBS_29(29),
    OBS_30(30),
    OBS_31(31),
    OBS_32(32),
    OBS_33(33),
    OBS_34(34),
    OBS_35(35),
    OBS_36(36),
    OBS_37(37),
    OBS_38(38),
    OBS_39(39),
    OBS_40(40),
    OBS_41(41),
    OBS_42(42),
    OBS_43(43),
    OBS_44(44),
    OBS_45(45),
    OBS_46(46),
    OBS_47(47),
    OBS_48(48),
    OBS_49(49);	  
	    
	    static Map<Integer, Observation50> map = new HashMap<Integer, Observation50>();
	    
	    static {
	    	
	    	map.put(0, OBS_0);
	    	map.put(1, OBS_1);
	    	map.put(2, OBS_2);
	    	map.put(3, OBS_3);
	    	map.put(4, OBS_4);
	    	map.put(5, OBS_5);
	    	map.put(6, OBS_6);
	    	map.put(7, OBS_7);
	    	map.put(8, OBS_8);
	    	map.put(9, OBS_9);
	    	map.put(10, OBS_10);
	    	map.put(11, OBS_11);
	    	map.put(12, OBS_12);
	    	map.put(13, OBS_13);
	    	map.put(14, OBS_14);
	    	map.put(15, OBS_15);
	    	map.put(16, OBS_16);
	    	map.put(17, OBS_17);
	    	map.put(18, OBS_18);
	    	map.put(19, OBS_19);
	    	map.put(20, OBS_20);
	    	map.put(21, OBS_21);
	    	map.put(22, OBS_22);
	    	map.put(23, OBS_23);
	    	map.put(24, OBS_24);
	    	map.put(25, OBS_25);
	    	map.put(26, OBS_26);
	    	map.put(27, OBS_27);
	    	map.put(28, OBS_28);
	    	map.put(29, OBS_29);
	    	map.put(30, OBS_30);
	    	map.put(31, OBS_31);
	    	map.put(32, OBS_32);
	    	map.put(33, OBS_33);
	    	map.put(34, OBS_34);
	    	map.put(35, OBS_35);
	    	map.put(36, OBS_36);
	    	map.put(37, OBS_37);
	    	map.put(38, OBS_38);
	    	map.put(39, OBS_39);
	    	map.put(40, OBS_40);
	    	map.put(41, OBS_41);
	    	map.put(42, OBS_42);
	    	map.put(43, OBS_43);
	    	map.put(44, OBS_44);
	    	map.put(45, OBS_45);
	    	map.put(46, OBS_46);
	    	map.put(47, OBS_47);
	    	map.put(48, OBS_48);
	    	map.put(49, OBS_49);	
	    }
	    
    	
    	private int id;
		
    	Observation50(int id) {
    		
    		this.id = id;
    	}
    	
    	int getId() {
    	
    		return this.id;
    	}
    	
    	Observation50 getObservation(int id) {
    		
    		return map.get(id);
    	}
    	
		public ObservationDiscrete<Observation50> observation() {
			return new ObservationDiscrete<Observation50>(this);
		}
};
