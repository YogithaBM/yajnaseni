import { useState, useCallback } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Divider,
  Grid,
  Stack,
  Typography,
} from '@mui/material';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AnalyticsIcon from '@mui/icons-material/Analytics';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import PeriodCalendar from '../components/PeriodCalendar.jsx';
import { useAppState } from '../context/AppContext.jsx';
import { dayDiff, normalizeDates } from '../lib/cycleStats.js';
import * as api from '../api.js';

function computeClientCycleAnalysis(dates) {
  const norm = normalizeDates(dates);
  const cycleCount = Math.max(0, norm.length - 1);
  if (cycleCount < 2) {
    return {
      number_of_cycles: cycleCount,
      message: 'need more data',
      irregular: false,
      average_cycle_length: null,
      shortest_cycle_length: null,
      longest_cycle_length: null,
      estimated_next_period_window: null,
      prediction_label: 'estimate, cycles vary',
      source: 'client_fallback',
    };
  }

  const gaps = [];
  for (let i = 1; i < norm.length; i += 1) {
    gaps.push(dayDiff(norm[i - 1], norm[i]));
  }
  const sum = gaps.reduce((acc, g) => acc + g, 0);
  const avg = Math.round((sum / gaps.length) * 10) / 10;
  const shortest = Math.min(...gaps);
  const longest = Math.max(...gaps);
  const irregular = gaps.some((g) => g < 21 || g > 35);

  const lastIso = norm[norm.length - 1];
  const [y, m, d] = lastIso.split('-').map(Number);
  const expectedDate = new Date(Date.UTC(y, m - 1, d + Math.round(avg)));
  const startDate = new Date(Date.UTC(y, m - 1, d + Math.round(avg) - 2));
  const endDate = new Date(Date.UTC(y, m - 1, d + Math.round(avg) + 2));

  const toIso = (dt) => dt.toISOString().slice(0, 10);
  return {
    number_of_cycles: cycleCount,
    average_cycle_length: avg,
    shortest_cycle_length: shortest,
    longest_cycle_length: longest,
    irregular,
    gaps,
    estimated_next_period_window: {
      start: toIso(startDate),
      expected: toIso(expectedDate),
      end: toIso(endDate),
      label: 'estimate, cycles vary',
    },
    prediction_label: 'estimate, cycles vary',
    message: null,
    source: 'client_fallback',
  };
}

