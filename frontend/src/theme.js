import { createTheme } from '@mui/material/styles';

/**
 * Shared Material-UI theme:
 * - Single calm purple/violet accent (#6c4bd1)
 * - Soft lavender background (#f6f4fb)
 * - Rounded cards (14px)
 * - Minimum 16px typography (1rem) for enhanced readability
 * - Touch targets of at least 44px for accessibility
 */
const theme = createTheme({
  palette: {
    primary: {
      main: '#6c4bd1',
      dark: '#4a32a0',
      light: '#9a7ff0',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#00897b',
      contrastText: '#ffffff',
    },
    background: {
      default: '#f6f4fb',
      paper: '#ffffff',
    },
    text: {
      primary: '#24203a',
      secondary: '#5c5875',
    },
  },
  shape: {
    borderRadius: 14,
  },
  typography: {
    fontSize: 16,
    htmlFontSize: 16,
    fontFamily: '"Segoe UI", Noto Sans, "Noto Sans Kannada", "Noto Sans Tamil", '
      + '"Noto Sans Telugu", sans-serif',
    h4: { fontWeight: 800, fontSize: '2rem' },
    h5: { fontWeight: 800, fontSize: '1.5rem' },
    h6: { fontWeight: 700, fontSize: '1.25rem' },
    subtitle1: { fontSize: '1.05rem', fontWeight: 600 },
    subtitle2: { fontSize: '1rem', fontWeight: 600 },
    body1: { fontSize: '1rem', lineHeight: 1.6 },
    body2: { fontSize: '1rem', lineHeight: 1.55 },
    button: { textTransform: 'none', fontWeight: 600, fontSize: '1rem' },
  },
  components: {
    MuiButton: {
      defaultProps: { disableElevation: true },
      styleOverrides: {
        root: {
          minHeight: 44,
          minWidth: 44,
          borderRadius: 10,
          padding: '8px 18px',
        },
      },
    },
    MuiIconButton: {
      styleOverrides: {
        root: {
          minHeight: 44,
          minWidth: 44,
        },
      },
    },
    MuiCard: {
      defaultProps: { elevation: 0 },
      styleOverrides: {
        root: {
          borderRadius: 14,
          border: '1px solid #ddd8ef',
        },
      },
    },
    MuiCardContent: {
      styleOverrides: {
        root: {
          padding: 20,
          '&:last-child': { paddingBottom: 20 },
        },
      },
    },
  },
});

export default theme;
