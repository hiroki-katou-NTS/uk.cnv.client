/// <reference path="../viewcontext.d.ts" />

module nts.ui.icons {

    @handler({
        bindingName: 'svg-icon',
        validatable: true,
        virtual: false
    })
    export class SvgIconBindingHandler implements KnockoutBindingHandler {
        init(element: SVGElement, valueAccessor: () => string | KnockoutObservable<string>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            element.removeAttribute('data-bind');

            return { controlsDescendantBindings: false };
        }
        update(element: SVGElement, valueAccessor: () => string | KnockoutObservable<string>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext) {
            const icon: string = ko.unwrap(valueAccessor());
            const size: number = ko.unwrap(allBindingsAccessor.get('size'));
            const width: number = ko.unwrap(allBindingsAccessor.get('width'));
            const height: number = ko.unwrap(allBindingsAccessor.get('height'));

            if (element.tagName.toUpperCase() === 'SVG') {
                element.innerHTML = '';

                const div = document.createElement('div');

                $(element).replaceWith(div);

                div.innerHTML = `<svg fill="none" xmls="http://www.w3.org/2000/svg" width="${size || width || 32}" height="${size || height || 32}" viewBox="0 0 ${size || width || 32} ${size || height || 32}">${(_.get(nts.ui.icons, icon) || _.get(nts.ui.icons, (icon || '').replace(/_(UN)?SELECT/g, ''))) || ''}</svg>`;

                element.setAttribute('fill', 'none');
                element.setAttribute('width', `${size || width || 32}`);
                element.setAttribute('height', `${size || height || 32}`);
                element.setAttribute('viewBox', `0 0 ${size || width || 32} ${size || height || 32}`);
                element.setAttribute('xmlns', 'http://www.w3.org/2000/svg');

                _.each(div.firstElementChild.childNodes, (c: HTMLElement) => element.appendChild(c.cloneNode(true)));

                $(div).replaceWith(element);
            }
        }
    }

    export const CALENDAR_SWITCH = `<rect x="1.50195" y="1.09424" width="14.0589" height="14.0788" rx="1.5" fill="white" stroke="#30CC40"/>
    <path d="M7.08887 11.493H9.73723V10.8976H8.82922V6.88013H8.28063C8.0158 7.04308 7.70682 7.14963 7.27173 7.23111V7.68864H8.10407V10.8976H7.08887V11.493Z" fill="#30CC40"/>
    <path d="M1.00195 2.59424C1.00195 1.48967 1.89738 0.594238 3.00195 0.594238H14.0608C15.1654 0.594238 16.0608 1.48967 16.0608 2.59424V4.59955H1.00195V2.59424Z" fill="#30CC40"/>
    <rect x="18.4434" y="18.0154" width="14.0589" height="14.0788" rx="1.5" fill="white" stroke="#30CC40"/>
    <path d="M24.0163 28.2646H27.0115V27.6504H25.8513C25.6243 27.6504 25.3342 27.6692 25.0946 27.6943C26.0783 26.7604 26.7908 25.8391 26.7908 24.9491C26.7908 24.1155 26.2422 23.564 25.391 23.564C24.7856 23.564 24.3758 23.8272 23.9785 24.2534L24.3884 24.6545C24.6343 24.3725 24.937 24.1406 25.3027 24.1406C25.8198 24.1406 26.0909 24.4853 26.0909 24.9867C26.0909 25.7451 25.3847 26.6413 24.0163 27.8447V28.2646Z" fill="#30CC40"/>
    <path d="M17.9434 19.5154C17.9434 18.4108 18.8388 17.5154 19.9434 17.5154H31.0022C32.1068 17.5154 33.0022 18.4108 33.0022 19.5154V21.5207H17.9434V19.5154Z" fill="#30CC40"/>
    <path d="M32.9055 15.6753V4.36621L18.0845 4.36621" stroke="#30CC40" stroke-linecap="round"/>
    <path d="M21.7459 0.634398L18.019 4.36621L21.7459 8.09802" stroke="#30CC40" stroke-linecap="round"/>
    <path d="M1.0022 17.5154V28.8245L15.8232 28.8245" stroke="#30CC40" stroke-linecap="round"/>
    <path d="M12.1618 32.5563L15.8887 28.8245L12.1618 25.0926" stroke="#30CC40" stroke-linecap="round"/>`;

    export const ARROW_LEFT_SQUARE = `<rect width="20" height="20" rx="2" transform="matrix(-1 0 0 1 20 0)" fill="white"/>
    <path d="M14 10.5C14.2761 10.5 14.5 10.2761 14.5 10C14.5 9.72386 14.2761 9.5 14 9.5V10.5ZM5.64645 9.64645C5.45118 9.84171 5.45118 10.1583 5.64645 10.3536L8.82843 13.5355C9.02369 13.7308 9.34027 13.7308 9.53553 13.5355C9.7308 13.3403 9.7308 13.0237 9.53553 12.8284L6.70711 10L9.53553 7.17157C9.7308 6.97631 9.7308 6.65973 9.53553 6.46447C9.34027 6.2692 9.02369 6.2692 8.82843 6.46447L5.64645 9.64645ZM14 9.5H6V10.5H14V9.5Z" fill="#30CC40"/>`;

    export const ARROW_LEFT_SQUARE_DISABLE = `<rect x="0.5" y="1.4834" width="19" height="19" rx="1.5" fill="white" stroke="#EAE8F2"/>
    <path d="M14 11.4834C14.2761 11.4834 14.5 11.2595 14.5 10.9834C14.5 10.7073 14.2761 10.4834 14 10.4834V11.4834ZM5.64645 10.6298C5.45118 10.8251 5.45118 11.1417 5.64645 11.337L8.82843 14.5189C9.02369 14.7142 9.34027 14.7142 9.53553 14.5189C9.7308 14.3237 9.7308 14.0071 9.53553 13.8118L6.70711 10.9834L9.53553 8.15497C9.7308 7.95971 9.7308 7.64313 9.53553 7.44786C9.34027 7.2526 9.02369 7.2526 8.82843 7.44786L5.64645 10.6298ZM14 10.4834H6V11.4834H14V10.4834Z" fill="#C6C6D1"/>`;

