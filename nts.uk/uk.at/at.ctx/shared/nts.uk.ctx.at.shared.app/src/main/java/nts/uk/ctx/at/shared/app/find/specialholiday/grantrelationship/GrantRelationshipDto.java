package nts.uk.ctx.at.shared.app.find.specialholiday.grantrelationship;

import lombok.Value;
/**
 * 
 * @author yennth
 *
 */
@Value
public class GrantRelationshipDto {
	int specialHolidayCode;
	String relationshipCode;
	String relationshipName;
	Integer grantRelationshipDay;
	Integer morningHour;
	boolean activeAlready;
}
  