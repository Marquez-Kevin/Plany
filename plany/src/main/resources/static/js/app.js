document.addEventListener('DOMContentLoaded', () => {
    // Referencias a los elementos del DOM
    const authSection = document.getElementById('auth-section');
    const loginCard = document.getElementById('loginCard');
    const registerCard = document.getElementById('registerCard');
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const showRegisterLink = document.getElementById('showRegisterLink');
    const showLoginLink = document.getElementById('showLoginLink');
    const greetingUserName = document.getElementById('greetingUserName');

    const taskListSection = document.getElementById('task-list-section');
    const addTaskBtn = document.getElementById('addTaskBtn');
    const cancelAddTaskBtn = document.getElementById('cancelAddTaskBtn');
    const addTaskForm = document.getElementById('addTaskForm');
    const taskList = document.getElementById('taskList');
    const progressCircle = document.querySelector('.progress-circle');
    const progressPercentageSpan = document.querySelector('.progress-percentage');
    const modalMessage = document.getElementById('modalMessage');

    // Campos del formulario de Añadir Tarea
    const taskNameInput = document.getElementById('taskName');
    const taskDescriptionInput = document.getElementById('taskDescription');
    const taskEndDateInput = document.getElementById('taskEndDate');
    const taskPrioritySelect = document.getElementById('taskPriority');
    const taskCategorySelect = document.getElementById('taskCategory');

    // CORREGIDO: URL base de tu API de Spring Boot
    const API_BASE_URL = 'http://localhost:8081/api';
    let currentUserId = null;
    let currentUserName = '';

    /**
     * Muestra un mensaje modal personalizado.
     */
    function showModalMessage(message, type) {
        modalMessage.textContent = message;
        modalMessage.className = `alert mt-3 alert-${type} d-block`;
        setTimeout(() => {
            modalMessage.classList.add('d-none');
        }, 3000);
    }

    /**
     * Cambia la visibilidad de las secciones de la interfaz.
     */
    function showSection(sectionToShow) {
        authSection.classList.add('d-none');
        taskListSection.classList.add('d-none');
        addTaskSection.classList.add('d-none');

        if (sectionToShow === 'auth') {
            authSection.classList.remove('d-none');
            loginCard.classList.remove('d-none');
            registerCard.classList.add('d-none');
        } else if (sectionToShow === 'tasks') {
            taskListSection.classList.remove('d-none');
        } else if (sectionToShow === 'add-task') {
            addTaskSection.classList.remove('d-none');
        }
    }

    /**
     * Obtiene y muestra las tareas del usuario actual.
     */
    async function fetchAndDisplayTasks() {
        if (!currentUserId) {
            showModalMessage('Inicia sesión para ver tus tareas.', 'info');
            taskList.innerHTML = '<li class="list-group-item text-center text-muted">Por favor, inicie sesión para ver sus tareas.</li>';
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/tareas/today/${currentUserId}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const tasks = await response.json();
            taskList.innerHTML = '';

            if (tasks.length === 0) {
                const li = document.createElement('li');
                li.className = 'list-group-item text-center text-muted';
                li.textContent = 'No tienes tareas pendientes para hoy. ¡Bien hecho!';
                taskList.appendChild(li);
                return;
            }

            tasks.forEach(task => {
                const li = document.createElement('li');
                li.className = 'list-group-item';
                li.dataset.taskId = task.idTarea;

                const checkboxDiv = document.createElement('div');
                checkboxDiv.className = 'form-check';
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.className = 'form-check-input';
                checkbox.checked = task.estado && task.estado.codEst === 1;
                checkbox.addEventListener('change', () => toggleTaskCompletion(task.idTarea, checkbox.checked));
                checkboxDiv.appendChild(checkbox);

                const taskContent = document.createElement('div');
                taskContent.className = `task-title ${checkbox.checked ? 'task-completed' : ''}`;
                taskContent.innerHTML = `
                    <span class="task-indicator ${getIndicatorColor(task.prioridad ? task.prioridad.nombrePrioridad : 'Baja')}"></span>
                    ${task.titulo}
                    <br>
                    <small class="text-muted">${task.descripcion || ''}</small>
                `;

                li.appendChild(checkboxDiv);
                li.appendChild(taskContent);
                taskList.appendChild(li);
            });
        } catch (error) {
            console.error('Error fetching tasks:', error);
            showModalMessage('Error al cargar las tareas. Por favor, inténtelo de nuevo.', 'error');
            taskList.innerHTML = '<li class="list-group-item text-danger text-center">Error al cargar las tareas.</li>';
        }
    }

    function getIndicatorColor(priorityName) {
        switch (priorityName) {
            case 'Alta': return 'red';
            case 'Media': return 'yellow';
            case 'Baja': return 'green';
            default: return '';
        }
    }

    /**
     * Actualiza el progreso semanal.
     */
    async function updateWeeklyProgress() {
        if (!currentUserId) {
            progressCircle.style.setProperty('--progress', '0%');
            progressPercentageSpan.textContent = '0%';
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/tareas/progress/${currentUserId}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const progress = await response.json();
            progressCircle.style.setProperty('--progress', `${progress}%`);
            progressPercentageSpan.textContent = `${progress.toFixed(0)}%`;
        } catch (error) {
            console.error('Error fetching weekly progress:', error);
            showModalMessage('Error al cargar el progreso semanal.', 'error');
        }
    }

    /**
     * Cambia el estado de completado de una tarea.
     */
    async function toggleTaskCompletion(taskId, isCompleted) {
        try {
            const response = await fetch(`${API_BASE_URL}/tareas/${taskId}/complete`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(`HTTP error! status: ${response.status} - ${errorData.message || response.statusText}`);
            }

            const taskElement = document.querySelector(`li[data-task-id="${taskId}"] .task-title`);
            if (taskElement) {
                taskElement.classList.toggle('task-completed', isCompleted);
            }
            updateWeeklyProgress();
            showModalMessage('Estado de tarea actualizado.', 'success');
        } catch (error) {
            console.error('Error toggling task completion:', error);
            const checkbox = document.querySelector(`li[data-task-id="${taskId}"] input[type="checkbox"]`);
            if (checkbox) {
                checkbox.checked = !isCompleted;
            }
            showModalMessage(`No se pudo actualizar el estado de la tarea: ${error.message}.`, 'error');
        }
    }

    // --- LÓGICA DE AUTENTICACIÓN ---

    /**
     * Maneja el inicio de sesión del usuario.
     */
    async function handleLogin(email, password) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ correoUsu: email, contrasena: password }),
            });

            // MEJORADO: Manejo de error antes de intentar leer JSON
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Credenciales inválidas o error del servidor' }));
                throw new Error(errorData.message);
            }

            const data = await response.json();
            currentUserId = data.userId;
            // Aquí podrías hacer otra llamada para obtener el nombre del usuario si lo necesitas
            greetingUserName.textContent = '¡HOLA!'; // Simplificado

            showModalMessage('¡Inicio de sesión exitoso!', 'success');
            showSection('tasks');
            fetchAndDisplayTasks();
            updateWeeklyProgress();

        } catch (error) {
            console.error('Error durante el inicio de sesión:', error);
            showModalMessage(`Error al iniciar sesión: ${error.message}`, 'error');
        }
    }

    /**
     * Maneja el registro de un nuevo usuario.
     */
    async function handleRegister(name, email, password) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombreUsu: name, correoUsu: email, contrasena: password }),
            });

            const data = await response.json();
            if (response.ok) {
                showModalMessage('¡Registro exitoso! Ahora puedes iniciar sesión.', 'success');
                loginForm.reset();
                showSection('auth');
            } else {
                showModalMessage(`Error al registrar: ${data.message || 'Error desconocido al registrar.'}`, 'error');
            }
        } catch (error) {
            console.error('Error durante el registro:', error);
            showModalMessage('Error de conexión al intentar registrarse.', 'error');
        }
    }

    // --- EVENT LISTENERS ---

    showRegisterLink.addEventListener('click', (e) => {
        e.preventDefault();
        loginCard.classList.add('d-none');
        registerCard.classList.remove('d-none');
        registerForm.reset();
    });

    showLoginLink.addEventListener('click', (e) => {
        e.preventDefault();
        registerCard.classList.add('d-none');
        loginCard.classList.remove('d-none');
        loginForm.reset();
    });

    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;
        handleLogin(email, password);
    });

    registerForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('registerName').value;
        const email = document.getElementById('registerEmail').value;
        const password = document.getElementById('registerPassword').value;
        handleRegister(name, email, password);
    });

    addTaskBtn.addEventListener('click', () => {
        showSection('add-task');
        addTaskForm.reset();
        const today = new Date();
        today.setDate(today.getDate() + 7);
        taskEndDateInput.value = today.toISOString().slice(0, 10);
    });

    cancelAddTaskBtn.addEventListener('click', () => {
        showSection('tasks');
    });

    /**
     * Envío del formulario de Añadir Tarea
     */
    addTaskForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        if (!currentUserId) {
            showModalMessage('Debes iniciar sesión para añadir tareas.', 'error');
            return;
        }

        const newTask = {
            titulo: taskNameInput.value,
            descripcion: taskDescriptionInput.value,
            fechaCreacion: new Date().toISOString().slice(0, 10),
            fechaFin: taskEndDateInput.value,
            prioridad: { codPrio: parseInt(taskPrioritySelect.value) },
            categoria: { codCat: parseInt(taskCategorySelect.value) },
            estado: { codEst: 2 }, // Pendiente
            usuario: { idUsuario: currentUserId }
        };

        try {
            // CORREGIDO: La URL ahora se construye correctamente y es plural
            const response = await fetch(`${API_BASE_URL}/tareas`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newTask)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'No se pudo agregar la tarea.' }));
                throw new Error(errorData.message);
            }

            await response.json();
            showModalMessage('¡Tarea añadida con éxito!', 'success');
            showSection('tasks');
            fetchAndDisplayTasks();
            updateWeeklyProgress();

        } catch (error) {
            console.error('Error adding task:', error);
            showModalMessage(`Error al agregar la tarea: ${error.message}.`, 'error');
        }
    });

    // Inicializa la vista en la sección de autenticación
    showSection('auth');
});