    export const ARROW_RIGHT_SQUARE = `<rect x="0.5" y="1.4834" width="19" height="19" rx="1.5" fill="white" stroke="#30CC40"/>
    <path d="M6 11.4834C5.72386 11.4834 5.5 11.2595 5.5 10.9834C5.5 10.7073 5.72386 10.4834 6 10.4834V11.4834ZM14.3536 10.6298C14.5488 10.8251 14.5488 11.1417 14.3536 11.337L11.1716 14.5189C10.9763 14.7142 10.6597 14.7142 10.4645 14.5189C10.2692 14.3237 10.2692 14.0071 10.4645 13.8118L13.2929 10.9834L10.4645 8.15497C10.2692 7.95971 10.2692 7.64313 10.4645 7.44786C10.6597 7.2526 10.9763 7.2526 11.1716 7.44786L14.3536 10.6298ZM6 10.4834H14V11.4834H6V10.4834Z" fill="#30CC40"/>`;

    export const ARROW_RIGHT = `<path d="M1 4.5C0.723858 4.5 0.5 4.27614 0.5 4C0.5 3.72386 0.723858 3.5 1 3.5L1 4.5ZM9.35355 3.64645C9.54882 3.84171 9.54882 4.15829 9.35355 4.35355L6.17157 7.53553C5.97631 7.7308 5.65973 7.7308 5.46447 7.53553C5.2692 7.34027 5.2692 7.02369 5.46447 6.82843L8.29289 4L5.46447 1.17157C5.2692 0.976311 5.2692 0.659728 5.46447 0.464466C5.65973 0.269204 5.97631 0.269204 6.17157 0.464466L9.35355 3.64645ZM1 3.5L9 3.5V4.5H1L1 3.5Z" fill="#30CC40"/>`;

    export const AR = `<path d="M1.38867 4.59155C1.11253 4.59155 0.888672 4.3677 0.888672 4.09155C0.888672 3.81541 1.11253 3.59155 1.38867 3.59155L1.38867 4.59155ZM9.74223 3.738C9.93749 3.93326 9.93749 4.24984 9.74223 4.44511L6.56024 7.62709C6.36498 7.82235 6.0484 7.82235 5.85314 7.62709C5.65788 7.43182 5.65788 7.11524 5.85314 6.91998L8.68157 4.09155L5.85314 1.26313C5.65788 1.06786 5.65788 0.751281 5.85314 0.556019C6.0484 0.360757 6.36498 0.360757 6.56024 0.556019L9.74223 3.738ZM1.38867 3.59155L9.38867 3.59155V4.59155H1.38867L1.38867 3.59155Z" fill="#30CC40"/>`;

    export const PEOPLES = `<path fill-rule="evenodd" clip-rule="evenodd" d="M15.5445 4C14.9922 4 14.5445 4.44772 14.5445 5V11C14.5445 11.5523 14.9922 12 15.5445 12H21.5245C22.0768 12 22.5245 11.5523 22.5245 11V5C22.5245 4.44772 22.0768 4 21.5245 4H15.5445ZM12.9697 15.9992C12.4174 15.9992 11.9697 16.4469 11.9697 16.9992V31.9992H24.9372V16.9992C24.9372 16.4469 24.4894 15.9992 23.9372 15.9992H12.9697Z" fill="#30CC40"/>
    <path fill-rule="evenodd" clip-rule="evenodd" d="M9.55455 1H3.57459L3.57459 7H9.55455L9.55455 1ZM3.57459 0C3.02231 0 2.57459 0.447715 2.57459 1V7C2.57459 7.55228 3.0223 8 3.57459 8H9.55455C10.1068 8 10.5546 7.55228 10.5546 7V1C10.5546 0.447715 10.1068 0 9.55455 0H3.57459ZM11.9674 13.4119H1L1 27.4119H10V28.4119H1H0V27.4119V13.4119C0 12.8596 0.447715 12.4119 1 12.4119H11.9674C12.5197 12.4119 12.9674 12.8596 12.9674 13.4119V14.051H12.112C12.063 14.051 12.0148 14.0516 11.9674 14.053V13.4119ZM27 28.4119H35.9999H36.9999V27.4119V13.4119C36.9999 12.8596 36.5522 12.4119 35.9999 12.4119H25.0324C24.4801 12.4119 24.0324 12.8596 24.0324 13.4119V14.051H25.0324V13.4119H35.9999V27.4119H27V28.4119ZM27.6082 1H33.5882V7H27.6082V1ZM26.6082 1C26.6082 0.447715 27.0559 0 27.6082 0H33.5882C34.1404 0 34.5882 0.447715 34.5882 1V7C34.5882 7.55228 34.1404 8 33.5882 8H27.6082C27.0559 8 26.6082 7.55228 26.6082 7V1Z" fill="#30CC40"/>`;

    export const BUILDING = `<path fill-rule="evenodd" clip-rule="evenodd" d="M23 14.5C23 14.2239 23.2239 14 23.5 14H36.5C36.7761 14 37 14.2239 37 14.5V31.5C37 31.7761 36.7761 32 36.5 32H23.5C23.2239 32 23 31.7761 23 31.5V14.5ZM26 17H29V20H26V17ZM29 21H26V24H29V21ZM31 17H34V20H31V17ZM34 21H31V24H34V21Z" fill="white"/>
        <path fill-rule="evenodd" clip-rule="evenodd" d="M0.5 0C0.223858 0 0 0.223858 0 0.5V31.5C0 31.7761 0.223857 32 0.5 32H9V27H15V32H23.5C23.7761 32 24 31.7761 24 31.5V0.5C24 0.223858 23.7761 0 23.5 0H0.5ZM6 5H3V8H6V5ZM3 9H6V12H3V9ZM6 13H3V16H6V13ZM3 17H6V20H3V17ZM6 21H3V24H6V21ZM8 5H11V8H8V5ZM11 9H8V12H11V9ZM8 13H11V16H8V13ZM11 17H8V20H11V17ZM8 21H11V24H8V21ZM16 5H13V8H16V5ZM13 9H16V12H13V9ZM16 13H13V16H16V13ZM13 17H16V20H13V17ZM16 21H13V24H16V21ZM21 5H18V8H21V5ZM18 9H21V12H18V9ZM21 13H18V16H21V13ZM18 17H21V20H18V17ZM21 21H18V24H21V21Z" fill="white"/>`;

