import { Link as RouterLink } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Divider,
  Grid,
  Stack,
  Typography,
} from '@mui/material';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import PhoneInTalkIcon from '@mui/icons-material/PhoneInTalk';
import LockIcon from '@mui/icons-material/Lock';
import PrivacyNotice from '../components/PrivacyNotice.jsx';
import { DISCLAIMER, PRIVACY_NOTICE } from '../lib/constants.js';
import { useAppState } from '../context/AppContext.jsx';

const HELPLINES = [
  {
    name: 'National Emergency Number',
    number: '112',
    description: 'Immediate police, medical, and fire emergency service across India.',
  },
  {
    name: "Women's Helpline (All-India)",
    number: '1091 / 181',
    description: '24x7 toll-free support for women facing distress, harassment, or medical emergencies.',
  },
  {
    name: 'Tele-MANAS (Mental Health Helpline)',
    number: '14416 / 1800-891-4416',
    description: 'Free, 24x7 government mental health counseling by MoHFW.',
  },
  {
    name: 'KIRAN Mental Health Support',
    number: '1800-599-0019',
    description: 'Dedicated 24x7 psychological support and crisis counseling helpline.',
  },
];

const RED_FLAGS = [
  'Sudden, severe, or sharp lower abdominal or pelvic pain (especially one-sided).',
  'Abnormally heavy bleeding (soaking through two or more pads/tampons every hour for 2+ consecutive hours).',
  'Feeling dizzy, lightheaded, abnormally weak, or fainting accompanied by bleeding.',
  'High fever with pelvic pain, chills, or unusual foul-smelling vaginal discharge.',
  'Severe pain or abnormal bleeding if there is any chance of pregnancy (requires immediate ectopic rule-out).',
];

