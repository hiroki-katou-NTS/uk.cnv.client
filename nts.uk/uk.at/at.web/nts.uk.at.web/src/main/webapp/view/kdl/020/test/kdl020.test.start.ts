
module nts.uk.at.view.kdl020.test {
 __viewContext.ready(function() {
		service.getSid().done((data : any) => {
			let screenModel = new viewmodel.ScreenModel(data);
			__viewContext.bind(screenModel);
		})
    });
}