    export const NETWORK = `<path d="M20.0786 11.167L20.0786 15.6503" stroke="#30CC40" stroke-linecap="round"/>
        <path d="M6.08447 15.6509L6.08447 20.9198" stroke="#30CC40" stroke-linecap="round"/>
        <path d="M20.0786 15.6509L20.0786 20.9198" stroke="#30CC40" stroke-linecap="round"/>
        <path d="M33.918 15.6509L33.918 20.9198" stroke="#30CC40" stroke-linecap="round"/>
        <path d="M6.08447 15.6509L33.9171 15.6509" stroke="#30CC40" stroke-linecap="round"/>
        <rect x="14.5786" y="0.5" width="11" height="11" rx="0.5" fill="white" stroke="#30CC40"/>
        <rect x="14.0786" y="20" width="12" height="12" rx="1" fill="#30CC40"/>
        <rect x="0.000488281" y="20" width="12" height="12" rx="1" fill="#30CC40"/>
        <rect x="28.0005" y="20" width="12" height="12" rx="1" fill="#30CC40"/>`;

    export const COPY = `<rect x="0.5" y="0.5" width="15" height="15" rx="0.5" stroke="white"/>
        <rect x="5" y="4" width="16" height="16" rx="1" fill="white"/>`;

    export const PASTE = `<mask id="path-1-inside-1" fill="white">
        <rect y="2" width="16" height="16" rx="0.5"/>
        </mask>
        <rect y="2" width="16" height="16" rx="0.5" stroke="#30CC40" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" mask="url(#path-1-inside-1)"/>
        <rect x="5" y="6" width="16" height="16" rx="0.5" fill="#30CC40"/>
        <path d="M9 11H17" stroke="white" stroke-linecap="round"/>
        <path d="M9 13H17" stroke="white" stroke-linecap="round"/>
        <path d="M9 15H17" stroke="white" stroke-linecap="round"/>
        <path d="M9 17H17" stroke="white" stroke-linecap="round"/>
        <rect x="4" width="8" height="3" rx="1" fill="#30CC40"/>`;

    export const THREE_DOT = `<rect x="14" y="7" width="3" height="3" fill="#30CC40"/>
        <rect x="14" y="13" width="3" height="3" fill="#30CC40"/>
        <rect x="14" y="19" width="3" height="3" fill="#30CC40"/>`;

    export const PAINT = `<mask id="mask0" mask-type="alpha" maskUnits="userSpaceOnUse" x="3" y="3" width="9" height="10">
        <rect width="7" height="7" rx="1" transform="matrix(0.842033 0.539426 -0.535035 0.84483 6.48047 3.27661)" fill="#C4C4C4"/>
        </mask>
        <g mask="url(#mask0)">
        <path d="M7.55496 8.12151L12.3747 7.05259L8.62946 12.9664L2.73522 9.19042L7.55496 8.12151Z" fill="#30CC40"/>
        </g>
        <path fill-rule="evenodd" clip-rule="evenodd" d="M8.18265 5.55454L10.9977 7.35793L8.32254 11.5821L4.11237 8.88495L6.78603 4.6632L8.13598 5.52802C8.15123 5.53779 8.16681 5.54662 8.18265 5.55454ZM7.25711 3.7774C7.27923 3.78934 7.30107 3.80219 7.32258 3.81597L11.5327 6.5131C11.9978 6.81102 12.1352 7.43077 11.8397 7.89736L9.16457 12.1215C8.86908 12.5881 8.25254 12.7248 7.7875 12.4269L3.57734 9.72978C3.1123 9.43187 2.97485 8.81211 3.27034 8.34553L5.94399 4.12378L4.87616 3.4397C4.64364 3.29074 4.57492 2.98086 4.72266 2.74757C4.87041 2.51428 5.17867 2.44591 5.4112 2.59487L7.25711 3.7774Z" fill="#30CC40"/>
        <path d="M14.5448 10.6187C14.7042 11.3376 14.1778 12.0658 13.3691 12.2451C12.5603 12.4245 11.7754 11.9871 11.616 11.2682C11.4565 10.5493 12.503 8.34002 12.503 8.34002C12.503 8.34002 14.3854 9.89974 14.5448 10.6187Z" fill="#30CC40"/>`;

    export const LIST_PLUS = `<path d="M0.5 2C0.5 1.17157 1.17157 0.5 2 0.5H25C25.8284 0.5 26.5 1.17157 26.5 2V3.5H0.5V2Z" stroke="#30CC40"/>
        <path d="M0.5 2C0.5 1.17157 1.17157 0.5 2 0.5H9.5V3.5H0.5V2Z" fill="#30CC40" stroke="#30CC40"/>
        <rect x="0.5" y="8.5" width="26" height="3" stroke="#30CC40"/>
        <rect x="0.5" y="8.5" width="9" height="3" fill="#30CC40" stroke="#30CC40"/>
        <rect x="0.5" y="16.5" width="26" height="3" stroke="#30CC40"/>
        <rect x="0.5" y="16.5" width="9" height="3" fill="#30CC40" stroke="#30CC40"/>
        <path d="M0.5 24.5H26.5V26C26.5 26.8284 25.8284 27.5 25 27.5H2C1.17157 27.5 0.5 26.8284 0.5 26V24.5Z" stroke="#30CC40"/>
        <path d="M0.5 24.5H9.5V27.5H2C1.17157 27.5 0.5 26.8284 0.5 26V24.5Z" fill="#30CC40" stroke="#30CC40"/>
        <rect x="33.5" y="31.5" width="17" height="17" rx="1.5" transform="rotate(-180 33.5 31.5)" fill="#30CC40" stroke="#30CC40"/>
        <path d="M25 19.5173V26.4835" stroke="#F2FFCF" stroke-linecap="round"/>
        <path d="M21.5168 23L28.4831 23" stroke="#F2FFCF" stroke-linecap="round"/>`;

    export const PLUS_SQUARE = `<rect width="30" height="30" rx="2" fill="white"/>
        <path d="M15 10V20" stroke="#44E08C" stroke-width="2" stroke-linecap="square"/>
        <path d="M10 15L20 15" stroke="#44E08C" stroke-width="2" stroke-linecap="square"/>`;

