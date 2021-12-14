/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
/// <reference path="../../../kdp/003/f/kdp003.f.vm.ts" />

module nts.uk.at.view.kdp005.a {

	export module viewmodel {

		import modal = nts.uk.ui.windows.sub.modal;
		import setShared = nts.uk.ui.windows.setShared;
		import getShared = nts.uk.ui.windows.getShared;
		import block = nts.uk.ui.block;
		import dialog = nts.uk.ui.dialog;
		import jump = nts.uk.request.jump;
		import getText = nts.uk.resource.getText;
		import getMessage = nts.uk.resource.getMessage;
		import characteristics = nts.uk.characteristics;
		import FingerStampSetting = nts.uk.at.kdp003.a.FingerStampSetting;
		import IMessage = nts.uk.at.view.kdp004.a.IMessage;
		import StorageData = nts.uk.at.view.kdp004.a.StorageData;
		import checkType = nts.uk.at.view.kdp.share.checkType;

		const DIALOG = {
			R: '/view/kdp/003/r/index.xhtml',
			F: '/view/kdp/003/f/index.xhtml',
			M: '/view/kdp/003/m/index.xhtml',
			P: '/view/kdp/003/p/index.xhtml'
		};

		const KDP005_SAVE_DATA = 'loginKDP005';
		const WORKPLACES_STORAGE = 'WORKPLACES_STORAGE';

		const API = {
			NOTICE: 'at/record/stamp/notice/getStampInputSetting',
			GET_LOCATION: 'at/record/stamp/employment_system/get_location_stamp_input',
			SETTING_STAMP_COMMON: 'at/record/stamp/settings_stamp_common'
		};

		export class ScreenModel {
			saveSuccess = false;
			stampSetting: KnockoutObservable<StampSetting> = ko.observable({});
			stampTab: KnockoutObservable<StampTab> = ko.observable(new StampTab());
			stampToSuppress: KnockoutObservable<StampToSuppress> = ko.observable({
				departure: false,
				goOut: false,
				goingToWork: false,
				turnBack: false
			});
			stampResultDisplay: KnockoutObservable<IStampResultDisplay> = ko.observable({});
			serverTime: KnockoutObservable<any> = ko.observable('');
			isUsed: KnockoutObservable<boolean> = ko.observable(false);
			errorMessage: KnockoutObservable<string> = ko.observable('');
			loginInfo: any = null;
			retry: number = 0;
			listCompany = [];
			btnHistory: KnockoutObservable<boolean> = ko.observable(false);
			btnChangeCompany: KnockoutObservable<boolean> = ko.observable(false);
			messageNoti: KnockoutObservable<IMessage> = ko.observable();
			fingerStampSetting: KnockoutObservable<FingerStampSetting> = ko.observable(DEFAULT_SETTING);

			//basyo mode
			modeBasyo: KnockoutObservable<boolean> = ko.observable(false);

			// workplace get in basyo;
			workplace: string[] | [] = [];

			// get from basyo;
			worklocationCode: null | string = null;
			workPlaceId: string = null;
			workPlaceInfos: IWorkPlaceInfo[] = [];
			supportUse: KnockoutObservable<boolean> = ko.observable(false);
			pageComment: KnockoutObservable<string> = ko.observable('');
			commentColor: KnockoutObservable<string> = ko.observable('');

			constructor() {
				let self = this;
				self.isUsed.subscribe((value) => {
					let cid = __viewContext.user.companyId;
					if (value && _.find(self.listCompany, ['companyId', cid])) {
						self.btnHistory(true);
					} else {
						self.btnHistory(false);
					}
				});
			}

