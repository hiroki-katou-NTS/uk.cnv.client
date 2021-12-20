module nts.uk.at.view.kdl020.a {
    __viewContext.ready(function() {
        var screenModel = new nts.uk.at.view.kdl020.a.viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
		//$('#contents-all').css('display','');
        });
    });
}