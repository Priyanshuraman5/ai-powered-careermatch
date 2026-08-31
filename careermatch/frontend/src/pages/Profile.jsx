import React, { useEffect, useState } from "react";
import { userApi, codingApi, resumeApi, resolveFileUrl } from "../api/client";
import "./Profile.css";

const emptyProfile = {
  fullName: "",
  headline: "",
  location: "",
  phone: "",
  about: "",
  profileImage: "",

  skills: [],
  education: [],
  certifications: [],
  experience: [],
  projects: [],
  achievements: [],
  codingProfiles: [],
  resume: null,
  resumeName: "",
  certificateFiles: [],
};

const emptyEducation = {
  institution: "",
  degree: "",
  fieldOfStudy: "",
  startYear: "",
  endYear: "",
  cgpa: "",
  description: "",
};

const emptyCertification = {
  name: "",
  issuer: "",
  issueDate: "",
  expiryDate: "",
  credentialId: "",
  description: "",
};

const emptyExperience = {
  company: "",
  position: "",
  employmentType: "",
  location: "",
  startDate: "",
  endDate: "",
  currentlyWorking: false,
  description: "",
};

const emptyProject = {
  name: "",
  description: "",
  technologies: "",
  startDate: "",
  endDate: "",
};

const emptyAchievement = {
  title: "",
  description: "",
  date: "",
};

const emptyCodingProfile = {
  platform: "",
  username: "",
};

// Fields the backend may legitimately return as `null` (nothing saved yet)
// but that the UI always needs as an array to safely call .length/.map on.
const LIST_FIELDS = [
  "skills",
  "education",
  "certifications",
  "experience",
  "projects",
  "achievements",
  "codingProfiles",
  "certificateFiles",
];

// Merges an API response into the profile shape the UI expects: fills in
// defaults, coerces any null list field to [], and derives the `resume`
// object from resumeName/resumeUrl. Use this everywhere profile state is
// set from a server response instead of hand-rolling the merge, so a
// null coming back from the DB (e.g. a field nobody has saved yet) can
// never crash a .length/.map call in the render.
function normalizeProfile(data) {
  const merged = { ...emptyProfile, ...(data || {}) };

  LIST_FIELDS.forEach((field) => {
    if (!Array.isArray(merged[field])) {
      merged[field] = [];
    }
  });

  merged.resume = data?.resumeName
    ? { name: data.resumeName, url: data.resumeUrl }
    : null;

  return merged;
}

