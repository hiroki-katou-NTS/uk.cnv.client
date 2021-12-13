package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@Getter
public class WorkplaceInfoDto {
	/** 職場ID */
	private String workplaceId;

    /** The workplace code. */
    private String workplaceCode;

    /** The workplace name. */
    private String workplaceName;

    /** The wkp generic name. */
    private String wkpGenericName;

    /** The wkp display name. */
    private String wkpDisplayName;

    /** The outside wkp code. */
    private String outsideWkpCode;
    
}
