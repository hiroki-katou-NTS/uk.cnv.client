module nts.uk.at.view.ksu003.ab {
	export module service {
        /**
         *  Service paths
         */
		let servicePath: any = {
			getTaskInfo : "screen/at/schedule/getTaskInfo",
			getTaskPallet: "screen/at/schedule/getTaskPallet",
			getTaskPaletteDisplay : "screen/at/schedule/getTaskPaletteDisplay",
			checkEmpAttendance : "screen/at/schedule/checkEmpAttendance",
		};

		/**
         * ①<<ScreenQuery>> 作業選択準備情報を取得する
         */
		export function getTaskInfo(command: any): JQueryPromise<any> {
			return nts.uk.request.ajax(servicePath.getTaskInfo, command);
		}

		/**
         * ①<<ScreenQuery>> 作業パレットを取得する
         */
		export function getTaskPallet(command: any): JQueryPromise<any> {
			return nts.uk.request.ajax(servicePath.getTaskPallet, command);
		}
		
		/**
         * ①<<Query>> 作業パレット表示情報を取得する
         */
		export function getTaskPaletteDisplay(command: any): JQueryPromise<any> {
        	return nts.uk.request.ajax(servicePath.getTaskPaletteDisplay, command);
   		}

		/**
         * ①<<ScreenQuery>> 社員の出勤系をチェックする
         */
		export function checkEmpAttendance(command: any): JQueryPromise<any> {
        	return nts.uk.request.ajax(servicePath.checkEmpAttendance, command);
   		}
	}
}