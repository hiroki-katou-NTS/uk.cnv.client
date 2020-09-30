/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.cmm024.d {
	import service = nts.uk.com.view.cmm024.a.service;
	import HistoryUpdate = nts.uk.com.view.cmm024.a.service.HistoryUpdate;
	import ScheduleHistoryDto = nts.uk.com.view.cmm024.a.service.ScheduleHistoryDto;
	import ScheduleHistoryModel = nts.uk.com.view.cmm024.a.service.ScheduleHistoryModel;

	@bean()
	class ViewModel extends ko.ViewModel {

		registrationHistory: KnockoutObservableArray<any> = ko.observableArray([]);
		registrationHistoryType: KnockoutObservable<number> = ko.observable(0);
		allowStartDate: KnockoutObservable<Date> = ko.observable(moment(new Date()).toDate());
		newStartDate: KnockoutObservable<Date> = ko.observable(moment(new Date()).toDate());
		newEndDate: KnockoutObservable<Date> = ko.observable(moment(new Date()).toDate());
		//set & get Share
		newScheduleHistory: KnockoutObservable<ScheduleHistoryDto> = ko.observable(null);
		enableDatePicker: KnockoutObservable<boolean> = ko.observable(false);
		scheduleHistoryModel: KnockoutObservable<ScheduleHistoryModel> = ko.observable(null);

		constructor(params: any) {
			// start point of object
			super();

			let vm = this;
			vm.registrationHistory.push({ id: HistoryUpdate.HISTORY_DELETE, name: vm.$i18n('CMM024_33') });
			vm.registrationHistory.push({ id: HistoryUpdate.HISTORY_EDIT, name: vm.$i18n('CMM024_34') });
		}

		created(params: any) {
			// start point of object
			let vm = this;
		}

		mounted() {
			// raise event when view initial success full
			let vm = this;

			//get value from parent form
			vm.newEndDate(moment(service.END_DATE, 'YYYY/MM/DD').toDate());

			vm.registrationHistoryType.subscribe((value) => {
				vm.enableDatePicker(value !== 0);
			})

			vm.$window.storage('CMM024_D_INPUT').then((data: ScheduleHistoryModel) => {
				let scheduleHistoryUpdate: ScheduleHistoryDto = null;

				vm.scheduleHistoryModel(data);

				scheduleHistoryUpdate = vm.scheduleHistoryModel().scheduleHistoryUpdate;
				vm.newStartDate(moment(scheduleHistoryUpdate.startDate).toDate());

				let allowStartDate: Date = moment(vm.scheduleHistoryModel().allowStartDate).toDate();
				vm.allowStartDate(allowStartDate);
			});
		}

		/**
		 * Process
		 * */
		proceed() {
			let vm = this;
			if (!nts.uk.ui.errors.hasError()) {
				let stDate: Date = vm.newStartDate(), //開始年月日テキストボックス -> A2_6, B2_6
					endDate: Date = vm.newEndDate(),
					allowDate: Date = vm.allowStartDate();

				let dataModel = vm.scheduleHistoryModel();

				if (moment(stDate).isBefore(allowDate)) {

					let oldDate: string = moment(allowDate, 'YYYY/MM/DD').format('YYYY/MM/DD');
					vm.$dialog.error({ messageId: "Msg_156", messageParams: [oldDate] });

					return;

				} else {
					let status = false,
						params = {
							companyId: dataModel.workPlaceCompanyId,
							startDate: moment.utc(dataModel.scheduleHistoryUpdate.startDate, 'YYYY-MM-DD'),
							endDate: moment.utc(dataModel.scheduleHistoryUpdate.endDate, 'YYYY-MM-DD'),
							startDateBeforeChange: null,
							screen: dataModel.screen
						};

					console.log(service.HistoryUpdate);
					if (vm.registrationHistoryType() == service.HistoryUpdate.HISTORY_DELETE) {
						vm.$dialog.confirm({ messageId: 'Msg_18' }).then((result: string) => {
							if (result === 'yes') {
								vm.deleteScheduleHistory(params);
								vm.$window.storage("CMM024_D_RESULT", {
									newScheduleHistory: null,
									RegistrationHistoryType: vm.registrationHistoryType()
								});
								vm.$window.close();
							}
						});
					} else {

						params.startDate = moment.utc(vm.newStartDate(), 'YYYY-MM-DD');
						params.endDate = moment.utc(vm.newEndDate(), 'YYYY-MM-DD');
						params.startDateBeforeChange = moment.utc(dataModel.scheduleHistoryUpdate.startDate, 'YYYY-MM-DD');

						status = vm.updateScheduleHistory(params);

						vm.$window.storage("CMM024_D_RESULT", {
							newScheduleHistory: {
								startDate: moment(vm.newStartDate()).format("YYYY/MM/DD"),
								newEndDate: moment(vm.newStartDate()).subtract(1, "days").format("YYYY/MM/DD")
							},
							RegistrationHistoryType: vm.registrationHistoryType(),
							status: status
						});

						vm.$window.close();
					}
				}
				return false;
			}
		}

		/**
		 * Cancel and close window
		 * */
		cancel() {
			let vm = this;
			vm.$window.storage("CMM024_D_RESULT", null);
			vm.$window.close();
			return false;
		}

		initScheduleHistory() {
			let vm = this;
		}

		/**
		 * Delete a schedule history of company / workplace
		*/
		deleteScheduleHistory(params: any) {
			let vm = this;

			switch (params.screen) {
				case 'A':
					vm.$blockui('show');
					service.deleteAScheduleHistoryByCompany(params)
						.done((response) => {
							vm.$dialog.info({ messageId: 'Msg_16' });
							vm.$blockui('hide');
						})
						.fail(() => vm.$blockui('hide'))
						.always(() => vm.$blockui('hide'));
					break;

				case 'B':
					vm.$blockui('show');
					service.deleteAScheduleHistoryByWorkplace(params)
						.done((response) => {
							vm.$dialog.info({ messageId: 'Msg_16' });
							vm.$blockui('hide');
						})
						.fail(() => vm.$blockui('hide'))
						.always(() => vm.$blockui('hide'));
					break;
			}
		}

		/**
		 * update a schedule history of company / workplace
		*/
		updateScheduleHistory(params: any) {
			let vm = this,
				status: boolean = false;

			switch (params.screen) {
				case 'A':
					vm.$blockui('show');
					service.updateAScheduleHistoryByCompany(params)
						.done((response) => {
							vm.$dialog.info({ messageId: 'Msg_15' });
							status = true;
							vm.$blockui('hide');
						})
						.fail(() => vm.$blockui('hide'))
						.always(() => vm.$blockui('hide'));
					break;

				case 'B':
					vm.$blockui('show');
					service.updateAScheduleHistoryByWorkplace(params)
						.done((response) => {
							vm.$dialog.info({ messageId: 'Msg_15' });
							status = true;
							vm.$blockui('hide');
						})
						.fail(() => vm.$blockui('hide'))
						.always(() => vm.$blockui('hide'));
					break;
			}

			return status;
		}
	}
}