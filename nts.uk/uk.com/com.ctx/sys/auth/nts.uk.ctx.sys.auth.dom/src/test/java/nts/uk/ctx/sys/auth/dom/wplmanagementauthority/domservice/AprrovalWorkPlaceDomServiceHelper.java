package nts.uk.ctx.sys.auth.dom.wplmanagementauthority.domservice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nts.uk.ctx.sys.auth.dom.role.Role;

public class AprrovalWorkPlaceDomServiceHelper {

	public static class Helper {
		public static List<String> WPLS = 
				Arrays.asList("w1", "w2", "w3");
		
		public static Map<String, String> ROLE_IDS =
				new HashMap<String, String>() {/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

				{
					put("e1", "r1");
					put("e2", "r2");
					put("e3", "r3");
			    }};
			    
			    
	    public static List<String> SIDS =
	    		Arrays.asList("s1", "s2", "s3");
	    
	    public static List<Role> ROLES =
	    		Arrays.asList(
						new Role(
								"r1",
								null,
								null,
								null,
								null,
								null,
								null,
								null
								),
						new Role(
								"r2",
								null,
								null,
								null,
								null,
								null,
								null,
								null
								),
						new Role(
								"r3",
								null,
								null,
								null,
								null,
								null,
								null,
								null
								));
	}
}
