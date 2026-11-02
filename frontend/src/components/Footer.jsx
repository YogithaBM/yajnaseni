import { Box, Typography } from '@mui/material';
import { DISCLAIMER, PRIVACY_NOTICE } from '../lib/constants.js';

/**
 * Persistent footer. Both strings are rendered verbatim and are part of the
 * product spec, so they must not be reworded.
 */
export default function Footer() {
  return (
    <Box
      component="footer"
      className="app-footer no-print"
      sx={{
        mt: 4,
        px: { xs: 2, sm: 3 },
        py: 2,
        borderTop: '1px solid #ddd8ef',
        bgcolor: '#ffffff',
        display: 'flex',
        flexWrap: 'wrap',
        gap: 1,
        columnGap: 3,
        justifyContent: 'space-between',
        alignItems: 'baseline',
      }}
    >
      <Typography variant="body2" sx={{ fontWeight: 600 }} data-testid="footer-disclaimer">
        {DISCLAIMER}
      </Typography>
      <Typography variant="body2" color="text.secondary">
        <span aria-hidden="true">🔒 </span>
        <span data-testid="footer-privacy">{PRIVACY_NOTICE}</span>
      </Typography>
      <Typography variant="caption" color="text.secondary">
        Voice input: Kannada, Hindi, Tamil, Telugu, English (in-browser).
      </Typography>
    </Box>
  );
}
