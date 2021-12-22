module nts.uk.at.view.kmk003.a10 {
    export module service {

        /**
         *  Service paths
         */
        let servicePath: any = {
            getAllBonusPaySetting: "at/share/bonusPaySetting/getAllBonusPaySetting",
			getWTBPSetting: "at/share/wtBonusPaySetting/getWTBPSetting"
        };

        /**
         * function find all BonusPaySetting
         */
        export function findAllBonusPaySetting(): JQueryPromise<model.BonusPaySettingFindDto[]> {
            return nts.uk.request.ajax(servicePath.getAllBonusPaySetting);
        }

		export function getWTBPSetting(workTimeCode: string): JQueryPromise<any> {
            return nts.uk.request.ajax("at", servicePath.getWTBPSetting + '/' + workTimeCode);
        }

        /**
         * Data Model
         */
        export module model {
            export interface BonusPaySettingFindDto {
                code: string;
                name: string;
            }
        }
    }
}
