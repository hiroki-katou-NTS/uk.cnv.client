/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />

module nts.uk.at.kdp003.f {
	import a = nts.uk.at.kdp003.a;

	export interface CodeNameData {
		id?: string;
		code?: string;
		name?: string;
	}

	// admin mode param
	export interface AdminModeParam {
		mode: 'admin';
		companyId: string;
		companyDesignation?: boolean;
	}

	// employee mode param
	export interface EmployeeModeParam {
		mode: 'employee';
		companyId: string;
		employee?: CodeNameData;
		passwordRequired?: boolean;
	}

	// finger mode param
	export interface FingerVeinModeParam {
		mode: 'fingerVein';
		companyId: string;
		employee?: CodeNameData;
	}

	export interface NotiModeParam {
		mode: 'notification';
		companyId: string;
		companyDesignation?: boolean;
	}

	export type MODE = null | 'admin' | 'employee' | 'fingerVein' | 'notification';
	export type SCREEN_NAME = 'KDP001' | 'KDP002' | 'KDP003' | 'KDP004' | 'KDP005';

	export const LOGINDATA = 'KDP003F_LOGINDATA';
	const CONTRACTDATA = 'contractInfo';

	export const API = {
		LOGIN_ADMIN: '/ctx/sys/gateway/kdp/login/adminmode',
		LOGIN_EMPLOYEE: '/ctx/sys/gateway/kdp/login/employeemode',
		COMPANIES: '/ctx/sys/gateway/kdp/login/getLogginSetting',
		FINGER_STAMP_SETTING: 'at/record/stamp/finger/get-finger-stamp-setting',
		CONFIRM_STAMP_INPUT: '/at/record/stamp/employment/system/confirm-use-of-stamp-input',
		ROLE: '/ctx/sys/gateway/kdp/login/employeeRoleStamping'
	};

	@bean()
	export class ViewModel extends ko.ViewModel {
		mode: KnockoutObservable<MODE> = ko.observable(null);
		message: KnockoutObservable<Message | null> = ko.observable(null);

		model: Model = new Model();

		listCompany: KnockoutObservableArray<CompanyItem> = ko.observableArray([]);

		parentName: KnockoutObservable<SCREEN_NAME> = ko.observable('KDP003');

		contractCd: string = null;

		constructor(private params?: AdminModeParam | EmployeeModeParam | FingerVeinModeParam | NotiModeParam) {
			super();
		}

		public created() {
			const vm = this;
			const { params, model } = vm;

			if (!params) {
				vm.params = {} as any;
			} else if (params) {
				
				const { companyId, employee } = params as EmployeeModeParam;

				if (companyId) {
					model.companyId(companyId);
				}

				if (employee) {
					if (!_.isNil(employee.id)) {
						model.employeeId(employee.id);
					}

					if (!_.isNil(employee.code)) {
						model.employeeCode(employee.code);
					}

					if (!_.isNil(employee.name)) {
						model.employeeName(employee.name);
					}
				}
			}

			vm.model.companyId
				.subscribe((id: string) => {
					const dataSources: CompanyItem[] = ko.unwrap(vm.listCompany);

					vm.message(null);

					if (dataSources.length) {
						const exist = _.find(dataSources, (item: CompanyItem) => item.companyId === id);

						if (exist) {
							const parentName = ko.unwrap(vm.parentName);
							const employeeCode: string = ko.unwrap(model.employeeCode);

							model.companyCode(exist.companyCode);
							model.companyName(exist.companyName);

							// UI[A6]  打刻利用失敗時のメッセージについて
							if (!ko.unwrap(vm.message) && employeeCode) {
								const params: CommanStampInput = {
									companyId: exist.companyId,
									employeeId: '',
									employeeCode,
									stampMeans: StampMeans.NAME_SELECTION
								};

								const authen = (data: ConfirmStampInput) => {
									if (data.used === CanEngravingUsed.NOT_PURCHASED_STAMPING_OPTION) {
										// UI[A6]  打刻オプション未購入 
										vm.message({ messageId: 'Msg_1644' });
									} else if (data.used === CanEngravingUsed.UNREGISTERED_STAMP_CARD) {
										// UI[A6]  打刻カード未登録
										vm.message({ messageId: 'Msg_1619' });
									} else if (data.used === CanEngravingUsed.ENGTAVING_FUNCTION_CANNOT_USED) {
										// UI[A6]  打刻機能利用不可
										const messageParams = [];

										switch (parentName) {
											case 'KDP001':
												messageParams.push(vm.$i18n('KDP001_1'));
												break;
											case 'KDP002':
												messageParams.push(vm.$i18n('KDP002_1'));
												break;
											default:
											case 'KDP003':
												messageParams.push(vm.$i18n('KDP002_2'));
												break;
											case 'KDP004':
												messageParams.push(vm.$i18n('KDP002_3'));
												break;
											case 'KDP005':
												messageParams.push(vm.$i18n('KDP002_4'));
												break;
										}

										vm.message({
											messageId: 'Msg_1645',
											messageParams
										});
									} else {
										vm.message(null);
									}
								};

								switch (parentName) {
									case 'KDP001':
										params.stampMeans = StampMeans.PORTAL;
										break;
									case 'KDP002':
										params.stampMeans = StampMeans.INDIVITION;
										break;
									default:
									case 'KDP003':
										params.stampMeans = StampMeans.NAME_SELECTION;
										break;
									case 'KDP004':
										params.stampMeans = StampMeans.FINGER_AUTHC;
										break;
									case 'KDP005':
										params.stampMeans = StampMeans.IC_CARD;
										break;
								}

								vm.$ajax('at', API.CONFIRM_STAMP_INPUT, params).then(authen);
							}
						}
					}
				});

			vm.listCompany.subscribe(() => {
				model.companyId.valueHasMutated();
			});

			model.employeeCode.subscribe(() => {
				model.companyId.valueHasMutated();
			});
		}