function Profile() {
  const [profile, setProfile] = useState(emptyProfile);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [editingBasic, setEditingBasic] = useState(false);
  const [basicForm, setBasicForm] = useState(emptyProfile);

  const [modal, setModal] = useState(null);
  const [editingIndex, setEditingIndex] = useState(null);
  const [form, setForm] = useState({});

  const [skillInput, setSkillInput] = useState("");
  const [message, setMessage] = useState("");

  /* ==============================
     LOAD PROFILE
  ============================== */

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      setLoading(true);

      const response = await userApi.getProfile();
      const data = response.data || {};

      setProfile(normalizeProfile(data));
    } catch (error) {
      console.error("Failed to load profile:", error);
      showMessage("Unable to load profile");
    } finally {
      setLoading(false);
    }
  };

  /* ==============================
     MESSAGE
  ============================== */

  const showMessage = (text) => {
    setMessage(text);

    setTimeout(() => {
      setMessage("");
    }, 3000);
  };

  /* ==============================
     BASIC PROFILE
  ============================== */

  const openBasicEdit = () => {
    setBasicForm({
      ...profile,
    });

    setEditingBasic(true);
  };

  const handleBasicChange = (e) => {
    const { name, value } = e.target;

    setBasicForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const saveBasicProfile = async () => {
    try {
      setSaving(true);

      // Preserve all profile fields (including profileImage and resumeName) when saving text updates
      const updatedProfileData = {
        ...profile,
        fullName: basicForm.fullName,
        headline: basicForm.headline,
        location: basicForm.location,
        phone: basicForm.phone,
        about: basicForm.about,
      };

      const response = await userApi.updateProfile(updatedProfileData);
      const data = response.data || updatedProfileData;

      setProfile(normalizeProfile(data));

      setEditingBasic(false);

      showMessage("Profile saved successfully");
    } catch (error) {
      console.error("Failed to save profile:", error);
      showMessage("Failed to save profile");
    } finally {
      setSaving(false);
    }
  };

  /* ==============================
     DYNAMIC SECTION MODAL
  ============================== */

  const openModal = (type, index = null) => {
    setModal(type);
    setEditingIndex(index);

    if (index !== null) {
      setForm({
        ...profile[type][index],
      });

      return;
    }

    switch (type) {
      case "education":
        setForm({ ...emptyEducation });
        break;

      case "certifications":
        setForm({ ...emptyCertification });
        break;

      case "experience":
        setForm({ ...emptyExperience });
        break;

      case "projects":
        setForm({ ...emptyProject });
        break;

      case "achievements":
        setForm({ ...emptyAchievement });
        break;

      case "codingProfiles":
        setForm({ ...emptyCodingProfile });
        break;

      default:
        setForm({});
    }
  };

  const closeModal = () => {
    setModal(null);
    setEditingIndex(null);
    setForm({});
  };

  const handleFormChange = (e) => {
    const { name, value, type, checked } = e.target;

    setForm((previous) => ({
      ...previous,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  /* ==============================
     SAVE SECTION
  ============================== */

  const saveSection = async () => {
    if (!modal) return;

    try {
      setSaving(true);

      const updatedItems = [...(profile[modal] || [])];

      if (editingIndex !== null) {
        updatedItems[editingIndex] = form;
      } else {
        updatedItems.push(form);
      }

      const updatedProfileData = {
        ...profile,
        [modal]: updatedItems,
      };

      const response = await userApi.updateProfile(updatedProfileData);
      const data = response.data || updatedProfileData;

      setProfile(normalizeProfile(data));

      showMessage(
        editingIndex !== null
          ? "Updated successfully"
          : "Added successfully"
      );

      closeModal();
    } catch (error) {
      console.error("Failed to save:", error);
      showMessage("Failed to save to server");
    } finally {
      setSaving(false);
    }
  };

  /* ==============================
     DELETE SECTION ITEM
  ============================== */

  const deleteItem = async (type, index) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this item?"
    );

    if (!confirmed) return;

    try {
      setSaving(true);
      const updatedItems = profile[type].filter(
        (_, itemIndex) => itemIndex !== index
      );

      const updatedProfileData = {
        ...profile,
        [type]: updatedItems,
      };

      const response = await userApi.updateProfile(updatedProfileData);
      const data = response.data || updatedProfileData;

      setProfile(normalizeProfile(data));

      showMessage("Deleted successfully");
    } catch (error) {
      console.error("Failed to delete:", error);
      showMessage("Failed to delete from server");
    } finally {
      setSaving(false);
    }
  };

  /* ==============================
     SKILLS
  ============================== */

  const addSkill = async () => {
    const skill = skillInput.trim();
    if (!skill) return;

    const exists = profile.skills.some(
      (item) => item.toLowerCase() === skill.toLowerCase()
    );

    if (exists) {
      setSkillInput("");
      return;
    }

    const updatedSkills = [...profile.skills, skill];
    const updatedProfileData = {
      ...profile,
      skills: updatedSkills,
    };

    try {
      setSaving(true);
      const response = await userApi.updateProfile(updatedProfileData);
      const data = response.data || updatedProfileData;

      setProfile(normalizeProfile(data));

      setSkillInput("");
      showMessage("Skill added successfully");
    } catch (error) {
      console.error("Failed to add skill:", error);
      showMessage("Failed to save skill");
    } finally {
      setSaving(false);
    }
  };

  const removeSkill = async (index) => {
    const skill = profile.skills[index];
    const updatedSkills = profile.skills.filter(
      (_, itemIndex) => itemIndex !== index
    );

    const updatedProfileData = {
      ...profile,
      skills: updatedSkills,
    };

    try {
      setSaving(true);
      const response = await userApi.updateProfile(updatedProfileData);
      const data = response.data || updatedProfileData;

      setProfile(normalizeProfile(data));

      showMessage(`${skill} removed`);
    } catch (error) {
      console.error("Failed to remove skill:", error);
      showMessage("Failed to remove skill");
    } finally {
      setSaving(false);
    }
  };

  const handleSkillKeyDown = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      addSkill();
    }
  };

  /* ==============================
     PROFILE IMAGE (PERSISTENT)
  ============================== */

  const handleProfileImage = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      setSaving(true);

      // Real upload: the file is sent to the server and stored on disk.
      // The response contains the persisted profile with a profileImage URL
      // (not a base64 blob), so it survives refresh and server restarts.
      const response = await userApi.uploadProfilePicture(file);
      const data = response.data;

      setProfile(normalizeProfile(data));

      showMessage("Profile picture saved successfully!");
    } catch (error) {
      console.error("Failed to save picture:", error);
      showMessage("Failed to save picture to server");
    } finally {
      setSaving(false);
      e.target.value = "";
    }
  };

  /* ==============================
     RESUME (PERSISTENT)
  ============================== */

  const handleResume = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      setSaving(true);

      // Real upload: previously this only sent {name: file.name} to the
      // server - the actual PDF/DOCX bytes never left the browser, so
      // nothing existed to fetch back after a refresh.
      const response = await userApi.uploadResume(file);
      const data = response.data;

      setProfile(normalizeProfile(data));

      showMessage("Resume saved permanently!");
    } catch (error) {
      console.error("Failed to save resume:", error);
      showMessage("Failed to save resume to server");
    } finally {
      setSaving(false);
      e.target.value = "";
    }
  };

  /* ==============================
     CERTIFICATE FILES (PERSISTENT)
  ============================== */

  const handleCertificateFiles = async (e) => {
    const files = Array.from(e.target.files || []);
    if (files.length === 0) return;

    try {
      setSaving(true);

      const response = await userApi.uploadCertificateFiles(files);
      const data = response.data;

      setProfile(normalizeProfile(data));

      showMessage("Certificate uploaded successfully!");
    } catch (error) {
      console.error("Failed to upload certificate:", error);
      showMessage("Failed to upload certificate to server");
    } finally {
      setSaving(false);
      e.target.value = "";
    }
  };

  /* ==============================
     LOADING
  ============================== */

  if (loading) {
    return (
      <div className="profile-loading">
        <div className="profile-spinner"></div>
        <p>Loading profile...</p>
      </div>
    );
  }

  return (
    <div className="profile-page">
      {message && <div className="profile-toast">{message}</div>}

      {/* COVER */}
      <div className="profile-cover"></div>

      {/* HEADER */}
      <section className="profile-header-card">
        <div className="profile-header-content">
          <div className="profile-picture-wrapper">
            {profile.profileImage ? (
              <img
                src={resolveFileUrl(profile.profileImage)}
                alt="Profile"
                className="profile-picture"
              />
            ) : (
              <div className="profile-picture-placeholder">
                {profile.fullName
                  ? profile.fullName.charAt(0).toUpperCase()
                  : "U"}
              </div>
            )}

            <label className="photo-upload-button">
              📷
              <input
                type="file"
                accept="image/*"
                onChange={handleProfileImage}
              />
            </label>
          </div>

          <div className="profile-main-info">
            <h1>{profile.fullName || "Your Name"}</h1>

            <p className="profile-headline">
              {profile.headline || "Add your professional headline"}
            </p>

            {profile.location && (
              <p className="profile-location">📍 {profile.location}</p>
            )}
          </div>

          <button className="edit-profile-button" onClick={openBasicEdit}>
            ✏ Edit Profile
          </button>
        </div>
      </section>

      {/* CONTENT */}
      <main className="profile-content">
        {/* ABOUT */}
        <ProfileSection
          title="About"
          action={
            <button className="section-edit-button" onClick={openBasicEdit}>
              ✏ Edit
            </button>
          }
        >
          {profile.about ? (
            <p className="about-text">{profile.about}</p>
          ) : (
            <EmptyState text="Add information about yourself, your interests and career goals." />
          )}
        </ProfileSection>

        {/* EDUCATION */}
        <ProfileSection
          title="Education"
          action={
            <button
              className="add-button"
              onClick={() => openModal("education")}
            >
              + Add Education
            </button>
          }
        >
          {profile.education.length > 0 ? (
            profile.education.map((item, index) => (
              <EducationCard
                key={item.id || index}
                education={item}
                onEdit={() => openModal("education", index)}
                onDelete={() => deleteItem("education", index)}
              />
            ))
          ) : (
            <EmptyState text="Add your school, college or university." />
          )}
        </ProfileSection>

        {/* SKILLS */}
        <ProfileSection
          title="Skills"
          action={
            <div className="skill-add-area">
              <input
                type="text"
                placeholder="Add skill"
                value={skillInput}
                onChange={(e) => setSkillInput(e.target.value)}
                onKeyDown={handleSkillKeyDown}
              />
              <button className="add-button" onClick={addSkill}>
                + Add
              </button>
            </div>
          }
        >
          {profile.skills.length > 0 ? (
            <div className="skills-container">
              {profile.skills.map((skill, index) => (
                <div className="skill-chip" key={index}>
                  {skill}
                  <button onClick={() => removeSkill(index)}>×</button>
                </div>
              ))}
            </div>
          ) : (
            <EmptyState text="Add your technical and professional skills." />
          )}
        </ProfileSection>

        {/* EXPERIENCE */}
        <ProfileSection
          title="Experience"
          action={
            <button
              className="add-button"
              onClick={() => openModal("experience")}
            >
              + Add Experience
            </button>
          }
        >
          {profile.experience.length > 0 ? (
            profile.experience.map((item, index) => (
              <ExperienceCard
                key={item.id || index}
                experience={item}
                onEdit={() => openModal("experience", index)}
                onDelete={() => deleteItem("experience", index)}
              />
            ))
          ) : (
            <EmptyState text="Add internships, jobs or professional experience." />
          )}
        </ProfileSection>

        {/* CERTIFICATIONS */}
        <ProfileSection
          title="Certifications"
          action={
            <button
              className="add-button"
              onClick={() => openModal("certifications")}
            >
              + Add Certificate
            </button>
          }
        >
          {profile.certifications.length > 0 ? (
            profile.certifications.map((item, index) => (
              <CertificationCard
                key={item.id || index}
                certificate={item}
                onEdit={() => openModal("certifications", index)}
                onDelete={() => deleteItem("certifications", index)}
              />
            ))
          ) : (
            <EmptyState text="Add your certificates and professional credentials." />
          )}
        </ProfileSection>

        {/* CERTIFICATE FILES (actual uploaded documents) */}
        <ProfileSection
          title="Certificate Documents"
          action={
            <label className="add-button upload-resume-button">
              + Upload Certificate File
              <input
                type="file"
                accept=".pdf,.jpg,.jpeg,.png"
                multiple
                onChange={handleCertificateFiles}
              />
            </label>
          }
        >
          {profile.certificateFiles && profile.certificateFiles.length > 0 ? (
            <div className="skills-container">
              {profile.certificateFiles.map((cert, index) => (
                <a
                  key={index}
                  className="resume-card"
                  href={resolveFileUrl(cert.url)}
                  target="_blank"
                  rel="noreferrer"
                >
                  📄 {cert.name}
                </a>
              ))}
            </div>
          ) : (
            <EmptyState text="Upload scanned copies of your certificates (PDF or image)." />
          )}
        </ProfileSection>

        {/* PROJECTS */}
        <ProfileSection
          title="Projects"
          action={
            <button
              className="add-button"
              onClick={() => openModal("projects")}
            >
              + Add Project
            </button>
          }
        >
          {profile.projects.length > 0 ? (
            <div className="projects-grid">
              {profile.projects.map((item, index) => (
                <ProjectCard
                  key={item.id || index}
                  project={item}
                  onEdit={() => openModal("projects", index)}
                  onDelete={() => deleteItem("projects", index)}
                />
              ))}
            </div>
          ) : (
            <EmptyState text="Add projects you have built or contributed to." />
          )}
        </ProfileSection>

        {/* ACHIEVEMENTS */}
        <ProfileSection
          title="Achievements"
          action={
            <button
              className="add-button"
              onClick={() => openModal("achievements")}
            >
              + Add Achievement
            </button>
          }
        >
          {profile.achievements.length > 0 ? (
            profile.achievements.map((item, index) => (
              <AchievementCard
                key={item.id || index}
                achievement={item}
                onEdit={() => openModal("achievements", index)}
                onDelete={() => deleteItem("achievements", index)}
              />
            ))
          ) : (
            <EmptyState text="Add awards, hackathons, competitions and other achievements." />
          )}
        </ProfileSection>

        {/* RESUME */}
        <ProfileSection
          title="Resume"
          action={
            <label className="add-button upload-resume-button">
              + Upload Resume
              <input
                type="file"
                accept=".pdf,.doc,.docx"
                onChange={handleResume}
              />
            </label>
          }
        >
          {profile.resume ? (
            <a
              className="resume-card"
              href={resolveFileUrl(profile.resume.url)}
              target="_blank"
              rel="noreferrer"
            >
              <div className="resume-icon">📄</div>
              <div className="resume-info">
                <strong>{profile.resume.name}</strong>
                <p>Your uploaded resume — click to view/download</p>
              </div>
            </a>
          ) : (
            <EmptyState text="Upload your resume for AI job matching and skill-gap analysis." />
          )}
        </ProfileSection>
      </main>

      {/* BASIC PROFILE MODAL */}
      {editingBasic && (
        <Modal title="Edit Profile" onClose={() => setEditingBasic(false)}>
          <div className="form-grid">
            <FormField label="Full Name">
              <input
                name="fullName"
                value={basicForm.fullName || ""}
                onChange={handleBasicChange}
              />
            </FormField>

            <FormField label="Professional Headline">
              <input
                name="headline"
                value={basicForm.headline || ""}
                onChange={handleBasicChange}
              />
            </FormField>

            <FormField label="Location">
              <input
                name="location"
                value={basicForm.location || ""}
                onChange={handleBasicChange}
              />
            </FormField>

            <FormField label="Phone">
              <input
                name="phone"
                value={basicForm.phone || ""}
                onChange={handleBasicChange}
              />
            </FormField>

            <FormField label="About" full>
              <textarea
                name="about"
                rows="6"
                value={basicForm.about || ""}
                onChange={handleBasicChange}
                placeholder="Tell us about yourself..."
              />
            </FormField>
          </div>

          <ModalActions
            onCancel={() => setEditingBasic(false)}
            onSave={saveBasicProfile}
            saving={saving}
          />
        </Modal>
      )}

      {/* DYNAMIC MODAL */}
      {modal && (
        <Modal
          title={
            editingIndex !== null
              ? `Edit ${getModalTitle(modal)}`
              : `Add ${getModalTitle(modal)}`
          }
          onClose={closeModal}
        >
          {modal === "education" && (
            <EducationForm form={form} onChange={handleFormChange} />
          )}

          {modal === "certifications" && (
            <CertificationForm form={form} onChange={handleFormChange} />
          )}

          {modal === "experience" && (
            <ExperienceForm form={form} onChange={handleFormChange} />
          )}

          {modal === "projects" && (
            <ProjectForm form={form} onChange={handleFormChange} />
          )}

          {modal === "achievements" && (
            <AchievementForm form={form} onChange={handleFormChange} />
          )}

          {modal === "codingProfiles" && (
            <CodingProfileForm form={form} onChange={handleFormChange} />
          )}

          <ModalActions
            onCancel={closeModal}
            onSave={saveSection}
            saving={saving}
          />
        </Modal>
      )}
    </div>
  );
}

