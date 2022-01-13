module nts.uk.at.view.kdw013.h {
	import getShared = nts.uk.ui.windows.getShared;
	import setShared = nts.uk.ui.windows.setShared;
	import block = nts.uk.ui.block;
	import error = nts.uk.ui.dialog.error;
	import info = nts.uk.ui.dialog.info;
	import errors = nts.uk.ui.errors;
	import ajax = nts.uk.request.ajax;
	import getText = nts.uk.resource.getText;
	import getMessage = nts.uk.resource.getMessage;

	export module viewmodel {
		const paths: any = {
			start: "screen/at/kdw013/h/start",
			save: "screen/at/kdw013/h/save",
			getWorkPlaceId: "screen/at/kdw013/h/getWorkPlaceId",
			getWorkType: "screen/at/kdw013/h/getWorkType",
			reload: "screen/at/kdw013/h/reloadData"
		}
		export class ScreenModel {
			itemId28: ItemValue;
			itemId29: ItemValue;

			itemId31_34: StartEndTime;

			itemId157_159: StartEndTime;
			itemId163_165: StartEndTime;
			breakTimeBase: StartEndTime[] = [];

			itemId169_171: StartEndTime;
			itemId175_177: StartEndTime;
			itemId181_183: StartEndTime;
			itemId187_189: StartEndTime;
			itemId193_195: StartEndTime;
			itemId199_201: StartEndTime;
			itemId205_207: StartEndTime;
			itemId211_213: StartEndTime;
			breakTimeOptions: StartEndTime[] = [];

			isShowBreakTimeOptions: KnockoutObservable<boolean> = ko.observable(false);
			breakTimeOptionsText: KnockoutObservable<string> = ko.observable(getText('KDW013_92'));

			itemOptions: ItemValueOption[] = [];

			params: Param;
			dataMaster: DataMaster;
			primitiveValueDaily: any[] = __viewContext.enums.PrimitiveValueDaily;
			
			errorLable: KnockoutObservable<string> = ko.observable('');
			
			disableRegistration: KnockoutObservable<boolean> = ko.observable(true);

			constructor() {
				let self = this;
				self.params = getShared('KDW013H');
				self.setError();
				self.setBaseItems();// data co dinh
				self.orderOptionItems(); // data item tuy y
				self.isShowBreakTimeOptions.subscribe(v => {
					if(v){
						self.breakTimeOptionsText(getText('KDW013_93'));
					}else{
						self.breakTimeOptionsText(getText('KDW013_92'));
					}
				})
			}

			public startPage(): JQueryPromise<any> {
				let self = this;
				let dfd = $.Deferred();
				self.breakTimeBase.push(this.itemId157_159, this.itemId163_165);
				self.breakTimeOptions.push(
					this.itemId169_171, 
					this.itemId175_177, 
					this.itemId181_183, 
					this.itemId187_189, 
					this.itemId193_195,
					this.itemId199_201, 
					this.itemId205_207, 
					this.itemId211_213
				);
				block.invisible();
				let param = _.map(self.params.displayAttItems, i => i.attendanceItemId);
				param.push(28, 29, 31, 34, 157,159,163,165,169,171,175,177,181,183,187,189,193,195,199,201,205,207,211,213);
				ajax(paths.start, { itemIds: param }).done(function(data: DataMaster) {
					self.dataMaster = data;
					self.setDataMaster();
					self.setEnable();
					//console.log(data);
					self.setEnlableRegisterBotton();
					dfd.resolve();
					$(document).ready(function() {
		                $('.proceed').focus();
		            });
				}).fail(function(res: any) {
					error({ messageId: res.messageId });
				}).always(() => {
					block.clear();
				});
				return dfd.promise();
			}
			
			setEnlableRegisterBotton(){
				let self = this;
				let enableItems = false;
				let isMe = __viewContext.user.employeeId == self.params.employeeId;
				_.forEach(self.dataMaster.dailyAttendanceItemAuthority.displayAndInput, i =>{
					let use = isMe ? i.youCanChangeIt : i.canBeChangedByOthers;
					if(i.toUse && use){
						enableItems = true;	
					}
				});
				
				if(!enableItems){
					self.disableRegistration(true);
				}
			}
			
			setError(){
				let self = this;
				let infor: any = _.find(self.params.lockInfos, (i:DailyLock)=> moment(self.params.date).isSame(moment(i.date)));
				if(infor){
					let err: string = null;
					if(infor.lockDailyResult == 0){
						err = getText('KDW013_53');
					}
					if(infor.lockWpl == 0){
						err = err ? (err + ',' + getText('KDW013_54')) : getText('KDW013_54');
					}
					if(infor.lockApprovalMontｈ == 0){
						err = err ? (err + ',' + getText('KDW013_55')) : getText('KDW013_55');
					}
					if(infor.lockConfirmMonth == 0){
						err = err ? (err + ',' + getText('KDW013_56')) : getText('KDW013_56');
					}
					if(infor.lockApprovalDay == 0){
						err = err ? (err + ',' + getText('KDW013_57')) : getText('KDW013_57');
					}
					if(infor.lockConfirmDay == 0){
						err = err ? (err + ',' + getText('KDW013_58')) : getText('KDW013_58');
					}
					if(infor.lockPast == 0){
						err = err ? (err + ',' + getText('KDW013_59')) : getText('KDW013_59');
					}
					if(err){
						self.errorLable(getText('KDW013_52', [err]));
					}else{
						self.disableRegistration(false);
					}
				}
			}
			
			setEnable(){
				let self = this;
				let dailyAttendanceItemAuthority = self.dataMaster.dailyAttendanceItemAuthority;
				let isMe = __viewContext.user.employeeId == self.params.employeeId;
				if(dailyAttendanceItemAuthority){
					_.forEach(self.dataMaster.dailyAttendanceItemAuthority.displayAndInput, displayAndInput => {
						if(displayAndInput && displayAndInput.toUse){
							if(displayAndInput.itemDailyID == 28){
								self.itemId28.use = displayAndInput.toUse; 
								self.itemId28.enable = isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers;
							}else if(displayAndInput.itemDailyID == 29){
								self.itemId29.use = displayAndInput.toUse;
								self.itemId29.enable = isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers;
							}else if(displayAndInput.itemDailyID == 31 || displayAndInput.itemDailyID == 34){
								self.itemId31_34.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 157 || displayAndInput.itemDailyID == 159){
								self.itemId157_159.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 163 || displayAndInput.itemDailyID == 165){
								self.itemId163_165.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 169 || displayAndInput.itemDailyID == 171){
								self.itemId169_171.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 175 || displayAndInput.itemDailyID == 177){
								self.itemId175_177.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 181 || displayAndInput.itemDailyID == 183){
								self.itemId181_183.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 187 || displayAndInput.itemDailyID == 189){
								self.itemId187_189.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 193 || displayAndInput.itemDailyID == 195){
								self.itemId193_195.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 199 || displayAndInput.itemDailyID == 201){
								self.itemId199_201.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 205 || displayAndInput.itemDailyID == 207){
								self.itemId205_207.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else if(displayAndInput.itemDailyID == 211 || displayAndInput.itemDailyID == 213){
								self.itemId211_213.enable(displayAndInput.itemDailyID, displayAndInput.toUse, isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers);
							}else {
								let itemOption = _.find(self.itemOptions, item => item.itemId == displayAndInput.itemDailyID);
								if(itemOption){
									itemOption.use = displayAndInput.toUse;
									itemOption.enable = isMe ? displayAndInput.youCanChangeIt: displayAndInput.canBeChangedByOthers;
								}
							}
						}
					});
				}
			}

			setDataMaster() {
				let self = this;
				if (self.dataMaster && self.dataMaster.workTypes && self.itemId28.value()) {
					let workType = _.find(self.dataMaster.workTypes, w => w.workTypeCode == self.itemId28.value());
					if (workType){
						self.itemId28.itemSelectedDisplay(self.itemId28.value() + ' ' + workType.name);
					} else {
						self.itemId28.itemSelectedDisplay(self.itemId28.value() + ' ' + getText('KDW013_40'));
					}
				} else {
					self.itemId28.itemSelectedDisplay('');
				}

				if (self.dataMaster && self.dataMaster.workTimeSettings && self.itemId29.value()) {
					let workTime = _.find(self.dataMaster.workTimeSettings, w => w.worktimeCode == self.itemId29.value());
					if (workTime) {
						self.itemId29.itemSelectedDisplay(self.itemId29.value() + ' ' + workTime.workTimeDisplayName.workTimeName);
					} else {
						self.itemId29.itemSelectedDisplay(self.itemId29.value() + ' ' + getText('KDW013_40'));
					}
				} else if(self.itemId29.value() == null || self.itemId29.value() == ''){
					self.itemId29.itemSelectedDisplay(getText('KDW013_86'));
				}
				
				//set name và type cho item tuy ý
				_.forEach(self.itemOptions, item => {
					let itemName = _.find(self.dataMaster.attItemName, a => a.attendanceItemId == item.itemId);
					if(!itemName){
						item.use = false;
					}
					let itemType = _.find(self.dataMaster.dailyAttendanceItem, d => d.attendanceItemId == item.itemId);
					item.setUseNameType(itemName ? itemName.attendanceItemName : null, itemType ? itemType.dailyAttendanceAtr : null, self.getPrimitiveValue(itemType ? itemType.primitiveValue: null), itemType ? itemType.masterType: null);
					let option: Option[] = [];
					let divergenceReasonInputMethods = null;
					//trường hợp đặc biệt
					if (item.itemId == 438 || item.itemId == 439) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 1);
					} else if (item.itemId == 443 || item.itemId == 444) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 2);
					} else if (item.itemId == 448 || item.itemId == 449) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 3);
					} else if (item.itemId == 453 || item.itemId == 454) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 4);
					} else if (item.itemId == 458 || item.itemId == 459) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 5);
					} else if (item.itemId == 801 || item.itemId == 802) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 6);
					} else if (item.itemId == 806 || item.itemId == 807) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 7);
					} else if (item.itemId == 811 || item.itemId == 811) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 8);
					} else if (item.itemId == 816 || item.itemId == 817) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 9);
					} else if (item.itemId == 821 || item.itemId == 822) {
						divergenceReasonInputMethods = _.find(self.dataMaster.divergenceReasonInputMethods, d => d.divergenceTimeNo == 10);
					}

					if (divergenceReasonInputMethods){
						option = _.map(divergenceReasonInputMethods.reasons, (r: any) => { return { code: r.divergenceReasonCode, name: r.reason } });
						let selected = _.find(divergenceReasonInputMethods.reasons, (r: any) => r.divergenceReasonCode == item.value());
						if (!selected) {
							option = [{ code: item.value(), name: getText('KDW013_40') }, ... option];
						}
					}
					if (option.length > 0) {
						item.options = option;
					}
				});

			}
			
			getPrimitiveValue(primitiveValue: number): string {
				if(primitiveValue){
					if(primitiveValue == 21){
						return 'BusinessTypeCode';
					}else if(primitiveValue == 54){
						return 'AnyItemAmount';
					}else if(primitiveValue == 55){
						return 'AnyAmountMonth';
					}else if(primitiveValue == 56){
						return 'AnyItemTime';
					}else if(primitiveValue == 57){
						return 'AnyTimeMonth';
					}else if(primitiveValue == 58){
						return 'AnyItemTimes';
					}else if(primitiveValue == 59){
						return 'AnyTimesMonth';
					}else if(primitiveValue == 60){
						return 'DiverdenceReasonCode';
					}else{
						return _.find(this.primitiveValueDaily, (p: any) => p.value == primitiveValue).name.replace('Enum_PrimitiveValueDaily_','');	
					}
				}
				return '';
			}

			orderOptionItems() {
				let self = this;
				//order
				let tg: IItemValue[] = [];
				_.forEach(_.sortBy(self.params.displayAttItems, ['order']), (item) => {
					let e = _.find(self.params.itemValues, i => i.itemId == item.attendanceItemId);
					if (e) {
						tg.push(e)
					}
				});
				//set
				_.forEach(tg, (item) => {
					self.itemOptions.push(new ItemValueOption(item));
				});
			}

			setBaseItems(): void {
				let self = this;

				//fake data các item co dinh
				self.itemId28 = new ItemValue(_.find(self.params.itemValues, i => i.itemId == 28));
				self.itemId29 = new ItemValue(_.find(self.params.itemValues, i => i.itemId == 29));

				self.itemId31_34 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 31), _.find(self.params.itemValues, i => i.itemId == 34));

				self.itemId157_159 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 157), _.find(self.params.itemValues, i => i.itemId == 159), 1);
				self.itemId163_165 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 163), _.find(self.params.itemValues, i => i.itemId == 165), 2);

				self.itemId169_171 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 169), _.find(self.params.itemValues, i => i.itemId == 171), 3);
				self.itemId175_177 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 175), _.find(self.params.itemValues, i => i.itemId == 177), 4);
				self.itemId181_183 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 181), _.find(self.params.itemValues, i => i.itemId == 183), 5);
				self.itemId187_189 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 187), _.find(self.params.itemValues, i => i.itemId == 189), 6);
				self.itemId193_195 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 193), _.find(self.params.itemValues, i => i.itemId == 195), 7);
				self.itemId199_201 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 199), _.find(self.params.itemValues, i => i.itemId == 201), 8);
				self.itemId205_207 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 205), _.find(self.params.itemValues, i => i.itemId == 207), 9);
				self.itemId211_213 = new StartEndTime(_.find(self.params.itemValues, i => i.itemId == 211), _.find(self.params.itemValues, i => i.itemId == 213), 10);

			}
			
			openKdl001() {
                var self = this;
                setShared('kml001multiSelectMode', false);
                setShared('kml001selectAbleCodeList', []);
                setShared('kml001selectedCodeList', self.itemId29.value() ? [self.itemId29.value()]: []);
                setShared('kml001isSelection', true);
                setShared('kml001BaseDate', moment(self.params.date).format('YYYY/MM/DD'));
				block.grayout();
				ajax(paths.getWorkPlaceId, { employeeId: self.params.employeeId, date: moment(self.params.date) }).done(function(data: any) {
					setShared('kml001WorkPlaceId', data ? data.workPlaceId: null);
					nts.uk.ui.windows.sub.modal("/view/kdl/001/a/index.xhtml").onClosed(function () {
	                    const kml001selectedCodeList = getShared("kml001selectedCodeList");
							if(kml001selectedCodeList[0] && kml001selectedCodeList[0] != ''){
								self.itemId29.value(kml001selectedCodeList[0]);
								let workTime = _.find(self.dataMaster.workTimeSettings, w => w.worktimeCode == self.itemId29.value());
								if (workTime) {
									self.itemId29.itemSelectedDisplay(self.itemId29.value() + ' ' + workTime.workTimeDisplayName.workTimeName);
								} else {
									self.itemId29.itemSelectedDisplay(self.itemId29.value() + ' ' + getText('KDW013_40'));
								}	
							}else{
								self.itemId29.value(null);
								self.itemId29.itemSelectedDisplay(getText('KDW013_86'));
							}
	                    console.log(kml001selectedCodeList);
	                });
				}).fail(function(res: any) {
					error({ messageId: res.messageId });
				}).always(() => {
					block.clear();
				});
            }

			openKdl002() {
                var self = this;
	            setShared('KDL002_Multiple',false);
	            setShared('kdl002isSelection',true);
	            setShared('KDL002_SelectedItemId',self.itemId28.value() ? [self.itemId28.value()]: []);
	            setShared('KDL002_isShowNoSelectRow', false);
				ajax(paths.getWorkType, { employeeId: self.params.employeeId, date: moment(self.params.date), code: self.itemId28.value() }).done(function(data: any) {
					setShared('KDL002_AllItemObj', data);
		            nts.uk.ui.windows.sub.modal('/view/kdl/002/a/index.xhtml').onClosed(function(): any {
		                var lst = getShared('KDL002_SelectedNewItem');
							if(lst[0] && lst[0] != ''){
								self.itemId28.value(lst[0].code);
								let workType = _.find(self.dataMaster.workTypes, w => w.workTypeCode == self.itemId28.value());
								if (workType){
									self.itemId28.itemSelectedDisplay(self.itemId28.value() + ' ' + workType.name);
								} else {
									self.itemId28.itemSelectedDisplay(self.itemId28.value() + ' ' + getText('KDW013_40'));
								}
							}
		                console.log(lst);
		            });
				});
            }

			registration() {
				let self = this;
				let data: any[] = [];

				if (self.itemId28.isChange()) {
					data.push(self.itemId28.toDataSave());
				}

				if (self.itemId29.isChange()) {
					data.push(self.itemId29.toDataSave());
				}

				self.itemId31_34.toDataSave().forEach((e) => data.push(e));

				self.breakTimeBase.forEach((startEnd) => {
					startEnd.toDataSave().forEach((e) => data.push(e));
				});

				self.breakTimeOptions.forEach((startEnd) => {
					startEnd.toDataSave().forEach((e) => data.push(e));
				});

				self.itemOptions.forEach((item) => {
					if (item.use && item.isChange()){
						data.push(item.optionToDataSave());
					}
				});

				console.log(data);
				
				block.grayout();
				let param = {
					empTarget: self.params.employeeId, //対象社員
					targetDate: new Date(self.params.date), //対象日
					items: data //実績内容  => List<ItemValue> id và giá trị
				};
				ajax(paths.save, param).done((data: any) => {
					if(data.messageAlert == 'Msg_15'){
						info({ messageId: 'Msg_15' }).then(()=>{
							self.reLoadData();	
						});
					}else{
						_.forEach(data.errorMap, errs => {
							_.forEach(errs, err => {
								if(_.includes(err.message, 'Msg_')){
									errors.add({ 
										message: getMessage(err.message, self.getParamNameItemId(err.itemId)), 
										errorCode: err.message, 
										$control: $('#'+err.itemId+''), 
										location: null
									});
								}else{
									if($('#'+err.itemId+'').length){
										$('#'+err.itemId+'').ntsError('set', err.message);
									}else{
										error(err.message);	
									}
								}
							});
						});
					}
				}).fail(function(res: any) {
					error({ messageId: res.messageId });
				}).always(() => {
					block.clear();
				});
			}
			
			getParamNameItemId(itemId: any): any[]{
				if(itemId == 31 || itemId == 34){
					return [getText('KDW013_84'), getText('KDW013_85')];
				}else if(itemId == 157 || itemId == 159){
					return [getText('KDW013_88', [1]), getText('KDW013_89', [1, 1])];
				}else if(itemId == 163 || itemId == 165){
					return [getText('KDW013_88', [2]), getText('KDW013_89', [2, 2])];
				}else if(itemId == 169 || itemId == 171){
					return [getText('KDW013_88', [3]), getText('KDW013_89', [3, 3])];
				}else if(itemId == 175 || itemId == 177){
					return [getText('KDW013_88', [4]), getText('KDW013_89', [4, 4])];
				}else if(itemId == 181 || itemId == 183){
					return [getText('KDW013_88', [5]), getText('KDW013_89', [5, 5])];
				}else if(itemId == 187 || itemId == 189){
					return [getText('KDW013_88', [6]), getText('KDW013_89', [6, 6])];
				}else if(itemId == 193 || itemId == 195){
					return [getText('KDW013_88', [7]), getText('KDW013_89', [7, 7])];
				}else if(itemId == 199 || itemId == 201){
					return [getText('KDW013_88', [8]), getText('KDW013_89', [8, 8])];
				}else if(itemId == 205 || itemId == 207){
					return [getText('KDW013_88', [9]), getText('KDW013_89', [9, 9])];
				}else if(itemId == 211 || itemId == 213){
					return [getText('KDW013_88', [10]), getText('KDW013_89', [10, 10])];
				}
				
				return [itemId];
			}
			
			reLoadData(){
				let self = this;
				block.grayout();
				let param = {
					empTarget: self.params.employeeId, //対象社員
					targetDate: new Date(self.params.date), //対象日
					itemIds: _.map(self.params.displayAttItems, (i) => i.attendanceItemId) //勤怠項目リスト 
				};
				param.itemIds.push(28, 29);
				ajax(paths.reload, param).done((data: IItemValue[]) => {
					console.log(data);
					self.itemId28.value(_.find(data, i => i.itemId == 28).value);
					self.itemId28.valueBeforeChange = self.itemId28.value();
					let workType = _.find(self.dataMaster.workTypes, w => w.workTypeCode == self.itemId28.value());
					if (workType){
						self.itemId28.itemSelectedDisplay(self.itemId28.value() + ' ' + workType.name);
					} else {
						self.itemId28.itemSelectedDisplay(self.itemId28.value() + ' ' + getText('KDW013_40'));
					}
					self.itemId29.value(_.find(data, i => i.itemId == 29).value);
					self.itemId29.valueBeforeChange = self.itemId29.value();
					let workTime = _.find(self.dataMaster.workTimeSettings, w => w.worktimeCode == self.itemId29.value());
					if (workTime) {
						self.itemId29.itemSelectedDisplay(self.itemId29.value() + ' ' + workTime.workTimeDisplayName.workTimeName);
					} else if(self.itemId29.value() == null){
						self.itemId29.itemSelectedDisplay(getText('KDW013_86'));
					}
					
					_.forEach(self.itemOptions, (item) => {
						if(item.type == 2 && item.masterType == 9){
							item.value(_.find(data, i => i.itemId == item.itemId).value == 1);
						}else {
							item.value(_.find(data, i => i.itemId == item.itemId).value);
						}
						item.valueBeforeChange = item.value();
					});
				}).fail(function(res: any) {
					error({ messageId: res.messageId });
				}).always(() => {
					block.clear();
				});
			}

            /**
             * Close dialog
             */
			public closeDialog(): void {
				nts.uk.ui.windows.close();
			}

			showTime() {
				this.isShowBreakTimeOptions(!this.isShowBreakTimeOptions());
			}
		}
	}
	class StartEndTime {
		start: ItemValue;
		end: ItemValue;
		constructor(start: IItemValue, end: IItemValue, breakTimeNo?: number) {
			this.start = new ItemValue(start, breakTimeNo ? getText('KDW013_88', [breakTimeNo]) : undefined);
			this.end = new ItemValue(end, breakTimeNo ? getText('KDW013_89', [breakTimeNo, breakTimeNo]) : undefined);
			this.end.value.subscribe(() => {
			});
		}
		toDataSave(): IItemValue[] {
			let result: any[] = [];
			if (this.start.isChange()) {
				result.push(this.start.toDataSave());
			}
			if (this.end.isChange()) {
				result.push(this.end.toDataSave());
			}
			return result;
		}
		enable(itemId: number, use: boolean, enable: boolean){
			if(itemId == this.start.itemId){
				this.start.use = use;
				this.start.enable = enable;
			}else if(itemId == this.end.itemId){
				this.end.use = use;
				this.end.enable = enable;
			}
		}
	}
	class ItemValue {
		itemId: number;
		value: KnockoutObservable<any> = ko.observable(null);
		valueBeforeChange: any;
		valueType: number;
		layoutCode: string;
		fixed: boolean;
		itemSelectedDisplay: KnockoutObservable<string> = ko.observable('');
		use: boolean = false;
		name: string; //only use for break times
		enable: boolean = false;
		constructor(itemValue?: IItemValue, name?: string) {
			this.itemId = itemValue.itemId;
			this.value(itemValue.value);
			this.valueBeforeChange = itemValue.value;
			this.valueType = itemValue.valueType;
			this.layoutCode = itemValue.layoutCode;
			this.name = name;
			this.fixed = itemValue.fixed;
			
		}
		updateValue(itemValue: IItemValue){
			this.value(itemValue.value);
			this.valueBeforeChange = itemValue.value;
			this.valueType = itemValue.valueType;
			this.layoutCode = itemValue.layoutCode;
			this.fixed = itemValue.fixed;
		}
		toDataSave() {
			return {
				itemId: this.itemId,
				value: this.value(),
				valueType: this.valueType,
				layoutCode: this.layoutCode,
				isFixed: this.fixed
			};
		}
		isChange(): boolean {
			if(this.valueBeforeChange == null && this.value() == ''){
				return false;
			}
			return this.value() != this.valueBeforeChange;
		}
	}

	class ItemValueOption extends ItemValue {
		lable: KnockoutObservable<string> = ko.observable('');
		type: number;
		masterType: number = null;
		options: Option[] = [];
		primitiveValue: string;
		constructor(itemValue: IItemValue) {
			super(itemValue);
		}
		setUseNameType(name?: string, type?: number, primitiveValue?: string, masterType?: number) {
			this.lable(name);
			this.type = type;
			this.masterType = masterType;
			this.primitiveValue = primitiveValue;
			if(this.type == 2 && this.masterType == 9){
				this.value(this.value() == 1);
			}
		}
		optionToDataSave() {
			if(this.type == 2 && this.masterType == 9){
				return {
					itemId: this.itemId,
					value: this.value() ? 1 : 0,
					valueType: this.valueType,
					layoutCode: this.layoutCode,
					isFixed: this.fixed
				};
			}else{
				return this.toDataSave();				
			}
		}
	}
	type Option = {
		code: string;
		name: string;
	}

	type IItemValue = {
		itemId: number;
		value: any;
		valueType: number;
		layoutCode: string;
		fixed: boolean;
	}

	type Param = {
		employeeId: string; //対象社員
		date: string; //対象日
		displayAttItems: DisplayAttItem[]; //実績入力ダイアログ表示項目一覧  => List<表示する勤怠項目> id và thứ tự hiển thị
		itemValues: IItemValue[]; //実績内容  => List<ItemValue> id và giá trị
		lockInfos: DailyLock | null; //日別実績のロック状態 Optional<日別実績のロック状態>
	}

	type DataMaster = {
		attItemName: any[];
		dailyAttendanceItem: any[];
		divergenceReasonInputMethods: any[];
		divergenceTimeRoots: any[];
		workTimeSettings: any[];
		workTypes: any[];
		dailyAttendanceItemAuthority: {displayAndInput: DisplayAndInput[]};
	}
	
	type DisplayAndInput = {
		itemDailyID: number;
		toUse: boolean;
		youCanChangeIt: boolean;
		canBeChangedByOthers: boolean;
	}

	type DailyLock = {
		employeeId: string;
		date: string;
		lockDailyResult: number;
		lockWpl: number;
		lockApprovalMontｈ: number;
		lockConfirmMonth: number;
		lockApprovalDay: number;
		lockConfirmDay: number;
		lockPast: number;
	}

	type DisplayAttItem = {
		attendanceItemId: number;
		order: number;
	}


__viewContext.ready(function() {
	var screenModel = new viewmodel.ScreenModel();
	screenModel.startPage().done(function() {
		__viewContext.bind(screenModel);
	});
});
}