			public startPage(): JQueryPromise<void> {
				let self = this;
				let dfd = $.Deferred<void>();
				const vm = new ko.ViewModel();

				self.getWorkPlacesInfo();
				self.basyo().done(() => {
					vm.$window.storage("contractInfo")
						.then((data: any) => {
							if (data) {
								service.getLogginSetting(data.contractCode).done((res) => {
									self.listCompany = _.filter(res, 'icCardStamp');
									if (self.listCompany.length == 0) {
										self.errorMessage(getMessage("Msg_1527"));
										self.isUsed(false);
										dfd.resolve();
									} else {
										self.btnChangeCompany(self.listCompany.length > 0);
										characteristics.restore("loginKDP005").done(function (loginInfo: ILoginInfo) {
											if (loginInfo) {
												self.loginInfo = loginInfo;

												if (ko.unwrap(self.modeBasyo)) {
													self.loginInfo.selectedWP = self.workplace;
													nts.uk.characteristics.save(KDP005_SAVE_DATA, self.loginInfo);
												}

												if (__viewContext.user.companyId != loginInfo.companyId || __viewContext.user.employeeCode != loginInfo.employeeCode) {
													self.login(self.loginInfo).done(() => {
														location.reload();
													}).fail(() => {
														dfd.resolve();
													});
												} else {
													self.login(self.loginInfo).done(() => {
														$.when(self.doFirstLoad(), self.loadNotice(self.loginInfo)).done(() => {
															dfd.resolve();
														});
													}).fail(() => {
														dfd.resolve();
													});
												}
											} else {
												self.setLoginInfo().done((loginResult) => {
													if (!loginResult) {
														self.isUsed(false);
														dfd.resolve();
													} else {
														$.when(self.doFirstLoad(), self.loadNotice(self.loginInfo)).done(() => {
															dfd.resolve();
														});
													}
												});
											}
										});
									}
								});
							} else {
								self.openDialogF({
									mode: 'admin'
								})
								dfd.resolve();
							}
						});
				});
				return dfd.promise();
			}

			alwaysLoadMessage(param: number) {
				if (param > 0) {
					setInterval(() => {
						this.loadNotice();
					}, param * 60000);
				}
			}

			getErrorNotUsed(errorType) {
				const notUseMessage = [
					{ text: "Msg_1644", value: 1 },
					{ text: "Msg_1645", value: 2 },
					{ text: "Msg_1619", value: 3 }
				]
				let item = _.find(notUseMessage, ['value', errorType]);
				return item ? getMessage(item.text, [getText('KDP002_4')]) : '';
			}

			doFirstLoad(): JQueryPromise<any> {
				let dfd = $.Deferred<any>(), self = this;
				let loginInfo = self.loginInfo;
				block.grayout();
				service.confirmUseOfStampInput({ employeeId: null, stampMeans: 2 }).done((res) => {
					self.isUsed(res.used == 0);
					if (self.isUsed()) {
						block.grayout();
						service.startPage().done((res: any) => {
							if (!res.stampSetting || !res.stampResultDisplay || res.stampSetting.pageLayouts.length == 0) {
								self.errorMessage(self.getErrorNotUsed(1));
								self.isUsed(false);
								dfd.resolve();
								return;
							}

							self.alwaysLoadMessage(res.stampSetting.correctionInterval);
							self.stampSetting(res.stampSetting);
							self.stampTab().bindData(res.stampSetting.pageLayouts);
							self.stampResultDisplay(res.stampResultDisplay);
							self.fingerStampSetting(res);
							dfd.resolve();
						}).fail((res) => {
							dialog.alertError({ messageId: res.messageId }).then(() => {
								jump("com", "/view/ccg/008/a/index.xhtml");
							});
						}).always(() => {
							dfd.resolve();
							block.clear();
						});
						// self.getStampToSuppress();
					} else {
						self.isUsed(false);
						self.errorMessage(self.getErrorNotUsed(res.used));
						dfd.resolve();
					}
				}).always(() => {
					block.clear();
				});
				return dfd.promise();
			}

