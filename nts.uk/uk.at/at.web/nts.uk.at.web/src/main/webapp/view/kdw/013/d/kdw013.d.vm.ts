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

    const map2Link = (otAtr: OverTimeLeaveAtr) => {
        if (otAtr === OVER_TIME_APPLICATION) {
            return $vm.$i18n('KDW013_35');
        }

        return $vm.$i18n('KDW013_36');
    };
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
                    link: map2Link(overtimeLeaveAtr),
                    description: map2Description(overtimeLeaveAtr, time)
                }))
                .value();

            this.dataSource($ds);
        }

        mounted() {
            const vm = this;
            const { employeeId } = vm.$user;

            $(vm.$el)
                // patch click link
                // D1_7のリンクをクリックすると、申請画面を別のタブで起動する
                .on('click', 'td[aria-describedby="kdw-013-ddata_link"]', (evt: JQueryEventObject) => {
                    const ds = ko.unwrap(vm.dataSource);
                    const di = $(evt.target).closest('tr[data-id]').data('id');
                    const exist = _.find(ds, ({ overtimeLeaveAtr }) => overtimeLeaveAtr === di);

                    if (exist) {
                        const params: KAF005Params = {
                            appType: 1,
                            baseDate: exist.date,
                            employeeIds: [employeeId],
                            isAgentMode: false
                        };
                        if (exist.overtimeLeaveAtr !== OVER_TIME_APPLICATION) {
                            vm.$jump.blank('at', '/view/kaf/010/a/index.xhtml', params);
                        } else {
                            vm.$jump.blank('at', '/view/kaf/005/a/index.xhtml?overworkatr=2', params);
                        }
                    }
                });

            $(vm.$el)
                // remove grid tabindex
                .find('#kdw-013-ddata_container')
                .removeAttr('tabindex')
                // find link & set tabindex
                .find('td[aria-describedby="kdw-013-ddata_link"]')
                .attr('tabindex', '1')
                .on('keydown', (evt) => {
                    if (evt.keyCode === 13) {
                        evt.preventDefault();

                        $(evt.target).trigger('click');
                    }
                })
                // focus to first link
                .first()
                .focus();
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