 /// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kaf000.b.viewmodel {
	import character = nts.uk.characteristics;
    import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;
    import UserType = nts.uk.at.view.kaf000.shr.viewmodel.model.UserType;
    import Status = nts.uk.at.view.kaf000.shr.viewmodel.model.Status;
    import ApprovalAtr = nts.uk.at.view.kaf000.shr.viewmodel.model.ApprovalAtr;
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
	import PrintContentOfEachAppDto = nts.uk.at.view.kaf000.shr.viewmodel.PrintContentOfEachAppDto;
	import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
	import Kaf011BViewModel = nts.uk.at.view.kaf011.b.viewmodel.Kaf011BViewModel;

    @bean()
    class Kaf000BViewModel extends ko.ViewModel {
        listApp: KnockoutObservableArray<string> = ko.observableArray([]);
        currentApp: KnockoutObservable<string> = ko.observable("");
        appType: KnockoutObservable<number>;
        appDispInfoStartupOutput: KnockoutObservable<any> = ko.observable(null);
        application: KnockoutObservable<Application> = ko.observable(new Application(0));
        approvalReason: KnockoutObservable<string> = ko.observable("");
		opPrintContentOfEachApp: PrintContentOfEachAppDto = {
            opPrintContentOfHolidayWork: null,
			opPrintContentOfWorkChange: null,
			opAppStampOutput: null,
			opArrivedLateLeaveEarlyInfo: null,
			opInforGoBackCommonDirectOutput: null,
            opBusinessTripInfoOutput: null,
			opDetailOutput: null,
            opOptionalItemOutput: null,
		};
        childParam: any = {};
		kaf011BViewModel:KnockoutObservable<Kaf011BViewModel> = ko.observable(null);

		displayGoback: KnockoutObservable<boolean> = ko.observable(false);
		enableBack: KnockoutObservable<boolean> = ko.pureComputed(() => {
			const vm = this;
			let index = _.indexOf(vm.listApp(), vm.currentApp())
			if(index > 0) {
				return true;
			} else {
				return false;
			}
		});
		enableNext: KnockoutObservable<boolean> = ko.pureComputed(() => {
			const vm = this;
            let index = _.indexOf(vm.listApp(), vm.currentApp());
			if(index < vm.listApp().length-1) {
				return true;
			} else {
				return false;
			}
		});

        displayApprovalButton: KnockoutObservable<boolean> = ko.observable(true);
        enableApprovalButton: KnockoutObservable<boolean> = ko.observable(true);
        displayApprovalLabel: KnockoutObservable<boolean> = ko.observable(false);

        displayDenyButton: KnockoutObservable<boolean> = ko.observable(true);
        enableDenyButton: KnockoutObservable<boolean> = ko.observable(true);
        displayDenyLabel: KnockoutObservable<boolean> = ko.observable(false);

        displayReleaseButton: KnockoutObservable<boolean> = ko.observable(true);
        enableReleaseButton: KnockoutObservable<boolean> = ko.observable(true);

        displayRemandButton: KnockoutObservable<boolean> = ko.observable(true);
        enableRemandButton: KnockoutObservable<boolean> = ko.observable(true);

        displayUpdateButton: KnockoutObservable<boolean> = ko.observable(true);
        enableUpdateButton: KnockoutObservable<boolean> = ko.observable(true);

        displayDeleteButton: KnockoutObservable<boolean> = ko.observable(true);
        enableDeleteButton: KnockoutObservable<boolean> = ko.observable(true);

        displayCancelButton: KnockoutObservable<boolean> = ko.observable(true);
        enableCancelButton: KnockoutObservable<boolean> = ko.observable(true);
		displayCancelLabel: KnockoutObservable<boolean> = ko.observable(false);

        errorEmpty: KnockoutObservable<boolean> = ko.observable(true);

        childUpdateEvent: () => any;
		childReloadEvent: () => any;

		appNameList: any = null;

        created(params: any) {
            const vm = this;
			character.restore("AppListExtractCondition").then((obj: any) => {
                if (nts.uk.util.isNullOrUndefined(obj)) {
                    vm.displayGoback(false);
                } else {
                    vm.displayGoback(true);
                }
            });
            vm.listApp(__viewContext.transferred.value.listAppMeta);
            vm.currentApp(__viewContext.transferred.value.currentApp);
            vm.appType = ko.observable(null);
            vm.childParam = {
				appType: vm.appType,
            	application: vm.application,
				printContentOfEachAppDto: vm.opPrintContentOfEachApp,
                approvalReason: vm.approvalReason,
                appDispInfoStartupOutput: vm.appDispInfoStartupOutput,
                eventUpdate: function(a: any) { vm.getChildUpdateEvent.apply(vm, [a]) },
				eventReload: function(a: any) { vm.getChildReloadEvent.apply(vm, [a]) },
            }
			
			vm.$blockui("show");
			vm.$ajax(API.getAppNameInAppList).then((data) => {
				vm.appNameList = data;
				vm.loadData();
			});
        }

        loadData() {
            const vm = this;
            vm.$blockui("show");
            vm.$ajax(`${API.getDetailPC}/${vm.currentApp()}`).done((successData: any) => {
            	vm.approvalReason("");
                vm.appType(successData.appDetailScreenInfo.application.appType);
				vm.application().appType = vm.appType();
                vm.application().appID(successData.appDetailScreenInfo.application.appID);
		        vm.application().opAppReason(successData.appDetailScreenInfo.application.opAppReason);
		        vm.application().opAppStandardReasonCD(successData.appDetailScreenInfo.application.opAppStandardReasonCD);
		        vm.application().opReversionReason(successData.appDetailScreenInfo.application.opReversionReason);
		        vm.application().opStampRequestMode(successData.appDetailScreenInfo.application.opStampRequestMode);
                vm.appDispInfoStartupOutput(successData);
				if(vm.childParam.appType() == AppType.COMPLEMENT_LEAVE_APPLICATION){
					vm.kaf011BViewModel(new Kaf011BViewModel(vm.childParam));
				}else{
					vm.kaf011BViewModel(null);
				}
                let viewContext: any = __viewContext,
                    loginID = viewContext.user.employeeId,
                    loginFlg = successData.appDetailScreenInfo.application.enteredPerson == loginID || successData.appDetailScreenInfo.application.employeeID == loginID,
					opString = "B",
					appNameInfo = _.find(vm.appNameList, (o: any) => {
						let condition = o.appType==vm.appType();
						if(vm.appType() == 7) {
							if(vm.application().opStampRequestMode()==0) {
								condition = condition && o.opApplicationTypeDisplay==3;
								opString = "C";
							} else {
								condition = condition && o.opApplicationTypeDisplay==4;
								opString = "D";
							}
						}
						if(vm.appType() == 0) {
							return false;
						}
						return condition;
					});
				if(appNameInfo) {
					$('#pg-disp-name').text(appNameInfo.appName);
					$('#pg-id').text(appNameInfo.opProgramID + opString);
					if($('#pg-id').length == 0) {
						let $el = $("#pg-disp-name");
			            let pgid = "<span id='pg-id'>" + appNameInfo.opProgramID + opString + "</span>";
			            let pgidcaret = "<div id='pg-id-caret'></div>"
			            $("body").append(pgid);
			            $("body").append(pgidcaret);
			
			            $el.mouseenter((e) => {
			                let top = $el.offset().top + 23;
			                let left = $el.offset().left + 5;
			                $("#pg-id").css({"visibility": "visible", "top": top + "px", "left" : left + "px", "z-index" : "1000"});
			                $("#pg-id-caret").css({"visibility": "visible", "top": top + "px", "left" : left + "px", "z-index" : "1000"});
			            });
			            $el.mouseleave((e) => {
			                $("#pg-id").css({"visibility": "hidden", "top": "0px", "left" : "0px", "z-index" : "-1"});
			                $("#pg-id-caret").css({"visibility": "hidden", "top": "0px", "left" : "0px", "z-index" : "-1"});
			            });
					}
				} else {
					$('#pg-disp-name').text("");
				}
                vm.setControlButton(
                    successData.appDetailScreenInfo.user,
                    successData.appDetailScreenInfo.approvalATR,
                    successData.appDetailScreenInfo.reflectPlanState,
                    successData.appDetailScreenInfo.authorizableFlags,
                    successData.appDetailScreenInfo.alternateExpiration,
                    loginFlg,
					successData.appDetailScreenInfo.pastApp);
				if(_.isFunction(vm.childReloadEvent)) {
	                vm.childReloadEvent();
	            }
            }).fail((res: any) => {
                vm.handlerExecuteErrorMsg(res);
            });
        }

		getAppType(key: string) {
			return _.get(AppType, '[' + key + ']');
		}

        setControlButton(
            userTypeValue: any, // phân loại người dùng
            approvalAtrValue: any, // trạng thái approval của Phase
            state: any, // trạng thái đơn
            canApprove: any,  // có thể bấm nút approval không true, false
            expired: any, // phân biệt thời hạn
            loginFlg: any, // login có phải người viết đơn/ người xin hay k
			pastApp?: boolean
        ) {
            const vm = this;
            vm.displayApprovalButton((userTypeValue == UserType.APPLICANT_APPROVER || userTypeValue == UserType.APPROVER)
                && (approvalAtrValue != ApprovalAtr.APPROVED));
            vm.enableApprovalButton(!pastApp && ((state == Status.DENIAL || state == Status.NOTREFLECTED || state == Status.REMAND)
                && canApprove
                && !expired));

            vm.displayDenyButton((userTypeValue == UserType.APPLICANT_APPROVER || userTypeValue == UserType.APPROVER)
                && (approvalAtrValue != ApprovalAtr.DENIAL));
            vm.enableDenyButton(!pastApp &&
				((state == Status.DENIAL || state == Status.WAITREFLECTION || state == Status.NOTREFLECTED || state == Status.REMAND)
	                && canApprove
	                && !expired));

            vm.displayRemandButton((userTypeValue == UserType.APPLICANT_APPROVER || userTypeValue == UserType.APPROVER));
            vm.enableRemandButton(
					!pastApp &&
				(
                ((state == Status.DENIAL || state == Status.NOTREFLECTED || state == Status.REMAND)
                    && canApprove
                    && !expired) ||
                (state == Status.WAITREFLECTION
                    && canApprove
                    && (approvalAtrValue == ApprovalAtr.APPROVED || approvalAtrValue == ApprovalAtr.DENIAL)
                    && !expired))
            );

            vm.displayReleaseButton((userTypeValue == UserType.APPLICANT_APPROVER || userTypeValue == UserType.APPROVER));
            vm.enableReleaseButton(
				!pastApp &&
				((state == Status.DENIAL || state == Status.WAITREFLECTION || state == Status.NOTREFLECTED || state == Status.REMAND)
                && canApprove
                && (approvalAtrValue == ApprovalAtr.APPROVED || approvalAtrValue == ApprovalAtr.DENIAL)
			));

            vm.displayUpdateButton((userTypeValue == UserType.APPLICANT_APPROVER || userTypeValue == UserType.APPLICANT || userTypeValue == UserType.OTHER));
            vm.enableUpdateButton(
				!pastApp &&
				(state == Status.NOTREFLECTED || state == Status.REMAND)
				);

            vm.displayDeleteButton((userTypeValue == UserType.APPLICANT_APPROVER || userTypeValue == UserType.APPLICANT || userTypeValue == UserType.OTHER));
            vm.enableDeleteButton(
				!pastApp &&
				(state == Status.NOTREFLECTED || state == Status.REMAND)
			);

			if(state == Status.CANCELED) {
				vm.displayCancelButton(false);
				vm.displayCancelLabel(true);	
			} else {
				vm.displayCancelButton(true);
				vm.displayCancelLabel(false);
			}
			if(pastApp) {
				vm.enableCancelButton(false);	
			} else {
				vm.enableCancelButton((userTypeValue == UserType.APPLICANT_APPROVER || userTypeValue == UserType.APPLICANT) && state != Status.CANCELED);
			}

            vm.displayApprovalLabel((userTypeValue == UserType.APPLICANT_APPROVER || userTypeValue == UserType.APPROVER)
                && (approvalAtrValue == ApprovalAtr.APPROVED));

            vm.displayDenyLabel((userTypeValue == UserType.APPLICANT_APPROVER || userTypeValue == UserType.APPROVER)
                && (approvalAtrValue == ApprovalAtr.DENIAL));
        }

        back() {
            const vm = this;
            let index = _.indexOf(vm.listApp(), vm.currentApp());
            if (index > 0) {
                vm.currentApp(vm.listApp()[index - 1]);
				vm.$errors("clear").then(() => {
					vm.loadData();
				});
            }
        }

        next() {
            const vm = this;
            let index = _.indexOf(vm.listApp(), vm.currentApp());
			if (index < (vm.listApp().length-1)) {
                vm.currentApp(vm.listApp()[index + 1]);
				vm.$errors("clear").then(() => {
					vm.loadData();
				});
            }
        }

        btnApprove() {
			const vm = this;
            vm.$blockui("show");
            let memo = vm.approvalReason(),
            	appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput()),
            	command = { memo, appDispInfoStartupOutput };

            vm.$ajax(API.approve, command)
            .done((successData: any) => {
                vm.$dialog.info({ messageId: "Msg_220" }).then(() => {
					CommonProcess.handleMailResult(successData, vm).then(() => {
						let param = successData.reflectAppIdLst;
	                	nts.uk.request.ajax("at", API.reflectApp, param);
	                    vm.loadData();
					});
                });
            }).fail((res: any) => {
                vm.handlerExecuteErrorMsg(res);
            }).always(() => vm.$blockui("hide"));
        }

		btnApproveComment() {
			const vm = this;
			let appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput()),
				dParam = { appDispInfoStartupOutput };
			vm.$window.modal('/view/kaf/000/e/index.xhtml', dParam).then((result) => {
				if(result) {
					if(result.reload) {
						vm.loadData();	
					}
				}
			});
        }

        btnDenyComment() {
			const vm = this;
			let appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput()),
				dParam = { appDispInfoStartupOutput };
			vm.$window.modal('/view/kaf/000/f/index.xhtml', dParam).then((result) => {
				if(result) {
					if(result.reload) {
						vm.loadData();	
					}	
				}
			});
		}

        btnRelease() {
			const vm = this;
            vm.$blockui("show");
            vm.$dialog.confirm({ messageId: "Msg_248" }).then((result: 'no' | 'yes' | 'cancel') => {
                if (result === 'yes') {
                    return vm.$ajax(API.release, ko.toJS(vm.appDispInfoStartupOutput()));
                }
            }).done((successData: any) => {
				if(successData) {
					if(successData.processDone) {
	                    vm.$dialog.info({ messageId: "Msg_221" }).then(() => {
							CommonProcess.handleMailResult(successData, vm).then(() => {
			                    vm.loadData();
							});
	                    });
	                }
				}
            }).fail((res: any) => {
                vm.handlerExecuteErrorMsg(res);
            }).always(() => vm.$blockui("hide"));
        }

        btnRemand() {
			const vm = this;
			let appID = [vm.application().appID()],
				version = vm.appDispInfoStartupOutput().appDetailScreenInfo.application.version;
			if(vm.appType()==AppType.COMPLEMENT_LEAVE_APPLICATION) {
				appID = [];
				if(vm.childParam.printContentOfEachAppDto.optHolidayShipment.abs) {
					appID.push(vm.childParam.printContentOfEachAppDto.optHolidayShipment.abs.application.appID);
				}
				if(vm.childParam.printContentOfEachAppDto.optHolidayShipment.rec) {
					appID.push(vm.childParam.printContentOfEachAppDto.optHolidayShipment.rec.application.appID);
				}
			}
			let command = { appID, version };
			vm.$window.storage('KDL034_PARAM', command);
			vm.$window.modal('/view/kdl/034/a/index.xhtml').then(() => {
				vm.loadData();
			});
        }

        updateData() {
            const vm = this;

            // nếu component con có bind event ra
            if(_.isFunction(vm.childUpdateEvent)) {
                vm.childUpdateEvent().then((result: any) => {
					if(result) {
						vm.loadData();
					} else {
						vm.$blockui('hide');
					}
				}).fail((res: any) => {
					if(res) {
						let a = [
							AppType.OVER_TIME_APPLICATION, // 残業申請
				            AppType.ABSENCE_APPLICATION, // 休暇申請
				            AppType.HOLIDAY_WORK_APPLICATION, // 休出時間申請
				            AppType.ANNUAL_HOLIDAY_APPLICATION, // 時間休暇申請
				            AppType.COMPLEMENT_LEAVE_APPLICATION, // 振休振出申請
				            AppType.OPTIONAL_ITEM_APPLICATION, // 任意項目申請
						];
						if(_.includes(a, vm.appType()) || vm.currentApp()=="sample") {
							vm.handlerExecuteErrorMsg(res);
						}
					}
					vm.$blockui('hide')
				});
            }
        }

        // Hàm phục vụ việc gọi lại trong viewmodel của component con
        // và cập nhật update function của component con ra ngoài
        getChildUpdateEvent(evt: () => void) {
            const vm = this;
            // gán event update của component vào childUpdateEvent
            vm.childUpdateEvent = evt;
        }

		getChildReloadEvent(evt: () => void) {
            const vm = this;
            // gán event update của component vào childUpdateEvent
            vm.childReloadEvent = evt;
        }

        btnReferences() {

        }

        btnSendEmail() {
			const vm = this;
            let command = {
            	appIDLst: [vm.currentApp()],
				isMultiMode: false,
				appDispInfoStartupOutput: vm.appDispInfoStartupOutput(), 

            };
            nts.uk.ui.windows.setShared("KDL030_PARAM", command);
            nts.uk.ui.windows.sub.modal("/view/kdl/030/a/index.xhtml");
        }

        btnDelete() {
            const vm = this;
            vm.$blockui("show");
            vm.$dialog.confirm({ messageId: "Msg_18" }).then((result: 'no' | 'yes' | 'cancel') => {
                if (result === 'yes') {
					let hdsubRecLinkData: any = null
					if(vm.childParam.appType() == AppType.COMPLEMENT_LEAVE_APPLICATION && vm.kaf011BViewModel().appCombinaSelected() == 0){
						hdsubRecLinkData = {
							absId: vm.kaf011BViewModel().displayInforWhenStarting().abs.application.appID, 
							recId: vm.kaf011BViewModel().displayInforWhenStarting().rec.application.appID,
							linkApp: vm.kaf011BViewModel().application().appID() == vm.kaf011BViewModel().displayInforWhenStarting().abs.application.appID 
								? vm.kaf011BViewModel().displayInforWhenStarting().rec.application : vm.kaf011BViewModel().displayInforWhenStarting().abs.application
						}
						// hdsubRecLinkData.linkApp.inputDate = moment().format('YYYY/MM/DD HH:mm:ss');
					}	

					let appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput()),
		            	command = { 
							appDispInfoStartupOutput: appDispInfoStartupOutput, 
							hdsubRecLinkData: hdsubRecLinkData
						 };
                    return vm.$ajax(API.deleteapp, command);
                }
            }).done((successData: any) => {
				if(successData) {
					vm.$dialog.info({ messageId: "Msg_16" }).then(() => {
						CommonProcess.handleMailResult(successData, vm).then(() => {
		                    character.restore("AppListExtractCondition").then((obj: any) => {
								let param = 0;
								if(obj.appListAtr==1) {
									param = 1;
								}
								vm.$jump("at", "/view/cmm/045/a/index.xhtml?a="+param);
				            });
						});
	                });
				}
            }).fail((res: any) => {
                vm.handlerExecuteErrorMsg(res);
            }).always(() => vm.$blockui("hide"));

        }

        btnCancel() {
            const vm = this;
            vm.$blockui("show");
            vm.$dialog.confirm({ messageId: "Msg_249" }).then((result: 'no' | 'yes' | 'cancel') => {
                if (result === 'yes') {
                    return vm.$ajax(API.cancel, ko.toJS(vm.appDispInfoStartupOutput()));
                }
            }).done((successData: any) => {
				if(successData) {
					vm.$dialog.info({ messageId: "Msg_224" }).then(() => {
	                    vm.loadData();
	                });
				} 
				// else {
				// 	vm.$dialog.info({ messageId: "Msg_2188" }).then(() => {
	            //         vm.loadData();
	            //     });
				// }
            }).fail((res: any) => {
                vm.handlerExecuteErrorMsg(res);
            }).always(() => vm.$blockui("hide"));
        }

        handlerExecuteErrorMsg(res: any) {
            const vm = this;
            switch(res.messageId) {
            case "Msg_426":
                vm.$dialog.error({ messageId: "Msg_426" }).then(() => {
					character.restore("AppListExtractCondition").then((obj: any) => {
						let param = 0;
						if(obj.appListAtr==1) {
							param = 1;
						}
						vm.$jump("at", "/view/cmm/045/a/index.xhtml?a="+param);
		            });
                });
                break;
            case "Msg_197":
                vm.$dialog.error({ messageId: "Msg_197" }).then(() => {
                    vm.loadData();
                });
                break;
            case "Msg_198":
                vm.$dialog.error({ messageId: "Msg_198" }).then(() => {
					character.restore("AppListExtractCondition").then((obj: any) => {
						let param = 0;
						if(obj.appListAtr==1) {
							param = 1;
						}
						vm.$jump("at", "/view/cmm/045/a/index.xhtml?a="+param);
		            });
                });
                break;
            case "Msg_1692":
            case "Msg_1693":
                break;
			case "Msg_1691":
			case 'Msg_235':
			case 'Msg_391':
			case 'Msg_1518':
			case 'Msg_236':
			case 'Msg_324':
			case 'Msg_237':
			case 'Msg_238':
			case 'Msg_327':
			case 'Msg_328':
			case 'Msg_1530':
			case 'Msg_448':
			case 'Msg_449':
			case 'Msg_450':
			case 'Msg_451':
			case 'Msg_768':
			case 'Msg_1715':
			case 'Msg_1521':
			case 'Msg_1648':
            case 'Msg_430':
            case 'Msg_1687':
            case 'Msg_1409':
            case 'Msg_511':
            case 'Msg_476':
            case 'Msg_477':
            case 'Msg_478':
            case 'Msg_1686':
            case 'Msg_1706':
            case 'Msg_1983':
			case "Msg_1654":
				vm.$dialog.error({ messageId: res.messageId, messageParams: res.parameterIds }).then(() => {
					vm.$blockui("hide");
				});
				break;
            default:
                vm.$dialog.error(res.message).then(() => {
                    vm.$jump("com", "/view/ccg/008/a/index.xhtml");
                });
                break;
            }
        }

        btnPrint() {
            const vm = this;
            vm.$blockui("show");
            let appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput()),
				opPrintContentOfEachApp = vm.opPrintContentOfEachApp,
				appNameList = vm.appNameList,
                command = { appDispInfoStartupOutput, opPrintContentOfEachApp, appNameList };
            nts.uk.request.exportFile("at", API.print, command)
            .done((successData: any) => {

            }).fail((res: any) => {
                vm.handlerExecuteErrorMsg(res);
            }).always(() => vm.$blockui("hide"));
        }

		backtoCMM045() {
			const vm = this;
			character.restore("AppListExtractCondition").then((obj: any) => {
				let param = 0;
				if(obj.appListAtr==1) {
					param = 1;
				}
				vm.$jump("at", "/view/cmm/045/a/index.xhtml");
            });
		}

		sendMailAfterUpdate() {
			const vm = this;
			return vm.$ajax(API.sendMailAfterUpdate)
			.then((data: any) => {
				if(data) {
					if(data.isAutoSendMail) {
						let mailResult = [];
						mailResult.push({ value: data.autoSuccessMail, type: 'info' });
						mailResult.push({ value: data.autoFailMail, type: 'error' });
						mailResult.push({ value: data.autoFailServer, type: 'error' });
						CommonProcess.showMailResult(_.slice(mailResult, 1), vm).then(() => vm.loadData());
					}
				}
			});
		}

		getAppNameForAppOverTime(overtimeAtr: number) {
			const vm = this;
			let appNameInfo = _.find(vm.appNameList, (o: any) => vm.appType() == 0 && o.opApplicationTypeDisplay==overtimeAtr);
			if(appNameInfo) {
				$('#pg-disp-name').text(appNameInfo.appName);
				$('#pg-id').text(appNameInfo.opProgramID + "B");
				if($('#pg-id').length == 0) {
					let $el = $("#pg-disp-name");
		            let pgid = "<span id='pg-id'>" + appNameInfo.opProgramID + "B" + "</span>";
		            let pgidcaret = "<div id='pg-id-caret'></div>"
		            $("body").append(pgid);
		            $("body").append(pgidcaret);
		
		            $el.mouseenter((e) => {
		                let top = $el.offset().top + 23;
		                let left = $el.offset().left + 5;
		                $("#pg-id").css({"visibility": "visible", "top": top + "px", "left" : left + "px", "z-index" : "1000"});
		                $("#pg-id-caret").css({"visibility": "visible", "top": top + "px", "left" : left + "px", "z-index" : "1000"});
		            });
		            $el.mouseleave((e) => {
		                $("#pg-id").css({"visibility": "hidden", "top": "0px", "left" : "0px", "z-index" : "-1"});
		                $("#pg-id-caret").css({"visibility": "hidden", "top": "0px", "left" : "0px", "z-index" : "-1"});
		            });
				}
			} else {
				$('#pg-disp-name').text("");
			}
		}
    }

    const API = {
        getDetailPC: "at/request/application/getDetailPC",
        deleteapp: "at/request/application/deleteapp",
        approve: "at/request/application/approveapp",
    	deny: "at/request/application/denyapp",
        release: "at/request/application/releaseapp",
        cancel: "at/request/application/cancelapp",
        print: "at/request/application/print",
		getAppNameInAppList: "at/request/application/screen/applist/getAppNameInAppList",
		sendMailAfterUpdate: "",
		reflectApp: "at/request/application/reflect-app"
    }
}