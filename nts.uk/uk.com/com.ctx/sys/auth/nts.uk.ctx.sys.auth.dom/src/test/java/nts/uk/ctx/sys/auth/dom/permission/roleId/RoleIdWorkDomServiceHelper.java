package nts.uk.ctx.sys.auth.dom.permission.roleId;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;

public class RoleIdWorkDomServiceHelper {
	public static class Helper {
		public static List<String> SIDS = 
				Arrays.asList("e1", "e2", "e3");	
		
		public static String E1 = "e1";
		public static String E2 = "e2";
		public static String E3 = "e3";
		public static Optional<String> U1OP = Optional.of("u1");
		public static Optional<String> U2OP = Optional.of("u2");
		public static Optional<String> U3OP = Optional.of("u3");
		
		public static Optional<RoleSet> R1_ROLESET = createRoleSet("r1");
		
		public static Optional<RoleSet> R2_ROLESET = createRoleSet("r2");
		
		public static Optional<RoleSet> R3_ROLESET = createRoleSet("r3");
		
				
		public static Optional<RoleSet> createRoleSet(String r) {
			return Optional.of(new RoleSet(
					null,
					null,
					null,
					Optional.ofNullable(r),
					null,
					null,
					null,
					null,
					null
					));
		}
	}
}