    export const LOCATION = `<mask id="path-1-outside-1" maskUnits="userSpaceOnUse" x="2" y="0" width="8" height="10" fill="black">
        <rect fill="white" x="2" width="8" height="10"/>
        <path fill-rule="evenodd" clip-rule="evenodd" d="M6 9C6 9 9 6 9 4C9 2.34315 7.65685 1 6 1C4.34315 1 3 2.34315 3 4C3 6 6 9 6 9ZM6 5C5.44772 5 5 4.55228 5 4C5 3.44772 5.44772 3 6 3C6.55228 3 7 3.44772 7 4C7 4.55228 6.55228 5 6 5Z"/>
        </mask>
        <path fill-rule="evenodd" clip-rule="evenodd" d="M6 9C6 9 9 6 9 4C9 2.34315 7.65685 1 6 1C4.34315 1 3 2.34315 3 4C3 6 6 9 6 9ZM6 5C5.44772 5 5 4.55228 5 4C5 3.44772 5.44772 3 6 3C6.55228 3 7 3.44772 7 4C7 4.55228 6.55228 5 6 5Z" fill="#30CC40"/>
        <path d="M6 9L5.29289 9.70711C5.68342 10.0976 6.31658 10.0976 6.70711 9.70711L6 9ZM8 4C8 4.27342 7.88977 4.67189 7.63682 5.17779C7.39199 5.66745 7.05468 6.1771 6.7 6.65C6.34775 7.11967 5.99309 7.53366 5.72546 7.83104C5.59215 7.97916 5.48168 8.09695 5.40555 8.17671C5.36751 8.21656 5.33813 8.24682 5.31886 8.26653C5.30922 8.27638 5.30212 8.28358 5.29773 8.28802C5.29553 8.29024 5.29402 8.29176 5.2932 8.29258C5.2928 8.29299 5.29257 8.29322 5.29251 8.29327C5.29249 8.2933 5.29251 8.29328 5.29257 8.29322C5.2926 8.29319 5.29268 8.29311 5.2927 8.29309C5.29279 8.293 5.29289 8.29289 6 9C6.70711 9.70711 6.70723 9.70698 6.70737 9.70684C6.70743 9.70678 6.70758 9.70663 6.70771 9.70651C6.70795 9.70626 6.70824 9.70597 6.70858 9.70563C6.70925 9.70496 6.71011 9.7041 6.71114 9.70306C6.71321 9.70098 6.716 9.69817 6.71948 9.69465C6.72645 9.68761 6.73619 9.67772 6.74853 9.66511C6.77319 9.6399 6.80827 9.60375 6.85226 9.55766C6.94019 9.46555 7.0641 9.33334 7.21204 9.16896C7.50691 8.84134 7.90225 8.38033 8.3 7.85C8.69532 7.3229 9.10801 6.70755 9.42568 6.07221C9.73523 5.45311 10 4.72658 10 4H8ZM6 2C7.10457 2 8 2.89543 8 4H10C10 1.79086 8.20914 0 6 0V2ZM4 4C4 2.89543 4.89543 2 6 2V0C3.79086 0 2 1.79086 2 4H4ZM6 9C6.70711 8.29289 6.70721 8.293 6.7073 8.29309C6.70732 8.29311 6.7074 8.29319 6.70743 8.29322C6.70749 8.29328 6.70751 8.2933 6.70749 8.29327C6.70743 8.29322 6.7072 8.29299 6.7068 8.29258C6.70598 8.29176 6.70447 8.29024 6.70227 8.28802C6.69788 8.28358 6.69078 8.27638 6.68114 8.26653C6.66187 8.24682 6.63249 8.21656 6.59445 8.17671C6.51832 8.09695 6.40785 7.97916 6.27454 7.83104C6.00691 7.53366 5.65225 7.11967 5.3 6.65C4.94532 6.1771 4.60801 5.66745 4.36318 5.17779C4.11023 4.67189 4 4.27342 4 4H2C2 4.72658 2.26477 5.45311 2.57432 6.07221C2.89199 6.70755 3.30468 7.3229 3.7 7.85C4.09775 8.38033 4.49309 8.84134 4.78796 9.16896C4.9359 9.33334 5.05981 9.46555 5.14774 9.55766C5.19173 9.60375 5.22681 9.6399 5.25147 9.66511C5.26381 9.67772 5.27355 9.68761 5.28052 9.69465C5.284 9.69817 5.28679 9.70098 5.28886 9.70306C5.28989 9.7041 5.29075 9.70496 5.29142 9.70563C5.29176 9.70597 5.29205 9.70626 5.29229 9.70651C5.29242 9.70663 5.29257 9.70678 5.29263 9.70684C5.29277 9.70698 5.29289 9.70711 6 9ZM4 4C4 5.10457 4.89543 6 6 6V4H4ZM6 2C4.89543 2 4 2.89543 4 4H6V2ZM8 4C8 2.89543 7.10457 2 6 2V4H8ZM6 6C7.10457 6 8 5.10457 8 4H6V6Z" fill="white" mask="url(#path-1-outside-1)"/>
        <path d="M1 11H11" stroke="#30CC40" stroke-linecap="round" stroke-dasharray="3 3"/>`;

