module nts.uk.at.view.ksm007.a {

    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import modal = nts.uk.ui.windows.sub.modal;
    import block = nts.uk.ui.block;
    import resource = nts.uk.resource;

    export class ScreenModel {
        options: any;
        currentIds: KnockoutObservable<any> = ko.observable(null);
        currentCodes: KnockoutObservable<any> = ko.observable([]);
        currentNames: KnockoutObservable<any> = ko.observable([]);
        workplaceGroupList: KnockoutObservable<any> = ko.observable([]);
        registerForm: KnockoutObservable<RegisterForm> = ko.observable(new RegisterForm());
        isModeNew: KnockoutObservable<boolean> = ko.observable(false);
        nightShiftOperationAtr: KnockoutObservable<number> = ko.observable(0);

        constructor() {

            let self = this;
            self.options = {
                itemList: self.workplaceGroupList,
                currentCodes: self.currentCodes,
                currentNames: self.currentNames,
                currentIds: self.currentIds,
                multiple: false,
                tabindex: 2,
                isAlreadySetting: false,
                showEmptyItem: false,
                reloadData: ko.observable(''),
                height: 378,
                selectedMode: 1
            };

            self.currentIds.subscribe((val) => {            
                if (val && val.length > 0) {
                    
                    self.isModeNew(false); //edit

                    service.getNightShiftInfor({ workplaceGroupId: val }).done((data) => {
                        if( data) {
                            self.nightShiftOperationAtr(data.nightShiftOperationAtr || 0);
                            if (data.clockHourMinuteStart) {
                                let time = _.split(data.clockHourMinuteStart, ':');
                                let timeFormat: any = nts.uk.time.format.byId("ClockDay_Short_HM", parseInt(time[0]) * 60 + parseInt(time[1]));
                                self.registerForm().startTime(timeFormat);
                            } else
                                self.registerForm().startTime(null);

                            if (data.clockHourMinuteEnd) {
                                let time = _.split(data.clockHourMinuteEnd, ':');
                                let timeFormat: any = nts.uk.time.format.byId("ClockDay_Short_HM", parseInt(time[0]) * 60 + parseInt(time[1]));
                                self.registerForm().endTime(timeFormat);
                            } else 
                                self.registerForm().endTime(null);
                        } else {
                            self.registerForm().startTime(null);
                            self.registerForm().endTime(null);
                            self.nightShiftOperationAtr(0);
                        }
                    });

                    service.getWorkplaceGroupInfo(val).done((res) => {
                        // data server return list format
                        if (!_.isNil(res) && res.workplaces && res.workplaces[0]) {
                            self.registerForm().bindData(res.workplaces[0]);
                        }

                        service.getWorkplaceByGroup(val).done((wkps) => {
                            self.getAndBindWorkplaceInfo(wkps, new Date());
                        });
                    });
                    $('#requiredName').focus();
                } else {
                    self.nightShiftOperationAtr(0);
                }
                nts.uk.ui.errors.clearAll();
            });

            service.getOptionInformation().done((data) => {
                if( _.isNil(data))
                    self.registerForm().selectedWkpType(0); //normal
                else 
                    self.registerForm().checkWorkplaceGroupTypes(data);
            });

            if (self.workplaceGroupList().length === 0) {
                self.createNew();                
            }

            self.registerForm().selectedWkpType.subscribe(value => {
                if (value == 0) {
                    $("#multi-list_displayContainer").height(316);
                    $("#multi-list_scrollContainer").height(316);
                    $("#workplace-group-pannel").height(390);
                } else {
                    $("#multi-list_displayContainer").height(354);
                    $("#multi-list_scrollContainer").height(354);
                    $("#workplace-group-pannel").height(428);
                }
            });
            self.registerForm().selectedWkpType.valueHasMutated();
        }

        startPage(): JQueryPromise<any> {
            let dfd = $.Deferred();
            dfd.resolve();
            return dfd.promise();
        }

        register() {
            let self = this;
            self.registerForm().trimData();
            
            let wpType = self.registerForm().selectedWkpType();

            $(".nts-input").trigger("validate");

            if (self.registerForm().workplaces().length === 0) {
                $('#workplace-list').ntsError('set', resource.getMessage("MsgB_2", [resource.getText('Com_Workplace')]), "MsgB_2");
            }

            if (nts.uk.ui.errors.hasError()) {
                return;
            }
            nts.uk.ui.block.grayout();
            if (self.registerForm().newMode()) {
                service.registerWorkplaceGroup(self.registerForm().convertToCommand(null))
                    .done((res) => {
                        self.checkWorkplaceGroupRegisterResult(res, wpType);
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                    }).always(function () {
                        nts.uk.ui.block.clear();
                    });
            } else {
                service.updateWorkplaceGroup(self.registerForm().convertToCommand(self.currentIds()))
                    .done((res) => {
                        self.checkWorkplaceGroupRegisterResult(res, -1);
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                    }).always(function () {
                        nts.uk.ui.block.clear();
                    });
            }
        }

