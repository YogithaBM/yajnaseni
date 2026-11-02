import { Link as RouterLink } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import SectionCard from '../components/SectionCard.jsx';
import VoiceInput from '../components/VoiceInput.jsx';
import TriageBadge from '../components/TriageBadge.jsx';
import SymptomCard from '../components/SymptomCard.jsx';
import ExplanationPanel from '../components/ExplanationPanel.jsx';
import { useAppState } from '../context/AppContext.jsx';
import testInputs from '../data/test-inputs.json';

const INPUT_GROUPS = testInputs.reduce((groups, input) => {
  (groups[input.language] = groups[input.language] || []).push(input);
  return groups;
}, {});

const GROUP_LABELS = {
  kn: 'Kannada',
  hi: 'Hindi',
  ta: 'Tamil',
  te: 'Telugu',
  hinglish: 'Code-mixed Hinglish',
};

// One flat option list, with a non-selectable language header before each group.
const SAMPLE_OPTIONS = Object.keys(INPUT_GROUPS).flatMap((lang) => [
  { header: GROUP_LABELS[lang] || lang, key: `header-${lang}` },
  ...INPUT_GROUPS[lang].map((input) => ({
    key: input.id,
    value: input.id,
    label: `${input.title} - ${input.text.slice(0, 48)}`,
  })),
]);

export default function SymptomPage() {
  const {
    text,
    setText,
    voiceLocale,
    setVoiceLocale,
    status,
    error,
    extraction,
    explanation,
    answers,
    setSymptom,
    appendTranscript,
    analyze,
  } = useAppState();

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Symptoms
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
        Describe what you have been experiencing. The assistant fills in the
        chips below; you stay in control of every answer.
      </Typography>

      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
          gap: 2,
          alignItems: 'start',
        }}
      >
        <Box>
          <SectionCard title="How are you feeling?" titleId="input-title">
            <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
              Type in any language, or use your voice in Kannada, Hindi, Tamil or
              Telugu. Typing always works, even when voice does not.
            </Typography>
            <TextField
              multiline
              minRows={5}
              fullWidth
              value={text}
              placeholder="Example: 3 mahine se period nahi aaya, acne bhi hai"
              onChange={(e) => setText(e.target.value)}
              inputProps={{ 'aria-label': 'Describe your symptoms' }}
            />

            <VoiceInput
              locale={voiceLocale}
              onLocaleChange={setVoiceLocale}
              onTranscript={appendTranscript}
            />

            <TextField
              select
              size="small"
              fullWidth
              label="Try a sample input"
              value=""
              onChange={(e) => {
                const chosen = testInputs.find((i) => i.id === e.target.value);
                if (chosen) setText(chosen.text);
              }}
              sx={{ mt: 2 }}
            >
              <MenuItem value="" disabled>
                Choose...
              </MenuItem>
              {SAMPLE_OPTIONS.map((option) => (
                option.header ? (
                  <MenuItem key={option.key} disabled sx={{ fontWeight: 700 }}>
                    {option.header}
                  </MenuItem>
                ) : (
                  <MenuItem key={option.key} value={option.value}>
                    {option.label}
                  </MenuItem>
                )
              ))}
            </TextField>

            <Stack direction="row" spacing={2} sx={{ mt: 2 }} alignItems="center">
              <Button
                variant="contained"
                size="large"
                onClick={() => analyze()}
                disabled={status === 'loading'}
              >
                {status === 'loading' ? 'Analysing...' : 'Analyse'}
              </Button>
              <Button component={RouterLink} to="/calendar" variant="text">
                Add period dates
              </Button>
            </Stack>

            {status === 'loading' && (
              <Typography variant="body2" sx={{ mt: 1 }} role="status">
                Asking the assistant...
              </Typography>
            )}
            {status === 'error' && error && (
              <Alert severity="error" role="alert" sx={{ mt: 2 }}>
                {error}
              </Alert>
            )}
          </SectionCard>
        </Box>

        <Box>
          {extraction && extraction.fallback_notice && (
            <Alert severity="warning" sx={{ mb: 2 }} role="status">
              {extraction.fallback_notice}
            </Alert>
          )}
          {extraction && extraction.source === 'offline_fallback' && (
            <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>
              Source: offline keyword extraction
            </Typography>
          )}

          {extraction && <TriageBadge triage={extraction.triage} />}

          <SymptomCard answers={answers} onSet={setSymptom} disabled={!answers} />

          <ExplanationPanel
            explanation={explanation}
            loading={status === 'loading'}
            error={null}
          />

          {answers && (
            <Button component={RouterLink} to="/summary" variant="outlined" fullWidth>
              View printable doctor summary
            </Button>
          )}
        </Box>
      </Box>
    </Box>
  );
}
