// i18n.js
import i18n from 'i18n-js';
import * as Localization from 'expo-localization';

import en from './assets/i18n/translations/buttons/en.json';
import id from './assets/i18n/translations/buttons/id.json';

i18n.translations = { en, id };
i18n.fallbacks = true;
i18n.locale = Localization.locale;

export default i18n;
