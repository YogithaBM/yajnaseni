import { Link as RouterLink } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Stack,
  Typography,
} from '@mui/material';
import HealthAndSafetyIcon from '@mui/icons-material/HealthAndSafety';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import DescriptionIcon from '@mui/icons-material/Description';
import PrivacyNotice from '../components/PrivacyNotice.jsx';
import { useAppState } from '../context/AppContext.jsx';
import { DISCLAIMER, PRIVACY_NOTICE } from '../lib/constants.js';

const STEPS = [
  {
    title: '1. Describe what you feel',
    body: 'Type in your own words or use your voice in Kannada, Hindi, Tamil, Telugu, or English. You can also pick from common clinical sample inputs.',
  },
  {
    title: '2. Confirm what was understood',
    body: 'Tap each symptom chip to confirm or correct cycle gap, acne, hair growth, hair thinning, weight change, dark patches, pain, and bleeding.',
  },
  {
    title: '3. Track your period dates',
    body: 'Mark your recent period start dates. Irregularity and cycle statistics are computed privately in your browser using deterministic math.',
  },
  {
    title: '4. Take a summary to your doctor',
    body: 'Generate a clean, printable English report with confirmed symptoms, cycle history, questions to ask, and standard lab tests to discuss.',
  },
];

export default function HomePage() {
  const { status, answers, periodDates, t } = useAppState();

  return (
    <Box sx={{ maxWidth: 960, mx: 'auto' }}>
      {/* Hero section */}
      <Box sx={{ mb: 3 }}>
        <Typography
          variant="h4"
          component="h1"
          gutterBottom
          sx={{
            fontWeight: 800,
            color: 'primary.main',
            fontSize: { xs: '1.75rem', sm: '2.25rem' },
          }}
        >
          {t('appFullName') || 'Sakhi — Yajnaseni'}
        </Typography>
        <Typography
          variant="h6"
          component="p"
          color="text.secondary"
          sx={{
            maxWidth: 780,
            fontWeight: 400,
            lineHeight: 1.6,
            mb: 2,
            fontSize: { xs: '1rem', sm: '1.15rem' },
          }}
        >
          A quiet, supportive space to describe menstrual and hormone-related symptoms in your own
          language, understand patterns commonly associated with cycle irregularities, and prepare
          thoughtfully for a doctor visit.
        </Typography>
      </Box>

      {/* Privacy Notice */}
      <PrivacyNotice />

      {/* Not-a-diagnosis banner note */}
      <Alert
        severity="info"
        sx={{
          mb: 3,
          borderRadius: 2.5,
          border: '1px solid #c7d2fe',
          bgcolor: '#eef2ff',
        }}
        role="note"
        data-testid="not-a-diagnosis-banner"
      >
        <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 0.25 }}>
          {DISCLAIMER}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Sakhi helps you notice patterns commonly associated with hormonal and cycle health that are
          worth discussing with a doctor. It does not provide clinical diagnoses or replace professional medical advice.
        </Typography>
      </Alert>

      {/* Primary Action Buttons */}
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        sx={{ my: 3 }}
      >
        <Button
          component={RouterLink}
          to="/symptoms"
          variant="contained"
          size="large"
          startIcon={<HealthAndSafetyIcon />}
          sx={{
            minHeight: 48,
            px: 3,
            fontSize: '1rem',
            fontWeight: 700,
            borderRadius: 2,
          }}
        >
          {t('checkSymptoms') || 'Check my symptoms'}
        </Button>
        <Button
          component={RouterLink}
          to="/calendar"
          variant="outlined"
          size="large"
          startIcon={<CalendarMonthIcon />}
          sx={{
            minHeight: 48,
            px: 3,
            fontSize: '1rem',
            fontWeight: 700,
            borderRadius: 2,
            borderWidth: 2,
            '&:hover': { borderWidth: 2 },
          }}
        >
          {t('trackPeriod') || 'Track my period'}
        </Button>
        <Button
          component={RouterLink}
          to="/summary"
          variant="text"
          size="large"
          startIcon={<DescriptionIcon />}
          sx={{
            minHeight: 48,
            px: 2,
            fontSize: '0.98rem',
            fontWeight: 600,
            borderRadius: 2,
          }}
        >
          {t('viewSummary') || 'Doctor summary'}
        </Button>
      </Stack>

      {/* Active Session Status Card */}
      {(answers || periodDates.length > 0) && (
        <Card
          sx={{
            mb: 3,
            border: '1px solid #ddd8ef',
            bgcolor: '#f5f0ff',
            borderRadius: 2.5,
          }}
        >
          <CardContent sx={{ p: 2.5 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 700, color: 'primary.dark', mb: 0.5 }}>
              Active Session Information
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {answers
                ? `Symptom answers are loaded${status === 'done' ? ' and analyzed' : ''}.`
                : 'No symptom analysis performed yet.'}{' '}
              {periodDates.length > 0
                ? `${periodDates.length} period start date(s) marked.`
                : 'No period dates logged yet.'}{' '}
              🔒 Period dates are saved in your browser&apos;s localStorage; no information is sent to a server for storage.
            </Typography>
          </CardContent>
        </Card>
      )}

      {/* How It Works Grid */}
      <Box sx={{ mt: 4, mb: 2 }}>
        <Typography variant="h5" component="h2" sx={{ fontWeight: 800, mb: 2, color: 'text.primary' }}>
          How it works
        </Typography>
        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' },
            gap: 2,
          }}
        >
          {STEPS.map((step) => (
            <Card
              key={step.title}
              sx={{
                border: '1px solid #ddd8ef',
                borderRadius: 2.5,
                bgcolor: '#ffffff',
                transition: 'box-shadow 0.2s ease',
                '&:hover': {
                  boxShadow: '0 4px 12px rgba(108, 75, 209, 0.08)',
                },
              }}
            >
              <CardContent sx={{ p: 2.5 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1, color: 'primary.main' }}>
                  {step.title}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.55 }}>
                  {step.body}
                </Typography>
              </CardContent>
            </Card>
          ))}
        </Box>
      </Box>

      {/* Quick link to Safety Guide */}
      <Box sx={{ mt: 3, p: 2, bgcolor: '#fff8f0', borderRadius: 2.5, border: '1px solid #ffeedd' }}>
        <Typography variant="subtitle2" sx={{ fontWeight: 700, color: '#b45309', mb: 0.5 }}>
          Need emergency assistance or health information?
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
          Learn about red-flag symptoms that require immediate care and access 24x7 all-India women&apos;s and mental health helplines.
        </Typography>
        <Button
          component={RouterLink}
          to="/about"
          size="small"
          variant="outlined"
          color="warning"
          sx={{ minHeight: 44, fontWeight: 700 }}
        >
          Read About & Safety Guide
        </Button>
      </Box>

      <Typography variant="body2" sx={{ mt: 3, fontWeight: 600, color: 'text.secondary' }}>
        {DISCLAIMER} — {PRIVACY_NOTICE}
      </Typography>
    </Box>
  );
}
