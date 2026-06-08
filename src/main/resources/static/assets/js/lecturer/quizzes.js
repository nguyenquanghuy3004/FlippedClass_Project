const API_URL = '/api/quizzes';
let currentQuizzes = [];

document.addEventListener("DOMContentLoaded", () => {
  fetchQuizzes();
});

function showAlert(message, isError = false) {
  const alertEl = document.getElementById('alertMessage');
  alertEl.innerText = message;
  alertEl.classList.remove('d-none', 'alert-success', 'alert-danger');
  alertEl.classList.add(isError ? 'alert-danger' : 'alert-success');
  
  setTimeout(() => {
    alertEl.classList.add('d-none');
  }, 3000);
}

function toggleQuizForm(show = true, quiz = null) {
  const container = document.getElementById('quizFormContainer');
  const formTitle = document.getElementById('formTitle');
  
  if (show) {
    container.style.display = 'flex';
    document.getElementById('quizForm').reset();
    
    if (quiz) {
      formTitle.innerHTML = '<i class="ti ti-pencil me-2 text-primary"></i>Edit Quiz';
      document.getElementById('quizId').value = quiz.id;
      document.getElementById('title').value = quiz.title;
      document.getElementById('description').value = quiz.description;
      document.getElementById('durationMinutes').value = quiz.durationMinutes;
      document.getElementById('passScore').value = quiz.passScore;
      document.getElementById('difficulty').value = quiz.difficulty || 'MEDIUM';
      document.getElementById('active').checked = quiz.active;
    } else {
      formTitle.innerHTML = '<i class="ti ti-plus me-2 text-primary"></i>Create New Quiz';
      document.getElementById('quizId').value = '';
      document.getElementById('durationMinutes').value = 30;
      document.getElementById('passScore').value = 50;
    }
    
    container.scrollIntoView({ behavior: 'smooth', block: 'start' });
  } else {
    container.style.display = 'none';
  }
}

async function fetchQuizzes() {
  const grid = document.getElementById('quizzes-grid');
  const loader = document.getElementById('loader');
  grid.innerHTML = '';
  loader.classList.remove('d-none');

  try {
    const response = await fetch(API_URL);
    if (!response.ok) throw new Error('Failed to load');
    currentQuizzes = await response.json();
    renderQuizzes(currentQuizzes);
  } catch (error) {
    console.error(error);
    showAlert('Failed to load quizzes', true);
  } finally {
    loader.classList.add('d-none');
  }
}

function renderQuizzes(quizzes) {
  const grid = document.getElementById('quizzes-grid');
  grid.innerHTML = '';

  if (quizzes.length === 0) {
    grid.innerHTML = '<div class="col-12 text-center text-muted py-5"><i class="ti ti-inbox fs-1 mb-3 text-secondary"></i><h4>No quizzes found</h4><p>Click "Create New Quiz" above to get started.</p></div>';
    return;
  }

  quizzes.forEach(quiz => {
    const badgeClass = quiz.active ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary';
    const badgeText = quiz.active ? '<i class="ti ti-circle-filled fs-9 me-1"></i> Active' : '<i class="ti ti-circle-dashed fs-9 me-1"></i> Draft';
      
    const cardHtml = `
      <div class="col-12 col-md-6 col-xl-4">
        <div class="quiz-card">
          <span class="quiz-badge ${badgeClass}">${badgeText}</span>
          
          <div class="quiz-icon-wrapper">
            <i class="ti ti-brain"></i>
          </div>
          
          <h4 class="quiz-title-text text-truncate">${quiz.title}</h4>
          <p class="text-muted small flex-grow-1 mb-4" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">${quiz.description || 'No description provided.'}</p>
          
          <div class="quiz-stats">
            <div class="stat-item">
              <div class="stat-value">${quiz.durationMinutes}m</div>
              <div class="stat-label">Duration</div>
            </div>
            <div class="stat-item">
              <div class="stat-value text-primary">${quiz.passScore}%</div>
              <div class="stat-label">Pass Score</div>
            </div>
            <div class="stat-item">
              <div class="stat-value text-warning">${quiz.difficulty || 'N/A'}</div>
              <div class="stat-label">Level</div>
            </div>
          </div>
          
          <div class="d-flex gap-2 border-top pt-3 mt-auto border-light">
            <button class="btn btn-light text-primary flex-grow-1 fw-bold rounded-pill" onclick='editQuiz(${quiz.id})'>Edit</button>
            <button class="btn btn-light text-danger rounded-pill px-3" onclick="deleteQuiz(${quiz.id})"><i class="ti ti-trash"></i></button>
          </div>
        </div>
      </div>
    `;
    grid.insertAdjacentHTML('beforeend', cardHtml);
  });
}

function editQuiz(id) {
  const quiz = currentQuizzes.find(q => q.id === id);
  if (quiz) toggleQuizForm(true, quiz);
}

async function saveQuiz() {
  const id = document.getElementById('quizId').value;
  const title = document.getElementById('title').value;
  const description = document.getElementById('description').value;
  const durationMinutes = document.getElementById('durationMinutes').value;
  const passScore = document.getElementById('passScore').value;
  const difficulty = document.getElementById('difficulty').value;
  const active = document.getElementById('active').checked;
  
  if (!title || !durationMinutes || !passScore) {
    showAlert('Please fill all required fields!', true);
    return;
  }

  const payload = {
    learningNodeId: 1, 
    lecturerId: 1,     
    title,
    description,
    durationMinutes: parseInt(durationMinutes),
    passScore: parseInt(passScore),
    difficulty,
    active
  };

  const method = id ? 'PUT' : 'POST';
  const url = id ? `${API_URL}/${id}` : API_URL;
  const btn = document.getElementById('saveBtn');
  const originalText = btn.innerHTML;
  
  btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Saving...';
  btn.disabled = true;

  try {
    const response = await fetch(url, {
      method: method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!response.ok) throw new Error('Failed to save');
    
    showAlert(id ? 'Quiz updated successfully' : 'Quiz created successfully');
    toggleQuizForm(false);
    fetchQuizzes();
  } catch (error) {
    console.error(error);
    showAlert('Error saving quiz', true);
  } finally {
    btn.innerHTML = originalText;
    btn.disabled = false;
  }
}

async function deleteQuiz(id) {
  if (!confirm('Are you sure you want to delete this quiz?')) return;

  try {
    const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
    if (!response.ok) throw new Error('Failed to delete');
    
    showAlert('Quiz deleted successfully');
    fetchQuizzes();
  } catch (error) {
    console.error(error);
    showAlert('Error deleting quiz', true);
  }
}
