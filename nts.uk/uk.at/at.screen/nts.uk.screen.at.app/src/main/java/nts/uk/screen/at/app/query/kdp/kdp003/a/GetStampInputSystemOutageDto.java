package nts.uk.screen.at.app.query.kdp.kdp003.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetStampInputSystemOutageDto {

	private String stopMessage;
	
	private String notiMessage;
	
	private boolean isStopSystem;
	
}
