package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

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
public class GetStartupProcessingInformationDto {

	public String employeeId; // 社員ID
	public String employeeCode; // 社員コード
	public String businessName; // ビジネスネーム
	public int yearMonth;
	
}
