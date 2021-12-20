package nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.time;

import lombok.val;
import nts.gul.util.Either;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;

import java.util.Optional;

public class TimePointRevise extends TimeRevise{
    public TimePointRevise(HourlySegment hourly, Optional<TimeBaseNumber> baseNumber, Optional<TimeBase60Delimiter> delimiter, Optional<TimeBase10Rounding> rounding) {
        super(hourly, baseNumber, delimiter, rounding);
    }

    @Override
    protected Either<ErrorMessage, Integer> toMinutes(String target) {
        val minutes = super.getDelimiter().get().toMinutes(target);
        if(minutes.isRight()){
            // マイナス値かチェック
            if(minutes.getRight() < 0) {
                // マイナス時間からマイナス時刻への変換
                return Either.right(-(1440 + minutes.getRight()));
            }
        }
        return minutes;
    }
}
