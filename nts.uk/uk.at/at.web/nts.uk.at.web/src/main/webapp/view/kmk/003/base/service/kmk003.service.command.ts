module nts.uk.at.view.kmk003.a {
    import flexset = nts.uk.at.view.kmk003.a.service.model.flexset;
    import predset = nts.uk.at.view.kmk003.a.service.model.predset;
    import worktimeset = nts.uk.at.view.kmk003.a.service.model.worktimeset;
    export module service {
        export module model {
            export module command {
                export interface FlexWorkSettingSaveCommand {
                    flexWorkSetting: flexset.FlexWorkSettingDto;
                    predseting: predset.PredetemineTimeSettingDto;
                    worktimeSetting: worktimeset.WorkTimeSettingDto;
                }
            }
        }
    }
}