/* =====================================================
   SECTION
===================================================== */

function ProfileSection({ title, action, children }) {
  return (
    <section className="profile-section">
      <div className="section-header">
        <h2>{title}</h2>
        <div>{action}</div>
      </div>
      <div className="section-content">{children}</div>
    </section>
  );
}

/* =====================================================
   EMPTY STATE
===================================================== */

function EmptyState({ text }) {
  return (
    <div className="empty-state">
      <div className="empty-icon">+</div>
      <p>{text}</p>
    </div>
  );
}

/* =====================================================
   EDUCATION
===================================================== */

function EducationCard({ education, onEdit, onDelete }) {
  return (
    <div className="timeline-card">
      <div className="timeline-icon">🎓</div>
      <div className="timeline-content">
        <h3>{education.degree || "Education"}</h3>
        <h4>{education.institution}</h4>
        {education.fieldOfStudy && <p>{education.fieldOfStudy}</p>}
        {(education.startYear || education.endYear) && (
          <span className="muted-text">
            {education.startYear} - {education.endYear || "Present"}
          </span>
        )}
        {education.cgpa && <p className="small-info">CGPA: {education.cgpa}</p>}
        {education.description && (
          <p className="card-description">{education.description}</p>
        )}
      </div>
      <CardActions onEdit={onEdit} onDelete={onDelete} />
    </div>
  );
}

