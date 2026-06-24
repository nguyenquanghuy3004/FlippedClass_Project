const fs = require('fs');

let supContent = fs.readFileSync('src/main/resources/templates/supporter/learningPath.html', 'utf8');
let lecContent = fs.readFileSync('src/main/resources/templates/lecturer/learningPath.html', 'utf8');

const searchStr = "btn.prop('disabled', false).text('Save Changes');";
let supIdx = supContent.indexOf(searchStr);

if (supIdx !== -1) {
    let goodSup = supContent.substring(0, supIdx + searchStr.length);
    let lecIdx = lecContent.indexOf(searchStr);
    if (lecIdx !== -1) {
        let goodLecEnd = lecContent.substring(lecIdx + searchStr.length);
        let combined = goodSup + goodLecEnd;
        
        const correctJS = `
                window.viewQuizResults = async function(quizId) {
                    try {
                        const token = localStorage.getItem('jwt_token');
                        const response = await fetch('/api/quizzes/' + quizId + '/attempts', {
                            headers: { Authorization: 'Bearer ' + token }
                        });
                        if (!response.ok) throw new Error('Failed to fetch quiz results');
                        const attempts = await response.json();
                        
                        const tbody = document.getElementById('quizResultsTableBody');
                        const emptyDiv = document.getElementById('quizResultsEmpty');
                        
                        tbody.innerHTML = '';
                        if (attempts && attempts.length > 0) {
                            emptyDiv.classList.add('d-none');
                            attempts.forEach(attempt => {
                                const tr = document.createElement('tr');
                                const date = new Date(attempt.submittedAt).toLocaleString();
                                const scoreClass = attempt.score >= 5 ? 'bg-success' : 'bg-danger';
                                const initial = attempt.studentName ? attempt.studentName.charAt(0).toUpperCase() : 'S';
                                const name = attempt.studentName || 'Unknown Student';
                                
                                tr.innerHTML = \`
                                    <td class="ps-4">
                                        <div class="d-flex align-items-center gap-2">
                                            <div class="bg-primary-subtle text-primary rounded-circle d-flex align-items-center justify-content-center fw-bold" style="width: 32px; height: 32px;">
                                                \${initial}
                                            </div>
                                            <span class="fw-medium">\${name}</span>
                                        </div>
                                    </td>
                                    <td><span class="badge \${scoreClass} rounded-pill">\${attempt.score} / 10</span></td>
                                    <td>\${attempt.correctAnswers} / \${attempt.totalQuestions}</td>
                                    <td class="text-muted small">\${date}</td>
                                \`;
                                tbody.appendChild(tr);
                            });
                        } else {
                            emptyDiv.classList.remove('d-none');
                        }
                        
                        $('#quizResultsModal').modal('show');
                    } catch (error) {
                        alert(error.message);
                    }
                };
`;
        let finalScriptEnd = "});\n        </script>";
        let replaced = combined.replace(finalScriptEnd, correctJS + '\n' + finalScriptEnd);
        fs.writeFileSync('src/main/resources/templates/supporter/learningPath.html', replaced, 'utf8');
        console.log("SUCCESSFULLY FIXED");
    } else {
        console.log("FAILED LEC");
    }
} else {
    console.log("FAILED SUP");
}
