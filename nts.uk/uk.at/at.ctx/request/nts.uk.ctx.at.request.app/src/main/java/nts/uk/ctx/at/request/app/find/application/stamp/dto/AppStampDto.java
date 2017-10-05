package nts.uk.ctx.at.request.app.find.application.stamp.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.request.dom.application.stamp.AppStamp;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Data
@AllArgsConstructor
public class AppStampDto {
    
	private String appID;
    
    private String inputDate;
    
    private String enteredPerson;
    
    private String applicationDate;
    
    private String titleReason;
    
    private String detailReason;
    
    private String employeeID;
	
	private Integer stampRequestMode;
	
	private List<AppStampGoOutPermitDto> appStampGoOutPermitCmds;
	
	private List<AppStampWorkDto> appStampWorkCmds;
	
	private List<AppStampCancelDto> appStampCancelCmds;
	
	private AppStampOnlineRecordDto appStampOnlineRecordCmd;
	
	public static AppStampDto convertToDto(AppStamp appStamp){
		if(appStamp == null) return null;
		return new AppStampDto(
				appStamp.getApplicationID(), 
				appStamp.getInputDate().toString("yyyy/MM/dd"), 
				appStamp.getEnteredPersonSID(), 
				appStamp.getApplicationDate().toString("yyyy/MM/dd"), 
				"", 
				"", 
				appStamp.getApplicantSID(), 
				appStamp.getStampRequestMode().value, 
				appStamp.getAppStampGoOutPermits().stream().map(x -> AppStampGoOutPermitDto.convertToDto(x)).collect(Collectors.toList()), 
				appStamp.getAppStampWorks().stream().map(x -> AppStampWorkDto.convertToDto(x)).collect(Collectors.toList()), 
				appStamp.getAppStampCancels().stream().map(x -> AppStampCancelDto.convertToDto(x)).collect(Collectors.toList()), 
				AppStampOnlineRecordDto.convertToDto(appStamp.getAppStampOnlineRecords()));
	}
}

