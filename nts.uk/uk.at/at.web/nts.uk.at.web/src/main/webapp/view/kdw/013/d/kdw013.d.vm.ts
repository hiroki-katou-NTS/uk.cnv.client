module nts.uk.ui.at.kdw013.d {
    enum OverTimeLeaveAtr {
        // 0: 残業申請
        OVER_TIME_APPLICATION = 0,

        //1: 休日出勤申請
        HOLIDAY_WORK_APPLICATION = 1
    };

    const { formatTime } = share;
    const $vm = new ko.ViewModel();

    const { OVER_TIME_APPLICATION } = OverTimeLeaveAtr;
    const map2Description = (otAtr: OverTimeLeaveAtr, time: number) => {
        if (otAtr === OVER_TIME_APPLICATION) {
            return $vm.$i18n('KDW013_38', [formatTime(time, 'Time_Short_HM')]);
        }

        return $vm.$i18n('KDW013_39', [formatTime(time, 'Time_Short_HM')]);
    };

    @bean()
    export class ViewModel extends ko.ViewModel {
        dataSource: KnockoutObservableArray<DataItem> = ko.observableArray([]);

        constructor(params: a.OvertimeLeaveTime[] | null) {
            super();

            if (!params || !params.length) {
                this.close();
            }

            const $ds = _
                .chain(params)
                .orderBy(['date'])
                // mapping data from dto to grid
                .map(({ date, overtimeLeaveAtr, time }) => ({
                    overtimeLeaveAtr,
                    date,
                    time,
                    description: map2Description(overtimeLeaveAtr, time)
                }))
                .value();

            this.dataSource($ds);
        }

        mounted() {
            $('.btn-jump').first().focus();
        }
        
        jump() {
            const pr = this;
            
            const params: KAF005Params = {
                            appType: 1,
                            baseDate: pr.date,
                            employeeIds: [$vm.$user.employeeId],
                            isAgentMode: false
                        };
            
            if (pr.overtimeLeaveAtr !== OVER_TIME_APPLICATION) {
                $vm.$jump.blank('at', '/view/kaf/010/a/index.xhtml', params);
            } else {
                $vm.$jump.blank('at', '/view/kaf/005/a/index.xhtml?overworkatr=2', params);
            }

        }

        // close dialog
        close() {
            const vm = this;
            // ダイアログを閉じる
            vm.$window.close();
        }
    }

    interface KAF005Params {
        appType: number;
        employeeIds: Array<string>;
        baseDate: string;
        isAgentMode?: boolean;
    }
}