			public setLoginInfo(): JQueryPromise<any> {
				let dfd = $.Deferred<any>(), self = this, vm = new ko.ViewModel();
				self.openDialogF({
					mode: 'admin'
				}).done((loginResult) => {
					if (!loginResult) {
						self.errorMessage(getMessage("Msg_1647"));
						dfd.resolve();
					} else {
						self.basyo()
							.then(() => {
								if (!ko.unwrap(self.modeBasyo)) {
									self.openDialogK().done((result) => {
										if (!result) {
											if (__viewContext.user.companyId != loginResult.em.companyId || __viewContext.user.employeeCode != loginResult.em.employeeCode) {
												location.reload();
												dfd.resolve();
											} else {
												self.stampSetting({});
												self.errorMessage(getMessage("Msg_1647"));
												dfd.resolve();
											}
										} else {
											self.loginInfo = loginResult.em;
											self.loginInfo.selectedWP = result;
											self.saveSuccess = true;
											characteristics.save("loginKDP005", self.loginInfo).done(() => {
												if (__viewContext.user.companyId != loginResult.em.companyId || __viewContext.user.employeeCode != loginResult.em.employeeCode) {
													location.reload();
													dfd.resolve();
												} else {
													dfd.resolve(self.loginInfo);
												}
											});
										}
									});
								} else {
									self.loginInfo = loginResult.em;
									self.loginInfo.selectedWP = self.workplace;
									nts.uk.characteristics.save(KDP005_SAVE_DATA, self.loginInfo).done(() => {
										location.reload();
									});
								}
							});

					}
				}).always(() => {
					vm.$blockui('clear');
				});
				return dfd.promise();
			}



			public openDialogF(param): JQueryPromise<any> {
				let vm = new ko.ViewModel();
				let dfd = $.Deferred<any>();
				vm.$window.modal('at', '/view/kdp/003/f/index.xhtml', param).then(function (loginResult): any {
					if (loginResult && loginResult.em) {
						dfd.resolve(loginResult);
					} else {
						dfd.resolve();
					}
				});
				return dfd.promise();
			}