export default function CalendarPage() {
  const {
    periodDates,
    cycleStats,
    toggleDate,
    clearDates,
    clearAllData,
    setSymptom,
    t,
  } = useAppState();

  const [analysis, setAnalysis] = useState(null);
  const [analyzing, setAnalyzing] = useState(false);
  const [confirmClearOpen, setConfirmClearOpen] = useState(false);

  const handleAnalyzeCycle = useCallback(async () => {
    setAnalyzing(true);
    try {
      const res = await api.analyzeCycle(periodDates);
      setAnalysis(res);
    } catch (err) {
      // Backend unreachable or offline: fallback to deterministic in-browser calculation
      const clientFallback = computeClientCycleAnalysis(periodDates);
      setAnalysis(clientFallback);
    } finally {
      setAnalyzing(false);
    }
  }, [periodDates]);

  const handleConfirmClear = useCallback(() => {
    clearAllData();
    setAnalysis(null);
    setConfirmClearOpen(false);
  }, [clearAllData]);

  return (
    <Box sx={{ maxWidth: 880, mx: 'auto' }}>
      <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'primary.main', fontWeight: 800 }}>
        Period Calendar & Cycle Tracker
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 2, maxWidth: 740, lineHeight: 1.6 }}>
        Tap the start date of each recent period below. Your dates stay strictly in your
        browser (localStorage). Cycles are analyzed deterministically without storing any personal information.
      </Typography>

      <PeriodCalendar
        dates={periodDates}
        onToggle={toggleDate}
        onClear={clearDates}
      />

      {/* Action Bar */}
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        sx={{ my: 2 }}
        alignItems={{ xs: 'stretch', sm: 'center' }}
      >
        <Button
          variant="contained"
          size="large"
          startIcon={analyzing ? <CircularProgress size={20} color="inherit" /> : <AnalyticsIcon />}
          onClick={handleAnalyzeCycle}
          disabled={analyzing || periodDates.length < 2}
          sx={{ minHeight: 44, px: 3, fontWeight: 700 }}
        >
          {analyzing ? 'Analyzing cycle...' : 'Analyze my cycle'}
        </Button>

        <Button
          variant="outlined"
          color="error"
          startIcon={<DeleteOutlineIcon />}
          onClick={() => setConfirmClearOpen(true)}
          disabled={periodDates.length === 0}
          sx={{ minHeight: 44 }}
        >
          Clear all my data
        </Button>

        {cycleStats.ready && (
          <Button
            variant="text"
            onClick={() => setSymptom('cycle_gap_days', cycleStats.averageGapDays, 'edit')}
            sx={{ minHeight: 44 }}
          >
            Use average gap ({cycleStats.averageGapDays} days) in symptoms
          </Button>
        )}
      </Stack>

      {/* Analysis Results Card */}
      {analysis && (
        <Card
          sx={{
            my: 3,
            border: '1px solid #ddd8ef',
            borderRadius: 3,
            bgcolor: '#ffffff',
            boxShadow: '0 4px 14px rgba(108, 75, 209, 0.08)',
          }}
          aria-live="polite"
        >
          <CardContent sx={{ p: { xs: 2.5, sm: 3 } }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
              <CalendarMonthIcon sx={{ color: 'primary.main', fontSize: 26 }} />
              <Typography variant="h6" component="h2" sx={{ fontWeight: 800 }}>
                Cycle Analysis Results
              </Typography>
            </Box>

            {analysis.message === 'need more data' || analysis.number_of_cycles < 2 ? (
              <Alert severity="info" sx={{ borderRadius: 2 }}>
                <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                  Need more data
                </Typography>
                <Typography variant="body2">
                  At least 2 complete menstrual cycles (3 recorded period start dates) are needed to calculate
                  cycle averages, irregularity patterns, and estimated windows. You currently have{' '}
                  {analysis.number_of_cycles} cycle(s) recorded.
                </Typography>
              </Alert>
            ) : (
              <Box>
                <Grid container spacing={2} sx={{ mb: 2 }}>
                  <Grid item xs={6} sm={3}>
                    <Card variant="outlined" sx={{ p: 1.5, textAlign: 'center', borderRadius: 2 }}>
                      <Typography variant="caption" color="text.secondary">
                        Completed Cycles
                      </Typography>
                      <Typography variant="h5" sx={{ fontWeight: 800, color: 'primary.main', mt: 0.5 }}>
                        {analysis.number_of_cycles}
                      </Typography>
                    </Card>
                  </Grid>

                  <Grid item xs={6} sm={3}>
                    <Card variant="outlined" sx={{ p: 1.5, textAlign: 'center', borderRadius: 2 }}>
                      <Typography variant="caption" color="text.secondary">
                        Average Cycle
                      </Typography>
                      <Typography variant="h5" sx={{ fontWeight: 800, color: 'primary.main', mt: 0.5 }}>
                        {analysis.average_cycle_length} <span style={{ fontSize: '0.85rem' }}>days</span>
                      </Typography>
                    </Card>
                  </Grid>

                  <Grid item xs={6} sm={3}>
                    <Card variant="outlined" sx={{ p: 1.5, textAlign: 'center', borderRadius: 2 }}>
                      <Typography variant="caption" color="text.secondary">
                        Shortest Cycle
                      </Typography>
                      <Typography variant="h5" sx={{ fontWeight: 800, mt: 0.5 }}>
                        {analysis.shortest_cycle_length} <span style={{ fontSize: '0.85rem' }}>days</span>
                      </Typography>
                    </Card>
                  </Grid>

                  <Grid item xs={6} sm={3}>
                    <Card variant="outlined" sx={{ p: 1.5, textAlign: 'center', borderRadius: 2 }}>
                      <Typography variant="caption" color="text.secondary">
                        Longest Cycle
                      </Typography>
                      <Typography variant="h5" sx={{ fontWeight: 800, mt: 0.5 }}>
                        {analysis.longest_cycle_length} <span style={{ fontSize: '0.85rem' }}>days</span>
                      </Typography>
                    </Card>
                  </Grid>
                </Grid>

                {/* Irregularity Alert */}
                <Alert
                  severity={analysis.irregular ? 'warning' : 'success'}
                  icon={analysis.irregular ? <ErrorOutlineIcon /> : <CheckCircleOutlineIcon />}
                  sx={{ mb: 2, borderRadius: 2 }}
                >
                  <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                    {analysis.irregular ? 'Irregular Cycle Pattern Detected' : 'Regular Cycle Pattern'}
                  </Typography>
                  <Typography variant="body2">
                    {analysis.irregular
                      ? 'One or more recorded gaps fall outside the typical 21–35 day range. Patterns commonly associated with cycle irregularity are worth discussing with a doctor.'
                      : 'All recorded cycle gaps fall within the expected 21–35 day window.'}
                  </Typography>
                </Alert>

                {/* Next Period Prediction Window */}
                {analysis.estimated_next_period_window && (
                  <Box sx={{ p: 2, bgcolor: '#fbf9ff', borderRadius: 2, border: '1px dashed #6c4bd1' }}>
                    <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'primary.main' }}>
                      Estimated Next Period Window:
                    </Typography>
                    <Typography variant="h6" sx={{ fontWeight: 800, my: 0.5 }}>
                      {analysis.estimated_next_period_window.start} to {analysis.estimated_next_period_window.end}
                    </Typography>
                    <Typography variant="caption" sx={{ color: 'text.secondary', fontWeight: 600 }}>
                      ⚠️ {analysis.prediction_label || 'estimate, cycles vary'}
                    </Typography>
                  </Box>
                )}
              </Box>
            )}
          </CardContent>
        </Card>
      )}

      {/* Confirmation Dialog for Clear All Data */}
      <Dialog
        open={confirmClearOpen}
        onClose={() => setConfirmClearOpen(false)}
        aria-labelledby="clear-dialog-title"
        aria-describedby="clear-dialog-description"
      >
        <DialogTitle id="clear-dialog-title" sx={{ fontWeight: 700 }}>
          Clear all saved data?
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="clear-dialog-description">
            This will permanently remove all period dates saved in your browser&apos;s localStorage
            and reset the cycle analysis for this session. This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setConfirmClearOpen(false)} sx={{ minHeight: 44 }}>
            Cancel
          </Button>
          <Button
            onClick={handleConfirmClear}
            color="error"
            variant="contained"
            autoFocus
            sx={{ minHeight: 44 }}
          >
            Yes, clear all my data
          </Button>
        </DialogActions>
      </Dialog>

      <Divider sx={{ my: 3 }} />

      {/* Navigation Buttons */}
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
        <Button
          component={RouterLink}
          to="/symptoms"
          variant="outlined"
          sx={{ minHeight: 44, px: 3 }}
        >
          Back to symptoms
        </Button>
        <Button
          component={RouterLink}
          to="/summary"
          variant="text"
          sx={{ minHeight: 44 }}
        >
          View doctor summary
        </Button>
      </Stack>
    </Box>
  );
}
