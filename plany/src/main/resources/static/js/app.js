document.addEventListener('DOMContentLoaded', () => {
    // DOM References
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
    const logoutBtn = document.getElementById('logoutBtn');

    // Edit task section
    const editTaskSection = document.getElementById('editTaskSection');
    const editTaskForm = document.getElementById('editTaskForm');
    const cancelEditTaskBtn = document.getElementById('cancelEditTaskBtn');
    const editTaskNameInput = document.getElementById('editTaskName');
    const editTaskDescriptionInput = document.getElementById('editTaskDescription');
    const editTaskEndDateInput = document.getElementById('editTaskEndDate');
    const editTaskPrioritySelect = document.getElementById('editTaskPriority');
    const editTaskCategorySelect = document.getElementById('editTaskCategory');
    const editTaskTypeSelect = document.getElementById('editTaskType');
    const editTaskReminderMsgInput = document.getElementById('editTaskReminderMsg');
    const editTaskReminderDateInput = document.getElementById('editTaskReminderDate');

    // Form fields
    const taskNameInput = document.getElementById('taskName');
    const taskDescriptionInput = document.getElementById('taskDescription');
    const taskEndDateInput = document.getElementById('taskEndDate');
    const taskPrioritySelect = document.getElementById('taskPriority');
    const taskCategorySelect = document.getElementById('taskCategory');

    // API Configuration
    const API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' 
        ? 'http://localhost:8081/api' 
        : `${window.location.protocol}//${window.location.host}/api`;
    let currentUserId = null;
    let currentUserName = '';

    let currentEditingTask = null;

    // ===== SESSION MANAGEMENT =====

    /**
     * Saves user session to localStorage
     */
    function saveUserSession(userId, userName) {
        localStorage.setItem('plany_userId', userId);
        localStorage.setItem('plany_userName', userName);
        localStorage.setItem('plany_sessionTime', Date.now().toString());
    }

    /**
     * Loads user session from localStorage
     */
    function loadUserSession() {
        const userId = localStorage.getItem('plany_userId');
        const userName = localStorage.getItem('plany_userName');
        const sessionTime = localStorage.getItem('plany_sessionTime');
        
        if (userId && userName && sessionTime) {
            // Check if session is not older than 24 hours
            const sessionAge = Date.now() - parseInt(sessionTime);
            const maxSessionAge = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
            
            if (sessionAge < maxSessionAge) {
                currentUserId = userId;
                currentUserName = userName;
                return true;
            } else {
                // Session expired, clear it
                clearUserSession();
            }
        }
        return false;
    }

    /**
     * Clears user session from localStorage
     */
    function clearUserSession() {
        localStorage.removeItem('plany_userId');
        localStorage.removeItem('plany_userName');
        localStorage.removeItem('plany_sessionTime');
        currentUserId = null;
        currentUserName = '';
    }

    /**
     * Checks if user is authenticated and loads their data
     */
    async function checkAuthentication() {
        if (loadUserSession()) {
            // User has a valid session, show dashboard
            greetingUserName.textContent = currentUserName;
            showSection('tasks');
            await fetchAndDisplayTasks();
            await updateWeeklyProgress();
            return true;
        } else {
            // No valid session, show login
            showSection('auth');
            return false;
        }
    }

    /**
     * Shows a modal message
     */
    function showModalMessage(message, type) {
        modalMessage.textContent = message;
        modalMessage.className = `modal-message alert-${type}`;
        modalMessage.classList.remove('d-none');
        
        setTimeout(() => {
            modalMessage.classList.add('d-none');
        }, 4000);
    }

    /**
     * Changes section visibility
     */
    function showSection(sectionToShow) {
        authSection.classList.add('d-none');
        taskListSection.classList.add('d-none');
        addTaskSection.classList.add('d-none');
        editTaskSection.classList.add('d-none');

        if (sectionToShow === 'auth') {
            authSection.classList.remove('d-none');
            loginCard.classList.remove('d-none');
            registerCard.classList.add('d-none');
        } else if (sectionToShow === 'tasks') {
            taskListSection.classList.remove('d-none');
        } else if (sectionToShow === 'add-task') {
            addTaskSection.classList.remove('d-none');
        } else if (sectionToShow === 'edit-task') {
            editTaskSection.classList.remove('d-none');
        }
    }

    /**
     * Opens edit task form with task data
     */
    async function editTask(task) {
        currentEditingTask = task;
        
        // Ensure dropdowns are populated
        await fetchAndPopulateDropdowns();
        
        // Populate form fields with task data
        editTaskNameInput.value = task.titulo || '';
        editTaskDescriptionInput.value = task.descripcion || '';
        editTaskEndDateInput.value = task.fechaFin || '';
        editTaskPrioritySelect.value = task.prioridad ? task.prioridad.codPrio : '';
        editTaskCategorySelect.value = task.categoria ? task.categoria.codCat : '';
        editTaskTypeSelect.value = task.tipo ? task.tipo.codTipo : '';
        
        // Handle reminder data
        if (task.recordatorio) {
            editTaskReminderMsgInput.value = task.recordatorio.mensaje || '';
            editTaskReminderDateInput.value = task.recordatorio.fechaHora || '';
        } else {
            editTaskReminderMsgInput.value = '';
            editTaskReminderDateInput.value = '';
        }
        
        showSection('edit-task');
    }

    /**
     * Deletes a task after confirmation
     */
    async function deleteTask(taskId, taskTitle) {
        // Show confirmation dialog
        const confirmed = confirm(`¿Estás seguro de que quieres eliminar la tarea "${taskTitle}"?\n\nEsta acción no se puede deshacer.`);
        
        if (!confirmed) {
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/tareas/${taskId}`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Error desconocido del servidor' }));
                throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
            }
            
            console.log('Tarea eliminada exitosamente');
            showModalMessage('¡Tarea eliminada con éxito!', 'success');
            
            // Refresh tasks and stats
            fetchAndDisplayTasks();
            updateWeeklyProgress();
        } catch (error) {
            console.error('Error al eliminar tarea:', error);
            showModalMessage(`Error al eliminar la tarea: ${error.message}`, 'error');
        }
    }

    /**
     * Fetches and displays tasks
     */
    async function fetchAndDisplayTasks() {
        if (!currentUserId) {
            showModalMessage('Inicia sesión para ver tus tareas.', 'info');
            taskList.innerHTML = '<div class="empty-state">Por favor, inicie sesión para ver sus tareas.</div>';
            return;
        }

        try {
            console.log('Llamando a endpoint de tareas para usuario:', currentUserId);
            const response = await fetch(`${API_BASE_URL}/tareas/today/${currentUserId}`);
            console.log('Respuesta del servidor:', response.status, response.statusText);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const tasks = await response.json();
            console.log('Tareas recibidas:', tasks);
            
            displayTasks(tasks);
            updateTaskStats(tasks);
        } catch (error) {
            console.error('Error fetching tasks:', error);
            showModalMessage('Error al cargar las tareas. Por favor, inténtelo de nuevo.', 'error');
            taskList.innerHTML = '<div class="empty-state error">Error al cargar las tareas.</div>';
        }
    }

    /**
     * Displays tasks in the UI
     */
    function displayTasks(tasks) {
        taskList.innerHTML = '';

        if (tasks.length === 0) {
            taskList.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-check-circle"></i>
                    <p>No tienes tareas pendientes. ¡Bien hecho!</p>
                </div>
            `;
            return;
        }

        // Ordenar tareas: pendientes primero por fecha más cercana, completadas al final
        const sortedTasks = tasks.sort((a, b) => {
            const aIsCompleted = a.estado && a.estado.nombreEstado === 'Completada';
            const bIsCompleted = b.estado && b.estado.nombreEstado === 'Completada';
            
            // Si una está completada y la otra no, la pendiente va primero
            if (aIsCompleted && !bIsCompleted) return 1;
            if (!aIsCompleted && bIsCompleted) return -1;
            
            // Si ambas están completadas, ordenar por fecha de creación (más reciente primero)
            if (aIsCompleted && bIsCompleted) {
                return new Date(b.fechaCreacion) - new Date(a.fechaCreacion);
            }
            
            // Si ambas están pendientes, ordenar por fecha de fin (más cercana primero)
            const today = new Date();
            const aDate = a.fechaFin ? new Date(a.fechaFin) : new Date('9999-12-31');
            const bDate = b.fechaFin ? new Date(b.fechaFin) : new Date('9999-12-31');
            
            return aDate - bDate;
        });

        sortedTasks.forEach(task => {
            const taskElement = createTaskElement(task);
            taskList.appendChild(taskElement);
        });
    }

    /**
     * Creates a task element
     */
    function createTaskElement(task) {
        const taskItem = document.createElement('div');
        taskItem.className = `task-item ${task.estado && task.estado.nombreEstado === 'Completada' ? 'completed' : ''}`;
        taskItem.dataset.taskId = task.idTarea;

        // Format due date
        const fechaFin = task.fechaFin ? new Date(task.fechaFin).toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        }) : 'Sin fecha';

        // Get priority class
        const priorityClass = getPriorityClass(task.prioridad ? task.prioridad.nombrePrioridad : 'Baja');

        taskItem.innerHTML = `
            <div class="task-checkbox">
                <input type="checkbox" ${task.estado && task.estado.nombreEstado === 'Completada' ? 'checked' : ''}>
            </div>
            <div class="task-content">
                <div class="task-title">${task.titulo}</div>
                ${task.descripcion ? `<div class="task-description">${task.descripcion}</div>` : ''}
                <div class="task-meta">
                    <div class="task-date">
                        <i class="fas fa-calendar-alt"></i>
                        ${fechaFin}
                    </div>
                    <div class="task-priority">
                        <span class="priority-indicator ${priorityClass}"></span>
                        ${task.prioridad ? task.prioridad.nombrePrioridad : 'Sin prioridad'}
                    </div>
                </div>
            </div>
            <div class="task-actions">
                <button class="btn-edit-task" data-task-id="${task.idTarea}">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn-delete-task" data-task-id="${task.idTarea}">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        `;

        // Add event listener for checkbox
        const checkbox = taskItem.querySelector('input[type="checkbox"]');
        checkbox.addEventListener('change', () => toggleTaskCompletion(task.idTarea, checkbox.checked));

        // Add event listener for edit button
        const editButton = taskItem.querySelector('.btn-edit-task');
        editButton.addEventListener('click', () => editTask(task));

        // Add event listener for delete button
        const deleteButton = taskItem.querySelector('.btn-delete-task');
        deleteButton.addEventListener('click', () => deleteTask(task.idTarea, task.titulo));

        return taskItem;
    }

    /**
     * Updates task statistics
     */
    function updateTaskStats(tasks) {
        const pendingTasks = tasks.filter(task => task.estado && task.estado.nombreEstado === 'Pendiente').length;
        const completedTasks = tasks.filter(task => task.estado && task.estado.nombreEstado === 'Completada').length;
        const totalTasks = tasks.length;

        // Update pending tasks count
        const pendingTasksElement = document.getElementById('pendingTasks');
        if (pendingTasksElement) {
            pendingTasksElement.textContent = pendingTasks;
        }

        // Update progress stats
        const completedTasksElement = document.getElementById('completedTasks');
        const totalTasksElement = document.getElementById('totalTasks');
        
        if (completedTasksElement) completedTasksElement.textContent = completedTasks;
        if (totalTasksElement) totalTasksElement.textContent = totalTasks;
    }

    /**
     * Gets priority class for styling
     */
    function getPriorityClass(priorityName) {
        switch (priorityName) {
            case 'Alta': return 'high';
            case 'Media': return 'medium';
            case 'Baja': return 'low';
            default: return 'low';
        }
    }

    /**
     * Updates weekly progress
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
     * Toggles task completion
     */
    async function toggleTaskCompletion(taskId, isCompleted) {
        try {
            const response = await fetch(`${API_BASE_URL}/tareas/${taskId}/toggle`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(`HTTP error! status: ${response.status} - ${errorData.message || response.statusText}`);
            }

            const taskElement = document.querySelector(`[data-task-id="${taskId}"]`);
            if (taskElement) {
                taskElement.classList.toggle('completed', isCompleted);
            }
            
            updateWeeklyProgress();
            fetchAndDisplayTasks(); // Refresh tasks to update stats
            showModalMessage('Estado de tarea actualizado.', 'success');
        } catch (error) {
            console.error('Error toggling task completion:', error);
            const checkbox = document.querySelector(`[data-task-id="${taskId}"] input[type="checkbox"]`);
            if (checkbox) {
                checkbox.checked = !isCompleted;
            }
            showModalMessage(`No se pudo actualizar el estado de la tarea: ${error.message}.`, 'error');
        }
    }

    // ===== AUTHENTICATION =====

    /**
     * Handles user login
     */
    async function handleLogin(email, password) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ correoUsu: email, contrasena: password }),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Credenciales inválidas o error del servidor' }));
                throw new Error(errorData.message);
            }

            const data = await response.json();
            currentUserId = data.userId;
            currentUserName = data.nombreUsu || 'Usuario';
            
            // Update the greeting with the new user's name
            greetingUserName.textContent = currentUserName;
            
            // Save user session to localStorage
            saveUserSession(currentUserId, currentUserName);

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
     * Handles user registration
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

    /**
     * Fetches and populates dropdowns
     */
    async function fetchAndPopulateDropdowns() {
        try {
            // Load states
            const estadosResponse = await fetch(`${API_BASE_URL}/tareas/estados`);
            if (!estadosResponse.ok) throw new Error('No se pudieron cargar los estados de tarea.');
            
            const estados = await estadosResponse.json();
            console.log('Estados disponibles:', estados);
            
            // Find pending state
            let estadoPendiente = estados.find(estado => estado.nombreEstado === 'Pendiente') || 
                                 estados.find(estado => estado.codEst === 2);
            
            if (!estadoPendiente) {
                console.warn('No se encontró estado pendiente, usando el primero disponible');
                estadoPendiente = estados[0];
            }
            
            window.estadoPendiente = estadoPendiente;
            
            // Load types
            const tiposResponse = await fetch(`${API_BASE_URL}/tareas/tipos`);
            if (!tiposResponse.ok) throw new Error('No se pudieron cargar los tipos de tarea.');
            
            const tipos = await tiposResponse.json();
            console.log('Tipos disponibles:', tipos);
            
            // Populate both task type dropdowns (create and edit)
            const taskTypeSelect = document.getElementById('taskType');
            const editTaskTypeSelect = document.getElementById('editTaskType');
            
            // Clear and populate create task dropdown
            taskTypeSelect.innerHTML = '<option value="">Selecciona tipo</option>';
            tipos.forEach(tipo => {
                const option = document.createElement('option');
                option.value = tipo.codTipo;
                option.textContent = tipo.nombreTipo;
                taskTypeSelect.appendChild(option);
            });
            
            // Clear and populate edit task dropdown
            editTaskTypeSelect.innerHTML = '<option value="">Selecciona tipo</option>';
            tipos.forEach(tipo => {
                const option = document.createElement('option');
                option.value = tipo.codTipo;
                option.textContent = tipo.nombreTipo;
                editTaskTypeSelect.appendChild(option);
            });
            
        } catch (error) {
            console.error('Error cargando datos:', error);
            showModalMessage(error.message, 'error');
        }
    }

    /**
     * Handles user logout
     */
    function handleLogout() {
        clearUserSession();
        // Clear the greeting name
        greetingUserName.textContent = 'Usuario';
        showModalMessage('Sesión cerrada exitosamente', 'info');
        showSection('auth');
        loginForm.reset();
    }

    // ===== EVENT LISTENERS =====

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

    addTaskBtn.addEventListener('click', async () => {
        showSection('add-task');
        addTaskForm.reset();
        const today = new Date();
        taskEndDateInput.value = today.toISOString().slice(0, 10);
        
        // Ensure dropdowns are populated
        await fetchAndPopulateDropdowns();
    });

    cancelAddTaskBtn.addEventListener('click', () => {
        showSection('tasks');
    });

    cancelEditTaskBtn.addEventListener('click', () => {
        showSection('tasks');
        currentEditingTask = null;
    });

    editTaskForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!currentEditingTask || !currentUserId) return;

        const reminderMsg = editTaskReminderMsgInput.value;
        const reminderDate = editTaskReminderDateInput.value;

        const updatedTask = {
            titulo: editTaskNameInput.value,
            descripcion: editTaskDescriptionInput.value,
            fechaFin: editTaskEndDateInput.value,
            prioridad: { codPrio: parseInt(editTaskPrioritySelect.value) },
            categoria: { codCat: parseInt(editTaskCategorySelect.value) },
            tipo: { codTipo: parseInt(editTaskTypeSelect.value) },
            estado: currentEditingTask.estado, // Keep current state
            usuario: { idUsuario: currentUserId },
            recordatorio: null
        };

        if (reminderMsg && reminderDate) {
            updatedTask.recordatorio = {
                mensaje: reminderMsg,
                fechaHora: reminderDate
            };
        }

        console.log('Objeto de tarea actualizada a enviar:', updatedTask);

        try {
            const response = await fetch(`${API_BASE_URL}/tareas/${currentEditingTask.idTarea}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updatedTask)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Error desconocido del servidor' }));
                throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
            }
            
            const updatedTaskResponse = await response.json();
            console.log('Tarea actualizada exitosamente:', updatedTaskResponse);
            
            showModalMessage('¡Tarea actualizada con éxito!', 'success');
            showSection('tasks');
            currentEditingTask = null;
            fetchAndDisplayTasks();
            updateWeeklyProgress();
        } catch (error) {
            console.error('Error al actualizar tarea:', error);
            showModalMessage(`Error al actualizar la tarea: ${error.message}`, 'error');
        }
    });

    addTaskForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!currentUserId) return;

        const reminderMsg = document.getElementById('taskReminderMsg').value;
        const reminderDate = document.getElementById('taskReminderDate').value;

        const newTask = {
            titulo: taskNameInput.value,
            descripcion: taskDescriptionInput.value,
            fechaCreacion: new Date().toISOString().slice(0, 10),
            fechaFin: taskEndDateInput.value,
            prioridad: { codPrio: parseInt(taskPrioritySelect.value) },
            categoria: { codCat: parseInt(taskCategorySelect.value) },
            tipo: { codTipo: parseInt(document.getElementById('taskType').value) },
            estado: window.estadoPendiente || { codEst: 2 },
            usuario: { idUsuario: currentUserId },
            recordatorio: null
        };

        if (reminderMsg && reminderDate) {
            newTask.recordatorio = {
                mensaje: reminderMsg,
                fechaHora: reminderDate
            };
        }

        console.log('Objeto de tarea a enviar:', newTask);

        try {
            const response = await fetch(`${API_BASE_URL}/tareas`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newTask)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Error desconocido del servidor' }));
                throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
            }
            
            const createdTask = await response.json();
            console.log('Tarea creada exitosamente:', createdTask);
            
            showModalMessage('¡Tarea añadida con éxito!', 'success');
            showSection('tasks');
            fetchAndDisplayTasks();
            updateWeeklyProgress();
        } catch (error) {
            console.error('Error al crear tarea:', error);
            showModalMessage(`Error al crear la tarea: ${error.message}`, 'error');
        }
    });

    logoutBtn.addEventListener('click', handleLogout);

    // ===== FILTROS DE BÚSQUEDA =====
    
    /**
     * Llena los dropdowns de filtros con datos dinámicos
     */
    async function fetchAndPopulateFilterDropdowns() {
        // Prioridades
        const filtroPrioridad = document.getElementById('filtro-prioridad');
        filtroPrioridad.innerHTML = '<option value="">Todas las prioridades</option>';
        try {
            const res = await fetch(`${API_BASE_URL}/prioridades`);
            const prioridades = await res.json();
            prioridades.forEach(prio => {
                const option = document.createElement('option');
                option.value = prio.nombrePrioridad;
                option.textContent = prio.nombrePrioridad;
                filtroPrioridad.appendChild(option);
            });
        } catch (e) {
            console.error('Error cargando prioridades para filtros:', e);
        }

        // Categorías
        const filtroCategoria = document.getElementById('filtro-categoria');
        filtroCategoria.innerHTML = '<option value="">Todas las categorías</option>';
        try {
            const res = await fetch(`${API_BASE_URL}/categorias`);
            const categorias = await res.json();
            categorias.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.nombreCategoria;
                option.textContent = cat.nombreCategoria;
                filtroCategoria.appendChild(option);
            });
        } catch (e) {
            console.error('Error cargando categorías para filtros:', e);
        }
    }

    /**
     * Carga las tareas con los filtros aplicados
     */
    async function cargarTareasConFiltros() {
        if (!currentUserId) {
            console.error('No hay usuario autenticado');
            return;
        }

        const estado = document.getElementById('filtro-estado').value;
        const prioridad = document.getElementById('filtro-prioridad').value;
        const categoria = document.getElementById('filtro-categoria').value;
        const busqueda = document.getElementById('busqueda-tarea').value;
        const fechaInicio = document.getElementById('filtro-fecha-inicio').value;
        const fechaFin = document.getElementById('filtro-fecha-fin').value;

        let url = `${API_BASE_URL}/tareas/filter/${currentUserId}?`;
        if (estado) url += `estado=${encodeURIComponent(estado)}&`;
        if (prioridad) url += `prioridad=${encodeURIComponent(prioridad)}&`;
        if (categoria) url += `categoria=${encodeURIComponent(categoria)}&`;
        if (busqueda) url += `busqueda=${encodeURIComponent(busqueda)}&`;
        if (fechaInicio) url += `fechaInicio=${encodeURIComponent(fechaInicio)}&`;
        if (fechaFin) url += `fechaFin=${encodeURIComponent(fechaFin)}&`;

        console.log('URL de filtrado:', url);

        try {
            const res = await fetch(url);
            if (!res.ok) {
                throw new Error(`Error ${res.status}: ${res.statusText}`);
            }
            const tareas = await res.json();
            console.log('Tareas filtradas:', tareas);
            displayTasks(tareas);
            updateTaskStats(tareas);
        } catch (e) {
            console.error('Error al filtrar tareas:', e);
            showModalMessage('Error al filtrar tareas: ' + e.message, 'error');
        }
    }

    // Event listeners para filtros
    document.getElementById('btn-filtrar').addEventListener('click', function() {
        console.log('Botón filtrar clickeado');
        cargarTareasConFiltros();
    });

    document.getElementById('btn-hoy').addEventListener('click', function() {
        console.log('Botón hoy clickeado');
        cargarTareasDeHoy();
    });

    document.getElementById('btn-limpiar-filtros').addEventListener('click', function() {
        console.log('Botón limpiar filtros clickeado');
        document.getElementById('busqueda-tarea').value = '';
        document.getElementById('filtro-estado').value = '';
        document.getElementById('filtro-prioridad').value = '';
        document.getElementById('filtro-categoria').value = '';
        document.getElementById('filtro-fecha-inicio').value = '';
        document.getElementById('filtro-fecha-fin').value = '';
        cargarTareasConFiltros();
    });

    /**
     * Carga las tareas del día actual
     */
    async function cargarTareasDeHoy() {
        if (!currentUserId) {
            console.error('No hay usuario autenticado');
            return;
        }

        const hoy = new Date().toISOString().slice(0, 10); // Formato YYYY-MM-DD
        
        // Limpiar todos los filtros
        document.getElementById('busqueda-tarea').value = '';
        document.getElementById('filtro-estado').value = '';
        document.getElementById('filtro-prioridad').value = '';
        document.getElementById('filtro-categoria').value = '';
        document.getElementById('filtro-fecha-inicio').value = '';
        document.getElementById('filtro-fecha-fin').value = '';

        // Aplicar filtro de fecha de hoy
        document.getElementById('filtro-fecha-inicio').value = hoy;
        document.getElementById('filtro-fecha-fin').value = hoy;

        console.log('Filtrando tareas de hoy:', hoy);

        try {
            const res = await fetch(`${API_BASE_URL}/tareas/filter/${currentUserId}?fechaInicio=${hoy}&fechaFin=${hoy}`);
            if (!res.ok) {
                throw new Error(`Error ${res.status}: ${res.statusText}`);
            }
            const tareas = await res.json();
            console.log('Tareas de hoy:', tareas);
            displayTasks(tareas);
            updateTaskStats(tareas);
            showModalMessage(`Mostrando tareas del ${hoy}`, 'info');
        } catch (e) {
            console.error('Error al cargar tareas de hoy:', e);
            showModalMessage('Error al cargar tareas de hoy: ' + e.message, 'error');
        }
    }

    // Initialize
    checkAuthentication();
    fetchAndPopulateDropdowns();
    fetchAndPopulateFilterDropdowns();
});