    export const FILE_PLUS = `<mask id="path-1-inside-1" fill="white">
        <path d="M0 1C0 0.734784 0.105357 0.48043 0.292893 0.292893C0.48043 0.105357 0.734784 0 1 0H5C5.1326 2.83187e-05 5.25975 0.0527253 5.3535 0.1465L7.8535 2.6465C7.94728 2.74025 7.99997 2.8674 8 3V9C8 9.26522 7.89464 9.51957 7.70711 9.70711C7.51957 9.89464 7.26522 10 7 10H1C0.734784 10 0.48043 9.89464 0.292893 9.70711C0.105357 9.51957 0 9.26522 0 9V1ZM6.793 3L5 1.207V3H6.793ZM4 1H1V9H7V4H4.5C4.36739 4 4.24021 3.94732 4.14645 3.85355C4.05268 3.75979 4 3.63261 4 3.5V1ZM4 5C4.13261 5 4.25979 5.05268 4.35355 5.14645C4.44732 5.24021 4.5 5.36739 4.5 5.5V6H5C5.13261 6 5.25979 6.05268 5.35355 6.14645C5.44732 6.24021 5.5 6.36739 5.5 6.5C5.5 6.63261 5.44732 6.75979 5.35355 6.85355C5.25979 6.94732 5.13261 7 5 7H4.5V7.5C4.5 7.63261 4.44732 7.75979 4.35355 7.85355C4.25979 7.94732 4.13261 8 4 8C3.86739 8 3.74021 7.94732 3.64645 7.85355C3.55268 7.75979 3.5 7.63261 3.5 7.5V7H3C2.86739 7 2.74021 6.94732 2.64645 6.85355C2.55268 6.75979 2.5 6.63261 2.5 6.5C2.5 6.36739 2.55268 6.24021 2.64645 6.14645C2.74021 6.05268 2.86739 6 3 6H3.5V5.5C3.5 5.36739 3.55268 5.24021 3.64645 5.14645C3.74021 5.05268 3.86739 5 4 5Z"/>
        </mask>
        <path d="M0 1C0 0.734784 0.105357 0.48043 0.292893 0.292893C0.48043 0.105357 0.734784 0 1 0H5C5.1326 2.83187e-05 5.25975 0.0527253 5.3535 0.1465L7.8535 2.6465C7.94728 2.74025 7.99997 2.8674 8 3V9C8 9.26522 7.89464 9.51957 7.70711 9.70711C7.51957 9.89464 7.26522 10 7 10H1C0.734784 10 0.48043 9.89464 0.292893 9.70711C0.105357 9.51957 0 9.26522 0 9V1ZM6.793 3L5 1.207V3H6.793ZM4 1H1V9H7V4H4.5C4.36739 4 4.24021 3.94732 4.14645 3.85355C4.05268 3.75979 4 3.63261 4 3.5V1ZM4 5C4.13261 5 4.25979 5.05268 4.35355 5.14645C4.44732 5.24021 4.5 5.36739 4.5 5.5V6H5C5.13261 6 5.25979 6.05268 5.35355 6.14645C5.44732 6.24021 5.5 6.36739 5.5 6.5C5.5 6.63261 5.44732 6.75979 5.35355 6.85355C5.25979 6.94732 5.13261 7 5 7H4.5V7.5C4.5 7.63261 4.44732 7.75979 4.35355 7.85355C4.25979 7.94732 4.13261 8 4 8C3.86739 8 3.74021 7.94732 3.64645 7.85355C3.55268 7.75979 3.5 7.63261 3.5 7.5V7H3C2.86739 7 2.74021 6.94732 2.64645 6.85355C2.55268 6.75979 2.5 6.63261 2.5 6.5C2.5 6.36739 2.55268 6.24021 2.64645 6.14645C2.74021 6.05268 2.86739 6 3 6H3.5V5.5C3.5 5.36739 3.55268 5.24021 3.64645 5.14645C3.74021 5.05268 3.86739 5 4 5Z" fill="black"/>
        <path d="M1 0V-1V0ZM5 0L5.00021 -1H5V0ZM5.3535 0.1465L4.64629 0.8535L4.64639 0.853607L5.3535 0.1465ZM7.8535 2.6465L7.14639 3.35361L7.1465 3.35371L7.8535 2.6465ZM8 3H9V2.99979L8 3ZM0 9H-1H0ZM6.793 3V4H9.20721L7.50011 2.29289L6.793 3ZM5 1.207L5.70711 0.499893L4 -1.20721V1.207H5ZM5 3H4V4H5V3ZM4 1H5V0H4V1ZM1 1V0H0V1H1ZM1 9H0V10H1V9ZM7 9V10H8V9H7ZM7 4H8V3H7V4ZM4.5 6H3.5V7H4.5V6ZM4.5 7V6H3.5V7H4.5ZM3.5 7H4.5V6H3.5V7ZM3.5 6V7H4.5V6H3.5ZM1 1L1 1L-0.414214 -0.414214C-0.789286 -0.0391407 -1 0.469567 -1 1H1ZM1 1L1 1V-1C0.469567 -1 -0.0391407 -0.789286 -0.414214 -0.414214L1 1ZM1 1H5V-1H1V1ZM4.99979 1C4.86719 0.999972 4.74003 0.947275 4.64629 0.8535L6.06071 -0.5605C5.77947 -0.841824 5.39801 -0.999915 5.00021 -1L4.99979 1ZM4.64639 0.853607L7.14639 3.35361L8.56061 1.93939L6.06061 -0.560607L4.64639 0.853607ZM7.1465 3.35371C7.05272 3.25997 7.00003 3.13281 7 3.00021L9 2.99979C8.99992 2.602 8.84183 2.22053 8.5605 1.93929L7.1465 3.35371ZM7 3V9H9V3H7ZM7 9L8.41421 10.4142C8.78929 10.0391 9 9.53043 9 9H7ZM7 9V11C7.53043 11 8.03914 10.7893 8.41421 10.4142L7 9ZM7 9H1V11H7V9ZM1 9H1L-0.414214 10.4142C-0.0391412 10.7893 0.469567 11 1 11V9ZM1 9H1H-1C-1 9.53043 -0.789286 10.0391 -0.414214 10.4142L1 9ZM1 9V1H-1V9H1ZM7.50011 2.29289L5.70711 0.499893L4.29289 1.91411L6.08589 3.70711L7.50011 2.29289ZM4 1.207V3H6V1.207H4ZM5 4H6.793V2H5V4ZM4 0H1V2H4V0ZM0 1V9H2V1H0ZM1 10H7V8H1V10ZM8 9V4H6V9H8ZM7 3H4.5V5H7V3ZM4.5 3C4.63261 3 4.75979 3.05268 4.85355 3.14645L3.43934 4.56066C3.72064 4.84196 4.10217 5 4.5 5V3ZM4.85355 3.14645C4.94732 3.24021 5 3.36739 5 3.5H3C3 3.89783 3.15804 4.27936 3.43934 4.56066L4.85355 3.14645ZM5 3.5V1H3V3.5H5ZM4 6C3.86739 6 3.74021 5.94732 3.64645 5.85355L5.06066 4.43934C4.77936 4.15804 4.39783 4 4 4V6ZM3.64645 5.85355C3.55268 5.75979 3.5 5.63261 3.5 5.5H5.5C5.5 5.10217 5.34196 4.72064 5.06066 4.43934L3.64645 5.85355ZM3.5 5.5V6H5.5V5.5H3.5ZM4.5 7H5V5H4.5V7ZM5 7C4.86739 7 4.74021 6.94732 4.64645 6.85355L6.06066 5.43934C5.77936 5.15804 5.39783 5 5 5V7ZM4.64645 6.85355C4.55268 6.75979 4.5 6.63261 4.5 6.5H6.5C6.5 6.10217 6.34196 5.72064 6.06066 5.43934L4.64645 6.85355ZM4.5 6.5C4.5 6.36739 4.55268 6.24021 4.64645 6.14645L6.06066 7.56066C6.34196 7.27936 6.5 6.89783 6.5 6.5H4.5ZM4.64645 6.14645C4.74021 6.05268 4.86739 6 5 6V8C5.39783 8 5.77936 7.84196 6.06066 7.56066L4.64645 6.14645ZM5 6H4.5V8H5V6ZM3.5 7V7.5H5.5V7H3.5ZM3.5 7.5C3.5 7.36739 3.55268 7.24022 3.64645 7.14645L5.06066 8.56066C5.34196 8.27936 5.5 7.89782 5.5 7.5H3.5ZM3.64645 7.14645C3.74022 7.05268 3.86739 7 4 7V9C4.39782 9 4.77935 8.84197 5.06066 8.56066L3.64645 7.14645ZM4 7C4.13261 7 4.25978 7.05268 4.35355 7.14645L2.93934 8.56066C3.22065 8.84197 3.60218 9 4 9V7ZM4.35355 7.14645C4.44732 7.24022 4.5 7.36739 4.5 7.5H2.5C2.5 7.89782 2.65804 8.27936 2.93934 8.56066L4.35355 7.14645ZM4.5 7.5V7H2.5V7.5H4.5ZM3.5 6H3V8H3.5V6ZM3 6C3.13261 6 3.25979 6.05268 3.35355 6.14645L1.93934 7.56066C2.22064 7.84196 2.60217 8 3 8V6ZM3.35355 6.14645C3.44732 6.24021 3.5 6.36739 3.5 6.5H1.5C1.5 6.89783 1.65804 7.27936 1.93934 7.56066L3.35355 6.14645ZM3.5 6.5C3.5 6.63261 3.44732 6.75979 3.35355 6.85355L1.93934 5.43934C1.65804 5.72064 1.5 6.10217 1.5 6.5H3.5ZM3.35355 6.85355C3.25979 6.94732 3.13261 7 3 7V5C2.60217 5 2.22064 5.15804 1.93934 5.43934L3.35355 6.85355ZM3 7H3.5V5H3V7ZM4.5 6V5.5H2.5V6H4.5ZM4.5 5.5C4.5 5.63261 4.44732 5.75979 4.35355 5.85355L2.93934 4.43934C2.65804 4.72064 2.5 5.10217 2.5 5.5H4.5ZM4.35355 5.85355C4.25979 5.94732 4.13261 6 4 6V4C3.60217 4 3.22064 4.15804 2.93934 4.43934L4.35355 5.85355Z" fill="#30CC40" mask="url(#path-1-inside-1)"/>`;

