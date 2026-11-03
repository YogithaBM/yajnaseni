import { beforeEach, describe, expect, it } from 'vitest';
import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Navbar from './Navbar.jsx';
import { AppProvider } from '../context/AppContext.jsx';

function renderNavbar() {
  return render(
    <MemoryRouter>
      <AppProvider>
        <Navbar />
      </AppProvider>
    </MemoryRouter>
  );
}

describe('Navbar', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('renders brand and navigation links with aria-labels', () => {
    renderNavbar();
    expect(screen.getAllByText('Sakhi').length).toBeGreaterThan(0);
    expect(screen.getByRole('navigation', { name: /main navigation/i })).toBeInTheDocument();
  });

  it('provides a language switcher with English, Hindi, and Kannada', () => {
    renderNavbar();
    const langSelect = screen.getByRole('combobox', { name: /select language/i });
    expect(langSelect).toBeInTheDocument();

    // Open select dropdown and click Hindi
    fireEvent.mouseDown(langSelect);
    const hindiOption = screen.getByRole('option', { name: 'हिंदी' });
    fireEvent.click(hindiOption);

    expect(screen.getAllByText('सखी').length).toBeGreaterThan(0);
  });

  it('opens mobile drawer when hamburger is clicked and closes on Escape', () => {
    renderNavbar();
    const openBtn = screen.getByRole('button', { name: /open navigation menu/i });
    fireEvent.click(openBtn);

    // Mobile nav drawer should now be open
    expect(screen.getByRole('navigation', { name: /mobile navigation/i })).toBeInTheDocument();

    // Press Escape
    fireEvent.keyDown(window, { key: 'Escape', code: 'Escape' });
    // Drawer should close
    expect(screen.queryByRole('navigation', { name: /mobile navigation/i })).not.toBeInTheDocument();
  });

  it('closes mobile drawer when a nav link is clicked', () => {
    renderNavbar();
    const openBtn = screen.getByRole('button', { name: /open navigation menu/i });
    fireEvent.click(openBtn);

    const mobileNav = screen.getByRole('navigation', { name: /mobile navigation/i });
    expect(mobileNav).toBeInTheDocument();

    const homeLink = screen.getAllByText(/home/i).find((el) => mobileNav.contains(el));
    if (homeLink) {
      fireEvent.click(homeLink);
    }
    expect(screen.queryByRole('navigation', { name: /mobile navigation/i })).not.toBeInTheDocument();
  });
});
