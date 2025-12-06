import axios from "axios";

const API_URL = "http://localhost:8080"; // Spring Boot backend

export const api = axios.create({
    baseURL: API_URL,
    withCredentials: true, // if using session/cookie auth
});

// Auth
export const login = (username: string, password: string) =>
    api.post("/login", { username, password });

export const register = (username: string, password: string, role: string) =>
    api.post("/register", { username, password, role });

// Todos
export const getTodos = () => api.get("/todos");
export const addTodo = (title: string) => api.post("/todos", { title });
export const toggleTodo = (id: number) => api.patch(`/todos/${id}/toggle`);
export const deleteTodo = (id: number) => api.delete(`/todos/${id}`);
