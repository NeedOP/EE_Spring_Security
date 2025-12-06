import { useEffect, useState } from "react";
import { addTodo, deleteTodo, getTodos, toggleTodo } from "../api";
import { Todo } from "../types";
import "./TodoPage.css";

export const TodoPage = () => {
    const [todos, setTodos] = useState<Todo[]>([]);
    const [newTodo, setNewTodo] = useState("");

    const fetchTodos = async () => {
        const res = await getTodos();
        setTodos(res.data);
    };

    useEffect(() => {
        fetchTodos();
    }, []);

    const handleAdd = async () => {
        if (!newTodo) return;
        await addTodo(newTodo);
        setNewTodo("");
        fetchTodos();
    };

    const handleToggle = async (id: number) => {
        await toggleTodo(id);
        fetchTodos();
    };

    const handleDelete = async (id: number) => {
        await deleteTodo(id);
        fetchTodos();
    };

    return (
        <div className="todo-container">
            <h1>Todos</h1>
            <div className="todo-input">
                <input
                    type="text"
                    value={newTodo}
                    onChange={e => setNewTodo(e.target.value)}
                    placeholder="Add new todo"
                />
                <button onClick={handleAdd}>Add</button>
            </div>
            <ul className="todo-list">
                {todos.map(todo => (
                    <li key={todo.id}>
            <span
                className={todo.completed ? "completed" : ""}
                onClick={() => handleToggle(todo.id)}
            >
              {todo.title}
            </span>
                        <button onClick={() => handleDelete(todo.id)}>Delete</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};