		public mounted() {
			const vm = this;
			const { model, params } = vm;
			const SCREEN: RegExpMatchArray = window.top.location.href.match(/kdp\/00\d/);

			if (SCREEN.length) {
				const name: SCREEN_NAME = SCREEN[0].replace(/\//g, '').toUpperCase() as any;

				if (name) {
					vm.parentName(name);
				}
			}

			vm.$blockui('show')
				.then(() => {
					vm.$window.storage(CONTRACTDATA)
						.then((data: any) => {
							if (data) {
								vm.contractCd = data.contractCode;
							}
						})
						.then(() => vm.$ajax(API.COMPANIES, {contractCode: vm.contractCd}))
						.then((data: CompanyItem[]) => {
							const valueHasMutated = () => {
								const companyId = ko.toJS(params.companyId);
								const exist: CompanyItem = _.find(data, (c) => c.companyId === companyId);

								vm.listCompany(data);

								if (exist) {
									if (ko.unwrap(model.companyId) !== exist.companyId) {
										model.companyId(exist.companyId);
									} else {
										model.companyId.valueHasMutated();
									}
								} else if (data.length === 1) {
									model.companyId(data[0].companyId);
								}
							};

							if (params.mode === 'admin') {
								let showMsg1527 = false;
								const parentName = ko.unwrap(vm.parentName);

								switch (parentName) {
									case 'KDP001':
										break;
									case 'KDP002':
										break;
									default:
									case 'KDP003':
										showMsg1527 = _.every(data, d => d.selectUseOfName === false) || !data.length;
										break;
									case 'KDP004':
										showMsg1527 = _.every(data, d => d.fingerAuthStamp === false) || !data.length;
										break;
									case 'KDP005':
										showMsg1527 = _.every(data, d => d.icCardStamp === false) || !data.length;
										break;
								}

								if (showMsg1527 === true) {
									vm.$dialog
										.error({ messageId: 'Msg_1527' })
										.then(() => vm.$window.close({ msgErrorId: 'Msg_1527' }));
								} else {
									valueHasMutated();
								}
							} else {
								valueHasMutated();
							}
						})
						.then(() => vm.$ajax('at', API.FINGER_STAMP_SETTING, params))
						.then((data: a.FingerStampSetting) => {
							const { stampSetting } = data;

							_.extend(vm.params, {
								passwordRequired: stampSetting ? stampSetting.passwordRequiredArt : true
							});
						})
						.then(() => {
							if (vm.params.mode === 'admin') {
								vm.$window.size.height(270);
							} else if (vm.params.mode === 'employee') {
								if (!vm.params.passwordRequired) {
									vm.$window.size.height(225);
								} else {
									vm.$window.size.height(270);
								}
							}
						})
				})
				// get mode from params or set default
				.always(() => {
					vm.$blockui('clear')
						.then(() => vm.mode(vm.params.mode || 'admin'))
						.then(() => {
							const cbi = '.ui-igcombo-field';
							const cbw = '.ui-igcombo-wrapper';

							//$(vm.$el).find(`[tabindex]:not(${cbw}):not(${cbi})`).first().focus();
							$("#master-content").find("[tabindex]:not(" + cbw + "):not(" + cbi + ")").first().focus()
						});
				});

			$(vm.$el)
				.on('keyup', '#password-input, #employee-code-inp-2', evt => {
					if (evt.keyCode === 13) {
						// hook blur for update value from input to model
						$(evt.target).trigger('blur');
						const { employeeCode, password } = vm.model;

						if (!!ko.unwrap(employeeCode) && !!ko.unwrap(password)) {
							vm.submitLogin();
						}
					}
				});
		}

		public submitLogin() {
			const vm = this;
			const { params } = vm;
			const { LOGIN_ADMIN, LOGIN_EMPLOYEE } = API;

			switch (params.mode) {
				default:
				case 'admin':
					// note: メニュー別OCD内の記述を移送表に追加する
					vm.model.passwordInvalid = false;
					vm.model.isAdminMode = true;
					vm.model.runtimeEnvironmentCreate = true;

					vm.loginAdmin(LOGIN_ADMIN);
					break;
				case 'employee':
				case 'fingerVein':
					// note: メニュー別OCD内の記述を移送表に追加する
					vm.model.isAdminMode = false;
					vm.model.runtimeEnvironmentCreate = false;

					vm.loginAdmin(LOGIN_EMPLOYEE);
					break;
				case 'notification':
					vm.model.passwordInvalid = false;
					vm.model.isAdminMode = true;
					vm.model.runtimeEnvironmentCreate = true;

					vm.loginNoti(LOGIN_ADMIN);
					break;
			}
		}

		loginAdmin(api: string) {
			const vm = this;
			const { passwordRequired } = vm.params as EmployeeModeParam;
			const model: ModelData = ko.toJS(vm.model);
			const { password, companyCode } = model;
			const companies: CompanyItem[] = ko.unwrap(vm.listCompany);

			const message = ko.unwrap(vm.message);

			var dataResultLogin: TimeStampLoginData;

			var roleEmployee: RoleEmployee;

			if (message) {
				return vm.$dialog.error(message);
			}

			if (passwordRequired === false) {
				_.omit(model, ['password']);
			// note: メニュー別OCD内の記述を移送表に追加する
				model.passwordInvalid = true;
			}

			vm.$validate()
				.then((valid: boolean) => {
					if (!valid) {
						return;
					}
					vm.$blockui('show')
						.then(() => vm.$ajax(api, model))
						.fail((response: any) => {
							const { message, messageId } = response;

							if (!messageId) {
								vm.$dialog.error(message);
							} else {
								vm.$dialog.error({ messageId });
							}
						})
						.then((response: TimeStampLoginData) => {
							dataResultLogin = response;
						})
						.then(() => {
							vm.$ajax(API.ROLE)
								.done((data: RoleEmployee) => {
									if (data) {
										roleEmployee = data;
										if (roleEmployee.employeeReferenceRange == 3) {
											vm.$dialog.error({ messageId: 'Msg_1887' });
										}
									}
								})
						})
						.then(() => {
							const { successMsg } = dataResultLogin;
							if (!!successMsg) {
								return vm.$dialog
									.info({ messageId: successMsg })
									.then(() => dataResultLogin);
							}

							return dataResultLogin;
						})
						.then(() => {

							_.extend(dataResultLogin, {
								companies
							});

							_.extend(dataResultLogin.em, {
								password,
								companyCode
							});
							setTimeout(() => {
								if (roleEmployee) {
									if (roleEmployee.employeeReferenceRange != 3) {
										vm.$window.close(dataResultLogin);
									}
								}
							}, 100);
						})
						.always(() => vm.$blockui('clear'));

				});
		}

		loginNoti(api: string) {
			const vm = this;
			const { passwordRequired } = vm.params as EmployeeModeParam;
			const model: ModelData = ko.toJS(vm.model);
			const { password, companyCode } = model;
			const companies: CompanyItem[] = ko.unwrap(vm.listCompany);

			const message = ko.unwrap(vm.message);

			var dataResultLogin: TimeStampLoginData;

			var roleEmployee: RoleEmployee;

			if (message) {
				if (message.messageId !== 'Msg_1645') {
					return vm.$dialog.error(message);
				}
			}

			if (passwordRequired === false) {
				_.omit(model, ['password']);
			// note: メニュー別OCD内の記述を移送表に追加する
				model.passwordInvalid = true;
			}

			vm.$validate()
				.then((valid: boolean) => {
					if (!valid) {
						return;
					}
					if (companyCode === '') {
						return vm.$dialog.error({ messageId: 'Msg_301' });
					}
					vm.$blockui('show')
						.then(() => vm.$ajax(api, model))
						.fail((response: any) => {

							const { message, messageId } = response;

							if (!messageId) {
								vm.$dialog.error(message);
							} else {
								vm.$dialog.error({ messageId });
							}
						})
						.then((response: TimeStampLoginData) => {
							dataResultLogin = response;
						})
						.then(() => {
							vm.$ajax(API.ROLE)
								.done((data: RoleEmployee) => {
									if (data) {
										roleEmployee = data;
										if (roleEmployee.employeeReferenceRange == 3) {
											vm.$dialog.error({ messageId: 'Msg_1887' });
										}
									}
								})
						})
						.then(() => {
							const { successMsg } = dataResultLogin;
							if (!!successMsg) {
								return vm.$dialog
									.info({ messageId: successMsg })
									.then(() => dataResultLogin);
							}

							return dataResultLogin;
						})
						.then(() => {
							_.extend(dataResultLogin, {
								companies
							});

							_.extend(dataResultLogin.em, {
								password,
								companyCode
							});
							setTimeout(() => {
								if (roleEmployee) {
									if (roleEmployee.employeeReferenceRange != 3) {
										vm.$window.close('loginSuccess');
									}
								}
							}, 100);
						})
						.always(() => vm.$blockui('clear'));

				});
		}

		cancelLogin() {
			const vm = this;

			vm.$window.close();
		}
	}

