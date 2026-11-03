import { Navigate, Route, Routes } from 'react-router-dom';
import { Box, Toolbar } from '@mui/material';
import Navbar from './components/Navbar.jsx';
import Footer from './components/Footer.jsx';
import HomePage from './pages/HomePage.jsx';
import SymptomPage from './pages/SymptomPage.jsx';
import CalendarPage from './pages/CalendarPage.jsx';
import SummaryPage from './pages/SummaryPage.jsx';
import AboutPage from './pages/AboutPage.jsx';
import { AppProvider } from './context/AppContext.jsx';

/**
 * Application shell: fixed navbar, routed page body and the persistent footer.
 * All shared state comes from <AppProvider>, so moving between routes keeps
 * whatever the user has typed, confirmed or tapped.
 *
 * A router (BrowserRouter in main.jsx) must wrap this component.
 */
export default function App() {
  return (
    <AppProvider>
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          bgcolor: 'background.default',
        }}
      >
        <Navbar />
        {/* Spacer matching the fixed AppBar height. */}
        <Toolbar />

        <Box
          component="main"
          id="main-content"
          sx={{
            flex: 1,
            width: '100%',
            maxWidth: 1100,
            mx: 'auto',
            px: { xs: 2, sm: 3 },
            py: 3,
          }}
        >
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/symptoms" element={<SymptomPage />} />
            <Route path="/calendar" element={<CalendarPage />} />
            <Route path="/summary" element={<SummaryPage />} />
            <Route path="/about" element={<AboutPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Box>

        <Footer />
      </Box>
    </AppProvider>
  );
}
