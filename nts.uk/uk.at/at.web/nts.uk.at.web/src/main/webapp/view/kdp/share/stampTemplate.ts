module nts.uk.at.view.kdp010 {
	import getText = nts.uk.resource.getText;
	/* 
	numberTemplate = 
		1: 個人利用の打刻の設定 (Stamping settings for personal use)
		2: 共有利用の打刻の設定 (Setting of stamping for shared use)
		3: 応援移動の打刻の設定 (SSetting of stamping of cheering movement)
		4: 店舗内応援の打刻の設定 (Setting of stamping of support in the store)
	mode = 
		0: set Audio
		1: not set Audio
	return ButtonSettings refer numberTemplate
	*/
	export const GetStampTemplate = (numberTemplate: number, mode?: number):any =>{
		if(mode == undefined || mode == null) mode = 1;
		if(numberTemplate == 1){
			return [
				{
					buttonPositionNo: 1,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 0, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_250"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: mode == 0 ? 1 : 0
				},{
					buttonPositionNo: 2,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 1, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_254"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: mode == 0 ? 2 : 0
				},{
					buttonPositionNo: 3,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 0, 
							changeCalArt: 0, 
							setPreClockArt: 1, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_251"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: 0
				},{
					buttonPositionNo: 4,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 1, 
							changeCalArt: 0, 
							setPreClockArt: 2, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_255"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: 0
				},{
					buttonPositionNo: 5,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 4, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: 0},
						reservationArt: 0},
					taskChoiceArt: null,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_257"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: 0
				},{
					buttonPositionNo: 6,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 5, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: null,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_258"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: 0
				}
			];
		}else if(numberTemplate == 2){
			return [
				{
					buttonPositionNo: 1,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 6, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: 0,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_273"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: mode == 0 ? 1 : 0
				},{
					buttonPositionNo: 2,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 1, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_274"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: mode == 0 ? 2 : 0
				},{
					buttonPositionNo: 5,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 4, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: 0},
						reservationArt: 0},
					taskChoiceArt: null,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_257"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: 0
				},{
					buttonPositionNo: 6,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 5, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: null,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_258"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: 0
				}
			];			
		}else if(numberTemplate == 3){
			return [
				{
					buttonPositionNo: 1,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 6, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: 0,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_273"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: mode == 0 ? 1 : 0
				},{
					buttonPositionNo: 2,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 1, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_274"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: mode == 0 ? 2 : 0
				},{
					buttonPositionNo: 6,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 8, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_275",["{#Com_Workplace}"]), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: 0
				}
			];
		}else if(numberTemplate == 4){
			return [
				{
					buttonPositionNo: 1,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 0, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_250"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: mode == 0 ? 1 : 0
				},{
					buttonPositionNo: 2,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 1, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: null,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_274"), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: mode == 0 ? 2 : 0
				},{
					buttonPositionNo: 6,
					usrArt: 1,
					buttonType: {
						stampType: {
							changeClockArt: 6, 
							changeCalArt: 0, 
							setPreClockArt: 0, 
							changeHalfDay: false, 
							goOutArt: null},
						reservationArt: 0},
					taskChoiceArt: 0,
					supportWplSet: 1,
					buttonDisSet: {
						buttonNameSet: {
							buttonName: getText("KDP010_276",["{#Com_Workplace}"]), 
							textColor: "#ffffff"}, 
							backGroundColor: "#127D09"},
					audioType: 0
				}
			];
		}
	};
}