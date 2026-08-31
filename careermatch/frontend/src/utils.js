export function matchColor(score) {
  if (score == null) return 'var(--color-surface-alt)'
  if (score >= 0.75) return 'var(--color-success)'
  if (score >= 0.45) return 'var(--color-warning)'
  return 'var(--color-danger)'
}
