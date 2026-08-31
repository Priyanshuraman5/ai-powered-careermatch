import { useState } from 'react'

export default function SkillGap() {
  const [jobDescription, setJobDescription] = useState('')
  const [analysis, setAnalysis] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleAnalyze = async () => {
    if (!jobDescription.trim()) return

    setLoading(true)
    setError('')
    setAnalysis(null)

    try {
      const response = await fetch('/api/skill-gap/analyze', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('cm_token')}`,
        },
        body: JSON.stringify({
          jobDescription: jobDescription.trim(),
        }),
      })

      if (!response.ok) {
        let message = 'Analysis failed'

        try {
          const data = await response.json()
          message =
            data?.message ||
            data?.error ||
            data?.detail ||
            message
        } catch {
          const text = await response.text()
          if (text) message = text
        }

        throw new Error(message)
      }

      const data = await response.json()
      setAnalysis(data)
    } catch (err) {
      console.error('Skill gap analysis failed:', err)
      setError(
        err?.message ||
          'Unable to analyze the job description. Please try again.'
      )
    } finally {
      setLoading(false)
    }
  }

  const atsScore = analysis?.atsScore ?? 0
  const matchingScore = analysis?.matchingScore ?? 0
  const profileStrength = analysis?.profileStrength ?? 0

  const matchedSkills = Array.isArray(analysis?.matchedSkills)
    ? analysis.matchedSkills
    : []

  const missingSkills = Array.isArray(analysis?.missingSkills)
    ? analysis.missingSkills
    : []

  const recommendations = Array.isArray(analysis?.recommendations)
    ? analysis.recommendations
    : []

  return (
    <div className="container" style={{ maxWidth: 1200 }}>
      {/* Header */}
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ marginBottom: 6 }}>Job Description Analyzer</h2>

        <p style={{ color: 'var(--color-text-muted)', margin: 0 }}>
          Paste a job description below to compare with your profile and get
          AI-powered analysis
        </p>
      </div>

      {/* Input Section */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: '1fr 1fr',
          gap: 16,
          marginBottom: 16,
        }}
      >
        {/* Job Description */}
        <div className="card">
          <h3 style={{ marginTop: 0 }}>1. Job Description</h3>

          <p
            style={{
              color: 'var(--color-text-muted)',
              fontSize: 14,
              marginBottom: 14,
            }}
          >
            Paste the job description you want to analyze
          </p>

          <textarea
            value={jobDescription}
            onChange={(e) => {
              setJobDescription(e.target.value)
              if (error) setError('')
            }}
            placeholder="Paste job description here..."
            maxLength={5000}
            style={{
              width: '100%',
              minHeight: 180,
              resize: 'vertical',
              boxSizing: 'border-box',
              padding: 14,
              borderRadius: 10,
              border: '1px solid var(--color-border)',
              background: 'var(--color-background)',
              color: 'var(--color-text)',
              outline: 'none',
              fontFamily: 'inherit',
              fontSize: 14,
            }}
          />

          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              marginTop: 10,
            }}
          >
            <span
              style={{
                color: 'var(--color-text-muted)',
                fontSize: 12,
              }}
            >
              {jobDescription.length} / 5000 characters
            </span>

            <button
              onClick={handleAnalyze}
              disabled={!jobDescription.trim() || loading}
              style={{
                padding: '10px 20px',
                border: 'none',
                borderRadius: 8,
                cursor:
                  jobDescription.trim() && !loading
                    ? 'pointer'
                    : 'not-allowed',
                opacity:
                  jobDescription.trim() && !loading ? 1 : 0.5,
              }}
            >
              {loading ? '⏳ Analyzing...' : '✨ Analyze Now'}
            </button>
          </div>

          {error && (
            <div
              style={{
                marginTop: 14,
                padding: 12,
                borderRadius: 8,
                border: '1px solid var(--color-danger, #ef4444)',
                color: 'var(--color-danger, #ef4444)',
                fontSize: 14,
              }}
            >
              {error}
            </div>
          )}
        </div>

        {/* My Profile */}
        <div className="card">
          <h3 style={{ marginTop: 0 }}>2. My Profile</h3>

          <p
            style={{
              color: 'var(--color-text-muted)',
              fontSize: 14,
              marginBottom: 14,
            }}
          >
            Your uploaded resume and extracted profile
          </p>

          <div
            style={{
              padding: 18,
              borderRadius: 10,
              background: 'var(--color-surface-alt)',
              border: '1px solid var(--color-border)',
            }}
          >
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 8,
                marginBottom: 14,
              }}
            >
              <span style={{ color: 'var(--color-success)' }}>●</span>

              <strong>Resume Uploaded</strong>
            </div>

            <div
              style={{
                display: 'grid',
                gridTemplateColumns: '1.5fr 1fr 1fr',
                gap: 16,
              }}
            >
              <div>
                <div
                  style={{
                    fontSize: 13,
                    color: 'var(--color-text-muted)',
                    marginBottom: 4,
                  }}
                >
                  Resume
                </div>

                <div>Raman_Resume.pdf</div>
              </div>

              <div>
                <div
                  style={{
                    fontSize: 13,
                    color: 'var(--color-text-muted)',
                    marginBottom: 4,
                  }}
                >
                  Skills Detected
                </div>

                <strong>
                  {analysis?.profileSkillCount ?? '--'}
                </strong>
              </div>

              <div>
                <div
                  style={{
                    fontSize: 13,
                    color: 'var(--color-text-muted)',
                    marginBottom: 4,
                  }}
                >
                  Experience
                </div>

                <strong>
                  {analysis?.experience ?? '--'}
                </strong>
              </div>
            </div>
          </div>

          <button
            style={{
              marginTop: 16,
              padding: '10px 18px',
              borderRadius: 8,
              cursor: 'pointer',
            }}
          >
            👤 View My Profile
          </button>
        </div>
      </div>

      {/* Analysis Results */}
      <div className="card">
        <div style={{ marginBottom: 20 }}>
          <h3 style={{ margin: 0 }}>📊 Analysis Results</h3>

          <p
            style={{
              color: 'var(--color-text-muted)',
              fontSize: 14,
              marginTop: 6,
            }}
          >
            AI-powered comparison of job description with your profile
          </p>
        </div>

        {/* Scores */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(3, 1fr)',
            gap: 16,
            marginBottom: 24,
          }}
        >
          <ScoreCard
            title="ATS Score"
            value={`${atsScore} / 100`}
            percentage={atsScore}
            description="How well your profile passes ATS scan"
          />

          <ScoreCard
            title="Matching Score"
            value={`${matchingScore}%`}
            percentage={matchingScore}
            description="Overall match between JD and your profile"
          />

          <ScoreCard
            title="Profile Strength"
            value={`${profileStrength}%`}
            percentage={profileStrength}
            description="Strength of your profile for this role"
          />
        </div>

        {/* Matched / Missing Skills */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: 16,
            borderTop: '1px solid var(--color-border)',
            paddingTop: 20,
          }}
        >
          <SkillBox
            title="Matched Skills"
            icon="✓"
            description="Skills you have that match the job requirements"
            skills={matchedSkills}
            emptyText={
              analysis
                ? 'No matching skills found'
                : 'Your matched skills will appear here'
            }
            type="matched"
          />

          <SkillBox
            title="Missing Skills"
            icon="×"
            description="Skills required in the job but missing in your profile"
            skills={missingSkills}
            emptyText={
              analysis
                ? 'No missing skills identified'
                : 'Missing skills will appear here'
            }
            type="missing"
          />
        </div>

        {/* AI Recommendations */}
        <div
          style={{
            borderTop: '1px solid var(--color-border)',
            marginTop: 20,
            paddingTop: 20,
          }}
        >
          <h3 style={{ marginBottom: 6 }}>💡 AI Recommendations</h3>

          <p
            style={{
              color: 'var(--color-text-muted)',
              fontSize: 14,
              marginTop: 0,
            }}
          >
            Personalized recommendations to improve your match
          </p>

          {!analysis && !loading && (
            <div
              style={{
                padding: 28,
                textAlign: 'center',
                border: '1px dashed var(--color-border)',
                borderRadius: 10,
                color: 'var(--color-text-muted)',
              }}
            >
              <div style={{ fontSize: 24, marginBottom: 8 }}>✦</div>

              <div style={{ color: 'var(--color-text)' }}>
                AI recommendations will appear here
              </div>

              <div style={{ fontSize: 13, marginTop: 6 }}>
                Complete the analysis to get personalized suggestions
              </div>
            </div>
          )}

          {loading && (
            <div
              style={{
                padding: 28,
                textAlign: 'center',
                border: '1px dashed var(--color-border)',
                borderRadius: 10,
                color: 'var(--color-text-muted)',
              }}
            >
              Analyzing your profile against the job description...
            </div>
          )}

          {analysis && !loading && (
            <div
              style={{
                border: '1px solid var(--color-border)',
                borderRadius: 10,
                padding: 18,
              }}
            >
              {recommendations.length > 0 ? (
                recommendations.map((recommendation, index) => (
                  <div
                    key={`${recommendation}-${index}`}
                    style={{
                      display: 'flex',
                      gap: 10,
                      padding: '12px 0',
                      borderBottom:
                        index < recommendations.length - 1
                          ? '1px solid var(--color-border)'
                          : 'none',
                    }}
                  >
                    <span>💡</span>

                    <span>{recommendation}</span>
                  </div>
                ))
              ) : (
                <div
                  style={{
                    color: 'var(--color-text-muted)',
                    textAlign: 'center',
                  }}
                >
                  No additional recommendations were generated.
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

function ScoreCard({
  title,
  value,
  percentage,
  description,
}) {
  const safePercentage = Math.max(
    0,
    Math.min(100, Number(percentage) || 0)
  )

  return (
    <div
      style={{
        padding: 18,
        borderRadius: 10,
        background: 'var(--color-surface-alt)',
        border: '1px solid var(--color-border)',
      }}
    >
      <div style={{ marginBottom: 12 }}>
        <strong>{title}</strong>
      </div>

      <div
        style={{
          fontSize: 28,
          fontWeight: 700,
          marginBottom: 12,
        }}
      >
        {value}
      </div>

      <div
        style={{
          height: 8,
          background: 'var(--color-background)',
          borderRadius: 5,
          overflow: 'hidden',
          marginBottom: 10,
        }}
      >
        <div
          style={{
            width: `${safePercentage}%`,
            height: '100%',
            transition: 'width 0.4s ease',
          }}
        />
      </div>

      <small style={{ color: 'var(--color-text-muted)' }}>
        {description}
      </small>
    </div>
  )
}

function SkillBox({
  title,
  icon,
  description,
  skills,
  emptyText,
  type,
}) {
  return (
    <div>
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: 10,
          marginBottom: 6,
        }}
      >
        <span style={{ fontSize: 20 }}>{icon}</span>

        <h3 style={{ margin: 0 }}>{title}</h3>
      </div>

      <p
        style={{
          color: 'var(--color-text-muted)',
          fontSize: 13,
          marginTop: 0,
        }}
      >
        {description}
      </p>

      {skills.length > 0 ? (
        <div
          style={{
            minHeight: 90,
            padding: 16,
            border: '1px solid var(--color-border)',
            borderRadius: 10,
            display: 'flex',
            flexWrap: 'wrap',
            alignContent: 'flex-start',
            gap: 8,
          }}
        >
          {skills.map((skill) => (
            <span
              key={skill}
              style={{
                padding: '7px 10px',
                borderRadius: 7,
                border: '1px solid var(--color-border)',
                background:
                  type === 'matched'
                    ? 'var(--color-surface-alt)'
                    : 'var(--color-background)',
                fontSize: 13,
              }}
            >
              {type === 'matched' ? '✓ ' : '× '}
              {skill}
            </span>
          ))}
        </div>
      ) : (
        <div
          style={{
            minHeight: 90,
            padding: 20,
            border: '1px dashed var(--color-border)',
            borderRadius: 10,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            textAlign: 'center',
            color: 'var(--color-text-muted)',
          }}
        >
          <div>{emptyText}</div>

          <small style={{ marginTop: 6 }}>
            Paste a job description and click Analyze Now
          </small>
        </div>
      )}
    </div>
  )
}