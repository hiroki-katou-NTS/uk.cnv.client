package nts.uk.file.at.app.schedule.filemanagement;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL055_個人スケジュールの取込み.B：内容チェック.メニュー別OCD.取り込みエラーDto
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class MappingErrorDto {

    // 社員コード
    private Optional<String> employeeCode;
    
    // 社員名
    private Optional<String> employeeName;
    
    // 年月日
    private Optional<GeneralDate> date;

    // エラーメッセージ
    private String errorMessage;
    
}
