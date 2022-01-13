module nts.uk.at.view.kdl009.a {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
			setTimeout(function() {
                $("#closeDialog-id").focus();
            }, 1);
			//$("#cancel-btn").focus();
        });//end screenModel
    });
}
