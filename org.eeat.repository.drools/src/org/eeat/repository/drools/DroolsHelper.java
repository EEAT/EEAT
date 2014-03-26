package org.eeat.repository.drools;

import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.runtime.rule.RuleContext;

public class DroolsHelper {
	static public int PROPERTY_TYPE = 0;
	static public int PROPERTY_PACKAGE = 1;
	static public int PROPERTY_NAME = 2;
	static public int PROPERTY_REAMAINDER = 3;

	static public String getPropertyNamePart(String rulename, int propertyPart) {
		String[] tokens = rulename.split(":");
		return (propertyPart < tokens.length) ? tokens[propertyPart] : null;
	}

	static public String getRuleMetaAttributeValue(RuleContext context, String kbPackage, String name,
			String attribute) {
		// String kbPackage = "org.eeat.api.model";
		// String name =
		// "p@talx.model.TransportToolkit.betweenEventually_between1";
		KnowledgePackage kp = context.getKnowledgeRuntime().getKnowledgeBase().getKnowledgePackage(kbPackage);
		System.out.println("kp " + kp);
		if (kp != null) {
			for (Rule r : kp.getRules()) {
				if (r.getName().equals(name)) {
					return r.getMetaAttribute(attribute);
				}
			}
		}
		return null;
	}
}
