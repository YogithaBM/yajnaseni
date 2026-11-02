import { Card, CardContent, Typography } from '@mui/material';

/**
 * Standard Material-UI card used for every page section: gives all panels the
 * same padding, heading style and spacing.
 */
export default function SectionCard({
  title,
  titleId,
  children,
  className = '',
  sx,
  contentSx,
}) {
  return (
    <Card
      component="section"
      className={`card ${className}`.trim()}
      aria-labelledby={titleId}
      sx={{ mb: 2, border: '1px solid #ddd8ef', ...sx }}
    >
      <CardContent sx={contentSx}>
        {title && (
          <Typography
            variant="h6"
            component="h2"
            id={titleId}
            sx={{ fontSize: '1.1rem', mb: 1 }}
          >
            {title}
          </Typography>
        )}
        {children}
      </CardContent>
    </Card>
  );
}