    export const PRINTER = `<path d="M2.36621 0.3125C2.36621 0.139911 2.49446 0 2.65267 0H7.31785C7.47605 0 7.60431 0.139911 7.60431 0.3125V2.07792H2.36621V0.3125Z" fill="#30CC40"/>
        <path fill-rule="evenodd" clip-rule="evenodd" d="M7.31785 0.3125H2.65267V1.76542H7.31785V0.3125ZM2.65267 0C2.49446 0 2.36621 0.139911 2.36621 0.3125V2.07792H7.60431V0.3125C7.60431 0.139911 7.47605 0 7.31785 0H2.65267Z" fill="#30CC40"/>
        <path d="M0 2.96265C0 2.61747 0.256504 2.33765 0.572917 2.33765H9.42708C9.74349 2.33765 10 2.61747 10 2.96265V7.5568C10 7.90198 9.7435 8.1818 9.42708 8.1818H0.572917C0.256504 8.1818 0 7.90198 0 7.5568V2.96265Z" fill="#30CC40"/>
        <path fill-rule="evenodd" clip-rule="evenodd" d="M9.42708 2.65015H0.572917C0.41471 2.65015 0.286458 2.79006 0.286458 2.96265V7.5568C0.286458 7.72939 0.41471 7.8693 0.572917 7.8693H9.42708C9.58529 7.8693 9.71354 7.72939 9.71354 7.5568V2.96265C9.71354 2.79006 9.58529 2.65015 9.42708 2.65015ZM0.572917 2.33765C0.256504 2.33765 0 2.61747 0 2.96265V7.5568C0 7.90198 0.256504 8.1818 0.572917 8.1818H9.42708C9.7435 8.1818 10 7.90198 10 7.5568V2.96265C10 2.61747 9.7435 2.33765 9.42708 2.33765H0.572917Z" fill="#30CC40"/>
        <path d="M2.38086 4.91064C2.38086 4.56547 2.63736 4.28564 2.95378 4.28564H7.04604C7.36245 4.28564 7.61895 4.56547 7.61895 4.91064V9.37493C7.61895 9.72011 7.36245 9.99993 7.04604 9.99993H2.95378C2.63736 9.99993 2.38086 9.72011 2.38086 9.37493V4.91064Z" fill="white"/>
        <path fill-rule="evenodd" clip-rule="evenodd" d="M7.04604 4.59814H2.95378C2.79557 4.59814 2.66732 4.73806 2.66732 4.91064V9.37493C2.66732 9.54752 2.79557 9.68743 2.95378 9.68743H7.04604C7.20424 9.68743 7.3325 9.54752 7.3325 9.37493V4.91064C7.3325 4.73806 7.20424 4.59814 7.04604 4.59814ZM2.95378 4.28564C2.63736 4.28564 2.38086 4.56547 2.38086 4.91064V9.37493C2.38086 9.72011 2.63736 9.99993 2.95378 9.99993H7.04604C7.36245 9.99993 7.61895 9.72011 7.61895 9.37493V4.91064C7.61895 4.56547 7.36245 4.28564 7.04604 4.28564H2.95378Z" fill="#30CC40"/>
        <path d="M8.00879 4.12939C8.00879 3.95681 8.13704 3.81689 8.29525 3.81689H8.58171C8.73991 3.81689 8.86816 3.95681 8.86816 4.12939V4.44189C8.86816 4.61448 8.73991 4.75439 8.58171 4.75439H8.29525C8.13704 4.75439 8.00879 4.61448 8.00879 4.44189V4.12939Z" fill="#F1FDFB"/>`;

