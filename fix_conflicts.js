const fs = require('fs');

function fixFile(file, resolver) {
    let content = fs.readFileSync(file, 'utf8');
    const regex = /<<<<<<< HEAD\r?\n([\s\S]*?)=======\r?\n([\s\S]*?)>>>>>>> origin\/Nguyenhuy\r?\n/g;
    content = content.replace(regex, (match, head, origin) => {
        return resolver(head, origin);
    });
    fs.writeFileSync(file, content);
}

fixFile('src/main/resources/templates/lecturer/learningNode.html', (head, origin) => {
    if (head.includes('layout:decorate')) return head;
    if (head.includes('Add Node')) {
        return `          <h2 class="mb-0 fw-bold title-course" id="pathTitle">Learning Path</h2>
        </div>
        <div class="col-auto">
          <button id="addNodeBtn" class="btn btn-primary rounded-pill fw-semibold shadow-sm px-4">
            <i class="ti ti-plus me-2"></i>Add Node
          </button>
          <a th:href="@{/lecturer/practice-config(spaceId=\${spaceId})}" class="btn btn-success rounded-pill fw-semibold shadow-sm ms-2 px-4">
            <i class="ti ti-settings me-2"></i>Practice Config
          </a>
        </div>
`;
    }
    return head;
});

fixFile('src/main/resources/templates/student/student-dashboard.html', (h, o) => h);

fixFile('src/main/resources/templates/student/learning-spaces.html', (head, origin) => {
    if (head.includes('state.spaces = ')) {
        return `        state.spaces = (data.learningSpaces || []).filter(space => space.status === 'ACTIVE');
        renderNotifications(state.spaces, data.commentNotifications || []);
`;
    }
    if (head.includes('isDoc')) {
        return `      const isDoc = node.nodeType === 'DOCUMENT';
      const isLocked = node.status === 'LOCKED';
      let linkProps = '';
      if (isLocked) {
          linkProps = \`style="opacity: 0.6; cursor: not-allowed; background-color: #f1f5f9;" onclick="event.preventDefault(); Swal.fire('Locked', 'Please complete the prerequisite lesson first.', 'warning');"\`;
      } else {
          linkProps = \`href="/student/learning-node?\${params.toString()}"\`;
          if (isDoc) {
              linkProps += \` onclick="window.openDocumentNode(\${node.id}, event)"\`;
          }
      }

      return \`
      <a class="node-card" \${linkProps}>
        <div class="node-index" \${isLocked ? 'style="background-color: #cbd5e1; color: #64748b;"' : ''}>\${index + 1}</div>
`;
    }
    return head;
});

fixFile('src/main/resources/templates/student/take-quiz.html', (h, o) => h);
fixFile('src/main/resources/templates/student/learning-node.html', (h, o) => h);

console.log('Fixed conflicts');
