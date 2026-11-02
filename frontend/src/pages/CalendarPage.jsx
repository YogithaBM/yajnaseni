import { Link as RouterLink } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Stack,
  Typography,
} from '@mui/material';
import PeriodCalendar from '../components/PeriodCalendar.jsx';
import { useAppState } from '../context/AppContext.jsx';

export default function CalendarPage() {
  const { periodDates, cycleStats, toggleDate, clearDates, setSymptom } = useAppState();

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Period calendar
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2, maxWidth: 720 }}>
        Tap the first day of each recent period. The gap between dates is
        computed in your browser with plain JavaScript, so the same dates always
        give the same answer - no AI model is involved.
      </Typography>

      <PeriodCalendar
        dates={periodDates}
        onToggle={toggleDate}
        onClear={clearDates}
      />

      {cycleStats.ready && (
        <Alert
          severity={cycleStats.irregular ? 'warning' : 'success'}
          sx={{ mb: 2 }}
          role="status"
        >
          {cycleStats.irregular
            ? 'Your last gaps fall outside the usual 21-35 day range. This is worth discussing with a doctor.'
            : 'Your last gaps are inside the usual 21-35 day range.'}
        </Alert>
      )}

      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 1 }}>
        <Button
          variant="contained"
          onClick={() => setSymptom('cycle_gap_days', cycleStats.averageGapDays, 'edit')}
          disabled={!cycleStats.ready}
        >
          Use average gap ({cycleStats.averageGapDays ?? '-'} days) in my symptoms
        </Button>
        <Button component={RouterLink} to="/symptoms" variant="outlined">
          Back to symptoms
        </Button>
        <Button component={RouterLink} to="/summary" variant="text">
          Doctor summary
        </Button>
      </Stack>
    </Box>
  );
}
