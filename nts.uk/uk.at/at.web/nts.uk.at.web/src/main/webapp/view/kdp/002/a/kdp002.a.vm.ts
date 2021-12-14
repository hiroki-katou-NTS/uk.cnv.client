module nts.uk.at.view.kdp002.a {
    export module viewmodel {

        type STATE = 'state1' | 'state2' | 'state3';

        export class ScreenModel {
            stampSetting: KnockoutObservable<StampSetting> = ko.observable({});
            stampClock: StampClock = new StampClock();
            stampTab: KnockoutObservable<StampTab> = ko.observable(new StampTab());
            stampGrid: KnockoutObservable<EmbossGridInfo> = ko.observable({});
            stampToSuppress: KnockoutObservable<StampToSuppress> = ko.observable({});
            stampResultDisplay: KnockoutObservable<IStampResultDisplay> = ko.observable({});
            serverTime: KnockoutObservable<any> = ko.observable('');
            workManagementMultiple: KnockoutObservable<boolean> = ko.observable(false);
            checkerShowWork2: KnockoutObservable<boolean> = ko.observable(false);

            pageComment: KnockoutObservable<string> = ko.observable('');
            commentColor: KnockoutObservable<string> = ko.observable('');

            state!: KnockoutComputed<STATE>;
            constructor() {
                const vm = this;

                vm.state = ko.computed({
                    read: () => {
                        const wmm = ko.unwrap(vm.workManagementMultiple);
                        const stampGrid = ko.unwrap(vm.stampGrid);
                        const displayMethod = ko.unwrap(stampGrid.displayMethod);

                        if (displayMethod === 1) {
                            return 'state2';
                        }
                        if (displayMethod === 2 && wmm) {
                            return 'state1';
                        }
                        if (!wmm && displayMethod === 2) {
                            return 'state2';

                        }
                        return 'state3';
                    }
                });
            }



            public startPage(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                nts.uk.ui.block.grayout();

                service.getWorkManagementMultiple().done((result: boolean) => {
                    self.workManagementMultiple(!result);

                    service.startPage().done((res: IStartPage) => {
                        self.stampSetting(res.stampSetting);

                        self.stampTab().bindData(res.stampSetting.pageLayouts);
                        self.stampGrid(new EmbossGridInfo(res, ko.unwrap(self.workManagementMultiple)));

                        self.stampGrid().yearMonth.subscribe((val) => {
                            self.getTimeCardData();
                        });

                        let stampToSuppress = res.stampToSuppress ? res.stampToSuppress : {};
                        stampToSuppress.isUse = res.stampSetting ? res.stampSetting.buttonEmphasisArt : false;
                        self.stampToSuppress(stampToSuppress);
                        self.stampResultDisplay(res.stampResultDisplay);
                        // add correction interval
                        self.stampClock.addCorrectionInterval(self.stampSetting().correctionInterval);
                        dfd.resolve();
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId, messageParams: res.parameterIds }).then(() => {
                            nts.uk.request.jump("com", "/view/ccg/008/a/index.xhtml");
                        });
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
				});

                return dfd.promise();
            }

            public getTimeCardData() {
                nts.uk.ui.errors.clearAll();
                $(".nts-input").trigger("validate");
                if (nts.uk.ui.errors.hasError()) {
                    return;
                }

                let self = this;
                nts.uk.ui.block.grayout();
                let data = {
                    date: self.stampGrid().yearMonth() + '/15'
                };
                service.getTimeCardData(data).done((timeCard) => {
                    self.stampGrid().bindItemData(timeCard.listAttendances);
                    reCalGridWidthHeight();
                }).fail((res) => {
                    nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }

            public getStampData() {
                nts.uk.ui.errors.clearAll();
                $(".nts-input").trigger("validate");
                if (nts.uk.ui.errors.hasError()) {
                    return;
                }

                let self = this;
                nts.uk.ui.block.grayout();
                const { employeeId } = __viewContext.user;
                const { startDate, endDate } = self.stampGrid().dateValue();

                service.getStampData({ startDate, endDate, employeeId }).done((stampDatas) => {
                    self.stampGrid().bindItemData(stampDatas);
                    reCalGridWidthHeight();
                }).fail((res) => {
                    nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }

            public getPageLayout(pageNo: number) {
                let self = this;
                let layout = _.find(self.stampTab().layouts(), (ly) => { return ly.pageNo === pageNo });

                if (layout) {
                    let btnSettings = layout.buttonSettings;
                    btnSettings.forEach(btn => {
                        btn.onClick = self.clickBtn1;
                    });
                    layout.buttonSettings = btnSettings;
                }

                return layout;
            }

            public clickBtn1(btn: any, layout: any) {
                const vm = this;
                const view = new ko.ViewModel();

                nts.uk.ui.block.invisible();
                nts.uk.request
                    .syncAjax("com", "server/time/now/")
                    .done((res) => {
                        let data = {
                            datetime: moment.utc(res).format('YYYY/MM/DD HH:mm:ss'),
                            authcMethod: 0,
                            stampMeans: 3,
                            reservationArt: btn.btnReservationArt,
                            changeHalfDay: btn.changeHalfDay,
                            goOutArt: btn.goOutArt,
                            setPreClockArt: btn.setPreClockArt,
                            changeClockArt: btn.changeClockArt,
                            changeCalArt: btn.changeCalArt
                        };
                        service.stampInput(data).done((res) => {
                            let param = {
                                sid: __viewContext.user.employeeId,
                                date: view.$date.now()
                            }
                            if (vm.stampResultDisplay().notUseAttr == 1 && btn.changeClockArt == 1) {
                                vm.openScreenC(btn, layout);
                            } else {
                                vm.openScreenB(btn, layout);
                            }
                        }).fail((res) => {
                            nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                        });
                    });
            }

            public openScreenB(button, layout) {
                let self = this;

                nts.uk.ui.windows.setShared("resultDisplayTime", self.stampSetting().resultDisplayTime);
                nts.uk.ui.windows.setShared("infoEmpToScreenB", {
                    employeeId: __viewContext.user.employeeId,
                    employeeCode: __viewContext.user.employeeCode,
                    mode: Mode.Personal,
                });
                nts.uk.ui.windows.setShared("screenB", {
                    screen: "KDP002"
                });
                nts.uk.ui.windows.sub.modal('/view/kdp/002/b/index.xhtml').onClosed(() => {
                    if (self.stampGrid().displayMethod() === 1) {
                        self.getStampData();
                    } else {
                        self.getTimeCardData();
                    }
                    self.reloadHighLight();
                    self.stampToSuppress.valueHasMutated();
                    self.openKDP002T(button, layout);
                });
            }

            public openScreenC(button, layout) {
                let self = this;

                let data = {
                    pageNo: layout.pageNo,
                    buttonDisNo: button.btnPositionNo
                }

                service.getError(data).done((res) => {
                    nts.uk.ui.windows.setShared('KDP010_2C', self.stampResultDisplay().displayItemId, true);
                    nts.uk.ui.windows.setShared("infoEmpToScreenC", {
                        employeeId: __viewContext.user.employeeId,
                        employeeCode: __viewContext.user.employeeCode,
                        mode: Mode.Personal,
                        error: res
                    });
                    nts.uk.ui.windows.setShared("screenC", {
                        screen: "KDP002"
                    });
                    nts.uk.ui.windows.sub.modal('/view/kdp/002/c/index.xhtml').onClosed(function (): any {
                        if (self.stampGrid().displayMethod() === 1) {
                            self.getStampData();
                        } else {
                            self.getTimeCardData();
                        }
                        self.reloadHighLight();
                        self.stampToSuppress.valueHasMutated();
                        self.openKDP002T(button, layout);
                    });
                });
            }

            public openKDP002T(button: ButtonSetting, layout) {
                const vm = new ko.ViewModel();
                let data = {
                    pageNo: layout.pageNo,
                    buttonDisNo: button.btnPositionNo
                }

                service.getError(data).done((res) => {
                    if (res && res.dailyAttdErrorInfos && res.dailyAttdErrorInfos.length > 0) {
                        nts.uk.ui.windows.setShared('KDP010_2T', res, true);

                        nts.uk.ui.windows.sub.modal('/view/kdp/002/t/index.xhtml').onClosed(function (): any {
                            let returnData = nts.uk.ui.windows.getShared('KDP010_T');
                            if (!returnData.isClose && returnData.errorDate) {
                                // T1	打刻結果の取得対象項目の追加
                                // 残業申請（早出）
                                let transfer = returnData.btn.transfer;
                                vm.$jump('at', returnData.btn.screen, { baseDate: transfer.appDate });
                                // nts.uk.request.jump(returnData.btn.screen, transfer);
                            }
                        });
                    }
                });
            }

            public reloadHighLight() {
                let self = this;
                    if (self.stampToSuppress().isUse) {
                        service.getHighlightSetting().done((res) => {
                            res.isUse = self.stampToSuppress().isUse;
                            self.stampToSuppress(res);
                        });
                    }
            }
        }
    }
    export enum Mode {
        Personal = 1, // 個人
        Shared = 2  // 共有 
    }

}
var paramSize = 0;
let reCalGridWidthHeight = () => {
    const resize = () => {
        var bottomMasterWrapper = $('#master-wrapper')[0].getBoundingClientRect().bottom;
        var topStampInfo = $('#stamp-info')[0].getBoundingClientRect().top;
        var h = bottomMasterWrapper - topStampInfo - 98;

        let stampBtnHeight = (h < 48 ? 48 : h) + 'px';
        const $hgrid = $('#stamp-history-list');
        const $cgrid = $('#time-card-list');
		if (paramSize !== h) {
			paramSize = h;
	        if ($hgrid.data('igGrid')) {
	            $hgrid.igGrid("option", "height", stampBtnHeight);
	            $hgrid.data("height", stampBtnHeight);
	        }
	        if ($cgrid.data('igGrid')) {
	            $cgrid.igGrid("option", "height", stampBtnHeight);
	            $cgrid.data("height", stampBtnHeight);
	        }
		}
    };
    if ($('#stamp-info')[0]) {
        setTimeout(resize);
    }
}