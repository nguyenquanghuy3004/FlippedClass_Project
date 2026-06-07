
    // --- Auth Guard & Initialization ---
    const token = localStorage.getItem('jwt_token');
    const userId = localStorage.getItem('user_id');
    if (!token || !userId) {
      alert('You must be logged in to take a quiz.');
      window.location.href = '/signin';
    }

    const urlParams = new URLSearchParams(window.location.search);
    const quizId = urlParams.get('quizId');
    if (!quizId) {
      alert('Quiz ID is missing!');
      window.location.href = '/student/dashboard';
    }

    let timeRemaining = 0;
    let timerInterval = null;
    let totalQuestions = 0;
    let quizQuestions = [];

    const countdownEl = document.getElementById('countdown');
    const navGrid = document.getElementById('navGrid');
    const questionsContainer = document.getElementById('questions-container');

    // --- Fetch Quiz Data ---
    async function initQuiz() {
      try {
        // Fetch Quiz Info
        const quizRes = await fetch(`/api/quizzes/${quizId}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!quizRes.ok) throw new Error('Failed to load quiz details.');
        const quizData = await quizRes.json();
        
        document.getElementById('quiz-title-display').innerText = quizData.title;
        document.getElementById('quiz-topic-display').innerText = quizData.courseName ? `Topic: ${quizData.courseName}` : 'Topic: General';
        
        // Start Timer
        timeRemaining = (quizData.durationMinutes || 30) * 60;
        startTimer();

        // Fetch Questions
        const qRes = await fetch(`/api/quizzes/${quizId}/questions`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!qRes.ok) throw new Error('Failed to load questions.');
        quizQuestions = await qRes.json();
        totalQuestions = quizQuestions.length;

        renderQuestions();
        renderNavGrid();
        updateProgress();

      } catch (err) {
        alert(err.message);
      }
    }

    // --- Timer Logic ---
    function startTimer() {
      timerInterval = setInterval(() => {
        if (timeRemaining <= 0) {
          clearInterval(timerInterval);
          alert('Time is up! Submitting automatically...');
          submitQuiz(true); // auto submit
          return;
        }
        timeRemaining--;
        let m = Math.floor(timeRemaining / 60);
        let s = timeRemaining % 60;
        countdownEl.innerText = `${m < 10 ? '0' : ''}${m}:${s < 10 ? '0' : ''}${s}`;
      }, 1000);
    }

    // --- Dynamic Rendering ---
    function renderQuestions() {
      questionsContainer.innerHTML = ''; // clear loading
      
      quizQuestions.forEach((q, index) => {
        const qNum = index + 1;
        let optionsMap = {};
        try {
          const parsed = JSON.parse(q.options || "{}");
          if (Array.isArray(parsed)) {
            parsed.forEach((item, idx) => optionsMap[idx.toString()] = item);
          } else if (typeof parsed === 'object' && parsed !== null) {
            optionsMap = parsed;
          } else {
            optionsMap = { "Ans": q.options };
          }
        } catch(e) {
          optionsMap = { "Ans": q.options };
        }

        let optionsHtml = '';
        Object.entries(optionsMap).forEach(([optKey, optText]) => {
          optionsHtml += `
            <label class="q-option">
              <input type="radio" name="q${q.id}" value="${optKey}" onchange="markAnswered(${q.id})">
              <div class="q-radio-custom"></div>
              <div class="q-option-text">${optText}</div>
            </label>
          `;
        });

        const qHtml = `
          <div class="edu-card q-card" id="q-card-${q.id}">
            <div class="q-header">
              <div class="q-number">Question ${qNum}</div>
              <div class="q-type">${q.questionType === 'SINGLE_CHOICE' ? 'Single Choice' : q.questionType} (${q.points} pts)</div>
            </div>
            <div class="q-text">${q.content}</div>
            <div class="q-options">
              ${optionsHtml}
            </div>
          </div>
        `;
        questionsContainer.insertAdjacentHTML('beforeend', qHtml);
      });
    }

    function renderNavGrid() {
      navGrid.innerHTML = '';
      quizQuestions.forEach((q, index) => {
        const btn = document.createElement('div');
        btn.className = 'nav-btn';
        btn.id = 'nav-btn-' + q.id;
        btn.innerText = index + 1;
        btn.onclick = () => scrollToQuestion(q.id);
        navGrid.appendChild(btn);
      });
    }

    // --- User Actions ---
    function markAnswered(questionId) {
      const btn = document.getElementById('nav-btn-' + questionId);
      if (btn) btn.classList.add('answered');
      updateProgress();
    }

    function updateProgress() {
      const answeredCount = document.querySelectorAll('.nav-btn.answered').length;
      document.getElementById('prog-p1').innerText = `${answeredCount}/${totalQuestions}`;
      const pct = totalQuestions === 0 ? 0 : (answeredCount / totalQuestions) * 100;
      document.getElementById('prog-pct-p1').innerText = `${Math.round(pct)}%`;
      document.getElementById('bar-p1').style.width = `${pct}%`;
    }

    function scrollToQuestion(questionId) {
      const qEl = document.getElementById('q-card-' + questionId);
      if (qEl) {
        const y = qEl.getBoundingClientRect().top + window.scrollY - 80;
        window.scrollTo({top: y, behavior: 'smooth'});
      }
    }

    // --- Submit Logic ---
    async function submitQuiz(autoSubmit = false) {
      const answeredCount = document.querySelectorAll('.nav-btn.answered').length;
      if (!autoSubmit && answeredCount < totalQuestions) {
        if (!confirm(`You have only answered ${answeredCount} out of ${totalQuestions} questions. Are you sure you want to submit?`)) return;
      }

      // Gather answers
      let answers = {};
      quizQuestions.forEach(q => {
        const selected = document.querySelector(`input[name="q${q.id}"]:checked`);
        if (selected) {
          answers[q.id] = selected.value;
        }
      });

      const payload = {
        studentId: parseInt(userId),
        answers: answers
      };

      try {
        const res = await fetch(`/api/quizzes/${quizId}/attempts`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(payload)
        });

        if (!res.ok) {
          const err = await res.json();
          throw new Error(err.message || 'Failed to submit quiz.');
        }

        const result = await res.json();
        alert(`Quiz Submitted Successfully!\\nYour Score: ${result.score}%\nCorrect Answers: ${result.correctAnswers}/${result.totalQuestions}`);
        
        clearInterval(timerInterval);
        window.location.href = '/student/dashboard';
      } catch (err) {
        alert(err.message);
      }
    }

    // Run on load
    initQuiz();
  
