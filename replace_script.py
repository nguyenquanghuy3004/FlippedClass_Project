import re

file_path = r'd:\FLIPPED_CLASS\DONG4\FlippedClass_Project\src\main\resources\templates\lecturer\LearningSpace.html'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Replace #spaceGrid
grid_replacement = '''        <div class="row g-4 mb-5" id="spaceGrid">
            <div class="col-12 col-md-6 col-lg-4" th:each="space, iterStat : ">
                <div class="classroom-card">
                    <div class="card-banner" th:classappend="">
                        <div class="banner-pattern"></div>
                        <a th:href="@{/lecturer/space/{id}(id=)}" class="card-title-link">
                            <h2 class="course-title" th:text=""></h2>
                            <div class="course-subtitle" th:text=""></div>
                        </a>

                        <div class="card-options-top" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="ti ti-dots-vertical"></i>
                        </div>
                        <ul class="dropdown-menu shadow-sm border-0 rounded-3">
                            <li><a class="dropdown-item d-flex align-items-center gap-2 edit-space-btn" href="#"
                                   th:data-id="" th:data-name="" 
                                   th:data-description="" th:data-visibility="">
                                   <i class="ti ti-pencil"></i> Edit Space</a></li>
                            <li><a class="dropdown-item d-flex align-items-center gap-2 clone-space-btn" href="#"
                                   th:data-id="" th:data-name="">
                                   <i class="ti ti-copy"></i> Clone Space</a></li>
                            <li><a class="dropdown-item d-flex align-items-center gap-2 archive-space-btn" href="#"
                                   th:data-id="" th:data-status="">
                                   <i th:class=""></i>
                                   <span class="archive-text" th:text=""></span></a>
                            </li>
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li><a class="dropdown-item d-flex align-items-center gap-2 text-danger delete-space-btn"
                                    href="#" th:data-id="" th:data-status="">
                                    <i class="ti ti-trash"></i> Delete Space</a></li>
                        </ul>
                    </div>

                    <div class="card-avatar" th:classappend="" th:text=""></div>

                    <div class="card-body-content">
                        <!-- Content -->
                    </div>

                    <div class="card-footer">
                        <div class="d-flex align-items-center gap-1">
                            <span class="status-indicator" th:classappend="'status-' + " th:title="'Status: ' + "></span>
                            <span class="visibility-text text-muted" style="font-size: 0.75rem; font-weight: 600;" th:text=""></span>
                        </div>
                        <div class="d-flex gap-1">
                            <div class="footer-icon share-btn" title="Share Invite Code" th:data-code="">
                                <i class="ti ti-share"></i>
                            </div>
                            <div class="footer-icon" title="Open in class"><i class="ti ti-id-badge-2"></i></div>
                            <div class="footer-icon" title="Open folder"><i class="ti ti-folder"></i></div>
                            <div class="footer-icon" title="More"><i class="ti ti-dots-vertical"></i></div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div th:if="" class="col-12 text-center py-5 text-muted">
                <i class="ti ti-box-margin fs-1 d-block mb-2"></i> No active spaces found.
            </div>
        </div>'''
content = re.sub(r'<div class="row g-4 mb-5" id="spaceGrid">.*?</div>', grid_replacement, content, flags=re.DOTALL)

# 2. Replace trashList
trash_replacement = '''                        <div class="list-group list-group-flush" id="trashList">
                            <div th:if="" class="text-center py-5 text-muted">
                                <i class="ti ti-ghost fs-1 d-block mb-2"></i>Trash is empty.
                            </div>
                            <div th:each="space : " class="list-group-item d-flex justify-content-between align-items-center py-3">
                                <div>
                                    <h6 class="mb-1 fw-bold" th:text=""></h6>
                                    <small class="text-muted" th:text="'Deleted on: ' + "></small>
                                </div>
                                <button class="btn btn-sm btn-outline-success rounded-pill fw-bold px-3 restore-space-btn" th:data-id="">
                                    <i class="ti ti-refresh"></i> Restore
                                </button>
                            </div>
                        </div>'''
content = re.sub(r'<div class="list-group list-group-flush" id="trashList">.*?</div>', trash_replacement, content, flags=re.DOTALL)

# 3. Remove template block
content = re.sub(r'<!-- Template HTML mẫu cho mỗi thẻ Không gian học tập \(Space Card\) -->.*?</template>', '', content, flags=re.DOTALL)

# 4. Remove JS fetchSpaces function and its call
content = re.sub(r'// Hàm lấy danh sách Learning Space của giảng viên từ API.*?if \(token\) \{.*?\} else \{.*?window.location.href = \'/signin\';\n\s*\}', '', content, flags=re.DOTALL)

# 5. Fix Restore space JS to just reload
content = content.replace('fetchSpaces(); // reload both grid and trash', 'window.location.reload();')
content = content.replace('fetchSpaces(); // reload UI', 'window.location.reload();')
content = content.replace('fetchSpaces();', 'window.location.reload();')

# Write back
with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Replacement done via regex script")
