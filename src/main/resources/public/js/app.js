// Функции для работы с задачами
async function loadTasks() {
    try {
        const response = await fetch('/tasks');
        const tasks = await response.json();
        const tasksList = document.getElementById('tasksList');
        tasksList.innerHTML = tasks.map(task => createTaskCard(task)).join('');
    } catch (error) {
        console.error('Ошибка при загрузке задач:', error);
    }
}

async function loadEpics() {
    try {
        const response = await fetch('/epics');
        const epics = await response.json();
        const epicsList = document.getElementById('epicsList');
        epicsList.innerHTML = epics.map(epic => createEpicCard(epic)).join('');
    } catch (error) {
        console.error('Ошибка при загрузке эпиков:', error);
    }
}

async function loadHistory() {
    try {
        const response = await fetch('/history');
        const history = await response.json();
        const historyList = document.getElementById('historyList');
        historyList.innerHTML = history.map(task => createHistoryItem(task)).join('');
    } catch (error) {
        console.error('Ошибка при загрузке истории:', error);
    }
}

async function loadSubtasks(epicId) {
    try {
        const response = await fetch(`/epics/${epicId}/subtasks`);
        const subtasks = await response.json();
        const subtasksList = document.querySelector(`#epic-${epicId} .subtask-list`);
        if (subtasksList) {
            subtasksList.innerHTML = subtasks.map(subtask => createSubtaskCard(subtask)).join('');
        }
    } catch (error) {
        console.error('Ошибка при загрузке подзадач:', error);
    }
}

// Создание задач
async function createTask() {
    const form = document.getElementById('taskForm');
    const formData = new FormData(form);
    const task = {
        name: formData.get('name'),
        description: formData.get('description'),
        status: formData.get('status'),
        duration: parseInt(formData.get('duration')) || 0,
        startTime: formData.get('startTime') ? new Date(formData.get('startTime')).toISOString() : null
    };

    console.log('Отправляемые данные:', task);

    try {
        const response = await fetch('/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(task)
        });

        console.log('Статус ответа:', response.status);
        const responseText = await response.text();
        console.log('Текст ответа:', responseText);

        if (response.ok) {
            const modal = bootstrap.Modal.getInstance(document.getElementById('taskModal'));
            if (modal) {
                modal.hide();
            }
            form.reset();
            await loadTasks();
        } else {
            console.error('Ошибка при создании задачи:', responseText);
            alert('Ошибка при создании задачи: ' + responseText);
        }
    } catch (error) {
        console.error('Ошибка при создании задачи:', error);
        alert('Ошибка при создании задачи: ' + error.message);
    }
}

async function createEpic() {
    const form = document.getElementById('epicForm');
    const formData = new FormData(form);
    const epic = {
        name: formData.get('name'),
        description: formData.get('description')
    };

    try {
        const response = await fetch('/epics', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(epic)
        });

        if (response.ok) {
            bootstrap.Modal.getInstance(document.getElementById('epicModal')).hide();
            form.reset();
            loadEpics();
        } else {
            console.error('Ошибка при создании эпика:', await response.text());
        }
    } catch (error) {
        console.error('Ошибка при создании эпика:', error);
    }
}

async function createSubtask() {
    const form = document.getElementById('subtaskForm');
    const formData = new FormData(form);
    const subtask = {
        name: formData.get('name'),
        description: formData.get('description'),
        status: formData.get('status'),
        duration: parseInt(formData.get('duration')),
        startTime: formData.get('startTime') ? new Date(formData.get('startTime')).toISOString() : null,
        epicId: parseInt(formData.get('epicId'))
    };

    try {
        const response = await fetch('/subtasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(subtask)
        });

        if (response.ok) {
            bootstrap.Modal.getInstance(document.getElementById('subtaskModal')).hide();
            form.reset();
            loadSubtasks(subtask.epicId);
        } else {
            console.error('Ошибка при создании подзадачи:', await response.text());
        }
    } catch (error) {
        console.error('Ошибка при создании подзадачи:', error);
    }
}

