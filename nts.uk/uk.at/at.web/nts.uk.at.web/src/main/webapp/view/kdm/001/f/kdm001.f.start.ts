module nts.uk.at.view.kdm001.f {
    __viewContext.ready(function() {
        let screenModel = new nts.uk.at.view.kdm001.f.viewmodel.ScreenModel();
            __viewContext.bind(screenModel);
            $("#F5").focus();
    });
}
