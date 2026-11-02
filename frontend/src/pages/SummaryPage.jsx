import { Link as RouterLink } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Stack,
  Typography,
} from '@mui/material';
import DoctorSummary from '../components/DoctorSummary.jsx';
import { useAppState } from '../context/AppContext.jsx';
import { DISCLAIMER } from '../lib/constants.js';

export default function SummaryPage() {
  const { answers, cycleStats, text } = useAppState();

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Doctor summary
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2, maxWidth: 720 }}>
        A printable English page you can hand to a doctor: confirmed symptoms,
        cycle data, questions to ask and tests commonly discussed. Printing
        shows only this summary.
      </Typography>

      {!answers && (
        <Alert severity="info" sx={{ mb: 2 }}>
          Analyse your description first, then confirm or correct the chips. You
          can still print today&apos;s calendar data below.
        </Alert>
      )}

      <DoctorSummary
        answers={answers}
        cycleStats={cycleStats}
        reportedText={text}
        ready={Boolean(answers)}
      />

      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
        <Button component={RouterLink} to="/symptoms" variant="outlined">
          Back to symptoms
        </Button>
        <Button component={RouterLink} to="/calendar" variant="text">
          Edit period dates
        </Button>
      </Stack>

      <Typography variant="body2" sx={{ mt: 2, fontWeight: 600 }}>
        {DISCLAIMER}
      </Typography>
    </Box>
  );
}
