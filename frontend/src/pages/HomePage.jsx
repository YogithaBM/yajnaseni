import { Link as RouterLink } from 'react-router-dom';
import {
  Box,
  Button,
  Card,
  CardContent,
  Stack,
  Typography,
} from '@mui/material';
import PrivacyNotice from '../components/PrivacyNotice.jsx';
import { useAppState } from '../context/AppContext.jsx';
import { DISCLAIMER } from '../lib/constants.js';

const STEPS = [
  {
    title: '1. Describe what you feel',
    body: 'Type in your own words, or speak in Kannada, Hindi, Tamil or Telugu. '
      + 'You can also pick one of the 20 sample inputs.',
  },
  {
    title: '2. Confirm what was understood',
    body: 'Tap each symptom chip to confirm or correct cycle gap, acne, hair growth, '
      + 'hair thinning, weight change, dark patches, pain and bleeding.',
  },
  {
    title: '3. Mark your recent periods',
    body: 'Tap the start date of each recent period. Irregularity is calculated in '
      + 'your browser with plain JavaScript - no AI is involved.',
  },
  {
    title: '4. Take a summary to your doctor',
    body: 'Get a printable English page with confirmed symptoms, cycle data, '
      + 'questions to ask and tests commonly discussed.',
  },
];

export default function HomePage() {
  const { status, answers, periodDates } = useAppState();

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Welcome to Yajnaseni
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 2, maxWidth: 720 }}>
        Yajnaseni is a quiet space to describe period and hormone-related
        symptoms in your own language, see a plain-language explanation, and
        prepare for a doctor visit. It helps you notice patterns - it does not
        replace a doctor.
      </Typography>

      <PrivacyNotice />

      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ my: 2 }}>
        <Button component={RouterLink} to="/symptoms" variant="contained" size="large">
          Describe my symptoms
        </Button>
        <Button component={RouterLink} to="/calendar" variant="outlined" size="large">
          Mark my period dates
        </Button>
        <Button component={RouterLink} to="/summary" variant="text" size="large">
          Doctor summary
        </Button>
      </Stack>

      {(answers || periodDates.length > 0) && (
        <Card sx={{ mb: 2, border: '1px solid #ddd8ef', bgcolor: '#f3efff' }}>
          <CardContent>
            <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
              You already have information in this session
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {answers
                ? `Symptom answers are ready${status === 'done' ? ' and analysed' : ''}.`
                : 'No analysis yet.'}{' '}
              {periodDates.length > 0
                ? `${periodDates.length} period start date(s) marked.`
                : 'No period dates marked yet.'}{' '}
              Nothing is stored - it stays in this tab until you close it.
            </Typography>
          </CardContent>
        </Card>
      )}

      <Typography variant="h5" component="h2" sx={{ mt: 3, mb: 1 }}>
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
          <Card key={step.title} sx={{ border: '1px solid #ddd8ef' }}>
            <CardContent>
              <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 0.5 }}>
                {step.title}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {step.body}
              </Typography>
            </CardContent>
          </Card>
        ))}
      </Box>

      <Typography variant="body2" sx={{ mt: 3, fontWeight: 600 }}>
        {DISCLAIMER}
      </Typography>
    </Box>
  );
}