			public openDialogK(): JQueryPromise<any> {
				let vm = new ko.ViewModel();
				let dfd = $.Deferred<any>();
				let self = this;

				if (ko.unwrap(self.modeBasyo)) {
					dfd.resolve(self.workPlaceId);
				} else {
					vm.$window.modal('at', '/view/kdp/003/k/index.xhtml', { multiSelect: true }).then((selectedWP) => {
						if (selectedWP) {
							dfd.resolve(selectedWP.selectedId);
						}
						dfd.resolve(selectedWP);
					});
				}
				return dfd.promise();
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

			public getStampToSuppress() {
				let vm = this;
				block.invisible();
				service.getStampToSuppress().done((data: IStampToSuppress) => {
					block.clear();
					data.isUse = vm.stampSetting() ? vm.stampSetting().buttonEmphasisArt : false;
					vm.stampToSuppress(data);
					vm.stampToSuppress.valueHasMutated();
				});
			}

			public clickBtn1(btn: any, layout: any) {
				const vm = this;
				vm.getWorkPlacesInfo();

				modal('/view/kdp/005/h/index.xhtml').onClosed(function (): any {
					let ICCard = getShared('ICCard');
					if (ICCard && ICCard != '') {
						block.grayout();
						vm.getEmployeeIdByICCard(ICCard).done((employeeId: string) => {
							vm.authentic(employeeId).done(() => {
								vm.registerData(btn, layout, ICCard, employeeId);
							}).fail((errorMessage: string) => {
								setShared("errorMessage", errorMessage);
								vm.openIDialog();
							});
						}).fail(() => {
							vm.openIDialog();
						}).always(() => {
							block.clear();
						});
					}
				});
			}

			public openIDialog() {
				let self = this;
				setShared("resultDisplayTime", self.stampSetting().resultDisplayTime);
				modal('/view/kdp/005/i/index.xhtml');
			}

			public getEmployeeIdByICCard(cardNumber: string): JQueryPromise<any> {
				let self = this;
				let dfd = $.Deferred<any>();
				service.getEmployeeIdByICCard({ cardNumber: cardNumber }).done((data) => {
					if (data.employeeId) {
						dfd.resolve(data.employeeId);
					} else {
						dfd.reject();
					}
				});

				return dfd.promise();
			}

			//<<ScreenQuery>> 打刻入力社員の認証のみを行う
			public authentic(employeeId: string): JQueryPromise<any> {
				let self = this;
				let dfd = $.Deferred<any>();
				let param = {
					companyId: self.loginInfo.companyId,
					employeeCode: null,
					employeeId: employeeId,
					password: null,
					passwordInvalid: true,
					isAdminMode: false,
					runtimeEnvironmentCreat: false
				};
				service.authenticateOnlyStamped(param).done((res) => {
					if (res.result) {
						dfd.resolve();
					} else {
						dfd.reject(res.errorMessage);
					}
				}).fail((res) => {
					setShared("errorMessage", getMessage(res.messageId));
					self.openIDialog();
				});
				return dfd.promise();
			}

			playAudio(audioType: number) {
				const url = {
					oha: '../../share/voice/0_oha.mp3',
					otsu: '../../share/voice/1_otsu.mp3'
				}

				let source = '';

				if (audioType === 1) {
					source = url.oha;
				}

				if (audioType === 2) {
					source = url.otsu;
				}
				return source;
			}

			checkHis(self: ScreenModel) {
				let vm = new ko.ViewModel();
				modal('/view/kdp/005/h/index.xhtml').onClosed(function (): any {
					let ICCard = getShared('ICCard');
					if (ICCard && ICCard != '') {
						block.grayout();
						self.getEmployeeIdByICCard(ICCard).done((employeeId: string) => {
							self.authentic(employeeId).done(() => {
								vm.$window.modal('at', '/view/kdp/003/s/index.xhtml', { employeeId: employeeId });
							}).fail((errorMessage: string) => {
								setShared("errorMessage", errorMessage);
								self.openIDialog();
							});
						}).fail(() => {
							self.openIDialog();
						}).always(() => {
							block.clear();
						});
					}
				});
			}

			settingUser(self: ScreenModel) {
				const vm = new ko.ViewModel();
				self.openDialogF({
					mode: 'admin',
					companyId: __viewContext.user.companyId
				}).done((loginResult) => {
					if (loginResult && loginResult.result) {
						self.basyo()
							.then(() => {
								if (!ko.unwrap(self.modeBasyo)) {
									self.openDialogK().done((result) => {
										if (result) {
											self.saveSuccess = true;
											self.loginInfo = loginResult.em;
											self.loginInfo.selectedWP = result;
											characteristics.save("loginKDP005", self.loginInfo).done(() => {
												location.reload();
											});
										} else {
											location.reload();
											// if (__viewContext.user.companyId != loginResult.em.companyId || __viewContext.user.employeeCode != loginResult.em.employeeCode) {
											// 	setTimeout(() => {
											// 		if (self.saveSuccess) {
											// 			location.reload();
											// 		}
											// 	}, 500);
											// }
											// if (self.loginInfo) {
											// 	self.login(self.loginInfo).done(() => {
											// 		if (__viewContext.user.companyId != self.loginInfo.companyId || __viewContext.user.employeeCode != self.loginInfo.employeeCode) {
											// 			setTimeout(() => {
											// 				if (!self.saveSuccess) {

											// 					location.reload();
											// 				}
											// 			}, 500);
											// 		}
											// 	});
											// } else {
											// 	if (__viewContext.user.companyId != loginResult.em.companyId || __viewContext.user.employeeCode != loginResult.em.employeeCode) {
											// 		setTimeout(() => {
											// 			if (!self.saveSuccess) {
											// 				location.reload();
											// 			}
											// 		}, 500);
											// 	}
											// }
										}
									});
								} else {
									self.loginInfo = loginResult.em;
									self.loginInfo.selectedWP = self.workplace;
									characteristics.save("loginKDP005", self.loginInfo).done(() => {
										location.reload();
									});
								}
							})
					} else {
						let dfd = $.Deferred<void>();

						vm.$window.storage("contractInfo")
							.then((data: any) => {
								if (data) {
									service.getLogginSetting(data.contractCode).done((res) => {
										self.listCompany = _.filter(res, 'icCardStamp');
										if (self.listCompany.length == 0) {
											self.errorMessage(getMessage("Msg_1527"));
											self.isUsed(false);
											location.reload();
										}
									});
								}
							});
					}
				});
			}

			public login(loginInfo: any): JQueryPromise<any> {
				let self = this;
				let dfd = $.Deferred<any>();
				let isAdmin = true;
				block.grayout();
				loginInfo.isAdminMode = true;
				loginInfo.passwordInvalid = false;
				loginInfo.runtimeEnvironmentCreate = true;
				service.login(isAdmin, loginInfo).done((res) => {
					dfd.resolve();
				}).fail((res) => {
					self.stampSetting({});
					self.isUsed(false);
					self.errorMessage(getMessage(res.messageId));
					dfd.reject();
				}).always(() => {
					block.clear();
				});
				return dfd.promise();
			}

			public registerData(button, layout, stampedCardNumber, employeeIdRegister) {
				let self = this;
				let vm = new ko.ViewModel();
				block.invisible();
				let registerdata = {
					stampedCardNumber: stampedCardNumber,
					datetime: moment(vm.$date.now()).format('YYYY/MM/DD HH:mm:ss'),
					stampButton: {
						pageNo: layout.pageNo,
						buttonPositionNo: button.btnPositionNo
					},
					refActualResult: {
						cardNumberSupport: null,
						workLocationCD: self.worklocationCode,
						workTimeCode: null,
						overtimeDeclaration: null
					}
				};
				const mode: number = 1;
				const { employeeId, employeeCode } = employeeIdRegister;

				let source = self.playAudio(button.audioType);

				//打刻入力で共通設定を取得する
				vm.$ajax(API.SETTING_STAMP_COMMON)
					.done((data: ISettingsStampCommon) => {
						if (data) {
							self.supportUse(data.supportUse);
						}
					});

				vm.$window.storage(KDP005_SAVE_DATA)
					.then((dataStorage: any) => {

						let btnType = checkType(button.changeClockArt, button.changeCalArt, button.setPreClockArt, button.changeHalfDay, button.btnReservationArt);
						if (dataStorage.selectedWP.length > 1 && button.supportWplset == 1 && self.supportUse() === true && _.includes([14, 15, 16, 17, 18], btnType)) {
							vm.$window.modal('at', DIALOG.M, { screen: 'KDP005', employeeId: employeeId })
								.then((result: string) => {

									if (result) {
										if (result.notification !== null) {

											self.workPlaceId = result;

											let registerdata = {
												stampedCardNumber: stampedCardNumber,
												datetime: moment(vm.$date.now()).format('YYYY/MM/DD HH:mm:ss'),
												stampButton: {
													pageNo: layout.pageNo,
													buttonPositionNo: button.btnPositionNo
												},
												refActualResult: {
													cardNumberSupport: null,
													workPlaceId: self.workPlaceId,
													workLocationCD: self.worklocationCode,
													workTimeCode: null,
													overtimeDeclaration: null
												}
											};
											service.addCheckCard(registerdata).done((res) => {

												//phat nhac
												if (source) {
													let audio = new Audio(source);
													audio.play();
												}

												if (self.stampResultDisplay().notUseAttr == 1 && button.changeClockArt == 1) {
													self.openScreenC(button, layout, employeeIdRegister);
												} else {
													self.openScreenB(button, layout, employeeIdRegister);
												}
											}).fail((res) => {
												dialog.alertError({ messageId: res.messageId });
											}).always(() => {
												//					self.getStampToSuppress();
												block.clear();
											});
										}
									}

								});
						} else {

							if (dataStorage.selectedWP.length = 1) {
								if (self.workPlaceId !== '') {
									self.workPlaceId = dataStorage.selectedWP[0];
								}
							}

							let registerdata = {
								stampedCardNumber: stampedCardNumber,
								datetime: moment(vm.$date.now()).format('YYYY/MM/DD HH:mm:ss'),
								stampButton: {
									pageNo: layout.pageNo,
									buttonPositionNo: button.btnPositionNo
								},
								refActualResult: {
									cardNumberSupport: null,
									workPlaceId: self.workPlaceId,
									workLocationCD: self.worklocationCode,
									workTimeCode: null,
									overtimeDeclaration: null
								}
							};

							service.addCheckCard(registerdata).done((res) => {

								//phat nhac
								if (source) {
									let audio = new Audio(source);
									audio.play();
								}

								if (self.stampResultDisplay().notUseAttr == 1 && button.changeClockArt == 1) {
									self.openScreenC(button, layout, employeeIdRegister);
								} else {
									self.openScreenB(button, layout, employeeIdRegister);
								}
							}).fail((res) => {
								dialog.alertError({ messageId: res.messageId });
							}).always(() => {
								//					self.getStampToSuppress();
								block.clear();
							});
						}
					});



			}

			public openScreenB(button, layout, employeeIdRegister) {
				let self = this;
				let vm = new ko.ViewModel();
				setShared("resultDisplayTime", self.stampSetting().resultDisplayTime);
				setShared("infoEmpToScreenB", {
					employeeId: employeeIdRegister,
					mode: Mode.Personal,
					workPlaceId: self.workPlaceId
				});
				setShared("screenB", {
					screen: "KDP005"
				});
				modal('/view/kdp/002/b/index.xhtml').onClosed(() => {
					self.openKDP002T(button, layout);
				});
			}

			public openScreenC(button, layout, employeeIdRegister) {
				let self = this;
				let vm = new ko.ViewModel();
				setShared('KDP010_2C', self.stampResultDisplay().displayItemId, true);
				setShared("infoEmpToScreenC", {
					employeeId: employeeIdRegister,
					mode: Mode.Personal,
					workPlaceId: self.workPlaceId
				});
				setShared("screenC", {
					screen: "KDP005"
				});
				modal('/view/kdp/002/c/index.xhtml', { screen: "KDP005" }).onClosed(function (): any {
					self.openKDP002T(button, layout);
				});
			}

			public openKDP002T(button: ButtonSetting, layout) {
				let data = {
					pageNo: layout.pageNo,
					buttonDisNo: button.btnPositionNo
				}
				service.getError(data).done((res) => {
					if (res && res.dailyAttdErrorInfos && res.dailyAttdErrorInfos.length > 0) {
						setShared('KDP010_2T', res, true);
						modal('/view/kdp/002/t/index.xhtml').onClosed(function (): any {
							let returnData = getShared('KDP010_T');
							if (!returnData.isClose && returnData.errorDate) {
								let transfer = returnData.btn.transfer;
								jump(returnData.btn.screen, transfer);
							}
						});
					}
				});
			}

			shoNoti() {
				const self = this;
				let vm = new ko.ViewModel();
				const param = { setting: ko.unwrap(self.fingerStampSetting).noticeSetDto, screen: 'KDP005' };
				vm.$window.modal(DIALOG.R, param);
			}

			settingNoti() {
				const self = this;
				let vm = new ko.ViewModel();
				vm.$window.storage(KDP005_SAVE_DATA)
					.then((data: any) => {
						if (data) {
							const mode = 'notification';
							const companyId = (data || {}).companyId;
							vm.$window.modal('at', DIALOG.F, { mode, companyId })
								.then((output: string) => {
									if (output === 'loginSuccess') {
										vm.$window.modal('at', DIALOG.P)
											.then(() => {
												// self.loadNotice(self.loginInfo);
												window.location.reload(false);
											})
									}
								});
						}
					});
			}

			loadNotice(loginInfo?: any): JQueryPromise<any> {
				let vm = new ko.ViewModel();
				const self = this;
				let dfd = $.Deferred<any>();
				let startDate = vm.$date.now();
				// startDate.setDate(startDate.getDate() - 3);
				var wkpIds: string[];

				if (loginInfo) {
					wkpIds = loginInfo.selectedWP;
				} else {
					vm.$window
						.storage('loginKDP005')
						.then((data: any) => {
							if (data.selectedWP.length > 0) {
								wkpIds = data.selectedWP;
							}
						})
						.then(() => {
							if (wkpIds && wkpIds.length > 0) {
								const param = {
									periodDto: {
										startDate: startDate,
										endDate: vm.$date.now()
									},
									wkpIds: wkpIds
								}

								vm.$ajax(API.NOTICE, param)
									.done((data: IMessage) => {
										self.messageNoti(data);
									});
							}
						});
				}



				if (wkpIds && wkpIds.length > 0) {
					const param = {
						periodDto: {
							startDate: startDate,
							endDate: vm.$date.now()
						},
						wkpIds: loginInfo.selectedWP
					}

					vm.$blockui('invisible')
						.then(() => {
							vm.$ajax(API.NOTICE, param)
								.done((data: IMessage) => {
									self.messageNoti(data);
								});
						})
						.always(() => {
							dfd.resolve();
							vm.$blockui('clear');
						});
				}
				return dfd.promise();
			}

			getWorkPlacesInfo() {
				let vm = new ko.ViewModel();
				let self = this;

				vm.$window.storage(WORKPLACES_STORAGE)
					.then((data: IWorkPlaceInfo[]) => {
						self.workPlaceInfos = data
					});

			}

			// URLOption basyo
			basyo(): JQueryPromise<any> {
				let dfd = $.Deferred<any>();

				$.urlParam = function (name) {
					var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
					if (results == null) {
						return null;
					}
					else {
						return decodeURI(results[1]) || 0;
					}
				}

				const self = this,
					vm = new ko.ViewModel(),
					locationCd = $.urlParam('basyo');

				// URLOption basyoが存在している場合
				if (locationCd) {

					vm.$window.storage("contractInfo")
						.then((data: any) => {
							if (data) {
								const param = {
									contractCode: data.contractCode,
									workLocationCode: locationCd
								}
								vm.$ajax(API.GET_LOCATION, param)
									.done((data: IBasyo) => {
										if (data) {
											if (data.workLocationName != null || data.workpalceId != null) {
												self.worklocationCode = locationCd;
												dfd.resolve();
											}

											if (data.workpalceId) {
												if (data.workpalceId.length > 0) {
													self.modeBasyo(true);
													self.workplace = data.workpalceId;
												}

												if (data.workpalceId.length == 0) {
													self.modeBasyo(false);
												}
												dfd.resolve();
											}
										} else {
											dfd.resolve();
										}
									});
							} else {
								dfd.resolve();
							}
						});
				} else {
					dfd.resolve();
				}
				return dfd.promise();
			}
		}
	}

	enum Mode {
		Personal = 1, // 個人
		Shared = 2  // 共有 
	}

	const DEFAULT_SETTING: any = {
		stampSetting: null,
		stampResultDisplay: null
	}

	interface ISettingsStampCommon {
		supportUse: boolean;
		temporaryUse: boolean;
		workUse: boolean;
	}

	interface IBasyo {
		workLocationName: string;
		workpalceId: string[];
	}

	interface IWorkPlaceInfo {
		code: string,
		hierarchyCode: string,
		id: string,
		name: string,
		workplaceDisplayName: string,
		workplaceGeneric: string
	}
}