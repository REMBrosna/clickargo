import i18n from 'i18next';
import HttpApi from 'i18next-http-backend';
import { initReactI18next } from 'react-i18next';
import localStorageService from "app/services/localStorageService";

i18n
    .use(HttpApi)
    .use(initReactI18next)
    .init({
        lng: localStorageService.getItem("langPref") || 'kh',
        backend: {
            /* translation file path */
            loadPath: '/assets/i18n/translations/{{ns}}/{{lng}}.json'
        },
        fallbackLng: 'en',
        debug: false,
        /* can have multiple namespace, in case you want to divide a huge translation into smaller pieces and load them on demand */
        ns: ['common'],
        defaultNS: 'common',
        /* allows dot notation for keys */
        keySeparator: '.',
        interpolation: {
            escapeValue: false,
            formatSeparator: ','
        },
        react: {
            useSuspense: false
        }
    })


export default i18n;

