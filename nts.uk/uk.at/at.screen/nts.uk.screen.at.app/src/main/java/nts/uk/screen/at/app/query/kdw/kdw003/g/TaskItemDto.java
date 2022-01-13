package nts.uk.screen.at.app.query.kdw.kdw003.g;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskItemDto {
	private Integer frameNo;	
	private String taskCode;
	private String taskName;
	private String taskAbName;
	private String startDate;
	private String endDate;
	private List<Integer> listFrameNoUseAtr;
	private List<String> listFrameName;
}