        checkWorkplaceGroupRegisterResult(res: any, wpType: number = 0) {
            const self = this;
            let resultProcess = res.replaceResult;
            let listWorkplaceInfo = res.listWorkplaceInfo;
            let listWorkplaceGroupInfo = res.workplaceGroupResult;
            let bundledErrors: Array<any> = [];
            for (let idx in resultProcess) {
                let result = resultProcess[idx];
                if (resultProcess[idx].workplaceReplacement == 3) {
                    let workplaceInfo = _.find(listWorkplaceInfo, (wkp: any) => { return wkp.wkpid == result.wkpid; });
                    let workplaceGroupInfo = _.find(listWorkplaceGroupInfo, (wkp: any) => { return wkp.wkpgrpid == result.wkpgrpid; });
                    if (workplaceInfo && workplaceGroupInfo) {
                        bundledErrors.push({
                            message: resource.getMessage('Msg_1630', [workplaceInfo.workplaceCode, workplaceInfo.workplaceName, workplaceGroupInfo.wkpgrpname]),
                            messageId: "Msg_1630",
                            supplements: {}
                        });
                    }
                }
            }
            if (self.registerForm().newMode()) {
                self.options.reloadData.valueHasMutated();
                self.options.currentIds(res.wkpgrpid);
            } else {
                self.options.reloadData.valueHasMutated();
                // self.options.currentIds(res.wkpGrId);
                self.options.currentIds.valueHasMutated();
            }
            if (res.resProcessResult) {
                let mgsId = ( wpType > 0 ) ? "Msg_2097" :  'Msg_15';
                nts.uk.ui.dialog.info({ messageId: mgsId }).then(() => {
                    if (mgsId == "Msg_2097") self.openDialogScreenB();
                });
            }
            if (bundledErrors.length > 0) {
                nts.uk.ui.dialog.bundledErrors({ errors: bundledErrors });
            }
        }

        deleteWkpGroup() {
            nts.uk.ui.block.invisible();
            let self = this;
            let param = { wkpGrID: self.currentIds() };

            nts.uk.ui.dialog.confirm({ messageId: "Msg_18" })
                .ifYes(() => {
                    let i = _.findIndex(self.workplaceGroupList(), x => { return x.id == self.currentIds() });
                    let nextSelectedCode;
                    if (self.workplaceGroupList().length == 1) {
                        nextSelectedCode = '';
                    } else if (i === 0) {
                        nextSelectedCode = self.workplaceGroupList()[1].id;
                    } else if (i === (self.workplaceGroupList().length - 1)) {
                        nextSelectedCode = self.workplaceGroupList()[self.workplaceGroupList().length - 2].id;
                    } else {
                        nextSelectedCode = self.workplaceGroupList()[i + 1].id;
                    }

                    service.deleteWorkplaceGroup(param).done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" })
                            .then(() => {
                                self.options.reloadData.valueHasMutated();
                                if (self.workplaceGroupList().length == 1) {
                                    self.createNew();
                                } else {
                                    self.currentIds(nextSelectedCode);
                                }
                            });
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                    }).always(function () {
                        nts.uk.ui.block.clear();
                    });
                }).ifNo(() => {
                    nts.uk.ui.block.clear();
                });

        }

        removeWorkplace() {
            let self = this;
            if (self.registerForm().workplaces().length === 0 || self.registerForm().selectedWorkplaces().length === 0) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_1628" });
                return;
            }

            self.registerForm().removeWorkplace();
        }

        createNew() {
            let self = this;
            self.registerForm().clearData();
            self.currentIds(null);
            self.isModeNew(true);
            $('#requiredCode').focus();
            self.currentIds([]);
        }

        /**
         * open dialog CDL008
         * chose work place
         * @param baseDate, isMultiple, workplaceId
         * @return workplaceId
         */
        openDialogCDL008() {
            let self = this;
            block.grayout();
            setShared('inputCDL008', {
                selectedCodes: _.map(self.registerForm().workplaces(), 'workplaceId'),
                baseDate: moment(new Date()).toDate(),
                isMultiple: true,
                selectedSystemType: 0,
                isrestrictionOfReferenceRange: false,
                showNoSelection: false,
                isShowBaseDate: false
            });
            modal('com', "/view/cdl/008/a/index.xhtml").onClosed(() => {
                block.clear();
                let data = getShared('outputCDL008');
                let baseDate = getShared('baseDateCDL008');
                if (data && data.length > 0) {
                    self.getAndBindWorkplaceInfo(data, baseDate);
                }
            });
        }

        public getAndBindWorkplaceInfo(workplaceIds, date) {
            let self = this;
            let data = {
                workplaceIds: workplaceIds,
                baseDate: moment(date).format('YYYY/MM/DD')
            }
            service.getWorkplaceInfo(data).done((res) => {
                self.registerForm().bindWorkplace(res);
                $('#workplace-list').ntsError('clear');
            });
        }

        /**
        * Export excel
        */
        public exportExcel(): void {
            let self = this;
            block.grayout();
            service.exportExcel().fail(function (error) {
                nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                block.clear();
            }).always(() => {
                block.clear();
            });
        }

        public openDialogScreenB() {
            const self = this;
            let selectedWkpType: number = 0;
            if (self.registerForm().workplaceGroupTypes().length > 0)
                selectedWkpType = self.registerForm().selectedWkpType();
            nts.uk.ui.windows.setShared('inputScreenB', {
                wpGroupId: self.currentIds(),
                typeGroup: selectedWkpType,
                wpGroupCode: self.registerForm().workplaceGroupCd(),
                wpGroupName: self.registerForm().workplaceGroupName(),
            });

            modal('at', "/view/ksm/007/b/index.xhtml").onClosed(() => {
                let data = getShared('outputScreenB');
                if (data && data.isSave) {
                    self.currentIds.valueHasMutated();
                }
            });
        }
    }
}