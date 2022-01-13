package nts.uk.ctx.at.function.dom.adapter.holidayover60h;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.remainingnumber.holidayover60h.empinfo.grantremainingdata.HolidayOver60hGrantRemainingData;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HolidayOver60hGrantRemainingImport extends HolidayOver60hGrantRemainingData {

    /** 60H超休不足ダミーフラグ */
    private boolean dummyAtr = false;

}
