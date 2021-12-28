/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.kdp003.a {
	import f = nts.uk.at.kdp003.f;
	import k = nts.uk.at.kdp003.k;
	import share = nts.uk.at.view.kdp.share;

	const API = {
		NAME: '/sys/portal/webmenu/username',
		SETTING: '/at/record/stamp/management/personal/startPage',
		HIGHTLIGHT: 'at/record/stamp/management/personal/stamp/getHighlightSetting',
		// LOGIN_ADMIN: 'ctx/sys/gateway/kdp/login/adminmode',
		LOGIN_ADMIN: 'ctx/sys/gateway/login/password' + location.search,
		LOGIN_EMPLOYEE: 'ctx/sys/gateway/kdp/login/employeemode',
		COMPANIES: '/ctx/sys/gateway/kdp/login/getLogginSetting',
		FINGER_STAMP_SETTING: 'at/record/stamp/finger/get-finger-stamp-setting',
		CONFIRM_STAMP_INPUT: '/at/record/stamp/employment/system/confirm-use-of-stamp-input',
		EMPLOYEE_LIST: '/at/record/stamp/employment/in-workplace',
		REGISTER: '/at/record/stamp/employment/system/register-stamp-input',
		NOW: '/server/time/now',
		NOTICE: 'at/record/stamp/notice/getStampInputSetting',
		GET_WORKPLACE_BASYO: 'at/record/stamp/employment_system/get_location_stamp_input',
		confirmUseOfStampInput: 'at/record/stamp/employment_system/confirm_use_of_stamp_input',
		STAMP_SETTING_COMMON: 'at/record/stamp/settings_stamp_common',
		getEmployeeWorkByStamping: 'at/record/stamp/employee_work_by_stamping',
		getIsCloud: "at/record/stamp/finger/get-isCloud",
		getAuthenticate: "at/record/stamp/finger/get-authenticate"

	};

	const DIALOG = {
		F: '/view/kdp/003/f/index.xhtml',
		K: '/view/kdp/003/k/index.xhtml',
		S: '/view/kdp/003/s/index.xhtml',
		R: '/view/kdp/003/r/index.xhtml',
		M: '/view/kdp/003/m/index.xhtml',
		P: '/view/kdp/003/p/index.xhtml',
		KDP002_B: '/view/kdp/002/b/index.xhtml',
		KDP002_C: '/view/kdp/002/c/index.xhtml',
		KDP002_T: '/view/kdp/002/t/index.xhtml',
		KDP002_L: '/view/kdp/002/l/index.xhtml'
	};

	const KDP003_SAVE_DATA = 'loginKDP003';
	const WORKPLACES_STORAGE = 'WORKPLACES_STORAGE';
	const IS_RELOAD_VIEW = 'IS_RELOAD_VIEW_003';

	@bean()
	export class ViewModel extends ko.ViewModel {
		// Message
		// logingin: undefined
		// login false: has messageId
		// login success: null
		message: KnockoutObservable<f.Message | string | null | undefined | false> = ko.observable(undefined);

		// Determined login equal basyo -> OpenView K ?
		modeBasyo: KnockoutObservable<boolean> = ko.observable(false);
		// workplace get in basyo;
		workPlace: string[] | null = null;

		// Notification
		messageNoti: KnockoutObservable<IMessage> = ko.observable();

		workPlaceInfos: IWorkPlaceInfo[] = [];

		worklocationCode: null | string = null;
		workPlaceId: string = null;

		showMessage: KnockoutObservable<boolean | null> = ko.observable(null);

		useWork: KnockoutObservable<boolean> = ko.observable(false);

		// setting for button A3
		showClockButton: {
			setting: KnockoutObservable<boolean>;
			company: KnockoutObservable<boolean>;
		} = {
				setting: ko.observable(true),
				company: ko.observable(true)
			};

		// data option for list employee A2
		employeeData: EmployeeListParam = {
			employees: ko.observableArray([]),
			selectedId: ko.observable(undefined),
			nameSelectArt: ko.observable(false),
			baseDate: ko.observable(null)
		};

		// data option for tabs button A5
		buttonPage: {
			tabs: KnockoutObservableArray<share.PageLayout>;
			stampToSuppress: KnockoutObservable<share.StampToSuppress>;
		} = {
				tabs: ko.observableArray([]),
				stampToSuppress: ko.observable({
					departure: false,
					goingToWork: false,
					goOut: false,
					turnBack: false
				})
			};

		fingerStampSetting: KnockoutObservable<FingerStampSetting> = ko.observable(DEFAULT_SETTING);

		pageComment: KnockoutObservable<string> = ko.observable('');
		commentColor: KnockoutObservable<string> = ko.observable('');
		passContract: String;
		contractCode: String = "000000000000";

		totalOpenViewR: number = 0;

		created() {
			const vm = this;

			// Call step1: クラウド/オンプレの判断を行う
			vm.$ajax("at", API.getIsCloud)
				.then((data: boolean) => {
					// Step2: 契約コードに関するlocalstrageに登録する
					if (!data) {
						vm.$window.storage("contractInfo", {
							contractCode: "000000000000",
							contractPassword: null
						}).then(() => vm.getDataStartScreen());
					} else {
						// Step3: テナント認証する
						vm.$window.storage("contractInfo")
							.then((data: any) => {
								if (!data) {
									// Step4: CCG007_ログイン　A：契約認証を実行する
									vm.openDialogCCG007A()
								} else {
									vm.contractCode = data.contractCode;
									vm.$ajax(API.getAuthenticate, { contactCode: data.contractCode, password: data.contractPassword })
										.then((isSuccess: boolean) => {
											// Step4: CCG007_ログイン　A：契約認証を実行する
											if (!isSuccess) {
												vm.$window.storage(IS_RELOAD_VIEW).then((data: boolean) => {
													if (data || data == null) {
														localStorage.removeItem("nts.uk.characteristics." + KDP003_SAVE_DATA)
														vm.openDialogCCG007A()
													} else {
														vm.getDataStartScreen()
													}
												})
											} else {
												vm.getDataStartScreen();
											}
										});
								}
							});
					}
				})

			ko.computed({
				read: () => {
					const mes = ko.unwrap(vm.message);
					const noti = ko.unwrap(vm.fingerStampSetting).noticeSetDto;

					var result = null;

					if (mes === null) {
						result = false;
					}
					if (noti) {
						if (ko.unwrap(vm.fingerStampSetting).noticeSetDto.displayAtr == 1) {
							result = true;
						} else {
							result = false;
						}
					} else {
						result = false;
					}

					if (mes) {
						result = null;
					}

					vm.showMessage(result);

				}
			})
		}

		mounted() {
			const vm = this;
		}

		openDialogCCG007A() {
			const vm = this;
			nts.uk.ui.windows.sub.modal("com", "/view/ccg/007/a/index.xhtml", {
				height: 320,
				width: 400,
				title: nts.uk.resource.getText("CCG007_9"),
				dialogClass: 'no-close'
			}).onClosed(() => { vm.getDataStartScreen() });
		}

		getDataStartScreen() {
			const vm = this;

			vm.$window.storage(IS_RELOAD_VIEW, true)
			// show or hide stampHistoryButton
			vm.message.subscribe((value) => vm.showClockButton.company(value === null));

			vm.$window.storage('contractInfo')
				.done((data: any) => {
					vm.passContract = _.escape(data ? data.contractPassword : "");
				});

			vm.$ajax('at', API.NOW)
				.then((c) => {
					const date = moment(c, 'YYYY-MM-DDTHH:mm:ss').toDate();

					vm.employeeData.baseDate(date);
				});

			vm.$ajax(API.STAMP_SETTING_COMMON)
				.done((data: any) => {
					vm.useWork(data.workUse);
				})

			// reload employee list after change baseDate
			vm.employeeData.baseDate.subscribe((d: Date) => {
				if (!_.isDate(d)) {
					return;
				}

				vm.$window.storage(KDP003_SAVE_DATA)
					.then((data: undefined | StorageData) => {
						if (data) {
							vm.loadEmployees(data);
						}
					});
			});

			vm.mountedContent();
		}

		reloadView() {
			const vm = new ko.ViewModel();
			vm.$window.storage(IS_RELOAD_VIEW, false).then(() => location.reload())
		}

		// get WorkPlace from basyo -> save locastorage.
		basyo() {
			$.urlParam = function (name) {
				var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
				if (results == null) {
					return null;
				}
				else {
					return decodeURI(results[1]) || 0;
				}
			}

			const vm = this,
				locationCd = $.urlParam('basyo');

			if (locationCd) {
				const param = {
					contractCode: vm.contractCode,
					workLocationCode: locationCd
				}

				vm.$ajax(API.GET_WORKPLACE_BASYO, param)
					.done((data: IBasyo) => {
						if (data) {

							if (data.workLocationName != null || data.workpalceId != null) {
								vm.worklocationCode = locationCd;
							}

							if (data.workpalceId) {
								if (data.workpalceId.length > 0) {
									vm.modeBasyo(true);
									vm.workPlace = data.workpalceId;
								}
							}
						}
					});
			}
		}

		mountedContent() {
			const vm = this;
			const { storage } = vm.$window;
			$(window).trigger('resize');
			var checkUsed: boolean | null;

			vm.$ajax(API.confirmUseOfStampInput, { employeeId: null, stampMeans: 0 })
				.then((data: any) => {

					if (data.used == 2) {
						checkUsed = false;
					} else {
						checkUsed = true;
					}
				});

			return $.Deferred()
				.resolve(true)
				.then(() => storage(KDP003_SAVE_DATA))
				.then((storageData: undefined | StorageData) => {
					if (storageData === undefined) {
						return vm.$window.modal('at', DIALOG.F, { mode: 'admin' })
							.then((loginData: f.TimeStampLoginData) => ({
								loginData
							})) as JQueryPromise<LoginData>;
					}

					// data login by storage
					const {
						PWD,
						SCD,
						CCD
					} = storageData as StorageData;

					const loginParams: any = {
						companyCode: CCD,
						employeeCode: SCD,
						password: PWD,
						contractCode: vm.contractCode,
						contractPassword: vm.passContract
					};

					// auto login by storage data of preview login
					// <<ScreenQuery>> 打刻管理者でログインする

					return vm.$ajax('at', API.COMPANIES, { contractCode: vm.contractCode })
						.then((data: f.CompanyItem[]) => {

							if (!data.length || _.every(data, d => d.selectUseOfName === false)) {
								// note: ログイン失敗(打刻会社一覧が取得できない場合)
								vm.setMessage({ messageId: 'Msg_1527' });

								// UI[F2]  打刻使用可能会社の取得と判断 
								vm.showClockButton.setting(false);

								return {
									storageData
								};
							}

							vm.showClockButton.setting(true);

							return vm.$ajax('com', API.LOGIN_ADMIN, loginParams)
								.then((loginData: f.TimeStampLoginData) => ({
									loginData,
									storageData
								}));
						}) as JQueryPromise<LoginData>;
				})
				.then((data: LoginData) => {
					vm.$ajax(API.confirmUseOfStampInput, { employeeId: null, stampMeans: 0 })
						.then((data: any) => {

							if (data.used == 2) {
								checkUsed = false;
							} else {
								checkUsed = true;
							}
						});

					$.urlParam = function (name) {
						var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);

						if (results == null) {
							return null;
						}
						else {
							return decodeURI(results[1]) || 0;
						}
					}

					const locationCd = $.urlParam('basyo');

					if (locationCd) {
						const param = {
							contractCode: vm.contractCode,
							workLocationCode: locationCd
						}

						return vm.$ajax(API.GET_WORKPLACE_BASYO, param)
							.then((dataBasyo: IBasyo) => {
								if (dataBasyo) {
									if (dataBasyo.workLocationName != null || dataBasyo.workpalceId != null) {
										vm.worklocationCode = locationCd;
									}

									if (dataBasyo.workpalceId) {
										if (dataBasyo.workpalceId.length > 0) {
											vm.modeBasyo(true);
											vm.workPlace = dataBasyo.workpalceId;
										}
									}
								}
							})
							.then(() => data);
					}

					return data;
				})
				.then((data: LoginData) => {

					var exest = false;
					var check1527 = false;

					if (ko.unwrap(vm.message)) {
						if (ko.unwrap(vm.message).messageId === 'Msg_1527') {
							check1527 = true;
						}
					}

					// if dialog f return data (first login)

					//&& !exest
					if (data.loginData && !data.loginData.msgErrorId && !data.loginData.errorMessage && !data.storageData) {
						const { loginData } = data;
						const params = { multiSelect: true };

						if (!ko.unwrap(vm.modeBasyo) && loginData.msgErrorId !== "Msg_1527") {
							return vm.$window
								.modal('at', DIALOG.K, params)
								.then((workplaceData: k.Return) => ({
									loginData,
									workplaceData
								})) as JQueryPromise<LoginData>;
						}
					}

					return data;
				})
				.then((data: LoginData) => {
					var check1527 = false;

					if (data.loginData === undefined) {
						vm.setMessage({ messageId: 'Msg_1647' });
						return false;
					}

					if (ko.unwrap(vm.message)) {
						if (ko.unwrap(vm.message).messageId === 'Msg_1527') {
							check1527 = true;
						}
					}

					if (check1527) {
						vm.setMessage({ messageId: 'Msg_1527' });
						return false;
					}

					if (data.loginData.msgErrorId === "Msg_1527") {
						vm.setMessage({ messageId: 'Msg_1527' });
						return false;
					}

					var exest = false;

					if (data.loginData.notification == null) {
						exest = true;
					}

					if (!ko.unwrap(vm.modeBasyo)) {
						if (!exest) {
							vm.setMessage({ messageId: 'Msg_1647' });
							return false;
						}
					} else {
						if (data.loginData.result) {
							exest = false;
						}
						if (exest) {
							vm.setMessage({ messageId: 'Msg_1647' });
							return false;
						}
					}

					// if not return full data (first login)
					if (!data.storageData && (!data.loginData || !data.workplaceData && !ko.unwrap(vm.modeBasyo))) {
						vm.setMessage({ messageId: 'Msg_1647' });
						return false;
					}

					const { loginData, storageData, workplaceData } = data;

					if (data.workplaceData) {
						if (!data.workplaceData.selectedId) {
							if (data.workplaceData.notification == null) {
								vm.setMessage({ messageId: 'Msg_1647' });
								return false;
							}
						}
					}

					if (loginData.msgErrorId) {
						vm.setMessage({ messageId: loginData.msgErrorId });
						return false;
					}

					if (loginData.errorMessage) {
						vm.setMessage(loginData.errorMessage);
						return false;
					}

					if (!checkUsed) {
						vm.message({ messageId: 'Msg_1645', messageParams: [vm.$i18n('KDP002_2')] });
						return false;
					}

					// if login & select workspace success
					const { em } = loginData;

					if (ko.unwrap(vm.modeBasyo)) {

						const storeData = {
							CCD: em.companyCode,
							CID: em.companyId,
							PWD: em.password,
							SCD: em.employeeCode,
							SID: em.employeeId,
							WKLOC_CD: '',
							WKPID: vm.workPlace
						};
						return storage(KDP003_SAVE_DATA, storeData);
					} else {
						if (workplaceData) {
							const { selectedId } = workplaceData;

							const storeData = {
								CCD: em.companyCode,
								CID: em.companyId,
								PWD: em.password,
								SCD: em.employeeCode,
								SID: em.employeeId,
								WKLOC_CD: '',
								WKPID: _.isString(selectedId) ? [selectedId] : selectedId
							};

							return storage(KDP003_SAVE_DATA, storeData);
						}
					}

					return storageData;
				})
				.then((storageData: false | StorageData) => {

					if (storageData) {
						return vm.$ajax('at', API.FINGER_STAMP_SETTING)
							.then((data: FingerStampSetting) => {
								if (data) {
									vm.fingerStampSetting(data);
									var time = data.stampSetting.correctionInterval * 60000;

									setInterval(() => {
										vm.loadNotice();
									}, time);
								}
							})
							.then(() => storageData);
					}

					return storageData;
				})
				.then((data: false | StorageData) => {

					// if login and storage data success
					if (data) {
						vm.loadNotice(data);
						return vm.loadData(data);
					}
				})
				// show message from login data (return by f dialog)
				.fail((message: { messageId: string }) => {
					vm.message(message);
				});
		}

		shoNoti() {
			const vm = this;

			const param = { setting: ko.unwrap(vm.fingerStampSetting).noticeSetDto, screen: 'KDP003' };

			vm.$window.modal(DIALOG.R, param);
		}

		settingNoti() {
			const vm = this;

			vm.$window.storage(KDP003_SAVE_DATA)
				.then((data: undefined | StorageData) => {
					if (data) {
						const mode = 'notification';
						const companyId = (data || {}).CID;

						vm.$window.modal('at', DIALOG.F, { mode, companyId })
							.then((output: string) => {
								if (output === 'loginSuccess') {
									vm.$window.modal('at', DIALOG.P)
										.then(() => {
											window.location.reload(false);
										});
								}
							});
					}
				});
		}

		loadNotice(storage?: StorageData) {
			const vm = this;
			let startDate = vm.$date.now();
			//startDate.setDate(startDate.getDate() - 3);
			var wkpIds: string[];

			if (storage) {
				wkpIds = storage.WKPID;
			} else {
				vm.$window
					.storage('loginKDP003')
					.then((data: any) => {
						if (data.WKPID.length > 0) {
							wkpIds = data.WKPID;
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
									vm.messageNoti(data);
									if (data.stopByCompany.systemStatus == 3 || data.stopBySystem.systemStatusType == 3) {
										if (vm.totalOpenViewR === 0) {
											setTimeout(() => {
												const param = { setting: ko.unwrap(vm.fingerStampSetting).noticeSetDto, screen: 'KDP003' };

												vm.totalOpenViewR++;
												vm.$window.modal(DIALOG.R, param);
												vm.showClockButton.setting(false);
											}, 1000);
										}
									}
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
					wkpIds: wkpIds
				}

				vm.$blockui('invisible')
					.then(() => {
						vm.$ajax(API.NOTICE, param)
							.done((data: IMessage) => {
								vm.messageNoti(data);
								if (data.stopByCompany.systemStatus == 3 || data.stopBySystem.systemStatusType == 3) {
									if (vm.totalOpenViewR === 0) {
										const param = { setting: ko.unwrap(vm.fingerStampSetting).noticeSetDto, screen: 'KDP003' };

										vm.totalOpenViewR++;
										vm.$window.modal(DIALOG.R, param);
										vm.showClockButton.setting(false);
									}
								}
							});
					})
					.always(() => {
						vm.$blockui('clear');
					});
			}
		}

		// ※画面起動「※起動1」より再度実行(UI処理[A5])
		// <<ScreenQuery>> 打刻入力(氏名選択)の設定を取得する
		private loadData(storage: StorageData) {
			const vm = this;
			const clearState = () => {
				vm.setMessage({ messageId: 'KDP002_2' });

				// clear tabs button
				vm.buttonPage.tabs([]);
				// remove employee list
				vm.employeeData.nameSelectArt(false);
			};

			if (!_.isObject(storage)) {
				return;
			}

			// <<Command>> 打刻入力を利用できるかを確認する
			return $.Deferred()
				.resolve(ko.toJS(vm.fingerStampSetting))
				.then((data: FingerStampSetting) => {
					if (!data.stampSetting || !data.stampResultDisplay) {
						return vm.$ajax('at', API.FINGER_STAMP_SETTING)
							.then((data: FingerStampSetting) => {
								if (data) {
									vm.fingerStampSetting(data);
								}

								return data;
							});
					}

					return data;
				})
				.then((data: FingerStampSetting) => {
					if (data) {
						const { stampSetting, stampResultDisplay } = data;

						if (!stampResultDisplay) {
							// UI[A6] 打刻利用失敗時のメッセージについて 
							// note: 打刻オプション未購入
							vm.setMessage({ messageId: 'Msg_1644' });
							return;
						}

						if (stampSetting) {
							const { nameSelectArt } = stampSetting;

							// update interval for display datetime
							vm.$date.interval(100);
							setTimeout(() => {
								vm.$date.interval(stampSetting.correctionInterval * 60000);
							}, 1000);

							// clear message and show screen
							vm.message(null);

							// binding tabs data
							vm.buttonPage.tabs(stampSetting.pageLayouts);

							// show employee list
							vm.employeeData.nameSelectArt(!!nameSelectArt);
						} else {
							clearState();
						}
					} else {
						clearState();
					}
				})
				.then(() => ({
					departure: false,
					goingToWork: false,
					goOut: false,
					turnBack: false
				}))
				.then((data: share.StampToSuppress) => vm.buttonPage.stampToSuppress(data))
				// <<ScreenQuery>>: 打刻入力(氏名選択)で社員の一覧を取得する
				.then(() => vm.loadEmployees(storage)) as JQueryPromise<any>;
		}

		private loadEmployees(storage: StorageData) {
			const vm = this;
			const { baseDate } = ko.toJS(vm.employeeData) as EmployeeListData;

			if (!baseDate) {
				return;
			}

			const params = {
				baseDate: moment(baseDate).toISOString(),
				companyId: (storage || {}).CID || '',
				workplaceIds: (storage || {}).WKPID || []
			};

			return vm.$ajax('at', API.EMPLOYEE_LIST, params)
				.then((data: Employee[]) => {
					vm.employeeData.employees(data);
				}) as JQueryPromise<any>;
		}

		setting() {
			const vm = this;
			const { storage } = vm.$window;
			var openViewK: boolean | null = null;
			var saveSuccess: boolean = false;

			// if (!!ko.unwrap(vm.message)) {
			// 	vm.message(false);
			// }
			storage(KDP003_SAVE_DATA)
				.then((data: StorageData) => {
					const mode = 'admin';
					const companyId = (data || {}).CID;

					return vm.$window.modal('at', DIALOG.F, { mode, companyId });
				})
				.then((loginData: undefined | f.TimeStampLoginData) => {

					$.urlParam = function (name) {
						var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);

						if (results == null) {
							return null;
						}
						else {
							return decodeURI(results[1]) || 0;
						}
					}

					const locationCd = $.urlParam('basyo');

					if (locationCd) {
						const param = {
							contractCode: vm.contractCode,
							workLocationCode: locationCd
						}

						return vm.$ajax(API.GET_WORKPLACE_BASYO, param)
							.then((data: IBasyo) => {

								if (data) {

									if (data.workLocationName != null || data.workpalceId != null) {
										vm.worklocationCode = locationCd;
									}

									if (data.workpalceId) {
										if (data.workpalceId.length > 0) {
											vm.modeBasyo(true);
											vm.workPlace = data.workpalceId;
										}

										if (data.workpalceId.length == 0) {
											vm.modeBasyo(false);
										}
									}
								}
							})
							.then(() => loginData);
					}
					return loginData;
				})
				.then((loginData: undefined | f.TimeStampLoginData) => {

					const params = { multiSelect: true };

					if (loginData != undefined) {
						// !exist && 
						if (!ko.unwrap(vm.modeBasyo) && loginData.msgErrorId !== "Msg_1527") {
							return vm.$window.modal('at', DIALOG.K, params)
								.then((workplaceData: undefined | k.Return) => {
									if (workplaceData === undefined) {
										vm.reloadView()
									}

									openViewK = true;
									return {
										loginData,
										workplaceData
									};
								}) as JQueryPromise<LoginData>;
						}
					}
					return loginData;
				})
				.then((data: LoginData) => {
					var exist = true;
					var exist1 = false;
					var checkExistBasyo = false;
					var check1527 = false;

					if (data === undefined) {
						return false;
					}

					if (data.msgErrorId) {
						if (data.msgErrorId === "Msg_1527") {
							check1527 = true;
						}
					}

					if (check1527) {
						vm.setMessage({ messageId: 'Msg_1527' });
						return false;
					}

					if (ko.unwrap(vm.modeBasyo)) {

						if (data.notification == null) {
							var checkExistBasyo = true;
						}
						if (data.result) {
							checkExistBasyo = false;
						}
					}

					if (checkExistBasyo) {
						checkExistBasyo = false;
						return false;
					}

					// data.notification == null
					if (!exist1 && !ko.unwrap(vm.modeBasyo)) {
						exist = false;
					}

					if (ko.unwrap(vm.modeBasyo)) {
						if (vm.workPlace.length <= 0) {
							vm.setMessage({ messageId: 'Msg_1647' });
							return false;
						}
					} else {
						if (data.workplaceData) {
							if (!data.workplaceData.selectedId) {
								if (data.workplaceData.notification == null) {
									return storage(KDP003_SAVE_DATA)
										.then((data: undefined | StorageData) => {
											if (_.isNil(data)) {
												vm.setMessage({ messageId: 'Msg_1647' });

												return false;
											}
										});
								}
							}
						}
					}

					if (ko.unwrap(vm.modeBasyo)) {
						if (data.msgErrorId) {
							vm.setMessage({ messageId: data.msgErrorId });

							return false;
						}

						if (data.errorMessage) {
							vm.setMessage(data.errorMessage);

							return false;
						}
					} else {
						if (data.loginData.msgErrorId) {
							vm.setMessage({ messageId: data.loginData.msgErrorId });

							return false;
						}

						if (data.loginData.errorMessage) {
							vm.setMessage(data.loginData.errorMessage);

							return false;
						}
					}

					// if login & select workspace success

					if (ko.unwrap(vm.modeBasyo)) {
						const storageData = {
							CCD: data.em.companyCode,
							CID: data.em.companyId,
							PWD: data.em.password,
							SCD: data.em.employeeCode,
							SID: data.em.employeeId,
							WKLOC_CD: '',
							WKPID: vm.workPlace
						};

						return storage(KDP003_SAVE_DATA, storageData) as JQueryPromise<StorageData>;
					} else {

						const { em } = data.loginData;
						const { selectedId } = data.workplaceData;

						const storageData = {
							CCD: em.companyCode,
							CID: em.companyId,
							PWD: em.password,
							SCD: em.employeeCode,
							SID: em.employeeId,
							WKLOC_CD: '',
							WKPID: _.isString(selectedId) ? [selectedId] : selectedId
						};

						return storage(KDP003_SAVE_DATA, storageData) as JQueryPromise<StorageData>;
					}

				})
				.then((data: false | StorageData) => {

					// if login and storage data success
					if (data) {
						saveSuccess = true;
						// data login by storage
						const {
							CCD,
							CID,
							PWD,
							SCD,
							SID
						} = data;

						const loginParams: any = {
							companyCode: CCD,
							employeeCode: SCD,
							password: PWD,
							contractCode: vm.contractCode,
							contractPassword: vm.passContract
						};

						return vm.$ajax('com', API.LOGIN_ADMIN, loginParams)
							.then(() => vm.$ajax('at', API.FINGER_STAMP_SETTING))
							.then((data: FingerStampSetting) => {
								if (data) {
									vm.fingerStampSetting(data);
								}
							})
							.then(() => vm.loadData(data)) as JQueryPromise<any>;
					}
				})
				// show message from login data (return by f dialog)
				.fail((message: { messageId: string }) => {
					vm.message(message);
				})
				.always(() => {
					if (ko.unwrap(vm.modeBasyo)) {
						if (saveSuccess) {
							vm.reloadView()
							saveSuccess = false;
						}
					} else {
						if (openViewK && saveSuccess) {
							vm.reloadView()
							openViewK = false;
							saveSuccess = false;
						}
					}
				});
		}

		stampHistory() {
			const vm = this;
			const data: EmployeeListData = ko.toJS(vm.employeeData);
			const { selectedId, employees, nameSelectArt } = data;
			const employee = _.find(employees, (e) => e.employeeId === selectedId);

			if (selectedId === undefined && nameSelectArt) {
				return vm.$dialog.error({ messageId: 'Msg_1646' });
			}

			vm.$window
				.storage(KDP003_SAVE_DATA)
				.then((data: StorageData) => {
					// login by employeeCode
					// <mode> 一覧にない社員で打刻する
					return vm.$window.modal('at', DIALOG.F, {
						mode: 'employee',
						companyId: data.CID,
						employee: employee ? { id: employee.employeeId, code: employee.employeeCode, name: employee.employeeName } : null
					});
				})
				.then((data: f.TimeStampLoginData) => {
					if (data) {
						if (data.msgErrorId) {
							return vm.$dialog.error({ messageId: data.msgErrorId });
						}

						if (data.errorMessage) {
							return vm.$dialog.error(data.errorMessage);
						}

						return vm.$window.modal('at', DIALOG.S, { employeeId: data.em.employeeId });
					}
				});
		}

		stampButtonClick(btn: share.ButtonSetting, layout: share.PageLayout) {

			const vm = this;
			const { buttonPage, employeeData } = vm;
			const { selectedId, employees, nameSelectArt } = ko.toJS(employeeData) as EmployeeListData;
			let stampTime = moment(vm.$date.now()).format("HH:mm");
			let stampDateTime = moment(vm.$date.now()).format();
			
			const reloadSetting = () =>
				$.Deferred()
					.resolve(true)
					.then(() => ({
						departure: false,
						goingToWork: false,
						goOut: false,
						turnBack: false
					}))
					.then((data: any) => {
						const oldData = ko.unwrap(buttonPage.stampToSuppress);

						if (!_.isEqual(data, oldData)) {
							buttonPage.stampToSuppress(data);
						} else {
							buttonPage.stampToSuppress.valueHasMutated();
						}
					});

			// case: 社員一覧(A2)を選択していない場合
			if (selectedId === undefined && nameSelectArt === true) {
				return vm.$dialog.error({ messageId: 'Msg_1646' });
			}
			nts.uk.ui.block.invisible();
			// case: 社員一覧(A2)を選択している場合(社員を選択) || 社員一覧(A2)を選択している場合(固有部品：PA4)、又は社員一覧が表示されていない場合
			return vm.$window.storage(KDP003_SAVE_DATA)
				.then((data: StorageData) => {
					const params: f.EmployeeModeParam | f.FingerVeinModeParam = {
						mode: selectedId || nameSelectArt === true ? 'employee' : 'fingerVein',
						companyId: (data || {}).CID
					};

					if (selectedId) {
						const employee = _.find(employees, (e) => e.employeeId === selectedId);

						if (employee) {
							const { employeeId, employeeCode, employeeName } = employee;

							_.extend(params, {
								employee: {
									id: employeeId,
									code: employeeCode,
									name: employeeName
								}
							});
						}
					}
					params.passwordRequired = vm.fingerStampSetting().stampSetting.passwordRequiredArt
					return vm.$window.modal('at', DIALOG.F, params);
				})
				.then((data: f.TimeStampLoginData) => {
					if (data && !data.msgErrorId && !data.errorMessage) {

						if (data.em) {
							const mode: number = 1;
							const { employeeId, employeeCode } = data.em;
							const fingerStampSetting = ko.unwrap(vm.fingerStampSetting);
							// const workLocationName = vm.workplaceName;
							// const workpalceId = vm.workplaceId;

							// shorten name
							const { modal, storage } = vm.$window;

							// var isSupport: boolean = false;

							// if (btn.supportWplset == 1) {
							// 	isSupport = true;
							// }
							if (fingerStampSetting) {
								vm.$window.storage(KDP003_SAVE_DATA)
									.then((dataStorage: StorageData) => {
										var workGroup: any;
										var isShowViewL = false;
										if (ko.unwrap(vm.useWork)) {
											if (btn.taskChoiceArt && btn.taskChoiceArt == 1) {
												isShowViewL = true;
											}
										}

										vm.$ajax(API.getEmployeeWorkByStamping, { sid: employeeId, workFrameNo: 1, upperFrameWorkCode: '' })
											.then((data: any) => {
												if (data.task.length === 0) {
													if (isShowViewL) {
														isShowViewL = false;
													}
												}
												if (dataStorage.WKPID.length > 1) {
													if (btn.supportWplset == 1) {
														vm.$window.modal('at', DIALOG.M, { screen: 'KDP003', employeeId: employeeId })
															.then((data: string) => {
																if (data) {
																	if (data.notification !== null) {
																		vm.workPlaceId = data;

																		if (isShowViewL) {
																			vm.$window.modal('at', DIALOG.KDP002_L, { employeeId: employeeId })
																				.then((data: any) => {
																					workGroup = data;

																					vm.$ajax(API.REGISTER, {
																						employeeId,
																						dateTime: stampDateTime,
																						stampButton: {
																							pageNo: layout.pageNo,
																							buttonPositionNo: btn.btnPositionNo
																						},
																						refActualResult: {
																							cardNumberSupport: null,
																							workPlaceId: vm.workPlaceId,
																							workLocationCD: vm.worklocationCode,
																							workTimeCode: null,
																							workGroup
																						}
																					}).then(() => {
																						const { stampResultDisplay } = fingerStampSetting;
																						const { displayItemId, notUseAttr } = stampResultDisplay || { displayItemId: [], notUseAttr: 0 } as StampResultDisplay;
																						const { USE } = NotUseAtr;

																						vm.playAudio(btn.audioType);
																						const employeeInfo = { mode, employeeId, employeeCode, workPlaceId: vm.workPlaceId };

																						if (notUseAttr === USE && [share.ChangeClockArt.WORKING_OUT].indexOf(btn.changeClockArt) > -1) {

																							return storage('KDP010_2C', displayItemId)
																								.then(() => storage('infoEmpToScreenC', employeeInfo))
																								.then(() => storage('screenC', { screen: "KDP003" }))
																								.then(() => modal('at', DIALOG.KDP002_C)) as JQueryPromise<any>;
																						} else {
																							const { stampSetting } = fingerStampSetting;
																							const { resultDisplayTime } = stampSetting;

																							return storage('resultDisplayTime', resultDisplayTime)
																								.then(() => storage('infoEmpToScreenB', employeeInfo))
																								.then(() => storage('screenB', { screen: "KDP003" }))
																								.then(() => modal('at', DIALOG.KDP002_B, {stampTime: stampTime })) as JQueryPromise<any>;
																						}
																					})
																						.fail((message: BussinessException) => {
																							const { messageId, parameterIds } = message;
																							vm.$dialog.error({ messageId, messageParams: parameterIds });
																						});
																				})
																		} else {
																			vm.$ajax(API.REGISTER, {
																				employeeId,
																				dateTime: stampDateTime,
																				stampButton: {
																					pageNo: layout.pageNo,
																					buttonPositionNo: btn.btnPositionNo
																				},
																				refActualResult: {
																					cardNumberSupport: null,
																					workPlaceId: vm.workPlaceId,
																					workLocationCD: vm.worklocationCode,
																					workTimeCode: null
																				}
																			}).then(() => {
																				const { stampResultDisplay } = fingerStampSetting;
																				const { displayItemId, notUseAttr } = stampResultDisplay || { displayItemId: [], notUseAttr: 0 } as StampResultDisplay;
																				const { USE } = NotUseAtr;

																				vm.playAudio(btn.audioType);
																				const employeeInfo = { mode, employeeId, employeeCode, workPlaceId: vm.workPlaceId };

																				if (notUseAttr === USE && [share.ChangeClockArt.WORKING_OUT].indexOf(btn.changeClockArt) > -1) {

																					return storage('KDP010_2C', displayItemId)
																						.then(() => storage('infoEmpToScreenC', employeeInfo))
																						.then(() => storage('screenC', { screen: "KDP003" }))
																						.then(() => modal('at', DIALOG.KDP002_C)) as JQueryPromise<any>;
																				} else {
																					const { stampSetting } = fingerStampSetting;
																					const { resultDisplayTime } = stampSetting;

																					return storage('resultDisplayTime', resultDisplayTime)
																						.then(() => storage('infoEmpToScreenB', employeeInfo))
																						.then(() => storage('screenB', { screen: "KDP003" }))
																						.then(() => modal('at', DIALOG.KDP002_B, {stampTime: stampTime })) as JQueryPromise<any>;
																				}
																			}).fail((message: BussinessException) => {
																				const { messageId, parameterIds } = message;
																			})
																		}
																	}
																}
															});
													} else {
														if (isShowViewL) {
															vm.workPlaceId = dataStorage.WKPID[0];
															vm.$window.modal('at', DIALOG.KDP002_L, { employeeId: employeeId })
																.then((data: any) => {
																	workGroup = data;

																	vm.$ajax(API.REGISTER, {
																		employeeId,
																		dateTime: stampDateTime,
																		stampButton: {
																			pageNo: layout.pageNo,
																			buttonPositionNo: btn.btnPositionNo
																		},
																		refActualResult: {
																			cardNumberSupport: null,
																			workPlaceId: vm.workPlaceId,
																			workLocationCD: vm.worklocationCode,
																			workTimeCode: null,
																			workGroup
																		}
																	}).then(() => {
																		const { stampResultDisplay } = fingerStampSetting;
																		const { displayItemId, notUseAttr } = stampResultDisplay || { displayItemId: [], notUseAttr: 0 } as StampResultDisplay;
																		const { USE } = NotUseAtr;

																		vm.playAudio(btn.audioType);
																		const employeeInfo = { mode, employeeId, employeeCode, workPlaceId: vm.workPlaceId };

																		if (notUseAttr === USE && [share.ChangeClockArt.WORKING_OUT].indexOf(btn.changeClockArt) > -1) {

																			return storage('KDP010_2C', displayItemId)
																				.then(() => storage('infoEmpToScreenC', employeeInfo))
																				.then(() => storage('screenC', { screen: "KDP003" }))
																				.then(() => modal('at', DIALOG.KDP002_C)) as JQueryPromise<any>;
																		} else {
																			const { stampSetting } = fingerStampSetting;
																			const { resultDisplayTime } = stampSetting;

																			return storage('resultDisplayTime', resultDisplayTime)
																				.then(() => storage('infoEmpToScreenB', employeeInfo))
																				.then(() => storage('screenB', { screen: "KDP003" }))
																				.then(() => modal('at', DIALOG.KDP002_B, {stampTime: stampTime })) as JQueryPromise<any>;
																		}
																	})
																		.fail((message: BussinessException) => {
																			const { messageId, parameterIds } = message;
																			vm.$dialog.error({ messageId, messageParams: parameterIds });
																		});
																})
														} else {
															vm.workPlaceId = dataStorage.WKPID[0];
															vm.$ajax(API.REGISTER, {
																employeeId,
																dateTime: stampDateTime,
																stampButton: {
																	pageNo: layout.pageNo,
																	buttonPositionNo: btn.btnPositionNo
																},
																refActualResult: {
																	cardNumberSupport: null,
																	workPlaceId: vm.workPlaceId,
																	workLocationCD: vm.worklocationCode,
																	workTimeCode: null
																}
															}).then(() => {
																const { stampResultDisplay } = fingerStampSetting;
																const { displayItemId, notUseAttr } = stampResultDisplay || { displayItemId: [], notUseAttr: 0 } as StampResultDisplay;
																const { USE } = NotUseAtr;

																vm.playAudio(btn.audioType);
																const employeeInfo = { mode, employeeId, employeeCode, workPlaceId: vm.workPlaceId };

																if (notUseAttr === USE && [share.ChangeClockArt.WORKING_OUT].indexOf(btn.changeClockArt) > -1) {

																	return storage('KDP010_2C', displayItemId)
																		.then(() => storage('infoEmpToScreenC', employeeInfo))
																		.then(() => storage('screenC', { screen: "KDP003" }))
																		.then(() => modal('at', DIALOG.KDP002_C)) as JQueryPromise<any>;
																} else {
																	const { stampSetting } = fingerStampSetting;
																	const { resultDisplayTime } = stampSetting;

																	return storage('resultDisplayTime', resultDisplayTime)
																		.then(() => storage('infoEmpToScreenB', employeeInfo))
																		.then(() => storage('screenB', { screen: "KDP003" }))
																		.then(() => modal('at', DIALOG.KDP002_B, {stampTime: stampTime })) as JQueryPromise<any>;
																}
															}).fail((message: BussinessException) => {
																const { messageId, parameterIds } = message;
															})
														}
													}
												} else {
													vm.$blockui('invisible')
														.then(() => {
															vm.workPlaceId = dataStorage.WKPID[0];

															if (isShowViewL) {
																vm.$window.modal('at', DIALOG.KDP002_L, { employeeId: employeeId })
																	.then((data: any) => {
																		workGroup = data;

																		vm.$ajax(API.REGISTER, {
																			employeeId,
																			dateTime: stampDateTime,
																			stampButton: {
																				pageNo: layout.pageNo,
																				buttonPositionNo: btn.btnPositionNo
																			},
																			refActualResult: {
																				cardNumberSupport: null,
																				workPlaceId: vm.workPlaceId,
																				workLocationCD: vm.worklocationCode,
																				workTimeCode: null,
																				workGroup
																			}
																		}).done(() => {
																			vm.$blockui('clear');
																			const { stampResultDisplay } = fingerStampSetting;
																			const { displayItemId, notUseAttr } = stampResultDisplay || { displayItemId: [], notUseAttr: 0 } as StampResultDisplay;
																			const { USE } = NotUseAtr;

																			vm.playAudio(btn.audioType);
																			const employeeInfo = { mode, employeeId, employeeCode, workPlaceId: vm.workPlaceId };

																			if (notUseAttr === USE && [share.ChangeClockArt.WORKING_OUT].indexOf(btn.changeClockArt) > -1) {

																				return storage('KDP010_2C', displayItemId)
																					.then(() => storage('infoEmpToScreenC', employeeInfo))
																					.then(() => storage('screenC', { screen: "KDP003" }))
																					.then(() => modal('at', DIALOG.KDP002_C)) as JQueryPromise<any>;
																			} else {
																				const { stampSetting } = fingerStampSetting;
																				const { resultDisplayTime } = stampSetting;

																				return storage('resultDisplayTime', resultDisplayTime)
																					.then(() => storage('infoEmpToScreenB', employeeInfo))
																					.then(() => storage('screenB', { screen: "KDP003" }))
																					.then(() => modal('at', DIALOG.KDP002_B, {stampTime: stampTime })) as JQueryPromise<any>;
																			}
																		})
																			.fail((message: BussinessException) => {
																				const { messageId, parameterIds } = message;

																				vm.$dialog.error({ messageId, messageParams: parameterIds });
																			});
																	})
															} else {
																vm.$ajax(API.REGISTER, {
																	employeeId,
																	dateTime: stampDateTime,
																	stampButton: {
																		pageNo: layout.pageNo,
																		buttonPositionNo: btn.btnPositionNo
																	},
																	refActualResult: {
																		cardNumberSupport: null,
																		workPlaceId: vm.workPlaceId,
																		workLocationCD: vm.worklocationCode,
																		workTimeCode: null,
																		workGroup
																	}
																})
																	.done(() => {
																		vm.$blockui('clear');
																		const { stampResultDisplay } = fingerStampSetting;
																		const { displayItemId, notUseAttr } = stampResultDisplay || { displayItemId: [], notUseAttr: 0 } as StampResultDisplay;
																		const { USE } = NotUseAtr;

																		vm.playAudio(btn.audioType);
																		const employeeInfo = { mode, employeeId, employeeCode, workPlaceId: vm.workPlaceId };

																		if (notUseAttr === USE && [share.ChangeClockArt.WORKING_OUT].indexOf(btn.changeClockArt) > -1) {

																			return storage('KDP010_2C', displayItemId)
																				.then(() => storage('infoEmpToScreenC', employeeInfo))
																				.then(() => storage('screenC', { screen: "KDP003" }))
																				.then(() => modal('at', DIALOG.KDP002_C)) as JQueryPromise<any>;
																		} else {
																			const { stampSetting } = fingerStampSetting;
																			const { resultDisplayTime } = stampSetting;

																			return storage('resultDisplayTime', resultDisplayTime)
																				.then(() => storage('infoEmpToScreenB', employeeInfo))
																				.then(() => storage('screenB', { screen: "KDP003" }))
																				.then(() => modal('at', DIALOG.KDP002_B, {stampTime: stampTime })) as JQueryPromise<any>;
																		}
																	})
																	.fail((message: BussinessException) => {
																		const { messageId, parameterIds } = message;

																		vm.$dialog.error({ messageId, messageParams: parameterIds });
																	});
															}
														})
												}
											})
									});
							}
						}
						return data;
					}
					return null;
				})
				// always relead setting (color & type of all button in tab)
				.always(reloadSetting);
		}

		playAudio(type: 0 | 1 | 2) {
			const oha = '../../share/voice/0_oha.mp3';
			const otsu = '../../share/voice/1_otsu.mp3';

			if (type !== 0) {
				new Audio(type === 1 ? oha : otsu).play();
			}
		}

		setMessage(message: string | f.Message) {
			const vm = this;

			if (!ko.unwrap(vm.message)) {
				vm.message(message);
			}
		}
	}

	@handler({
		bindingName: 'kdp-error',
		validatable: true,
		virtual: false
	})
	export class MessageErrorBindingHandler implements KnockoutBindingHandler {
		update(element: any, valueAccessor: () => KnockoutObservable<f.Message | string | null | undefined>, __: KnockoutAllBindingsAccessor, vm: nts.uk.ui.vm.ViewModel): void {
			const $el = $(element);
			const msg = ko.unwrap(valueAccessor());

			if (!msg) {
				$el.html('');
			} else {
				if (_.isString(msg)) {
					$el.html(vm.$i18n(msg));
				} else {
					$el.html(vm.$i18n.message(msg.messageId, msg.messageParams));
				}
			}
		}
	}

	interface LoginData {
		loginData?: f.TimeStampLoginData;
		workplaceData?: k.Return;
		storageData?: StorageData;
	}

	interface StorageData {
		CID: string;
		CCD: string;
		SID: string;
		SCD: string;
		PWD: string;
		WKPID: string[];
		WKLOC_CD: string;
	}

	export interface FingerStampSetting {
		stampResultDisplay: StampResultDisplay;
		stampSetting: StampSetting;
		noticeSetDto: INoticeSet;
	}

	interface StampResultDisplay {
		companyId: string;
		displayItemId: number[];
		notUseAttr: NotUseAtr;
	}

	interface StampSetting {
		authcFailCnt: number;
		backGroundColor: string;
		buttonEmphasisArt: boolean;
		cid: string;
		correctionInterval: number;
		employeeAuthcUseArt: boolean;
		historyDisplayMethod: number;
		nameSelectArt: boolean;
		pageLayouts: share.PageLayout[];
		passwordRequiredArt: boolean;
		resultDisplayTime: number;
		textColor: string;
		supportWplset: number;
	}

	enum NotUseAtr {
		/** The use. */
		USE = 1,

		/** The not use. */
		NOT_USE = 0
	}

	enum AuthcMethod {

		// 0:ID認証
		ID_AUTHC = 0,

		// 1:ICカード認証
		IC_CARD_AUTHC = 1,

		// 2:静脈認証
		VEIN_AUTHC = 2,

		// 3:外部認証
		EXTERNAL_AUTHC = 3
	}

	enum StampMeans {
		NAME_SELECTION = 0, // 0:氏名選択
		FINGER_AUTHC = 1,   // 1:指認証打刻
		IC_CARD = 2,        // 2:ICカード打刻
		INDIVITION = 3,     //  3:個人打刻
		PORTAL = 4,         // 4:ポータル打刻
		SMART_PHONE = 5,    // 5:スマホ打刻
		TIME_CLOCK = 6,     // 6:タイムレコーダー打刻
		TEXT = 7,           // 7:テキスト受入
		RICOH_COPIER = 8    // 8:リコー複写機打刻
	}

	enum CanEngravingUsed {
		// 0 利用できる
		AVAILABLE = 0,
		// 1 打刻オプション未購入
		NOT_PURCHASED_STAMPING_OPTION = 1,
		// 2 打刻機能利用不可
		ENGTAVING_FUNCTION_CANNOT_USED = 2,
		// 3 打刻カード未登録
		UNREGISTERED_STAMP_CARD = 3
	}

	interface BussinessException {
		atTime: string;
		businessException: boolean;
		messageId: string;
		parameterIds: string[];
	}

	const DEFAULT_SETTING: FingerStampSetting = {
		stampSetting: null,
		stampResultDisplay: null,
		noticeSetDto: null
	};

	interface INoticeSet {
		comMsgColor: IColorSetting;
		companyTitle: string;
		personMsgColor: IColorSetting;
		wkpMsgColor: IColorSetting;
		wkpTitle: string;
		displayAtr: number;
	}

	interface IColorSetting {
		textColor: string;
		backGroundColor: string;
	}

	interface IMessage {
		messageNotices: IMessageNotice[];
		stopBySystem: IStopBySystem;
		stopByCompany: IStopByCompany;
	}

	interface IStopBySystem {
		systemStatusType: number;
		stopMode: number;
		stopMessage: String;
		usageStopMessage: String
	}

	interface IStopByCompany {
		systemStatus: number;
		stopMessage: String;
		stopMode: number;
		usageStopMessage: String
	}

	interface IMessageNotice {
		creatorID: string;
		inputDate: Date;
		modifiedDate: Date;
		targetInformation: ITargetInformation;
		startDate: Date;
		endDate: Date;
		employeeIdSeen: string[];
		notificationMessage: string;
	}

	interface ITargetInformation {
		targetSIDs: string[];
		targetWpids: string[];
		destination: number | null;
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
