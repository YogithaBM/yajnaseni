/**
 * Simple translation dictionary for English, Hindi, and Kannada.
 */
export const TRANSLATIONS = {
  en: {
    appTitle: 'Sakhi',
    appFullName: 'Sakhi — Yajnaseni',
    navHome: 'Home',
    navSymptoms: 'Check Symptoms',
    navCalendar: 'Period Calendar',
    navSummary: 'Doctor Summary',
    navAbout: 'About & Safety',
    openMenu: 'Open navigation menu',
    closeMenu: 'Close navigation menu',
    languageSelect: 'Select language',
    langEnglish: 'English',
    langHindi: 'हिंदी',
    langKannada: 'ಕನ್ನಡ',
    disclaimer: 'This is information, not a diagnosis.',
    privacyNotice: 'No data is stored.',
    checkSymptoms: 'Check my symptoms',
    trackPeriod: 'Track my period',
    viewSummary: 'Doctor summary',
    aboutSafety: 'About & Safety guide',
  },
  hi: {
    appTitle: 'सखी',
    appFullName: 'सखी — यज्ञसेनी',
    navHome: 'होम',
    navSymptoms: 'लक्षण जांचें',
    navCalendar: 'मासिक धर्म कैलेंडर',
    navSummary: 'डॉक्टर सारांश',
    navAbout: 'बारे में और सुरक्षा',
    openMenu: 'नेविगेशन मेनू खोलें',
    closeMenu: 'नेविगेशन मेनू बंद करें',
    languageSelect: 'भाषा चुनें',
    langEnglish: 'English',
    langHindi: 'हिंदी',
    langKannada: 'ಕನ್ನಡ',
    disclaimer: 'यह केवल जानकारी है, कोई चिकित्सीय निदान नहीं।',
    privacyNotice: 'कोई भी डेटा स्टोर नहीं किया जाता है।',
    checkSymptoms: 'अपने लक्षण जांचें',
    trackPeriod: 'पीरियड ट्रैक करें',
    viewSummary: 'डॉक्टर सारांश देखें',
    aboutSafety: 'सुरक्षा और जानकारी',
  },
  kn: {
    appTitle: 'ಸಖಿ',
    appFullName: 'ಸಖಿ — ಯಾಜ್ಞಸೇನಿ',
    navHome: 'ಮುಖಪುಟ',
    navSymptoms: 'ಲಕ್ಷಣಗಳ ತಪಾಸಣೆ',
    navCalendar: 'ಋತುಚಕ್ರ ಕ್ಯಾಲೆಂಡರ್',
    navSummary: 'ವೈದ್ಯರ ಸಾರಾಂಶ',
    navAbout: 'ಬಗ್ಗೆ ಮತ್ತು ಸುರಕ್ಷತೆ',
    openMenu: 'ಮೆನು ತೆರೆಯಿರಿ',
    closeMenu: 'ಮೆನು ಮುಚ್ಚಿರಿ',
    languageSelect: 'ಭಾಷೆ ಆಯ್ಕೆಮಾಡಿ',
    langEnglish: 'English',
    langHindi: 'हिंदी',
    langKannada: 'ಕನ್ನಡ',
    disclaimer: 'ಇದು ಕೇವಲ ಮಾಹಿತಿ, ವೈದ್ಯಕೀಯ ರೋಗನಿರ್ಣಯವಲ್ಲ.',
    privacyNotice: 'ಯಾವುದೇ ಡೇಟಾವನ್ನು ಉಳಿಸಲಾಗುವುದಿಲ್ಲ.',
    checkSymptoms: 'ನನ್ನ ಲಕ್ಷಣಗಳನ್ನು ಪರಿಶೀಲಿಸಿ',
    trackPeriod: 'ಋತುಚಕ್ರ ದಿನಗಳನ್ನು ಗುರುತಿಸಿ',
    viewSummary: 'ವೈದ್ಯರ ಸಾರಾಂಶ',
    aboutSafety: 'ಮಾಹಿತಿ ಮತ್ತು ಸುರಕ್ಷತೆ',
  },
};

export const SUPPORTED_LANGUAGES = [
  { code: 'en', label: 'English' },
  { code: 'hi', label: 'हिंदी' },
  { code: 'kn', label: 'ಕನ್ನಡ' },
];

export function getTranslation(lang, key) {
  const active = TRANSLATIONS[lang] || TRANSLATIONS.en;
  return active[key] || TRANSLATIONS.en[key] || key;
}
