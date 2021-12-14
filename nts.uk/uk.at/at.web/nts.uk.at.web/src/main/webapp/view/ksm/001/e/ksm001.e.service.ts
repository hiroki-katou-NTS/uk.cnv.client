module nts.uk.at.view.ksm001.e {
    
    import UsageSettingDto = nts.uk.at.view.ksm001.a.service.model.UsageSettingDto;
    
    export module service {
        var paths = {
            saveCompanySettingEstimate: "ctx/at/schedule/shift/estimate/usagesetting/save"
        }

        /**
         * call service save UsageSettingDto
         */
        export function saveCompanySettingEstimate(dto: UsageSettingDto){
            return nts.uk.request.ajax('at', paths.saveCompanySettingEstimate, dto);
        }

    }
}