    export const NO_IMAGE = `<rect width="106" height="106" rx="2" transform="matrix(-1 0 0 1 106 0)" fill="#EAE8F2"/>
        <path d="M23.9872 85.8994H25.8072V82.0384C25.8072 80.9334 25.6642 79.7244 25.5732 78.6844H25.6382L26.6132 80.7514L29.3952 85.8994H31.3452V76.2664H29.5252V80.1144C29.5252 81.2064 29.6812 82.4804 29.7722 83.4814H29.7072L28.7452 81.4014L25.9502 76.2664H23.9872V85.8994ZM36.6105 86.0814C38.4305 86.0814 40.1075 84.6774 40.1075 82.2594C40.1075 79.8414 38.4305 78.4374 36.6105 78.4374C34.7905 78.4374 33.1135 79.8414 33.1135 82.2594C33.1135 84.6774 34.7905 86.0814 36.6105 86.0814ZM36.6105 84.5214C35.6225 84.5214 35.0635 83.6374 35.0635 82.2594C35.0635 80.8944 35.6225 79.9974 36.6105 79.9974C37.5985 79.9974 38.1575 80.8944 38.1575 82.2594C38.1575 83.6374 37.5985 84.5214 36.6105 84.5214ZM44.8075 85.8994H46.7315V76.2664H44.8075V85.8994ZM48.9425 85.8994H50.8535V80.8944C51.3605 80.3484 51.8285 80.0754 52.2445 80.0754C52.9335 80.0754 53.2715 80.4654 53.2715 81.5964V85.8994H55.1695V80.8944C55.6895 80.3484 56.1575 80.0754 56.5735 80.0754C57.2625 80.0754 57.5875 80.4654 57.5875 81.5964V85.8994H59.4985V81.3624C59.4985 79.5294 58.7835 78.4374 57.2365 78.4374C56.2875 78.4374 55.5855 79.0094 54.9225 79.7114C54.5715 78.9054 53.9605 78.4374 52.9205 78.4374C51.9715 78.4374 51.2955 78.9574 50.6845 79.5944H50.6455L50.5025 78.6194H48.9425V85.8994ZM63.2668 86.0814C64.0988 86.0814 64.8268 85.6654 65.4638 85.1194H65.5158L65.6458 85.8994H67.2058V81.6484C67.2058 79.5424 66.2568 78.4374 64.4108 78.4374C63.2668 78.4374 62.2268 78.8664 61.3688 79.3994L62.0578 80.6604C62.7338 80.2704 63.3838 79.9714 64.0598 79.9714C64.9568 79.9714 65.2688 80.5174 65.3078 81.2324C62.3698 81.5444 61.1088 82.3634 61.1088 83.9234C61.1088 85.1584 61.9668 86.0814 63.2668 86.0814ZM63.8908 84.5864C63.3318 84.5864 62.9288 84.3394 62.9288 83.7674C62.9288 83.1174 63.5268 82.6234 65.3078 82.4024V83.8714C64.8398 84.3264 64.4368 84.5864 63.8908 84.5864ZM71.7145 89.0584C74.1455 89.0584 75.6795 87.9404 75.6795 86.4714C75.6795 85.1974 74.7175 84.6514 72.9625 84.6514H71.7275C70.8695 84.6514 70.5835 84.4434 70.5835 84.0534C70.5835 83.7544 70.7005 83.5984 70.8955 83.4294C71.2075 83.5334 71.5455 83.5984 71.8315 83.5984C73.3915 83.5984 74.6395 82.7664 74.6395 81.1284C74.6395 80.6734 74.4965 80.2704 74.3145 80.0234H75.5495V78.6194H72.9235C72.6245 78.5154 72.2345 78.4374 71.8315 78.4374C70.2845 78.4374 68.8935 79.3604 68.8935 81.0634C68.8935 81.9214 69.3615 82.6104 69.8685 82.9744V83.0264C69.4265 83.3384 69.0495 83.8454 69.0495 84.3914C69.0495 84.9894 69.3355 85.3664 69.7125 85.6134V85.6784C69.0495 86.0554 68.6725 86.5754 68.6725 87.1864C68.6725 88.4734 69.9985 89.0584 71.7145 89.0584ZM71.8315 82.4154C71.1945 82.4154 70.6875 81.9344 70.6875 81.0634C70.6875 80.2184 71.1815 79.7374 71.8315 79.7374C72.4815 79.7374 72.9755 80.2184 72.9755 81.0634C72.9755 81.9344 72.4685 82.4154 71.8315 82.4154ZM72.0005 87.8494C70.9475 87.8494 70.2715 87.4984 70.2715 86.8874C70.2715 86.5884 70.4145 86.3024 70.7395 86.0424C70.9995 86.1074 71.3115 86.1464 71.7535 86.1464H72.6375C73.4175 86.1464 73.8465 86.2764 73.8465 86.7964C73.8465 87.3684 73.0925 87.8494 72.0005 87.8494ZM79.8411 86.0814C80.7381 86.0814 81.6611 85.7694 82.3761 85.2754L81.7261 84.1054C81.2061 84.4304 80.6861 84.6124 80.0881 84.6124C79.0091 84.6124 78.2291 83.9884 78.0731 82.7924H82.5581C82.6101 82.6234 82.6491 82.2724 82.6491 81.9084C82.6491 79.9064 81.6091 78.4374 79.6071 78.4374C77.8781 78.4374 76.2141 79.9064 76.2141 82.2594C76.2141 84.6644 77.8001 86.0814 79.8411 86.0814ZM78.0471 81.5184C78.1901 80.4654 78.8661 79.9194 79.6331 79.9194C80.5691 79.9194 81.0111 80.5434 81.0111 81.5184H78.0471Z" fill="white"/>
        <rect x="45" y="20" width="16" height="16" rx="4" fill="white"/>
        <path d="M36 44.8994C36 42.6903 37.7909 40.8994 40 40.8994H65.3642C67.5733 40.8994 69.3642 42.6903 69.3642 44.8994V62.8994H36V44.8994Z" fill="white"/>`;