// Обновление задач
async function updateTaskStatus(taskId, status) {
    try {
        const response = await fetch(`/tasks/${taskId}`);
        const task = await response.json();
        task.status = status;

        const updateResponse = await fetch(`/tasks/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(task)
        });

        if (updateResponse.ok) {
            loadTasks();
            loadEpics();
        } else {
            console.error('Ошибка при обновлении задачи:', await updateResponse.text());
        }
    } catch (error) {
        console.error('Ошибка при обновлении задачи:', error);
    }
}

// Удаление задач
async function deleteTask(taskId) {
    if (!confirm('Вы уверены, что хотите удалить эту задачу?')) {
        return;
    }

    try {
        const response = await fetch(`/tasks/${taskId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadTasks();
        } else {
            console.error('Ошибка при удалении задачи:', await response.text());
        }
    } catch (error) {
        console.error('Ошибка при удалении задачи:', error);
    }
}

// Создание карточек задач
function createTaskCard(task) {
    return `
        <div class="card task-card" id="task-${task.id}">
            <div class="card-header">
                <h5 class="card-title mb-0">${task.name}</h5>
                <div>
                    <select class="form-select task-status" onchange="updateTaskStatus(${task.id}, this.value)">
                        <option value="NEW" ${task.status === 'NEW' ? 'selected' : ''}>Новая</option>
                        <option value="IN_PROGRESS" ${task.status === 'IN_PROGRESS' ? 'selected' : ''}>В работе</option>
                        <option value="DONE" ${task.status === 'DONE' ? 'selected' : ''}>Завершена</option>
                    </select>
                    <button class="btn btn-danger btn-sm ms-2" onclick="deleteTask(${task.id})">Удалить</button>
                </div>
            </div>
            <div class="card-body">
                <p class="card-text">${task.description || ''}</p>
                <div class="task-time">
                    ${task.startTime ? `Начало: ${formatDateTime(task.startTime)}<br>` : ''}
                    ${task.duration ? `Длительность: ${task.duration} мин.` : ''}
                </div>
            </div>
        </div>
    `;
}

function createEpicCard(epic) {
    return `
        <div class="card task-card" id="epic-${epic.id}">
            <div class="card-header">
                <h5 class="card-title mb-0">${epic.name}</h5>
                <div>
                    <button class="btn btn-primary btn-sm" onclick="showSubtaskModal(${epic.id})">
                        Добавить подзадачу
                    </button>
                    <button class="btn btn-danger btn-sm ms-2" onclick="deleteTask(${epic.id})">Удалить</button>
                </div>
            </div>
            <div class="card-body">
                <p class="card-text">${epic.description || ''}</p>
                <div class="subtask-list"></div>
            </div>
        </div>
    `;
}

function createSubtaskCard(subtask) {
    return `
        <div class="card task-card" id="subtask-${subtask.id}">
            <div class="card-header">
                <h6 class="card-title mb-0">${subtask.name}</h6>
                <div>
                    <select class="form-select task-status" onchange="updateTaskStatus(${subtask.id}, this.value)">
                        <option value="NEW" ${subtask.status === 'NEW' ? 'selected' : ''}>Новая</option>
                        <option value="IN_PROGRESS" ${subtask.status === 'IN_PROGRESS' ? 'selected' : ''}>В работе</option>
                        <option value="DONE" ${subtask.status === 'DONE' ? 'selected' : ''}>Завершена</option>
                    </select>
                    <button class="btn btn-danger btn-sm ms-2" onclick="deleteTask(${subtask.id})">Удалить</button>
                </div>
            </div>
            <div class="card-body">
                <p class="card-text">${subtask.description || ''}</p>
                <div class="task-time">
                    ${subtask.startTime ? `Начало: ${formatDateTime(subtask.startTime)}<br>` : ''}
                    ${subtask.duration ? `Длительность: ${subtask.duration} мин.` : ''}
                </div>
            </div>
        </div>
    `;
}

function createHistoryItem(task) {
    return `
        <div class="history-item">
            <h6>${task.name}</h6>
            <div class="task-time">
                ${task.startTime ? `Начало: ${formatDateTime(task.startTime)}<br>` : ''}
                ${task.duration ? `Длительность: ${task.duration} мин.` : ''}
            </div>
        </div>
    `;
}

// Вспомогательные функции
function formatDateTime(dateTimeStr) {
    const date = new Date(dateTimeStr);
    return date.toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function showSubtaskModal(epicId) {
    const modal = new bootstrap.Modal(document.getElementById('subtaskModal'));
    document.querySelector('#subtaskForm input[name="epicId"]').value = epicId;
    modal.show();
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    // Загрузка начальных данных
    loadTasks();
    loadEpics();
    loadHistory();

    // Обработчики для вкладок
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('data-bs-target').substring(1);
            
            // Скрыть все вкладки
            document.querySelectorAll('.tab-pane').forEach(tab => {
                tab.classList.remove('show', 'active');
            });
            
            // Показать выбранную вкладку
            document.getElementById(targetId).classList.add('show', 'active');
            
            // Обновить активную ссылку
            document.querySelectorAll('.nav-link').forEach(navLink => {
                navLink.classList.remove('active');
            });
            this.classList.add('active');

            // Загрузить данные для вкладки
            switch(targetId) {
                case 'tasks':
                    loadTasks();
                    break;
                case 'epics':
                    loadEpics();
                    break;
                case 'history':
                    loadHistory();
                    break;
            }
        });
    });

    // Обработчик для модального окна подзадач
    document.getElementById('subtaskModal').addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget;
        const epicId = button.getAttribute('data-epic-id');
        document.querySelector('#subtaskForm input[name="epicId"]').value = epicId;
    });
}); 