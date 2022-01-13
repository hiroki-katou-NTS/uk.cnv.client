module cmm045.a.viewmodel {
    import vmbase = cmm045.shr.vmbase;
    import getText = nts.uk.resource.getText;
    import block = nts.uk.ui.block;
    import character = nts.uk.characteristics;
    import request = nts.uk.request;
    import getShared = nts.uk.ui.windows.getShared;
	import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;
    import isNullOrEmpty = nts.uk.util.isNullOrEmpty;
    export class ScreenModel {
        menuName: KnockoutObservable<string> = ko.observable("");
        roundingRules: KnockoutObservableArray<vmbase.ApplicationDisplayAtr> = ko.observableArray([]);
        //delete switch button - ver35
//        selectedRuleCode: KnockoutObservable<any> = ko.observable(0);// switch button
        //lst fill in grid list
        items: KnockoutObservableArray<vmbase.DataModeApp> = ko.observableArray([]);
        //lst full data get from db
        lstApp: KnockoutObservableArray<vmbase.DataModeApp> = ko.observableArray([]);
        lstAppCommon: KnockoutObservableArray<vmbase.ApplicationDataOutput> = ko.observableArray([]);
        lstAppMaster: KnockoutObservableArray<vmbase.AppMasterInfo> = ko.observableArray([]);
        lstListAgent: KnockoutObservableArray<vmbase.ApproveAgent> = ko.observableArray([]);
        lstAppCompltSync: KnockoutObservableArray<vmbase.AppAbsRecSyncData> = ko.observableArray([]);

        // approvalMode: KnockoutObservable<boolean> = ko.observable(false);
        approvalCount: KnockoutObservable<vmbase.ApplicationStatus> = ko.observable(new vmbase.ApplicationStatus(0, 0, 0, 0, 0, 0));
        itemList: KnockoutObservableArray<any>;
        selectedIds: KnockoutObservableArray<any> = ko.observableArray([1,5]);// check box - ver50
        dateValue: KnockoutObservable<vmbase.Date> = ko.observable({ startDate: '', endDate: '' });
        itemApplication: KnockoutObservableArray<vmbase.ChoseApplicationList> = ko.observableArray([]);
        // selectedCode: KnockoutObservable<number> = ko.observable(-1);// combo box
        mode: KnockoutObservable<number> = ko.observable(1);
        startDateString: KnockoutObservable<string> = ko.observable("");
        endDateString: KnockoutObservable<string> = ko.observable("");
        useApprovalFunction : KnockoutObservable<number> = ko.observable(0);
        //UPDATE EA 4134
		//  USE(1, "Enum_UseClassificationAtr_USE"),
        //	NOT_USE(0, "Enum_UseClassificationAtr_NOT_USE");
        //  spr

        isSpr: KnockoutObservable<boolean> = ko.observable(false);
        // extractCondition: KnockoutObservable<number> = ko.observable(0);
        //ver33
        isHidden: KnockoutObservable<boolean> = ko.observable(false);
        //_______CCG001____
        ccgcomponent: any;
        showinfoSelectedEmployee: KnockoutObservable<boolean>;
        baseDate: KnockoutObservable<Date>;
        selectedEmployee: KnockoutObservableArray<any>;
        workplaceId: KnockoutObservable<string> = ko.observable("");
        employeeId: KnockoutObservable<string> = ko.observable("");
        //ver35
        lstSidFilter: KnockoutObservableArray<string> = ko.observableArray([]);
        lstContentApp: KnockoutObservableArray<any> = ko.observableArray([]);

        //ver60
        //Grid list item
        // apptypeGridColumns: KnockoutObservable<NtsGridListColumn>;
        selectedAppId: KnockoutObservableArray<string> = ko.observableArray([]);
		orderCD: KnockoutObservable<number> = ko.observable(0);
        appListExtractConditionDto: vmbase.AppListExtractConditionDto = new vmbase.AppListExtractConditionDto(null,null,true,true,0,0,false,[],true,false,false,false,false,true,[],[]);
        columnWidth: vmbase.columnWidth = new vmbase.columnWidth(true, 340, "", "");
        appListInfo: any = null;
        appListAtr: number;
        isBeforeCheck: KnockoutObservable<boolean> = ko.observable(true);
        isAfterCheck: KnockoutObservable<boolean> = ko.observable(true);
        isLimit500: KnockoutObservable<boolean> = ko.observable(false);
        isApprove: KnockoutObservable<boolean>;
        isActiveApprove: any;
		confirmAll: boolean = false;
		notConfirmAll: boolean = false;

        constructor() {
            let self = this;
            $(".popup-panel-cmm045").ntsPopup({
                position: {
                    my: "left bottom",
                    at: "right top",
                    of: ".hyperlink"
                },
                showOnStart: false,
                dismissible: false
            });

            $("a.hyperlink").click(() => {
            	$(".popup-panel-cmm045").ntsPopup("toggle");
				$(".popup-panel-cmm045").css('top', '215px');
            });

            $(window).on("mousedown.popup", function(e) {
                let control = $(".popup-panel-cmm045");
                if (!$(e.target).is(control)
                    && control.has(e.target).length === 0
                    && !$(e.target).is($(".hyperlink"))) {
                    $(".popup-panel-cmm045").ntsPopup("hide");
                }
            });

            self.itemList = ko.observableArray([
                { id: 1, name: getText('CMM045_20') },
                { id: 2, name: getText('CMM045_21') },
                { id: 3, name: getText('CMM045_22') },
                { id: 4, name: getText('CMM045_23') },
                { id: 5, name: getText('CMM045_24') }
            ]);

            /*self.selectedCode.subscribe(function(codeChanged) {
                self.filterByAppType(codeChanged);
            });*/

            //_____CCG001________
            self.selectedEmployee = ko.observableArray([]);
            self.showinfoSelectedEmployee = ko.observable(false);
            self.ccgcomponent = {

            showEmployeeSelection: false, // 検索タイプ
            systemType: 2, // システム区分 - 就業
            showQuickSearchTab: true, // クイック検索
            showAdvancedSearchTab: true, // 詳細検索
            showBaseDate: false, // 基準日利用
            showClosure: false, // 就業締め日利用
            showAllClosure: false, // 全締め表示
            showPeriod: false, // 対象期間利用
            periodFormatYM: true, // 対象期間精度

            /** Required parameter */
            baseDate: moment.utc().toISOString(), // 基準日
            inService: true, // 在職区分
            leaveOfAbsence: true, // 休職区分
            closed: true, // 休業区
            retirement: true, // 退職区分

            /** Quick search tab options */
            showAllReferableEmployee: true, // 参照可能な社員すべて
            showOnlyMe: true, // 自分だけ
            showSameWorkplace: true, // 同じ職場の社員
            showSameWorkplaceAndChild: true, // 同じ職場とその配下の社員

            /** Advanced search properties */
            showEmployment: true, // 雇用条件
            showWorkplace: true, // 職場条件
            showClassification: true, // 分類条件
            showJobTitle: true, // 職位条件
            showWorktype: true, // 勤種条件
            isMutipleCheck: true,
            /**
            * @param dataList: list employee returned from component.
            * Define how to use this list employee by yourself in the function's body.
            */


            returnDataFromCcg001: function(data: any){
                self.showinfoSelectedEmployee(true);
                self.selectedEmployee(data.listEmployee);
                console.log(data.listEmployee);
                self.lstSidFilter([]);
                _.each(data.listEmployee, function(emp){
                    self.lstSidFilter.push(emp.employeeId);
                });


				if(!self.checkConditionParam()) {
					return;
				}
				let empIDLst = [];
				_.each(data.listEmployee, function(emp){
                    empIDLst.push(emp.employeeId);
                });
				self.appListExtractConditionDto.opListEmployeeID = empIDLst;

				block.invisible();
				service.findByEmpIDLst(self.appListExtractConditionDto).done((data: any) => {
					return self.reload(data.appListExtractCondition, data.appListInfo);
				}).always(() => block.clear());
                // self.filter();
             }
            }
            self.getMenu();
            window.onresize = function(event: any) {
				if(self.mode()==1) {
					character.restore('TableColumnWidth1' + __viewContext.user.companyId + __viewContext.user.employeeId).then((obj: any) => {
						$('#status-div').width(955);
						if(window.innerWidth-90 < 965) {
							$('#app-resize').width(920);
							$('.nts-fixed-header-container .fixed-table').width(920);
							$('.nts-fixed-header-wrapper').width(937);
							$('.nts-fixed-header-container').width(920);
							$('.nts-fixed-header-container').css('max-width', 920);
							$('.nts-fixed-body-wrapper').width(920);
							$('.nts-fixed-body-table').width(920);
							$('.nts-fixed-body-container').width(937);
							$('.nts-fixed-body-container').css('max-width', 953);
						} else {
							$('#app-resize').width(window.innerWidth-134);
							$('.nts-fixed-header-container .fixed-table').width(window.innerWidth-134);
							$('.nts-fixed-header-wrapper').width(window.innerWidth-117);
							$('.nts-fixed-header-container').width(window.innerWidth-134);
							$('.nts-fixed-header-container').css('max-width', window.innerWidth-134);
							$('.nts-fixed-body-wrapper').width(window.innerWidth-134);
							$('.nts-fixed-body-table').width(window.innerWidth-134);
							$('.nts-fixed-body-container').width(window.innerWidth-117);
							$('.nts-fixed-body-container').css('max-width', window.innerWidth-101);
						}
	                    if(obj !== undefined) {
							$("col.check").width(obj.width.check);
							$("col.details").width(obj.width.details);
							$("col.applicantName").width(obj.width.applicantName);
							$("col.appType").width(obj.width.appType);
							$("col.prePostAtr").width(obj.width.prePostAtr);
							$("col.appDate").width(obj.width.appDate);
							$("col.appContent").width(obj.width.appContent);
							$("col.inputDate").width(obj.width.inputDate);
							$("col.reflectionStatus").width(obj.width.reflectionStatus);
							$("col.opApprovalStatusInquiry").width(obj.width.opApprovalStatusInquiry);
							$('.nts-fixed-header-container .fixed-table').width(_.sum(_.values(obj.width)));
							$('.nts-fixed-body-table').width(_.sum(_.values(obj.width)));
	                    } else {
	                        if($('.nts-fixed-header-container').width()-812 < 70) {
								$('col.appContent').width(70);
							} else {
								$('col.appContent').width($('.nts-fixed-header-container').width()-812);
							}
	                    }
						if(window.innerHeight-374 < 60) {
							$('.nts-fixed-body-container').height(60);
							$('.nts-fixed-body-wrapper').height(44);
						} else {
							$('.nts-fixed-body-container').height(window.innerHeight-374);
							$('.nts-fixed-body-wrapper').height(window.innerHeight-390);
						}
						let headerSize = $('.nts-fixed-header-wrapper .ui-widget-header').length,
							leftValue = 0;
						for(let i = 0; i < headerSize; i++) {
							leftValue += $('.nts-fixed-header-wrapper .ui-widget-header')[i].offsetWidth;
							$('.resize-handle')[i].style.left = leftValue + 'px';
						}
	                });
	            } else {
					character.restore('TableColumnWidth0' + __viewContext.user.companyId + __viewContext.user.employeeId).then((obj: any) => {
						$('#status-div').width(880);
		                if(window.innerWidth-90 < 880) {
							$('#app-resize').width(845);
							$('.nts-fixed-header-container .fixed-table').width(845);
							$('.nts-fixed-header-wrapper').width(862);
							$('.nts-fixed-header-container').width(845);
							$('.nts-fixed-header-container').css('max-width', 845);
							$('.nts-fixed-body-wrapper').width(845);
							$('.nts-fixed-body-table').width(845);
							$('.nts-fixed-body-container').width(862);
							$('.nts-fixed-body-container').css('max-width', 878);
						} else {
							$('#app-resize').width(window.innerWidth-129);
							$('.nts-fixed-header-container .fixed-table').width(window.innerWidth-129);
							$('.nts-fixed-header-wrapper').width(window.innerWidth-112);
							$('.nts-fixed-header-container').width(window.innerWidth-129);
							$('.nts-fixed-header-container').css('max-width', window.innerWidth-129);
							$('.nts-fixed-body-wrapper').width(window.innerWidth-129);
							$('.nts-fixed-body-table').width(window.innerWidth-129);
							$('.nts-fixed-body-container').width(window.innerWidth-112);
							$('.nts-fixed-body-container').css('max-width', window.innerWidth-96);
						}
	                    if(obj !== undefined) {
							$("col.details").width(obj.width.details);
							$("col.applicantName").width(obj.width.applicantName);
							$("col.appType").width(obj.width.appType);
							$("col.prePostAtr").width(obj.width.prePostAtr);
							$("col.appDate").width(obj.width.appDate);
							$("col.appContent").width(obj.width.appContent);
							$("col.inputDate").width(obj.width.inputDate);
							$("col.reflectionStatus").width(obj.width.reflectionStatus);
							$("col.opApprovalStatusInquiry").width(obj.width.opApprovalStatusInquiry);
							$('.nts-fixed-header-container .fixed-table').width(_.sum(_.values(obj.width)));
							$('.nts-fixed-body-table').width(_.sum(_.values(obj.width)));
	                    } else {
	                        if($('.nts-fixed-header-container').width()-775 < 70) {
								$('col.appContent').width(70);
							} else {
								$('col.appContent').width($('.nts-fixed-header-container').width()-775);
							}
	                    }
						if(window.innerHeight-340 < 60) {
							$('.nts-fixed-body-container').height(60);
							$('.nts-fixed-body-wrapper').height(44);
						} else {
							$('.nts-fixed-body-container').height(window.innerHeight-340);
							$('.nts-fixed-body-wrapper').height(window.innerHeight-356);
						}
						let headerSize = $('.nts-fixed-header-wrapper .ui-widget-header').length,
							leftValue = 0;
						for(let i = 0; i < headerSize; i++) {
							leftValue += $('.nts-fixed-header-wrapper .ui-widget-header')[i].offsetWidth;
							$('.resize-handle')[i].style.left = leftValue + 'px';
						}
	                });
				}
            }

			self.selectedIds.subscribe(value => {
				self.appListExtractConditionDto.opUnapprovalStatus = _.includes(value, 1) ? true : false;
				self.appListExtractConditionDto.opApprovalStatus = _.includes(value, 2) ? true : false;
				self.appListExtractConditionDto.opDenialStatus = _.includes(value, 3) ? true : false;
				self.appListExtractConditionDto.opAgentApprovalStatus = _.includes(value, 4) ? true : false;
				self.appListExtractConditionDto.opRemandStatus = _.includes(value, 5) ? true : false;
				self.appListExtractConditionDto.opCancelStatus = _.includes(value, 6) ? true : false;
			});

			self.selectedAppId.subscribe(value => {
				_.each(self.appListExtractConditionDto.opListOfAppTypes, x => {
					let appType = x.appType.toString();
					if(!_.isNull(x.opApplicationTypeDisplay)) {
						appType += x.opApplicationTypeDisplay.toString();
					};
					if(_.includes(value, appType)) {
						x.choice = true;
					} else {
						x.choice = false;
					}
				});
			});

			self.isBeforeCheck.subscribe(value => {
				self.appListExtractConditionDto.preOutput = value;
			});
        	self.isAfterCheck.subscribe(value => {
				self.appListExtractConditionDto.postOutput = value;
			});

			self.orderCD.subscribe(value => {
				self.appListExtractConditionDto.appDisplayOrder = value;
			});
        }

        saveContentWidth() {
            let self = this;
            var contentWidth = $("col.appContent").width();

            if(self.mode() == 0) {
                self.columnWidth.appLstAtr = true;
				self.columnWidth.width = {
					'details': $("col.details").width(),
					'applicantName': $("col.applicantName").width(),
					'appType': $("col.appType").width(),
					'prePostAtr': $("col.prePostAtr").width(),
					'appDate': $("col.appDate").width(),
					'appContent': $("col.appContent").width(),
					'inputDate': $("col.inputDate").width(),
					'reflectionStatus': $("col.reflectionStatus").width(),
					'opApprovalStatusInquiry': $("col.opApprovalStatusInquiry").width()
				};
                self.columnWidth.cID = __viewContext.user.companyId;
                self.columnWidth.sID = __viewContext.user.employeeId;
            } else {
                self.columnWidth.appLstAtr = false;
				self.columnWidth.width = {
					'check': $("col.check").width(),
					'details': $("col.details").width(),
					'applicantName': $("col.applicantName").width(),
					'appType': $("col.appType").width(),
					'prePostAtr': $("col.prePostAtr").width(),
					'appDate': $("col.appDate").width(),
					'appContent': $("col.appContent").width(),
					'inputDate': $("col.inputDate").width(),
					'reflectionStatus': $("col.reflectionStatus").width(),
					'opApprovalStatusInquiry': $("col.opApprovalStatusInquiry").width()
				};
                self.columnWidth.cID = __viewContext.user.companyId;
                self.columnWidth.sID = __viewContext.user.employeeId;
            }
            console.log(contentWidth);

            if (self.mode() == 0) {
                character.restore('TableColumnWidth0' + __viewContext.user.companyId + __viewContext.user.employeeId).then((obj) => {
                    if(obj !== undefined) {
						let detailsWidth = obj.width.details,
							applicantNameWidth = obj.width.applicantName,
							appTypeWidth = obj.width.appType,
							prePostAtrWidth = obj.width.prePostAtr,
							appDateWidth = obj.width.appDate,
							contentWidth = obj.width.appContent,
							inputDateWidth = obj.width.inputDate,
							reflectionStatusWidth = obj.width.reflectionStatus,
							opApprovalStatusInquiryWidth = obj.width.opApprovalStatusInquiry;
						let oldWidth = {
							'details': detailsWidth,
							'applicantName': applicantNameWidth,
							'appType': appTypeWidth,
							'prePostAtr': prePostAtrWidth,
							'appDate': appDateWidth,
							'appContent': contentWidth,
							'inputDate': inputDateWidth,
							'reflectionStatus': reflectionStatusWidth,
							'opApprovalStatusInquiry': opApprovalStatusInquiryWidth
						};
                        if(JSON.stringify(self.columnWidth.width) !== JSON.stringify(oldWidth)) {
                            character.save('TableColumnWidth0' + __viewContext.user.companyId + __viewContext.user.employeeId, self.columnWidth).then(() => {
                                nts.uk.ui.dialog.info({ messageId: "Msg_357" });
                            });
                        }
                    } else {
						let widthAuto = window.innerWidth - 90 > 880 ? window.innerWidth - 129 : 845,
							detailsWidth = 55,
							applicantNameWidth = 120,
							appTypeWidth = 90,
							prePostAtrWidth = 65,
							appDateWidth = 155,
							contentWidth = 340,
							inputDateWidth = 120,
							reflectionStatusWidth = 75,
							opApprovalStatusInquiryWidth = 95;
							contentWidth = widthAuto - 55 - 120 - 90 - 65- 155 - 120 - 75 - 95 - 5;
						let oldWidth = {
							'details': detailsWidth,
							'applicantName': applicantNameWidth,
							'appType': appTypeWidth,
							'prePostAtr': prePostAtrWidth,
							'appDate': appDateWidth,
							'appContent': contentWidth,
							'inputDate': inputDateWidth,
							'reflectionStatus': reflectionStatusWidth,
							'opApprovalStatusInquiry': opApprovalStatusInquiryWidth
						};
                        if(JSON.stringify(self.columnWidth.width) !== JSON.stringify(oldWidth)) {
                            character.save('TableColumnWidth0' + __viewContext.user.companyId + __viewContext.user.employeeId, self.columnWidth).then(() => {
                                nts.uk.ui.dialog.info({ messageId: "Msg_357" });
                            });
                        }
                    }
                });
            } else {
                character.restore('TableColumnWidth1' + __viewContext.user.companyId + __viewContext.user.employeeId).then((obj) => {
                    if(obj !== undefined) {
						let checkWidth = obj.width.check,
							detailsWidth = obj.width.details,
							applicantNameWidth = obj.width.applicantName,
							appTypeWidth = obj.width.appType,
							prePostAtrWidth = obj.width.prePostAtr,
							appDateWidth = obj.width.appDate,
							contentWidth = obj.width.appContent,
							inputDateWidth = obj.width.inputDate,
							reflectionStatusWidth = obj.width.reflectionStatus,
							opApprovalStatusInquiryWidth = obj.width.opApprovalStatusInquiry;
						let oldWidth = {
							'check': checkWidth,
							'details': detailsWidth,
							'applicantName': applicantNameWidth,
							'appType': appTypeWidth,
							'prePostAtr': prePostAtrWidth,
							'appDate': appDateWidth,
							'appContent': contentWidth,
							'inputDate': inputDateWidth,
							'reflectionStatus': reflectionStatusWidth,
							'opApprovalStatusInquiry': opApprovalStatusInquiryWidth
						};
                        if(JSON.stringify(self.columnWidth.width) !== JSON.stringify(oldWidth)) {
                            character.save('TableColumnWidth1' + __viewContext.user.companyId + __viewContext.user.employeeId, self.columnWidth).then(() => {
                                nts.uk.ui.dialog.info({ messageId: "Msg_357" });
                            });
                        }
                    } else {
						let widthAuto = window.innerWidth - 90 > 965 ? window.innerWidth - 134 : 920,
							checkWidth = 35,
							detailsWidth = 55,
							applicantNameWidth = 120,
							appTypeWidth = 90,
							prePostAtrWidth = 65,
							appDateWidth = 157,
							contentWidth = 340,
							inputDateWidth = 120,
							reflectionStatusWidth = 75,
							opApprovalStatusInquiryWidth = 95;
							contentWidth = widthAuto - 35 - 55 - 120 - 90 - 65- 157 - 120 - 75 - 95 - 5;
						let oldWidth = {
							'check': checkWidth,
							'details': detailsWidth,
							'applicantName': applicantNameWidth,
							'appType': appTypeWidth,
							'prePostAtr': prePostAtrWidth,
							'appDate': appDateWidth,
							'appContent': contentWidth,
							'inputDate': inputDateWidth,
							'reflectionStatus': reflectionStatusWidth,
							'opApprovalStatusInquiry': opApprovalStatusInquiryWidth
						};
                        if(JSON.stringify(self.columnWidth.width) !== JSON.stringify(oldWidth)) {
                            character.save('TableColumnWidth1' + __viewContext.user.companyId + __viewContext.user.employeeId, self.columnWidth).then(() => {
                                nts.uk.ui.dialog.info({ messageId: "Msg_357" });
                            });
                        }
                    }
                });
            }
        }

		checkConditionParam() {
			const self = this;
            if (nts.uk.ui.errors.hasError()) {
                return false;
            }
            //check filter
            //check startDate
            if (self.dateValue().startDate == null || self.dateValue().startDate == '') {//期間開始日付または期間終了日付が入力されていない
                $('#daterangepicker>.ntsDateRange_Container>.ntsDateRange>.ntsStartDate input').ntsError('set', {messageId:"Msg_359"});
                return false;
            }
            //check endDate
            if (self.dateValue().endDate == null || self.dateValue().endDate == '') {//期間開始日付または期間終了日付が入力されていない
                $('#daterangepicker>.ntsDateRange_Container>.ntsDateRange>.ntsEndDate input').ntsError('set', {messageId:"Msg_359"});
                return false;
            }
            if (self.mode() == 1 && self.selectedIds().length == 0) {//承認状況のチェックの確認
                nts.uk.ui.dialog.alertError({ messageId: "Msg_360" });
                return false;
            }
			if (!self.appListExtractConditionDto.preOutput && !self.appListExtractConditionDto.postOutput) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_1722" }).then(() => {
                    $(".popup-panel-cmm045").ntsPopup("toggle");
                });
                return false;
			}
			let selectAppTypeLst = _.filter(self.appListExtractConditionDto.opListOfAppTypes, o => o.choice);
			if (_.isEmpty(selectAppTypeLst)) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_1723" }).then(() => {
                    $(".popup-panel-cmm045").ntsPopup("toggle");
                });
                return false;
			}
			return true;
		}

		findByPeriod() {
			const self = this;
			if(!self.checkConditionParam()) {
				return;
			}
			self.appListExtractConditionDto.periodStartDate = self.dateValue().startDate;
			self.appListExtractConditionDto.periodEndDate = self.dateValue().endDate;

			block.invisible();
			service.findByPeriod(self.appListExtractConditionDto).done((data: any) => {
				return self.reload(data.appListExtractCondition, data.appListInfo);
			}).always(() => block.clear());
		}

        start(): JQueryPromise<any> {
			const self = this;
            block.invisible();
            let paramSprCmm045: vmbase.IntefaceSPR = __viewContext.transferred.value == null ?
                    null : __viewContext.transferred.value.PARAM_SPR_CMM045;
            //spr call
            if(paramSprCmm045 !== undefined && paramSprCmm045 !== null && !nts.uk.request.location.current.isFromMenu){
                // character.save('AppListExtractCondition', null);
                let date: vmbase.Date = { startDate: paramSprCmm045.startDate, endDate: paramSprCmm045.endDate }
                self.dateValue(date);
                self.mode(paramSprCmm045.mode);
                self.isSpr(true);
            }
            return character.restore("AppListExtractCondition").then((obj) => {
				let url = $(location).attr('search');
	            let urlParam: number = undefined;
				if(!_.isUndefined(url.split("=")[1])) {
					urlParam = parseInt(url.split("=")[1]);
				}
				if(_.isNaN(_.toNumber(urlParam))) {
					if(_.isEmpty(obj)) {
						self.mode(1);
					} else {
						self.appListExtractConditionDto = obj;
						self.updateFromAppListExtractCondition();
						self.mode(obj.appListAtr);	
					}
					nts.uk.request.location.current.queryString.items = {a: self.mode().toString()};
					history.pushState({}, null, "?a="+self.mode());
				} else {
					character.remove('AppListExtractCondition');
					self.mode(urlParam);	
				}
                //write log
                let paramLog = {programId: 'CMM045',
                                screenId: 'A',
                                queryString: 'a='+self.mode()};
                service.writeLog(paramLog);
				return service.getAppNameInAppList();
			}).then((data: any) => {
				if(_.isEmpty(self.appListExtractConditionDto.opListOfAppTypes)) {
					self.appListExtractConditionDto.opListOfAppTypes = data;
				}
				self.updateFromAppListExtractCondition();
				// self.selectedAppId(_.chain(self.appListExtractConditionDto.opListOfAppTypes).filter(o => o.choice).map(x => x.appType).value());
                _.forEach(data, item => {
                    if(item.appName != "") {
						let appType = item.appType.toString();
						if(!_.isNull(item.opApplicationTypeDisplay)) {
							appType += item.opApplicationTypeDisplay.toString();
						};
                        self.itemApplication().push(new vmbase.ChoseApplicationList(appType, item.appName));
                    }
                });
                _.uniqBy(self.itemApplication(), ['appType', 'appName']);
				let newParam: any = {
					mode: self.mode(),
					device: 0,
					listOfAppTypes: data,
					appListExtractCondition: self.appListExtractConditionDto
                };
				if(paramSprCmm045 !== undefined && paramSprCmm045 !== null) {
					newParam.startDate = paramSprCmm045.startDate;
					newParam.endDate = paramSprCmm045.endDate;
				}

                // self.itemList()
				return service.getApplicationList(newParam);
			}).then((data: any) => {
				return self.reload(data.appListExtractCondition, data.appListInfo);
			}).then((data) => {
				if(data) {
					$('#ccgcomponent').ntsGroupComponent(self.ccgcomponent);
				}
			}).fail((res) => {
				nts.uk.ui.dialog.alertError({ messageId: res.messageId, messageParams: res.parameterIds }).then(() => {
					nts.uk.request.jump("com", "/view/ccg/008/a/index.xhtml");
				});
			}).always(() => block.clear());
        }

		reload(appListExtractCondition: any, appListInfo: any) {
			const self = this;
			if(!_.isNull(appListExtractCondition)) {
				self.appListExtractConditionDto = appListExtractCondition;
				self.updateFromAppListExtractCondition();
			}
			self.appListInfo = appListInfo;
			if(!isNullOrUndefined(appListInfo) && !isNullOrUndefined(appListInfo.displaySet)){
				let displaySet = appListInfo.displaySet.useApprovalFunction;
				self.useApprovalFunction(displaySet)
			};
			let newItemLst = [];
			_.each(appListInfo.appLst, item => {
				newItemLst.push(new vmbase.DataModeApp(item));
			});
			self.items(newItemLst);
			//if (appListInfo.numberOfApp != null) {
            self.approvalCount(new vmbase.ApplicationStatus(
				appListInfo.numberOfApp.unApprovalNumber,
				appListInfo.numberOfApp.approvalNumber,
                appListInfo.numberOfApp.approvalAgentNumber,
				appListInfo.numberOfApp.cancelNumber,
				appListInfo.numberOfApp.remandNumner,
                appListInfo.numberOfApp.denialNumber));
            //}

            if (self.mode() == 1) {
                $("#grid1").ntsGrid("destroy");
                let colorBackGr = self.fillColorbackGrAppr();
                let lstHidden: Array<any> = self.findRowHidden(self.items());
                self.reloadGridApproval(lstHidden,colorBackGr, false);
                // self.reloadGridApproval(lstHidden,colorBackGr, self.isHidden());
            } else {
                let colorBackGr = self.fillColorbackGr();
                $("#grid2").ntsGrid("destroy");
                self.reloadGridApplicaion(colorBackGr, false);
                // self.reloadGridApplicaion(colorBackGr, self.isHidden());
          	}
            self.isLimit500(appListInfo.moreThanDispLineNO);
            self.isApprove = ko.computed(() => {
                return self.mode() == 1 && self.items().length > 0 && _.filter(self.items(), x => {
					return x.checkAtr && moment(x.opAppStartDate).add(-(self.appListInfo.displaySet.appDateWarningDisp), "days").isSameOrBefore(moment.utc())
				}).length > 0 && self.appListInfo.displaySet.appDateWarningDisp !== 0;
            }, self);
            self.isActiveApprove = ko.computed(() => {
                return self.items().length > 0 && _.filter(self.items(), x => x.checkAtr).length > 0;
            }, self);

			return true;
		}

		updateFromAppListExtractCondition() {
			const self = this;
			let obj = self.appListExtractConditionDto,
				date: vmbase.Date = { startDate: obj.periodStartDate, endDate: obj.periodEndDate }
            self.dateValue(date);
			self.isBeforeCheck(obj.preOutput ? true : false);	
			self.isAfterCheck(obj.postOutput ? true : false);
            let arraySelectedIds = [];
            if (obj.opUnapprovalStatus) {//未承認
                arraySelectedIds.push(1);
            }
            if (obj.opApprovalStatus) {//承認済み
                arraySelectedIds.push(2);
            }
            if (obj.opDenialStatus) {//否認
                arraySelectedIds.push(3);
            }
            if (obj.opAgentApprovalStatus) {//代行承認済み
                arraySelectedIds.push(4);
            }
            if (obj.opRemandStatus) {//差戻
                arraySelectedIds.push(5);
            }
            if (obj.opCancelStatus) {//取消
                arraySelectedIds.push(6);
            }
			self.orderCD(obj.appDisplayOrder);
			self.selectedIds(arraySelectedIds);
			self.lstSidFilter(obj.opListEmployeeID);
			self.selectedAppId(_.chain(obj.opListOfAppTypes).filter(o => o.choice).map(x => {
				let appType = x.appType.toString();
				if(!_.isNull(x.opApplicationTypeDisplay)) {
					appType += x.opApplicationTypeDisplay.toString();
				};
				return appType;
			}).value());
			self.appListAtr = obj.appListAtr;
		}

        setupGrid(options: {
            withCcg001: boolean,
            width: any,
            height: any,
            columns: Array<{
                headerText: string,
                width: string,
                key: string,
                extraClassProperty?: string,
                checkbox?: { visible: Function, applyToProperty: string },
                button?: { text: string, click: Function }
            }>
        },useApprovalFunction: number) {

            let $container = $("#app-grid-container");
            $container.hide();

            if ($container.children("#not-constructed").length === 1) {

                $container.empty();

                if (options.withCcg001) {
                    $container.addClass("with-ccg001");
                    $("#app-resize").addClass("with-ccg001");
                }

                // header
                let $colgroup = $("<colgroup/>");
                let $trHead = $("<tr/>");
                options.columns.forEach(column => {
                    $("<col/>")
                        .attr("width", column.width)
                        .appendTo($colgroup)
                        .addClass(column.key);

                    let $th = $("<th/>")
                        .addClass("ui-widget-header")
						.addClass(column.key);

                    // batch check
                    if (column.checkbox !== undefined) {
                        let items = this.items();
                        let checkableItems = items.filter(item => item.checkAtr === true);
                        if (checkableItems.length > 0) {
                            $("<label/>")
                                .addClass("ntsCheckBox")
                                .append($("<input/>")
                                    .attr("id", "batch-check")
                                    .attr("type", "checkbox")
                                    .addClass(column.key))
                                .append($("<span/>").addClass(useApprovalFunction == 1 ? "box" : ""))
                                .change((e) => {
                                    let checked = $(e.target).prop("checked");
                                    $appGrid.find("input[type=checkbox]." + column.key)
                                        .prop("checked", checked);
                                    items
                                        .filter(item => item.checkAtr === true)
                                        .forEach(item => item.check = checked);
									this.items(items);
                                })
                                .appendTo($th);
                        }
                    }
                    else {
                        $th.text(column.headerText);
                    }

                    $th.appendTo($trHead);
                });
                let $thead = $("<thead/>")
                    .append($trHead);

                // body
                let $tbody = $("<tbody/>");


                // build grid
                let $appGrid = $("<table/>")
                    .attr("id", "app-grid")
                    .data("size", { width: options.width, height: options.height })
                    .append($colgroup)
                    .append($thead)
                    .append($tbody);

                // event handler
                options.columns.forEach(column => {
                    if (column.button !== undefined) {
                        $appGrid.on("click", "." + column.key, column.button.click);
                    }

                    if (column.checkbox !== undefined) {
                        $appGrid.on("change", "input." + column.key, e => {
                            let appId = $(e.target).closest("td").data("app-id");
                            let checked = $(e.target).prop("checked");
                            let items = this.items();
                            items.filter(item => item.appID === appId)[0].check = checked;

                            // sync with batch check
                            let allChecked = true;
                            for (let i = 0; i < items.length; i++) {
                                let item = items[i];
                                if (item.checkAtr === false) continue;
                                if (item.check === false) {
                                    allChecked = false;
                                    break;
                                }
                            }
                            $("#batch-check").prop("checked", allChecked);
                        });
                    }
                });

                $container.append($appGrid).show();

                let size = $appGrid.data("size");
                fixedTable.init($appGrid, {
                    height: size.height,
                    width: size.width
                });

                dragResize(
                    $container.find(".nts-fixed-header-container table"),
                    $container.find(".nts-fixed-body-container table"));
            }

            // $("#app-resize").css("width", options.width - 20);

            this.loadGridData(options.columns);

            $container.show();
        }

        loadGridData(
            columns: Array<{
                headerText: string,
                width: string,
                key: string,
                extraClassProperty?: string,
                checkbox?: { visible: Function, applyToProperty: string },
                button?: { text: string, click: Function }
            }>) {
			const self = this;
            let $container = $("#app-grid-container");
            let $tbody = $container.find(".nts-fixed-body-wrapper tbody");
            $tbody.empty();

            self.items().forEach((item, i) => {
                let $tr = $("<tr/>");
                columns.forEach(column => {

                    let $td = $("<td/>")
                        .data("app-id", item.appID)
                        .addClass(column.key);

                    if (column.extraClassProperty !== undefined) {
                        $td.addClass(item[column.extraClassProperty]);
                    }

                    if (column.checkbox !== undefined) {
                        var extraClass = "";
                        if(self.appListInfo.displaySet.appDateWarningDisp !== 0 && moment(item.opAppStartDate).add(-(self.appListInfo.displaySet.appDateWarningDisp), "days") <= moment.utc()) {
                            extraClass = "approvalCell";
                        } else {
                            extraClass = "";
                        }
                        if (column.checkbox.visible(item) === true) {
                        	if(self.useApprovalFunction() == 1){
                                $("<label/>")
                                    .addClass("ntsCheckBox")
                                    .append($("<input/>")
                                        .attr("type", "checkbox")
                                        .addClass(column.key))
                                    .append($("<span/>").addClass("box"))
                                    .appendTo($td)
                                    .parent("td")
                                    .addClass(extraClass);
							}else {
                                $("<label/>")
                                    .addClass("ntsCheckBox")
                                    .append($("<input/>")
                                        .attr("type", "checkbox")
                                        .addClass(column.key))
                                    .append($("<span/>").addClass(""))
                                    .appendTo($td)
                                    .parent("td")
                                    .addClass(extraClass);
							}

                        }
                    }
                    else if (column.button !== undefined) {
                        $("<button/>")
                            .addClass(column.key)
                            .text(column.button.text)
                            .appendTo($td);
                    }
                    else if(column.key == 'appDate') {
                        var date = moment(item.opAppStartDate).format("M/D(ddd)");
                        if(item.opAppStartDate !== item.opAppEndDate) {
                            date = self.appDateRangeColor(moment(item.opAppStartDate).format("M/D(ddd)"), moment(item.opAppEndDate).format("M/D(ddd)"));
                            $td.html(date);
                        } else {
							let linkAppDate = null, paramDate1 = null, paramDate2 = null;
							if(item.appType==10) {
								if(!_.isNull(item.opComplementLeaveApp.complementLeaveFlg)) {
									linkAppDate = moment(item.opComplementLeaveApp.linkAppDate).format("M/D(ddd)");
									if(item.opComplementLeaveApp.complementLeaveFlg==1) {
										paramDate1 = date;
										paramDate2 = linkAppDate;	
									} else {
										paramDate1 = linkAppDate;
										paramDate2 = date;
									}
								}	
							}
                            $td.html(self.appDateColor(paramDate1 ? paramDate1 : date, "", "", paramDate2));
                        }
                    }
					else if(column.key == 'appContent') {
						$td.html(self.customContent(column.key, item));
						$td.addClass(item.opBackgroundColor);
					}
                    else {
                        $td.html(self.customContent(column.key, item));
                    }

                    $("td.appType").css("white-space", "normal");

                    $td.appendTo($tr);
                });
                $tr.appendTo($tbody);
            });

            resetColumnsSize(
                $container.find("colgroup").eq(0).children(),
                $container.find(".nts-fixed-header-wrapper table"),
                $container.find(".nts-fixed-body-wrapper table"));
        }

		customContent(key: string, item: any) {

			const self = this;
			if(key=='applicantName') {
				let nameStr = '';
				if(!_.isNull(self.appListInfo) && self.appListInfo.displaySet.workplaceNameDisp==1) {
					//if(!nts.uk.util.isNullOrEmpty(item.workplaceName)) {
					nameStr += item.workplaceName + '\n';
					//}
				}
				nameStr += item[key];
				if(item.application.employeeID != item.application.enteredPerson) {
					nameStr += item.opEntererName;
				}
				return _.escape(nameStr).replace(/\n/g, '<br/>');
			}

			if(key=='appType') {
				let appInfo = { appName: ''};
				if(item.opAppTypeDisplay) {
					appInfo = _.find(self.appListExtractConditionDto.opListOfAppTypes, o => o.appType == item[key] && o.opApplicationTypeDisplay==item.opAppTypeDisplay);
				} else {
					appInfo = _.find(self.appListExtractConditionDto.opListOfAppTypes, o => o.appType == item[key]);
				}
				if(_.isUndefined(appInfo)) {
					return '';
				} else {
					return _.escape(appInfo.appName);
				}
			}
			if(key=='prePostAtr') {
				if(item[key]==0) {
					return _.escape(nts.uk.resource.getText('KAF000_47'));
				} else {
					return _.escape(nts.uk.resource.getText('KAF000_48'));
				}
			}
			if(key=='appContent') {
				return _.escape(item[key]).replace(/\n/g, '<br/>');
            }
            if(key=='inputDate') {
                let cl = "";
                let time = moment(item[key]).format("M/D(ddd) H:mm");
				let isSyncApp = false;
				if(item.appType==10) {
					if(!_.isNull(item.opComplementLeaveApp.complementLeaveFlg)) {
						isSyncApp = true;	
					}
				}
                // var time = nts.uk.time.formatDate(new Date(item[key]), "m/dD hh:mm");

                if(_.includes(time, ''))
                return self.inputDateColor(time, cl, isSyncApp);
            }
			if(key=='reflectionStatus') {
				let statusStr = _.escape(getText(item[key]));
				let isSyncApp = false;
				if(item.appType==10) {
					if(!_.isNull(item.opComplementLeaveApp.complementLeaveFlg)) {
						isSyncApp = true;	
					}
				}
				if(isSyncApp) {
					return '<div>' + statusStr + '</div><div style="margin-top: 5px;">' + statusStr + '</div>';
				} else {
					return '<div>' + statusStr + '</div>';
				}
			}
			return _.escape(item[key]);
		}

        reloadGridApplicaion(colorBackGr: any, isHidden: boolean) {

            var self = this;
            let widthAuto = isHidden == false ? 1175 : 1110;
            // let widthAuto = isHidden == false ? 1250 : 1185;
            // widthAuto = screen.width - 100 >= widthAuto ? widthAuto : screen.width - 100;
            widthAuto = window.innerWidth - 90 > 880 ? window.innerWidth - 129 : 845;
            var detailsWidth = 55,
				applicantNameWidth = 120,
				appTypeWidth = 90,
				prePostAtrWidth = 65,
				appDateWidth = 155,
				contentWidth = 340,
				inputDateWidth = 120,
				reflectionStatusWidth = 75,
				opApprovalStatusInquiryWidth = 95;
            character.restore('TableColumnWidth0' + __viewContext.user.companyId + __viewContext.user.employeeId).then((obj) => {
                    if(obj !== undefined && self.mode() === 0 && obj.appLstAtr === true && obj.cID === __viewContext.user.companyId && obj.sID === __viewContext.user.employeeId) {
						detailsWidth = obj.width.details,
						applicantNameWidth = obj.width.applicantName,
						appTypeWidth = obj.width.appType,
						prePostAtrWidth = obj.width.prePostAtr,
						appDateWidth = obj.width.appDate,
						contentWidth = obj.width.appContent,
						inputDateWidth = obj.width.inputDate,
						reflectionStatusWidth = obj.width.reflectionStatus,
						opApprovalStatusInquiryWidth = obj.width.opApprovalStatusInquiry;
                    } else {
                        contentWidth = widthAuto - 55 - 120 - 90 - 65- 155 - 120 - 75 - 95 - 5;
                    }
            }).then(() => {
                let columns = [
                    { headerText: getText('CMM045_50'), key: 'details', width: detailsWidth, button: {
                        text: getText('CMM045_50'),
                        click: (e) => {
                            let targetAppId = $(e.target).closest("td").data("app-id");
                            let lstAppId = self.items().map(app => app.appID);
                            // window.localStorage.setItem('UKProgramParam', 'a=0');
                            character.save('AppListExtractCondition', self.appListExtractConditionDto).then(() => {
								nts.uk.request.jump("/view/kaf/000/b/index.xhtml", { 'listAppMeta': lstAppId, 'currentApp': targetAppId });
							});
                        }
                    } },
                    { headerText: getText('CMM045_51'), key: 'applicantName', width: applicantNameWidth,  },
                    { headerText: getText('CMM045_52'), key: 'appType', width: appTypeWidth},
                    { headerText: getText('CMM045_53'), key: 'prePostAtr', width: prePostAtrWidth, hidden: false},
                    { headerText: getText('CMM045_54'), key: 'appDate', width: appDateWidth},
                    { headerText: getText('CMM045_55'), key: 'appContent', width: contentWidth},
                    // { headerText: getText('CMM045_55'), key: 'appContent', width: '340px'},
                    { headerText: getText('CMM045_56'), key: 'inputDate', width: inputDateWidth},
                    { headerText: getText('CMM045_57'), key: 'reflectionStatus', width: reflectionStatusWidth, extraClassProperty: "appStatusName"},
                    { headerText: getText('CMM045_58'), key: 'opApprovalStatusInquiry', width: opApprovalStatusInquiryWidth }
                ];
                let heightAuto = window.innerHeight - 340 > 60 ? window.innerHeight - 340 : 60;
                // let heightAuto = window.innerHeight - 342 >= 325 ? window.innerHeight - 342 : 325;
                this.setupGrid({
                    withCcg001: true,
                    width: widthAuto,
                    height: heightAuto,
                    columns: columns.filter(c => c.hidden !== true),
                });

                $("#app-resize").css("width", widthAuto);
            });

        }

        fillColorbackGr(): Array<vmbase.CellState>{
            let self = this;
            let result = [];
            _.each(self.items(), function(item) {
                let rowId = item.appID;

                if(item.reflectionStatus === 'CMM045_63') {
                    item.appStatusName = 'approvalCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['approvalCell']));
                }
                if(item.reflectionStatus === 'CMM045_64') {
                    item.appStatusName = 'reflectCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['reflectCell']));
                }
                if(item.reflectionStatus === 'CMM045_65') {
                    item.appStatusName = 'denialCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['denialCell']));
                }
                if(item.reflectionStatus === 'CMM045_62') {
                    item.appStatusName = 'unapprovalCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['unapprovalCell']));
                }
                if(item.reflectionStatus === 'CMM045_66') {
                    item.appStatusName = 'remandCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['remandCell']));
                }
                if(item.reflectionStatus === 'CMM045_67') {
                    item.appStatusName = 'cancelCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['cancelCell']));
                }
            });
            return result;
        }
        fillColorbackGrAppr(): Array<vmbase.CellState>{
            let self = this;
            let result = [];
            _.each(self.items(), function(item) {
                let rowId = item.appID;

                if(item.reflectionStatus === 'CMM045_63') {
                    item.appStatusName = 'approvalCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['approvalCell']));
                }
                if(item.reflectionStatus === 'CMM045_64') {
                    item.appStatusName = 'reflectCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['reflectCell']));
                }
                if(item.reflectionStatus === 'CMM045_65') {
                    item.appStatusName = 'denialCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['denialCell']));
                }
                if(item.reflectionStatus === 'CMM045_62') {
                    item.appStatusName = 'unapprovalCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['unapprovalCell']));
                }
                if(item.reflectionStatus === 'CMM045_66') {
                    item.appStatusName = 'remandCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['remandCell']));
                }
                if(item.reflectionStatus === 'CMM045_67') {
                    item.appStatusName = 'cancelCell';
                    result.push(new vmbase.CellState(rowId,'reflectionStatus',['cancelCell']));
                }
            });
            return result;
        }
        fillColorText(): Array<vmbase.TextColor>{
            //fill color text
            let self = this;
            let result = [];
            _.each(self.items(), function(item) {
                if(item.appType == 10){
                    return;
                }
                //color text appDate
                let color = item.appDate.substring(9,10);
                if (color == '土') {//土
                    result.push(new vmbase.TextColor(item.appID,'appDate','saturdayCell'));
                }
                if (color == '日') {//日
                    result.push(new vmbase.TextColor(item.appID,'appDate','sundayCell'));
                }
                //fill color text input date
                let colorIn = item.inputDate.substring(9,10);
                if (colorIn == '土') {//土
                    result.push(new vmbase.TextColor(item.appID,'inputDate','saturdayCell'));
                }
                if (colorIn == '日') {//日
                    result.push(new vmbase.TextColor(item.appID,'inputDate','sundayCell'));
                }
             });
            return result;
        }
        reloadGridApproval(lstHidden: Array<any>, colorBackGr: any, isHidden: boolean) {

            var self = this;
            let widthAuto = isHidden == false ? 1175 : 1110;
            // widthAuto = screen.width - 35 >= widthAuto ? widthAuto : screen.width - 35;
            widthAuto = window.innerWidth - 90 > 965 ? window.innerWidth - 134 : 920;
            var checkWidth = 35,
				detailsWidth = 55,
				applicantNameWidth = 120,
				appTypeWidth = 90,
				prePostAtrWidth = 65,
				appDateWidth = 157,
				contentWidth = 340,
				inputDateWidth = 120,
				reflectionStatusWidth = 75,
				opApprovalStatusInquiryWidth = 95;
            character.restore('TableColumnWidth1' + __viewContext.user.companyId + __viewContext.user.employeeId).then((obj) => {
                    if(obj !== undefined && self.mode() === 1 && obj.appLstAtr === false && obj.cID === __viewContext.user.companyId && obj.sID === __viewContext.user.employeeId) {
						checkWidth = obj.width.check,
						detailsWidth = obj.width.details,
						applicantNameWidth = obj.width.applicantName,
						appTypeWidth = obj.width.appType,
						prePostAtrWidth = obj.width.prePostAtr,
						appDateWidth = obj.width.appDate,
						contentWidth = obj.width.appContent,
						inputDateWidth = obj.width.inputDate,
						reflectionStatusWidth = obj.width.reflectionStatus,
						opApprovalStatusInquiryWidth = obj.width.opApprovalStatusInquiry;
                    } else {
                        contentWidth = widthAuto - 35 - 55 - 120 - 90 - 65- 157 - 120 - 75 - 95 - 5;
                    }
            }).then(() => {
                let columns = [
                        { headerText: getText('CMM045_49'), key: 'check', dataType: 'boolean', width: checkWidth, checkbox: {
                                visible: item => item.checkAtr === true,
                                applyToProperty: "check"
                            } },
                        { headerText: getText('CMM045_50'), key: 'details', width: detailsWidth, button: {
                                text: getText('CMM045_50'),
                                click: (e) => {
                                    let targetAppId = $(e.target).closest("td").data("app-id");
                                    let lstAppId = self.items().map(app => app.appID);
                                    // nts.uk.localStorage.setItem('UKProgramParam', 'a=1');
                                    character.save('AppListExtractCondition', self.appListExtractConditionDto).then(() => {
                                        nts.uk.request.jump("/view/kaf/000/b/index.xhtml", { 'listAppMeta': lstAppId, 'currentApp': targetAppId });
                                    });
                                }
                            } },
                        { headerText: getText('CMM045_51'), key: 'applicantName', width: applicantNameWidth },
                        { headerText: getText('CMM045_52'), key: 'appType', width: appTypeWidth},
                        { headerText: getText('CMM045_53'), key: 'prePostAtr', width: prePostAtrWidth, hidden: isHidden},
                        { headerText: getText('CMM045_54'), key: 'appDate', width: appDateWidth},
                        { headerText: getText('CMM045_55'), key: 'appContent', width: contentWidth},
                        { headerText: getText('CMM045_56'), key: 'inputDate', width: inputDateWidth},
                        { headerText: getText('CMM045_57'), key: 'reflectionStatus', width: reflectionStatusWidth, extraClassProperty: "appStatusName"},
                        { headerText: getText('CMM045_58'), key: 'opApprovalStatusInquiry', width: opApprovalStatusInquiryWidth },
                    ]


                let heightAuto = window.innerHeight - 364 > 60 ? window.innerHeight - 364 : 60;
                // let heightAuto = window.innerHeight - 375 > 292 ? window.innerHeight - 375 : 292;
                this.setupGrid({
                    withCcg001: true,
                    width: widthAuto,
                    height: heightAuto,
                    columns: columns.filter(c => c.hidden !== true)
                },self.useApprovalFunction());

                $("#app-resize").css("width", widthAuto);
            });

        }
        
        inputDateColor_Old(input: string, classApp: string): string{
            let inputDate = '<div class = "' + classApp + '" >' + input + '</div>';
            //fill color text input date
            let colorIn = input.substring(11,12);
            if (colorIn == '土') {//土
                inputDate = '<div class = "saturdayCell ' + classApp + '" >' + '<div>'  + input + '</div></div>';
            }
            if (colorIn == '日') {//日
                inputDate = '<div class = "sundayCell ' + classApp + '" >' + '<div>'  + input + '</div></div>';
            }
            return inputDate;
        }
        //ver41
        inputDateColor(input: string, classApp: string, isSyncApp: boolean): string{
            let inputDate = '<div class = "' + classApp + '" >' + '<div>' + input + '</div>';
			if(isSyncApp) {
				inputDate += '<div style="margin-top: 5px;">' + input + '</div>';
			}
			inputDate += '</div>';
            //fill color text input date
            let a = input.split("(")[1];
            let colorIn = a.substring(0,1);
            if (colorIn == '土') {//土
                inputDate = '<div class = "saturdayCell ' + classApp + '" >' + '<div>' + input + '</div>';
				if(isSyncApp) {
					inputDate += '<div style="margin-top: 5px;">' + input + '</div>';
				}
				inputDate += '</div>';
            }
            if (colorIn == '日') {//日
                inputDate = '<div class = "sundayCell ' + classApp + '" >' + '<div>' + input + '</div>';
				if(isSyncApp) {
					inputDate += '<div style="margin-top: 5px;">' + input + '</div>';
				}
				inputDate += '</div>';
            }
            return inputDate;
        }
        appDateColor(date: string, classApp: string, priod: string, linkAppDate: string): string{
            let appDate = '<div class = "' + classApp + '" >' + '<div>' + date + priod + '</div>';
			let dateDay = date.split("(")[1].substring(0,1);
			if (dateDay == '土') {//土
				appDate = '<div class = "saturdayCell ' + classApp + '" >' + '<div>' + date + priod + '</div></div>';
			}
			if (dateDay == '日') {//日
				appDate = '<div class = "sundayCell ' + classApp + '" >' + '<div>' + date + priod + '</div></div>';
			}
            if(linkAppDate) {
            	let linkAppDateHtml = "<div style='margin-top: 5px;'>" + linkAppDate + priod + "</div>";
				let linkAppDateDay = linkAppDate.split("(")[1].substring(0,1);
				if (linkAppDateDay == '土') {
					linkAppDateHtml = '<div class="saturdayCell" style="margin-top: 5px;">' + '<div>' + linkAppDate + priod + '</div></div>';
				}
				if (linkAppDateDay == '日') {
					linkAppDateHtml = '<div class="sundayCell" style="margin-top: 5px;">' + '<div>' + linkAppDate + priod + '</div></div>';
				}
				appDate += linkAppDateHtml;
            }

            return appDate;
        }
        //doi ung theo y amid-mizutani さん
        appDateRangeColor(startDate: string, endDate: string): string{
            let sDate = '<div class = "dateRange" >' + startDate + '</div>';;
            let eDate =  '<div class = "dateRange" >' + endDate + '</div>';
            //color text appDate
            let a = startDate.split("(")[1];
            let b = endDate.split("(")[1];
            let color1 = a.substring(0,1);
            if (color1 == '土') {//土
                sDate = '<div class = "saturdayCell  dateRange" >' + '<div>' + startDate +  '</div></div>';
            }
            if (color1 == '日') {//日
                sDate = '<div class = "sundayCell  dateRange" >' + '<div>'  + startDate + '</div></div>';
            }
            let color2 = b.substring(0,1);
            if (color2 == '土') {//土
                eDate = '<div class = "saturdayCell  dateRange" >' + '<div>'  + endDate + '</div></div>';
            }
            if (color2 == '日') {//日
                eDate = '<div class = "sundayCell  dateRange" >' + '<div>'  + endDate +  '</div></div>';
            }
            return sDate + '<div class = "dateRange" >' + '－' +  '</div>' +  eDate;
        }
        
        /**
         * find application holiday work by id
         */
        checkSync(appId: string, lstCompltLeave: Array<vmbase.AppAbsRecSyncData>):vmbase.AppAbsRecSyncData{
            return _.find(lstCompltLeave, function(complt){
                return complt.appMainID == appId;
        });
        }
        /**
         * find master info by id
         */
        findMasterInfo(lstMaster: Array<vmbase.AppMasterInfo>, appId: string) {
            return _.find(lstMaster, function(master) {
                return master.appID == appId;
            });
        }
        //yyyy/MM/dd(W)
        convertDate(date: string) {
            let a: number = moment(date, 'YYYY/MM/DD').isoWeekday();
            switch (a) {
                case 1://Mon
                    return date + '(月)';
                case 2://Tue
                    return date + '(火)';
                case 3://Wed
                    return date + '(水)';
                case 4://Thu
                    return date + '(木)';
                case 5://Fri
                    return date + '(金)';
                case 6://Sat
                    return date + '(土)';
                default://Sun
                    return date + '(日)';
            }
        }
        //MM/dd(W) ver24
        convertDateMDW(date: string) {
            let a: number = moment(date, 'YYYY/MM/DD').isoWeekday();
            let toDate = moment(date, 'YYYY/MM/DD').toDate();
            let dateMDW = (toDate.getMonth()+1) + '/'+ toDate.getDate();
            switch (a) {
                case 1://Mon
                    return dateMDW + '(月)';
                case 2://Tue
                    return dateMDW + '(火)';
                case 3://Wed
                    return dateMDW + '(水)';
                case 4://Thu
                    return dateMDW + '(木)';
                case 5://Fri
                    return dateMDW + '(金)';
                case 6://Sat
                    return dateMDW + '(土)';
                default://Sun
                    return dateMDW + '(日)';
            }
        }
        //Short_MD
        convertDateShort_MD(date: string) {
            let a: number = moment(date, 'YYYY/MM/DD').isoWeekday();
            let toDate = moment(date, 'YYYY/MM/DD').toDate();
            let dateMDW = (toDate.getMonth()+1) + '/'+ toDate.getDate();
            return dateMDW;
        }
        //yyyy/MM/dd(W) hh:mm
        convertDateTime_Old(dateTime: string) {
            let a: number = moment(dateTime, 'YYYY/MM/DD hh:mm').isoWeekday();
            let date = dateTime.split(" ")[0];
            let time = dateTime.split(" ")[1];
            return this.convertDate(date) + ' ' + time;
        }
        //ver41
        //Short_MDW  hh:mm : MM/dd(W) hh:mm
        convertDateTime(dateTime: string) {
            let a: number = moment(dateTime, 'YYYY/MM/DD hh:mm').isoWeekday();
            let date = dateTime.split(" ")[0];
            let time = dateTime.split(" ")[1];
            return this.convertDateMDW(date) + ' ' + time;
        }
        
        /**
         * find row hidden
         */
        findRowHidden(lstItem: Array<vmbase.DataModeApp>): any{
            let lstHidden = []
            _.each(lstItem, function(item){
                if(item.appStatusNo != 5){
                    lstHidden.push(item.appID);
                }
            });
            return lstHidden;
        }
        /**
         * find check box
         */
        findcheck(selectedIds: Array<any>, idCheck: number): boolean {
            let check = false;
            _.each(selectedIds, function(id) {
                if (id == idCheck) {
                    check = true;
                }
            });
            return check;
        }
        
        /**
         * count status when filter by appType
         */
        countStatus(lstApp: Array<vmbase.DataModeApp>): vmbase.ApplicationStatus{
            var self = this;
            let unApprovalNumber = 0;
            let approvalNumber = 0;
            let approvalAgentNumber = 0;
            let cancelNumber = 0;
            let remandNumner = 0;
            let denialNumber = 0;
            _.each(lstApp, function(app){
                let add = self.checkSync(app.appID, self.lstAppCompltSync()) !== undefined ? 2 : 1;
                if(app.appStatusNo == 5){ unApprovalNumber += add; }//UNAPPROVED:5
                if(app.appStatusNo == 4){//APPROVED: 4
                    let agent = self.findAgent(app.appID);
                    if(agent != undefined && agent.agentId != null && agent.agentId != '' && agent.agentId.match(/^\s+$/) == null){
                        approvalAgentNumber += add;
                    }else{
                        approvalNumber += add;
                    }
                }
                if(app.appStatusNo == 3){ cancelNumber += add; }//CANCELED: 3
                if(app.appStatusNo == 2){ remandNumner += add; }//REMAND: 2
                if(app.appStatusNo == 1){ denialNumber += add; }//DENIAL: 1
            })
            return new vmbase.ApplicationStatus(unApprovalNumber, approvalNumber,
                approvalAgentNumber, cancelNumber, remandNumner,denialNumber);
        }
        findAgent(appId: string): any{
            return _.find(this.lstListAgent(), function(agent){
                return agent.appID == appId;
            });
        }
        convertTime_Short_HM(time: number): string {
            let hh = Math.floor(time / 60);
            let min1 = Math.floor(time % 60);
            let min = '';
            if (min1 >= 10) {
                min = min1.toString();
            } else {
                min = '0' + min1;
            }
            return hh + ':' + min;
        }
        //find content app
        findContent(appId: string): any{
            let self = this;
            return _.find(self.lstContentApp(), function(app) {
                return app.appId == appId;
            });
        }

        approveAll() {
            console.log("Approve all");
        }
        getMenu(){
            let self = this;
            block.invisible();
            service.getMenu().done((data)=>{
                if(!isNullOrEmpty(data)){
                    let item = _.filter(data, (e)=>e.param.equals(self.mode() == 1 ? "a=1" :"a=0"));
                    if(!isNullOrEmpty(item)){
                        self.menuName(item[0].name);
					}
                };
            }).always(()=>{
                block.clear()
            });
        };
        print(params: any) {
            let self = this;
            let lstApp = self.appListInfo,
                programName = self.menuName().replace('CMM045A ', '');
            lstApp.appLst = ko.toJS(self.items);
            lstApp.displaySet.startDateDisp = self.appListExtractConditionDto.periodStartDate;
            lstApp.displaySet.endDateDisp = self.appListExtractConditionDto.periodEndDate;
			block.invisible();
            const command = { appListAtr: self.appListAtr, lstApp: lstApp, programName: programName };
            service.print(command).always(() => { 
				block.clear(); 
				$('#daterangepicker .ntsEndDatePicker').focus();
			});
            
        }

		checkDialog(itemLst: any, itemConfirmLst: any, confirmAllPreApp: boolean, notConfirmAllPreApp: boolean, confirmAllActual: boolean, notConfirmAllActual: boolean): any {
			const self = this;
			let dfd = $.Deferred();
			if(_.isEmpty(itemLst)) {
				return dfd.resolve(itemConfirmLst);
			}
			let item = itemLst[0];
			if(item.appType!=AppType.OVER_TIME_APPLICATION && item.appType!=AppType.HOLIDAY_WORK_APPLICATION) {
				itemConfirmLst.push(item);
				return self.checkDialog(_.slice(itemLst, 1), itemConfirmLst, confirmAllPreApp, notConfirmAllPreApp, confirmAllActual, notConfirmAllActual).then((result: any) => {
					return dfd.resolve(result);
				});
			}
			if(_.isEmpty(item.opBackgroundColor)) {
				itemConfirmLst.push(item);
				return self.checkDialog(_.slice(itemLst, 1), itemConfirmLst, confirmAllPreApp, notConfirmAllPreApp, confirmAllActual, notConfirmAllActual).then((result: any) => {
					return dfd.resolve(result);
				});
			}
			if(item.opBackgroundColor=='bg-pre-application-excess') {
				if(confirmAllPreApp) {
					itemConfirmLst.push(item);
					return self.checkDialog(_.slice(itemLst, 1), itemConfirmLst, confirmAllPreApp, notConfirmAllPreApp, confirmAllActual, notConfirmAllActual).then((result: any) => {
						return dfd.resolve(result);
					});
				}
				if(notConfirmAllPreApp) {
					return self.checkDialog(_.slice(itemLst, 1), itemConfirmLst, confirmAllPreApp, notConfirmAllPreApp, confirmAllActual, notConfirmAllActual).then((result: any) => {
						return dfd.resolve(result);
					});
				}
			} else {
				if(confirmAllActual) {
					itemConfirmLst.push(item);
					return self.checkDialog(_.slice(itemLst, 1), itemConfirmLst, confirmAllPreApp, notConfirmAllPreApp, confirmAllActual, notConfirmAllActual).then((result: any) => {
						return dfd.resolve(result);
					});
				}
				if(notConfirmAllActual) {
					return self.checkDialog(_.slice(itemLst, 1), itemConfirmLst, confirmAllPreApp, notConfirmAllPreApp, confirmAllActual, notConfirmAllActual).then((result: any) => {
						return dfd.resolve(result);
					});
				}
			}
			let appInfo = { appName: ''},
				appName = "";
			if(item.opAppTypeDisplay) {
				appInfo = _.find(self.appListExtractConditionDto.opListOfAppTypes, o => o.appType == item.appType && o.opApplicationTypeDisplay==item.opAppTypeDisplay);
			} else {
				appInfo = _.find(self.appListExtractConditionDto.opListOfAppTypes, o => o.appType == item.appType);
			}
			if(_.isUndefined(appInfo)) {
				appName = '';
			} else {
				appName = _.escape(appInfo.appName);
			}
			nts.uk.ui.windows.setShared("CMM045B_PARAMS", {
				applicantName: item.applicantName,
				appName,
				appDate: item.appDate,
				opBackgroundColor: item.opBackgroundColor,
				appContent: item.appContent,
				isMulti: itemLst.length > 1,
				confirmAllPreApp: confirmAllPreApp, 
				notConfirmAllPreApp: notConfirmAllPreApp, 
				confirmAllActual: confirmAllActual, 
				notConfirmAllActual: notConfirmAllActual
			});
			nts.uk.ui.windows.sub.modal("/view/cmm/045/b/index.xhtml").onClosed(() => {
				let result = nts.uk.ui.windows.getShared('CMM045B_RESULT');
				if(result) {
					if(result.confirm) {
						itemConfirmLst.push(item);
						return self.checkDialog(_.slice(itemLst, 1), itemConfirmLst, 
											result.confirmAllPreApp, 
											result.notConfirmAllPreApp, 
											result.confirmAllActual, 
											result.notConfirmAllActual).then((result: any) => {
							return dfd.resolve(result);
						});	
					}
				}
				return self.checkDialog(_.slice(itemLst, 1), itemConfirmLst, 
											result ? result.confirmAllPreApp : false, 
											result ? result.notConfirmAllPreApp : false, 
											result ? result.confirmAllActual : false, 
											result ? result.notConfirmAllActual : false).then((result: any) => {
					return dfd.resolve(result);
				});
			});
			return dfd.promise();
		}

		appListApprove(isApprovalAll: boolean) {
			const self = this;
			let	msgConfirm = '';
			if(isApprovalAll) {
				let checkBoxList = $("#app-grid-container").find(".nts-fixed-body-wrapper tbody").find("tr").find("td.check").find("span");
				_.each(checkBoxList, checkbox => {
					let appID = $(checkbox).closest("td").data("app-id"),
						currentItem = _.find(self.items(), item => item.appID == appID);
					if(!_.isUndefined(currentItem)) {
						if(!currentItem.check) {
							checkbox.click();
						}
					}
				});
				msgConfirm = 'Msg_1551';
			} else {
				msgConfirm = 'Msg_1549';
			}
			nts.uk.ui.dialog.confirm({ messageId: msgConfirm}).ifYes(() => {
				block.invisible();
				let listOfApplicationCmds: any = [];
				_.each(self.items(), function(item) {
					// 対象の申請が未承認の申請の場合
					if(!item.checkAtr) {
						return;
					}
					// INPUT「一括承認」＝True
					if(!isApprovalAll) {
						if(!item.check)	{
							return;
						}
					}
					if(item.appType == 10){
						if(!_.isNull(item.opComplementLeaveApp.complementLeaveFlg)) {
							let linkItem = _.clone(item);
							linkItem.appID = item.opComplementLeaveApp.linkAppID;
							linkItem.appDate = item.opComplementLeaveApp.linkAppDate;
							linkItem.opAppStartDate = item.opComplementLeaveApp.linkAppDate;
							linkItem.opAppEndDate = item.opComplementLeaveApp.linkAppDate;
							linkItem.application = item.opComplementLeaveApp.application;
							listOfApplicationCmds.push(item);
	                    	listOfApplicationCmds.push(linkItem);	
							return;
						}
	                }
	            	listOfApplicationCmds.push(item);
	            });
				if(_.isEmpty(listOfApplicationCmds)) {
					block.clear();
					return;
				}
				
				self.checkDialog(listOfApplicationCmds, [], false, false).then((listCmdAfterConfirm: any) => {
					let device = 0,
						command = 
						{ 
							isApprovalAll, 
							device, 
							listOfApplicationCmds: listCmdAfterConfirm
						};
					service.approveCheck(command).then((data: any) => {
						if(data) {
							let comfirmData = [];
							_.each(Object.keys(data.successMap), (dataAppID: any) => {
								let obj = _.find(listOfApplicationCmds, o => o.appID == dataAppID);
								if(!_.isUndefined(obj)) {
									comfirmData.push(obj);
								}
							});
							return service.approverAfterConfirm(comfirmData).done((data)=>{
								//service.reflectListApp(Object.keys(data.successMap));
							});
						}
					}).then((data: any) => {
						if(data) {
							let isInfoDialog = true,
								displayMsg = "";
							if(!_.isEmpty(data.successMap)) {
								displayMsg += nts.uk.resource.getMessage('Msg_220') + "\n";
							} else {
								isInfoDialog = false;
							}
							if(!_.isEmpty(data.failMap)) {
								if(isInfoDialog) {
									displayMsg += nts.uk.resource.getMessage('Msg_1726');
								} else {
									displayMsg += nts.uk.resource.getMessage('Msg_1725');
								}
								let itemFailMap = _.filter(listOfApplicationCmds, item => _.includes(Object.keys(data.failMap), item.appID));
								_.each(itemFailMap, item => {
									let appInfo = _.find(self.appListExtractConditionDto.opListOfAppTypes, o => o.appType == item.appType),
										appName = "";
									if(!_.isUndefined(appInfo)) {
										appName = appInfo.appName;
									}
									displayMsg += "\n " + item.applicantName  + " " + item.appDate + " " + appName + ": " + data.failMap[item.appID];
								});
							}
							if(_.isEmpty(displayMsg)) {
								displayMsg += nts.uk.resource.getMessage('Msg_1725');
							}
							if(isInfoDialog) {
								nts.uk.ui.dialog.info(displayMsg).then(() => {$('#daterangepicker .ntsEndDatePicker').focus()});
							} else {
							 	nts.uk.ui.dialog.alertError(displayMsg).then(() => {$('#daterangepicker .ntsEndDatePicker').focus()});
							}
							return data;
						}
		            }).then((data) => {
						if(!_.isEmpty(data.successMap)) {
							return service.findByPeriod(self.appListExtractConditionDto);
						}
					}).then((data: any) => {
						if(data) {
							return self.reload(data.appListExtractCondition, data.appListInfo);
						}
					}).always(() => {
	                    block.clear();
	                    $('#daterangepicker .ntsEndDatePicker').focus();
	                });
				});
			});
		}
    }
}
