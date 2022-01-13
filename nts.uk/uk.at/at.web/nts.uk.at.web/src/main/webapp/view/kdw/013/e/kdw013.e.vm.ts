module nts.uk.ui.at.kdw013.e {
	import getText = nts.uk.resource.getText;

	interface IParams {
		workNo: number;
		//taskDtos: TaskDto[];
		ouenWorkTimes: OuenWorkTimeOfDailyAttendance[];
		ouenWorkTimeSheets: OuenWorkTimeSheetOfDailyAttendance[];
		taskSettings: a.TaskFrameSettingDto[];
		date: Date
	}

	interface GetWorkDataMasterInforDto {
		tasks1: TaskDto[];
		tasks2: TaskDto[];
		tasks3: TaskDto[];
		tasks4: TaskDto[];
		tasks5: TaskDto[];
	}

	interface SelectTaskItemDto {
		taskDtos: TaskDto[];
	}

	type TaskCbb = {
		taskCode: string,
		taskName: string
	}

	@bean()
	export class ViewModel extends ko.ViewModel {

		taskDtos1: KnockoutObservableArray<TaskDto> = ko.observableArray();
		taskDtos2: KnockoutObservableArray<TaskDto> = ko.observableArray();
		taskDtos3: KnockoutObservableArray<TaskDto> = ko.observableArray();
		taskDtos4: KnockoutObservableArray<TaskDto> = ko.observableArray();
		taskDtos5: KnockoutObservableArray<TaskDto> = ko.observableArray();

		ouenWorkTime: KnockoutObservable<OuenWorkTimeOfDailyAttendance> = ko.observable(null);
		ouenWorkTimeSheet: KnockoutObservable<OuenWorkTimeSheetOfDailyAttendance> = ko.observable(null);
		taskSettings: KnockoutObservableArray<a.TaskFrameSettingDto> = ko.observableArray([]);
		date: KnockoutObservable<Date> = ko.observable();

		useTask1: KnockoutObservable<boolean> = ko.observable(false);
		useTask2: KnockoutObservable<boolean> = ko.observable(false);
		useTask3: KnockoutObservable<boolean> = ko.observable(false);
		useTask4: KnockoutObservable<boolean> = ko.observable(false);
		useTask5: KnockoutObservable<boolean> = ko.observable(false);

		setTaskName1: KnockoutObservable<string> = ko.observable();
		setTaskName2: KnockoutObservable<string> = ko.observable();
		setTaskName3: KnockoutObservable<string> = ko.observable();
		setTaskName4: KnockoutObservable<string> = ko.observable();
		setTaskName5: KnockoutObservable<string> = ko.observable();

		taskLst1: KnockoutObservableArray<TaskCbb> = ko.observableArray([]);
		taskLst2: KnockoutObservableArray<TaskCbb> = ko.observableArray([]);
		taskLst3: KnockoutObservableArray<TaskCbb> = ko.observableArray([]);
		taskLst4: KnockoutObservableArray<TaskCbb> = ko.observableArray([]);
		taskLst5: KnockoutObservableArray<TaskCbb> = ko.observableArray([]);

		selectedTaskCD1: KnockoutObservable<string> = ko.observable('');
		selectedTaskCD2: KnockoutObservable<string> = ko.observable('');
		selectedTaskCD3: KnockoutObservable<string> = ko.observable('');
		selectedTaskCD4: KnockoutObservable<string> = ko.observable('');
		selectedTaskCD5: KnockoutObservable<string> = ko.observable('');

		taskCD1: any;
		taskCD2: any;
		taskCD3: any;
		taskCD4: any;
		taskCD5: any;

		startTime: KnockoutObservable<number> = ko.observable();
		endTime: KnockoutObservable<number> = ko.observable();
		totalTime: KnockoutObservable<number> = ko.observable();

		created(param: IParams) {
			const vm = this;

			if (param) {
				//vm.taskDtos(param.taskDtos);
				vm.ouenWorkTime(_.filter(param.ouenWorkTimes, (t: OuenWorkTimeOfDailyAttendance) => { return t.no == param.workNo })[0]);
				vm.ouenWorkTimeSheet(_.filter(param.ouenWorkTimeSheets, (ts: OuenWorkTimeSheetOfDailyAttendance) => { return ts.workNo == param.workNo })[0]);
				vm.taskSettings(param.taskSettings);
				vm.date(param.date);
			}

			vm.getSetting();
			vm.startTime(vm.ouenWorkTimeSheet().timeSheet.start.timeWithDay);
			vm.endTime(vm.ouenWorkTimeSheet().timeSheet.end.timeWithDay);
			vm.totalTime(vm.ouenWorkTime() ? vm.ouenWorkTime().workTime.totalTime : 0);

			vm.taskCD1 = vm.ouenWorkTimeSheet().workContent.work == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD1 == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD1;
			vm.taskCD2 = vm.ouenWorkTimeSheet().workContent.work == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD2 == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD2;
			vm.taskCD3 = vm.ouenWorkTimeSheet().workContent.work == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD3 == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD3;
			vm.taskCD4 = vm.ouenWorkTimeSheet().workContent.work == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD4 == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD4;
			vm.taskCD5 = vm.ouenWorkTimeSheet().workContent.work == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD5 == null ? '' : vm.ouenWorkTimeSheet().workContent.work.workCD5;
			vm.selectedTaskCD1(vm.taskCD1);
			vm.selectedTaskCD2(vm.taskCD2);
			vm.selectedTaskCD3(vm.taskCD3);
			vm.selectedTaskCD4(vm.taskCD4);
			vm.selectedTaskCD5(vm.taskCD5);
			vm.getCbbList();
		}

		getSetting() {
			const vm = this;

			_.forEach(vm.taskSettings(), s => {
				if (s.frameNo == 1) {
					vm.useTask1(s.useAtr == 1);
					vm.setTaskName1(s.frameName);
				}

				if (s.frameNo == 2) {
					vm.useTask2(s.useAtr == 1);
					vm.setTaskName2(s.frameName);
				}

				if (s.frameNo == 3) {
					vm.useTask3(s.useAtr == 1);
					vm.setTaskName3(s.frameName);
				}

				if (s.frameNo == 4) {
					vm.useTask4(s.useAtr == 1);
					vm.setTaskName4(s.frameName);
				}

				if (s.frameNo == 5) {
					vm.useTask5(s.useAtr == 1);
					vm.setTaskName5(s.frameName);
				}

			});
		}

		getCbbList() {
			const vm = this;

			vm.taskLst1().push({
				taskCode: '',
				taskName: '未選択'
			});

			vm.taskLst2().push({
				taskCode: '',
				taskName: '未選択'
			});

			vm.taskLst3().push({
				taskCode: '',
				taskName: '未選択'
			});

			vm.taskLst4().push({
				taskCode: '',
				taskName: '未選択'
			});

			vm.taskLst5().push({
				taskCode: '',
				taskName: '未選択'
			});

			const paramS = {
				sId: __viewContext.user.employeeId,
				refDate: vm.date(),
				taskCode1: vm.taskCD1,
				taskCode2: vm.taskCD2,
				taskCode3: vm.taskCD3,
				taskCode4: vm.taskCD4,
				taskCode5: vm.taskCD5
			};

			vm.$ajax('at', '/screen/at/kdw013/e/start_task_content_without_time', paramS)
				.then((result: GetWorkDataMasterInforDto) => {
					vm.taskDtos1(result.tasks1);
					vm.taskDtos2(result.tasks2);
					vm.taskDtos3(result.tasks3);
					vm.taskDtos4(result.tasks4);
					vm.taskDtos5(result.tasks5);

					let selectedTask1 = _.find(vm.taskDtos1(), t => t.taskFrameNo == 1 && t.code == paramS.taskCode1);
					if (!selectedTask1 && paramS.taskCode1 != '') {
						vm.taskLst1.push({
							taskCode: paramS.taskCode1,
							taskName: paramS.taskCode1 + ' ' + getText('KDW013_40')
						});
					}

					let selectedTask2 = _.find(vm.taskDtos2(), t => t.taskFrameNo == 2 && t.code == paramS.taskCode2);
					if (!selectedTask2 && paramS.taskCode2 != '') {
						vm.taskLst2.push({
							taskCode: paramS.taskCode2,
							taskName: paramS.taskCode2 + ' ' + getText('KDW013_40')
						});
					}

					let selectedTask3 = _.find(vm.taskDtos3(), t => t.taskFrameNo == 3 && t.code == paramS.taskCode3);
					if (!selectedTask3 && paramS.taskCode3 != '') {
						vm.taskLst3.push({
							taskCode: paramS.taskCode3,
							taskName: paramS.taskCode3 + ' ' + getText('KDW013_40')
						});
					}

					let selectedTask4 = _.find(vm.taskDtos4(), t => t.taskFrameNo == 4 && t.code == paramS.taskCode4);
					if (!selectedTask4 && paramS.taskCode4 != '') {
						vm.taskLst4.push({
							taskCode: paramS.taskCode4,
							taskName: paramS.taskCode4 + ' ' + getText('KDW013_40')
						});
					}

					let selectedTask5 = _.find(vm.taskDtos5(), t => t.taskFrameNo == 5 && t.code == paramS.taskCode5);
					if (!selectedTask5 && paramS.taskCode5 != '') {
						vm.taskLst5.push({
							taskCode: paramS.taskCode5,
							taskName: paramS.taskCode5 + ' ' + getText('KDW013_40')
						});
					}

					let t1: any = vm.taskLst1();
					_.forEach(vm.taskDtos1(), task => {
						t1.push({
							taskCode: task.code,
							taskName: task.code + ' ' + task.displayInfo.taskName
						});
					});
					vm.taskLst1(t1);

					_.forEach(vm.taskDtos2(), task => {
						vm.taskLst2.push({
							taskCode: task.code,
							taskName: task.code + ' ' + task.displayInfo.taskName
						});
					});

					_.forEach(vm.taskDtos3(), task => {
						vm.taskLst3.push({
							taskCode: task.code,
							taskName: task.code + ' ' + task.displayInfo.taskName
						});
					});

					_.forEach(vm.taskDtos4(), task => {
						vm.taskLst4.push({
							taskCode: task.code,
							taskName: task.code + ' ' + task.displayInfo.taskName
						});
					});

					_.forEach(vm.taskDtos5(), task => {
						vm.taskLst5.push({
							taskCode: task.code,
							taskName: task.code + ' ' + task.displayInfo.taskName
						});
					});

					// Sort list combobox by code in ascending order
					vm.taskLst1().sort((a, b) => parseInt(a.taskCode) - parseInt(b.taskCode));
					vm.taskLst2().sort((a, b) => parseInt(a.taskCode) - parseInt(b.taskCode));
					vm.taskLst3().sort((a, b) => parseInt(a.taskCode) - parseInt(b.taskCode));
					vm.taskLst4().sort((a, b) => parseInt(a.taskCode) - parseInt(b.taskCode));
					vm.taskLst5().sort((a, b) => parseInt(a.taskCode) - parseInt(b.taskCode));

					vm.selectedTaskCD1(vm.taskCD1);
					vm.selectedTaskCD2(vm.taskCD2);
					vm.selectedTaskCD3(vm.taskCD3);
					vm.selectedTaskCD4(vm.taskCD4);
					vm.selectedTaskCD5(vm.taskCD5);
				});
		}

		mounted() {
			const vm = this;
			$('#editor-start').focus();

			vm.selectedTaskCD1.subscribe((value) => {
				if (vm.ouenWorkTimeSheet().workContent.work == null) {
					vm.ouenWorkTimeSheet().workContent.work = { workCD1: value, workCD2: null, workCD3: null, workCD4: null, workCD5: null };

				} else {
					vm.ouenWorkTimeSheet().workContent.work.workCD1 = value;
				}

				vm.selectTask(2, vm.selectedTaskCD1());
			});

			vm.selectedTaskCD2.subscribe((value) => {
				vm.ouenWorkTimeSheet().workContent.work.workCD2 = value == '' ? null : value;
				vm.selectTask(3, vm.selectedTaskCD2());
			});

			vm.selectedTaskCD3.subscribe((value) => {
				vm.ouenWorkTimeSheet().workContent.work.workCD3 = value == '' ? null : value;
				vm.selectTask(4, vm.selectedTaskCD3());
			});

			vm.selectedTaskCD4.subscribe((value) => {
				vm.ouenWorkTimeSheet().workContent.work.workCD4 = value == '' ? null : value;
				vm.selectTask(5, vm.selectedTaskCD4());
			});

			vm.selectedTaskCD5.subscribe((value) => {
				vm.ouenWorkTimeSheet().workContent.work.workCD5 = value == '' ? null : value;
			});

			vm.startTime.subscribe((value) => {
				vm.ouenWorkTimeSheet().timeSheet.start.timeWithDay = value;
			})

			vm.endTime.subscribe((value) => {
				vm.ouenWorkTimeSheet().timeSheet.end.timeWithDay = value;
			})

			vm.totalTime.subscribe((value) => {

				if (vm.ouenWorkTime()) {
					vm.ouenWorkTime().workTime.totalTime = value;
				} else {
					let workTime = { totalTime: value };
					vm.ouenWorkTime({ no: vm.ouenWorkTimeSheet().workNo, workTime: workTime });
				}

			})

		}

		selectTask(taskFrameNo: number, upperTaskCode: String) {
			const vm = this;
			const param = {
				sId: vm.$user.employeeId,
				refDate: vm.date(),
				taskFrameNo: taskFrameNo,
				taskCode: upperTaskCode
			}
			vm.$ajax('at', '/screen/at/kdw013/e/select_task_item', param)
				.then((result: SelectTaskItemDto) => {
					let taskLst: TaskCbb[] = [];
					taskLst.push({
						taskCode: '',
						taskName: '未選択'
					});

					_.forEach(result.taskDtos, task => {
						taskLst.push({
							taskCode: task.code,
							taskName: task.code + ' ' + task.displayInfo.taskName
						});
					});

					switch (taskFrameNo) {
						case 2:
							let selectedTask2 = _.find(taskLst, t => t.taskCode == vm.taskCD2);
							if (!selectedTask2 && vm.taskCD2 != '') {
								taskLst.push({
									taskCode: vm.taskCD2,
									taskName: vm.taskCD2 + ' ' + getText('KDW013_40')
								});
							}
							vm.taskLst2(taskLst);
							break;
						case 3:
							let selectedTask3 = _.find(taskLst, t => t.taskCode == vm.taskCD3);
							if (!selectedTask3 && vm.taskCD3 != '') {
								taskLst.push({
									taskCode: vm.taskCD3,
									taskName: vm.taskCD3 + ' ' + getText('KDW013_40')
								});
							}
							vm.taskLst3(taskLst);
							break;
						case 4:
							let selectedTask4 = _.find(taskLst, t => t.taskCode == vm.taskCD4);
							if (!selectedTask4 && vm.taskCD4 != '') {
								taskLst.push({
									taskCode: vm.taskCD4,
									taskName: vm.taskCD4 + ' ' + getText('KDW013_40')
								});
							}
							vm.taskLst4(taskLst);
							break;
						case 5:
							let selectedTask5 = _.find(taskLst, t => t.taskCode == vm.taskCD5);
							if (!selectedTask5 && vm.taskCD5 != '') {
								taskLst.push({
									taskCode: vm.taskCD5,
									taskName: vm.taskCD5 + ' ' + getText('KDW013_40')
								});
							}
							vm.taskLst5(taskLst);
							break;

					}
				});

		}

		decide() {
			const vm = this;
			vm.totalTime.valueHasMutated();
			let param = {
				empId: vm.$user.employeeId,
				date: vm.date(),
				ouenTimeSheet: ko.unwrap(vm.ouenWorkTimeSheet),
				ouenTime: { workNo: ko.unwrap(vm.ouenWorkTime).no, workTime: ko.unwrap(vm.ouenWorkTime).workTime },
			};

			vm.$blockui('show');
			$('.ntsControl').trigger('validate');
			if (!nts.uk.ui.errors.hasError()) {
				vm.$ajax('at', '/screen/at/kdw013/e/update_timezone', param)
					.done(() => {
						vm.$dialog.info({ messageId: 'Msg_15' }).then(() => {
							vm.close()
						});
					}).fail((error: any) => {
						vm.$dialog.error(error);
					}).always(() => {
						vm.$blockui("hide");
					});

			} else {
				vm.$blockui("clear");
			}
		}

		// ダイアログを閉じる
		close() {
			const vm = this;
			vm.$window.close();
		}

	}










}