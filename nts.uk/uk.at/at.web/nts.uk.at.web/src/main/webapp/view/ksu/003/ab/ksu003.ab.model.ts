module nts.uk.at.view.ksu003.ab.model {
	import duration = nts.uk.time.minutesBased.duration; // convert time 
	import formatById = nts.uk.time.format.byId;
	
	export class ScheFunctionControlDto {
		changeableWorks: Array<number>; /** 時刻修正できる勤務形態 */
		useATR: number; /** 実績表示できるか */
		constructor(changeableWorks: Array<number>,
			useATR: number) {
			this.changeableWorks = changeableWorks;
			this.useATR = useATR;
		}
	}
}