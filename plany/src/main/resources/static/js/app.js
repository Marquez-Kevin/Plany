document.addEventListener('DOMContentLoaded', () => {
    // Referencias a los elementos del DOM
    const authSection = document.getElementById('auth-section');
    const loginCard = document.getElementById('loginCard');
    const registerCard = document.getElementById('registerCard');
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const showRegisterLink = document.getElementById('showRegisterLink');
    const showLoginLink = document.getElementById('showLoginLink');
    const greetingUserName = document.getElementById('greetingUserName'); // Para mostrar el nombre del usuario

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

    // URL base de tu API de Spring Boot (¡Recuerda que lo cambiamos a 8081!)
    const API_BASE_URL = 'http://localhost:8081/api'; // Actualizado el puerto
    let currentUserId = null; // Se establecerá después de un inicio de sesión exitoso
    let currentUserName = ''; // Se establecerá después de un inicio de sesión exitoso

    /**
     * @brief Muestra un mensaje modal personalizado en lugar de alert().
     * @param {string} message - El mensaje a mostrar.
     * @param {string} type - 'success', 'error', 'info'.
     */
    function showModalMessage(message, type) {
        modalMessage.textContent = message;
        modalMessage.className = `alert mt-3 alert-${type} d-block`; // Muestra el mensaje
        setTimeout(() => {
            modalMessage.classList.add('d-none'); // Oculta después de 3 segundos
        }, 3000);
    }

    /**
     * @brief Cambia la visibilidad de las secciones de la interfaz.
     * @param {string} sectionToShow - El ID de la sección a mostrar ('auth', 'tasks', 'add-task').
     */
    function showSection(sectionToShow) {
        authSection.classList.add('d-none');
        taskListSection.classList.add('d-none');
        addTaskSection.classList.add('d-none'); // Oculta siempre la sección de añadir tarea al cambiar de vista

        if (sectionToShow === 'auth') {
            authSection.classList.remove('d-none');
            // Por defecto, muestra la tarjeta de login al ir a la sección de auth
            loginCard.classList.remove('d-none');
            registerCard.classList.add('d-none');
        } else if (sectionToShow === 'tasks') {
            taskListSection.classList.remove('d-none');
        } else if (sectionToShow === 'add-task') {
            addTaskSection.classList.remove('d-none');
        }
    }

    /**
     * @brief Fetches and displays tasks for the current user.
     * @returns {void}
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
            taskList.innerHTML = ''; // Limpia las tareas existentes en la lista

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
                li.dataset.taskId = task.idTarea; // Guarda el ID de la tarea

                const checkboxDiv = document.createElement('div');
                checkboxDiv.className = 'form-check';
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.className = 'form-check-input';
                // Marca el checkbox si la tarea ya está completada (asumiendo ID 1 es Completada)
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

    /**
     * @brief Determines the color of the task indicator based on priority name.
     * @param {string} priorityName - The name of the priority.
     * @returns {string} The CSS class name for the indicator color.
     */
    function getIndicatorColor(priorityName) {
        switch (priorityName) {
            case 'Alta': return 'red';
            case 'Media': return 'yellow';
            case 'Baja': return 'green';
            default: return '';
        }
    }

    /**
     * @brief Updates the weekly progress display.
     * @returns {void}
     */
    async function updateWeeklyProgress() {
        if (!currentUserId) {
            progressCircle.style.setProperty('--progress', `0%`);
            progressPercentageSpan.textContent = `0%`;
            return;
        }

        try {
            const response = await fetch(`http:/localhost:8081/api/tareas/progress/${currentUserId}`);
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
     * @brief Toggles the completion status of a task.
     * @param {number} taskId - The ID of the task to toggle.
     * @param {boolean} isCompleted - The new completion status (true if completed, false if pending).
     * @returns {void}
     */
    async function toggleTaskCompletion(taskId, isCompleted) {
        try {
            const endpoint = `${API_BASE_URL}/tareas/${taskId}/complete`;
            const method = 'PUT';

            const response = await fetch(endpoint, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(`HTTP error! status: ${response.status} - ${errorData.message || response.statusText}`);
            }

            const taskElement = document.querySelector(`li[data-task-id="${taskId}"] .task-title`);
            if (taskElement) {
                if (isCompleted) {
                    taskElement.classList.add('task-completed');
                } else {
                    taskElement.classList.remove('task-completed');
                }
            }
            updateWeeklyProgress();
            showModalMessage('Estado de tarea actualizado.', 'success');
        } catch (error) {
            console.error('Error toggling task completion:', error);
            const checkbox = document.querySelector(`li[data-task-id="${taskId}"] input[type="checkbox"]`);
            if (checkbox) {
                checkbox.checked = !isCompleted; // Revertir el estado del checkbox si falla
            }
            showModalMessage(`No se pudo actualizar el estado de la tarea: ${error.message}.`, 'error');
        }
    }

    // --- LÓGICA DE AUTENTICACIÓN ---

    /**
     * @brief Maneja el inicio de sesión del usuario.
     * @param {string} email - El correo electrónico del usuario.
     * @param {string} password - La contraseña del usuario.
     */
    async function handleLogin(email, password) {
        try {
            const response = await fetch(`http:/localhost:8081/api/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ correoUsu: email, contrasena: password }),
            });

            const data = await response.json();
            if (response.ok) {
                currentUserId = data.userId;
                // Asumimos que el nombre del usuario se puede obtener después de iniciar sesión o se puede almacenar
                // Por simplicidad, si el backend no lo devuelve, lo dejamos vacío o pedimos un endpoint /user/{id}
                // Si tu backend devuelve el nombre, actualiza esta línea:
                // currentUserName = data.nombreUsu;
                greetingUserName.textContent = `¡HOLA, ${currentUserName.toUpperCase()}!`; // O "USUARIO" si no hay nombre

                showModalMessage('¡Inicio de sesión exitoso!', 'success');
                showSection('tasks'); // Muestra la sección de tareas
                fetchAndDisplayTasks();
                updateWeeklyProgress();
            } else {
                showModalMessage(`Error al iniciar sesión: ${data.message || 'Credenciales inválidas'}`, 'error');
            }
        } catch (error) {
            console.error('Error durante el inicio de sesión:', error);
            showModalMessage('Error de conexión al intentar iniciar sesión.', 'error');
        }
    }

    /**
     * @brief Maneja el registro de un nuevo usuario.
     * @param {string} name - El nombre del usuario.
     * @param {string} email - El correo electrónico del usuario.
     * @param {string} password - La contraseña del usuario.
     */
    async function handleRegister(name, email, password) {
        console.log('Intentando registrar usuario:', { name, email, password }); // Log de inicio
        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ nombreUsu: name, correoUsu: email, contrasena: password }),
            });

            console.log('Respuesta del servidor para registro (raw):', response); // Log de respuesta raw
            const data = await response.json();
            console.log('Respuesta del servidor para registro (JSON):', data); // Log de respuesta JSON

            if (response.ok) {
                showModalMessage('¡Registro exitoso! Ahora puedes iniciar sesión.', 'success');
                loginForm.reset(); // Limpia el formulario de login
                showSection('auth'); // Vuelve a la vista de login
                loginCard.classList.remove('d-none');
                registerCard.classList.add('d-none');
            } else {
                showModalMessage(`Error al registrar: ${data.message || 'Error desconocido al registrar.'}`, 'error');
            }
        } catch (error) {
            console.error('Error durante el registro:', error);
            showModalMessage('Error de conexión al intentar registrarse.', 'error');
        }
    }

    // --- EVENT LISTENERS ---

    // Mostrar formulario de registro
    showRegisterLink.addEventListener('click', (e) => {
        e.preventDefault();
        loginCard.classList.add('d-none');
        registerCard.classList.remove('d-none');
        registerForm.reset();
    });

    // Mostrar formulario de inicio de sesión
    showLoginLink.addEventListener('click', (e) => {
        e.preventDefault();
        registerCard.classList.add('d-none');
        loginCard.classList.remove('d-none');
        loginForm.reset();
    });

    // Envío del formulario de Login
    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;
        handleLogin(email, password);
    });

    // Envío del formulario de Registro
    registerForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('registerName').value;
        const email = document.getElementById('registerEmail').value;
        const password = document.getElementById('registerPassword').value;
        handleRegister(name, email, password);
    });

    // Mostrar sección de añadir tarea
    addTaskBtn.addEventListener('click', () => {
        showSection('add-task');
        addTaskForm.reset(); // Limpia el formulario
        // Opcional: Establecer fecha de fin por defecto a hoy + 7 días
        const today = new Date();
        today.setDate(today.getDate() + 7);
        taskEndDateInput.value = today.toISOString().slice(0, 10);
    });

    // Ocultar sección de añadir tarea y volver a tareas
    cancelAddTaskBtn.addEventListener('click', () => {
        showSection('tasks');
        addTaskForm.reset(); // Limpia el formulario
        fetchAndDisplayTasks(); // Refresca por si hubo cambios durante la edición
    });

    // Envío del formulario de Añadir Tarea
    addTaskForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        if (!currentUserId) {
            showModalMessage('Debes iniciar sesión para añadir tareas.', 'error');
            return;
        }

        const titulo = taskNameInput.value;
        const descripcion = taskDescriptionInput.value;
        const fechaFin = taskEndDateInput.value; // Formato YYYY-MM-DD
        const codPrio = parseInt(taskPrioritySelect.value);
        const codCat = parseInt(taskCategorySelect.value);

        // Validaciones básicas
        if (!titulo || !fechaFin || isNaN(codPrio) || isNaN(codCat)) {
            showModalMessage('Por favor, complete todos los campos obligatorios.', 'error');
            return;
        }

        const newTask = {
            titulo: titulo,
            descripcion: descripcion,
            fechaCreacion: new Date().toISOString().slice(0, 10), // Fecha actual
            fechaFin: fechaFin,
            recordatorio: null, // Asumimos no hay recordatorio al crear la tarea
            tipo: { codTipo: 1 }, // ID de tipo predeterminado (ej. 'Trabajo')
            prioridad: { codPrio: codPrio },
            categoria: { codCat: codCat },
            estado: { codEst: 2 }, // ID de estado 'Pendiente' (asumiendo 2 es pendiente)
            usuario: { idUsuario: currentUserId }
        };

        try {
            const response = await fetch(`http:/localhost:8081/api/tareas`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newTask)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(`HTTP error! status: ${response.status} - ${errorData.message || response.statusText}`);
            }

            const addedTask = await response.json();
            console.log('Task added:', addedTask);
            showModalMessage('¡Tarea añadida con éxito!', 'success');

            // Después de añadir, vuelve a la lista de tareas y refresca
            showSection('tasks'); // Muestra la sección de tareas
            addTaskForm.reset();
            fetchAndDisplayTasks(); // Refresca la lista
            updateWeeklyProgress(); // Actualiza el progreso
        } catch (error) {
            console.error('Error adding task:', error);
            showModalMessage(`Error al agregar la tarea: ${error.message}.`, 'error');
        }
    });

    // Inicializa la vista para mostrar la sección de autenticación al cargar la página
    showSection('auth');
});
