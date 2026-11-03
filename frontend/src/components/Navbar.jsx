import { useState, useCallback, useEffect } from 'react';
import { NavLink } from 'react-router-dom';
import {
  AppBar,
  Box,
  Button,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  MenuItem,
  Select,
  Toolbar,
  Typography,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';
import FavoriteIcon from '@mui/icons-material/Favorite';
import { useAppState } from '../context/AppContext.jsx';
import { SUPPORTED_LANGUAGES } from '../data/translations.js';

export const NAV_ITEMS = [
  { to: '/', labelKey: 'navHome', defaultLabel: 'Home', end: true },
  { to: '/symptoms', labelKey: 'navSymptoms', defaultLabel: 'Check Symptoms' },
  { to: '/calendar', labelKey: 'navCalendar', defaultLabel: 'Period Calendar' },
  { to: '/summary', labelKey: 'navSummary', defaultLabel: 'Doctor Summary' },
  { to: '/about', labelKey: 'navAbout', defaultLabel: 'About & Safety' },
];

export default function Navbar() {
  const { lang, setLang, t } = useAppState();
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = useCallback(() => {
    setMobileOpen((prev) => !prev);
  }, []);

  const handleDrawerClose = useCallback(() => {
    setMobileOpen(false);
  }, []);

  // Ensure Escape key always closes the menu
  useEffect(() => {
    function handleKeyDown(event) {
      if (event.key === 'Escape' && mobileOpen) {
        handleDrawerClose();
      }
    }
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [mobileOpen, handleDrawerClose]);

  return (
    <AppBar
      position="fixed"
      color="inherit"
      elevation={2}
      className="no-print"
      sx={{
        bgcolor: '#ffffff',
        borderBottom: '1px solid #e5e0f5',
        zIndex: (theme) => theme.zIndex.drawer + 1,
      }}
    >
      <Toolbar
        sx={{
          maxWidth: 1200,
          width: '100%',
          mx: 'auto',
          px: { xs: 2, sm: 3 },
          minHeight: 64,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: 1.5,
        }}
      >
        {/* Brand / Logo */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <FavoriteIcon
            sx={{ color: 'primary.main', fontSize: 26 }}
            aria-hidden="true"
          />
          <Typography
            variant="h6"
            component={NavLink}
            to="/"
            onClick={handleDrawerClose}
            sx={{
              color: 'primary.main',
              textDecoration: 'none',
              fontWeight: 800,
              fontSize: { xs: '1.15rem', sm: '1.25rem' },
              letterSpacing: '0.02em',
              whiteSpace: 'nowrap',
              display: 'flex',
              alignItems: 'baseline',
              gap: 0.5,
            }}
          >
            <span>{t('appTitle') || 'Sakhi'}</span>
            <Typography
              component="span"
              variant="caption"
              sx={{
                color: 'text.secondary',
                fontWeight: 600,
                display: { xs: 'none', sm: 'inline' },
              }}
            >
              Yajnaseni
            </Typography>
          </Typography>
        </Box>

        {/* Desktop Navigation (>= 768px: md breakpoint) */}
        <Box
          component="nav"
          aria-label="Main navigation"
          sx={{
            display: { xs: 'none', md: 'flex' },
            alignItems: 'center',
            gap: 0.5,
          }}
        >
          {NAV_ITEMS.map((item) => (
            <Button
              key={item.to}
              component={NavLink}
              to={item.to}
              end={item.end}
              color="primary"
              sx={{
                minHeight: 44,
                minWidth: 44,
                px: 1.75,
                fontSize: '0.95rem',
                fontWeight: 600,
                borderRadius: 2,
                color: 'text.primary',
                '&.active': {
                  bgcolor: 'rgba(108, 75, 209, 0.12)',
                  color: 'primary.main',
                  fontWeight: 700,
                },
                '&:hover': {
                  bgcolor: 'rgba(108, 75, 209, 0.06)',
                },
              }}
            >
              {t(item.labelKey) || item.defaultLabel}
            </Button>
          ))}
        </Box>

        {/* Controls: Language Switcher and Mobile Hamburger Button */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          {/* Language Switcher */}
          <Select
            size="small"
            value={lang || 'en'}
            onChange={(e) => setLang(e.target.value)}
            inputProps={{
              'aria-label': t('languageSelect') || 'Select language',
              id: 'language-selector',
            }}
            sx={{
              minHeight: 44,
              fontSize: '0.88rem',
              fontWeight: 600,
              borderRadius: 2,
              bgcolor: '#f8f6fc',
              '.MuiOutlinedInput-notchedOutline': { borderColor: '#ddd8ef' },
              '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: 'primary.main' },
            }}
          >
            {SUPPORTED_LANGUAGES.map((item) => (
              <MenuItem key={item.code} value={item.code} sx={{ minHeight: 44 }}>
                {item.label}
              </MenuItem>
            ))}
          </Select>

          {/* Hamburger button (< 768px: xs to md) */}
          <IconButton
            color="primary"
            aria-label={mobileOpen ? (t('closeMenu') || 'Close navigation menu') : (t('openMenu') || 'Open navigation menu')}
            aria-expanded={mobileOpen}
            aria-controls="mobile-navigation-drawer"
            onClick={handleDrawerToggle}
            sx={{
              display: { xs: 'inline-flex', md: 'none' },
              minWidth: 44,
              minHeight: 44,
              border: '1px solid #ddd8ef',
              borderRadius: 2,
            }}
          >
            {mobileOpen ? <CloseIcon /> : <MenuIcon />}
          </IconButton>
        </Box>
      </Toolbar>

      {/* Slide-in Mobile Drawer (< 768px) */}
      <Drawer
        id="mobile-navigation-drawer"
        anchor="right"
        open={mobileOpen}
        onClose={handleDrawerClose}
        ModalProps={{
          keepMounted: true, // Better mobile performance
        }}
        PaperProps={{
          sx: {
            width: '80%',
            maxWidth: 320,
            bgcolor: '#ffffff',
            p: 2,
            display: 'flex',
            flexDirection: 'column',
          },
        }}
      >
        {/* Drawer Header with Title and Close Button */}
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            pb: 2,
            borderBottom: '1px solid #ddd8ef',
            mb: 1,
          }}
        >
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <FavoriteIcon sx={{ color: 'primary.main', fontSize: 22 }} />
            <Typography variant="subtitle1" sx={{ fontWeight: 800, color: 'primary.main' }}>
              {t('appTitle') || 'Sakhi'}
            </Typography>
          </Box>
          <IconButton
            onClick={handleDrawerClose}
            aria-label={t('closeMenu') || 'Close navigation menu'}
            sx={{ minWidth: 44, minHeight: 44 }}
          >
            <CloseIcon />
          </IconButton>
        </Box>

        {/* Mobile Navigation Links */}
        <List component="nav" aria-label="Mobile navigation" sx={{ py: 1 }}>
          {NAV_ITEMS.map((item) => (
            <ListItem key={item.to} disablePadding sx={{ mb: 0.5 }}>
              <ListItemButton
                component={NavLink}
                to={item.to}
                end={item.end}
                onClick={handleDrawerClose}
                sx={{
                  minHeight: 48,
                  borderRadius: 2,
                  px: 2,
                  '&.active': {
                    bgcolor: 'rgba(108, 75, 209, 0.12)',
                    color: 'primary.main',
                    fontWeight: 700,
                  },
                }}
              >
                <ListItemText
                  primary={t(item.labelKey) || item.defaultLabel}
                  primaryTypographyProps={{
                    fontSize: '1rem',
                    fontWeight: 600,
                  }}
                />
              </ListItemButton>
            </ListItem>
          ))}
        </List>

        {/* Safe notice in drawer */}
        <Box sx={{ mt: 'auto', pt: 2, borderTop: '1px solid #eee' }}>
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 0.5 }}>
            🔒 {t('privacyNotice') || 'No data is stored.'}
          </Typography>
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
            {t('disclaimer') || 'This is information, not a diagnosis.'}
          </Typography>
        </Box>
      </Drawer>
    </AppBar>
  );
}
