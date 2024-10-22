// Function to load tasks
function loadTasks() {
    fetch('/api/tasks')
        .then(response => response.json())
        .then(tasks => {
            const taskList = document.getElementById('taskList');
            taskList.innerHTML = '';
            tasks.forEach(task => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${task.title}</td>
                    <td>${task.status}</td>
                    <td>${new Date(task.dueDate).toLocaleString()}</td>
                    <td>
                        <a href="/tasks/${task.id}" class="btn btn-sm btn-info">View</a>
                        <a href="/tasks/${task.id}/edit" class="btn btn-sm btn-warning">Edit</a>
                        <button onclick="deleteTask(${task.id})" class="btn btn-sm btn-danger">Delete</button>
                    </td>
                `;
                taskList.appendChild(row);
            });
        });
}

// Function to delete a task
function deleteTask(id) {
    if (confirm('Are you sure you want to delete this task?')) {
        fetch(`/api/tasks/${id}`, { method: 'DELETE' })
            .then(() => {
                loadTasks();
            });
    }
}

// Function to create or update a task
function submitTaskForm(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const task = Object.fromEntries(formData.entries());

    const method = task.id ? 'PUT' : 'POST';
    const url = task.id ? `/api/tasks/${task.id}` : '/api/tasks';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(task),
    })
    .then(() => {
        window.location.href = '/tasks';
    });
}

// Event listeners
document.addEventListener('DOMContentLoaded', () => {
    const taskList = document.getElementById('taskList');
    if (taskList) {
        loadTasks();
    }

    const taskForm = document.getElementById('taskForm');
    if (taskForm) {
        taskForm.addEventListener('submit', submitTaskForm);
    }
});