	export interface Message {
		messageId: string;
		messageParams?: string[];
	}

	export interface ConfirmStampInput {
		systemDate: string;
		used: CanEngravingUsed;
	}

	export interface CommanStampInput {
		companyId: string;
		employeeId: string;
		employeeCode: string;
		stampMeans: StampMeans;
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

	export interface CompanyItem {
		companyId: string;
		companyCode: string;
		companyName: string;
		contractCd?: string;
		icCardStamp?: boolean
		selectUseOfName?: boolean;
		fingerAuthStamp?: boolean;
	}

	export interface EmployeeData {
		readonly companyId: string;
		readonly companyCode?: string;
		readonly personalId: string;
		readonly employeeId: string;
		readonly employeeCode: string;
		readonly employeeName: string;
		password?: string;
	}

	export interface TimeStampLoginData {
		readonly showChangePass: boolean;
		readonly msgErrorId: string;
		readonly showContract: boolean;
		readonly result: boolean;
		readonly em: EmployeeData;
		readonly successMsg: string;
		readonly errorMessage: string;
	}

	export interface ModelData {
		readonly contractCode: string;
		readonly companyId: string;
		readonly companyCode: string;
		readonly companyName?: string;
		readonly employeeId: string;
		readonly employeeCode: string;
		readonly employeeName?: string;
		readonly password?: string;
		passwordInvalid: boolean;
		isAdminMode: boolean;
		runtimeEnvironmentCreate: boolean;
	}

	export interface RoleEmployee {
		roleId: string;
		roleCode: string;
		roleType: number;
		employeeReferenceRange: number;
		name: string;
		rolcontractCodeeId: string;
		assignAtr: number;
		companyId: string
	}

	export class Model {
		// default data;
		contractCode: string = '000000000004';
		passwordInvalid: boolean = false;
		isAdminMode: boolean = false;
		runtimeEnvironmentCreate: boolean = true;

		companyId: KnockoutObservable<string> = ko.observable('');
		companyCode: KnockoutObservable<string> = ko.observable('');
		companyName: KnockoutObservable<string> = ko.observable('');

		employeeId: KnockoutObservable<string> = ko.observable('');
		employeeCode: KnockoutObservable<string> = ko.observable('');
		employeeName: KnockoutObservable<string> = ko.observable('');

		password: KnockoutObservable<string> = ko.observable('');

		constructor(params?: ModelData) {
			const model = this;

			if (params) {
				model.companyCode(params.companyCode);
			}
		}
	}
}