/* =====================================================
   EXPERIENCE
===================================================== */

function ExperienceCard({ experience, onEdit, onDelete }) {
  return (
    <div className="timeline-card">
      <div className="timeline-icon">💼</div>
      <div className="timeline-content">
        <h3>{experience.position}</h3>
        <h4>{experience.company}</h4>
        {experience.employmentType && (
          <span className="type-badge">{experience.employmentType}</span>
        )}
        <span className="muted-text">
          {experience.startDate} -{" "}
          {experience.currentlyWorking ? "Present" : experience.endDate}
        </span>
        {experience.location && (
          <p className="small-info">📍 {experience.location}</p>
        )}
        {experience.description && (
          <p className="card-description">{experience.description}</p>
        )}
      </div>
      <CardActions onEdit={onEdit} onDelete={onDelete} />
    </div>
  );
}

/* =====================================================
   CERTIFICATION
===================================================== */

function CertificationCard({ certificate, onEdit, onDelete }) {
  return (
    <div className="timeline-card">
      <div className="timeline-icon">🏆</div>
      <div className="timeline-content">
        <h3>{certificate.name}</h3>
        {certificate.issuer && <h4>{certificate.issuer}</h4>}
        {certificate.issueDate && (
          <span className="muted-text">Issued: {certificate.issueDate}</span>
        )}
        {certificate.credentialId && (
          <p className="small-info">
            Credential ID: {certificate.credentialId}
          </p>
        )}
        {certificate.description && (
          <p className="card-description">{certificate.description}</p>
        )}
      </div>
      <CardActions onEdit={onEdit} onDelete={onDelete} />
    </div>
  );
}

