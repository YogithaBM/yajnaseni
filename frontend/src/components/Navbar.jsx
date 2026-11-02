import { NavLink } from 'react-router-dom';
import {
  AppBar,
  Box,
  Button,
  Toolbar,
  Typography,
} from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';

export const NAV_LINKS = [
  { to: '/', label: 'Home', end: true },
  { to: '/symptoms', label: 'Symptoms' },
  { to: '/calendar', label: 'Calendar' },
  { to: '/summary', label: 'Summary' },
];

/**
 * Fixed Material-UI AppBar. On narrow screens the links stay in one row that
 * scrolls horizontally, so every page is always reachable and the bar never
 * jumps in height.
 */
export default function Navbar() {
  return (
    <AppBar position="fixed" color="inherit" elevation={2} sx={{ bgcolor: '#ffffff' }}>
      <Toolbar sx={{ gap: 1, px: { xs: 1.5, sm: 3 } }}>
        <FavoriteIcon sx={{ color: 'primary.main' }} aria-hidden="true" />
        <Typography
          variant="h6"
          component={NavLink}
          to="/"
          sx={{
            color: 'primary.main',
            textDecoration: 'none',
            fontWeight: 800,
            letterSpacing: '0.02em',
            whiteSpace: 'nowrap',
          }}
        >
          Yajnaseni
        </Typography>

        <Box
          component="nav"
          aria-label="Main navigation"
          sx={{
            display: 'flex',
            gap: { xs: 0, sm: 1 },
            ml: 'auto',
            overflowX: 'auto',
            flexShrink: 1,
          }}
        >
          {NAV_LINKS.map((link) => (
            <Button
              key={link.to}
              component={NavLink}
              to={link.to}
              end={link.end}
              color="primary"
              sx={{
                minWidth: 0,
                px: { xs: 1, sm: 1.75 },
                fontSize: { xs: '0.85rem', sm: '0.95rem' },
                whiteSpace: 'nowrap',
                '&.active': {
                  bgcolor: 'rgba(108, 75, 209, 0.12)',
                  fontWeight: 700,
                },
              }}
            >
              {link.label}
            </Button>
          ))}
        </Box>
      </Toolbar>
    </AppBar>
  );
}
