module nts.uk.ui.at.kdw013.share {
    const { countHalf } = nts.uk.text;

    @handler({
        bindingName: 'description'
    })
    export class DescriptionEditorBindingHandler implements KnockoutBindingHandler {
        init = (element: HTMLElement, valueAccessor: () => KnockoutObservable<string>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext) => {
            element.classList.add('nts-description');
            element.classList.add('ntsControl');
            const raw = valueAccessor();
            const name = allBindingsAccessor.get('name');
            const constraint = allBindingsAccessor.get('constraint');
            const hasError: KnockoutObservable<boolean> = allBindingsAccessor.get('hasError');

            const message = $('<div>', { 'class': 'message' }).appendTo(element).get(0);
            const textarea = $('<textarea style="resize: none;">', { 'class': 'nts-input' }).prependTo(element).on('blur', () => subscribe(value())).get(0);

            const text = ko.observable('');
            const value = ko.observable('');
            const subscribe = ($value: string) => {
                const $name = ko.unwrap(name);
                // get from constraint
                const { primitiveValueConstraints } = __viewContext;
                const primitive = primitiveValueConstraints[constraint];
                const maxLength = (primitive || { maxLength: 9999 }).maxLength || 9999;

                if (countHalf($value) > maxLength) {
                    if (ko.isObservable(hasError)) {
                        hasError(true);
                    }

                    element.classList.add('error');

                    const $mgs = viewModel.$i18n.message('MsgB_3', [viewModel.$i18n($name), `${maxLength}`]);

                    return text($mgs);
                }

                if (ko.isObservable(hasError)) {
                    hasError(false);
                }

                // emit out
                if ($value !== raw()) {
                    raw($value);
                }
            };

            viewModel
                .$validate
                .constraint(constraint)
                .then((value: vm.Constraint) => {
                    // console.log(value);
                });

            value
                .subscribe(subscribe);

            if (ko.isObservable(hasError)) {
                hasError
                    .subscribe((notValid) => {
                        if (!notValid) {
                            text('');
                            element.classList.remove('error');
                        }
                    });
            }

            raw
                // binding to view
                .subscribe((c) => {
                    if (c !== value()) {
                        value(c);
                    }
                });

            ko.applyBindingsToNode(textarea, {
                value,
                valueUpdate: 'input'
            }, bindingContext);
            ko.applyBindingsToNode(message, { text }, bindingContext);

            element.removeAttribute('data-bind');
        }
    }
}