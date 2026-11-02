import { createTheme } from '@mui/material/styles';

/**
 * Shared Material-UI theme: purple accent for buttons/links, soft lavender
 * background, rounded cards. Kept in one place so every page looks the same.
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
    },
    background: {
      default: '#f6f4fb',
      paper: '#ffffff',
    },
    text: {
      primary: '#24203a',
      secondary: '#6b6883',
    },
  },
  shape: {
    borderRadius: 12,
  },
  typography: {
    fontFamily: '"Segoe UI", Noto Sans, "Noto Sans Kannada", "Noto Sans Tamil", '
      + '"Noto Sans Telugu", sans-serif',
    h4: { fontWeight: 700 },
    h5: { fontWeight: 700 },
    h6: { fontWeight: 700 },
    button: { textTransform: 'none', fontWeight: 600 },
  },
  components: {
    MuiButton: {
      defaultProps: { disableElevation: true },
    },
    MuiCard: {
      defaultProps: { elevation: 0 },
    },
    MuiCardContent: {
      styleOverrides: { root: { padding: 16, '&:last-child': { paddingBottom: 16 } } },
    },
  },
});

export default theme;
