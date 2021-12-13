/**
 * 
 */
package nts.uk.screen.at.app.ksu001.get28dateperiod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author laitv
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ModePeriodParam {
	public String endDate; // A画面パラメータ．基準期間．終了日
	
	public boolean isNextMonth; // 期間を進めるか
}
