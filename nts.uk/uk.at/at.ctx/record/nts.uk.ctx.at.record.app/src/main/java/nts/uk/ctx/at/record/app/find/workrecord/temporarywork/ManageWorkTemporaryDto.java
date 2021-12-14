/**
 * 
 */
package nts.uk.ctx.at.record.app.find.workrecord.temporarywork;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author hoangdd
 *
 */

/**
 * Sets the time treat temporary same.
 *
 * @param timeTreatTemporarySame the new time treat temporary same
 */
@Setter
@Getter
@NoArgsConstructor
public class ManageWorkTemporaryDto {
	int maxUsage;
	int timeTreatTemporarySame;
}