/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.ui.at.ksu002.a {
	import m = nts.uk.ui.memento;
	import c = nts.uk.ui.calendar;
	import setShared = nts.uk.ui.windows.setShared;

	type DayData = c.DayData<ScheduleData>;
	type DayDataRawObsv = c.DayData<ObserverScheduleData>;
	type DayDataMementoObsv = c.DayData<ObserverScheduleData<WorkSchedule>>;
	type DayDataSave2Memento = DayData | DayDataRawObsv | DayDataMementoObsv;

	const API = {
		GSCHE: '/screen/ksu/ksu002/displayInWorkInformation',
		GSCHER: '/screen/ksu/ksu002/getDataDaily',
		SAVE_DATA: '/screen/ksu/ksu002/regisWorkSchedule',
		GET_START_DAY_OF_WEEK: '/ctx/at/shared/workrule/weekmanage/find'
	};

	const memento: m.Options = {
		size: 20,
		// callback function raise when undo or redo
		replace: function (data: DayDataRawObsv[], preview: DayData) {
			const exist = _.find(data, f => moment(f.date).isSame(preview.date, 'date'));

			if (exist) {
				const { data } = exist;
				const { wtime, wtype, value, state, classification } = preview.data;

				data.wtime.code(wtime.code);
				data.wtime.name(wtime.name);

				data.wtype.code(wtype.code);
				data.wtype.name(wtype.name);

				if (data.value.begin() !== value.begin) {
					data.value.begin(value.begin);
				} else {
					data.value.begin.valueHasMutated();
				}

				if (data.value.finish() !== value.finish) {
					data.value.finish(value.finish);
				} else {
					data.value.finish.valueHasMutated();
				}

				if (data.value.required() !== value.required) {
					data.value.required(value.required);
				} else {
					data.value.required.valueHasMutated();
				}

				if (data.state.wtype() !== state.wtype) {
					data.state.wtype(state.wtype);
				} else {
					data.state.wtype.valueHasMutated();
				}

				if (data.state.wtime() !== state.wtime) {
					data.state.wtime(state.wtime);
				} else {
					data.state.wtime.valueHasMutated();
				}

				if (data.classification() !== classification) {
					data.classification(classification);
				} else {
					data.classification.valueHasMutated();
				}

				if (data.state.value.begin() !== state.value.begin) {
					data.state.value.begin(state.value.begin);
				} else {
					data.state.value.begin.valueHasMutated();
				}

				if (data.state.value.finish() !== state.value.finish) {
					data.state.value.finish(state.value.finish);
				} else {
					data.state.value.finish.valueHasMutated();
				}
			}
		},
		softReset: function (dayDatas: DayDataRawObsv[]) {
			dayDatas
				.forEach(({ data }) => {
					const {
						$raw,
						wtime,
						wtype,
						value,
						achievement
					} = data;

					if (ko.unwrap(achievement)) {
						$raw.achievements.workTypeCode = ko.unwrap(wtype.code);
						$raw.achievements.workTypeName = ko.unwrap(wtype.name);

						$raw.achievements.workTimeCode = ko.unwrap(wtime.code);
						$raw.achievements.workTimeName = ko.unwrap(wtime.name);

						$raw.achievements.startTime = ko.unwrap(value.begin);
						$raw.achievements.endTime = ko.unwrap(value.finish);
					} else {
						$raw.workTypeCode = ko.unwrap(wtype.code);
						$raw.workTypeName = ko.unwrap(wtype.name);

						$raw.workTimeCode = ko.unwrap(wtime.code);
						$raw.workTimeName = ko.unwrap(wtime.name);

						$raw.startTime = ko.unwrap(value.begin);
						$raw.endTime = ko.unwrap(value.finish);
					}
				});
		},
		hasChange: function (dayDatas: DayDataRawObsv[]) {
			const changeds = dayDatas
				.map(({ data }) => {
					const {
						$raw,
						wtime,
						wtype,
						value,
						achievement
					} = data;
					const {
						workTypeCode,
						workTimeCode,
						startTime,
						endTime
					} = ko.unwrap(achievement) ? $raw.achievements : $raw;

					return ko.unwrap(wtype.code) !== workTypeCode
						|| ko.unwrap(wtime.code) !== workTimeCode
						|| ko.unwrap(value.begin) !== startTime
						|| ko.unwrap(value.finish) !== endTime;
				})
				.filter((f) => !!f);

			return changeds.length !== 0;
		}
	};

	@bean()
	export class ViewModel extends ko.ViewModel {
		enable!: KnockoutComputed<boolean>;
		currentUser!: KnockoutComputed<string>;
		showC: KnockoutObservable<boolean> = ko.observable(true);

		mode: KnockoutObservable<EDIT_MODE> = ko.observable('copy');
		yearMonth: KnockoutObservable<string> = ko.observable(moment().format('YYYYMM'));
		baseDate: KnockoutObservable<c.DateRange | null> = ko.observable(null);
		baseDateKCP015: KnockoutObservable<Date> = ko.observable(null);
		schedules: m.MementoObservableArray<DayDataRawObsv> = ko.observableArray([]).extend({ memento }) as any;

		workplaceId: KnockoutObservable<string> = ko.observable(null);
		achievement: KnockoutObservable<ACHIEVEMENT> = ko.observable(ACHIEVEMENT.NO);
		workData: KnockoutObservable<null | WorkData> = ko.observable(null);
		kdl053Open: any = null;
		
		employeeId: KnockoutObservableArray<string>;
		
		data: WorkSchedule<moment.Moment>[] = [];
		dataDaily: Achievement[] = [];
		
		dayStartWeek: KnockoutObservable<number> = ko.observable(null);
		
		isSelectedStartWeek: KnockoutObservable<boolean> = ko.observable(false);
		
		storageDataStartWeek: KnockoutObservable<any> = ko.observable(null);
		
		readyLoadData: boolean = false;
		
		// Data result from 予定・実績を取得する
		plansResultsData: KnockoutObservable<any> = ko.observable(null);
		
		legalworkinghours: KnockoutObservable<any> = ko.observable(null);
		
		startupProcessingInformation: KnockoutObservable<any> = ko.observable(null); 
		
		listOfPeriodsClose: KnockoutObservable<any> = ko.observable(null);
		
		visibleA1_1: KnockoutObservable<boolean> = ko.observable(false);
		visibleA1_2: KnockoutObservable<boolean> = ko.observable(false);
		visibleA1_3: KnockoutObservable<boolean> = ko.observable(false);
		visibleA1_4: KnockoutObservable<boolean> = ko.observable(false);
    
        taskId = null;
		
		dr: c.DateRange = {
				begin: null,
				finish: null
			};
		
		constructor(){
			super();
			let self = this;
			self.startupProcessingInformation.subscribe((v:any)=>{
				_.each(v.scheModifyAuthCtrlByPerson, (e:any) => {
					if(e.functionNo == 1){
						self.visibleA1_1(e.isAvailable);
					}
					if(e.functionNo == 2){
						self.visibleA1_4(e.isAvailable);
					}
				});
				_.each(v.scheModifyAuthCtrlCommon, (e:any) => {
					if(e.functionNo == 1){
						self.visibleA1_3(e.isAvailable);
					}
					if(e.functionNo == 2){
						self.visibleA1_2(e.isAvailable);
					}
				});
			});
		}
		
		created() {
			const vm = this;
			
			vm.getfirst();
			
			vm.employeeId = ko.observableArray([vm.$user.employeeId]);
			
			initEvent();

			vm.enable = ko.computed({
				read: () => {
					const bdate = ko.unwrap(vm.baseDate);

					return !!bdate && !!bdate.begin && !!bdate.finish;
				},
				owner: vm
			});

			vm.currentUser = ko.computed({
				read: () => {
					const bName = ko.unwrap(vm.startupProcessingInformation);

					return `${vm.$i18n('KSU002_20')}&nbsp;&nbsp;&nbsp;&nbsp;${bName ? bName.employeeCode : ''}&nbsp;&nbsp;${bName ? bName.businessName : ''}`;
				},
				owner: vm
			});

			// call to api and get data
			vm.baseDate
				.subscribe((d: c.DateRange) => {
					if (!d) {
						vm.baseDateKCP015(null);
						vm.dr.begin = null;
						vm.dr.finish = null;
						vm.getPlansResultsData(false);	
						// reset memento
						vm.schedules.reset();

						return;
					}

					const { begin, finish } = d;

					if (!begin || !finish) {
						vm.baseDateKCP015(null);
						vm.dr.begin = null;
						vm.dr.finish = null;
						vm.getPlansResultsData(false);
						// reset memento
						vm.schedules.reset();

						return;
					}

					if (moment(begin).isSame(vm.dr.begin, 'date') && moment(finish).isSame(vm.dr.finish, 'date')) {
						vm.baseDateKCP015(null);
						return;
					}

					vm.dr.begin = begin;
					vm.dr.finish = finish;
					vm.baseDateKCP015(vm.dr.finish);
					if(vm.readyLoadData){
						vm.loadData();
					}else{
						vm.readyLoadData = true;
					}
				});

			// UI-4
			vm.achievement
				.subscribe((arch) => {
					const { IMPRINT } = EDIT_STATE;
					const schedules: DayDataMementoObsv[] = ko.unwrap(vm.schedules);

						vm.loadData();
						return;
				});
		}
		
		rebidingData(){
			let vm = this;
			if(vm.achievement() === ACHIEVEMENT.NO){
//				vm.bidingData();
			}else{
//				vm.bidingDataDaily();
			}
		}
		
		loadData(){
			let vm = this;	
			
			vm.$errors('clear')
				.then(() => vm.$blockui('grayout'))
				.then(() => vm.getPlansResultsData(false))
				.then((response: WorkSchedule<string>[]) => _.chain(response)
					.orderBy(['date'])
					.map(m => ({
						...m,
						date: moment(m.date, 'YYYY/MM/DD')
					}))
					.value()
				)
				.then((response: WorkSchedule<moment.Moment>[]) => {
					if (response && response.length) {
						const { NO } = ACHIEVEMENT;
						const arch = ko.unwrap(vm.achievement);
						const clones: DayDataMementoObsv[] = ko.unwrap(vm.schedules);

						_.each(response, ($raw) => {
							const exits = _.find(clones, c => $raw.date.isSame(c.date, 'date'));

							if (exits) {
								const { IMPRINT } = EDIT_STATE;
								const {
									date,
									endTimeEditState,
									startTimeEditState,
									workTimeEditStatus,
									workTypeEditStatus,
									workHolidayCls,
									confirmed,
									achievements,
									needToWork,
									dateInfoDuringThePeriod,
									workTimeForm
								} = $raw;

								const {
									workTypeName,
									workTimeName,
									workTypeCode,
									workTimeCode,
									startTime,
									endTime
								} = arch === NO ? $raw : (achievements || $raw);

								// hack i18n
								_.extend(names, { [workTypeName]: workTypeName });
								_.extend(names, { [workTimeName]: workTimeName });

								exits.data = {
									$raw: {
										...$raw,
										date: date.toDate()
									},
									wtype: {
										code: ko.observable(workTypeCode),
										name: ko.observable(workTypeName)
									},
									wtime: {
										code: ko.observable(workTimeCode),
										name: ko.observable(workTimeName)
									},
									value: {
										begin: ko.observable(startTime),
										finish: ko.observable(endTime),
										validate: ko.observable(true),
										required: ko.observable(needToWork ? WORKTIME_SETTING.REQUIRED : WORKTIME_SETTING.OPTIONAL)
									},
									workTimeForm: ko.observable(workTimeForm),
									holiday: ko.observable(null),
									event: ko.observable(null),
									specialDay: ko.observable(false),
									confirmed: ko.observable(confirmed),
									achievement: ko.observable(arch === NO ? null : !!achievements),
									classification: ko.observable(workHolidayCls),
									need2Work: ko.observable(needToWork),
									state: {
										value: {
											begin: ko.observable(startTimeEditState ? startTimeEditState.editStateSetting : IMPRINT),
											finish: ko.observable(endTimeEditState ? endTimeEditState.editStateSetting : IMPRINT),
										},
										wtime: ko.observable(workTimeEditStatus ? workTimeEditStatus.editStateSetting : IMPRINT),
										wtype: ko.observable(workTypeEditStatus ? workTypeEditStatus.editStateSetting : IMPRINT)
									}
								};
								//exits.data.holiday(workTimeForm);
								if (dateInfoDuringThePeriod) {
									const {
										holidayName,
										specificDay,
										listSpecDayNameCompany,
										listSpecDayNameWorkplace,
										optCompanyEventName,
										optWorkplaceEventName
									} = dateInfoDuringThePeriod;

									// What is this???
									if (holidayName) {
										exits.data.holiday(holidayName);
									}

									if (specificDay) {
										exits.data.specialDay(specificDay);
									}

									if (specificDay || !!optCompanyEventName || !!optWorkplaceEventName) {
										const events: string[] = [];
										events.push('<table><colgroup><col width="90"></col><col width="10"></col></colgroup><tbody>');

										events.push(`<tr class="cen"><td>${vm.$i18n('KSU001_4014')}</td><td>${vm.$i18n('KSU001_4018')}</td><td>${!!optCompanyEventName ? _.escape(optCompanyEventName) : vm.$i18n('KSU001_4019')}</td></tr>`);

										events.push(`<tr class="wen"><td>${vm.$i18n('KSU001_4015')}</td><td>${vm.$i18n('KSU001_4018')}</td><td>${!!optWorkplaceEventName ? _.escape(optWorkplaceEventName) : vm.$i18n('KSU001_4019')}</td></tr>`);

										events.push(`<tr class="sdc"><td rowspan="${listSpecDayNameCompany.length || 1}">${vm.$i18n('KSU001_4016')}</td><td>${vm.$i18n('KSU001_4018')}</td><td>${!!listSpecDayNameCompany.length ? _.escape(_.head(listSpecDayNameCompany) || '') : vm.$i18n('KSU001_4019')}</td></tr>`);

										if (!!listSpecDayNameCompany.length) {
											_.each(listSpecDayNameCompany, (v, i) => {
												if (!!i) {
													events.push(`<tr class="sdc"><td></td><td>${_.escape(v)}</td></tr>`)
												}
											});
										}

										events.push(`<tr class="swc"><td rowspan="${listSpecDayNameWorkplace.length || 1}">${vm.$i18n('KSU001_4017')}</td><td>${vm.$i18n('KSU001_4018')}</td><td>${!!listSpecDayNameWorkplace.length ? _.escape(_.head(listSpecDayNameWorkplace) || '') : vm.$i18n('KSU001_4019')}</td></tr>`);

										if (!!listSpecDayNameWorkplace.length) {
											_.each(listSpecDayNameWorkplace, (v, i) => {
												if (!!i) {
													events.push(`<tr class="swc"><td></td><td>${_.escape(v)}</td></tr>`)
												}
											});
										}

										events.push('</tbody></table>');
										exits.data.event(events.join(''));
									}
								}
							}
						});
						vm.schedules(clones);
						vm.schedules.reset();
					}
				}).always(() => vm.$blockui('hide'));
			};
		
		getfirst(){
			let self = this;
			let sv = self.$ajax('at','screen/ksu/ksu002/getStartupProcessingInformation');
			//<<Query>> 週の管理を取得する
			let sv1 = self.$ajax('at', API.GET_START_DAY_OF_WEEK);
			let sv2 = self.$ajax('at', '/screen/ksu/ksu002/getListOfPeriodsClose')
				.fail((error:any)=>{
					self.$dialog.error(error);
				});
			let sv3 = self.$window.storage("KSU002.USER_DATA");
			
			$.when(sv, sv1, sv2, sv3).done((data: any, data1: any, data2: any, data3: any)=>{
				self.startupProcessingInformation(data);
				if(data3){
					self.storageDataStartWeek(data3.fdate);
				}
				self.dayStartWeek(data1.dayOfWeek);	
				if(self.readyLoadData){
					self.loadData();
				}else{
					self.readyLoadData = true;					
				}
				if(data2){
					self.listOfPeriodsClose(data2);
				}
			});
		}
		
		getPlansResultsData(getDaily: boolean): JQueryPromise<any>{
			let vm = this;
			let dfd = $.Deferred<void>();
			const { begin, finish } = vm.baseDate();
			vm.legalworkinghours(null);
			vm.plansResultsData(null);
			if(!begin || !finish){
				return;
			}
			const command = {
				listSid: [vm.$user.employeeId],
				startDate: moment(begin).format('YYYY/MM/DD'),
				endDate: moment(finish).format('YYYY/MM/DD'),
				actualData: vm.achievement() === ACHIEVEMENT.YES
			};
			
			let sv1 = vm.$ajax('at','screen/ksu/ksu002/getPlansResults', command).done((data:any) => {
				if(getDaily){
					dfd.resolve(data.workScheduleWorkDaily);
				}else{
					dfd.resolve(data.workScheduleWorkInfor2);					
				}
			});
			let sv2 = vm.$ajax('at','screen/ksu/ksu002/getlegalworkinghours', command);
			$.when(sv1, sv2).done((data1: any, data2: any) => {
				vm.legalworkinghours(data2);
				vm.plansResultsData(data1);
				vm.plansResultsData.valueHasMutated();
			});
			return dfd.promise();
		}
		
		
		mounted() {
		}
		
		openB() {
			let vm = this;
			const { begin, finish } = vm.baseDate();
			let shareData = {
                startDate: moment(begin).format('YYYY/MM/DD'),//対象期間開始日
                endDate: moment(finish).format('YYYY/MM/DD'),//対象期間終了日
                employeeCode: vm.startupProcessingInformation().employeeCode,//社員コード
                employeeName: vm.startupProcessingInformation().businessName,//社員名
                targetDate: moment(vm.yearMonth(), 'YYYYMMDD').format('YYYY/MM/DD'),//対象年月
                startDay: 0, //起算曜日
                isStartingDayOfWeek: false
            }
			vm.$window.storage("KSU002.USER_DATA").done(data => {
				shareData.startDay = data.fdate;
				shareData.isStartingDayOfWeek = vm.dayStartWeek() === data.fdate;
				vm.$window.storage("ksu002B_params", shareData).then(() => {
		            nts.uk.ui.windows.sub.modal('/view/ksu/002/b/index.xhtml');
		        });				
			});
		}
		// UI-8: Undo-Redoの処理
		undoOrRedo(action: 'undo' | 'redo') {
			const vm = this;

			if (action === 'undo') {
				vm.schedules.undo();
			} else if (action === 'redo') {
				vm.schedules.redo();
			}
		}

		// edit data on copy mode
		clickDayCell(type: c.CLICK_CELL, dayData: DayDataRawObsv) {
			const vm = this;
			const mode = ko.unwrap(vm.mode);
			const { REQUIRED } = WORKTIME_SETTING;
			const workData = ko.unwrap(vm.workData);
			const preview: DayData = ko.toJS(dayData);

			if (type === 'info' && mode === 'copy' && workData) {
				const { wtime, wtype } = workData;
				const wrap: DayDataRawObsv[] = ko.unwrap(vm.schedules);
				const current = _.find(wrap, f => moment(f.date).isSame(preview.date, 'date'));

				if (current) {
					const { confirmed, achievement, need2Work } = current.data;

					if (!ko.unwrap(confirmed) && !ko.unwrap(achievement) && !!ko.unwrap(need2Work)) {
						const cloned: DayData = ko.toJS(current);

						// UI-5: 不正な勤務情報の貼り付けのチェック
						if (wtype.type === REQUIRED && (wtime.code === 'none' || (wtime.code === 'deferred' && !cloned.data.wtime.code))) {
							vm.$dialog.error({ messageId: 'Msg_1809' });
						} else {
							// UI-5: エラーがならない場合は、常に勤務情報を勤務予定セルに反映する。
							$.Deferred()
								.resolve(true)
								// change data
								.then(() => {
									const { data } = current;

									data.wtype.code(wtype.code);
									data.wtype.name(wtype.name);
									data.value.required(wtype.type);
									data.classification(wtype.style);

									if (wtime.code === 'none') {
										data.wtime.code(null);
										data.wtime.name(null);

										if (data.value.begin() !== null) {
											data.value.begin(null);
										} else {
											data.value.begin.valueHasMutated();
										}

										if (data.value.finish() !== null) {
											data.value.finish(null);
										} else {
											data.value.finish.valueHasMutated();
										}
									} else if (wtime.code !== 'deferred') {
										data.wtime.code(wtime.code);
										data.wtime.name(wtime.name);

										if (data.value.begin() !== wtime.value.begin) {
											data.value.begin(wtime.value.begin);
										} else {
											data.value.begin.valueHasMutated();
										}

										if (data.value.finish() !== wtime.value.finish) {
											data.value.finish(wtime.value.finish);
										} else {
											data.value.finish.valueHasMutated();
										}
									}
								})
								.then(() => vm.compare(cloned, current))
								// save to memento after change data
								.then(() => vm.memento(current, cloned));
						}
					}
				}
			}
		}

		// edit data on edit mode
		changeDayCell(current: DayData) {
			const vm = this;
			const wrap: DayDataRawObsv[] = ko.unwrap(vm.schedules);
			const preview = _.find(wrap, f => moment(f.date).isSame(current.date, 'date'));

			if (preview) {
				const cloned: DayData = ko.toJS(preview);

				$.Deferred()
					.resolve(true)
					.then(() => {
						const { data } = preview;
						const { wtime, wtype, value } = current.data;

						data.wtime.code(wtime.code);
						data.wtime.name(wtime.name);

						data.wtype.code(wtype.code);
						data.wtype.name(wtype.name);

						data.value.begin(value.begin);
						data.value.finish(value.finish);
					})
					.then(() => vm.compare(cloned, preview))
					// save to memento after change data
					.then(() => vm.memento(preview, cloned));
			}
		}

		saveData() {
			const vm = this;

			// Remove validate all (accept error data in old cells)
			// vm.$validate()
			$.Deferred()
				// get valid flag from kiban viewmodel
				.resolve(vm.$validate.valid())
				.then((valid: boolean) => {
					if (valid) {
						const sid = vm.$user.employeeId;
						const registerDates: StorageData[] = [];
						const command = { sid, registerDates };
						const schedules = ko.unwrap(vm.schedules);
						let schedulesFilter = schedules;
						if(!vm.isSelectedStartWeek()){
							schedulesFilter = _.filter(schedulesFilter, function(n:any) {
								let date: Date = new Date(moment(n.date).format("YYYY/MM/DD"));
							  	return date >= new Date(moment(vm.dr.begin).format("YYYY/MM/DD")) && 
									date <= new Date(moment(vm.dr.finish).format("YYYY/MM/DD"));
							});
						}
						
						_.each(schedulesFilter, ({ date, data }) => {
							const {
								$raw,
								wtime,
								wtype,
								value,
								achievement
							} = data;
							const {
								workTypeCode,
								workTimeCode,
								startTime,
								endTime
							} = ko.unwrap(achievement) ? $raw.achievements : $raw;
							const start = ko.unwrap(value.begin) as number;
							const end = ko.unwrap(value.finish) as number;
							const workTimeCd = ko.unwrap(wtime.code);
							const workTypeCd = ko.unwrap(wtype.code);

							if (workTypeCd !== workTypeCode || workTimeCd !== workTimeCode || start !== startTime || end !== endTime) {
								
								registerDates.push({ date, end, start, workTimeCode: workTimeCd, workTypeCode: workTypeCd });
							}
						});

						vm.$blockui('show')
							.then(() => vm.$ajax('at', API.SAVE_DATA, command))
                            .then((rs: any) => {
                                vm.taskId = rs.taskInfor.id;
                                vm.checkStateAsyncTask();
                            });
							// reload data
							// .then(() => vm.achievement.valueHasMutated())
							//.always(() => vm.$blockui('clear'));
					}
				});
        }

        checkStateAsyncTask() {
            let vm = this;

            nts.uk.deferred.repeat(conf => conf
                .task(() => {
                    return nts.uk.request.asyncTask.getInfo(vm.taskId).done(function(res: any) {
                        // finish task
                        if (res.succeeded || res.failed || res.cancelled) {
                            vm.$blockui('clear');
                            let arrayItems = [];
                            let dataResult: any = {};
                            dataResult.listErrorInfo = [];
                            dataResult.hasError = false;
                            dataResult.isRegistered = true;
                            _.forEach(res.taskDatas, item => {
                                if (item.key == 'STATUS_REGISTER') {
                                    dataResult.isRegistered = item.valueAsBoolean;
                                } else if (item.key == 'STATUS_ERROR') {
                                    dataResult.hasError = item.valueAsBoolean;
                                } else {
                                    arrayItems.push(item);
                                }
                            });

                            if (arrayItems.length > 0) {
                                let listErrorInfo = _.map(arrayItems, obj2 => {
                                    return new InforError(JSON.parse(obj2.valueAsString));
                                });
                                dataResult.listErrorInfo = listErrorInfo;
                            }

                            vm.achievement(vm.achievement());
                            vm.achievement.valueHasMutated();
                            if (!dataResult.listErrorInfo.length) {
                                return vm.$dialog.info({ messageId: 'Msg_15' }).then(() => { vm.schedules.reset(true); });
                            } else {
                                const { listErrorInfo, registered } = dataResult;
                                const params = {
                                    errorRegistrationList: listErrorInfo,
                                    employeeIds: [vm.$user.employeeId],
                                    isRegistered: Number(registered)
                                };
                                setShared('dataShareDialogKDL053', params);
                                // call KDL053
                                try {
                                    vm.kdl053Open.close();
                                }
                                catch (exception_var) { }

                                vm.kdl053Open = nts.uk.ui.windows.sub.modeless('at', '/view/kdl/053/a/index.xhtml');
                                return vm.kdl053Open;
                            }
                        }
                    });
                }).while(infor => {
                    return infor.pending || infor.running;
                }).pause(1000));
        }

		// check state & memento data
		private memento(current: DayDataSave2Memento, preview: DayDataSave2Memento) {
			const vm = this;
			const c: DayData = ko.toJS(current);
			const p: DayData = ko.toJS(preview);

			// prevent if save data twice time
			if (c.data.wtype.code !== p.data.wtype.code
				|| c.data.wtime.code !== p.data.wtime.code
				|| c.data.value.begin !== p.data.value.begin
				|| c.data.value.finish !== p.data.value.finish) {
				vm.schedules.memento({ current, preview });
			}
		}

		private compare(cloned: DayData, current: DayDataRawObsv) {
			const { state } = current.data;
			const changed: DayData = ko.toJS(current);

			if (changed.data.wtype.code !== cloned.data.wtype.code) {
				state.wtype(EDIT_STATE.HAND_CORRECTION_MYSELF);
				// if change time code, change all state
				state.wtime(EDIT_STATE.HAND_CORRECTION_MYSELF);
				state.value.begin(EDIT_STATE.HAND_CORRECTION_MYSELF);
				state.value.finish(EDIT_STATE.HAND_CORRECTION_MYSELF);
			}

			if (changed.data.wtime.code !== cloned.data.wtime.code) {
				state.wtime(EDIT_STATE.HAND_CORRECTION_MYSELF);
				// if change time code, change all state
				state.wtype(EDIT_STATE.HAND_CORRECTION_MYSELF);
				state.value.begin(EDIT_STATE.HAND_CORRECTION_MYSELF);
				state.value.finish(EDIT_STATE.HAND_CORRECTION_MYSELF);
			}

			if (changed.data.value.begin !== cloned.data.value.begin) {
				state.value.begin(EDIT_STATE.HAND_CORRECTION_MYSELF);
			}

			if (changed.data.value.finish !== cloned.data.value.finish) {
				state.value.finish(EDIT_STATE.HAND_CORRECTION_MYSELF);
			}
		}
	}
	
	function initEvent(): void {
        //click btnA5
        $('#A5_1').ntsPopup({
            position: {
                my: 'left top',
                at: 'left bottom+3',
                of: $('#A5')
            }
        });

        $('#A5').click(function() {
            $('#A5_1').ntsPopup("toggle");
        });
    }

	interface StorageData {
		date: string | Date;
		workTypeCode: string;
		workTimeCode: string;
		start: number;
		end: number;
	}

	// 
	interface HandlerResult {
		hasError: boolean;
		listErrorInfo: ErrorInformation[];
		registered: boolean;
	}

	interface ErrorInformation {
		sid: string;
		scd: string;
		empName: string;
		date: Date | string;
		attendanceItemId: number | null;
		errorMessage: string;
	}

	interface Achievement {
		// 勤務種類コード
		workTypeCode: string;
		// 勤務種類名
		workTypeName: string;
		// 就業時間帯コード
		workTimeCode: string;
		// 就業時間帯名
		workTimeName: string;
		// 開始時刻
		startTime: null | number;
		// 終了時刻
		endTime: null | number;
	}

	interface WorkSchedule<D = Date> {
		// 実績か
		achievements: Achievement;
		// 確定済みか
		confirmed: boolean;
		// 年月日
		date: D;
		// 社員ID
		// employeeId: string;
		// データがあるか
		// haveData: boolean;
		// 勤務予定が必要か
		needToWork: boolean;
		supportCategory: number;
		// 出勤休日区分
		workHolidayCls: null | WORK_STYLE;
		// 勤務種類コード
		workTypeCode: string;
		// 勤務種類名
		workTypeName: string;
		// 就業時間帯コード
		workTimeCode: string;
		// 就業時間帯名
		workTimeName: string;
		// 開始時刻
		startTime: null | number;
		// 終了時刻
		endTime: null | number;
		// 勤務種類編集状態
		workTypeEditStatus: null | EditStateOfDailyAttd;
		// 就業時間帯編集状態
		workTimeEditStatus: null | EditStateOfDailyAttd;
		// 開始時刻編集状態
		startTimeEditState: null | EditStateOfDailyAttd;
		// 終了時刻編集状態
		endTimeEditState: null | EditStateOfDailyAttd;
		// Data info for event daisy (sakura)
		dateInfoDuringThePeriod: DateInfoDuringThePeriod;
		workTimeForm: null | number;
	}

	interface DateInfoDuringThePeriod {
		// 祝日であるか
		holidayName: string;
		// 特定日であるか
		specificDay: boolean;
		// 会社の特定日名称リスト
		listSpecDayNameCompany: string[];
		// 職場の特定日名称リスト
		listSpecDayNameWorkplace: string[];
		// 会社行事名称
		optCompanyEventName: string | null;
		//  職場行事名称
		optWorkplaceEventName: string | null;
	}

    interface EditStateOfDailyAttd {
        // 勤怠項目ID
        attendanceItemId: number;
        // 編集状態: 日別実績の編集状態
        editStateSetting: EDIT_STATE;
    }

    interface IError {
        sid: string,
        scd: string,
        empName: string,
        date: string,
        attendanceItemId: string,
        errorMessage: string,
    }

    export class InforError {
        sid: string;
        scd: string;
        empName: string;
        date: string;
        attendanceItemId: string;
        errorMessage: string;
        constructor(param: IError) {
            let self = this;
            self.sid = param.sid;
            self.scd = param.scd;
            self.empName = param.empName;
            self.date = param.date;
            self.attendanceItemId = param.attendanceItemId;
            self.errorMessage = param.errorMessage;
        }
    }
}
function calculateDaysStartEndWeek(start: Date, end: Date, settingDayStart: number, isSelectedSettingDayStart: boolean): ({start: Date, end: Date}){
	if(isSelectedSettingDayStart){
		const locale = moment.locale();
		moment.updateLocale(locale, {
			week: {
				dow: settingDayStart == 7 ? 0: settingDayStart,
				doy: 0
			}
		});
		let nStart = moment(start).startOf('week')
		let nEnd = moment(end).endOf('week')
		moment.updateLocale(locale, {
			week: {
				dow: 0,
				doy: 0
			}
		});
		return {start: nStart.toDate(), end: nEnd.toDate()};
	}else{
		return {start: start, end: end};
	}
}
function converNumberToTime(number: number): string{
	if (-60 < number &&  number < 0) {
		return ('-' + Math.ceil(number/60).toString() +':'+ (Math.abs(number%60) == 0 ? '00': (Math.abs(number%60)).toString())); 
	}
	return (number < 0 ? Math.ceil(number/60).toString():Math.floor(number/60).toString()) +':'+ (Math.abs(number%60) == 0 ? '00': (Math.abs(number%60)).toString()); 
}