/* =====================================================
   PROJECT
===================================================== */

function ProjectCard({ project, onEdit, onDelete }) {
  const technologies = project.technologies
    ? project.technologies
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean)
    : [];

  return (
    <div className="project-card">
      <div className="project-top">
        <div className="project-icon">🚀</div>
        <CardActions onEdit={onEdit} onDelete={onDelete} />
      </div>
      <h3>{project.name}</h3>
      {project.description && <p>{project.description}</p>}
      {technologies.length > 0 && (
        <div className="project-technologies">
          {technologies.map((technology, index) => (
            <span key={index}>{technology}</span>
          ))}
        </div>
      )}
    </div>
  );
}

/* =====================================================
   ACHIEVEMENT
===================================================== */

function AchievementCard({ achievement, onEdit, onDelete }) {
  return (
    <div className="timeline-card">
      <div className="timeline-icon">🥇</div>
      <div className="timeline-content">
        <h3>{achievement.title}</h3>
        {achievement.date && (
          <span className="muted-text">{achievement.date}</span>
        )}
        {achievement.description && (
          <p className="card-description">{achievement.description}</p>
        )}
      </div>
      <CardActions onEdit={onEdit} onDelete={onDelete} />
    </div>
  );
}

/* =====================================================
   CARD ACTIONS
===================================================== */