export default function AboutPage() {
  const { t } = useAppState();

  return (
    <Box sx={{ maxWidth: 860, mx: 'auto' }}>
      <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'primary.main', fontWeight: 800 }}>
        About Sakhi & Safety Guide
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 3, fontSize: '1.05rem', lineHeight: 1.6 }}>
        Sakhi is a quiet, supportive tool created to help women describe menstrual and hormonal
        symptoms in their own language, understand patterns, and prepare for confident,
        structured conversations with their doctor.
      </Typography>

      <PrivacyNotice />

      {/* Not a diagnosis alert */}
      <Alert severity="info" sx={{ mb: 3, borderRadius: 2 }} role="note">
        <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 0.5 }}>
          Important Medical Notice
        </Typography>
        <Typography variant="body2">
          {DISCLAIMER} This tool is intended for personal tracking and clinical visit preparation.
          Patterns identified are commonly associated with hormonal health and are worth discussing
          with a doctor. Sakhi does not provide clinical diagnoses or prescriptions.
        </Typography>
      </Alert>

      {/* When to seek urgent care */}
      <Card sx={{ mb: 3, border: '2px solid #f5c6cb', bgcolor: '#fff8f8', borderRadius: 3 }}>
        <CardContent sx={{ p: { xs: 2.5, sm: 3 } }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 1.5 }}>
            <WarningAmberIcon sx={{ color: '#c62828', fontSize: 32 }} />
            <Typography variant="h5" component="h2" sx={{ color: '#c62828', fontWeight: 800 }}>
              When to Seek Immediate Medical Care
            </Typography>
          </Box>
          <Typography variant="body2" sx={{ mb: 2, color: 'text.primary', fontWeight: 500 }}>
            If you experience any of the following red-flag symptoms, do not wait for an appointment.
            Seek immediate medical evaluation at an emergency clinic or hospital:
          </Typography>
          <Box component="ul" sx={{ m: 0, pl: 3 }}>
            {RED_FLAGS.map((flag) => (
              <Box component="li" key={flag} sx={{ mb: 1, fontSize: '0.98rem', color: '#5c1d1d' }}>
                <Typography variant="body2" sx={{ fontWeight: 600 }}>
                  {flag}
                </Typography>
              </Box>
            ))}
          </Box>
        </CardContent>
      </Card>

      {/* Helplines Directory */}
      <Card sx={{ mb: 3, border: '1px solid #ddd8ef', borderRadius: 3 }}>
        <CardContent sx={{ p: { xs: 2.5, sm: 3 } }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 2 }}>
            <PhoneInTalkIcon sx={{ color: 'primary.main', fontSize: 28 }} />
            <Typography variant="h5" component="h2" sx={{ fontWeight: 700 }}>
              Emergency & Healthcare Helplines
            </Typography>
          </Box>
          <Grid container spacing={2}>
            {HELPLINES.map((hl) => (
              <Grid item xs={12} sm={6} key={hl.name}>
                <Card variant="outlined" sx={{ height: '100%', borderRadius: 2, bgcolor: '#fcfaff' }}>
                  <CardContent sx={{ p: 2 }}>
                    <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'primary.main' }}>
                      {hl.name}
                    </Typography>
                    <Typography
                      variant="h6"
                      sx={{ fontWeight: 800, color: '#c62828', my: 0.5, letterSpacing: '0.05em' }}
                    >
                      {hl.number}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {hl.description}
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </CardContent>
      </Card>

      {/* How it works vs What it does not do */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%', border: '1px solid #c8e6c9', bgcolor: '#fbfdfb', borderRadius: 3 }}>
            <CardContent sx={{ p: { xs: 2.5, sm: 3 } }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                <LocalHospitalIcon sx={{ color: 'success.main' }} />
                <Typography variant="h6" component="h3" sx={{ fontWeight: 700 }}>
                  How Sakhi Works
                </Typography>
              </Box>
              <Typography variant="body2" color="text.secondary" paragraph>
                1. <strong>Natural Expression:</strong> You describe symptoms in your own words, using text or voice in Kannada, Hindi, Tamil, Telugu, or English.
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                2. <strong>Patient Control:</strong> You confirm or edit each detected symptom chip (cycle length, hair changes, pain, weight).
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                3. <strong>Mathematical Cycle Tracking:</strong> Period intervals and cycle irregularity are calculated deterministically using plain JavaScript maths.
              </Typography>
              <Typography variant="body2" color="text.secondary">
                4. <strong>Visit Preparation:</strong> Produces a printable English clinical summary with key discussion questions and standard laboratory tests.
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%', border: '1px solid #ffe0b2', bgcolor: '#fffdfa', borderRadius: 3 }}>
            <CardContent sx={{ p: { xs: 2.5, sm: 3 } }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                <LockIcon sx={{ color: 'warning.main' }} />
                <Typography variant="h6" component="h3" sx={{ fontWeight: 700 }}>
                  What Sakhi Does NOT Do
                </Typography>
              </Box>
              <Typography variant="body2" color="text.secondary" paragraph>
                • <strong>No Medical Diagnosis:</strong> It cannot diagnose conditions like PCOS, endometriosis, or thyroid disorders.
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                • <strong>No Server Storage:</strong> Your period data is stored exclusively in your browser (localStorage). No data is stored on a server.
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                • <strong>No Prescriptions:</strong> It never recommends prescription drugs or hormone therapies.
              </Typography>
              <Typography variant="body2" color="text.secondary">
                • <strong>No Replacement for Doctors:</strong> It is designed to assist you in preparing for an in-person gynecological consultation.
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Action buttons */}
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ my: 3 }}>
        <Button
          component={RouterLink}
          to="/symptoms"
          variant="contained"
          size="large"
          sx={{ minHeight: 44, px: 3, fontWeight: 700 }}
        >
          Check my symptoms
        </Button>
        <Button
          component={RouterLink}
          to="/calendar"
          variant="outlined"
          size="large"
          sx={{ minHeight: 44, px: 3, fontWeight: 700 }}
        >
          Track my period
        </Button>
        <Button
          component={RouterLink}
          to="/summary"
          variant="text"
          size="large"
          sx={{ minHeight: 44 }}
        >
          Doctor summary
        </Button>
      </Stack>

      <Divider sx={{ my: 2 }} />

      <Typography variant="body2" sx={{ fontWeight: 600, color: 'text.secondary', textAlign: 'center' }}>
        {DISCLAIMER} — {PRIVACY_NOTICE}
      </Typography>
    </Box>
  );
}
