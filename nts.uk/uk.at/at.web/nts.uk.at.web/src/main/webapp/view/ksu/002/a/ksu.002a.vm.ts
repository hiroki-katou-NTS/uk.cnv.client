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
		UNAME: '/sys/portal/webmenu/username',
		GSCHE: '/screen/ksu/ksu002/displayInWorkInformation',
		GSCHER: '/screen/ksu/ksu002/getDataDaily',
		SAVE_DATA: '/screen/ksu/ksu002/regisWorkSchedule',
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
		baseDate: KnockoutObservable<c.DateRange | null> = ko.observable(null);
		schedules: m.MementoObservableArray<DayDataRawObsv> = ko.observableArray([]).extend({ memento }) as any;

		workplaceId: KnockoutObservable<string> = ko.observable('');
		achievement: KnockoutObservable<ACHIEVEMENT> = ko.observable(ACHIEVEMENT.NO);
		workData: KnockoutObservable<null | WorkData> = ko.observable(null);
		kdl053Open: any = null;
		created() {
			const vm = this;
			const dr: c.DateRange = {
				begin: null,
				finish: null
			};

			const loadData = () => {
				const command = {
					listSid: [vm.$user.employeeId],
					startDate: moment(dr.begin).toISOString(),
					endDate: moment(dr.finish).toISOString(),
					actualData: vm.achievement() === ACHIEVEMENT.YES
				};

				vm.$errors('clear')
					.then(() => vm.$blockui('show'))
					.then(() => vm.$ajax('at', API.GSCHE, command))
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
										dateInfoDuringThePeriod
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

							vm.$nextTick(() => {
								$(vm.$el).find('[data-bind]').removeAttr('data-bind');
							});
						}
					})
					.always(() => vm.$blockui('hide'));
			};

			const bussinesName: KnockoutObservable<string> = ko.observable('');

			vm.enable = ko.computed({
				read: () => {
					const bdate = ko.unwrap(vm.baseDate);

					return !!bdate && !!bdate.begin && !!bdate.finish;
				},
				owner: vm
			});

			vm.currentUser = ko.computed({
				read: () => {
					const bName = ko.unwrap(bussinesName);

					return `${vm.$i18n('KSU002_20')}&nbsp;&nbsp;&nbsp;&nbsp;${vm.$user.employeeCode}&nbsp;&nbsp;${bName}`;
				},
				owner: vm
			});

			vm.$ajax('com', API.UNAME)
				.then((name: string) => bussinesName(name));

			// call to api and get data
			vm.baseDate
				.subscribe((d: c.DateRange) => {
					if (!d) {
						dr.begin = null;
						dr.finish = null;

						// reset memento
						vm.schedules.reset();

						return;
					}

					const { begin, finish } = d;

					if (!begin || !finish) {
						dr.begin = null;
						dr.finish = null;

						// reset memento
						vm.schedules.reset();

						return;
					}

					if (moment(begin).isSame(dr.begin, 'date') && moment(finish).isSame(dr.finish, 'date')) {
						return;
					}

					dr.begin = begin;
					dr.finish = finish;

					loadData();
				});

			// UI-4
			vm.achievement
				.subscribe((arch) => {
					const { NO } = ACHIEVEMENT;
					const { IMPRINT } = EDIT_STATE;
					const { begin, finish } = vm.baseDate();

					const command = {
						listSid: [vm.$user.employeeId],
						startDate: moment(begin).toISOString(),
						endDate: moment(finish).toISOString()
					};

					const schedules: DayDataMementoObsv[] = ko.unwrap(vm.schedules);

					if (arch === ACHIEVEMENT.NO) {
						loadData();
						return;
					}

					$.Deferred()
						.resolve(true)
						.then(() => vm.$blockui('show'))
						// fc
						.then(() => vm.$ajax('at', API.GSCHER, command))
						.then((response: Achievement[]) => {
							if (response.length === 0) {
								return;
							}

							_.each(schedules, (sc) => {
								const { data } = sc;
								const { $raw } = data;
								const exist = _.find(response, (d: Achievement & { date: string; }) => moment(d.date, 'YYYY/MM/DD').isSame(sc.date, 'date'));

								if (!exist) {
									$raw.achievements = null;
								} else {
									const { endTime, startTime, workTimeCode, workTimeName, workTypeCode, workTypeName } = exist;

									$raw.achievements = {
										endTime,
										startTime,
										workTimeCode,
										workTimeName,
										workTypeCode,
										workTypeName
									};
								}
							});
						})
						.then(() => {
							// reset data
							_.each(schedules, (sc) => {
								const { data } = sc;
								const { $raw, wtype, wtime, value } = data;
								const { endTimeEditState, startTimeEditState, workTimeEditStatus, workTypeEditStatus } = $raw;

								// UI-4-1 実績表示を「する」に選択する
								// UI-4-2 実績表示を「しない」に選択する
								if (!!$raw.achievements) {
									const {
										workTypeCode,
										workTypeName,
										workTimeCode,
										workTimeName,
										startTime,
										endTime
									} = $raw.achievements;

									wtype.code(workTypeCode || null);
									wtype.name(workTypeName || null);

									wtime.code(workTimeCode || null);
									wtime.name(workTimeName || null);

									value.begin(startTime);
									value.finish(endTime);

									data.confirmed($raw.confirmed);
									data.achievement(null);
									data.classification($raw.workHolidayCls);
									data.need2Work($raw.needToWork);

									data.state.wtype(workTypeEditStatus ? workTypeEditStatus.editStateSetting : IMPRINT);
									data.state.wtime(workTimeEditStatus ? workTimeEditStatus.editStateSetting : IMPRINT);

									data.state.value.begin(startTimeEditState ? startTimeEditState.editStateSetting : IMPRINT);
									data.state.value.finish(endTimeEditState ? endTimeEditState.editStateSetting : IMPRINT);
								}

								// state of achievement (both data & switch select)
								data.achievement(!!$raw.achievements);
							});

							// reset state of memento
							vm.schedules.reset();
						})
						.always(() => vm.$blockui('clear'));
				});
		}

		mounted() {
			const vm = this;

			$(vm.$el).find('[data-bind]').removeAttr('data-bind');
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

						_.each(schedules, ({ date, data }) => {
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
							.then((info: HandlerResult) => {
								if (!info.listErrorInfo.length) {
									return vm.$dialog.info({ messageId: 'Msg_15' }).then(() => vm.schedules.reset(true));
								} else {
									const { listErrorInfo, registered } = info;
									const params = {
										errorRegistrationList: listErrorInfo,
										employeeIds: [sid],
										isRegistered: Number(registered)
									};
									setShared('dataShareDialogKDL053', params);
									// call KDL053
									try {
									  vm.kdl053Open.close();
									}
									catch (exception_var) {}
									
									vm.kdl053Open =  nts.uk.ui.windows.sub.modeless('at', '/view/kdl/053/a/index.xhtml');
									return vm.kdl053Open;
								}
							})
							// reload data
							// .then(() => vm.achievement.valueHasMutated())
							.always(() => vm.$blockui('clear'));
					}
				});
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
}