function CardActions({ onEdit, onDelete }) {
  return (
    <div className="card-actions">
      <button onClick={onEdit} title="Edit">
        ✏
      </button>
      <button onClick={onDelete} title="Delete">
        🗑
      </button>
    </div>
  );
}

/* =====================================================
   MODAL
===================================================== */

function Modal({ title, onClose, children }) {
  return (
    <div className="modal-overlay" onMouseDown={onClose}>
      <div className="modal" onMouseDown={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{title}</h2>
          <button onClick={onClose}>×</button>
        </div>
        <div className="modal-body">{children}</div>
      </div>
    </div>
  );
}

/* =====================================================
   MODAL ACTIONS
===================================================== */

function ModalActions({ onCancel, onSave, saving }) {
  return (
    <div className="modal-actions">
      <button className="secondary-button" onClick={onCancel}>
        Cancel
      </button>
      <button className="primary-button" onClick={onSave} disabled={saving}>
        {saving ? "Saving..." : "Save"}
      </button>
    </div>
  );
}

/* =====================================================
   FORM FIELD
===================================================== */

function FormField({ label, children, full = false }) {
  return (
    <div className={`form-field ${full ? "form-field-full" : ""}`}>
      <label>{label}</label>
      {children}
    </div>
  );
}

/* =====================================================
   EDUCATION FORM
===================================================== */

function EducationForm({ form, onChange }) {
  return (
    <div className="form-grid">
      <FormField label="College / University">
        <input
          name="institution"
          value={form.institution || ""}
          onChange={onChange}
          placeholder="College or University"
        />
      </FormField>

      <FormField label="Degree">
        <input
          name="degree"
          value={form.degree || ""}
          onChange={onChange}
          placeholder="B.Tech, M.Tech, BCA..."
        />
      </FormField>

      <FormField label="Field of Study">
        <input
          name="fieldOfStudy"
          value={form.fieldOfStudy || ""}
          onChange={onChange}
          placeholder="Computer Science"
        />
      </FormField>

      <FormField label="CGPA / Percentage">
        <input
          name="cgpa"
          value={form.cgpa || ""}
          onChange={onChange}
          placeholder="9.0 / 90%"
        />
      </FormField>

      <FormField label="Start Year">
        <input
          name="startYear"
          value={form.startYear || ""}
          onChange={onChange}
          placeholder="2023"
        />
      </FormField>

      <FormField label="End Year">
        <input
          name="endYear"
          value={form.endYear || ""}
          onChange={onChange}
          placeholder="2027"
        />
      </FormField>

      <FormField label="Description" full>
        <textarea
          name="description"
          rows="4"
          value={form.description || ""}
          onChange={onChange}
        />
      </FormField>
    </div>
  );
}

/* =====================================================
   CERTIFICATION FORM
===================================================== */

function CertificationForm({ form, onChange }) {
  return (
    <div className="form-grid">
      <FormField label="Certificate Name">
        <input name="name" value={form.name || ""} onChange={onChange} />
      </FormField>

      <FormField label="Issuing Organization">
        <input name="issuer" value={form.issuer || ""} onChange={onChange} />
      </FormField>

      <FormField label="Issue Date">
        <input
          type="date"
          name="issueDate"
          value={form.issueDate || ""}
          onChange={onChange}
        />
      </FormField>

      <FormField label="Expiry Date">
        <input
          type="date"
          name="expiryDate"
          value={form.expiryDate || ""}
          onChange={onChange}
        />
      </FormField>

      <FormField label="Credential ID">
        <input
          name="credentialId"
          value={form.credentialId || ""}
          onChange={onChange}
        />
      </FormField>

      <FormField label="Description" full>
        <textarea
          name="description"
          rows="4"
          value={form.description || ""}
          onChange={onChange}
        />
      </FormField>
    </div>
  );
}

/* =====================================================
   EXPERIENCE FORM
===================================================== */

function ExperienceForm({ form, onChange }) {
  return (
    <div className="form-grid">
      <FormField label="Company">
        <input name="company" value={form.company || ""} onChange={onChange} />
      </FormField>

      <FormField label="Position">
        <input
          name="position"
          value={form.position || ""}
          onChange={onChange}
        />
      </FormField>

      <FormField label="Employment Type">
        <select
          name="employmentType"
          value={form.employmentType || ""}
          onChange={onChange}
        >
          <option value="">Select</option>
          <option value="Internship">Internship</option>
          <option value="Full-time">Full-time</option>
          <option value="Part-time">Part-time</option>
          <option value="Freelance">Freelance</option>
          <option value="Contract">Contract</option>
        </select>
      </FormField>

      <FormField label="Location">
        <input
          name="location"
          value={form.location || ""}
          onChange={onChange}
        />
      </FormField>

      <FormField label="Start Date">
        <input
          type="date"
          name="startDate"
          value={form.startDate || ""}
          onChange={onChange}
        />
      </FormField>

      <FormField label="End Date">
        <input
          type="date"
          name="endDate"
          value={form.endDate || ""}
          onChange={onChange}
          disabled={form.currentlyWorking}
        />
      </FormField>

      <FormField label="Currently Working">
        <label className="checkbox-field">
          <input
            type="checkbox"
            name="currentlyWorking"
            checked={form.currentlyWorking || false}
            onChange={onChange}
          />
          Currently working here
        </label>
      </FormField>

      <FormField label="Description" full>
        <textarea
          name="description"
          rows="5"
          value={form.description || ""}
          onChange={onChange}
          placeholder="Describe your responsibilities..."
        />
      </FormField>
    </div>
  );
}

/* =====================================================
   PROJECT FORM
===================================================== */

function ProjectForm({ form, onChange }) {
  return (
    <div className="form-grid">
      <FormField label="Project Name">
        <input name="name" value={form.name || ""} onChange={onChange} />
      </FormField>

      <FormField label="Technologies">
        <input
          name="technologies"
          value={form.technologies || ""}
          onChange={onChange}
          placeholder="Java, Spring Boot, React..."
        />
      </FormField>

      <FormField label="Start Date">
        <input
          type="date"
          name="startDate"
          value={form.startDate || ""}
          onChange={onChange}
        />
      </FormField>

      <FormField label="End Date">
        <input
          type="date"
          name="endDate"
          value={form.endDate || ""}
          onChange={onChange}
        />
      </FormField>

      <FormField label="Description" full>
        <textarea
          name="description"
          rows="5"
          value={form.description || ""}
          onChange={onChange}
          placeholder="Describe your project..."
        />
      </FormField>
    </div>
  );
}

/* =====================================================
   ACHIEVEMENT FORM
===================================================== */

function AchievementForm({ form, onChange }) {
  return (
    <div className="form-grid">
      <FormField label="Achievement">
        <input
          name="title"
          value={form.title || ""}
          onChange={onChange}
          placeholder="Hackathon Winner"
        />
      </FormField>

      <FormField label="Date">
        <input
          type="date"
          name="date"
          value={form.date || ""}
          onChange={onChange}
        />
      </FormField>

      <FormField label="Description" full>
        <textarea
          name="description"
          rows="5"
          value={form.description || ""}
          onChange={onChange}
        />
      </FormField>
    </div>
  );
}

/* =====================================================
   HELPERS
===================================================== */

function getModalTitle(type) {
  switch (type) {
    case "education":
      return "Education";
    case "certifications":
      return "Certification";
    case "experience":
      return "Experience";
    case "projects":
      return "Project";
    case "achievements":
      return "Achievement";
    default:
      return "Item";
  }
}

export default Profile;