    export const SELECTED_UNSELECT = `<path d="M14.168 9.35705L6.78567 6.5993C6.73303 6.57952 6.67583 6.5753 6.62086 6.58713C6.56589 6.59896 6.51547 6.62635 6.47556 6.66605C6.43565 6.70575 6.40794 6.75609 6.39571 6.81111C6.38348 6.86612 6.38725 6.9235 6.40656 6.97643L9.17201 14.3893C9.19347 14.4468 9.23227 14.496 9.28299 14.5303C9.33371 14.5645 9.39383 14.5821 9.45496 14.5805C9.5161 14.5788 9.57521 14.5581 9.62405 14.5212C9.67289 14.4843 9.70904 14.4331 9.72745 14.3746L10.518 11.7966L13.1982 14.4188C13.2533 14.4737 13.3278 14.5045 13.4054 14.5045C13.483 14.5045 13.5575 14.4737 13.6126 14.4188L14.3032 13.7264C14.3579 13.6712 14.3887 13.5966 14.3887 13.5187C14.3887 13.4409 14.3579 13.3662 14.3032 13.311L11.6318 10.7006L14.168 9.9139C14.2258 9.89386 14.2759 9.85628 14.3113 9.80638C14.3467 9.75647 14.3658 9.69674 14.3658 9.63547C14.3658 9.57421 14.3467 9.51447 14.3113 9.46457C14.2759 9.41467 14.2258 9.37708 14.168 9.35705Z" fill="#30CC40"/>
        <mask id="path-2-inside-1" fill="white">
        <rect x="0.388672" y="0.580566" width="20" height="20" rx="0.5"/>
        </mask>
        <rect x="0.388672" y="0.580566" width="20" height="20" rx="0.5" stroke="#30CC40" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" stroke-dasharray="5 5" mask="url(#path-2-inside-1)"/>`

    export const SELECTED_SELECT = `<path d="M14.168 9.35705L6.78567 6.5993C6.73303 6.57952 6.67583 6.5753 6.62086 6.58713C6.56589 6.59896 6.51547 6.62635 6.47556 6.66605C6.43565 6.70575 6.40794 6.75609 6.39571 6.81111C6.38348 6.86612 6.38725 6.9235 6.40656 6.97643L9.17201 14.3893C9.19347 14.4468 9.23227 14.496 9.28299 14.5303C9.33371 14.5645 9.39383 14.5821 9.45496 14.5805C9.5161 14.5788 9.57521 14.5581 9.62405 14.5212C9.67289 14.4843 9.70904 14.4331 9.72745 14.3746L10.518 11.7966L13.1982 14.4188C13.2533 14.4737 13.3278 14.5045 13.4054 14.5045C13.483 14.5045 13.5575 14.4737 13.6126 14.4188L14.3032 13.7264C14.3579 13.6712 14.3887 13.5966 14.3887 13.5187C14.3887 13.4409 14.3579 13.3662 14.3032 13.311L11.6318 10.7006L14.168 9.9139C14.2258 9.89386 14.2759 9.85628 14.3113 9.80638C14.3467 9.75647 14.3658 9.69674 14.3658 9.63547C14.3658 9.57421 14.3467 9.51447 14.3113 9.46457C14.2759 9.41467 14.2258 9.37708 14.168 9.35705Z" fill="white"/>
        <mask id="path-2-inside-1" fill="white">
        <rect x="0.388672" y="0.580566" width="20" height="20" rx="0.5"/>
        </mask>
        <rect x="0.388672" y="0.580566" width="20" height="20" rx="0.5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" stroke-dasharray="5 5" mask="url(#path-2-inside-1)"/>`;

    export const PASTE_UNSELECT = `<mask id="path-1-inside-1" fill="white">
    <rect x="0.737305" y="2.58057" width="16" height="16" rx="0.5"/>
    </mask>
    <rect x="0.737305" y="2.58057" width="16" height="16" rx="0.5" stroke="#30CC40" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" mask="url(#path-1-inside-1)"/>
    <rect x="5.7373" y="6.58057" width="16" height="16" rx="0.5" fill="#30CC40"/>
    <path d="M9.7373 11.5806H17.7373" stroke="white" stroke-linecap="round"/>
    <path d="M9.7373 13.5806H17.7373" stroke="white" stroke-linecap="round"/>
    <path d="M9.7373 15.5806H17.7373" stroke="white" stroke-linecap="round"/>
    <path d="M9.7373 17.5806H17.7373" stroke="white" stroke-linecap="round"/>
    <rect x="4.7373" y="0.580566" width="8" height="3" rx="1" fill="#30CC40"/>`;

    export const PASTE_SELECT = `<mask id="path-1-inside-1" fill="white">
    <rect x="0.737305" y="2.58057" width="16" height="16" rx="0.5"/>
    </mask>
    <rect x="0.737305" y="2.58057" width="16" height="16" rx="0.5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" mask="url(#path-1-inside-1)"/>
    <rect x="5.7373" y="6.58057" width="16" height="16" rx="0.5" fill="white"/>
    <path d="M9.7373 11.5806H17.7373" stroke="#30CC40" stroke-linecap="round"/>
    <path d="M9.7373 13.5806H17.7373" stroke="#30CC40" stroke-linecap="round"/>
    <path d="M9.7373 15.5806H17.7373" stroke="#30CC40" stroke-linecap="round"/>
    <path d="M9.7373 17.5806H17.7373" stroke="#30CC40" stroke-linecap="round"/>
    <rect x="4.7373" y="0.580566" width="8" height="3" rx="1" fill="white"/>`;
}